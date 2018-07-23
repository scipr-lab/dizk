/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254a;

import algebra.curves.barreto_naehrig.BNGT;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq12;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq2;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq6;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aGTParameters;

public class BN254aGT extends
        BNGT<BN254aFq, BN254aFq2, BN254aFq6, BN254aFq12, BN254aGT, BN254aGTParameters> {

    private static final BN254aGTParameters GTParameters = new BN254aGTParameters();

    public BN254aGT(final BN254aFq12 value) {
        super(value, GTParameters);
    }

    public BN254aGT construct(final BN254aFq12 element) {
        return new BN254aGT(element);
    }
}
