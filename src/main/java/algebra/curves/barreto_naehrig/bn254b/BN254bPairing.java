/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254b;

import algebra.curves.barreto_naehrig.BNPairing;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.*;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG1Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG2Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bGTParameters;

public class BN254bPairing extends BNPairing<
        BN254bFr, BN254bFq, BN254bFq2, BN254bFq6, BN254bFq12, BN254bG1, BN254bG2, BN254bGT, BN254bG1Parameters, BN254bG2Parameters, BN254bGTParameters, BN254bPublicParameters> {

    private static final BN254bPublicParameters publicParameters = new BN254bPublicParameters();

    public BN254bPublicParameters publicParameters() {
        return publicParameters;
    }

    public BN254bGT reducedPairing(final BN254bG1 P, final BN254bG2 Q) {
        final BN254bFq12 f = atePairing(P, Q);
        final BN254bFq12 result = finalExponentiation(f);
        return new BN254bGT(result);
    }
}
