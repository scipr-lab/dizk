/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package zk_proof_systems.zkSNARK.grothBGM17.objects;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.fields.AbstractFieldElementExpanded;
import java.io.Serializable;

/** Groth16-BGM17 Common Reference String (CRS) */
public class CRS<
        FieldT extends AbstractFieldElementExpanded<FieldT>,
        G1T extends AbstractG1<G1T>,
        G2T extends AbstractG2<G2T>>
    implements Serializable {

  private final ProvingKey<FieldT, G1T, G2T> provingKey;
  private final ProvingKeyRDD<FieldT, G1T, G2T> provingKeyRDD;
  private final VerificationKey<G1T, G2T> verificationKey;

  public CRS(
      final ProvingKeyRDD<FieldT, G1T, G2T> _provingKeyRDD,
      final VerificationKey<G1T, G2T> _verificationKey) {
    provingKey = null;
    provingKeyRDD = _provingKeyRDD;
    verificationKey = _verificationKey;
  }

  public CRS(
      final ProvingKey<FieldT, G1T, G2T> _provingKey,
      final VerificationKey<G1T, G2T> _verificationKey) {
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

  public VerificationKey<G1T, G2T> verificationKey() {
    return verificationKey;
  }
}
