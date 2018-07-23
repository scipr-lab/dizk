/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package zk_proof_systems.zkSNARK.objects;

import algebra.fields.AbstractFieldElementExpanded;
import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.curves.AbstractGT;

import java.io.Serializable;

/**
 * Common Reference String (CRS)
 */
public class CRS<FieldT extends AbstractFieldElementExpanded<FieldT>, G1T extends
        AbstractG1<G1T>, G2T extends AbstractG2<G2T>, GTT extends AbstractGT<GTT>> implements
        Serializable {

    private final ProvingKey<FieldT, G1T, G2T> provingKey;
    private final ProvingKeyRDD<FieldT, G1T, G2T> provingKeyRDD;
    private final VerificationKey<G1T, G2T, GTT> verificationKey;

    public CRS(
            final ProvingKeyRDD<FieldT, G1T, G2T> _provingKeyRDD,
            final VerificationKey<G1T, G2T, GTT> _verificationKey) {
        provingKey = null;
        provingKeyRDD = _provingKeyRDD;
        verificationKey = _verificationKey;
    }

    public CRS(
            final ProvingKey<FieldT, G1T, G2T> _provingKey,
            final VerificationKey<G1T, G2T, GTT> _verificationKey) {
        provingKey = _provingKey;
        provingKeyRDD = null;
        verificationKey = _verificationKey;
    }

    public ProvingKey<FieldT, G1T, G2T> provingKey() {
        return provingKey;
    }

    public ProvingKeyRDD<FieldT, G1T, G2T> provingKeyRDD() {
        return provingKeyRDD;
    }

    public VerificationKey<G1T, G2T, GTT> verificationKey() {
        return verificationKey;
    }
}
