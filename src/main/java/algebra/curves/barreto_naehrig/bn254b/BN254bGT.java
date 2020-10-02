/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254b;

import algebra.curves.barreto_naehrig.BNGT;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq12;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq2;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq6;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bGTParameters;

public class BN254bGT
    extends BNGT<BN254bFq, BN254bFq2, BN254bFq6, BN254bFq12, BN254bGT, BN254bGTParameters> {

  private static final BN254bGTParameters GTParameters = new BN254bGTParameters();

  public BN254bGT(final BN254bFq12 value) {
    super(value, GTParameters);
  }

  public BN254bGT construct(final BN254bFq12 element) {
    return new BN254bGT(element);
  }
}
