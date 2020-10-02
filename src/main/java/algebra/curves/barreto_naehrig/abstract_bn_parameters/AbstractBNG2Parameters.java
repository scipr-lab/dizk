/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.abstract_bn_parameters;

import algebra.curves.barreto_naehrig.BNFields.BNFq;
import algebra.curves.barreto_naehrig.BNFields.BNFq2;
import algebra.curves.barreto_naehrig.BNFields.BNFr;
import algebra.curves.barreto_naehrig.BNG2;
import java.util.ArrayList;

/** Generic class to represent the parameters defining a given BN G2 group */
public abstract class AbstractBNG2Parameters<
    BNFrT extends BNFr<BNFrT>,
    BNFqT extends BNFq<BNFqT>,
    BNFq2T extends BNFq2<BNFqT, BNFq2T>,
    BNG2T extends BNG2<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
    BNG2ParametersT extends AbstractBNG2Parameters<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>> {

  public abstract BNG2T ZERO();

  public abstract BNG2T ONE();

  public abstract BNFrT zeroFr();

  public abstract BNFrT oneFr();

  public abstract ArrayList<Integer> fixedBaseWindowTable();
}
