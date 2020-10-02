/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves;

import java.math.BigInteger;

public abstract class AbstractGT<GTT extends AbstractGT<GTT>> {

    public abstract GTT add(final GTT other);

    public abstract GTT mul(final BigInteger other);

    public abstract GTT one();

    public abstract boolean equals(final GTT other);

    public abstract String toString();

}