/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package zk_proof_systems.zkSNARK.objects;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;

public class Proof<G1T extends AbstractG1<G1T>, G2T extends AbstractG2<G2T>> {

    private final G1T gA;
    private final G2T gB;
    private final G1T gC;

    public Proof(final G1T _gA, final G2T _gB, final G1T _gC) {
        gA = _gA;
        gB = _gB;
        gC = _gC;
    }

    public G1T gA() {
        return gA;
    }

    public G2T gB() {
        return gB;
    }

    public G1T gC() {
        return gC;
    }
}
