package algebra.curves.barreto_naehrig.bn254a;

import algebra.curves.barreto_naehrig.BNBinaryReader;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq2;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFq2Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aFqParameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import java.io.InputStream;

public class BN254aBinaryReader
    extends BNBinaryReader<
        BN254aFr,
        BN254aFq,
        BN254aFq2,
        BN254aG1,
        BN254aG2,
        BN254aFqParameters,
        BN254aFq2Parameters,
        BN254aG1Parameters,
        BN254aG2Parameters> {
  public BN254aBinaryReader(InputStream inStream) {
    super(
        inStream,
        new BN254aFqParameters(),
        new BN254aFq2Parameters(),
        new BN254aG1Parameters(),
        new BN254aG2Parameters());
  }
}
