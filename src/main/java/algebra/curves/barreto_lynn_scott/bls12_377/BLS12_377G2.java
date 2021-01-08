/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_lynn_scott.bls12_377;

import algebra.curves.barreto_lynn_scott.BLSG2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G2Parameters;

public class BLS12_377G2 extends BLSG2<BLS12_377Fr, BLS12_377Fq, BLS12_377Fq2, BLS12_377G2, BLS12_377G2Parameters> {

  private static final BLS12_377G2Parameters G2Parameters = new BLS12_377G2Parameters();

  public BLS12_377G2(final BLS12_377Fq2 X, final BLS12_377Fq2 Y, final BLS12_377Fq2 Z) {
    super(X, Y, Z, G2Parameters);
  }

  public BLS12_377G2 self() {
    return this;
  }

  public BLS12_377G2 construct(final BLS12_377Fq2 X, final BLS12_377Fq2 Y, final BLS12_377Fq2 Z) {
    return new BLS12_377G2(X, Y, Z);
  }
}
