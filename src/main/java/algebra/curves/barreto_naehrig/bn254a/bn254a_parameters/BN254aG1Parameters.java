/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254a.bn254a_parameters;

import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG1Parameters;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.BN254aG1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class BN254aG1Parameters
        extends AbstractBNG1Parameters<BN254aFr, BN254aFq, BN254aG1, BN254aG1Parameters>
        implements Serializable {

    public static final BN254aG1 ZERO = new BN254aG1(BN254aFq.ZERO, BN254aFq.ONE, BN254aFq.ZERO);
    public static final BN254aG1 ONE = new BN254aG1(BN254aFq.ONE, new BN254aFq(2), BN254aFq.ONE);
    public static final ArrayList<Integer> fixedBaseWindowTable = new ArrayList<>(
            Arrays.asList(
                    1, // window 1 is unbeaten in [-inf, 4.99]
                    5, // window 2 is unbeaten in [4.99, 10.99]
                    11, // window 3 is unbeaten in [10.99, 32.29]
                    32, // window 4 is unbeaten in [32.29, 55.23]
                    55, // window 5 is unbeaten in [55.23, 162.03]
                    162, // window 6 is unbeaten in [162.03, 360.15]
                    360, // window 7 is unbeaten in [360.15, 815.44]
                    815, // window 8 is unbeaten in [815.44, 2373.07]
                    2373, // window 9 is unbeaten in [2373.07, 6977.75]
                    6978, // window 10 is unbeaten in [6977.75, 7122.23]
                    7122, // window 11 is unbeaten in [7122.23, 57818.46]
                    0, // window 12 is never the best
                    57818, // window 13 is unbeaten in [57818.46, 169679.14]
                    0, // window 14 is never the best
                    169679, // window 15 is unbeaten in [169679.14, 439758.91]
                    439759, // window 16 is unbeaten in [439758.91, 936073.41]
                    936073, // window 17 is unbeaten in [936073.41, 4666554.74]
                    0, // window 18 is never the best
                    4666555, // window 19 is unbeaten in [4666554.74, 7580404.42]
                    7580404, // window 20 is unbeaten in [7580404.42, 34552892.20]
                    0, // window 21 is never the best
                    34552892 // window 22 is unbeaten in [34552892.20, inf]
            )
    );

    public BN254aG1 ZERO() {
        return ZERO;
    }

    public BN254aG1 ONE() {
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
