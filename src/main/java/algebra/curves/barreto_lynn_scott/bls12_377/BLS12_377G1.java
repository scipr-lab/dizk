package algebra.curves.barreto_lynn_scott.bls12_377;

import algebra.curves.barreto_lynn_scott.BLSG1;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G1Parameters;

public class BLS12_377G1
    extends BLSG1<BLS12_377Fr, BLS12_377Fq, BLS12_377G1, BLS12_377G1Parameters> {

  public static final BLS12_377G1Parameters G1Parameters = new BLS12_377G1Parameters();

  public BLS12_377G1(final BLS12_377Fq X, final BLS12_377Fq Y, final BLS12_377Fq Z) {
    super(X, Y, Z, G1Parameters);
  }

  public BLS12_377G1 self() {
    return this;
  }

  public BLS12_377G1 construct(final BLS12_377Fq X, final BLS12_377Fq Y, final BLS12_377Fq Z) {
    return new BLS12_377G1(X, Y, Z);
  }
}
