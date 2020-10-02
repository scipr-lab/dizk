/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254a.bn254a_parameters;

import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG2Parameters;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq2;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.BN254aG2;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class BN254aG2Parameters
    extends AbstractBNG2Parameters<BN254aFr, BN254aFq, BN254aFq2, BN254aG2, BN254aG2Parameters>
    implements Serializable {

  public static final BN254aG2 ZERO = new BN254aG2(BN254aFq2.ZERO, BN254aFq2.ONE, BN254aFq2.ZERO);
  public static final BN254aG2 ONE =
      new BN254aG2(
          new BN254aFq2(
              new BN254aFq(
                  "10857046999023057135944570762232829481370756359578518086990519993285655852781"),
              new BN254aFq(
                  "11559732032986387107991004021392285783925812861821192530917403151452391805634")),
          new BN254aFq2(
              new BN254aFq(
                  "8495653923123431417604973247489272438418190587263600148770280649306958101930"),
              new BN254aFq(
                  "4082367875863433681332203403145435568316851327593401208105741076214120093531")),
          new BN254aFq2(1, 0));
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

  public BN254aG2 ZERO() {
    return ZERO;
  }

  public BN254aG2 ONE() {
    return ONE;
  }

  public BN254aFr zeroFr() {
    return BN254aFr.ZERO;
  }

  public BN254aFr oneFr() {
    return BN254aFr.ONE;
  }

  public ArrayList<Integer> fixedBaseWindowTable() {
    return fixedBaseWindowTable;
  }
}
