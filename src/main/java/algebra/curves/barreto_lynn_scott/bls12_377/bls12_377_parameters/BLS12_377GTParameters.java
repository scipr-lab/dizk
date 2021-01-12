package algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters;

import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSGTParameters;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq12;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq6;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377GT;

public class BLS12_377GTParameters
    extends AbstractBLSGTParameters<
        BLS12_377Fq, BLS12_377Fq2, BLS12_377Fq6, BLS12_377Fq12, BLS12_377GT, BLS12_377GTParameters> {

  public static final BLS12_377GT ONE = new BLS12_377GT(BLS12_377Fq12.ONE);

  public BLS12_377GT ONE() {
    return ONE;
  }
}
