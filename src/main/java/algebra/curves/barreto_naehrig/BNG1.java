/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig;

import algebra.curves.AbstractG1;
import algebra.curves.barreto_naehrig.BNFields.BNFq;
import algebra.curves.barreto_naehrig.BNFields.BNFr;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG1Parameters;

import java.util.ArrayList;

/**
 * Generic code to construct and operate on BN G1 points. This class is used to represent
 * a BN G1 group as well as their points (both are mixed here):
 *  - BNG1(): Is used to construct the group
 *  - BNG1.construct(): Is used to construct the points (of type BNG1T)
 * => construct() wraps the group constructor
 *  - Other functions of this class like: add() operate on `BNG1T` (the points)
 */
public abstract class BNG1<
        BNFrT extends BNFr<BNFrT>,
        BNFqT extends BNFq<BNFqT>,
        BNG1T extends BNG1<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
        BNG1ParametersT extends AbstractBNG1Parameters<BNFrT, BNFqT, BNG1T, BNG1ParametersT>>
        extends AbstractG1<BNG1T> {
    public final BNG1ParametersT G1Parameters;
    protected final BNFqT X;
    protected final BNFqT Y;
    public final BNFqT Z;

    public BNG1(final BNFqT X, final BNFqT Y, final BNFqT Z, final BNG1ParametersT G1Parameters) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.G1Parameters = G1Parameters;
    }

    public abstract BNG1T construct(BNFqT X, BNFqT Y, BNFqT Z);

    public BNG1T add(final BNG1T other) {
        assert (other != null);

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

        final BNFqT Z1Z1 = this.Z.square();
        final BNFqT Z2Z2 = other.Z.square();

        final BNFqT U1 = this.X.mul(Z2Z2);
        final BNFqT U2 = other.X.mul(Z1Z1);

        final BNFqT Z1_cubed = this.Z.mul(Z1Z1);
        final BNFqT Z2_cubed = other.Z.mul(Z2Z2);

        // S1 = Y1 * Z2 * Z2Z2
        final BNFqT S1 = this.Y.mul(Z2_cubed);
        // S2 = Y2 * Z1 * Z1Z1
        final BNFqT S2 = other.Y.mul(Z1_cubed);

        if (U1.equals(U2) && S1.equals(S2)) {
            // Doubling case
            // Nothing above can be reused
            return dbl();
        }

        // Rest of the add case
        // H = U2-U1
        final BNFqT H = U2.sub(U1);
        // I = (2 * H)^2
        final BNFqT I = H.add(H).square();
        // J = H * I
        final BNFqT J = H.mul(I);
        // r = 2 * (S2-S1)
        final BNFqT S2MinusS1 = S2.sub(S1);
        final BNFqT r = S2MinusS1.add(S2MinusS1);
        // V = U1 * I
        final BNFqT V = U1.mul(I);
        // X3 = r^2 - J - 2 * V
        final BNFqT X3 = r.square().sub(J).sub(V.add(V));
        final BNFqT S1_J = S1.mul(J);
        // Y3 = r * (V-X3)-2 * S1_J
        final BNFqT Y3 = r.mul(V.sub(X3)).sub(S1_J.add(S1_J));
        // Z3 = ((Z1+Z2)^2-Z1Z1-Z2Z2) * H
        final BNFqT Z3 = this.Z.add(other.Z).square().sub(Z1Z1).sub(Z2Z2).mul(H);

        return this.construct(X3, Y3, Z3);
    }

    public BNG1T sub(final BNG1T other) {
        return this.add(other.negate());
    }

    public boolean isZero() {
        return this.Z.isZero();
    }

    public boolean isOne() {
        return this.X.equals(this.one().X)
                && this.Y.equals(this.one().Y)
                && this.Z.equals(this.one().Z);
    }

    public boolean isSpecial() {
        return isZero() || isOne();
    }

    public BNG1T zero() {
        return this.G1Parameters.ZERO();
    }

    public BNG1T one() {
        return this.G1Parameters.ONE();
    }

    public BNG1T random(final Long seed, final byte[] secureSeed) {
        return this.one().mul(this.G1Parameters.oneFr().random(seed, secureSeed));
    }

    public BNG1T negate() {
        return this.construct(this.X, this.Y.negate(), this.Z);
    }

    public BNG1T dbl() {
        // Handle point at infinity
        if (isZero()) {
            return this.self();
        }

        // No need to handle points of order 2,4
        // (they cannot exist in a modulus-order subgroup)

        // NOTE: does not handle O and pts of order 2,4
        // http://www.hyperelliptic.org/EFD/g1p/auto-shortw-jacobian-0.html#doubling-dbl-2009-l

        // A = X1^2
        final BNFqT A = this.X.square();
        // B = Y1^2
        final BNFqT B = this.Y.square();
        // C = B^2
        final BNFqT C = B.square();
        // D = 2 * ((X1 + B)^2 - A - C)
        BNFqT D = this.X.add(B).square().sub(A).sub(C);
        D = D.add(D);
        // E = 3 * A
        final BNFqT E = A.add(A).add(A);
        // F = E^2
        final BNFqT F = E.square();
        // X3 = F - 2 D
        final BNFqT X3 = F.sub(D.add(D));
        // Y3 = E * (D - X3) - 8 * C
        BNFqT eightC = C.add(C);
        eightC = eightC.add(eightC);
        eightC = eightC.add(eightC);
        final BNFqT Y3 = E.mul(D.sub(X3)).sub(eightC);
        // Z3 = 2 * Y1 * Z1
        final BNFqT Y1Z1 = this.Y.mul(this.Z);
        final BNFqT Z3 = Y1Z1.add(Y1Z1);

        return this.construct(X3, Y3, Z3);
    }

    public BNG1T toAffineCoordinates() {
        if (isZero()) {
            return this.construct(this.X.zero(), this.Y.one(), this.Z.zero());
        } else {
            BNFqT ZInverse = this.Z.inverse();
            BNFqT Z2Inverse = ZInverse.square();
            BNFqT Z3Inverse = Z2Inverse.mul(ZInverse);
            return this.construct(this.X.mul(Z2Inverse), this.Y.mul(Z3Inverse), this.Z.one());
        }
    }

    public int bitSize() {
        return Math.max(this.X.bitSize(), Math.max(this.Y.bitSize(), this.Z.bitSize()));
    }

    public ArrayList<Integer> fixedBaseWindowTable() {
        return this.G1Parameters.fixedBaseWindowTable();
    }

    public String toString() {
        if (isZero()) {
            return "0";
        }

        return this.X.toString() + ", " + this.Y.toString() + ", " + this.Z.toString();
    }

    public boolean equals(final BNG1T other) {
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

        final BNFqT Z1_squared = this.Z.square();
        final BNFqT Z2_squared = other.Z.square();

        if (!this.X.mul(Z2_squared).equals(other.X.mul(Z1_squared))) {
            return false;
        }

        final BNFqT Z1_cubed = this.Z.mul(Z1_squared);
        final BNFqT Z2_cubed = other.Z.mul(Z2_squared);

        if (!this.Y.mul(Z2_cubed).equals(other.Y.mul(Z1_cubed))) {
            return false;
        }

        return true;
    }
}
