package algebra.curves.barreto_naehrig;

import algebra.curves.GenericFieldsTest;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFq12Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFq2Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFq6Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFqParameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFrParameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFq12Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFq2Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFq6Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFqParameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFrParameters;
import org.junit.jupiter.api.Test;

public class BNFieldsTest extends GenericFieldsTest {

  // BN254a test cases
  @Test
  public void BN254aFqTest() {
    final BN254aFqParameters FqParameters = new BN254aFqParameters();

    FieldTest(FqParameters.ONE());
    FieldExpandedTest(FqParameters.ONE());
  }

  @Test
  public void BN254aFrTest() {
    final BN254aFrParameters FrParameters = new BN254aFrParameters();

    FieldTest(FrParameters.ONE());
    FieldExpandedTest(FrParameters.ONE());
  }

  @Test
  public void BN254aFq2Test() {
    final BN254aFq2Parameters Fq2Parameters = new BN254aFq2Parameters();

    FieldTest(Fq2Parameters.ONE());
  }

  @Test
  public void BN254aFq6Test() {
    final BN254aFq6Parameters Fq6Parameters = new BN254aFq6Parameters();

    FieldTest(Fq6Parameters.ONE());
  }

  @Test
  public void BN254aFq12Test() {
    final BN254aFq12Parameters Fq12Parameters = new BN254aFq12Parameters();

    FieldTest(Fq12Parameters.ONE());
    FrobeniusMapTest(Fq12Parameters.ONE(), Fq12Parameters);
  }

  // BN254b test cases

  @Test
  public void BN254bFqTest() {
    final BN254bFqParameters FqParameters = new BN254bFqParameters();

    FieldTest(FqParameters.ONE());
    FieldExpandedTest(FqParameters.ONE());
  }

  @Test
  public void BN254bFrTest() {
    final BN254bFrParameters FrParameters = new BN254bFrParameters();

    FieldTest(FrParameters.ONE());
    FieldExpandedTest(FrParameters.ONE());
  }

  @Test
  public void BN254bFq2Test() {
    final BN254bFq2Parameters Fq2Parameters = new BN254bFq2Parameters();

    FieldTest(Fq2Parameters.ONE());
  }

  @Test
  public void BN254bFq6Test() {
    final BN254bFq6Parameters Fq6Parameters = new BN254bFq6Parameters();

    FieldTest(Fq6Parameters.ONE());
  }

  @Test
  public void BN254bFq12Test() {
    final BN254bFq12Parameters Fq12Parameters = new BN254bFq12Parameters();

    FieldTest(Fq12Parameters.ONE());
    FrobeniusMapTest(Fq12Parameters.ONE(), Fq12Parameters);
  }
}
