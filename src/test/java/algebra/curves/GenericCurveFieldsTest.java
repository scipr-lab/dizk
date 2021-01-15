package algebra.curves;

import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.fields.Fp12_2Over3Over2;
import algebra.fields.GenericFieldsTest;
import algebra.fields.abstractfieldparameters.AbstractFp12_2Over3Over2_Parameters;

public class GenericCurveFieldsTest extends GenericFieldsTest {
  protected void testFrobeniusMap(
      final Fp12_2Over3Over2 element, final AbstractFp12_2Over3Over2_Parameters Fp12Parameters) {
    // x^(q^0) = x^1 = x
    assertTrue(element.FrobeniusMap(0).equals(element));

    // x^(q^i) = (x^q)^i
    Fp12_2Over3Over2 elementPowQ = element.pow(Fp12Parameters.FpParameters().modulus());
    for (int power = 1; power < 10; ++power) {
      final Fp12_2Over3Over2 elementPowQi = element.FrobeniusMap(power);
      assertTrue(elementPowQi.equals(elementPowQ));
      elementPowQ = elementPowQ.pow(Fp12Parameters.FpParameters().modulus());
    }
  }
}
