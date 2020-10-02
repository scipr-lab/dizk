/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254a;

import algebra.curves.barreto_naehrig.BNG2;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq2;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;

public class BN254aG2 extends BNG2<BN254aFr, BN254aFq, BN254aFq2, BN254aG2, BN254aG2Parameters> {

  private static final BN254aG2Parameters G2Parameters = new BN254aG2Parameters();

  public BN254aG2(final BN254aFq2 X, final BN254aFq2 Y, final BN254aFq2 Z) {
    super(X, Y, Z, G2Parameters);
  }

  public BN254aG2 self() {
    return this;
  }

  public BN254aG2 construct(final BN254aFq2 X, final BN254aFq2 Y, final BN254aFq2 Z) {
    return new BN254aG2(X, Y, Z);
  }
}
