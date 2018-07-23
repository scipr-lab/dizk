/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.groups.integergroupparameters;

import algebra.groups.AdditiveIntegerGroup;
import algebra.groups.abstractintegergroupparameters.AbstractAdditiveIntegerGroupParameters;

import java.io.Serializable;
import java.math.BigInteger;

public class LargeAdditiveIntegerGroupParameters extends AbstractAdditiveIntegerGroupParameters
        implements
        Serializable {

    private AdditiveIntegerGroup ZERO;
    private AdditiveIntegerGroup ONE;

    private BigInteger modulus;

    public AdditiveIntegerGroup ZERO() {
        if (ZERO == null) {
            ZERO = new AdditiveIntegerGroup(BigInteger.ZERO, this);
        }

        return ZERO;
    }

    public AdditiveIntegerGroup ONE() {
        if (ONE == null) {
            ONE = new AdditiveIntegerGroup(BigInteger.ONE, this);
        }

        return ONE;
    }

    public BigInteger modulus() {
        if (modulus == null) {
            modulus = new BigInteger("143987564266532958");
        }

        return modulus;
    }

}
