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
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import reductions.r1cs_to_qap.R1CStoQAPRDD;
import relations.objects.Assignment;
import relations.qap.QAPRelationRDD;
import relations.qap.QAPWitnessRDD;
import scala.Tuple2;
import zk_proof_systems.zkSNARK.objects.Proof;
import zk_proof_systems.zkSNARK.objects.ProvingKeyRDD;

public class DistributedProver {
  public static <
          FieldT extends AbstractFieldElementExpanded<FieldT>,
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>>
      Proof<G1T, G2T> prove(
          final ProvingKeyRDD<FieldT, G1T, G2T> provingKey,
          final Assignment<FieldT> primary,
          final JavaPairRDD<Long, FieldT> oneFullAssignment,
          final FieldT fieldFactory,
          final Configuration config) {
    if (config.debugFlag()) {
      assert (provingKey.r1cs().isSatisfied(primary, oneFullAssignment));
    }

    config.beginLog("Computing witness polynomial");
    final QAPWitnessRDD<FieldT> qapWitness =
        R1CStoQAPRDD.R1CStoQAPWitness(
            provingKey.r1cs(), primary, oneFullAssignment, fieldFactory, config);
    config.endLog("Computing witness polynomial");

    if (config.debugFlag()) {
      // We are dividing degree 2(d-1) polynomial by degree d polynomial
      // and not adding a PGHR-style ZK-patch, so our H is degree d-2.
      final FieldT zero = fieldFactory.zero();
      qapWitness
          .coefficientsH()
          .filter(e -> e._1 >= qapWitness.degree() - 2)
          .foreach(
              coeff -> {
                if (coeff._1 == qapWitness.degree() - 2) {
                  assert (!coeff._2.equals(zero));
                } else if (coeff._1 > qapWitness.degree() - 2) {
                  assert (coeff._2.equals(zero));
                }
              });
      // Check that the witness satisfies the QAP relation.
      final FieldT t = fieldFactory.random(config.seed(), config.secureSeed());
      final QAPRelationRDD<FieldT> qap =
          R1CStoQAPRDD.R1CStoQAPRelation(provingKey.r1cs(), t, config);
      assert (qap.isSatisfied(qapWitness));
    }

    // Unpersist the R1CS constraints RDDs and free up memory.
    provingKey.r1cs().constraints().A().unpersist();
    provingKey.r1cs().constraints().B().unpersist();
    provingKey.r1cs().constraints().C().unpersist();

    // Choose two random field elements for prover zero-knowledge.
    final FieldT r = fieldFactory.random(config.seed(), config.secureSeed());
    final FieldT s = fieldFactory.random(config.seed(), config.secureSeed());

    // Get initial parameters from the proving key.
    final G1T alphaG1 = provingKey.alphaG1();
    final G1T betaG1 = provingKey.betaG1();
    final G2T betaG2 = provingKey.betaG2();
    final G1T deltaG1 = provingKey.deltaG1();
    final G2T deltaG2 = provingKey.deltaG2();
    final G1T rsDelta = deltaG1.mul(r.mul(s));

    final int numPartitions = config.numPartitions();

    config.beginRuntime("Proof");

    config.beginLog("Computing evaluation to query A: summation of variable_i*A_i(t)");
    final JavaRDD<Tuple2<FieldT, G1T>> computationA =
        oneFullAssignment.join(provingKey.queryA(), numPartitions).values();
    final G1T evaluationAt = VariableBaseMSM.distributedMSM(computationA);
    provingKey.queryA().unpersist();
    config.endLog("Computing evaluation to query A: summation of variable_i*A_i(t)");

    config.beginLog("Computing evaluation to query B: summation of variable_i*B_i(t)");
    final JavaRDD<Tuple2<FieldT, Tuple2<G1T, G2T>>> computationB =
        oneFullAssignment.join(provingKey.queryB(), numPartitions).values();
    final Tuple2<G1T, G2T> evaluationBt = VariableBaseMSM.distributedDoubleMSM(computationB);
    provingKey.queryB().unpersist();
    config.endLog("Computing evaluation to query B: summation of variable_i*B_i(t)");

    // Compute evaluationABC = a_i*((beta*A_i(t) + alpha*B_i(t) + C_i(t)) + H(t)*Z(t))/delta.
    config.beginLog("Computing evaluation to deltaABC");
    final JavaRDD<Tuple2<FieldT, G1T>> deltaABCAuxiliary =
        oneFullAssignment.join(provingKey.deltaABCG1(), numPartitions).values();
    G1T evaluationABC = VariableBaseMSM.distributedMSM(deltaABCAuxiliary);
    provingKey.deltaABCG1().unpersist();
    oneFullAssignment.unpersist();
    config.endLog("Computing evaluation to deltaABC");

    config.beginLog("Computing evaluation to query H");
    final JavaRDD<Tuple2<FieldT, G1T>> computationH =
        qapWitness.coefficientsH().join(provingKey.queryH(), numPartitions).values();
    final G1T evaluationHtZt = VariableBaseMSM.distributedMSM(computationH);
    provingKey.queryH().unpersist();
    evaluationABC = evaluationABC.add(evaluationHtZt); // H(t)*Z(t)/delta
    config.endLog("Computing evaluation to query H");

    // A = alpha + sum_i(a_i*A_i(t)) + r*delta
    final G1T A = alphaG1.add(evaluationAt).add(deltaG1.mul(r));

    // B = beta + sum_i(a_i*B_i(t)) + s*delta
    final Tuple2<G1T, G2T> B =
        new Tuple2<>(
            betaG1.add(evaluationBt._1).add(deltaG1.mul(s)),
            betaG2.add(evaluationBt._2).add(deltaG2.mul(s)));

    // C = sum_i(a_i*((beta*A_i(t) + alpha*B_i(t) + C_i(t)) + H(t)*Z(t))/delta) + A*s + r*b -
    // r*s*delta
    final G1T C = evaluationABC.add(A.mul(s)).add(B._1.mul(r)).sub(rsDelta);

    config.endRuntime("Proof");

    return new Proof<>(A, B._2, C);
  }
}
