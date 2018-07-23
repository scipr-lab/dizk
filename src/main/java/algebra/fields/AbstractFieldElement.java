/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields;

import java.io.Serializable;
import java.math.BigInteger;

public abstract class AbstractFieldElement<FieldT extends AbstractFieldElement<FieldT>>
        implements Serializable {
    /* Returns self element */
    public abstract FieldT self();

    /* Returns this + that */
    public abstract FieldT add(final FieldT that);

    /* Returns this - that */
    public abstract FieldT sub(final FieldT that);

    /* Returns this * that */
    public abstract FieldT mul(final FieldT that);

    /* Returns the zero element */
    public abstract FieldT zero();

    /* Returns if this == zero */
    public abstract boolean isZero();

    /* Returns the one element */
    public abstract FieldT one();

    /* Returns if this == one */
    public abstract boolean isOne();

    /* Returns -this */
    public abstract FieldT negate();

    /* Returns this^2 */
    public abstract FieldT square();

    /* Returns this^(-1) */
    public abstract FieldT inverse();

    /* Returns this^exponent with long exponent */
    public FieldT pow(final long exponent) {
        FieldT value = this.self();
        FieldT result = this.one();
        long currentExponent = exponent;
        while (currentExponent > 0) {
            if (currentExponent % 2 == 1) {
                result = result.mul(value);
            }
            value = value.square();
            currentExponent >>= 1;
        }
        return result;
    }

    /* Returns this^exponent with BigInteger exponent */
    public FieldT pow(final BigInteger exponent) {
        FieldT value = this.self();
        FieldT result = this.one();

        BigInteger currentExponent = exponent;
        final BigInteger TWO = BigInteger.ONE.add(BigInteger.ONE);
        while (currentExponent.compareTo(BigInteger.ZERO) == 1) {
            if (currentExponent.mod(TWO).equals(BigInteger.ONE)) {
                result = result.mul(value);
            }
            value = value.square();
            currentExponent = currentExponent.shiftRight(1);
        }
        return result;
    }

    /* Returns the maximum bit length of the values composing the element. */
    public abstract int bitSize();

    /**
     * If secureSeed is provided, returns cryptographically secure random field element using byte[].
     * Else if seed is provided, returns pseudorandom field element using long as seed.
     * Else, returns a pseudorandom field element without a seed.
     */
    public abstract FieldT random(final Long seed, final byte[] secureSeed);

    /* Returns this == that */
    public abstract boolean equals(final FieldT that);

    /* Returns this as string */
    public abstract String toString();

}