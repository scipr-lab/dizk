/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig;

import algebra.curves.AbstractG2;
import algebra.curves.barreto_naehrig.BNFields.BNFq;
import algebra.curves.barreto_naehrig.BNFields.BNFq2;
import algebra.curves.barreto_naehrig.BNFields.BNFr;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG2Parameters;

import java.util.ArrayList;

public abstract class BNG2<
        BNFrT extends BNFr<BNFrT>,
        BNFqT extends BNFq<BNFqT>,
        BNFq2T extends BNFq2<BNFqT, BNFq2T>,
        BNG2T extends BNG2<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
        BNG2ParametersT extends AbstractBNG2Parameters<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>>
        extends AbstractG2<BNG2T> {
    protected final BNG2ParametersT G2Parameters;
    protected BNFq2T X;
    protected BNFq2T Y;
    protected BNFq2T Z;

    public BNG2(
            final BNFq2T X,
            final BNFq2T Y,
            final BNFq2T Z,
            final BNG2ParametersT G2Parameters) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.G2Parameters = G2Parameters;
    }

    public abstract BNG2T construct(BNFq2T X, BNFq2T Y, BNFq2T Z);

    public BNG2T add(final BNG2T other) {
        // Handle special cases having to do with O
        if (isZero()) {
            return other;
        }

        if (other.isZero()) {
            return this.self();
        }

        // No need to handle points of order 2,4
        // (they cannot exist in a modulus-order subgroup)

        // Check for doubling case

        // Using Jacobian coordinates so:
        // (X1:Y1:Z1) = (X2:Y2:Z2)
        // iff
        // X1/Z1^2 == X2/Z2^2 and Y1/Z1^3 == Y2/Z2^3
        // iff
        // X1 * Z2^2 == X2 * Z1^2 and Y1 * Z2^3 == Y2 * Z1^3

        final BNFq2T Z1Z1 = this.Z.square();
        final BNFq2T Z2Z2 = other.Z.square();

        final BNFq2T U1 = this.X.mul(Z2Z2);
        final BNFq2T U2 = other.X.mul(Z1Z1);

        final BNFq2T Z1Cubed = this.Z.mul(Z1Z1);
        final BNFq2T Z2Cubed = other.Z.mul(Z2Z2);

        // S1 = Y1 * Z2 * Z2Z2
        final BNFq2T S1 = this.Y.mul(Z2Cubed);
        // S2 = Y2 * Z1 * Z1Z1
        final BNFq2T S2 = other.Y.mul(Z1Cubed);

        if (U1.equals(U2) && S1.equals(S2)) {
            // Double case
            // Nothing of above can be reused
            return dbl();
        }

        // Rest of the add case
        // H = U2-U1
        final BNFq2T H = U2.sub(U1);
        // I = (2 * H)^2
        final BNFq2T I = H.add(H).square();
        // J = H * I
        final BNFq2T J = H.mul(I);
        // r = 2 * (S2-S1)
        final BNFq2T S2MinusS1 = S2.sub(S1);
        final BNFq2T r = S2MinusS1.add(S2MinusS1);
        // V = U1 * I
        final BNFq2T V = U1.mul(I);
        // X3 = r^2 - J - 2 * V
        final BNFq2T X3 = r.square().sub(J).sub(V.add(V));
        // Y3 = r * (V-X3)-2 S1 J
        final BNFq2T S1_J = S1.mul(J);
        final BNFq2T Y3 = r.mul(V.sub(X3)).sub(S1_J.add(S1_J));
        // Z3 = ((Z1+Z2)^2-Z1Z1-Z2Z2) * H
        final BNFq2T Z3 = this.Z.add(other.Z).square().sub(Z1Z1).sub(Z2Z2).mul(H);

        return this.construct(X3, Y3, Z3);
    }

    public BNG2T sub(final BNG2T other) {
        return this.add(other.negate());
    }

    public BNG2T dbl() {
        if (isZero()) {
            return this.self();
        }

        // NOTE: does not handle O and pts of order 2,4
        // http://www.hyperelliptic.org/EFD/g1p/auto-shortw-projective.html#doubling-dbl-2007-bl

        // A = X1^2
        final BNFq2T A = this.X.square();
        // B = Y1^2
        final BNFq2T B = this.Y.square();
        // C = B^2
        final BNFq2T C = B.square();
        // D = 2 * ((X1 + B)^2 - A - C)
        BNFq2T D = this.X.add(B).square().sub(A).sub(C);
        D = D.add(D);
        // E = 3 * A
        final BNFq2T E = A.add(A).add(A);
        // F = E^2
        final BNFq2T F = E.square();
        // X3 = F - 2 D
        final BNFq2T X3 = F.sub(D.add(D));
        // Y3 = E * (D - X3) - 8 * C
        BNFq2T eightC = C.add(C);
        eightC = eightC.add(eightC);
        eightC = eightC.add(eightC);
        final BNFq2T Y3 = E.mul(D.sub(X3)).sub(eightC);
        final BNFq2T Y1Z1 = this.Y.mul(this.Z);
        // Z3 = 2 * Y1 * Z1
        final BNFq2T Z3 = Y1Z1.add(Y1Z1);

        return this.construct(X3, Y3, Z3);
    }

    public boolean isZero() {
        return this.Z.isZero();
    }

    public boolean isSpecial() {
        return isZero() || this.Z.isOne();
    }

    public boolean isOne() {
        return this.X.isOne()
            && this.Y.isOne()
            && this.Z.isOne();
    }

    public BNG2T zero() {
        return this.G2Parameters.ZERO();
    }

    public BNG2T one() {
        return this.G2Parameters.ONE();
    }

    public BNG2T random(final Long seed, final byte[] secureSeed) {
        return this.one().mul(this.G2Parameters.oneFr().random(seed, secureSeed).toBigInteger());
    }

    public BNG2T negate() {
        return this.construct(this.X, this.Y.negate(), this.Z);
    }

    public void setX(final BNFq2T X) {
        this.X = X;
    }

    public void setY(final BNFq2T Y) {
        this.Y = Y;
    }

    public void setZ(final BNFq2T Z) {
        this.Z = Z;
    }

    public BNG2T toAffineCoordinates() {
        if (isZero()) {
            return this.construct(this.X.zero(), this.Y.one(), this.Z.zero());
        } else {
            final BNFq2T ZInverse = this.Z.inverse();
            final BNFq2T Z2Inverse = ZInverse.square();
            final BNFq2T Z3Inverse = Z2Inverse.mul(ZInverse);
            return this.construct(this.X.mul(Z2Inverse), this.Y.mul(Z3Inverse), this.Z.one());
        }
    }

    public int bitSize() {
        return Math.max(this.X.bitSize(), Math.max(this.Y.bitSize(), this.Z.bitSize()));
    }

    public ArrayList<Integer> fixedBaseWindowTable() {
        return this.G2Parameters.fixedBaseWindowTable();
    }

    public String toString() {
        if (isZero()) {
            return "0";
        }

        return this.X.toString() + ", " + this.Y.toString() + ", " + this.Z.toString();
    }

    public boolean equals(final BNG2T other) {
        if (isZero()) {
            return other.isZero();
        }

        if (other.isZero()) {
            return false;
        }

        // Now neither is O.

        // using Jacobian coordinates so:
        // (X1:Y1:Z1) = (X2:Y2:Z2)
        // iff
        // X1/Z1^2 == X2/Z2^2 and Y1/Z1^3 == Y2/Z2^3
        // iff
        // X1 * Z2^2 == X2 * Z1^2 and Y1 * Z2^3 == Y2 * Z1^3

        final BNFq2T Z1Squared = this.Z.square();
        final BNFq2T Z2Squared = other.Z.square();

        if (!this.X.mul(Z2Squared).equals(other.X.mul(Z1Squared))) {
            return false;
        }

        final BNFq2T Z1Cubed = this.Z.mul(Z1Squared);
        final BNFq2T Z2Cubed = other.Z.mul(Z2Squared);

        return this.Y.mul(Z2Cubed).equals(other.Y.mul(Z1Cubed));
    }
}
