/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields;

import algebra.fields.abstractfieldparameters.AbstractFp12_2Over3Over2_Parameters;
import algebra.fields.abstractfieldparameters.AbstractFp6_3Over2_Parameters;

import java.math.BigInteger;

public class Fp12_2Over3Over2 extends AbstractFieldElement<Fp12_2Over3Over2> {
    public final Fp6_3Over2 c0;
    public final Fp6_3Over2 c1;
    private final AbstractFp12_2Over3Over2_Parameters Fp12Parameters;

    public Fp12_2Over3Over2(
            final Fp6_3Over2 c0,
            final Fp6_3Over2 c1,
            final AbstractFp12_2Over3Over2_Parameters Fp12Parameters) {
        this.c0 = c0;
        this.c1 = c1;
        this.Fp12Parameters = Fp12Parameters;
    }

    public Fp12_2Over3Over2 self() {
        return this;
    }

    public Fp12_2Over3Over2 add(final Fp12_2Over3Over2 that) {
        return new Fp12_2Over3Over2(c0.add(that.c0), c1.add(that.c1), Fp12Parameters);
    }

    public Fp12_2Over3Over2 sub(final Fp12_2Over3Over2 that) {
        return new Fp12_2Over3Over2(c0.sub(that.c0), c1.sub(that.c1), Fp12Parameters);
    }

    public Fp12_2Over3Over2 mul(final Fp that) {
        return new Fp12_2Over3Over2(c0.mul(that), c1.mul(that), Fp12Parameters);
    }

    public Fp12_2Over3Over2 mul(final Fp2 that) {
        return new Fp12_2Over3Over2(c0.mul(that), c1.mul(that), Fp12Parameters);
    }

    public Fp12_2Over3Over2 mul(final Fp6_3Over2 that) {
        return new Fp12_2Over3Over2(c0.mul(that), c1.mul(that), Fp12Parameters);
    }

    public Fp6_3Over2 mulByNonResidue(final Fp6_3Over2 that) {
        return that.construct(Fp12Parameters.nonresidue().mul(that.c2), that.c0, that.c1);
    }

    public Fp12_2Over3Over2 mul(final Fp12_2Over3Over2 that) {
    /* Devegili OhEig Scott Dahab --- Multiplication and Squaring on AbstractPairing-Friendly
     Fields.pdf; Section 3 (Karatsuba) */
        final Fp6_3Over2 c0C0 = c0.mul(that.c0);
        final Fp6_3Over2 c1C1 = c1.mul(that.c1);
        return new Fp12_2Over3Over2(
                c0C0.add(mulByNonResidue(c1C1)),
                (c0.add(c1)).mul(that.c0.add(that.c1)).sub(c0C0).sub(c1C1),
                Fp12Parameters);
    }

    public Fp12_2Over3Over2 zero() {
        return Fp12Parameters.ZERO();
    }

    public boolean isZero() {
        return c0.isZero() && c1.isZero();
    }

    public Fp12_2Over3Over2 one() {
        return Fp12Parameters.ONE();
    }

    public boolean isOne() {
        return c0.isOne() && c1.isZero();
    }

    public Fp12_2Over3Over2 random(final Long seed, final byte[] secureSeed) {
        return new Fp12_2Over3Over2(
                c0.random(seed, secureSeed),
                c1.random(seed, secureSeed),
                Fp12Parameters);
    }

    public Fp12_2Over3Over2 negate() {
        return new Fp12_2Over3Over2(c0.negate(), c1.negate(), Fp12Parameters);
    }

    public Fp12_2Over3Over2 square() {
    /* Devegili OhEig Scott Dahab --- Multiplication and Squaring on AbstractPairing-Friendly
     Fields.pdf; Section 3 (Complex squaring) */
        final Fp6_3Over2 c0c1 = c0.mul(c1);
        final Fp6_3Over2 factor = (c0.add(c1)).mul(c0.add(mulByNonResidue(c1)));
        return new Fp12_2Over3Over2(
                factor.sub(c0c1).sub(mulByNonResidue(c0c1)),
                c0c1.add(c0c1),
                Fp12Parameters);
    }

    public Fp12_2Over3Over2 inverse() {
    /* From "High-Speed Software Implementation of the Optimal Ate AbstractPairing over
    Barreto-Naehrig Curves"; Algorithm 8 */
        final Fp6_3Over2 t0 = c0.square();
        final Fp6_3Over2 t1 = c1.square();
        final Fp6_3Over2 t2 = t0.sub(mulByNonResidue(t1));
        final Fp6_3Over2 t3 = t2.inverse();

        return new Fp12_2Over3Over2(c0.mul(t3), c1.mul(t3).negate(), Fp12Parameters);
    }

    public Fp12_2Over3Over2 FrobeniusMap(long power) {
        return new Fp12_2Over3Over2(
                c0.FrobeniusMap(power),
                c1.FrobeniusMap(power).mul(Fp12Parameters.FrobeniusMapCoefficientsC1()[(int) (power % 12)]),
                Fp12Parameters);
    }

    public Fp12_2Over3Over2 unitaryInverse() {
        return new Fp12_2Over3Over2(c0, c1.negate(), Fp12Parameters);
    }

    public Fp12_2Over3Over2 cyclotomicSquared() {
        final AbstractFp6_3Over2_Parameters Fp6Parameters = Fp12Parameters.Fp6Parameters();

        Fp2 z0 = c0.c0;
        Fp2 z4 = c0.c1;
        Fp2 z3 = c0.c2;
        Fp2 z2 = c1.c0;
        Fp2 z1 = c1.c1;
        Fp2 z5 = c1.c2;

        Fp2 t0, t1, t2, t3, t4, t5, tmp;

        // t0 + t1*y = (z0 + z1*y)^2 = a^2
        tmp = z0.mul(z1);
        t0 = (z0.add(z1)).mul(z0.add(Fp6Parameters.nonresidue().mul(z1))).sub(tmp)
                .sub(Fp6Parameters.nonresidue().mul(tmp));
        t1 = tmp.add(tmp);
        // t2 + t3*y = (z2 + z3*y)^2 = b^2
        tmp = z2.mul(z3);
        t2 = (z2.add(z3)).mul(z2.add(Fp6Parameters.nonresidue().mul(z3))).sub(tmp)
                .sub(Fp6Parameters.nonresidue().mul(tmp));
        t3 = tmp.add(tmp);
        // t4 + t5*y = (z4 + z5*y)^2 = c^2
        tmp = z4.mul(z5);
        t4 = (z4.add(z5)).mul(z4.add(Fp6Parameters.nonresidue().mul(z5))).sub(tmp)
                .sub(Fp6Parameters.nonresidue().mul(tmp));
        t5 = tmp.add(tmp);

        // for A

        // z0 = 3 * t0 - 2 * z0
        z0 = t0.sub(z0);
        z0 = z0.add(z0);
        z0 = z0.add(t0);
        // z1 = 3 * t1 + 2 * z1
        z1 = t1.add(z1);
        z1 = z1.add(z1);
        z1 = z1.add(t1);

        // for B

        // z2 = 3 * (xi * t5) + 2 * z2
        tmp = Fp6Parameters.nonresidue().mul(t5);
        z2 = tmp.add(z2);
        z2 = z2.add(z2);
        z2 = z2.add(tmp);

        // z3 = 3 * t4 - 2 * z3
        z3 = t4.sub(z3);
        z3 = z3.add(z3);
        z3 = z3.add(t4);

        // for C

        // z4 = 3 * t2 - 2 * z4
        z4 = t2.sub(z4);
        z4 = z4.add(z4);
        z4 = z4.add(t2);

        // z5 = 3 * t3 + 2 * z5
        z5 = t3.add(z5);
        z5 = z5.add(z5);
        z5 = z5.add(t3);

        return new Fp12_2Over3Over2(
                new Fp6_3Over2(z0, z4, z3, Fp6Parameters),
                new Fp6_3Over2(z2, z1, z5, Fp6Parameters),
                Fp12Parameters);
    }

    public Fp12_2Over3Over2 mulBy024(final Fp2 ell0, final Fp2 ellVW, final Fp2 ellVV) {
        final AbstractFp6_3Over2_Parameters Fp6Parameters = Fp12Parameters.Fp6Parameters();

        Fp2 z0 = c0.c0;
        Fp2 z1 = c0.c1;
        Fp2 z2 = c0.c2;
        Fp2 z3 = c1.c0;
        Fp2 z4 = c1.c1;
        Fp2 z5 = c1.c2;

        Fp2 x0 = ell0;
        Fp2 x2 = ellVV;
        Fp2 x4 = ellVW;

        Fp2 t0, t1, t2, s0, T3, T4, D0, D2, D4, S1;

        D0 = z0.mul(x0);
        D2 = z2.mul(x2);
        D4 = z4.mul(x4);
        t2 = z0.add(z4);
        t1 = z0.add(z2);
        s0 = z1.add(z3).add(z5);

        // For z.a_.a_ = z0.
        S1 = z1.mul(x2);
        T3 = S1.add(D4);
        T4 = Fp6Parameters.nonresidue().mul(T3).add(D0);
        z0 = T4;

        // For z.a_.b_ = z1
        T3 = z5.mul(x4);
        S1 = S1.add(T3);
        T3 = T3.add(D2);
        T4 = Fp6Parameters.nonresidue().mul(T3);
        T3 = z1.mul(x0);
        S1 = S1.add(T3);
        T4 = T4.add(T3);
        z1 = T4;

        // For z.a_.c_ = z2
        t0 = x0.add(x2);
        T3 = t1.mul(t0).sub(D0).sub(D2);
        T4 = z3.mul(x4);
        S1 = S1.add(T4);
        T3 = T3.add(T4);

        // For z.b_.a_ = z3 (z3 needs z2)
        t0 = z2.add(z4);
        z2 = T3;
        t1 = x2.add(x4);
        T3 = t0.mul(t1).sub(D2).sub(D4);
        T4 = Fp6Parameters.nonresidue().mul(T3);
        T3 = z3.mul(x0);
        S1 = S1.add(T3);
        T4 = T4.add(T3);
        z3 = T4;

        // For z.b_.b_ = z4
        T3 = z5.mul(x2);
        S1 = S1.add(T3);
        T4 = Fp6Parameters.nonresidue().mul(T3);
        t0 = x0.add(x4);
        T3 = t2.mul(t0).sub(D0).sub(D4);
        T4 = T4.add(T3);
        z4 = T4;

        // For z.b_.c_ = z5.
        t0 = x0.add(x2).add(x4);
        T3 = s0.mul(t0).sub(S1);
        z5 = T3;

        return new Fp12_2Over3Over2(
                new Fp6_3Over2(z0, z1, z2, Fp6Parameters),
                new Fp6_3Over2(z3, z4, z5, Fp6Parameters),
                Fp12Parameters);
    }

    public Fp12_2Over3Over2 cyclotomicExponentiation(final BigInteger exponent) {
        Fp12_2Over3Over2 res = one();

        boolean found = false;
        for (int i = exponent.bitLength() - 1; i >= 0; --i) {
            if (found) {
                res = res.cyclotomicSquared();
            }

            if (exponent.testBit(i)) {
                found = true;
                res = res.mul(this);
            }
        }

        return res;
    }

    public int bitSize() {
        return Math.max(c0.bitSize(), c1.bitSize());
    }

    public String toString() {
        return c0.toString() + " / " + c1.toString();
    }

    public boolean equals(final Fp12_2Over3Over2 that) {
        if (that == null) {
            return false;
        }

        return c0.equals(that.c0) && c1.equals(that.c1);
    }
}
