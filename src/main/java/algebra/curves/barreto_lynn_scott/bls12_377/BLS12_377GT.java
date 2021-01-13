package algebra.curves.barreto_lynn_scott.bls12_377;

import algebra.curves.barreto_lynn_scott.BLSGT;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq12;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq6;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377GTParameters;

public class BLS12_377GT
    extends BLSGT<
        BLS12_377Fq,
        BLS12_377Fq2,
        BLS12_377Fq6,
        BLS12_377Fq12,
        BLS12_377GT,
        BLS12_377GTParameters> {

  private static final BLS12_377GTParameters GTParameters = new BLS12_377GTParameters();

  public BLS12_377GT(final BLS12_377Fq12 value) {
    super(value, GTParameters);
  }

  public BLS12_377GT construct(final BLS12_377Fq12 element) {
    return new BLS12_377GT(element);
  }
}
