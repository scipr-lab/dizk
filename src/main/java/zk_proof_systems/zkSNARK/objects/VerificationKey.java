/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package zk_proof_systems.zkSNARK.objects;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.curves.AbstractGT;
import java.util.List;

/** Groth16 verification key */
public class VerificationKey<
    G1T extends AbstractG1<G1T>, G2T extends AbstractG2<G2T>, GTT extends AbstractGT<GTT>> {

  private final G1T alphaG1;
  private final G2T betaG2;
  private final G2T deltaG2;
  private final List<G1T> gammaABC;

  public VerificationKey(
      final G1T _alphaG1, final G2T _betaG2, final G2T _deltaG2, final List<G1T> _gammaABC) {
    alphaG1 = _alphaG1;
    betaG2 = _betaG2;
    deltaG2 = _deltaG2;
    gammaABC = _gammaABC;
  }

  public boolean equals(final VerificationKey<G1T, G2T, GTT> other) {
    if (!alphaG1.equals(other.alphaG1())) {
      return false;
    }

    if (!betaG2.equals(other.betaG2())) {
      return false;
    }

    if (!deltaG2.equals(other.deltaG2())) {
      return false;
    }

    if (gammaABC.size() != other.gammaABC().size()) {
      return false;
    }

    for (int i = 0; i < gammaABC.size(); i++) {
      if (!gammaABC(i).equals(other.gammaABC(i))) {
        return false;
      }
    }

    return true;
  }

  public G1T alphaG1() {
    return alphaG1;
  }

  public G2T betaG2() {
    return betaG2;
  }

  public G2T deltaG2() {
    return deltaG2;
  }

  public G1T gammaABC(final int i) {
    return gammaABC.get(i);
  }

  public List<G1T> gammaABC() {
    return gammaABC;
  }
}
