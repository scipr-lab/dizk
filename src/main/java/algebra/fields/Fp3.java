/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields;

import algebra.fields.abstractfieldparameters.AbstractFp3Parameters;

import java.io.Serializable;

public class Fp3 extends AbstractFieldElement<Fp3> implements Serializable {
    protected final Fp c0;
    protected final Fp c1;
    protected final Fp c2;
    private final AbstractFp3Parameters Fp3Parameters;

    public Fp3(
            final Fp c0,
            final Fp c1,
            final Fp c2,
            final AbstractFp3Parameters Fp3Parameters) {
        this.c0 = c0;
        this.c1 = c1;
        this.c2 = c2;
        this.Fp3Parameters = Fp3Parameters;
    }

    public Fp3(
            final long c0,
            final long c1,
            final long c2,
            final AbstractFp3Parameters Fp3Parameters) {
        this.c0 = new Fp(c0, Fp3Parameters.FpParameters());
        this.c1 = new Fp(c1, Fp3Parameters.FpParameters());
        this.c2 = new Fp(c2, Fp3Parameters.FpParameters());
        this.Fp3Parameters = Fp3Parameters;
    }

    public Fp3 self() {
        return this;
    }

    public Fp3 add(final Fp3 that) {
        return new Fp3(c0.add(that.c0), c1.add(that.c1), c2.add(that.c2), Fp3Parameters);
    }

    public Fp3 sub(final Fp3 that) {
        return new Fp3(c0.sub(that.c0), c1.sub(that.c1), c2.sub(that.c2), Fp3Parameters);
    }

    public Fp3 mul(final Fp that) {
        return new Fp3(c0.mul(that), c1.mul(that), c2.mul(that), Fp3Parameters);
    }

    public Fp3 mul(final Fp3 that) {
    /* Devegili OhEig Scott Dahab --- Multiplication and Squaring on AbstractPairing-Friendly
     Fields.pdf; Section 4 (Karatsuba) */
        final Fp c0C0 = c0.mul(that.c0);
        final Fp c1C1 = c1.mul(that.c1);
        final Fp c2C2 = c2.mul(that.c2);
        final Fp c0Factor = c1.add(c2).mul(that.c1.add(that.c2)).sub(c1C1).sub(c2C2);
        final Fp c1Factor = c0.add(c1).mul(that.c0.add(that.c1)).sub(c0C0).sub(c1C1);
        final Fp c2Factor = c0.add(c2).mul(that.c0.add(that.c2)).sub(c0C0).add(c1C1).sub(c2C2);

        return new Fp3(
                c0C0.add(Fp3Parameters.nonresidue().mul(c0Factor)),
                c1Factor.add(Fp3Parameters.nonresidue().mul(c2C2)),
                c2Factor,
                Fp3Parameters);
    }

    public Fp3 zero() {
        return Fp3Parameters.ZERO();
    }

    public boolean isZero() {
        return c0.isZero() && c1.isZero() && c2.isZero();
    }

    public Fp3 one() {
        return Fp3Parameters.ONE();
    }

    public boolean isOne() {
        return c0.isOne() && c1.isZero() && c2.isZero();
    }

    public Fp3 random(final Long seed, final byte[] secureSeed) {
        return new Fp3(
                c0.random(seed, secureSeed),
                c1.random(seed, secureSeed),
                c2.random(seed, secureSeed),
                Fp3Parameters);
    }

    public Fp3 negate() {
        return new Fp3(c0.negate(), c1.negate(), c2.negate(), Fp3Parameters);
    }

    public Fp3 square() {
        /* Devegili OhEig Scott Dahab --- Multiplication and Squaring on AbstractPairing-Friendly
         Fields.pdf; Section 4 (CH-SQR2) */
        final Fp s0 = c0.square();
        final Fp c0c1 = c0.mul(c1);
        final Fp s1 = c0c1.add(c0c1);
        final Fp s2 = c0.sub(c1).add(c2).square();
        final Fp c1c2 = c1.mul(c2);
        final Fp s3 = c1c2.add(c1c2);
        final Fp s4 = c2.square();

        return new Fp3(
                s0.add(Fp3Parameters.nonresidue().mul(s3)),
                s1.add(Fp3Parameters.nonresidue().mul(s4)),
                s1.add(s2).add(s3).sub(s0).sub(s4),
                Fp3Parameters);
    }

    public Fp3 inverse() {
        /* From "High-Speed Software Implementation of the Optimal Ate AbstractPairing over
        Barreto-Naehrig Curves"; Algorithm 17 */
        final Fp t0 = c0.square();
        final Fp t1 = c1.square();
        final Fp t2 = c2.square();
        final Fp t3 = c0.mul(c1);
        final Fp t4 = c0.mul(c2);
        final Fp t5 = c1.mul(c2);
        final Fp s0 = t0.sub(Fp3Parameters.nonresidue().mul(t5));
        final Fp s1 = Fp3Parameters.nonresidue().mul(t2).sub(t3);
        final Fp s2 = t1
                .sub(t4); // typo in paper referenced above. should be "-" as per Scott, but is "*"
        final Fp t6 = c0.mul(s0).add(Fp3Parameters.nonresidue().mul(c2.mul(s1).add(c1.mul(s2))))
                .inverse();

        return new Fp3(t6.mul(s0), t6.mul(s1), t6.mul(s2), Fp3Parameters);
    }

    public Fp3 FrobeniusMap(long power) {
        return new Fp3(
                c0,
                Fp3Parameters.FrobeniusMapCoefficientsC1()[(int) power % 3].mul(c1),
                Fp3Parameters.FrobeniusMapCoefficientsC2()[(int) power % 3].mul(c2),
                Fp3Parameters);
    }

    public int bitSize() {
        return Math.max(c0.bitSize(), Math.max(c1.bitSize(), c2.bitSize()));
    }

    public Fp3 construct(final Fp c0, final Fp c1, final Fp c2) {
        return new Fp3(c0, c1, c2, Fp3Parameters);
    }

    public Fp3 construct(final long c0, final long c1, final long c2) {
        return new Fp3(c0, c1, c2, Fp3Parameters);
    }

    public String toString() {
        return c0.toString() + ", " + c1.toString() + ", " + c2.toString();
    }

    public boolean equals(final Fp3 that) {
        if (that == null) {
            return false;
        }

        return c0.equals(that.c0) && c1.equals(that.c1) && c2.equals(that.c2);
    }
}
