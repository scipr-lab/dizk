/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves;

import algebra.fields.*;
import algebra.fields.abstractfieldparameters.AbstractFp12_2Over3Over2_Parameters;
import algebra.fields.abstractfieldparameters.AbstractFp2Parameters;
import algebra.fields.abstractfieldparameters.AbstractFp6_3Over2_Parameters;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.*;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.*;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFq12Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFq12Parameters;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BNFieldsTest {
    private <FieldT extends AbstractFieldElement<FieldT>> void verify(
            final FieldT a,
            final FieldT b) {
        final FieldT zero = a.zero();
        assertTrue(zero.equals(zero));
        assertTrue(zero.isZero());
        assertFalse(zero.isOne());
        final FieldT one = a.one();
        assertTrue(one.equals(one));
        assertTrue(one.isOne());
        assertFalse(one.isZero());

        // FieldT.random() != FieldT.random()
        assertTrue(a.random(4L, null).equals(a.random(4L, null)));
        assertFalse(a.random(5L, null).equals(a.random(7L, null)));
        assertFalse(a.random(null, "zc".getBytes()).equals(a.random(null, "ash".getBytes())));

        // a == a
        assertTrue(a.equals(a));
        // a != b
        assertFalse(a.equals(b));
        // a-a = 0
        assertTrue(a.sub(a).equals(zero));
        // a+0 = a
        assertTrue(a.add(zero).equals(a));
        // a*0 = 0
        assertTrue(a.mul(zero).equals(zero));
        // a*1 = a
        assertTrue(a.mul(one).equals(a));
        // a*a^-1 = 1
        assertTrue(a.mul(a.inverse()).equals(one));
        // a*a = a^2
        assertTrue(a.mul(a).equals(a.square()));
        // a*a*a = a^3
        assertTrue(a.mul(a).mul(a).equals(a.pow(3)));
        // a-b = -(b-a)
        assertTrue(a.sub(b).equals(b.sub(a).negate()));
        // (a+b)+a = a+(b+a)
        assertTrue((a.add(b)).add(a).equals(a.add(b.add(a))));
        // (a*b)*a = a*(b*a)
        assertTrue((a.mul(b)).mul(a).equals(a.mul(b.mul(a))));
        // (a+b)^2 = a^2 + 2ab + b^2
        assertTrue((a.add(b)).square().equals(a.square().add(a.mul(b).add(a.mul(b))).add(b.square())));
    }

    private <FieldT extends AbstractFieldElementExpanded<FieldT>> void verifyExtended(final FieldT
                                                                                              a) {
        final FieldT one = a.one();
        assertTrue(one.equals(one));
        // (w_8)^8 = 1
        assertTrue(a.rootOfUnity(8).pow(8).equals(one));
    }

    private void verifyMulBy024(
            final Fp12_2Over3Over2 a,
            final AbstractFp12_2Over3Over2_Parameters Fp12Parameters) {
        final AbstractFp2Parameters Fp2Parameters = Fp12Parameters.Fp2Parameters();
        final AbstractFp6_3Over2_Parameters Fp6Parameters = Fp12Parameters.Fp6Parameters();

        final Fp2 c0 = new Fp2(7, 18, Fp2Parameters);
        final Fp2 c2 = new Fp2(23, 5, Fp2Parameters);
        final Fp2 c4 = new Fp2(192, 73, Fp2Parameters);

        final Fp12_2Over3Over2 naiveResult = a
                .mul(new Fp12_2Over3Over2(new Fp6_3Over2(c0, Fp2Parameters.ZERO(), c4, Fp6Parameters),
                        new Fp6_3Over2(Fp2Parameters.ZERO(), c2, Fp2Parameters.ZERO(), Fp6Parameters),
                        Fp12Parameters));
        final Fp12_2Over3Over2 mulBy024Result = a.mulBy024(c0, c2, c4);

        assertTrue(naiveResult.equals(mulBy024Result));
    }

    private void verifyFrobeniusMap(
            final Fp12_2Over3Over2 a,
            final AbstractFp12_2Over3Over2_Parameters Fp12Parameters) {
        assert (a.FrobeniusMap(0).equals(a));
        Fp12_2Over3Over2 a_q = a.pow(Fp12Parameters.FpParameters().modulus());
        for (int power = 1; power < 10; ++power) {
            final Fp12_2Over3Over2 a_qi = a.FrobeniusMap(power);
            assert (a_qi.equals(a_q));

            a_q = a_q.pow(Fp12Parameters.FpParameters().modulus());
        }
    }

    @Test
    public void BN254aFrTest() {
        final BN254aFr a = new BN254aFr("6");
        final BN254aFr b = new BN254aFr("13");

        verify(a, b);
        verifyExtended(a);
    }

    @Test
    public void BN254aFqTest() {
        final BN254aFq a = new BN254aFq("6");
        final BN254aFq b = new BN254aFq("13");

        verify(a, b);
        verifyExtended(a);
    }

    @Test
    public void BN254aFq2Test() {
        final BN254aFq c0 = new BN254aFq("6");
        final BN254aFq c1 = new BN254aFq("13");

        final BN254aFq2 a = new BN254aFq2(c0, c1);
        final BN254aFq2 b = new BN254aFq2(c1, c0);

        verify(a, b);
    }

    @Test
    public void BN254aFq6Test() {
        final BN254aFq b0 = new BN254aFq("6");
        final BN254aFq b1 = new BN254aFq("13");

        final BN254aFq2 c0 = new BN254aFq2(b0, b1);
        final BN254aFq2 c1 = new BN254aFq2(b1, b0);

        final BN254aFq6 a = new BN254aFq6(c0, c1, c0);
        final BN254aFq6 b = new BN254aFq6(c1, c0, c1);

        verify(a, b);
    }

    @Test
    public void BN254aFq12Test() {
        final BN254aFq12Parameters Fq12Parameters = new BN254aFq12Parameters();

        final BN254aFq b0 = new BN254aFq("6");
        final BN254aFq b1 = new BN254aFq("13");

        final BN254aFq2 c0 = new BN254aFq2(b0, b1);
        final BN254aFq2 c1 = new BN254aFq2(b1, b0);

        final BN254aFq6 d0 = new BN254aFq6(c0, c1, c0);
        final BN254aFq6 d1 = new BN254aFq6(c1, c0, c1);

        final BN254aFq12 a = new BN254aFq12(d0, d1);
        final BN254aFq12 b = new BN254aFq12(d1, d0);

        verify(a, b);
        verifyFrobeniusMap(Fq12Parameters.ONE(), Fq12Parameters);
    }

    @Test
    public void BN254bFrTest() {
        final BN254bFr a = new BN254bFr("6");
        final BN254bFr b = new BN254bFr("13");

        verify(a, b);
        verifyExtended(a);
    }

    @Test
    public void BN254bFqTest() {
        final BN254bFq a = new BN254bFq("6");
        final BN254bFq b = new BN254bFq("13");

        verify(a, b);
        verifyExtended(a);
    }

    @Test
    public void BN254bFq2Test() {
        final BN254bFq c0 = new BN254bFq("6");
        final BN254bFq c1 = new BN254bFq("13");

        final BN254bFq2 a = new BN254bFq2(c0, c1);
        final BN254bFq2 b = new BN254bFq2(c1, c0);

        verify(a, b);
    }

    @Test
    public void BN254bFq6Test() {
        final BN254bFq b0 = new BN254bFq("6");
        final BN254bFq b1 = new BN254bFq("13");

        final BN254bFq2 c0 = new BN254bFq2(b0, b1);
        final BN254bFq2 c1 = new BN254bFq2(b1, b0);

        final BN254bFq6 a = new BN254bFq6(c0, c1, c0);
        final BN254bFq6 b = new BN254bFq6(c1, c0, c1);

        verify(a, b);
    }

    @Test
    public void BN254bFq12Test() {
        final BN254bFq12Parameters Fq12Parameters = new BN254bFq12Parameters();

        final BN254bFq b0 = new BN254bFq("6");
        final BN254bFq b1 = new BN254bFq("13");

        final BN254bFq2 c0 = new BN254bFq2(b0, b1);
        final BN254bFq2 c1 = new BN254bFq2(b1, b0);

        final BN254bFq6 d0 = new BN254bFq6(c0, c1, c0);
        final BN254bFq6 d1 = new BN254bFq6(c1, c0, c1);

        final BN254bFq12 a = new BN254bFq12(d0, d1);
        final BN254bFq12 b = new BN254bFq12(d1, d0);

        verify(a, b);
        verifyFrobeniusMap(Fq12Parameters.ONE(), Fq12Parameters);
    }
}
