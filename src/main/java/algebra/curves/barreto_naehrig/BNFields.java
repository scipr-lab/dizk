package algebra.curves.barreto_naehrig;

import algebra.fields.*;

import java.math.BigInteger;

public interface BNFields {
    /* Scalar field Fr */
    public abstract class BNFr<BNFrT extends BNFr<BNFrT>>
            extends AbstractFieldElementExpanded<BNFrT> {

        public abstract Fp element();

        public abstract BNFrT zero();

        public abstract BNFrT one();

        public abstract BNFrT multiplicativeGenerator();

        public abstract BNFrT construct(final Fp element);

        public abstract String toString();

        public BNFrT add(final BNFrT other) {
            return this.construct(this.element().add(other.element()));
        }

        public BNFrT sub(final BNFrT other) {
            return this.construct(this.element().sub(other.element()));
        }

        public BNFrT mul(final BNFrT other) {
            return this.construct(this.element().mul(other.element()));
        }

        public boolean isZero() {
            return this.equals(this.zero());
        }

        public boolean isSpecial() {
            return this.isZero();
        }

        public boolean isOne() {
            return this.equals(this.one());
        }

        public BNFrT random(final Long seed, final byte[] secureSeed) {
            return this.construct(this.element().random(seed, secureSeed));
        }

        public BNFrT negate() {
            return this.construct(this.element().negate());
        }

        public BNFrT inverse() {
            return this.construct(this.element().inverse());
        }

        public BNFrT square() {
            return this.construct(this.element().square());
        }

        public BNFrT rootOfUnity(final long size) {
            return this.construct(this.element().rootOfUnity(size));
        }

        public int bitSize() {
            return this.element().bitSize();
        }

        public BigInteger toBigInteger() {
            return this.element().toBigInteger();
        }

        public boolean equals(final BNFrT other) {
            if (other == null) {
                return false;
            }

            return this.element().equals(other.element());
        }
    }

    /* Base field Fq */
    public abstract class BNFq<BNFqT extends BNFq<BNFqT>>
            extends AbstractFieldElementExpanded<BNFqT> {
        public abstract Fp element();

        public abstract BNFqT zero();

        public abstract BNFqT one();

        public abstract BNFqT multiplicativeGenerator();

        public abstract BNFqT construct(final long element);

        public abstract BNFqT construct(final Fp element);

        public abstract String toString();

        public BNFqT add(final BNFqT other) {
            return this.construct(this.element().add(other.element()));
        }

        public BNFqT sub(final BNFqT other) {
            return this.construct(this.element().sub(other.element()));
        }

        public BNFqT mul(final BNFqT other) {
            return this.construct(this.element().mul(other.element()));
        }

        public BNFqT mul(final Fp other) {
            return this.construct(this.element().mul(other));
        }

        public boolean isZero() {
            return this.equals(this.zero());
        }

        public boolean isSpecial() {
            return this.isZero();
        }

        public boolean isOne() {
            return this.equals(this.one());
        }

        public BNFqT random(final Long seed, final byte[] secureSeed) {
            return this.construct(this.element().random(seed, secureSeed));
        }

        public BNFqT negate() {
            return this.construct(this.element().negate());
        }

        public BNFqT inverse() {
            return this.construct(this.element().inverse());
        }

        public BNFqT square() {
            return this.construct(this.element().square());
        }

        public BNFqT rootOfUnity(final long size) {
            return this.construct(this.element().rootOfUnity(size));
        }

        public int bitSize() {
            return this.element().bitSize();
        }

        public BigInteger toBigInteger() {
            return this.element().toBigInteger();
        }

        public boolean equals(final BNFqT other) {
            if (other == null) {
                return false;
            }

            return this.element().equals(other.element());
        }
    }

    /* Twist field Fq2 */
    public abstract class BNFq2<BNFqT extends BNFq<BNFqT>, BNFq2T extends BNFq2<BNFqT, BNFq2T>>
            extends AbstractFieldElement<BNFq2T> {
        public abstract Fp2 element();

        public abstract BNFq2T zero();

        public abstract BNFq2T one();

        public abstract BNFq2T construct(final Fp2 element);

        public abstract String toString();

        public BNFq2T add(final BNFq2T other) {
            return this.construct(this.element().add(other.element()));
        }

        public BNFq2T sub(final BNFq2T other) {
            return this.construct(this.element().sub(other.element()));
        }

        public BNFq2T mul(final BNFq2T other) {
            return this.construct(this.element().mul(other.element()));
        }

        public BNFq2T mul(final BNFqT other) {
            return this.construct(this.element().mul(other.element()));
        }

        public boolean isZero() {
            return this.equals(this.zero());
        }

        public boolean isOne() {
            return this.equals(this.one());
        }

        public BNFq2T random(final Long seed, final byte[] secureSeed) {
            return this.construct(this.element().random(seed, secureSeed));
        }

        public BNFq2T negate() {
            return this.construct(this.element().negate());
        }

        public BNFq2T inverse() {
            return this.construct(this.element().inverse());
        }

        public BNFq2T square() {
            return this.construct(this.element().square());
        }

        public BNFq2T FrobeniusMap(long power) {
            return this.construct(this.element().FrobeniusMap(power));
        }

        public int bitSize() {
            return this.element().bitSize();
        }

        public boolean equals(final BNFq2T other) {
            if (other == null) {
                return false;
            }

            return this.element().equals(other.element());
        }
    }

    /* Field Fq6 */
    public abstract class BNFq6<
            BNFqT extends BNFq<BNFqT>,
            BNFq2T extends BNFq2<BNFqT, BNFq2T>,
            BNFq6T extends BNFq6<BNFqT, BNFq2T, BNFq6T>>
            extends AbstractFieldElement<BNFq6T> {
        public abstract Fp6_3Over2 element();

        public abstract BNFq6T zero();

        public abstract BNFq6T one();

        public abstract Fp2 mulByNonResidue(final Fp2 other);

        public abstract BNFq6T construct(final Fp6_3Over2 element);

        public abstract String toString();

        public BNFq6T add(final BNFq6T other) {
            return this.construct(this.element().add(other.element()));
        }

        public BNFq6T sub(final BNFq6T other) {
            return this.construct(this.element().sub(other.element()));
        }

        public BNFq6T mul(final BNFqT other) {
            return this.construct(this.element().mul(other.element()));
        }

        public BNFq6T mul(final BNFq2T other) {
            return this.construct(this.element().mul(other.element()));
        }

        public BNFq6T mul(final BNFq6T other) {
            return this.construct(this.element().mul(other.element()));
        }

        public boolean isZero() {
            return this.equals(this.zero());
        }

        public boolean isOne() {
            return this.equals(this.one());
        }

        public BNFq6T random(final Long seed, final byte[] secureSeed) {
            return this.construct(this.element().random(seed, secureSeed));
        }

        public BNFq6T negate() {
            return this.construct(this.element().negate());
        }

        public BNFq6T square() {
            return this.construct(this.element().square());
        }

        public BNFq6T inverse() {
            return this.construct(this.element().inverse());
        }

        public BNFq6T FrobeniusMap(long power) {
            return this.construct(this.element().FrobeniusMap(power));
        }

        public int bitSize() {
            return this.element().bitSize();
        }

        public boolean equals(final BNFq6T other) {
            if (other == null) {
                return false;
            }

            return this.element().equals(other.element());
        }
    }

    /* Field Fq12 */
    public abstract class BNFq12<
            BNFqT extends BNFq<BNFqT>,
            BNFq2T extends BNFq2<BNFqT, BNFq2T>,
            BNFq6T extends BNFq6<BNFqT, BNFq2T, BNFq6T>,
            BNFq12T extends BNFq12<BNFqT, BNFq2T, BNFq6T, BNFq12T>>
            extends AbstractFieldElement<BNFq12T> {

        public abstract Fp12_2Over3Over2 element();

        public abstract BNFq12T zero();

        public abstract BNFq12T one();

        public abstract BNFq12T construct(final Fp12_2Over3Over2 element);

        public abstract String toString();

        public BNFq12T add(final BNFq12T other) {
            return this.construct(this.element().add(other.element()));
        }

        public BNFq12T sub(final BNFq12T other) {
            return this.construct(this.element().sub(other.element()));
        }

        public BNFq12T mul(final BNFqT other) {
            return this.construct(this.element().mul(other.element()));
        }

        public BNFq12T mul(final BNFq2T other) {
            return this.construct(this.element().mul(other.element()));
        }

        public BNFq12T mul(final BNFq6T other) {
            return this.construct(this.element().mul(other.element()));
        }

//        public Fp6_3Over2 mulByNonResidue(final Fp6_3Over2 other) {
//            return other.construct(Fq12Parameters.nonresidue().mul(other.c2), other.c0, other.c1);
//        }

        public BNFq12T mul(final BNFq12T other) {
            return this.construct(this.element().mul(other.element()));
        }

        public BNFq12T pow(final BigInteger other) {
            return this.construct(this.element().pow(other));
        }

        public boolean isZero() {
            return this.equals(this.zero());
        }

        public boolean isOne() {
            return this.equals(this.one());
        }

        public BNFq12T random(final Long seed, final byte[] secureSeed) {
            return this.construct(this.element().random(seed, secureSeed));
        }

        public BNFq12T negate() {
            return this.construct(this.element().negate());
        }

        public BNFq12T square() {
            return this.construct(this.element().square());
        }

        public BNFq12T inverse() {
            return this.construct(this.element().inverse());
        }

        public BNFq12T FrobeniusMap(long power) {
            return this.construct(this.element().FrobeniusMap(power));
        }

        public BNFq12T unitaryInverse() {
            return this.construct(this.element().unitaryInverse());
        }

        public BNFq12T cyclotomicSquared() {
            return this.construct(this.element().cyclotomicSquared());
        }

        public BNFq12T mulBy024(
                final BNFq2T ell0,
                final BNFq2T ellVW,
                final BNFq2T ellVV) {
            return this.construct(
                    this.element().mulBy024(ell0.element(), ellVW.element(), ellVV.element()));
        }

        public BNFq12T cyclotomicExponentiation(final BigInteger exponent) {
            return this.construct(this.element().cyclotomicExponentiation(exponent));
        }

        public int bitSize() {
            return this.element().bitSize();
        }

        public boolean equals(final BNFq12T other) {
            if (other == null) {
                return false;
            }

            return this.element().equals(other.element());
        }
    }
}
