/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package zk_proof_systems.zkSNARK;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.curves.AbstractGT;
import algebra.curves.AbstractPairing;
import algebra.fields.AbstractFieldElementExpanded;
import algebra.msm.VariableBaseMSM;
import configuration.Configuration;
import relations.objects.Assignment;
import zk_proof_systems.zkSNARK.objects.Proof;
import zk_proof_systems.zkSNARK.objects.VerificationKey;

public class Verifier {
  public static <
          FieldT extends AbstractFieldElementExpanded<FieldT>,
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>,
          GTT extends AbstractGT<GTT>,
          PairingT extends AbstractPairing<G1T, G2T, GTT>>
      boolean verify(
          final VerificationKey<G1T, G2T> verificationKey,
          final Assignment<FieldT> primaryInput,
          final Proof<G1T, G2T> proof,
          final PairingT pairing,
          final Configuration config) {
    // Assert first element == FieldT.one().
    final FieldT firstElement = primaryInput.get(0);
    assert (firstElement.equals(firstElement.one()));

    // LHS: Compute A * B
    final GTT LHS = pairing.reducedPairing(proof.gA(), proof.gB());

    // RHS: Compute [alpha * beta]_T
    final GTT alphaBeta = pairing.reducedPairing(verificationKey.alphaG1(),verificationKey.betaG2());

    // RHS: Compute [C * delta]_T
    final G2T delta = verificationKey.deltaG2();
    final GTT CDelta = pairing.reducedPairing(proof.gC(), delta);

    // RHS: Compute \sum_{i=0}^{numInputs} pubInp_i * (beta*A_i(x) + alpha*B_i(x) + C_i(x))
    final G1T evaluationABC =
        VariableBaseMSM.serialMSM(primaryInput.elements(), verificationKey.ABC());

    // Compute the RHS: [alpha*beta + evaluationABC*1 + C*delta]_T
    final G2T generatorG2 = delta.one(); // See SerialSetup, we take ONE in both G1 and G2 as generator.
    final GTT RHS = alphaBeta.add(pairing.reducedPairing(evaluationABC, generatorG2)).add(CDelta);

    final boolean verifierResult = LHS.equals(RHS);
    if (!verifierResult && config.verboseFlag()) {
      System.out.println("Verification failed: ");
      System.out.println("\n\tLHS = " + LHS + "\n\tRHS = " + RHS);
    }

    return verifierResult;
  }
}
