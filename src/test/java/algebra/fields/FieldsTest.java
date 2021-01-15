/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields;

import algebra.fields.mock.fieldparameters.*;

import org.junit.jupiter.api.Test;


public class FieldsTest extends GenericFieldsTest {
  @Test
  public void ComplexFieldTest() {
    final ComplexField ffactory = new ComplexField();
    final ComplexField a = new ComplexField(2, 1);
    final ComplexField b = new ComplexField(5, 4);
    final ComplexField c = new ComplexField(6, 2);

    testField(ffactory, a, b, c);
    testFieldExpanded(ffactory);
  }

  @Test
  public void FpTest() {
    final LargeFpParameters FpParameters = new LargeFpParameters();
    final Fp ffactory = FpParameters.ONE();
    final Fp a = new Fp(6, FpParameters);
    final Fp b = new Fp(5, FpParameters);
    final Fp c = new Fp(2, FpParameters);

    testField(ffactory, a, b, c);
    testFieldExpanded(ffactory);
  }

  @Test
  public void Fp2Test() {
    final SmallFp2Parameters Fp2Parameters = new SmallFp2Parameters();
    final Fp2 ffactory = Fp2Parameters.ONE();
    final Fp2 a = new Fp2(6, 4, Fp2Parameters);
    final Fp2 b = new Fp2(5, 2, Fp2Parameters);
    final Fp2 c = new Fp2(2, 3, Fp2Parameters);

    testField(ffactory, a, b, c);
  }

  @Test
  public void Fp3Test() {
    final SmallFp3Parameters Fp3Parameters = new SmallFp3Parameters();
    final Fp3 ffactory = Fp3Parameters.ONE();
    final Fp3 a = new Fp3(6, 4, 7, Fp3Parameters);
    final Fp3 b = new Fp3(5, 2, 5, Fp3Parameters);
    final Fp3 c = new Fp3(2, 3, 2, Fp3Parameters);

    testField(ffactory, a, b, c);
  }

  @Test
  public void Fp6_2Over3Test() {
    final SmallFp6_2Over3_Parameters Fp6Parameters = new SmallFp6_2Over3_Parameters();
    final SmallFp3Parameters Fp3Parameters = Fp6Parameters.Fp3Parameters();
    final Fp3 c0 = new Fp3(6, 4, 7, Fp3Parameters);
    final Fp3 c1 = new Fp3(5, 2, 5, Fp3Parameters);

    final Fp6_2Over3 ffactory = Fp6Parameters.ONE();
    final Fp6_2Over3 a = new Fp6_2Over3(c0, c0, Fp6Parameters);
    final Fp6_2Over3 b = new Fp6_2Over3(c0, c1, Fp6Parameters);
    final Fp6_2Over3 c = new Fp6_2Over3(c1, c1, Fp6Parameters);

    testField(ffactory, a, b, c);
  }

  @Test
  public void Fp6_3Over2Test() {
    final SmallFp6_3Over2_Parameters Fp6Parameters = new SmallFp6_3Over2_Parameters();
    final SmallFp2Parameters Fp2Parameters = Fp6Parameters.Fp2Parameters();
    final Fp2 c0 = new Fp2(5, 4, Fp2Parameters);
    final Fp2 c1 = new Fp2(5, 2, Fp2Parameters);
    final Fp2 c2 = new Fp2(4, 3, Fp2Parameters);

    final Fp6_3Over2 ffactory = Fp6Parameters.ONE();
    final Fp6_3Over2 a = new Fp6_3Over2(c0, c1, c0, Fp6Parameters);
    final Fp6_3Over2 b = new Fp6_3Over2(c1, c2, c1, Fp6Parameters);
    final Fp6_3Over2 c = new Fp6_3Over2(c2, c1, c2, Fp6Parameters);

    testField(ffactory, a, b, c);
  }

  @Test
  public void Fp12_2Over3Over2Test() {
    final SmallFp12_2Over3Over2_Parameters Fp12Parameters = new SmallFp12_2Over3Over2_Parameters();
    final SmallFp2Parameters Fp2Parameters = Fp12Parameters.Fp2Parameters();
    final Fp2 c00 = new Fp2(6, 4, Fp2Parameters);
    final Fp2 c01 = new Fp2(5, 2, Fp2Parameters);
    final Fp2 c02 = new Fp2(8, 3, Fp2Parameters);

    final SmallFp6_3Over2_Parameters Fp6Parameters = Fp12Parameters.Fp6Parameters();
    final Fp6_3Over2 c0 = new Fp6_3Over2(c00, c01, c00, Fp6Parameters);
    final Fp6_3Over2 c1 = new Fp6_3Over2(c01, c02, c01, Fp6Parameters);
    final Fp6_3Over2 c2 = new Fp6_3Over2(c02, c01, c02, Fp6Parameters);

    final Fp12_2Over3Over2 ffactory = Fp12Parameters.ONE();
    final Fp12_2Over3Over2 a = new Fp12_2Over3Over2(c0, c1, Fp12Parameters);
    final Fp12_2Over3Over2 b = new Fp12_2Over3Over2(c1, c2, Fp12Parameters);
    final Fp12_2Over3Over2 c = new Fp12_2Over3Over2(c2, c0, Fp12Parameters);

    testField(ffactory, a, b, c);
    testMulBy024(Fp12Parameters.ONE(), Fp12Parameters);
  }
}
