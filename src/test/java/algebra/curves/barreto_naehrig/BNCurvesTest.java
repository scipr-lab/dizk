package algebra.curves.barreto_naehrig;

import algebra.curves.GenericCurvesTest;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG1Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG2Parameters;
import org.junit.jupiter.api.Test;

public class BNCurvesTest extends GenericCurvesTest {
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
}
