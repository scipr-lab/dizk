/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254a;

import algebra.curves.barreto_naehrig.BNG1;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;

/**
 * Class representing a specific BN group. This class is representing BOTH the group and a point in
 * the group. In fact, each point necessitates the X,Y,Z coordinates; and the group definition
 * additionally requires the `G1Parameters` attribute. Here all group elements are constructed by
 * `construct()` which is a wrapper around the "Group class" constructor.
 *
 * <p>That's why `BN254bG1Parameters.ONE` is treated as a "Group factory" here:
 * https://github.com/clearmatics/dizk/blob/develop/src/test/java/algebra/curves/CurvesTest.java#L93
 */
public class BN254aG1 extends BNG1<BN254aFr, BN254aFq, BN254aG1, BN254aG1Parameters> {

  public static final BN254aG1Parameters G1Parameters = new BN254aG1Parameters();

  public BN254aG1(final BN254aFq X, final BN254aFq Y, final BN254aFq Z) {
    super(X, Y, Z, G1Parameters);
  }

  public BN254aG1 self() {
    return this;
  }

  public BN254aG1 construct(final BN254aFq X, final BN254aFq Y, final BN254aFq Z) {
    return new BN254aG1(X, Y, Z);
  }
}
