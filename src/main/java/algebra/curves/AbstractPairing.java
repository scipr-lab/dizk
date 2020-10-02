/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves;

public abstract class AbstractPairing<
    G1T extends AbstractG1<G1T>, G2T extends AbstractG2<G2T>, GTT extends AbstractGT<GTT>> {

  protected abstract class G1Precompute {}

  protected abstract class G2Precompute {}

  protected abstract G1Precompute precomputeG1(final G1T P);

  protected abstract G2Precompute precomputeG2(final G2T Q);

  public abstract GTT reducedPairing(final G1T P, final G2T Q);
}
