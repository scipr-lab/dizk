/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.fields.abstractfieldparameters.AbstractFp12_2Over3Over2_Parameters;
import algebra.fields.abstractfieldparameters.AbstractFp2Parameters;
import algebra.fields.abstractfieldparameters.AbstractFp6_3Over2_Parameters;
import algebra.fields.mock.fieldparameters.*;
import org.junit.jupiter.api.Test;

public class FieldsTest {
  private <FieldT extends AbstractFieldElement<FieldT>> void verify(
      final FieldT a, final FieldT b) {
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

  private <FieldT extends AbstractFieldElementExpanded<FieldT>> void verifyExtended(
      final FieldT a) {
    final FieldT one = a.one();
    assertTrue(one.equals(one));
    // (w_8)^8 = 1
    assertTrue(a.rootOfUnity(8).pow(8).equals(one));
  }

  private void verifyMulBy024(
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

  @Test
  public void ComplexFieldTest() {
    final ComplexField a = new ComplexField(2);
    final ComplexField b = new ComplexField(5);

    verify(a, b);
    verifyExtended(a);
  }

  @Test
  public void FpTest() {
    final LargeFpParameters FpParameters = new LargeFpParameters();

    final Fp a = new Fp(6, FpParameters);
    final Fp b = new Fp(13, FpParameters);

    verify(a, b);
    verifyExtended(a);
  }

  @Test
  public void Fp2Test() {
    final SmallFp2Parameters Fp2Parameters = new SmallFp2Parameters();

    final Fp2 a = new Fp2(1, 2, Fp2Parameters);
    final Fp2 b = new Fp2(2, 1, Fp2Parameters);

    verify(a, b);
  }

  @Test
  public void Fp3Test() {
    final SmallFp3Parameters Fp3Parameters = new SmallFp3Parameters();

    final Fp3 a = new Fp3(1, 2, 3, Fp3Parameters);
    final Fp3 b = new Fp3(2, 1, 7, Fp3Parameters);

    verify(a, b);
  }

  @Test
  public void Fp6_2Over3Test() {
    final SmallFp6_2Over3_Parameters Fp6Parameters = new SmallFp6_2Over3_Parameters();

    final Fp3 c0 = new Fp3(1, 2, 3, Fp6Parameters.Fp3Parameters());
    final Fp3 c1 = new Fp3(2, 1, 7, Fp6Parameters.Fp3Parameters());

    final Fp6_2Over3 a = new Fp6_2Over3(c0, c1, Fp6Parameters);
    final Fp6_2Over3 b = new Fp6_2Over3(c1, c0, Fp6Parameters);

    verify(a, b);
  }

  @Test
  public void Fp6_3Over2Test() {
    final SmallFp6_3Over2_Parameters Fp6Parameters = new SmallFp6_3Over2_Parameters();

    final Fp2 c0 = new Fp2(1, 2, Fp6Parameters.Fp2Parameters());
    final Fp2 c1 = new Fp2(2, 1, Fp6Parameters.Fp2Parameters());
    final Fp2 c2 = new Fp2(2, 5, Fp6Parameters.Fp2Parameters());

    final Fp6_3Over2 a = new Fp6_3Over2(c0, c1, c2, Fp6Parameters);
    final Fp6_3Over2 b = new Fp6_3Over2(c2, c1, c0, Fp6Parameters);

    verify(a, b);
  }

  @Test
  public void Fp12_2Over3Over2Test() {
    final SmallFp12_2Over3Over2_Parameters Fp12Parameters = new SmallFp12_2Over3Over2_Parameters();

    final Fp2 c0 = new Fp2(1, 2, Fp12Parameters.Fp2Parameters());
    final Fp2 c1 = new Fp2(2, 1, Fp12Parameters.Fp2Parameters());
    final Fp2 c2 = new Fp2(2, 5, Fp12Parameters.Fp2Parameters());

    final Fp6_3Over2 c00 = new Fp6_3Over2(c0, c1, c2, Fp12Parameters.Fp6Parameters());
    final Fp6_3Over2 c01 = new Fp6_3Over2(c2, c1, c0, Fp12Parameters.Fp6Parameters());

    final Fp12_2Over3Over2 a = new Fp12_2Over3Over2(c00, c01, Fp12Parameters);
    final Fp12_2Over3Over2 b = new Fp12_2Over3Over2(c01, c00, Fp12Parameters);

    verify(a, b);
    verifyMulBy024(a, Fp12Parameters);
  }
}
