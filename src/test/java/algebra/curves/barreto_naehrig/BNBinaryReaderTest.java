package algebra.curves.barreto_naehrig;

import algebra.curves.barreto_naehrig.bn254a.BN254aBinaryReader;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import io.AbstractCurveReaderTest;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

public class BNBinaryReaderTest extends AbstractCurveReaderTest {
  @Test
  public void BN254aBinaryLoaderTest() throws IOException {
    final InputStream in = openTestFile("ec_test_data_alt-bn128.bin");
    final BN254aBinaryReader binReader = new BN254aBinaryReader(in);

    testReaderAgainstData(
        new BN254aFr(1), BN254aG1Parameters.ONE, BN254aG2Parameters.ONE, binReader);
  }
}
