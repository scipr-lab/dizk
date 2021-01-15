/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.mock;

import algebra.curves.AbstractGT;
import algebra.curves.mock.abstract_fake_parameters.AbstractFakeGTParameters;
import algebra.fields.Fp;
import java.io.Serializable;
import java.math.BigInteger;

public class FakeGT extends AbstractGT<FakeGT> implements Serializable {

  protected final Fp element;
  protected AbstractFakeGTParameters FakeGTParameters;

  public FakeGT(final Fp element, final AbstractFakeGTParameters FakeGTParameters) {
    this.element = element;
    this.FakeGTParameters = FakeGTParameters;
  }

  public FakeGT(final BigInteger number, final AbstractFakeGTParameters FakeGTParameters) {
    this(new Fp(number, FakeGTParameters.FqParameters()), FakeGTParameters);
  }

  public FakeGT(final String number, final AbstractFakeGTParameters FakeGTParameters) {
    this(new Fp(number, FakeGTParameters.FqParameters()), FakeGTParameters);
  }

  public FakeGT(final long number, final AbstractFakeGTParameters FakeGTParameters) {
    this(new Fp(Long.toString(number), FakeGTParameters.FqParameters()), FakeGTParameters);
  }

  public FakeGT add(final FakeGT other) {
    return new FakeGT(this.element.add(other.element), FakeGTParameters);
  }

  public FakeGT mul(final BigInteger other) {
    return new FakeGT(this.element.toBigInteger().multiply(other), FakeGTParameters);
  }

  public FakeGT one() {
    return FakeGTParameters.ONE();
  }

  public boolean isOne() {
    return this.equals(FakeGTParameters.ONE());
  }

  public String toString() {
    return this.element.toString();
  }

  public boolean equals(final FakeGT other) {
    if (other == null) {
      return false;
    }

    return this.element.equals(other.element);
  }
}
