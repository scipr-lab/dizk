/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves;

import algebra.groups.AbstractGroup;

public abstract class AbstractG1<G1T extends AbstractG1<G1T>> extends AbstractGroup<G1T> {

    public abstract boolean isSpecial();

    public abstract int bitSize();

}