/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_lynn_scott.bls12_377;

import algebra.curves.barreto_lynn_scott.BLSG1;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G1Parameters;

/**
 * Class representing a specific BN group. This class is representing BOTH the group and a point in
 * the group. In fact, each point necessitates the X,Y,Z coordinates; and the group definition
 * additionally requires the `G1Parameters` attribute. Here all group elements are constructed by
 * `construct()` which is a wrapper around the "Group class" constructor.
 *
 * <p>That's why `BN254bG1Parameters.ONE` is treated as a "Group factory" here:
 * https://github.com/clearmatics/dizk/blob/develop/src/test/java/algebra/curves/CurvesTest.java#L93
 */
public class BLS12_377G1 extends BLSG1<BLS12_377Fr, BLS12_377Fq, BLS12_377G1, BLS12_377G1Parameters> {

  public static final BLS12_377G1Parameters G1Parameters = new BLS12_377G1Parameters();

  public BLS12_377G1(final BLS12_377Fq X, final BLS12_377Fq Y, final BLS12_377Fq Z) {
    super(X, Y, Z, G1Parameters);
  }

  public BLS12_377G1 self() {
    return this;
  }

  public BLS12_377G1 construct(final BLS12_377Fq X, final BLS12_377Fq Y, final BLS12_377Fq Z) {
    return new BLS12_377G1(X, Y, Z);
  }
}
