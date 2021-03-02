package algebra.curves.barreto_naehrig;

import static algebra.curves.barreto_naehrig.BNFields.BNFq;
import static algebra.curves.barreto_naehrig.BNFields.BNFq2;
import static algebra.curves.barreto_naehrig.BNFields.BNFr;

import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNFq2Parameters;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNFqParameters;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG1Parameters;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG2Parameters;
import algebra.fields.Fp;
import algebra.fields.Fp2;
import io.BinaryCurveReader;
import java.io.IOException;
import java.io.InputStream;

/** Base class of binary reader for all BN curves. */
public class BNBinaryReader<
        BNFrT extends BNFr<BNFrT>,
        BNFqT extends BNFq<BNFqT>,
        BNFq2T extends BNFq2<BNFqT, BNFq2T>,
        BNG1T extends BNG1<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
        BNG2T extends BNG2<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
        BNFqParametersT extends AbstractBNFqParameters,
        BNFq2ParametersT extends AbstractBNFq2Parameters,
        BNG1ParametersT extends AbstractBNG1Parameters<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
        BNG2ParametersT extends
            AbstractBNG2Parameters<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>>
    extends BinaryCurveReader<BNFrT, BNG1T, BNG2T> {

  private final BNFrT FrOne;
  private final int FrSizeBytes;

  private final BNFqParametersT FqParams;
  private final BNFqT FqOne;
  private final int FqSizeBytes;

  private final BNG1ParametersT G1Params;
  private final BNG1T G1One;

  private final BNG2ParametersT G2Params;
  private final BNG2T G2One;

  private final BNFq2ParametersT Fq2Params;
  private final BNFq2T Fq2One;

  public BNBinaryReader(
      InputStream inStream_,
      BNFqParametersT FqParams_,
      BNFq2ParametersT Fq2Params_,
      BNG1ParametersT G1Params_,
      BNG2ParametersT G2Params_) {
    super(inStream_);

    FrOne = G1Params_.oneFr();
    FrSizeBytes = computeSizeBytes(FrOne);

    FqParams = FqParams_;
    FqOne = G1Params_.ONE().X.one();
    FqSizeBytes = computeSizeBytes(FqOne);

    G1Params = G1Params_;
    G1One = G1Params.ONE();

    G2Params = G2Params_;
    G2One = G2Params.ONE();

    Fq2Params = Fq2Params_;
    Fq2One = G2Params.ONE().X.one();
  }

  @Override
  public BNFrT readFr() throws IOException {
    return FrOne.construct(readBigInteger(FrSizeBytes));
  }

  @Override
  public BNG1T readG1() throws IOException {
    return G1One.construct(readFq(), readFq(), FqOne);
  }

  @Override
  public BNG2T readG2() throws IOException {
    final BNFq2T X = readFq2();
    final BNFq2T Y = readFq2();
    return G2One.construct(X, Y, Y.one());
  }

  protected BNFqT readFq() throws IOException {
    return FqOne.construct(readBigInteger(FqSizeBytes));
  }

  protected Fp readFqAsFp() throws IOException {
    return new Fp(readBigInteger(FqSizeBytes), FqParams);
  }

  protected BNFq2T readFq2() throws IOException {
    final Fp c0 = readFqAsFp();
    final Fp c1 = readFqAsFp();
    final Fp2 fp2 = new Fp2(c0, c1, Fq2Params);
    return Fq2One.construct(fp2);
  }
}
