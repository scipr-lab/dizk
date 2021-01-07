/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package zk_proof_systems.zkSNARK;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.fields.AbstractFieldElementExpanded;
import algebra.msm.VariableBaseMSM;
import configuration.Configuration;
import reductions.r1cs_to_qap.R1CStoQAP;
import relations.objects.Assignment;
import relations.qap.QAPRelation;
import relations.qap.QAPWitness;
import scala.Tuple2;
import zk_proof_systems.zkSNARK.objects.Proof;
import zk_proof_systems.zkSNARK.objects.ProvingKey;

public class SerialProver {
  public static <
          FieldT extends AbstractFieldElementExpanded<FieldT>,
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>>
      Proof<G1T, G2T> prove(
          final ProvingKey<FieldT, G1T, G2T> provingKey,
          final Assignment<FieldT> primary,
          final Assignment<FieldT> auxiliary,
          final FieldT fieldFactory,
          final Configuration config) {
    // If the debug flag is set, check up-front that the R1CS is satisfied
    if (config.debugFlag()) {
      assert (provingKey.r1cs().isSatisfied(primary, auxiliary));
    }

    config.beginRuntime("Witness");
    config.beginLog("Computing witness polynomial");
    final QAPWitness<FieldT> qapWitness =
        R1CStoQAP.R1CStoQAPWitness(provingKey.r1cs(), primary, auxiliary, fieldFactory, config);
    config.endLog("Computing witness polynomial");
    config.endRuntime("Witness");

    if (config.debugFlag()) {
      // We are dividing degree 2(d-1) polynomial by degree d polynomial
      // and not adding a PGHR-style ZK-patch, so our H is degree d-2.
      final FieldT zero = fieldFactory.zero();
      // Make sure that H has at most d+1 coeffs (which bounds deg(H(x)) <= d)
      assert (qapWitness.coefficientsH().size() == qapWitness.degree() + 1);
      // 1. Make sure that coefficients d-1 and d are 0 to make sure that the polynomial hasn't a
      // degree higher than d-2 (these steps refine the upper bound deg(H(x)) <= d-2)
      assert (qapWitness.coefficientsH(qapWitness.degree() - 1).equals(zero));
      assert (qapWitness.coefficientsH(qapWitness.degree()).equals(zero));
      // 2. Make sure that H has a non-zero d-2 coefficient (deg(H(x)) = d-2)
      assert (!qapWitness.coefficientsH(qapWitness.degree() - 2).equals(zero));
      // Check that the witness satisfies the QAP relation.
      final FieldT t = fieldFactory.random(config.seed(), config.secureSeed());
      final QAPRelation<FieldT> qap = R1CStoQAP.R1CStoQAPRelation(provingKey.r1cs(), t);
      assert (qap.isSatisfied(qapWitness));
      System.out.println("\n\t ===== [DEBUG] qap.isSatisfied(qapWitness) TRUTH value: " + qap.isSatisfied(qapWitness));
    }

    // Choose two random field elements for prover zero-knowledge.
    // r, s \sample \FF^2
    final FieldT r = fieldFactory.random(config.seed(), config.secureSeed());
    final FieldT s = fieldFactory.random(config.seed(), config.secureSeed());

    if (config.debugFlag()) {
        assert(qapWitness.coefficientsABC().size() == qapWitness.numVariables());
        assert(provingKey.queryA().size() == qapWitness.numVariables());
        assert(provingKey.deltaABCG1().size() == qapWitness.numVariables() - qapWitness.numInputs());
        System.out.println("\n\t ===== [DEBUG] Asserts on size pass =====");
      }

    // Get initial parameters from the proving key.
    final G1T alphaG1 = provingKey.alphaG1();
    final G1T betaG1 = provingKey.betaG1();
    final G2T betaG2 = provingKey.betaG2();
    final G1T deltaG1 = provingKey.deltaG1();
    final G2T deltaG2 = provingKey.deltaG2();
    final G1T rsDelta = deltaG1.mul(r.mul(s));

    final int numInputs = provingKey.r1cs().numInputs();
    final int numVariables = provingKey.r1cs().numVariables();

    config.beginRuntime("Proof");

    config.beginLog("Computing evaluation to query A: summation of variable_i*A_i(t)");
    // A = alpha + \sum_{i=0}^{numVariables} var_i * A_i(t) + r * delta
    // Below, the summation is decomposed as:
    // \sum_{i=0}^{numInputs} pubInp_i * A_i(t) + \sum_{i=numInputs + 1}^{numVariables} auxInp_i * A_i(t)
    G1T evaluationAt =
        VariableBaseMSM.serialMSM(primary.elements(), provingKey.queryA().subList(0, numInputs));
    evaluationAt =
        evaluationAt.add(
            VariableBaseMSM.serialMSM(
                auxiliary.elements(), provingKey.queryA().subList(numInputs, numVariables)));
    config.endLog("Computing evaluation to query A: summation of variable_i*A_i(t)");

    config.beginLog("Computing evaluation to query B: summation of variable_i*B_i(t)");
    // B = beta + \sum_{i=0}^{numVariables} var_i * B_i(t) +  s * delta
    // Below, the summation is decomposed as:
    // \sum_{i=0}^{numInputs} pubInp_i * B_i(t) + \sum_{i=numInputs + 1}^{numVariables} auxInp_i * B_i(t)
    final Tuple2<G1T, G2T> evaluationBtPrimary =
        VariableBaseMSM.doubleMSM(primary.elements(), provingKey.queryB().subList(0, numInputs));
    final Tuple2<G1T, G2T> evaluationBtWitness =
        VariableBaseMSM.doubleMSM(
            auxiliary.elements(), provingKey.queryB().subList(numInputs, numVariables));
    final G1T evaluationBtG1 = evaluationBtPrimary._1.add(evaluationBtWitness._1);
    final G2T evaluationBtG2 = evaluationBtPrimary._2.add(evaluationBtWitness._2);
    config.endLog("Computing evaluation to query B: summation of variable_i*B_i(t)");

    config.beginLog("Computing evaluation to query H");
    final G1T evaluationHtZt =
        VariableBaseMSM.serialMSM(qapWitness.coefficientsH(), provingKey.queryH());
    config.endLog("Computing evaluation to query H");

    // Compute evaluationABC = \sum_{i=numInputs+1}^{numVariables} var_i * ((beta*A_i(t) + alpha*B_i(t) + C_i(t)) + H(t)*Z(t))/delta.
    config.beginLog("Computing evaluation to deltaABC");
    final int numWitness = numVariables - numInputs;
    //G1T evaluationABC =
    //    VariableBaseMSM.serialMSM(
    //        auxiliary.subList(0, numWitness), provingKey.deltaABCG1().subList(0, numWitness));
    G1T evaluationABC =
        VariableBaseMSM.serialMSM(auxiliary.elements(), provingKey.deltaABCG1());
    evaluationABC = evaluationABC.add(evaluationHtZt); // H(t)*Z(t)/delta
    config.endLog("Computing evaluation to deltaABC");

    // A = alpha + \sum_{i=0}^{numVariables}(var_i*A_i(t)) + r*delta
    final G1T A = alphaG1.add(evaluationAt).add(deltaG1.mul(r));

    // B = beta + sum_{i=0}^{numVariables}(var_i*B_i(t)) + s*delta
    final Tuple2<G1T, G2T> B =
        new Tuple2<>(
            betaG1.add(evaluationBtG1).add(deltaG1.mul(s)),
            betaG2.add(evaluationBtG2).add(deltaG2.mul(s)));

    // C = \sum_{i=numInputs+1}^{numVariables}(var_i*((beta*A_i(t) + alpha*B_i(t) + C_i(t)) + H(t)*Z(t))/delta) + A*s + r*b - r*s*delta
    final G1T C = evaluationABC.add(A.mul(s)).add(B._1.mul(r)).sub(rsDelta);

    config.endRuntime("Proof");

    return new Proof<>(A, B._2, C);
  }
}
