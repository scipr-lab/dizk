/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.mock.abstract_fake_parameters;

import java.util.ArrayList;

import algebra.curves.mock.FakeG2;
import algebra.curves.mock.fake_parameters.FakeFqParameters;

public abstract class AbstractFakeG2Parameters {

  public abstract FakeFqParameters FqParameters();

  public abstract FakeG2 ZERO();

  public abstract FakeG2 ONE();

  public abstract ArrayList<Integer> fixedBaseWindowTable();
}
