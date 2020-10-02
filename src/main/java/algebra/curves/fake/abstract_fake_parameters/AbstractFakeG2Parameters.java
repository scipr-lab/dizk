/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.fake.abstract_fake_parameters;

import algebra.curves.fake.FakeG2;
import algebra.curves.fake.fake_parameters.FakeFqParameters;
import java.util.ArrayList;

public abstract class AbstractFakeG2Parameters {

  public abstract FakeFqParameters FqParameters();

  public abstract FakeG2 ZERO();

  public abstract FakeG2 ONE();

  public abstract ArrayList<Integer> fixedBaseWindowTable();
}
