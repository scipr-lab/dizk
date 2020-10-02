/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields;

import algebra.fields.abstractfieldparameters.AbstractFp2Parameters;
import java.math.BigInteger;

public class Fp2 extends AbstractFieldElement<Fp2> {
  protected final Fp c0;
  protected final Fp c1;
  private final AbstractFp2Parameters Fp2Parameters;

  public Fp2(final Fp c0, final Fp c1, final AbstractFp2Parameters Fp2Parameters) {
    this.c0 = c0;
    this.c1 = c1;
    this.Fp2Parameters = Fp2Parameters;
  }

  public Fp2(final BigInteger c0, final BigInteger c1, final AbstractFp2Parameters Fp2Parameters) {
    this.c0 = new Fp(c0, Fp2Parameters.FpParameters());
    this.c1 = new Fp(c1, Fp2Parameters.FpParameters());
    this.Fp2Parameters = Fp2Parameters;
  }

  public Fp2(final long c0, final long c1, final AbstractFp2Parameters Fp2Parameters) {
    this.c0 = new Fp(c0, Fp2Parameters.FpParameters());
    this.c1 = new Fp(c1, Fp2Parameters.FpParameters());
    this.Fp2Parameters = Fp2Parameters;
  }

  public Fp2 self() {
    return this;
  }

  public Fp2 add(final Fp2 other) {
    return new Fp2(c0.add(other.c0), c1.add(other.c1), Fp2Parameters);
  }

  public Fp2 sub(final Fp2 other) {
    return new Fp2(c0.sub(other.c0), c1.sub(other.c1), Fp2Parameters);
  }

  public Fp2 mul(final Fp other) {
    return new Fp2(c0.mul(other), c1.mul(other), Fp2Parameters);
  }

  public Fp2 mul(final Fp2 other) {
    // See: Devegili OhEig, Scott Dahab:
    // "Multiplication and Squaring on Pairing-Friendly Fields"
    // Section 3 (Karatsuba)
    final Fp c0C0 = c0.mul(other.c0);
    final Fp c1C1 = c1.mul(other.c1);
    return new Fp2(
        c0C0.add(Fp2Parameters.nonresidue().mul(c1C1)),
        (c0.add(c1)).mul(other.c0.add(other.c1)).sub(c0C0).sub(c1C1),
        Fp2Parameters);
  }

  public Fp2 zero() {
    return Fp2Parameters.ZERO();
  }

  public boolean isZero() {
    return c0.isZero() && c1.isZero();
  }

  public Fp2 one() {
    return Fp2Parameters.ONE();
  }

  public boolean isOne() {
    return c0.isOne() && c1.isZero();
  }

  public Fp2 random(final Long seed, final byte[] secureSeed) {
    return new Fp2(c0.random(seed, secureSeed), c1.random(seed, secureSeed), Fp2Parameters);
  }

  public Fp2 negate() {
    return new Fp2(c0.negate(), c1.negate(), Fp2Parameters);
  }

  public Fp2 square() {
    // Devegili OhEig, Scott Dahab:
    // "Multiplication and Squaring on Pairing-Friendly Fields"
    // Section 3 (Complex squaring)
    final Fp c0c1 = c0.mul(c1);
    final Fp factor = (c0.add(c1)).mul(c0.add(Fp2Parameters.nonresidue().mul(c1)));
    return new Fp2(
        factor.sub(c0c1).sub(Fp2Parameters.nonresidue().mul(c0c1)), c0c1.add(c0c1), Fp2Parameters);
  }

  public Fp2 inverse() {
    // See: "High-Speed Software Implementation of the Optimal Ate
    // Pairing over Barreto-Naehrig Curves"
    // Algorithm 8
    final Fp t0 = c0.square();
    final Fp t1 = c1.square();
    final Fp t2 = t0.sub(Fp2Parameters.nonresidue().mul(t1));
    final Fp t3 = t2.inverse();

    return new Fp2(c0.mul(t3), c1.mul(t3).negate(), Fp2Parameters);
  }

  public Fp2 FrobeniusMap(long power) {
    return new Fp2(
        c0, Fp2Parameters.FrobeniusMapCoefficientsC1()[(int) power % 2].mul(c1), Fp2Parameters);
  }

  public int bitSize() {
    return Math.max(c0.bitSize(), c1.bitSize());
  }

  public Fp2 construct(final Fp c0, final Fp c1) {
    return new Fp2(c0, c1, Fp2Parameters);
  }

  public Fp2 construct(final long c0, final long c1) {
    return new Fp2(c0, c1, Fp2Parameters);
  }

  public String toString() {
    return c0.toString() + ", " + c1.toString();
  }

  public boolean equals(final Fp2 other) {
    if (other == null) {
      return false;
    }

    return c0.equals(other.c0) && c1.equals(other.c1);
  }
}
