package algebra.curves.barreto_lynn_scott.abstract_bls_parameters;

import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq12;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq2;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq6;
import algebra.curves.barreto_lynn_scott.BLSGT;

/** Generic class to represent the parameters defining a given BLS GT group */
public abstract class AbstractBLSGTParameters<
    BLSFqT extends BLSFq<BLSFqT>,
    BLSFq2T extends BLSFq2<BLSFqT, BLSFq2T>,
    BLSFq6T extends BLSFq6<BLSFqT, BLSFq2T, BLSFq6T>,
    BLSFq12T extends BLSFq12<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T>,
    BLSGTT extends BLSGT<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T, BLSGTT, BLSGTParametersT>,
    BLSGTParametersT extends
        AbstractBLSGTParameters<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T, BLSGTT, BLSGTParametersT>> {

  public abstract BLSGTT ONE();
}
