/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.fake;

import algebra.fields.Fp;
import algebra.curves.AbstractG2;
import algebra.curves.fake.abstract_fake_parameters.AbstractFakeG2Parameters;

import java.math.BigInteger;
import java.util.ArrayList;

public class FakeG2 extends AbstractG2<FakeG2> {

    protected final Fp element;
    protected AbstractFakeG2Parameters FakeG2Parameters;

    public FakeG2(final Fp element, final AbstractFakeG2Parameters FakeG2Parameters) {
        this.element = element;
        this.FakeG2Parameters = FakeG2Parameters;
    }

    public FakeG2(final BigInteger number, final AbstractFakeG2Parameters FakeG2Parameters) {
        this(new Fp(number, FakeG2Parameters.FqParameters()), FakeG2Parameters);
    }

    public FakeG2(final String number, final AbstractFakeG2Parameters FakeG2Parameters) {
        this(new Fp(number, FakeG2Parameters.FqParameters()), FakeG2Parameters);
    }

    public FakeG2(final long number, final AbstractFakeG2Parameters FakeG2Parameters) {
        this(new Fp(Long.toString(number), FakeG2Parameters.FqParameters()), FakeG2Parameters);
    }

    public FakeG2 self() {
        return this;
    }

    public FakeG2 add(final FakeG2 other) {
        return new FakeG2(this.element.add(other.element), FakeG2Parameters);
    }

    public FakeG2 sub(final FakeG2 other) {
        return new FakeG2(this.element.sub(other.element), FakeG2Parameters);
    }

    public FakeG2 zero() {
        return FakeG2Parameters.ZERO();
    }

    public boolean isZero() {
        return this.equals(FakeG2Parameters.ZERO());
    }

    public boolean isSpecial() {
        return isZero();
    }

    public FakeG2 one() {
        return FakeG2Parameters.ONE();
    }

    public boolean isOne() {
        return this.equals(FakeG2Parameters.ONE());
    }

    public FakeG2 random(final Long seed, final byte[] secureSeed) {
        return new FakeG2(element.random(seed, secureSeed), FakeG2Parameters);
    }

    public FakeG2 negate() {
        return new FakeG2(this.element.negate(), FakeG2Parameters);
    }

    public FakeG2 dbl() {
        return this.add(this);
    }

    public int bitSize() {
        return this.element.bitSize();
    }

    public ArrayList<Integer> fixedBaseWindowTable() {
        return FakeG2Parameters.fixedBaseWindowTable();
    }

    public BigInteger toBigInteger() {
        return this.element.toBigInteger();
    }

    public String toString() {
        return this.element.toString();
    }

    public boolean equals(final FakeG2 other) {
        if (other == null) {
            return false;
        }

        return this.element.equals(other.element);
    }

}
