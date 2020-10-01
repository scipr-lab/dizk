/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.abstract_bn_parameters;

import algebra.curves.barreto_naehrig.BNFields.BNFq;
import algebra.curves.barreto_naehrig.BNFields.BNFr;
import algebra.curves.barreto_naehrig.BNG1;

import java.util.ArrayList;

/**
 * Generic class to represent the parameters defining a given BN G1 group
 */
public abstract class AbstractBNG1Parameters<
        BNFrT extends BNFr<BNFrT>,
        BNFqT extends BNFq<BNFqT>,
        BNG1T extends BNG1<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
        BNG1ParametersT extends AbstractBNG1Parameters<BNFrT, BNFqT, BNG1T, BNG1ParametersT>> {

    public abstract BNG1T ZERO();

    public abstract BNG1T ONE();

    public abstract BNFrT zeroFr();

    public abstract BNFrT oneFr();

    public abstract ArrayList<Integer> fixedBaseWindowTable();
}
