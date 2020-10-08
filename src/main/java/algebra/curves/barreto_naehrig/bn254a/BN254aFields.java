package algebra.curves.barreto_naehrig.bn254a;

import algebra.curves.barreto_naehrig.BNFields.*;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.*;
import algebra.fields.Fp;
import algebra.fields.Fp12_2Over3Over2;
import algebra.fields.Fp2;
import algebra.fields.Fp6_3Over2;
import java.math.BigInteger;

public class BN254aFields {
  /* Scalar field Fr */
  public static class BN254aFr extends BNFr<BN254aFr> {

    public static final BN254aFrParameters FrParameters = new BN254aFrParameters();
    public static final BN254aFr ZERO = new BN254aFr(FrParameters.ZERO());
    public static final BN254aFr ONE = new BN254aFr(FrParameters.ONE());
    public static final BN254aFr MULTIPLICATIVE_GENERATOR =
        new BN254aFr(FrParameters.multiplicativeGenerator());

    public Fp element;

    public BN254aFr(final BigInteger number) {
      this.element = new Fp(number, FrParameters);
    }

    public BN254aFr(final Fp number) {
      this(number.toBigInteger());
    }

    public BN254aFr(final String number) {
      this(new BigInteger(number));
    }

    public BN254aFr(final long number) {
      this(BigInteger.valueOf(number));
    }

    public BN254aFr self() {
      return this;
    }

    public Fp element() {
      return element;
    }

    public BN254aFr zero() {
      return ZERO;
    }

    public BN254aFr one() {
      return ONE;
    }

    public BN254aFr multiplicativeGenerator() {
      return MULTIPLICATIVE_GENERATOR;
    }

    public BN254aFr construct(final BigInteger number) {
      return new BN254aFr(number);
    }

    public BN254aFr construct(final long number) {
      return new BN254aFr(number);
    }

    public BN254aFr construct(final Fp element) {
      return new BN254aFr(element);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Base field Fq */
  public static class BN254aFq extends BNFq<BN254aFq> {

    public static final BN254aFqParameters FqParameters = new BN254aFqParameters();
    public static final BN254aFq ZERO = new BN254aFq(FqParameters.ZERO());
    public static final BN254aFq ONE = new BN254aFq(FqParameters.ONE());
    public static final BN254aFq MULTIPLICATIVE_GENERATOR =
        new BN254aFq(FqParameters.multiplicativeGenerator());

    public Fp element;

    public BN254aFq(final Fp element) {
      this.element = element;
    }

    public BN254aFq(final BigInteger number) {
      this.element = new Fp(number, FqParameters);
    }

    public BN254aFq(final String number) {
      this(new BigInteger(number));
    }

    public BN254aFq(final long number) {
      this(BigInteger.valueOf(number));
    }

    public BN254aFq self() {
      return this;
    }

    public Fp element() {
      return element;
    }

    public BN254aFq zero() {
      return ZERO;
    }

    public BN254aFq one() {
      return ONE;
    }

    public BN254aFq multiplicativeGenerator() {
      return MULTIPLICATIVE_GENERATOR;
    }

    public BN254aFq construct(final BigInteger number) {
      return new BN254aFq(number);
    }

    public BN254aFq construct(final Fp element) {
      return new BN254aFq(element);
    }

    public BN254aFq construct(final String element) {
      return new BN254aFq(element);
    }

    public BN254aFq construct(final long number) {
      return new BN254aFq(number);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Twist field Fq2 */
  public static class BN254aFq2 extends BNFq2<BN254aFq, BN254aFq2> {

    public static final BN254aFq2Parameters Fq2Parameters = new BN254aFq2Parameters();
    public static BN254aFq2 ZERO = new BN254aFq2(Fq2Parameters.ZERO());
    public static BN254aFq2 ONE = new BN254aFq2(Fq2Parameters.ONE());

    public Fp2 element;

    public BN254aFq2(final Fp2 element) {
      this.element = element;
    }

    public BN254aFq2(final BigInteger c0, final BigInteger c1) {
      this.element = new Fp2(c0, c1, Fq2Parameters);
    }

    public BN254aFq2(final BN254aFq c0, final BN254aFq c1) {
      this(c0.toBigInteger(), c1.toBigInteger());
    }

    public BN254aFq2(final long c0, final long c1) {
      this(BigInteger.valueOf(c0), BigInteger.valueOf(c1));
    }

    public BN254aFq2 self() {
      return this;
    }

    public Fp2 element() {
      return this.element;
    }

    public BN254aFq2 zero() {
      return ZERO;
    }

    public BN254aFq2 one() {
      return ONE;
    }

    public BN254aFq2 construct(final Fp2 element) {
      return new BN254aFq2(element);
    }

    public BN254aFq2 construct(final BN254aFq c0, final BN254aFq c1) {
      return new BN254aFq2(c0, c1);
    }

    public BN254aFq2 construct(final long c0, final long c1) {
      return new BN254aFq2(c0, c1);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Field Fq6 */
  public static class BN254aFq6 extends BNFq6<BN254aFq, BN254aFq2, BN254aFq6> {

    public static final BN254aFq6Parameters Fq6Parameters = new BN254aFq6Parameters();
    public static BN254aFq6 ZERO = new BN254aFq6(Fq6Parameters.ZERO());
    public static BN254aFq6 ONE = new BN254aFq6(Fq6Parameters.ONE());

    public Fp6_3Over2 element;

    public BN254aFq6(final Fp6_3Over2 element) {
      this.element = element;
    }

    public BN254aFq6(final BN254aFq2 c0, final BN254aFq2 c1, final BN254aFq2 c2) {
      this.element = new Fp6_3Over2(c0.element, c1.element, c2.element, Fq6Parameters);
    }

    public BN254aFq6 self() {
      return this;
    }

    public Fp6_3Over2 element() {
      return this.element;
    }

    public BN254aFq6 zero() {
      return ZERO;
    }

    public BN254aFq6 one() {
      return ONE;
    }

    public Fp2 mulByNonResidue(final Fp2 other) {
      return Fq6Parameters.nonresidue().mul(other);
    }

    public BN254aFq6 construct(final Fp6_3Over2 element) {
      return new BN254aFq6(element);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Field Fq12 */
  public static class BN254aFq12 extends BNFq12<BN254aFq, BN254aFq2, BN254aFq6, BN254aFq12> {

    public static final BN254aFq12Parameters Fq12Parameters = new BN254aFq12Parameters();
    public static BN254aFq12 ZERO = new BN254aFq12(Fq12Parameters.ZERO());
    public static BN254aFq12 ONE = new BN254aFq12(Fq12Parameters.ONE());

    public Fp12_2Over3Over2 element;

    public BN254aFq12(final Fp12_2Over3Over2 element) {
      this.element = element;
    }

    public BN254aFq12(final BN254aFq6 c0, final BN254aFq6 c1) {
      this.element = new Fp12_2Over3Over2(c0.element, c1.element, Fq12Parameters);
    }

    public BN254aFq12 self() {
      return this;
    }

    public Fp12_2Over3Over2 element() {
      return this.element;
    }

    public BN254aFq12 zero() {
      return ZERO;
    }

    public BN254aFq12 one() {
      return ONE;
    }

    public BN254aFq12 construct(final Fp12_2Over3Over2 element) {
      return new BN254aFq12(element);
    }

    public String toString() {
      return this.element.toString();
    }
  }
}
