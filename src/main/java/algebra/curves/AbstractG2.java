/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves;

import algebra.groups.AbstractGroup;

public abstract class AbstractG2<G2T extends AbstractG2<G2T>> extends AbstractGroup<G2T> {

    public abstract boolean isSpecial();

    public abstract int bitSize();

}