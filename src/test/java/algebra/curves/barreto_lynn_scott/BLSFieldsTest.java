package algebra.curves.barreto_lynn_scott;

import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377FqParameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377FrParameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377Fq2Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377Fq6Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377Fq12Parameters;

import org.junit.jupiter.api.Test;
import algebra.curves.GenericFieldsTest;

public class BLSFieldsTest {
  final GenericFieldsTest gTest = new GenericFieldsTest();

  // BLS12_377 test cases
  @Test
  public void BLS12_377FqTest() {
    final BLS12_377FqParameters FqParameters = new BLS12_377FqParameters();

    gTest.testFieldOperations(FqParameters.ONE());
    gTest.testFieldExpandedOperations(FqParameters.ONE());
  }

  @Test
  public void BLS12_377FrTest() {
    final BLS12_377FrParameters FrParameters = new BLS12_377FrParameters();

    gTest.testFieldOperations(FrParameters.ONE());
    gTest.testFieldExpandedOperations(FrParameters.ONE());
  }

  @Test
  public void BLS12_377Fq2Test() {
    final BLS12_377Fq2Parameters Fq2Parameters = new BLS12_377Fq2Parameters();

    gTest.testFieldOperations(Fq2Parameters.ONE());
  }

  @Test
  public void BLS12_377Fq6Test() {
    final BLS12_377Fq6Parameters Fq6Parameters = new BLS12_377Fq6Parameters();

    gTest.testFieldOperations(Fq6Parameters.ONE());
  }

  @Test
  public void BLS12_377Fq12Test() {
    final BLS12_377Fq12Parameters Fq12Parameters = new BLS12_377Fq12Parameters();

    gTest.testFieldOperations(Fq12Parameters.ONE());
    gTest.verifyFrobeniusMap(Fq12Parameters.ONE(), Fq12Parameters);
  }
}
