/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.mock.abstract_fake_parameters;

import algebra.curves.mock.FakeG1;
import algebra.curves.mock.fake_parameters.FakeFqParameters;
import java.util.ArrayList;

public abstract class AbstractFakeG1Parameters {

  public abstract FakeFqParameters FqParameters();

  public abstract FakeG1 ZERO();

  public abstract FakeG1 ONE();

  public abstract ArrayList<Integer> fixedBaseWindowTable();
}
