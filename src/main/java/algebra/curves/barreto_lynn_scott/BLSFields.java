package algebra.curves.barreto_lynn_scott;

import algebra.fields.*;
import java.math.BigInteger;

public interface BLSFields {
  /* Scalar field Fr */
  public abstract class BLSFr<BLSFrT extends BLSFr<BLSFrT>>
      extends AbstractFieldElementExpanded<BLSFrT> {

    public abstract Fp element();

    public abstract BLSFrT zero();

    public abstract BLSFrT one();

    public abstract BLSFrT multiplicativeGenerator();

    public abstract BLSFrT construct(final Fp element);

    public abstract String toString();

    public BLSFrT add(final BLSFrT other) {
      return this.construct(this.element().add(other.element()));
    }

    public BLSFrT sub(final BLSFrT other) {
      return this.construct(this.element().sub(other.element()));
    }

    public BLSFrT mul(final BLSFrT other) {
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

    public BLSFrT random(final Long seed, final byte[] secureSeed) {
      return this.construct(this.element().random(seed, secureSeed));
    }

    public BLSFrT negate() {
      return this.construct(this.element().negate());
    }

    public BLSFrT inverse() {
      return this.construct(this.element().inverse());
    }

    public BLSFrT square() {
      return this.construct(this.element().square());
    }

    public BLSFrT rootOfUnity(final long size) {
      return this.construct(this.element().rootOfUnity(size));
    }

    public int bitSize() {
      return this.element().bitSize();
    }

    public BigInteger toBigInteger() {
      return this.element().toBigInteger();
    }

    public boolean equals(final BLSFrT other) {
      if (other == null) {
        return false;
      }

      return this.element().equals(other.element());
    }
  }

  /* Base field Fq */
  public abstract class BLSFq<BLSFqT extends BLSFq<BLSFqT>>
      extends AbstractFieldElementExpanded<BLSFqT> {
    public abstract Fp element();

    public abstract BLSFqT zero();

    public abstract BLSFqT one();

    public abstract BLSFqT multiplicativeGenerator();

    public abstract BLSFqT construct(final long element);

    public abstract BLSFqT construct(final Fp element);

    public abstract String toString();

    public BLSFqT add(final BLSFqT other) {
      return this.construct(this.element().add(other.element()));
    }

    public BLSFqT sub(final BLSFqT other) {
      return this.construct(this.element().sub(other.element()));
    }

    public BLSFqT mul(final BLSFqT other) {
      return this.construct(this.element().mul(other.element()));
    }

    public BLSFqT mul(final Fp other) {
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

    public BLSFqT random(final Long seed, final byte[] secureSeed) {
      return this.construct(this.element().random(seed, secureSeed));
    }

    public BLSFqT negate() {
      return this.construct(this.element().negate());
    }

    public BLSFqT inverse() {
      return this.construct(this.element().inverse());
    }

    public BLSFqT square() {
      return this.construct(this.element().square());
    }

    public BLSFqT rootOfUnity(final long size) {
      return this.construct(this.element().rootOfUnity(size));
    }

    public int bitSize() {
      return this.element().bitSize();
    }

    public BigInteger toBigInteger() {
      return this.element().toBigInteger();
    }

    public boolean equals(final BLSFqT other) {
      if (other == null) {
        return false;
      }

      return this.element().equals(other.element());
    }
  }

  /* Twist field Fq2 */
  public abstract class BLSFq2<
          BLSFqT extends BLSFq<BLSFqT>, BLSFq2T extends BLSFq2<BLSFqT, BLSFq2T>>
      extends AbstractFieldElement<BLSFq2T> {
    public abstract Fp2 element();

    public abstract BLSFq2T zero();

    public abstract BLSFq2T one();

    public abstract BLSFq2T construct(final Fp2 element);

    public abstract String toString();

    public BLSFq2T add(final BLSFq2T other) {
      return this.construct(this.element().add(other.element()));
    }

    public BLSFq2T sub(final BLSFq2T other) {
      return this.construct(this.element().sub(other.element()));
    }

    public BLSFq2T mul(final BLSFq2T other) {
      return this.construct(this.element().mul(other.element()));
    }

    public BLSFq2T mul(final BLSFqT other) {
      return this.construct(this.element().mul(other.element()));
    }

    public boolean isZero() {
      return this.equals(this.zero());
    }

    public boolean isOne() {
      return this.equals(this.one());
    }

    public BLSFq2T random(final Long seed, final byte[] secureSeed) {
      return this.construct(this.element().random(seed, secureSeed));
    }

    public BLSFq2T negate() {
      return this.construct(this.element().negate());
    }

    public BLSFq2T inverse() {
      return this.construct(this.element().inverse());
    }

    public BLSFq2T square() {
      return this.construct(this.element().square());
    }

    public BLSFq2T FrobeniusMap(long power) {
      return this.construct(this.element().FrobeniusMap(power));
    }

    public int bitSize() {
      return this.element().bitSize();
    }

    public boolean equals(final BLSFq2T other) {
      if (other == null) {
        return false;
      }

      return this.element().equals(other.element());
    }
  }

  /* Field Fq6 */
  public abstract class BLSFq6<
          BLSFqT extends BLSFq<BLSFqT>,
          BLSFq2T extends BLSFq2<BLSFqT, BLSFq2T>,
          BLSFq6T extends BLSFq6<BLSFqT, BLSFq2T, BLSFq6T>>
      extends AbstractFieldElement<BLSFq6T> {
    public abstract Fp6_3Over2 element();

    public abstract BLSFq6T zero();

    public abstract BLSFq6T one();

    public abstract Fp2 mulByNonResidue(final Fp2 other);

    public abstract BLSFq6T construct(final Fp6_3Over2 element);

    public abstract String toString();

    public BLSFq6T add(final BLSFq6T other) {
      return this.construct(this.element().add(other.element()));
    }

    public BLSFq6T sub(final BLSFq6T other) {
      return this.construct(this.element().sub(other.element()));
    }

    public BLSFq6T mul(final BLSFqT other) {
      return this.construct(this.element().mul(other.element()));
    }

    public BLSFq6T mul(final BLSFq2T other) {
      return this.construct(this.element().mul(other.element()));
    }

    public BLSFq6T mul(final BLSFq6T other) {
      return this.construct(this.element().mul(other.element()));
    }

    public boolean isZero() {
      return this.equals(this.zero());
    }

    public boolean isOne() {
      return this.equals(this.one());
    }

    public BLSFq6T random(final Long seed, final byte[] secureSeed) {
      return this.construct(this.element().random(seed, secureSeed));
    }

    public BLSFq6T negate() {
      return this.construct(this.element().negate());
    }

    public BLSFq6T square() {
      return this.construct(this.element().square());
    }

    public BLSFq6T inverse() {
      return this.construct(this.element().inverse());
    }

    public BLSFq6T FrobeniusMap(long power) {
      return this.construct(this.element().FrobeniusMap(power));
    }

    public int bitSize() {
      return this.element().bitSize();
    }

    public boolean equals(final BLSFq6T other) {
      if (other == null) {
        return false;
      }

      return this.element().equals(other.element());
    }
  }

  /* Field Fq12 */
  public abstract class BLSFq12<
          BLSFqT extends BLSFq<BLSFqT>,
          BLSFq2T extends BLSFq2<BLSFqT, BLSFq2T>,
          BLSFq6T extends BLSFq6<BLSFqT, BLSFq2T, BLSFq6T>,
          BLSFq12T extends BLSFq12<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T>>
      extends AbstractFieldElement<BLSFq12T> {

    public abstract Fp12_2Over3Over2 element();

    public abstract BLSFq12T zero();

    public abstract BLSFq12T one();

    public abstract BLSFq12T construct(final Fp12_2Over3Over2 element);

    public abstract String toString();

    public BLSFq12T add(final BLSFq12T other) {
      return this.construct(this.element().add(other.element()));
    }

    public BLSFq12T sub(final BLSFq12T other) {
      return this.construct(this.element().sub(other.element()));
    }

    public BLSFq12T mul(final BLSFqT other) {
      return this.construct(this.element().mul(other.element()));
    }

    public BLSFq12T mul(final BLSFq2T other) {
      return this.construct(this.element().mul(other.element()));
    }

    public BLSFq12T mul(final BLSFq6T other) {
      return this.construct(this.element().mul(other.element()));
    }

    //        public Fp6_3Over2 mulByNonResidue(final Fp6_3Over2 other) {
    //            return other.construct(Fq12Parameters.nonresidue().mul(other.c2), other.c0,
    // other.c1);
    //        }

    public BLSFq12T mul(final BLSFq12T other) {
      return this.construct(this.element().mul(other.element()));
    }

    public BLSFq12T pow(final BigInteger other) {
      return this.construct(this.element().pow(other));
    }

    public boolean isZero() {
      return this.equals(this.zero());
    }

    public boolean isOne() {
      return this.equals(this.one());
    }

    public BLSFq12T random(final Long seed, final byte[] secureSeed) {
      return this.construct(this.element().random(seed, secureSeed));
    }

    public BLSFq12T negate() {
      return this.construct(this.element().negate());
    }

    public BLSFq12T square() {
      return this.construct(this.element().square());
    }

    public BLSFq12T inverse() {
      return this.construct(this.element().inverse());
    }

    public BLSFq12T FrobeniusMap(long power) {
      return this.construct(this.element().FrobeniusMap(power));
    }

    public BLSFq12T unitaryInverse() {
      return this.construct(this.element().unitaryInverse());
    }

    public BLSFq12T cyclotomicSquared() {
      return this.construct(this.element().cyclotomicSquared());
    }

    public BLSFq12T mulBy024(final BLSFq2T ell0, final BLSFq2T ellVW, final BLSFq2T ellVV) {
      return this.construct(
          this.element().mulBy024(ell0.element(), ellVW.element(), ellVV.element()));
    }

    public BLSFq12T cyclotomicExponentiation(final BigInteger exponent) {
      return this.construct(this.element().cyclotomicExponentiation(exponent));
    }

    public int bitSize() {
      return this.element().bitSize();
    }

    public boolean equals(final BLSFq12T other) {
      if (other == null) {
        return false;
      }

      return this.element().equals(other.element());
    }
  }
} // BLSFields
