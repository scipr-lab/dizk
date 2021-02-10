package algebra.fields;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.fields.abstractfieldparameters.AbstractFp12_2Over3Over2_Parameters;
import algebra.fields.abstractfieldparameters.AbstractFp2Parameters;
import algebra.fields.abstractfieldparameters.AbstractFp6_3Over2_Parameters;

public class GenericFieldsTest {
  protected <FieldT extends AbstractFieldElement<FieldT>> void testField(
      final FieldT fieldFactory, final FieldT a, final FieldT b, final FieldT c) {
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
    assertFalse(
        fieldFactory
            .random(null, "clear".getBytes())
            .equals(fieldFactory.random(null, "matics".getBytes())));

    // Check that the 3 input are distinct field elements for the test
    assertFalse(a.isZero());
    assertFalse(a.isOne());
    assertFalse(b.isZero());
    assertFalse(b.isOne());
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

  protected <FieldT extends AbstractFieldElementExpanded<FieldT>> void testFieldExpanded(
      final FieldT a) {
    final FieldT one = a.one();
    assertTrue(one.equals(one));
    // (w_8)^8 = 1
    assertTrue(a.rootOfUnity(8).pow(8).equals(one));
  }

  protected void testMulBy024(
      final Fp12_2Over3Over2 fieldFactory,
      final AbstractFp12_2Over3Over2_Parameters Fp12Parameters) {
    final AbstractFp2Parameters Fp2Parameters = Fp12Parameters.Fp2Parameters();
    final AbstractFp6_3Over2_Parameters Fp6Parameters = Fp12Parameters.Fp6Parameters();

    final Fp12_2Over3Over2 element = fieldFactory.random(4L, null);

    final Fp2 c0 = new Fp2(7, 18, Fp2Parameters);
    final Fp2 c2 = new Fp2(23, 5, Fp2Parameters);
    final Fp2 c4 = new Fp2(192, 73, Fp2Parameters);

    final Fp12_2Over3Over2 naiveResult =
        element.mul(
            new Fp12_2Over3Over2(
                new Fp6_3Over2(c0, Fp2Parameters.ZERO(), c4, Fp6Parameters),
                new Fp6_3Over2(Fp2Parameters.ZERO(), c2, Fp2Parameters.ZERO(), Fp6Parameters),
                Fp12Parameters));
    final Fp12_2Over3Over2 mulBy024Result = element.mulBy024(c0, c2, c4);

    assertTrue(naiveResult.equals(mulBy024Result));
  }
}
