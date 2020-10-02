/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254a;

import algebra.curves.barreto_naehrig.BNPairing;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.*;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aGTParameters;

public class BN254aPairing
    extends BNPairing<
        BN254aFr,
        BN254aFq,
        BN254aFq2,
        BN254aFq6,
        BN254aFq12,
        BN254aG1,
        BN254aG2,
        BN254aGT,
        BN254aG1Parameters,
        BN254aG2Parameters,
        BN254aGTParameters,
        BN254aPublicParameters> {

  private static final BN254aPublicParameters publicParameters = new BN254aPublicParameters();

  public BN254aPublicParameters publicParameters() {
    return publicParameters;
  }

  public BN254aGT reducedPairing(final BN254aG1 P, final BN254aG2 Q) {
    final BN254aFq12 f = atePairing(P, Q);
    final BN254aFq12 result = finalExponentiation(f);
    return new BN254aGT(result);
  }
}
