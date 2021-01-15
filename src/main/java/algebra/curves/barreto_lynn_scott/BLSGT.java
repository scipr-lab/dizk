package algebra.curves.barreto_lynn_scott;

import algebra.curves.AbstractGT;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq12;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq2;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq6;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSGTParameters;
import java.math.BigInteger;

public abstract class BLSGT<
        BLSFqT extends BLSFq<BLSFqT>,
        BLSFq2T extends BLSFq2<BLSFqT, BLSFq2T>,
        BLSFq6T extends BLSFq6<BLSFqT, BLSFq2T, BLSFq6T>,
        BLSFq12T extends BLSFq12<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T>,
        BLSGTT extends BLSGT<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T, BLSGTT, BLSGTParametersT>,
        BLSGTParametersT extends
            AbstractBLSGTParameters<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T, BLSGTT, BLSGTParametersT>>
    extends AbstractGT<BLSGTT> {
  public final BLSGTParametersT GTParameters;
  public final BLSFq12T element;

  public BLSGT(final BLSFq12T value, final BLSGTParametersT GTParameters) {
    this.element = value;
    this.GTParameters = GTParameters;
  }

  public abstract BLSGTT construct(final BLSFq12T element);

  public BLSGTT add(final BLSGTT other) {
    return this.construct(this.element.mul(other.element));
  }

  public BLSGTT mul(final BigInteger other) {
    return this.construct(this.element.pow(other));
  }

  public BLSGTT one() {
    return this.GTParameters.ONE();
  }

  public BLSGTT negate() {
    return this.construct(this.element.unitaryInverse());
  }

  public boolean equals(final BLSGTT other) {
    return this.element.equals(other.element);
  }

  public String toString() {
    return this.element.toString();
  }
}
