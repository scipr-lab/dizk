package algebra.curves.barreto_lynn_scott.abstract_bls_parameters;

import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFr;
import algebra.curves.barreto_lynn_scott.BLSG1;
import java.util.ArrayList;

/** Generic class to represent the parameters defining a given BLS G1 group */
public abstract class AbstractBLSG1Parameters<
    BLSFrT extends BLSFr<BLSFrT>,
    BLSFqT extends BLSFq<BLSFqT>,
    BLSG1T extends BLSG1<BLSFrT, BLSFqT, BLSG1T, BLSG1ParametersT>,
    BLSG1ParametersT extends AbstractBLSG1Parameters<BLSFrT, BLSFqT, BLSG1T, BLSG1ParametersT>> {

  public abstract BLSG1T ZERO();

  public abstract BLSG1T ONE();

  public abstract BLSFrT zeroFr();

  public abstract BLSFrT oneFr();

  public abstract ArrayList<Integer> fixedBaseWindowTable();
}
