/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.fields.*;
import algebra.fields.abstractfieldparameters.AbstractFp12_2Over3Over2_Parameters;
import algebra.fields.abstractfieldparameters.AbstractFp2Parameters;
import algebra.fields.abstractfieldparameters.AbstractFp6_3Over2_Parameters;

public class GenericFieldsTest {
  public <FieldT extends AbstractFieldElement<FieldT>> void testFieldOperations(
      final FieldT fieldFactory) {
    final FieldT zero = fieldFactory.zero();
    assertTrue(zero.equals(zero));
    assertTrue(zero.isZero());
    assertFalse(zero.isOne());

    final FieldT one = fieldFactory.one();
    assertTrue(one.equals(one));
    assertTrue(one.isOne());
    assertFalse(one.isZero());

    // FieldT.random() != FieldT.random()
    assertTrue(fieldFactory.random(4L, null).equals(fieldFactory.random(4L, null)));
    assertFalse(fieldFactory.random(5L, null).equals(fieldFactory.random(7L, null)));
    assertFalse(fieldFactory.random(null, "clear".getBytes()).equals(fieldFactory.random(null, "matics".getBytes())));

    // Select 3 distinct field elements for the test
    final FieldT a = fieldFactory.random(4L, null);
    assertFalse(a.isZero());
    assertFalse(a.isOne());
    final FieldT b = fieldFactory.random(7L, null);
    assertFalse(b.isZero());
    assertFalse(b.isOne());
    final FieldT c = fieldFactory.random(12L, null);
    assertFalse(c.isZero());
    assertFalse(c.isOne());
    // Make sure the elements are distinct
    assertFalse(a.equals(b));
    assertFalse(a.equals(c));
    assertFalse(b.equals(c));

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
    // (a+b)+c = a+(b+c)
    assertTrue((a.add(b)).add(c).equals(a.add(b.add(c))));
    // (a*b)*c = a*(b*c)
    assertTrue((a.mul(b)).mul(c).equals(a.mul(b.mul(c))));
    // (a+b)*c = a*c + b*c
    assertTrue((a.add(b)).mul(c).equals(a.mul(c).add(b.mul(c))));
    // (a+b)^2 = a^2 + 2ab + b^2
    assertTrue((a.add(b)).square().equals(a.square().add(a.mul(b).add(a.mul(b))).add(b.square())));
  }

  public <FieldT extends AbstractFieldElementExpanded<FieldT>> void testFieldExpandedOperations(
      final FieldT a) {
    final FieldT one = a.one();
    assertTrue(one.equals(one));
    // (w_8)^8 = 1
    assertTrue(a.rootOfUnity(8).pow(8).equals(one));
  }

  public void verifyMulBy024(
      final Fp12_2Over3Over2 a, final AbstractFp12_2Over3Over2_Parameters Fp12Parameters) {
    final AbstractFp2Parameters Fp2Parameters = Fp12Parameters.Fp2Parameters();
    final AbstractFp6_3Over2_Parameters Fp6Parameters = Fp12Parameters.Fp6Parameters();

    final Fp2 c0 = new Fp2(7, 18, Fp2Parameters);
    final Fp2 c2 = new Fp2(23, 5, Fp2Parameters);
    final Fp2 c4 = new Fp2(192, 73, Fp2Parameters);

    final Fp12_2Over3Over2 naiveResult =
        a.mul(
            new Fp12_2Over3Over2(
                new Fp6_3Over2(c0, Fp2Parameters.ZERO(), c4, Fp6Parameters),
                new Fp6_3Over2(Fp2Parameters.ZERO(), c2, Fp2Parameters.ZERO(), Fp6Parameters),
                Fp12Parameters));
    final Fp12_2Over3Over2 mulBy024Result = a.mulBy024(c0, c2, c4);

    assertTrue(naiveResult.equals(mulBy024Result));
  }

  public void verifyFrobeniusMap(
      final Fp12_2Over3Over2 a, final AbstractFp12_2Over3Over2_Parameters Fp12Parameters) {
    assert (a.FrobeniusMap(0).equals(a));
    Fp12_2Over3Over2 a_q = a.pow(Fp12Parameters.FpParameters().modulus());
    for (int power = 1; power < 10; ++power) {
      final Fp12_2Over3Over2 a_qi = a.FrobeniusMap(power);
      assert (a_qi.equals(a_q));

      a_q = a_q.pow(Fp12Parameters.FpParameters().modulus());
    }
  }
}
