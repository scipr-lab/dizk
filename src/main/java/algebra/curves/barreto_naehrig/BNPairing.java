/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig;

import algebra.curves.AbstractPairing;
import algebra.curves.barreto_naehrig.BNFields.*;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG1Parameters;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG2Parameters;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNGTParameters;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public abstract class BNPairing<
        BNFrT extends BNFr<BNFrT>,
        BNFqT extends BNFq<BNFqT>,
        BNFq2T extends BNFq2<BNFqT, BNFq2T>,
        BNFq6T extends BNFq6<BNFqT, BNFq2T, BNFq6T>,
        BNFq12T extends BNFq12<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
        BNG1T extends BNG1<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
        BNG2T extends BNG2<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
        BNGTT extends BNGT<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
        BNG1ParametersT extends AbstractBNG1Parameters<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
        BNG2ParametersT extends AbstractBNG2Parameters<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
        BNGTParametersT extends AbstractBNGTParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
        BNPublicParametersT extends BNPublicParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T>>
        extends AbstractPairing<BNG1T, BNG2T, BNGTT> {

    public abstract BNPublicParametersT publicParameters();

    protected class AteG1Precompute extends G1Precompute {
        final BNFqT PX;
        final BNFqT PY;

        private AteG1Precompute(final BNFqT PX, final BNFqT PY) {
            this.PX = PX;
            this.PY = PY;
        }

        public boolean equals(final AteG1Precompute that) {
            return PX.equals(that.PX) && PY.equals(that.PY);
        }
    }

    protected class AteEllCoefficients {
        BNFq2T ell0;
        BNFq2T ellVW;
        BNFq2T ellVV;

        public boolean equals(final AteEllCoefficients that) {
            return this.ell0.equals(that.ell0)
                    && this.ellVW.equals(that.ellVW)
                    && this.ellVV.equals(that.ellVV);
        }
    }

    protected class AteG2Precompute extends G2Precompute {
        final BNFq2T QX;
        final BNFq2T QY;
        final List<AteEllCoefficients> coefficients;

        private AteG2Precompute(
                final BNFq2T QX,
                final BNFq2T QY,
                final List<AteEllCoefficients> coefficients) {
            this.QX = QX;
            this.QY = QY;
            this.coefficients = coefficients;
        }

        public boolean equals(final AteG2Precompute that) {
            return this.QX.equals(that.QX)
                    && this.QY.equals(that.QY)
                    && this.coefficients.equals(that.coefficients);
        }
    }

    private AteEllCoefficients doublingStepForFlippedMillerLoop(
            final BNFqT twoInverse,
            BNG2T current) {
        final BNFq2T X = current.X, Y = current.Y, Z = current.Z;

        final BNFq2T A = X.mul(Y).mul(twoInverse);                               // A = X1 * Y1 / 2
        final BNFq2T B = Y.square();                                             // B = Y1^2
        final BNFq2T C = Z.square();                                             // C = Z1^2
        final BNFq2T D = C.add(C).add(C);                                        // D = 3 * C
        final BNFq2T E = this.publicParameters().twistCoefficientB().mul(D);     // E = twist_b * D
        final BNFq2T F = E.add(E).add(E);                                        // F = 3 * E
        final BNFq2T G = B.add(F).mul(twoInverse);                               // G = (B+F)/2
        final BNFq2T H = (Y.add(Z)).square().sub(B.add(C));                      // H = (Y1+Z1)^2-(B+C)
        final BNFq2T I = E.sub(B);                                               // I = E-B
        final BNFq2T J = X.square();                                             // J = X1^2
        final BNFq2T ESquare = E.square();                                       // E_squared = E^2

        current.setX(A.mul(B.sub(F)));                                    // X3 = A * (B-F)
        current.setY(G.square().sub(ESquare.add(ESquare).add(ESquare)));  // Y3 = G^2 - 3*E^2
        current.setZ(B.mul(H));                                           // Z3 = B * H

        AteEllCoefficients c = new AteEllCoefficients();
        c.ell0 = this.publicParameters().twist().mul(I);                  // ell_0 = xi * I
        c.ellVW = H.negate();                                             // ell_VW = - H (later: * yP)
        c.ellVV = J.add(J).add(J);                                        // ell_VV = 3*J (later: * xP)
        return c;
    }

    private AteEllCoefficients mixedAdditionStepForFlippedMillerLoop(
            final BNG2T base,
            BNG2T current) {
        final BNFq2T X1 = current.X, Y1 = current.Y, Z1 = current.Z;
        final BNFq2T x2 = base.X, y2 = base.Y;

        final BNFq2T D = X1.sub(x2.mul(Z1));               // D = X1 - X2*Z1
        final BNFq2T E = Y1.sub(y2.mul(Z1));               // E = Y1 - Y2*Z1
        final BNFq2T F = D.square();                       // F = D^2
        final BNFq2T G = E.square();                       // G = E^2
        final BNFq2T H = D.mul(F);                         // H = D*F
        final BNFq2T I = X1.mul(F);                        // I = X1 * F
        final BNFq2T J = H.add(Z1.mul(G)).sub(I.add(I));   // J = H + Z1*G - (I+I)

        current.setX(D.mul(J));                            // X3 = D*J
        current.setY(E.mul(I.sub(J)).sub(H.mul(Y1)));      // Y3 = E*(I-J)-(H*Y1)
        current.setZ(Z1.mul(H));                           // Z3 = Z1*H

        AteEllCoefficients c = new AteEllCoefficients();
        c.ell0 = this.publicParameters().twist()
                .mul(E.mul(x2).sub(D.mul(y2)));                // ell_0 = xi * (E * X2 - D * Y2)
        c.ellVV = E.negate();                              // ell_VV = - E (later: * xP)
        c.ellVW = D;                                       // ell_VW = D (later: * yP)
        return c;
    }

    private BNG2T mulByQ(final BNG2T element) {
        return element.construct(
                publicParameters().qXMulTwist().mul(element.X.FrobeniusMap(1)),
                publicParameters().qYMulTwist().mul(element.Y.FrobeniusMap(1)),
                element.Z.FrobeniusMap(1));
    }

    private BNFq12T ExpByNegativeZ(final BNFq12T elt) {
        BNFq12T result = elt.cyclotomicExponentiation(this.publicParameters().finalExponentZ());
        if (!this.publicParameters().isFinalExponentZNegative()) {
            result = result.unitaryInverse();
        }
        return result;
    }

    private BNFq12T finalExponentiationFirstChunk(final BNFq12T elt) {
        /**
         Computes result = elt^((q^6-1)*(q^2+1)).
         Follows, e.g., Beuchat et al page 9, by computing result as follows:
         elt^((q^6-1)*(q^2+1)) = (conj(elt) * elt^(-1))^(q^2+1)
         More precisely:
         A = conj(elt)
         B = elt.inverse()
         C = A * B
         D = C.Frobenius_map(2)
         result = D * C
         */

        final BNFq12T A = elt.unitaryInverse();
        final BNFq12T B = elt.inverse();
        final BNFq12T C = A.mul(B);
        final BNFq12T D = C.FrobeniusMap(2);
        return D.mul(C);
    }

    private BNFq12T finalExponentiationLastChunk(final BNFq12T elt) {
        /**
         Follows Laura Fuentes-Castaneda et al. "Faster hashing to AbstractG2"
         by computing:
         result = elt^(q^3 * (12*z^3 + 6z^2 + 4z - 1) +
         q^2 * (12*z^3 + 6z^2 + 6z) +
         q   * (12*z^3 + 6z^2 + 4z) +
         1   * (12*z^3 + 12z^2 + 6z + 1))
         which equals
         result = elt^( 2z * ( 6z^2 + 3z + 1 ) * (q^4 - q^2 + 1)/r ).
         Using the following addition chain:
         A = exp_by_neg_z(elt)  // = elt^(-z)
         B = A^2                // = elt^(-2*z)
         C = B^2                // = elt^(-4*z)
         D = C * B              // = elt^(-6*z)
         E = exp_by_neg_z(D)    // = elt^(6*z^2)
         F = E^2                // = elt^(12*z^2)
         G = epx_by_neg_z(F)    // = elt^(-12*z^3)
         H = conj(D)            // = elt^(6*z)
         I = conj(G)            // = elt^(12*z^3)
         J = I * E              // = elt^(12*z^3 + 6*z^2)
         K = J * H              // = elt^(12*z^3 + 6*z^2 + 6*z)
         L = K * B              // = elt^(12*z^3 + 6*z^2 + 4*z)
         M = K * E              // = elt^(12*z^3 + 12*z^2 + 6*z)
         N = M * elt            // = elt^(12*z^3 + 12*z^2 + 6*z + 1)
         O = L.Frobenius_map(1) // = elt^(q*(12*z^3 + 6*z^2 + 4*z))
         P = O * N              // = elt^(q*(12*z^3 + 6*z^2 + 4*z) * (12*z^3 + 12*z^2 + 6*z + 1))
         Q = K.Frobenius_map(2) // = elt^(q^2 * (12*z^3 + 6*z^2 + 6*z))
         R = Q * P              // = elt^(q^2 * (12*z^3 + 6*z^2 + 6*z) + q*(12*z^3 + 6*z^2 +
         4*z) * (12*z^3 + 12*z^2 + 6*z + 1))
         S = conj(elt)          // = elt^(-1)
         T = S * L              // = elt^(12*z^3 + 6*z^2 + 4*z - 1)
         U = T.Frobenius_map(3) // = elt^(q^3(12*z^3 + 6*z^2 + 4*z - 1))
         V = U * R              // = elt^(q^3(12*z^3 + 6*z^2 + 4*z - 1) + q^2 * (12*z^3 + 6*z^2
         + 6*z) + q*(12*z^3 + 6*z^2 + 4*z) * (12*z^3 + 12*z^2 + 6*z + 1))
         result = V
         */

        final BNFq12T A = ExpByNegativeZ(elt);
        final BNFq12T B = A.cyclotomicSquared();
        final BNFq12T C = B.cyclotomicSquared();
        final BNFq12T D = C.mul(B);
        final BNFq12T E = ExpByNegativeZ(D);
        final BNFq12T F = E.cyclotomicSquared();
        final BNFq12T G = ExpByNegativeZ(F);
        final BNFq12T H = D.unitaryInverse();
        final BNFq12T I = G.unitaryInverse();
        final BNFq12T J = I.mul(E);
        final BNFq12T K = J.mul(H);
        final BNFq12T L = K.mul(B);
        final BNFq12T M = K.mul(E);
        final BNFq12T N = M.mul(elt);
        final BNFq12T O = L.FrobeniusMap(1);
        final BNFq12T P = O.mul(N);
        final BNFq12T Q = K.FrobeniusMap(2);
        final BNFq12T R = Q.mul(P);
        final BNFq12T S = elt.unitaryInverse();
        final BNFq12T T = S.mul(L);
        final BNFq12T U = T.FrobeniusMap(3);

        return U.mul(R);
    }

    private BNFq12T millerLoop(final AteG1Precompute PPrec, final AteG2Precompute QPrec) {
        BNFq12T f = this.publicParameters().bnFq12Factory();

        boolean found = false;
        int idx = 0;

        final BigInteger loopCount = this.publicParameters().ateLoopCount();
        AteEllCoefficients c;

        for (int i = loopCount.bitLength(); i >= 0; i--) {
            final boolean bit = loopCount.testBit(i);
            if (!found) {
                // This skips the MSB itself.
                found |= bit;
                continue;
            }

            // Code below gets executed for all bits (EXCEPT the MSB itself) of
            // alt_bn128_param_p (skipping leading zeros) in MSB to LSB order.
            c = QPrec.coefficients.get(idx++);
            f = f.square();
            f = f.mulBy024(c.ell0, c.ellVW.mul(PPrec.PY), c.ellVV.mul(PPrec.PX));

            if (bit) {
                c = QPrec.coefficients.get(idx++);
                f = f.mulBy024(c.ell0, c.ellVW.mul(PPrec.PY), c.ellVV.mul(PPrec.PX));
            }
        }

        if (this.publicParameters().isAteLoopCountNegative()) {
            f = f.inverse();
        }

        c = QPrec.coefficients.get(idx++);
        f = f.mulBy024(c.ell0, c.ellVW.mul(PPrec.PY), c.ellVV.mul(PPrec.PX));

        c = QPrec.coefficients.get(idx);
        f = f.mulBy024(c.ell0, c.ellVW.mul(PPrec.PY), c.ellVV.mul(PPrec.PX));

        return f;
    }

    protected AteG1Precompute precomputeG1(final BNG1T P) {
        BNG1T PAffine = P.construct(P.X, P.Y, P.Z).toAffineCoordinates();

        return new AteG1Precompute(PAffine.X, PAffine.Y);
    }

    protected AteG2Precompute precomputeG2(final BNG2T Q) {
        BNG2T QAffine = Q.construct(Q.X, Q.Y, Q.Z).toAffineCoordinates();

        BNFqT fqFactory = this.publicParameters().coefficientB();
        BNFqT twoInverse = fqFactory.construct(2).inverse();

        BNG2T R = Q.construct(QAffine.X, QAffine.Y, QAffine.Y.one());
        final BigInteger loopCount = this.publicParameters().ateLoopCount();
        boolean found = false;

        final List<AteEllCoefficients> coeffs = new ArrayList<>();

        for (int i = loopCount.bitLength(); i >= 0; i--) {
            final boolean bit = loopCount.testBit(i);
            if (!found) {
                // This skips the MSB itself.
                found |= bit;
                continue;
            }

            coeffs.add(doublingStepForFlippedMillerLoop(twoInverse, R));

            if (bit) {
                coeffs.add(mixedAdditionStepForFlippedMillerLoop(QAffine, R));
            }
        }

        BNG2T Q1 = this.mulByQ(QAffine);
        assert (Q1.Z.equals(QAffine.X.one()));
        BNG2T Q2 = this.mulByQ(Q1);
        assert (Q2.Z.equals(QAffine.X.one()));

        if (this.publicParameters().isAteLoopCountNegative()) {
            R = R.construct(R.X, R.Y.negate(), R.Z);
        }
        Q2 = Q2.construct(Q2.X, Q2.Y.negate(), Q2.Z);

        coeffs.add(mixedAdditionStepForFlippedMillerLoop(Q1, R));
        coeffs.add(mixedAdditionStepForFlippedMillerLoop(Q2, R));

        return new AteG2Precompute(QAffine.X, QAffine.Y, coeffs);
    }

    protected BNFq12T atePairing(final BNG1T P, final BNG2T Q) {
        final AteG1Precompute PPrec = precomputeG1(P);
        final AteG2Precompute QPrec = precomputeG2(Q);
        return millerLoop(PPrec, QPrec);
    }

    public BNFq12T finalExponentiation(final BNFq12T elt) {
        final BNFq12T A = finalExponentiationFirstChunk(elt);
        return finalExponentiationLastChunk(A);
    }
}