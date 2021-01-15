/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.mock.fake_parameters;

import algebra.curves.mock.FakeGT;
import algebra.curves.mock.abstract_fake_parameters.AbstractFakeGTParameters;
import java.io.Serializable;
import java.math.BigInteger;

public class FakeGTParameters extends AbstractFakeGTParameters implements Serializable {

  private FakeFqParameters FqParameters;

  private FakeGT ONE;

  public FakeFqParameters FqParameters() {
    if (FqParameters == null) {
      FqParameters = new FakeFqParameters();
    }

    return FqParameters;
  }

  public FakeGT ONE() {
    if (ONE == null) {
      ONE = new FakeGT(BigInteger.ONE, this);
    }

    return ONE;
  }
}
