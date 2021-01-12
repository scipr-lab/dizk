package algebra.curves.barreto_lynn_scott;

import algebra.curves.AbstractPairing;
import algebra.curves.barreto_lynn_scott.BLSFields.*;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSG1Parameters;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSG2Parameters;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSGTParameters;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public abstract class BLSPairing<
        BLSFrT extends BLSFr<BLSFrT>,
        BLSFqT extends BLSFq<BLSFqT>,
        BLSFq2T extends BLSFq2<BLSFqT, BLSFq2T>,
        BLSFq6T extends BLSFq6<BLSFqT, BLSFq2T, BLSFq6T>,
        BLSFq12T extends BLSFq12<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T>,
        BLSG1T extends BLSG1<BLSFrT, BLSFqT, BLSG1T, BLSG1ParametersT>,
        BLSG2T extends BLSG2<BLSFrT, BLSFqT, BLSFq2T, BLSG2T, BLSG2ParametersT>,
        BLSGTT extends BLSGT<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T, BLSGTT, BLSGTParametersT>,
        BLSG1ParametersT extends AbstractBLSG1Parameters<BLSFrT, BLSFqT, BLSG1T, BLSG1ParametersT>,
        BLSG2ParametersT extends
            AbstractBLSG2Parameters<BLSFrT, BLSFqT, BLSFq2T, BLSG2T, BLSG2ParametersT>,
        BLSGTParametersT extends
            AbstractBLSGTParameters<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T, BLSGTT, BLSGTParametersT>,
        BLSPublicParametersT extends BLSPublicParameters<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T>>
    extends AbstractPairing<BLSG1T, BLSG2T, BLSGTT> {

  public abstract BLSPublicParametersT publicParameters();

  protected class AteG1Precompute extends G1Precompute {
    final BLSFqT PX;
    final BLSFqT PY;

    private AteG1Precompute(final BLSFqT PX, final BLSFqT PY) {
      this.PX = PX;
      this.PY = PY;
    }

    public boolean equals(final AteG1Precompute other) {
      return PX.equals(other.PX) && PY.equals(other.PY);
    }
  } // AteG1Precompute

  protected class AteEllCoefficients {
    BLSFq2T ell0;
    BLSFq2T ellVW;
    BLSFq2T ellVV;

    public boolean equals(final AteEllCoefficients other) {
      return this.ell0.equals(other.ell0)
          && this.ellVW.equals(other.ellVW)
          && this.ellVV.equals(other.ellVV);
    }
  } // AteEllCoefficients

  protected class AteG2Precompute extends G2Precompute {
    final BLSFq2T QX;
    final BLSFq2T QY;
    final List<AteEllCoefficients> coefficients;

    private AteG2Precompute(
        final BLSFq2T QX, final BLSFq2T QY, final List<AteEllCoefficients> coefficients) {
      this.QX = QX;
      this.QY = QY;
      this.coefficients = coefficients;
    }

    public boolean equals(final AteG2Precompute other) {
      return this.QX.equals(other.QX)
          && this.QY.equals(other.QY)
          && this.coefficients.equals(other.coefficients);
    }
  } // AteG2Precompute

  private AteEllCoefficients doublingStepForFlippedMillerLoop(
      final BLSFqT twoInverse, BLSG2T current) {
    // Below we assume that `current` = (X, Y, Z) \in E'(Fp2) is a point
    // in homogeneous projective coordinates.
    final BLSFq2T X = current.X, Y = current.Y, Z = current.Z;

    // A = (X * Y) / 2
    final BLSFq2T A = X.mul(Y).mul(twoInverse);
    // B = Y^2
    final BLSFq2T B = Y.square();
    // C = Z^2
    final BLSFq2T C = Z.square();
    // D = 3 * C
    final BLSFq2T D = C.add(C).add(C);
    // E = twist_b * D
    final BLSFq2T E = this.publicParameters().twistCoefficientB().mul(D);
    // F = 3 * E
    final BLSFq2T F = E.add(E).add(E);
    // G = (B + F)/2
    final BLSFq2T G = B.add(F).mul(twoInverse);
    // H = (Y + Z)^2 - (B + C)
    final BLSFq2T H = (Y.add(Z)).square().sub(B.add(C));
    // I = E - B
    final BLSFq2T I = E.sub(B);
    // J = X^2
    final BLSFq2T J = X.square();
    // E_squared = E^2
    final BLSFq2T ESquare = E.square();

    // X3 = A * (B - F)
    current.setX(A.mul(B.sub(F)));
    // Y3 = G^2 - 3*E^2
    current.setY(G.square().sub(ESquare.add(ESquare).add(ESquare)));
    // Z3 = B * H
    current.setZ(B.mul(H));

    AteEllCoefficients c = new AteEllCoefficients();
    // ell_0 = xi * I = xi * (3 * twist_b * Z^2 - Y^2)
    c.ell0 = this.publicParameters().twist().mul(I);
    // ell_VW = -H = -2YZ (later: * yP)
    c.ellVW = H.negate();
    // ell_VV = 3*J = 3*X^2 (later: * xP)
    c.ellVV = J.add(J).add(J);

    return c;
  }

  private AteEllCoefficients mixedAdditionStepForFlippedMillerLoop(
      final BLSG2T base, BLSG2T current) {
    final BLSFq2T X1 = current.X, Y1 = current.Y, Z1 = current.Z;
    final BLSFq2T X2 = base.X, Y2 = base.Y;

    final BLSFq2T A = Y2.mul(Z1);
    final BLSFq2T B = X2.mul(Z1);
    final BLSFq2T theta = Y1.sub(A);
    final BLSFq2T lambda = X1.sub(B);
    final BLSFq2T C = theta.square();
    final BLSFq2T D = lambda.square();
    final BLSFq2T E = lambda.mul(D);
    final BLSFq2T F = Z1.mul(C);
    final BLSFq2T G = X1.mul(D);
    final BLSFq2T H = E.add(F).sub(G.add(G));
    final BLSFq2T I = Y1.mul(E);
    final BLSFq2T J = theta.mul(X2).sub(lambda.mul(Y2));

    current.setX(lambda.mul(H));
    current.setY(theta.mul(G.sub(H)).sub(I));
    current.setZ(Z1.mul(E));

    AteEllCoefficients c = new AteEllCoefficients();
    c.ell0 = this.publicParameters().twist().mul(J);
    // VV gets multiplied to xP during line evaluation at P
    c.ellVV = theta.negate();
    // VW gets multiplied to yP during line evaluation at P
    c.ellVW = lambda;

    return c;
  }

  protected AteG1Precompute precomputeG1(final BLSG1T P) {
    BLSG1T PAffine = P.construct(P.X, P.Y, P.Z).toAffineCoordinates();

    return new AteG1Precompute(PAffine.X, PAffine.Y);
  }

  protected AteG2Precompute precomputeG2(final BLSG2T Q) {
    BLSG2T QAffine = Q.construct(Q.X, Q.Y, Q.Z).toAffineCoordinates();
    // Used for debug only
    BLSG2T QAffineSave = Q.construct(Q.X, Q.Y, Q.Z).toAffineCoordinates();

    BLSFqT fqFactory = this.publicParameters().coefficientB();
    BLSFqT twoInverse = fqFactory.construct(2).inverse();

    BLSG2T R = Q.construct(QAffine.X, QAffine.Y, QAffine.Y.one());

    // ateLoopCount = new BigInteger("9586122913090633729");
    final BigInteger loopCount = this.publicParameters().ateLoopCount();
    boolean found = false;

    final List<AteEllCoefficients> coeffs = new ArrayList<>();

    for (int i = loopCount.bitLength(); i >= 0; --i) {
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

    //BLSG2T Q1 = this.mulByQ(QAffine);
    //assert (Q1.Z.equals(QAffine.X.one()));
    //BLSG2T Q2 = this.mulByQ(Q1);
    //assert (Q2.Z.equals(QAffine.X.one()));

    return new AteG2Precompute(QAffineSave.X, QAffineSave.Y, coeffs);
  }

  // Implementation of the Miller loop for BLS12_377 curve
  // See https://eprint.iacr.org/2019/077.pdf for more info and potential optimizations
  private BLSFq12T millerLoop(final AteG1Precompute PPrec, final AteG2Precompute QPrec) {
    // blsFq12Factory = BLS12_377Fq12.ONE;
    BLSFq12T f = this.publicParameters().blsFq12Factory();

    boolean found = false;
    int idx = 0;

    // ateLoopCount = new BigInteger("9586122913090633729");
    final BigInteger loopCount = this.publicParameters().ateLoopCount();
    AteEllCoefficients c;

    for (int i = loopCount.bitLength(); i >= 0; --i) {
      final boolean bit = loopCount.testBit(i);
      if (!found) {
        // This skips the MSB itself.
        found |= bit;
        continue;
      }

      // Code below gets executed for all bits (EXCEPT the MSB itself) of
      // loopCount (skipping leading zeros) in MSB to LSB order.
      c = QPrec.coefficients.get(idx++);
      // Note: This squaring in Fq12 can be eliminated for the first loop
      // (since f is initialized with ONE in Fq12)
      // See Algo 6: https://www.iacr.org/archive/eurocrypt2011/66320047/66320047.pdf
      f = f.square();
      // Note: For the first iteration, f is ONE in Fq12 and is thus sparse.
      // Hence we can do a sparse/sparse multiplication for the line accumulation here.
      f = f.mulBy024(c.ell0, c.ellVW.mul(PPrec.PY), c.ellVV.mul(PPrec.PX));

      if (bit) {
        c = QPrec.coefficients.get(idx++);
        f = f.mulBy024(c.ell0, c.ellVW.mul(PPrec.PY), c.ellVV.mul(PPrec.PX));
      }
    }

    // Not executed for BLS12_377
    //if (this.publicParameters().isAteLoopCountNegative()) {
    //  f = f.inverse();
    //}

    return f;
  }

  // TODO: Move in the G2 group implementation
  private BLSG2T mulByQ(final BLSG2T element) {
    return element.construct(
        publicParameters().qXMulTwist().mul(element.X.FrobeniusMap(1)),
        publicParameters().qYMulTwist().mul(element.Y.FrobeniusMap(1)),
        element.Z.FrobeniusMap(1));
  }

  private BLSFq12T ExpByZ(final BLSFq12T elt) {
    BLSFq12T result = elt.cyclotomicExponentiation(this.publicParameters().finalExponentZ());
    if (this.publicParameters().isFinalExponentZNegative()) {
      result = result.unitaryInverse();
    }
    return result;
  }

  public BLSFq12T finalExponentiation(final BLSFq12T elt) {
    // We know that:
    // (p^12 - 1) / r = (p^6 - 1) (p^2 + 1) ((p^4 - p^2 + 1) / r)
    //                  |_________________|  |__________________|
    //                       easy part            hard part
    // where:
    // sage: cyclotomic_polynomial(12) # = x^4 - x^2 + 1
    final BLSFq12T easyPart = finalExponentiationFirstChunk(elt);
    return finalExponentiationLastChunk(easyPart);
  }

  private BLSFq12T finalExponentiationFirstChunk(final BLSFq12T elt) {
    // elt^(q^6)
    final BLSFq12T A = elt.FrobeniusMap(6);
    // elt^(-1)
    final BLSFq12T B = elt.inverse();
    // elt^(q^6 - 1)
    final BLSFq12T C = A.mul(B);
    // (elt^(q^6 - 1))^(q^2) = elt^((q^6 - 1) * (q^2))
    final BLSFq12T D = C.FrobeniusMap(2);
    // elt^((q^6 - 1) * (q^2) + (q^6 - 1)) = elt^((q^6 - 1) * (q^2 + 1))
    final BLSFq12T result = D.mul(C);

    // // Computes result = elt^((q^6-1)*(q^2+1)).
    // // Follows Beuchat et al page 9: https://eprint.iacr.org/2010/354.pdf
    // // by computing result as follows:
    // // elt^((q^6-1)*(q^2+1)) = (conj(elt) * elt^(-1))^(q^2+1)
    // final BLSFq12T A = elt.unitaryInverse();
    // final BLSFq12T B = elt.inverse();
    // final BLSFq12T C = A.mul(B);
    // final BLSFq12T D = C.FrobeniusMap(2);
    // final BLSFq12T result = D.mul(C);
    // return result;

    return result;
  }

  private BLSFq12T finalExponentiationLastChunk(final BLSFq12T elt) {
    // In the following, we follow the Algorithm 1 described in Table 1 of:
    // https://eprint.iacr.org/2016/130.pdf in order to compute the
    // hard part of the final exponentiation
    //
    // Note: As shown Table 3: https://eprint.iacr.org/2016/130.pdf this algorithm
    // isn't optimal since Algorithm 2 allows to have less temp. variables and has
    // a better complexity.
    //
    // In the following we denote by [x] = elt^(x):
    // A = [-2]
    final BLSFq12T A = elt.cyclotomicSquared().unitaryInverse();
    // B = [z]
    final BLSFq12T B = ExpByZ(elt);
    // C = [2z]
    final BLSFq12T C = B.cyclotomicSquared();
    // D = [z-2]
    final BLSFq12T D = A.mul(B);
    // E = [z^2-2z]
    final BLSFq12T E = ExpByZ(D);
    // F = [z^3-2z^2]
    final BLSFq12T F = ExpByZ(E);
    // G = [z^4-2z^3]
    final BLSFq12T G = ExpByZ(F);
    // H = [z^4-2z^3+2z]
    final BLSFq12T H = G.mul(C);
    // I = [z^5-2z^4+2z^2]
    final BLSFq12T I = ExpByZ(H);
    // J = [-z+2]
    final BLSFq12T J = D.unitaryInverse();
    // K = [z^5-2z^4+2z^2-z+2]
    final BLSFq12T K = I.mul(J);
    // L = [z^5-2z^4+2z^2-z+3] = [\lambda_0]
    final BLSFq12T L = K.mul(elt);
    // M = [-1]
    final BLSFq12T M = elt.unitaryInverse();
    // N = [z^2-2z+1] = [\lambda_3]
    final BLSFq12T N = E.mul(elt);
    // O = [(z^2-2z+1) * (q^3)]
    final BLSFq12T O = N.FrobeniusMap(3);
    // P = [z^4-2z^3+2z-1] = [\lambda_1]
    final BLSFq12T P = H.mul(M);
    // Q = [(z^4-2z^3+2z-1) * q]
    final BLSFq12T Q = P.FrobeniusMap(1);
    // R = [z^3-2z^2+z] = [\lambda_2]
    final BLSFq12T R = F.mul(B);
    // S = [(z^3-2z^2+z) * (q^2)]
    final BLSFq12T S = R.FrobeniusMap(2);
    // T = [(z^2-2z+1) * (q^3) + (z^3-2z^2+z) * (q^2)]
    final BLSFq12T T = O.mul(S);
    // U = [(z^2-2z+1) * (q^3) + (z^3-2z^2+z) * (q^2) + (z^4-2z^3+2z-1) * q]
    final BLSFq12T U = T.mul(Q);
    // result = [(z^2-2z+1) * (q^3) + (z^3-2z^2+z) * (q^2) + (z^4-2z^3+2z-1) * q + z^5-2z^4+2z^2-z+3]
    //        = [(p^4 - p^2 + 1)/r].
    final BLSFq12T result = U.mul(L);

    return result;
  }

  protected BLSFq12T atePairing(final BLSG1T P, final BLSG2T Q) {
    final AteG1Precompute PPrec = precomputeG1(P);
    final AteG2Precompute QPrec = precomputeG2(Q);
    return millerLoop(PPrec, QPrec);
  }
}
