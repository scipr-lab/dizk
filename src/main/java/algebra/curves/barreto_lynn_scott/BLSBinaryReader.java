package algebra.curves.barreto_lynn_scott;

import static algebra.curves.barreto_lynn_scott.BLSFields.BLSFq;
import static algebra.curves.barreto_lynn_scott.BLSFields.BLSFq2;
import static algebra.curves.barreto_lynn_scott.BLSFields.BLSFr;

import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSFq2Parameters;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSFqParameters;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSG1Parameters;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSG2Parameters;
import algebra.fields.Fp;
import algebra.fields.Fp2;
import io.BinaryCurveReader;
import java.io.IOException;
import java.io.InputStream;

/** Base class of binary reader for all BLS curves. */
public class BLSBinaryReader<
        BLSFrT extends BLSFr<BLSFrT>,
        BLSFqT extends BLSFq<BLSFqT>,
        BLSFq2T extends BLSFq2<BLSFqT, BLSFq2T>,
        BLSG1T extends BLSG1<BLSFrT, BLSFqT, BLSG1T, BLSG1ParametersT>,
        BLSG2T extends BLSG2<BLSFrT, BLSFqT, BLSFq2T, BLSG2T, BLSG2ParametersT>,
        BLSFqParametersT extends AbstractBLSFqParameters,
        BLSFq2ParametersT extends AbstractBLSFq2Parameters,
        BLSG1ParametersT extends AbstractBLSG1Parameters<BLSFrT, BLSFqT, BLSG1T, BLSG1ParametersT>,
        BLSG2ParametersT extends
            AbstractBLSG2Parameters<BLSFrT, BLSFqT, BLSFq2T, BLSG2T, BLSG2ParametersT>>
    extends BinaryCurveReader<BLSFrT, BLSG1T, BLSG2T> {

  private final BLSFrT FrOne;
  private final int FrSizeBytes;

  private final BLSFqParametersT FqParams;
  private final BLSFqT FqOne;
  private final int FqSizeBytes;

  private final BLSG1ParametersT G1Params;
  private final BLSG1T G1One;

  private final BLSG2ParametersT G2Params;
  private final BLSG2T G2One;

  private final BLSFq2ParametersT Fq2Params;
  private final BLSFq2T Fq2One;

  public BLSBinaryReader(
      InputStream inStream_,
      BLSFqParametersT FqParams_,
      BLSFq2ParametersT Fq2Params_,
      BLSG1ParametersT G1Params_,
      BLSG2ParametersT G2Params_) {
    super(inStream_);

    FrOne = G1Params_.oneFr();
    FrSizeBytes = computeSizeBytes(FrOne);
    System.out.println("FrSizeBytes: " + String.valueOf(FrSizeBytes));

    FqParams = FqParams_;
    FqOne = G1Params_.ONE().X.one();
    FqSizeBytes = computeSizeBytes(FqOne);
    System.out.println("FqSizeBytes: " + String.valueOf(FqSizeBytes));

    G1Params = G1Params_;
    G1One = G1Params.ONE();

    G2Params = G2Params_;
    G2One = G2Params.ONE();

    Fq2Params = Fq2Params_;
    Fq2One = G2Params.ONE().X.one();
  }

  @Override
  public BLSFrT readFr() throws IOException {
    return FrOne.construct(readBigInteger(FrSizeBytes));
  }

  @Override
  public BLSG1T readG1() throws IOException {
    return G1One.construct(readFq(), readFq(), FqOne);
  }

  @Override
  public BLSG2T readG2() throws IOException {
    final BLSFq2T X = readFq2();
    final BLSFq2T Y = readFq2();
    return G2One.construct(X, Y, Y.one());
  }

  protected BLSFqT readFq() throws IOException {
    return FqOne.construct(readBigInteger(FqSizeBytes));
  }

  protected Fp readFqAsFp() throws IOException {
    return new Fp(readBigInteger(FqSizeBytes), FqParams);
  }

  protected BLSFq2T readFq2() throws IOException {
    final Fp c0 = readFqAsFp();
    final Fp c1 = readFqAsFp();
    final Fp2 fp2 = new Fp2(c0, c1, Fq2Params);
    return Fq2One.construct(fp2);
  }
}
