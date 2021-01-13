package algebra.curves.barreto_lynn_scott.bls12_377;

import algebra.curves.barreto_lynn_scott.BLSFields.*;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.*;
import algebra.fields.Fp;
import algebra.fields.Fp12_2Over3Over2;
import algebra.fields.Fp2;
import algebra.fields.Fp6_3Over2;
import java.math.BigInteger;

public class BLS12_377Fields {
  /* Scalar field Fr */
  public static class BLS12_377Fr extends BLSFr<BLS12_377Fr> {
    public static final BLS12_377FrParameters FrParameters = new BLS12_377FrParameters();
    public static final BLS12_377Fr ZERO = new BLS12_377Fr(FrParameters.ZERO());
    public static final BLS12_377Fr ONE = new BLS12_377Fr(FrParameters.ONE());
    public static final BLS12_377Fr MULTIPLICATIVE_GENERATOR =
        new BLS12_377Fr(FrParameters.multiplicativeGenerator());
    public Fp element;

    public BLS12_377Fr(final BigInteger number) {
      this.element = new Fp(number, FrParameters);
    }

    public BLS12_377Fr(final Fp number) {
      this(number.toBigInteger());
    }

    public BLS12_377Fr(final String number) {
      this(new BigInteger(number));
    }

    public BLS12_377Fr(final long number) {
      this(BigInteger.valueOf(number));
    }

    public BLS12_377Fr self() {
      return this;
    }

    public Fp element() {
      return element;
    }

    public BLS12_377Fr zero() {
      return ZERO;
    }

    public BLS12_377Fr one() {
      return ONE;
    }

    public BLS12_377Fr multiplicativeGenerator() {
      return MULTIPLICATIVE_GENERATOR;
    }

    public BLS12_377Fr construct(final BigInteger number) {
      return new BLS12_377Fr(number);
    }

    public BLS12_377Fr construct(final long number) {
      return new BLS12_377Fr(number);
    }

    public BLS12_377Fr construct(final Fp element) {
      return new BLS12_377Fr(element);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Base field Fq */
  public static class BLS12_377Fq extends BLSFq<BLS12_377Fq> {
    public static final BLS12_377FqParameters FqParameters = new BLS12_377FqParameters();
    public static final BLS12_377Fq ZERO = new BLS12_377Fq(FqParameters.ZERO());
    public static final BLS12_377Fq ONE = new BLS12_377Fq(FqParameters.ONE());
    public static final BLS12_377Fq MULTIPLICATIVE_GENERATOR =
        new BLS12_377Fq(FqParameters.multiplicativeGenerator());

    public Fp element;

    public BLS12_377Fq(final Fp element) {
      this.element = element;
    }

    public BLS12_377Fq(final BigInteger number) {
      this.element = new Fp(number, FqParameters);
    }

    public BLS12_377Fq(final String number) {
      this(new BigInteger(number));
    }

    public BLS12_377Fq(final long number) {
      this(BigInteger.valueOf(number));
    }

    public BLS12_377Fq self() {
      return this;
    }

    public Fp element() {
      return element;
    }

    public BLS12_377Fq zero() {
      return ZERO;
    }

    public BLS12_377Fq one() {
      return ONE;
    }

    public BLS12_377Fq multiplicativeGenerator() {
      return MULTIPLICATIVE_GENERATOR;
    }

    public BLS12_377Fq construct(final BigInteger number) {
      return new BLS12_377Fq(number);
    }

    public BLS12_377Fq construct(final Fp element) {
      return new BLS12_377Fq(element);
    }

    public BLS12_377Fq construct(final String element) {
      return new BLS12_377Fq(element);
    }

    public BLS12_377Fq construct(final long number) {
      return new BLS12_377Fq(number);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Twist field Fq2 */
  public static class BLS12_377Fq2 extends BLSFq2<BLS12_377Fq, BLS12_377Fq2> {
    public static final BLS12_377Fq2Parameters Fq2Parameters = new BLS12_377Fq2Parameters();
    public static BLS12_377Fq2 ZERO = new BLS12_377Fq2(Fq2Parameters.ZERO());
    public static BLS12_377Fq2 ONE = new BLS12_377Fq2(Fq2Parameters.ONE());

    public Fp2 element;

    public BLS12_377Fq2(final Fp2 element) {
      this.element = element;
    }

    public BLS12_377Fq2(final BigInteger c0, final BigInteger c1) {
      this.element = new Fp2(c0, c1, Fq2Parameters);
    }

    public BLS12_377Fq2(final BLS12_377Fq c0, final BLS12_377Fq c1) {
      this(c0.toBigInteger(), c1.toBigInteger());
    }

    public BLS12_377Fq2(final long c0, final long c1) {
      this(BigInteger.valueOf(c0), BigInteger.valueOf(c1));
    }

    public BLS12_377Fq2 self() {
      return this;
    }

    public Fp2 element() {
      return this.element;
    }

    public BLS12_377Fq2 zero() {
      return ZERO;
    }

    public BLS12_377Fq2 one() {
      return ONE;
    }

    public BLS12_377Fq2 construct(final Fp2 element) {
      return new BLS12_377Fq2(element);
    }

    public BLS12_377Fq2 construct(final BLS12_377Fq c0, final BLS12_377Fq c1) {
      return new BLS12_377Fq2(c0, c1);
    }

    public BLS12_377Fq2 construct(final long c0, final long c1) {
      return new BLS12_377Fq2(c0, c1);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Field Fq6 */
  public static class BLS12_377Fq6 extends BLSFq6<BLS12_377Fq, BLS12_377Fq2, BLS12_377Fq6> {
    public static final BLS12_377Fq6Parameters Fq6Parameters = new BLS12_377Fq6Parameters();
    public static BLS12_377Fq6 ZERO = new BLS12_377Fq6(Fq6Parameters.ZERO());
    public static BLS12_377Fq6 ONE = new BLS12_377Fq6(Fq6Parameters.ONE());

    public Fp6_3Over2 element;

    public BLS12_377Fq6(final Fp6_3Over2 element) {
      this.element = element;
    }

    public BLS12_377Fq6(final BLS12_377Fq2 c0, final BLS12_377Fq2 c1, final BLS12_377Fq2 c2) {
      this.element = new Fp6_3Over2(c0.element, c1.element, c2.element, Fq6Parameters);
    }

    public BLS12_377Fq6 self() {
      return this;
    }

    public Fp6_3Over2 element() {
      return this.element;
    }

    public BLS12_377Fq6 zero() {
      return ZERO;
    }

    public BLS12_377Fq6 one() {
      return ONE;
    }

    public Fp2 mulByNonResidue(final Fp2 other) {
      return Fq6Parameters.nonresidue().mul(other);
    }

    public BLS12_377Fq6 construct(final Fp6_3Over2 element) {
      return new BLS12_377Fq6(element);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Field Fq12 */
  public static class BLS12_377Fq12
      extends BLSFq12<BLS12_377Fq, BLS12_377Fq2, BLS12_377Fq6, BLS12_377Fq12> {
    public static final BLS12_377Fq12Parameters Fq12Parameters = new BLS12_377Fq12Parameters();
    public static BLS12_377Fq12 ZERO = new BLS12_377Fq12(Fq12Parameters.ZERO());
    public static BLS12_377Fq12 ONE = new BLS12_377Fq12(Fq12Parameters.ONE());

    public Fp12_2Over3Over2 element;

    public BLS12_377Fq12(final Fp12_2Over3Over2 element) {
      this.element = element;
    }

    public BLS12_377Fq12(final BLS12_377Fq6 c0, final BLS12_377Fq6 c1) {
      this.element = new Fp12_2Over3Over2(c0.element, c1.element, Fq12Parameters);
    }

    public BLS12_377Fq12 self() {
      return this;
    }

    public Fp12_2Over3Over2 element() {
      return this.element;
    }

    public BLS12_377Fq12 zero() {
      return ZERO;
    }

    public BLS12_377Fq12 one() {
      return ONE;
    }

    public BLS12_377Fq12 construct(final Fp12_2Over3Over2 element) {
      return new BLS12_377Fq12(element);
    }

    public String toString() {
      return this.element.toString();
    }
  }
} // BLS12_377Fields
