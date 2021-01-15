package algebra.curves.barreto_naehrig;

import algebra.curves.GenericCurveFieldsTest;
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
import algebra.fields.Fp;
import algebra.fields.Fp12_2Over3Over2;
import algebra.fields.Fp2;
import algebra.fields.Fp6_3Over2;
import org.junit.jupiter.api.Test;

public class BNFieldsTest extends GenericCurveFieldsTest {
  // BN254a test cases
  @Test
  public void BN254aFqTest() {
    final BN254aFqParameters FqParameters = new BN254aFqParameters();
    final Fp ffactory = FqParameters.ONE();
    final Fp a = ffactory.construct(23456789);
    final Fp b = ffactory.construct(927243);
    final Fp c = ffactory.construct(652623743);

    testField(ffactory, a, b, c);
    testFieldExpanded(ffactory);
  }

  @Test
  public void BN254aFrTest() {
    final BN254aFrParameters FrParameters = new BN254aFrParameters();
    final Fp ffactory = FrParameters.ONE();
    final Fp a = ffactory.construct(23456789);
    final Fp b = ffactory.construct(927243);
    final Fp c = ffactory.construct(652623743);

    testField(ffactory, a, b, c);
    testFieldExpanded(FrParameters.ONE());
  }

  @Test
  public void BN254aFq2Test() {
    final BN254aFq2Parameters Fq2Parameters = new BN254aFq2Parameters();
    final Fp2 ffactory = Fq2Parameters.ONE();
    final Fp2 a = ffactory.construct(23456789, 23131123);
    final Fp2 b = ffactory.construct(927243, 54646465);
    final Fp2 c = ffactory.construct(652623743, 7867867);

    testField(ffactory, a, b, c);
  }

  @Test
  public void BN254aFq6Test() {
    final BN254aFq6Parameters Fq6Parameters = new BN254aFq6Parameters();
    final Fp2 Fp2factory = Fq6Parameters.Fq2Parameters.ONE();
    final Fp2 c0 = Fp2factory.construct(23456789, 23131123);
    final Fp2 c1 = Fp2factory.construct(927243, 54646465);
    final Fp2 c2 = Fp2factory.construct(652623743, 7867867);

    final Fp6_3Over2 Fp6factory = Fq6Parameters.ONE();
    final Fp6_3Over2 a = Fp6factory.construct(c0, c1, c0);
    final Fp6_3Over2 b = Fp6factory.construct(c1, c2, c1);
    final Fp6_3Over2 c = Fp6factory.construct(c2, c1, c2);

    testField(Fp6factory, a, b, c);
  }

  @Test
  public void BN254aFq12Test() {
    final BN254aFq12Parameters Fq12Parameters = new BN254aFq12Parameters();
    final BN254aFq6Parameters Fq6Parameters = Fq12Parameters.Fq6Parameters;
    final Fp2 Fp2factory = Fq6Parameters.Fq2Parameters.ONE();
    final Fp2 c00 = Fp2factory.construct(23456789, 23131123);
    final Fp2 c01 = Fp2factory.construct(927243, 54646465);
    final Fp2 c02 = Fp2factory.construct(652623743, 7867867);

    final Fp6_3Over2 Fp6factory = Fq6Parameters.ONE();
    final Fp6_3Over2 c0 = Fp6factory.construct(c00, c01, c00);
    final Fp6_3Over2 c1 = Fp6factory.construct(c01, c02, c01);

    final Fp12_2Over3Over2 Fp12factory = Fq12Parameters.ONE();
    final Fp12_2Over3Over2 a = Fp12factory.construct(c0, c1);
    final Fp12_2Over3Over2 b = Fp12factory.construct(c1, c1);
    final Fp12_2Over3Over2 c = Fp12factory.construct(c0, c0);

    testField(Fp12factory, a, b, c);
    testFrobeniusMap(Fp12factory, Fq12Parameters);
    testMulBy024(Fp12factory, Fq12Parameters);
  }

  // BN254b test cases
  @Test
  public void BN254bFqTest() {
    final BN254bFqParameters FqParameters = new BN254bFqParameters();
    final Fp ffactory = FqParameters.ONE();
    final Fp a = ffactory.construct(23456789);
    final Fp b = ffactory.construct(927243);
    final Fp c = ffactory.construct(652623743);

    testField(ffactory, a, b, c);
    testFieldExpanded(ffactory);
  }

  @Test
  public void BN254bFrTest() {
    final BN254bFrParameters FrParameters = new BN254bFrParameters();
    final Fp ffactory = FrParameters.ONE();
    final Fp a = ffactory.construct(23456789);
    final Fp b = ffactory.construct(927243);
    final Fp c = ffactory.construct(652623743);

    testField(ffactory, a, b, c);
    testFieldExpanded(FrParameters.ONE());
  }

  @Test
  public void BN254bFq2Test() {
    final BN254bFq2Parameters Fq2Parameters = new BN254bFq2Parameters();
    final Fp2 ffactory = Fq2Parameters.ONE();
    final Fp2 a = ffactory.construct(23456789, 23131123);
    final Fp2 b = ffactory.construct(927243, 54646465);
    final Fp2 c = ffactory.construct(652623743, 7867867);

    testField(ffactory, a, b, c);
  }

  @Test
  public void BN254bFq6Test() {
    final BN254bFq6Parameters Fq6Parameters = new BN254bFq6Parameters();
    final Fp2 Fp2factory = Fq6Parameters.Fq2Parameters.ONE();
    final Fp2 c0 = Fp2factory.construct(23456789, 23131123);
    final Fp2 c1 = Fp2factory.construct(927243, 54646465);
    final Fp2 c2 = Fp2factory.construct(652623743, 7867867);

    final Fp6_3Over2 Fp6factory = Fq6Parameters.ONE();
    final Fp6_3Over2 a = Fp6factory.construct(c0, c1, c0);
    final Fp6_3Over2 b = Fp6factory.construct(c1, c2, c1);
    final Fp6_3Over2 c = Fp6factory.construct(c2, c1, c2);

    testField(Fp6factory, a, b, c);
  }

  @Test
  public void BN254bFq12Test() {
    final BN254bFq12Parameters Fq12Parameters = new BN254bFq12Parameters();
    final BN254bFq6Parameters Fq6Parameters = Fq12Parameters.Fq6Parameters;
    final Fp2 Fp2factory = Fq6Parameters.Fq2Parameters.ONE();
    final Fp2 c00 = Fp2factory.construct(23456789, 23131123);
    final Fp2 c01 = Fp2factory.construct(927243, 54646465);
    final Fp2 c02 = Fp2factory.construct(652623743, 7867867);

    final Fp6_3Over2 Fp6factory = Fq6Parameters.ONE();
    final Fp6_3Over2 c0 = Fp6factory.construct(c00, c01, c00);
    final Fp6_3Over2 c1 = Fp6factory.construct(c01, c02, c01);

    final Fp12_2Over3Over2 Fp12factory = Fq12Parameters.ONE();
    final Fp12_2Over3Over2 a = Fp12factory.construct(c0, c1);
    final Fp12_2Over3Over2 b = Fp12factory.construct(c1, c1);
    final Fp12_2Over3Over2 c = Fp12factory.construct(c0, c0);

    testField(Fp12factory, a, b, c);
    testFrobeniusMap(Fp12factory, Fq12Parameters);
    testMulBy024(Fp12factory, Fq12Parameters);
  }
}
