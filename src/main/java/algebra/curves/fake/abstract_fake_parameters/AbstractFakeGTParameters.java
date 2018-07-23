/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.fake.abstract_fake_parameters;

import algebra.curves.fake.FakeGT;
import algebra.curves.fake.fake_parameters.FakeFqParameters;

public abstract class AbstractFakeGTParameters {

    public abstract FakeFqParameters FqParameters();

    public abstract FakeGT ONE();

}
