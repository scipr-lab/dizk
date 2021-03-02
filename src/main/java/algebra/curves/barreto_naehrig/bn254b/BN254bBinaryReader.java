package algebra.curves.barreto_naehrig.bn254b;

import algebra.curves.barreto_naehrig.BNBinaryReader;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq2;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFr;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFq2Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFqParameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG1Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG2Parameters;
import java.io.InputStream;

public class BN254bBinaryReader
    extends BNBinaryReader<
        BN254bFr,
        BN254bFq,
        BN254bFq2,
        BN254bG1,
        BN254bG2,
        BN254bFqParameters,
        BN254bFq2Parameters,
        BN254bG1Parameters,
        BN254bG2Parameters> {
  public BN254bBinaryReader(InputStream inStream) {
    super(
        inStream,
        new BN254bFqParameters(),
        new BN254bFq2Parameters(),
        new BN254bG1Parameters(),
        new BN254bG2Parameters());
  }
}
