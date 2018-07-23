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

public class VerificationKey<G1T extends AbstractG1<G1T>, G2T extends AbstractG2<G2T>, GTT
        extends AbstractGT<GTT>> {

    private final GTT alphaG1betaG2;
    private final G2T gammaG2;
    private final G2T deltaG2;
    private final List<G1T> gammaABC;

    public VerificationKey(
            final GTT _alphaG1betaG2,
            final G2T _gammaG2,
            final G2T _deltaG2,
            final List<G1T> _gammaABC) {
        alphaG1betaG2 = _alphaG1betaG2;
        gammaG2 = _gammaG2;
        deltaG2 = _deltaG2;
        gammaABC = _gammaABC;
    }

    public boolean equals(final VerificationKey<G1T, G2T, GTT> other) {
        if (!alphaG1betaG2.equals(other.alphaG1betaG2())) {
            return false;
        }

        if (!gammaG2.equals(other.gammaG2())) {
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

    public GTT alphaG1betaG2() {
        return alphaG1betaG2;
    }

    public G2T gammaG2() {
        return gammaG2;
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
