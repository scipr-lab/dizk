/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254a.bn254a_parameters;

import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNGTParameters;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq12;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq2;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq6;
import algebra.curves.barreto_naehrig.bn254a.BN254aGT;

public class BN254aGTParameters
        extends AbstractBNGTParameters<BN254aFq, BN254aFq2, BN254aFq6, BN254aFq12, BN254aGT, BN254aGTParameters> {

    public static final BN254aGT ONE = new BN254aGT(BN254aFq12.ONE);

    public BN254aGT ONE() {
        return ONE;
    }
}
