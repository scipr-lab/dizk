/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields;

import algebra.fields.abstractfieldparameters.AbstractFp6_3Over2_Parameters;

import java.io.Serializable;

public class Fp6_3Over2 extends AbstractFieldElement<Fp6_3Over2> implements Serializable {
    protected final Fp2 c0;
    protected final Fp2 c1;
    protected final Fp2 c2;
    private final AbstractFp6_3Over2_Parameters Fp6Parameters;

    public Fp6_3Over2(
            final Fp2 c0,
            final Fp2 c1,
            final Fp2 c2,
            final AbstractFp6_3Over2_Parameters Fp6Parameters) {
        this.c0 = c0;
        this.c1 = c1;
        this.c2 = c2;
        this.Fp6Parameters = Fp6Parameters;
    }

    public Fp6_3Over2 self() {
        return this;
    }

    public Fp6_3Over2 add(final Fp6_3Over2 that) {
        return new Fp6_3Over2(c0.add(that.c0), c1.add(that.c1), c2.add(that.c2), Fp6Parameters);
    }

    public Fp6_3Over2 sub(final Fp6_3Over2 that) {
        return new Fp6_3Over2(c0.sub(that.c0), c1.sub(that.c1), c2.sub(that.c2), Fp6Parameters);
    }

    public Fp6_3Over2 mul(final Fp that) {
        return new Fp6_3Over2(c0.mul(that), c1.mul(that), c2.mul(that), Fp6Parameters);
    }

    public Fp6_3Over2 mul(final Fp2 that) {
        return new Fp6_3Over2(c0.mul(that), c1.mul(that), c2.mul(that), Fp6Parameters);
    }

    public Fp2 mulByNonResidue(final Fp2 that) {
        return Fp6Parameters.nonresidue().mul(that);
    }

    public Fp6_3Over2 mul(final Fp6_3Over2 that) {
    /* Devegili OhEig Scott Dahab --- Multiplication and Squaring on AbstractPairing-Friendly
     Fields.pdf; Section 4 (Karatsuba) */
        final Fp2 c0C0 = c0.mul(that.c0);
        final Fp2 c1C1 = c1.mul(that.c1);
        final Fp2 c2C2 = c2.mul(that.c2);
        final Fp2 c0Factor = c1.add(c2).mul(that.c1.add(that.c2)).sub(c1C1).sub(c2C2);
        final Fp2 c1Factor = c0.add(c1).mul(that.c0.add(that.c1)).sub(c0C0).sub(c1C1);
        final Fp2 c2Factor = c0.add(c2).mul(that.c0.add(that.c2)).sub(c0C0).add(c1C1).sub(c2C2);

        return new Fp6_3Over2(
                c0C0.add(mulByNonResidue(c0Factor)),
                c1Factor.add(mulByNonResidue(c2C2)),
                c2Factor,
                Fp6Parameters);
    }

    public Fp6_3Over2 zero() {
        return Fp6Parameters.ZERO();
    }

    public boolean isZero() {
        return c0.isZero() && c1.isZero() && c2.isZero();
    }

    public Fp6_3Over2 one() {
        return Fp6Parameters.ONE();
    }

    public boolean isOne() {
        return c0.isOne() && c1.isZero() && c2.isZero();
    }

    public Fp6_3Over2 random(final Long seed, final byte[] secureSeed) {
        return new Fp6_3Over2(
                c0.random(seed, secureSeed),
                c1.random(seed, secureSeed),
                c2.random(seed, secureSeed),
                Fp6Parameters);
    }

    public Fp6_3Over2 negate() {
        return new Fp6_3Over2(c0.negate(), c1.negate(), c2.negate(), Fp6Parameters);
    }

    public Fp6_3Over2 square() {
    /* Devegili OhEig Scott Dahab --- Multiplication and Squaring on AbstractPairing-Friendly
     Fields.pdf; Section 4 (CH-SQR2) */
        final Fp2 s0 = c0.square();
        final Fp2 c0c1 = c0.mul(c1);
        final Fp2 s1 = c0c1.add(c0c1);
        final Fp2 s2 = c0.sub(c1).add(c2).square();
        final Fp2 c1c2 = c1.mul(c2);
        final Fp2 s3 = c1c2.add(c1c2);
        final Fp2 s4 = c2.square();

        return new Fp6_3Over2(
                s0.add(mulByNonResidue(s3)),
                s1.add(mulByNonResidue(s4)),
                s1.add(s2).add(s3).sub(s0).sub(s4),
                Fp6Parameters);
    }

    public Fp6_3Over2 inverse() {
    /* From "High-Speed Software Implementation of the Optimal Ate AbstractPairing over
    Barreto-Naehrig Curves"; Algorithm 17 */
        final Fp2 t0 = c0.square();
        final Fp2 t1 = c1.square();
        final Fp2 t2 = c2.square();
        final Fp2 t3 = c0.mul(c1);
        final Fp2 t4 = c0.mul(c2);
        final Fp2 t5 = c1.mul(c2);
        final Fp2 s0 = t0.sub(mulByNonResidue(t5));
        final Fp2 s1 = mulByNonResidue(t2).sub(t3);
        final Fp2 s2 = t1
                .sub(t4); // typo in paper referenced above. should be "-" as per Scott, but is "*"
        final Fp2 t6 = c0.mul(s0).add(mulByNonResidue(c2.mul(s1).add(c1.mul(s2)))).inverse();

        return new Fp6_3Over2(t6.mul(s0), t6.mul(s1), t6.mul(s2), Fp6Parameters);
    }

    public Fp6_3Over2 FrobeniusMap(long power) {
        return new Fp6_3Over2(c0.FrobeniusMap(power),
                Fp6Parameters.FrobeniusMapCoefficientsC1()[(int) (power % 6)].mul(c1.FrobeniusMap(power)),
                Fp6Parameters.FrobeniusMapCoefficientsC2()[(int) (power % 6)].mul(c2.FrobeniusMap(power)),
                Fp6Parameters);
    }

    public int bitSize() {
        return Math.max(c0.bitSize(), Math.max(c1.bitSize(), c2.bitSize()));
    }

    public Fp6_3Over2 construct(final Fp2 c0, final Fp2 c1, final Fp2 c2) {
        return new Fp6_3Over2(c0, c1, c2, Fp6Parameters);
    }

    public String toString() {
        return c0.toString() + " / " + c1.toString() + " / " + c2.toString();
    }

    public boolean equals(final Fp6_3Over2 that) {
        if (that == null) {
            return false;
        }

        return c0.equals(that.c0) && c1.equals(that.c1) && c2.equals(that.c2);
    }
}
