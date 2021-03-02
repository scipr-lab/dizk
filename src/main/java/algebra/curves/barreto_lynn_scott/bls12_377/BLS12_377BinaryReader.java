package algebra.curves.barreto_lynn_scott.bls12_377;

import algebra.curves.barreto_lynn_scott.BLSBinaryReader;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377Fq2Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377FqParameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G1Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G2Parameters;
import java.io.InputStream;

public class BLS12_377BinaryReader
    extends BLSBinaryReader<
        BLS12_377Fr,
        BLS12_377Fq,
        BLS12_377Fq2,
        BLS12_377G1,
        BLS12_377G2,
        BLS12_377FqParameters,
        BLS12_377Fq2Parameters,
        BLS12_377G1Parameters,
        BLS12_377G2Parameters> {
  public BLS12_377BinaryReader(InputStream inStream) {
    super(
        inStream,
        new BLS12_377FqParameters(),
        new BLS12_377Fq2Parameters(),
        new BLS12_377G1Parameters(),
        new BLS12_377G2Parameters());
  }
}
