package algebra.curves.barreto_lynn_scott;

import algebra.curves.GenericCurveFieldsTest;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377Fq12Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377Fq2Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377Fq6Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377FqParameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377FrParameters;
import algebra.fields.Fp;
import algebra.fields.Fp12_2Over3Over2;
import algebra.fields.Fp2;
import algebra.fields.Fp6_3Over2;
import org.junit.jupiter.api.Test;

public class BLSFieldsTest extends GenericCurveFieldsTest {
  // BLS12_377 test cases
  //
  // The values taken below are arbitrary.
  // Randomized tests case can be used by assigning a,b,c with ffactory.random()
  @Test
  public void BLS12_377FqTest() {
    final BLS12_377FqParameters FqParameters = new BLS12_377FqParameters();
    final Fp ffactory = FqParameters.ONE();
    final Fp a = ffactory.construct(23456789);
    final Fp b = ffactory.construct(927243);
    final Fp c = ffactory.construct(652623743);

    testField(ffactory, a, b, c);
    testFieldExpanded(ffactory);
  }

  @Test
  public void BLS12_377FrTest() {
    final BLS12_377FrParameters FrParameters = new BLS12_377FrParameters();
    final Fp ffactory = FrParameters.ONE();
    final Fp a = ffactory.construct(23456789);
    final Fp b = ffactory.construct(927243);
    final Fp c = ffactory.construct(652623743);

    testField(ffactory, a, b, c);
    testFieldExpanded(FrParameters.ONE());
  }

  @Test
  public void BLS12_377Fq2Test() {
    final BLS12_377Fq2Parameters Fq2Parameters = new BLS12_377Fq2Parameters();
    final Fp2 ffactory = Fq2Parameters.ONE();
    final Fp2 a = ffactory.construct(23456789, 23131123);
    final Fp2 b = ffactory.construct(927243, 54646465);
    final Fp2 c = ffactory.construct(652623743, 7867867);

    testField(ffactory, a, b, c);
  }

  @Test
  public void BLS12_377Fq6Test() {
    final BLS12_377Fq6Parameters Fq6Parameters = new BLS12_377Fq6Parameters();
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
  public void BLS12_377Fq12Test() {
    final BLS12_377Fq12Parameters Fq12Parameters = new BLS12_377Fq12Parameters();
    final BLS12_377Fq6Parameters Fq6Parameters = Fq12Parameters.Fq6Parameters;
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
