/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.groups.abstractintegergroupparameters;

import algebra.groups.AdditiveIntegerGroup;

import java.math.BigInteger;

public abstract class AbstractAdditiveIntegerGroupParameters {

    public abstract AdditiveIntegerGroup ZERO();

    public abstract AdditiveIntegerGroup ONE();

    public abstract BigInteger modulus();

}
