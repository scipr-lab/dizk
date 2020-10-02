/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254b;

import algebra.curves.barreto_naehrig.BNG1;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFr;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG1Parameters;

public class BN254bG1 extends BNG1<BN254bFr, BN254bFq, BN254bG1, BN254bG1Parameters> {

  public static final BN254bG1Parameters G1Parameters = new BN254bG1Parameters();

  public BN254bG1(final BN254bFq X, final BN254bFq Y, final BN254bFq Z) {
    super(X, Y, Z, G1Parameters);
  }

  public BN254bG1 self() {
    return this;
  }

  public BN254bG1 construct(final BN254bFq X, final BN254bFq Y, final BN254bFq Z) {
    return new BN254bG1(X, Y, Z);
  }
}
