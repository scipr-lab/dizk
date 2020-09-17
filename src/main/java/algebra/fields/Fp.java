/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields;

import algebra.fields.abstractfieldparameters.AbstractFpParameters;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class Fp extends AbstractFieldElementExpanded<Fp> {
    protected final BigInteger number;
    public AbstractFpParameters FpParameters;

    public Fp(final BigInteger number, final AbstractFpParameters FpParameters) {
        this.number = number.mod(FpParameters.modulus());
        this.FpParameters = FpParameters;
    }

    public Fp(final String number, final AbstractFpParameters FpParameters) {
        this(new BigInteger(number), FpParameters);
    }

    public Fp(final long number, final AbstractFpParameters FpParameters) {
        this(new BigInteger(Long.toString(number)), FpParameters);
    }

    public Fp self() {
        return this;
    }

    public Fp add(final Fp that) {
        return new Fp(number.add(that.number), FpParameters);
    }

    public Fp sub(final Fp that) {
        return new Fp(number.subtract(that.number), FpParameters);
    }

    public Fp mul(final Fp that) {
        return new Fp(number.multiply(that.number), FpParameters);
    }

    public Fp mul(final BigInteger that) {
        return new Fp(number.multiply(that), FpParameters);
    }

    public Fp zero() {
        return FpParameters.ZERO();
    }

    public boolean isZero() {
        return equals(FpParameters.ZERO());
    }

    public Fp one() {
        return FpParameters.ONE();
    }

    public boolean isOne() {
        return equals(FpParameters.ONE());
    }

    // TODO: Refactor this function (as well as config.secureSeed() and others)
    // to avoid passing "null" around. This makes code debugging harder, and begs
    // for null check on function arguments - which make code less readable and more
    // complex.
    // Be more strict on types in function signatures
    //
    // TODO: Split this function in 2 sub-functions:
    // - 1. `unsecure_random` taking a seed (Long) and returning an Fp el
    // - 2. `secure_random` taking a secureSeed (byte[]) and returning an Fp el
    // This means that a similar division will need to be achieved for the `random` in groups
    public Fp random(final Long seed, final byte[] secureSeed) {
        if (secureSeed != null && secureSeed.length > 0) {
            return new Fp(new SecureRandom(secureSeed).nextLong(), FpParameters);
        } else if (seed != null) {
            return new Fp(new Random(seed).nextLong(), FpParameters);
        } else {
            return new Fp(new Random().nextLong(), FpParameters);
        }
    }

    public Fp negate() {
        return new Fp(number.negate(), FpParameters);
    }

    public Fp square() {
        return new Fp(this.number.multiply(this.number), FpParameters);
    }

    public Fp inverse() {
        return new Fp(number.modInverse(FpParameters.modulus()), FpParameters);
    }

    public Fp multiplicativeGenerator() {
        return FpParameters.multiplicativeGenerator();
    }

    public Fp rootOfUnity(final long order) {
        final BigInteger exponent = FpParameters.modulus().divide(BigInteger.valueOf(order));

        return new Fp(FpParameters.root().modPow(exponent, FpParameters.modulus()), FpParameters);
    }

    public int bitSize() {
        return number.bitLength();
    }

    public Fp construct(final long value) {
        return new Fp(value, FpParameters);
    }

    public BigInteger toBigInteger() {
        return number;
    }

    public String toString() {
        return number.toString();
    }

    public boolean equals(final Fp that) {
        if (that == null) {
            return false;
        }

        return number.equals(that.number);
    }
}
