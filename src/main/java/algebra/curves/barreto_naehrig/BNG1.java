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
 *  - BNG1.construct(): I used to construct the points (of type BNG1T)
 * --> construct() wraps the group constructor
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

    public BNG1T add(final BNG1T that) {
        assert (that != null);

        // Handle special cases having to do with O
        if (isZero()) {
            return that;
        }

        if (that.isZero()) {
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
        final BNFqT Z2Z2 = that.Z.square();

        final BNFqT U1 = this.X.mul(Z2Z2);
        final BNFqT U2 = that.X.mul(Z1Z1);

        final BNFqT Z1_cubed = this.Z.mul(Z1Z1);
        final BNFqT Z2_cubed = that.Z.mul(Z2Z2);

        final BNFqT S1 = this.Y.mul(Z2_cubed);      // S1 = Y1 * Z2 * Z2Z2
        final BNFqT S2 = that.Y.mul(Z1_cubed);      // S2 = Y2 * Z1 * Z1Z1

        if (U1.equals(U2) && S1.equals(S2)) {
            // Double case; nothing above can be reused.
            return dbl();
        }

        // Rest of the add case.
        final BNFqT H = U2.sub(U1);                                   // H = U2-U1
        final BNFqT S2_minus_S1 = S2.sub(S1);
        final BNFqT I = H.add(H).square();                            // I = (2 * H)^2
        final BNFqT J = H.mul(I);                                     // J = H * I
        final BNFqT r = S2_minus_S1.add(S2_minus_S1);                 // r = 2 * (S2-S1)
        final BNFqT V = U1.mul(I);                                    // V = U1 * I
        final BNFqT X3 = r.square().sub(J).sub(V.add(V));             // X3 = r^2 - J - 2 * V
        final BNFqT S1_J = S1.mul(J);
        final BNFqT Y3 = r.mul(V.sub(X3)).sub(S1_J.add(S1_J));        // Y3 = r * (V-X3)-2 * S1_J
        final BNFqT Z3 = this.Z.add(that.Z).square().sub(Z1Z1).sub(Z2Z2)
                .mul(H);                                                  // Z3 = ((Z1+Z2)^2-Z1Z1-Z2Z2) * H

        return this.construct(X3, Y3, Z3);
    }

    public BNG1T sub(final BNG1T that) {
        return this.add(that.negate());
    }

    public boolean isZero() {
        return this.Z.isZero();
    }

    public boolean isSpecial() {
        return isZero() || isOne();
    }

    public boolean isOne() {
        return this.X.equals(this.one().X)
                && this.Y.equals(this.one().Y)
                && this.Z.equals(this.one().Z);
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
        // handle point at infinity
        if (isZero()) {
            return this.self();
        }

        // No need to handle points of order 2,4
        // (they cannot exist in a modulus-order subgroup)

        // NOTE: does not handle O and pts of order 2,4
        // http://www.hyperelliptic.org/EFD/g1p/auto-shortw-jacobian-0.html#doubling-dbl-2009-l

        final BNFqT A = this.X.square();                       // A = X1^2
        final BNFqT B = this.Y.square();                       // B = Y1^2
        final BNFqT C = B.square();                            // C = B^2
        BNFqT D = this.X.add(B).square().sub(A).sub(C);
        D = D.add(D);                                          // D = 2 * ((X1 + B)^2 - A - C)
        final BNFqT E = A.add(A).add(A);                       // E = 3 * A
        final BNFqT F = E.square();                            // F = E^2
        final BNFqT X3 = F.sub(D.add(D));                      // X3 = F - 2 D
        BNFqT eightC = C.add(C);
        eightC = eightC.add(eightC);
        eightC = eightC.add(eightC);
        final BNFqT Y3 = E.mul(D.sub(X3)).sub(eightC);         // Y3 = E * (D - X3) - 8 * C
        final BNFqT Y1Z1 = this.Y.mul(this.Z);
        final BNFqT Z3 = Y1Z1.add(Y1Z1);                       // Z3 = 2 * Y1 * Z1

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

    public boolean equals(final BNG1T that) {
        if (isZero()) {
            return that.isZero();
        }

        if (that.isZero()) {
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
        final BNFqT Z2_squared = that.Z.square();

        if (!this.X.mul(Z2_squared).equals(that.X.mul(Z1_squared))) {
            return false;
        }

        final BNFqT Z1_cubed = this.Z.mul(Z1_squared);
        final BNFqT Z2_cubed = that.Z.mul(Z2_squared);

        if (!this.Y.mul(Z2_cubed).equals(that.Y.mul(Z1_cubed))) {
            return false;
        }

        return true;
    }
}
