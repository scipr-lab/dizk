package algebra.curves.barreto_naehrig.bn254b;

import algebra.curves.barreto_naehrig.BNFields.*;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.*;
import algebra.fields.Fp;
import algebra.fields.Fp12_2Over3Over2;
import algebra.fields.Fp2;
import algebra.fields.Fp6_3Over2;
import java.math.BigInteger;

public class BN254bFields {

  /* Scalar field Fr */
  public static class BN254bFr extends BNFr<BN254bFr> {

    public static final BN254bFrParameters FrParameters = new BN254bFrParameters();
    public static final BN254bFr ZERO = new BN254bFr(FrParameters.ZERO());
    public static final BN254bFr ONE = new BN254bFr(FrParameters.ONE());
    public static final BN254bFr MULTIPLICATIVE_GENERATOR =
        new BN254bFr(FrParameters.multiplicativeGenerator());

    public Fp element;

    public BN254bFr(final BigInteger number) {
      this.element = new Fp(number, FrParameters);
    }

    public BN254bFr(final Fp number) {
      this(number.toBigInteger());
    }

    public BN254bFr(final String number) {
      this(new BigInteger(number));
    }

    public BN254bFr(final long number) {
      this(BigInteger.valueOf(number));
    }

    public BN254bFr self() {
      return this;
    }

    public Fp element() {
      return element;
    }

    public BN254bFr zero() {
      return ZERO;
    }

    public BN254bFr one() {
      return ONE;
    }

    public BN254bFr multiplicativeGenerator() {
      return MULTIPLICATIVE_GENERATOR;
    }

    public BN254bFr construct(final long number) {
      return new BN254bFr(number);
    }

    public BN254bFr construct(final Fp element) {
      return new BN254bFr(element);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Base field Fq */
  public static class BN254bFq extends BNFq<BN254bFq> {

    public static final BN254bFqParameters FqParameters = new BN254bFqParameters();
    public static final BN254bFq ZERO = new BN254bFq(FqParameters.ZERO());
    public static final BN254bFq ONE = new BN254bFq(FqParameters.ONE());
    public static final BN254bFq MULTIPLICATIVE_GENERATOR =
        new BN254bFq(FqParameters.multiplicativeGenerator());

    public Fp element;

    public BN254bFq(final Fp element) {
      this.element = element;
    }

    public BN254bFq(final BigInteger number) {
      this.element = new Fp(number, FqParameters);
    }

    public BN254bFq(final String number) {
      this(new BigInteger(number));
    }

    public BN254bFq(final long number) {
      this(BigInteger.valueOf(number));
    }

    public BN254bFq self() {
      return this;
    }

    public Fp element() {
      return element;
    }

    public BN254bFq zero() {
      return ZERO;
    }

    public BN254bFq one() {
      return ONE;
    }

    public BN254bFq multiplicativeGenerator() {
      return MULTIPLICATIVE_GENERATOR;
    }

    public BN254bFq construct(final Fp element) {
      return new BN254bFq(element);
    }

    public BN254bFq construct(final String element) {
      return new BN254bFq(element);
    }

    public BN254bFq construct(final long number) {
      return new BN254bFq(number);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Twist field Fq2 */
  public static class BN254bFq2 extends BNFq2<BN254bFq, BN254bFq2> {

    public static final BN254bFq2Parameters Fq2Parameters = new BN254bFq2Parameters();
    public static BN254bFq2 ZERO = new BN254bFq2(Fq2Parameters.ZERO());
    public static BN254bFq2 ONE = new BN254bFq2(Fq2Parameters.ONE());

    public Fp2 element;

    public BN254bFq2(final Fp2 element) {
      this.element = element;
    }

    public BN254bFq2(final BigInteger c0, final BigInteger c1) {
      this.element = new Fp2(c0, c1, Fq2Parameters);
    }

    public BN254bFq2(final BN254bFq c0, final BN254bFq c1) {
      this(c0.toBigInteger(), c1.toBigInteger());
    }

    public BN254bFq2(final long c0, final long c1) {
      this(BigInteger.valueOf(c0), BigInteger.valueOf(c1));
    }

    public BN254bFq2 self() {
      return this;
    }

    public Fp2 element() {
      return this.element;
    }

    public BN254bFq2 zero() {
      return ZERO;
    }

    public BN254bFq2 one() {
      return ONE;
    }

    public BN254bFq2 construct(final Fp2 element) {
      return new BN254bFq2(element);
    }

    public BN254bFq2 construct(final BN254bFq c0, final BN254bFq c1) {
      return new BN254bFq2(c0, c1);
    }

    public BN254bFq2 construct(final long c0, final long c1) {
      return new BN254bFq2(c0, c1);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Field Fq6 */
  public static class BN254bFq6 extends BNFq6<BN254bFq, BN254bFq2, BN254bFq6> {

    public static final BN254bFq6Parameters Fq6Parameters = new BN254bFq6Parameters();
    public static BN254bFq6 ZERO = new BN254bFq6(Fq6Parameters.ZERO());
    public static BN254bFq6 ONE = new BN254bFq6(Fq6Parameters.ONE());

    public Fp6_3Over2 element;

    public BN254bFq6(final Fp6_3Over2 element) {
      this.element = element;
    }

    public BN254bFq6(final BN254bFq2 c0, final BN254bFq2 c1, final BN254bFq2 c2) {
      this.element = new Fp6_3Over2(c0.element, c1.element, c2.element, Fq6Parameters);
    }

    public BN254bFq6 self() {
      return this;
    }

    public Fp6_3Over2 element() {
      return this.element;
    }

    public BN254bFq6 zero() {
      return ZERO;
    }

    public BN254bFq6 one() {
      return ONE;
    }

    public Fp2 mulByNonResidue(final Fp2 other) {
      return Fq6Parameters.nonresidue().mul(other);
    }

    public BN254bFq6 construct(final Fp6_3Over2 element) {
      return new BN254bFq6(element);
    }

    public String toString() {
      return this.element.toString();
    }
  }

  /* Field Fq12 */
  public static class BN254bFq12 extends BNFq12<BN254bFq, BN254bFq2, BN254bFq6, BN254bFq12> {

    public static final BN254bFq12Parameters Fq12Parameters = new BN254bFq12Parameters();
    public static BN254bFq12 ZERO = new BN254bFq12(Fq12Parameters.ZERO());
    public static BN254bFq12 ONE = new BN254bFq12(Fq12Parameters.ONE());

    public Fp12_2Over3Over2 element;

    public BN254bFq12(final Fp12_2Over3Over2 element) {
      this.element = element;
    }

    public BN254bFq12(final BN254bFq6 c0, final BN254bFq6 c1) {
      this.element = new Fp12_2Over3Over2(c0.element, c1.element, Fq12Parameters);
    }

    public BN254bFq12 self() {
      return this;
    }

    public Fp12_2Over3Over2 element() {
      return this.element;
    }

    public BN254bFq12 zero() {
      return ZERO;
    }

    public BN254bFq12 one() {
      return ONE;
    }

    public BN254bFq12 construct(final Fp12_2Over3Over2 element) {
      return new BN254bFq12(element);
    }

    public String toString() {
      return this.element.toString();
    }
  }
}
