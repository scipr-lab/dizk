/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig;

import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFqParameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFrParameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFq2Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFq6Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFq12Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFqParameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFrParameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFq2Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFq6Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFq12Parameters;

import org.junit.jupiter.api.Test;
import algebra.curves.GenericFieldsTest;

public class BNFieldsTest {
  final GenericFieldsTest gTest = new GenericFieldsTest();

  // BN254a test cases
  @Test
  public void BN254aFqTest() {
    final BN254aFqParameters FqParameters = new BN254aFqParameters();

    gTest.testFieldOperations(FqParameters.ONE());
    gTest.testFieldExpandedOperations(FqParameters.ONE());
  }

  @Test
  public void BN254aFrTest() {
    final BN254aFrParameters FrParameters = new BN254aFrParameters();

    gTest.testFieldOperations(FrParameters.ONE());
    gTest.testFieldExpandedOperations(FrParameters.ONE());
  }

  @Test
  public void BN254aFq2Test() {
    final BN254aFq2Parameters Fq2Parameters = new BN254aFq2Parameters();

    gTest.testFieldOperations(Fq2Parameters.ONE());
  }

  @Test
  public void BN254aFq6Test() {
    final BN254aFq6Parameters Fq6Parameters = new BN254aFq6Parameters();

    gTest.testFieldOperations(Fq6Parameters.ONE());
  }

  @Test
  public void BN254aFq12Test() {
    final BN254aFq12Parameters Fq12Parameters = new BN254aFq12Parameters();

    gTest.testFieldOperations(Fq12Parameters.ONE());
    gTest.verifyFrobeniusMap(Fq12Parameters.ONE(), Fq12Parameters);
  }

  // BN254b test cases

  @Test
  public void BN254bFqTest() {
    final BN254bFqParameters FqParameters = new BN254bFqParameters();

    gTest.testFieldOperations(FqParameters.ONE());
    gTest.testFieldExpandedOperations(FqParameters.ONE());
  }

  @Test
  public void BN254bFrTest() {
    final BN254bFrParameters FrParameters = new BN254bFrParameters();

    gTest.testFieldOperations(FrParameters.ONE());
    gTest.testFieldExpandedOperations(FrParameters.ONE());
  }

  @Test
  public void BN254bFq2Test() {
    final BN254bFq2Parameters Fq2Parameters = new BN254bFq2Parameters();

    gTest.testFieldOperations(Fq2Parameters.ONE());
  }

  @Test
  public void BN254bFq6Test() {
    final BN254bFq6Parameters Fq6Parameters = new BN254bFq6Parameters();

    gTest.testFieldOperations(Fq6Parameters.ONE());
  }

  @Test
  public void BN254bFq12Test() {
    final BN254bFq12Parameters Fq12Parameters = new BN254bFq12Parameters();

    gTest.testFieldOperations(Fq12Parameters.ONE());
    gTest.verifyFrobeniusMap(Fq12Parameters.ONE(), Fq12Parameters);
  }
}
