/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves;

import algebra.groups.AbstractGroup;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG1Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG2Parameters;
import algebra.curves.fake.FakeG1;
import algebra.curves.fake.FakeG2;
import algebra.curves.fake.FakeInitialize;
import algebra.curves.fake.fake_parameters.FakeG1Parameters;
import algebra.curves.fake.fake_parameters.FakeG2Parameters;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CurvesTest {
    private <GroupT extends AbstractGroup<GroupT>> void GroupTest(final GroupT groupFactory) {
        final BigInteger rand1 = new BigInteger("76749407");
        final BigInteger rand2 = new BigInteger("44410867");
        final BigInteger randsum = new BigInteger("121160274");

        final GroupT zero = groupFactory.zero();
        assertTrue(zero.equals(zero));
        final GroupT one = groupFactory.one();
        assertTrue(one.equals(one));
        final GroupT two = one.mul(new BigInteger("2"));
        assertTrue(two.equals(two));
        final GroupT three = one.mul(new BigInteger("3"));
        final GroupT four = one.mul(new BigInteger("4"));
        final GroupT five = one.mul(new BigInteger("5"));
        assertTrue(two.add(five).equals(three.add(four)));

        final long seed1 = 4;
        final long seed2 = 7;
        final byte[] secureSeed1 = "xz8f5j".getBytes();
        final byte[] secureSeed2 = "f5gh9c".getBytes();
        assertTrue(groupFactory.random(seed1, null).equals(groupFactory.random(seed1, null)));
        assertFalse(groupFactory.random(seed1, null).equals(groupFactory.random(seed2, null)));
        assertFalse(groupFactory.random(null, secureSeed1).equals(groupFactory.random(null, secureSeed1)));
        assertFalse(groupFactory.random(null, secureSeed1).equals(groupFactory.random(null, secureSeed2)));

        final GroupT a = groupFactory.random(seed1, null);
        final GroupT b = groupFactory.random(seed2, null);

        assertFalse(one.equals(zero));
        assertFalse(a.equals(zero));
        assertFalse(a.equals(one));

        assertFalse(b.equals(zero));
        assertFalse(b.equals(one));

        assertTrue(a.dbl().equals(a.add(a)));
        assertTrue(b.dbl().equals(b.add(b)));
        assertTrue(one.add(two).equals(three));
        assertTrue(two.add(one).equals(three));
        assertTrue(a.add(b).equals(b.add(a)));
        assertTrue(a.sub(a).equals(zero));
        assertTrue(a.sub(b).equals(a.add(b.negate())));
        assertTrue(a.sub(b).equals(b.negate().add(a)));

        // handle special cases
        assertTrue(zero.add(a.negate()).equals(a.negate()));
        assertTrue(zero.sub(a).equals(a.negate()));
        assertTrue(a.sub(zero).equals(a));
        assertTrue(a.add(zero).equals(a));
        assertTrue(zero.add(a).equals(a));

        assertTrue(a.add(b).dbl().equals(a.add(b).add(b.add(a))));
        assertTrue(a.add(b).mul(new BigInteger("2")).equals(a.add(b).add(b.add(a))));

        assertTrue(a.mul(rand1).add(a.mul(rand2)).equals(a.mul(randsum)));
    }

    @Test
    public void BN254aTest() {
        // Test G1 of BN254a
        GroupTest(BN254aG1Parameters.ONE);
        // Test G2 of BN254a
        GroupTest(BN254aG2Parameters.ONE);
    }

    @Test
    public void BN254bTest() {
        // Test G1 of BN254b
        GroupTest(BN254bG1Parameters.ONE);
        // Test G2 of BN254b
        GroupTest(BN254bG2Parameters.ONE);
    }

    @Test
    public void FakeTest() {
        FakeInitialize.init();
        final FakeG1 g1Factory = new FakeG1Parameters().ONE();
        final FakeG2 g2Factory = new FakeG2Parameters().ONE();

        GroupTest(g1Factory);
        GroupTest(g2Factory);
    }
}
