/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.abstract_bn_parameters;

import algebra.curves.barreto_naehrig.BNFields.BNFq;
import algebra.curves.barreto_naehrig.BNFields.BNFq12;
import algebra.curves.barreto_naehrig.BNFields.BNFq2;
import algebra.curves.barreto_naehrig.BNFields.BNFq6;
import algebra.curves.barreto_naehrig.BNGT;

/** Generic class to represent the parameters defining a given BN GT group */
public abstract class AbstractBNGTParameters<
    BNFqT extends BNFq<BNFqT>,
    BNFq2T extends BNFq2<BNFqT, BNFq2T>,
    BNFq6T extends BNFq6<BNFqT, BNFq2T, BNFq6T>,
    BNFq12T extends BNFq12<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
    BNGTT extends BNGT<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
    BNGTParametersT extends
        AbstractBNGTParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>> {

  public abstract BNGTT ONE();
}
