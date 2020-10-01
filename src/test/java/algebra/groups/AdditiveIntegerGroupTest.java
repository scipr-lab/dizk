/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.groups;

import algebra.groups.integergroupparameters.LargeAdditiveIntegerGroupParameters;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdditiveIntegerGroupTest implements Serializable {
    private <GroupT extends AbstractGroup<GroupT>> void verify(final GroupT a, final GroupT b) {
        final GroupT zero = a.zero();
        assertTrue(zero.equals(zero));
        final GroupT one = a.one();
        assertTrue(one.equals(one));
        final GroupT two = a.one().mul(new BigInteger("2"));
        assertTrue(two.equals(two));
        final GroupT five = a.one().mul(new BigInteger("5"));
        final GroupT three = a.one().mul(new BigInteger("3"));
        final GroupT four = a.one().mul(new BigInteger("4"));
        assertTrue(two.add(five).equals(three.add(four)));

        // GroupT.random() != GroupT.random()
        assertTrue(a.random(4L, null).equals(a.random(4L, null)));
        assertFalse(a.random(5L, null).equals(a.random(7L, null)));
        assertFalse(a.random(null, "zc".getBytes()).equals(a.random(null, "ash".getBytes())));

        // a == a
        assertTrue(a.equals(a));
        // a != b
        assertFalse(a.equals(b));
        // a+0 = a
        assertTrue(a.add(zero).equals(a));
        // a+a = a.dbl()
        assertTrue(a.add(a).equals(a.dbl()));
        // a+a+a = 3*a
        assertTrue(a.add(a).add(a).equals(a.mul(new BigInteger("3"))));
        // a-b = -(b-a)
        assertTrue(a.sub(b).equals(b.sub(a).negate()));
        // (a+b)+a = a+(b+a)
        assertTrue((a.add(b)).add(a).equals(a.add(b.add(a))));
    }

    @Test
    public void AdditiveIntegerGroupElementTest() {
        LargeAdditiveIntegerGroupParameters GroupParameters = new LargeAdditiveIntegerGroupParameters();
        final AdditiveIntegerGroup a = new AdditiveIntegerGroup(2, GroupParameters);
        final AdditiveIntegerGroup b = new AdditiveIntegerGroup(5, GroupParameters);

        verify(a, b);
    }
}