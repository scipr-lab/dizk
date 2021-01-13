package algebra.curves.barreto_lynn_scott;

import algebra.curves.GenericFieldsTest;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377Fq12Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377Fq2Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377Fq6Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377FqParameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377FrParameters;
import org.junit.jupiter.api.Test;

public class BLSFieldsTest extends GenericFieldsTest {
  // BLS12_377 test cases
  @Test
  public void BLS12_377FqTest() {
    final BLS12_377FqParameters FqParameters = new BLS12_377FqParameters();

    FieldTest(FqParameters.ONE());
    FieldExpandedTest(FqParameters.ONE());
  }

  @Test
  public void BLS12_377FrTest() {
    final BLS12_377FrParameters FrParameters = new BLS12_377FrParameters();

    FieldTest(FrParameters.ONE());
    FieldExpandedTest(FrParameters.ONE());
  }

  @Test
  public void BLS12_377Fq2Test() {
    final BLS12_377Fq2Parameters Fq2Parameters = new BLS12_377Fq2Parameters();

    FieldTest(Fq2Parameters.ONE());
  }

  @Test
  public void BLS12_377Fq6Test() {
    final BLS12_377Fq6Parameters Fq6Parameters = new BLS12_377Fq6Parameters();

    FieldTest(Fq6Parameters.ONE());
  }

  @Test
  public void BLS12_377Fq12Test() {
    final BLS12_377Fq12Parameters Fq12Parameters = new BLS12_377Fq12Parameters();

    FieldTest(Fq12Parameters.ONE());
    FrobeniusMapTest(Fq12Parameters.ONE(), Fq12Parameters);
  }
}
