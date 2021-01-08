/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_lynn_scott.bls12_377;

import algebra.curves.barreto_lynn_scott.BLSPairing;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.*;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G1Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G2Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377GTParameters;

public class BLS12_377Pairing
    extends BLSPairing<
        BLS12_377Fr,
        BLS12_377Fq,
        BLS12_377Fq2,
        BLS12_377Fq6,
        BLS12_377Fq12,
        BLS12_377G1,
        BLS12_377G2,
        BLS12_377GT,
        BLS12_377G1Parameters,
        BLS12_377G2Parameters,
        BLS12_377GTParameters,
        BLS12_377PublicParameters> {

  private static final BLS12_377PublicParameters publicParameters = new BLS12_377PublicParameters();

  public BLS12_377PublicParameters publicParameters() {
    return publicParameters;
  }

  public BLS12_377GT reducedPairing(final BLS12_377G1 P, final BLS12_377G2 Q) {
    final BLS12_377Fq12 f = atePairing(P, Q);
    final BLS12_377Fq12 result = finalExponentiation(f);
    return new BLS12_377GT(result);
  }
}
