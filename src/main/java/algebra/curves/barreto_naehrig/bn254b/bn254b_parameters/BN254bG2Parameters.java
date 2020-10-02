/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254b.bn254b_parameters;

import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG2Parameters;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq2;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFr;
import algebra.curves.barreto_naehrig.bn254b.BN254bG2;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class BN254bG2Parameters
    extends AbstractBNG2Parameters<BN254bFr, BN254bFq, BN254bFq2, BN254bG2, BN254bG2Parameters>
    implements Serializable {

  public static final BN254bG2 ZERO = new BN254bG2(BN254bFq2.ZERO, BN254bFq2.ONE, BN254bFq2.ZERO);
  public static final BN254bG2 ONE =
      new BN254bG2(
          new BN254bFq2(
              new BN254bFq(
                  "5079609021644061220823125455176595404585595222550497085336148917057303814280"),
              new BN254bFq(
                  "17041702796731330387964445988262372396109649438458604288057852904572562782012")),
          new BN254bFq2(
              new BN254bFq(
                  "942715487274747883577565654509921632861746790272414915686456132777971395793"),
              new BN254bFq(
                  "10226306760398461226809375285904929090012967024146452193615342198255969027831")),
          new BN254bFq2(1, 0));
  public static final ArrayList<Integer> fixedBaseWindowTable =
      new ArrayList<>(
          Arrays.asList(
              1, // window 1 is unbeaten in [-inf, 5.10]
              5, // window 2 is unbeaten in [5.10, 10.43]
              10, // window 3 is unbeaten in [10.43, 25.28]
              25, // window 4 is unbeaten in [25.28, 59.00]
              59, // window 5 is unbeaten in [59.00, 154.03]
              154, // window 6 is unbeaten in [154.03, 334.25]
              334, // window 7 is unbeaten in [334.25, 742.58]
              743, // window 8 is unbeaten in [742.58, 2034.40]
              2034, // window 9 is unbeaten in [2034.40, 4987.56]
              4988, // window 10 is unbeaten in [4987.56, 8888.27]
              8888, // window 11 is unbeaten in [8888.27, 26271.13]
              26271, // window 12 is unbeaten in [26271.13, 39768.20]
              39768, // window 13 is unbeaten in [39768.20, 106275.75]
              106276, // window 14 is unbeaten in [106275.75, 141703.40]
              141703, // window 15 is unbeaten in [141703.40, 462422.97]
              462423, // window 16 is unbeaten in [462422.97, 926871.84]
              926872, // window 17 is unbeaten in [926871.84, 4873049.17]
              0, // window 18 is never the best
              4873049, // window 19 is unbeaten in [4873049.17, 5706707.88]
              5706708, // window 20 is unbeaten in [5706707.88, 31673814.95]
              0, // window 21 is never the best
              31673815 // window 22 is unbeaten in [31673814.95, inf]
              ));

  public BN254bG2 ZERO() {
    return ZERO;
  }

  public BN254bG2 ONE() {
    return ONE;
  }

  public BN254bFr zeroFr() {
    return BN254bFr.ZERO;
  }

  public BN254bFr oneFr() {
    return BN254bFr.ONE;
  }

  public ArrayList<Integer> fixedBaseWindowTable() {
    return fixedBaseWindowTable;
  }
}
