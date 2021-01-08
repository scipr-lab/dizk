package algebra.curves.barreto_lynn_scott.abstract_bls_parameters;

import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq2;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFr;
import algebra.curves.barreto_lynn_scott.BLSG2;
import java.util.ArrayList;

/** Generic class to represent the parameters defining a given BLS G2 group */
public abstract class AbstractBLSG2Parameters<
    BLSFrT extends BLSFr<BLSFrT>,
    BLSFqT extends BLSFq<BLSFqT>,
    BLSFq2T extends BLSFq2<BLSFqT, BLSFq2T>,
    BLSG2T extends BLSG2<BLSFrT, BLSFqT, BLSFq2T, BLSG2T, BLSG2ParametersT>,
    BLSG2ParametersT extends AbstractBLSG2Parameters<BLSFrT, BLSFqT, BLSFq2T, BLSG2T, BLSG2ParametersT>> {

  public abstract BLSG2T ZERO();

  public abstract BLSG2T ONE();

  public abstract BLSFrT zeroFr();

  public abstract BLSFrT oneFr();

  public abstract ArrayList<Integer> fixedBaseWindowTable();
}
