package algebra.curves.barreto_lynn_scott;

import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377BinaryReader;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G1Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G2Parameters;
import io.AbstractCurveReaderTest;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

public class BLSBinaryReaderTest extends AbstractCurveReaderTest {
  @Test
  public void bls12_377BinaryLoaderTest() throws IOException {
    final InputStream in = openTestFile("ec_test_data_bls12-377.bin");
    final BLS12_377BinaryReader binReader = new BLS12_377BinaryReader(in);
    testReaderAgainstData(
        new BLS12_377Fr(1), BLS12_377G1Parameters.ONE, BLS12_377G2Parameters.ONE, binReader);
  }
}
