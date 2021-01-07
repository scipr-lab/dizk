/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.mock.abstract_fake_parameters;

import algebra.curves.mock.FakeGT;
import algebra.curves.mock.fake_parameters.FakeFqParameters;

public abstract class AbstractFakeGTParameters {

  public abstract FakeFqParameters FqParameters();

  public abstract FakeGT ONE();
}
