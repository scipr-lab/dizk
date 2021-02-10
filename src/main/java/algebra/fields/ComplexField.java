/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class ComplexField extends AbstractFieldElementExpanded<ComplexField> {
  private final double re;
  private final double im;

  private final int DOUBLE_LENGTH = 64;

  public ComplexField(final double real, final double imag) {
    re = real;
    im = imag;
  }

  public ComplexField() {
    this(0.0, 0.0);
  }

  public ComplexField(final double real) {
    this(real, 0.0);
  }

  public ComplexField(final long real) {
    this((double) real, 0.0);
  }

  public ComplexField(final long real, final long imag) {
    this((double) real, (double) imag);
  }

  public ComplexField self() {
    return this;
  }

  public ComplexField add(final ComplexField other) {
    return new ComplexField(this.re + other.re, this.im + other.im);
  }

  public ComplexField sub(final ComplexField other) {
    return new ComplexField(this.re - other.re, this.im - other.im);
  }

  public ComplexField mul(final ComplexField other) {
    final double real = this.re * other.re - this.im * other.im;
    final double imag = this.re * other.im + this.im * other.re;
    return new ComplexField(real, imag);
  }

  public ComplexField zero() {
    return new ComplexField(0.0);
  }

  public boolean isZero() {
    return this.equals(zero());
  }

  public ComplexField one() {
    return new ComplexField(1.0);
  }

  public boolean isOne() {
    return this.equals(one());
  }

  public ComplexField random(final Long seed, final byte[] secureSeed) {
    if (secureSeed != null && secureSeed.length > 0) {
      return new ComplexField(new SecureRandom(secureSeed).nextLong());
    } else if (seed != null) {
      return new ComplexField(new Random(seed).nextLong());
    } else {
      return new ComplexField(new Random().nextLong());
    }
  }

  public ComplexField negate() {
    return new ComplexField(-this.re, -this.im);
  }

  public ComplexField square() {
    return this.mul(this);
  }

  public ComplexField inverse() {
    final double magnitude = this.re * this.re + this.im * this.im;
    return new ComplexField(this.re / magnitude, -this.im / magnitude);
  }

  public ComplexField multiplicativeGenerator() {
    return new ComplexField(1);
  }

  public ComplexField rootOfUnity(final long size) {
    final double x = 2.0 * Math.PI / size;
    return new ComplexField(Math.cos(x), Math.sin(x));
  }

  public ComplexField pow(final long exponent) {
    ComplexField result = this.one();
    ComplexField value = this;
    long currentExponent = exponent;
    while (currentExponent > 0) {
      if (currentExponent % 2 == 1) {
        result = result.mul(value);
      }
      value = value.mul(value);
      currentExponent /= 2;
    }
    return result;
  }

  public int bitSize() {
    return DOUBLE_LENGTH;
  }

  // Additional function required to comply with the extended `AbstractFieldElementExpanded`
  // abstract class (with a constructor from BigInt). Do not use, converting BigInts to double isn't
  // without risks (lose precision + risk to obtain `Double.NEGATIVE_INFINITY` or
  // `Double.POSITIVE_INFINITY`)
  public ComplexField construct(final BigInteger number) {
    return new ComplexField(number.doubleValue());
  }

  public ComplexField construct(final long value) {
    return new ComplexField(value);
  }

  public BigInteger toBigInteger() {
    // Undefined behavior, method for modulus fields, do not use.
    return new BigInteger(Double.toString(Math.sqrt(re * re + im * im)));
  }

  public String toString() {
    return "(" + Double.toString(re) + ", " + Double.toString(im) + ")";
  }

  public boolean equals(final ComplexField other) {
    if (other == null) {
      return false;
    }
    return (Math.abs(this.re - other.re) < 0.000001) && (Math.abs(this.im - other.im) < 0.000001);
  }
}
