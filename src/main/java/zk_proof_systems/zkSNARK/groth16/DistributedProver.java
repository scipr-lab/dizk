/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package zk_proof_systems.zkSNARK.groth16;

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
import zk_proof_systems.zkSNARK.groth16.objects.Proof;
import zk_proof_systems.zkSNARK.groth16.objects.ProvingKeyRDD;

public class DistributedProver {
  public static <
          FieldT extends AbstractFieldElementExpanded<FieldT>,
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>>
      Proof<G1T, G2T> prove(
          final ProvingKeyRDD<FieldT, G1T, G2T> provingKey,
          final Assignment<FieldT> primary,
          final JavaPairRDD<Long, FieldT> fullAssignment,
          final FieldT fieldFactory,
          final Configuration config) {
    // Note: `R1CStoQAPWitness` already checks the value of the configuration `debugFlag`, and
    // already checks that the R1CS is satisfied on input `primary` and `fullAssignment`. No need to
    // do it again it, this is redundant.
    // if (config.debugFlag()) {
    //  assert (provingKey.r1cs().isSatisfied(primary, fullAssignment));
    // }

    config.beginLog("Computing witness polynomial");
    final QAPWitnessRDD<FieldT> qapWitness =
        R1CStoQAPRDD.R1CStoQAPWitness(
            provingKey.r1cs(), primary, fullAssignment, fieldFactory, config);
    config.endLog("Computing witness polynomial");

    if (config.debugFlag()) {
      // We are dividing degree 2(d-1) polynomial by degree d polynomial
      // and not adding a PGHR-style ZK-patch, so our H is degree d-2.
      final FieldT zero = fieldFactory.zero();
      // 1. Filter the coeffs to only get the 3 coeffs at indices d-2, d-1, d
      // 2. Carry out checks on these 3 coeffs, namely:
      //    - coeff at d-2 can not be 0 (we want a poly of deg d-2)
      //    - coeffs at d-1, d must be 0 (we want a poly of deg d-2)
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
      // Check that the witness satisfies the QAP relation
      // To that end, we pick a random evaluation point t and check
      // that the QAP is satisfied (i.e. this random evaluation point is not the one in the SRS)
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

    // Number of partitions per RDD set in the config
    final int numPartitions = config.numPartitions();

    config.beginRuntime("Generate proof");

    config.beginLog("Computing evaluation to query A: summation of variable_i*A_i(t)");
    // Get an RDD containing all pairs of elements with **matching keys** in `fullAssignment` and
    // `provingKey.queryA()`. The result of this `.join()` will be a (k, (v1, v2)) tuple, where (k,
    // v1) is in `fullAssignment` and (k, v2) is in `provingKey.queryA()`. Then, the `.value()`
    // returns the tuple (v1, v2) - removing the keys - which is just an index used to associate the
    // right scalar to the right group element in light of the `VariableBaseMSM` triggered at the
    // next line.
    final JavaRDD<Tuple2<FieldT, G1T>> computationA =
        fullAssignment.join(provingKey.queryA(), numPartitions).values();
    // `evaluationAt` = \sum_{i=0}^{m} a_i * u_i(x) (in Groth16)
    // where m = total number of wires
    //       a_i = ith wire/variable
    final G1T evaluationAt = VariableBaseMSM.distributedMSM(computationA);
    // Once `queryA` is not useful anymore, mark the RDD as non-persistent, and remove all blocks
    // for it from memory and disk.
    provingKey.queryA().unpersist();
    config.endLog("Computing evaluation to query A: summation of variable_i*A_i(t)");

    config.beginLog("Computing evaluation to query B: summation of variable_i*B_i(t)");
    final JavaRDD<Tuple2<FieldT, Tuple2<G1T, G2T>>> computationB =
        fullAssignment.join(provingKey.queryB(), numPartitions).values();
    // `evaluationBt` = \sum_{i=0}^{m} a_i * v_i(x) (in Groth16)
    // where m = total number of wires
    //       a_i = ith wire/variable
    // Note: We get an evaluation in G1 and G2, because B \in G2 is formed using this term, and C
    // (\in G1) also uses this term (see below, B is of type `Tuple2<G1T, G2T>` and will actually be
    // computed in both G1 and G2 for this exact purpose).
    final Tuple2<G1T, G2T> evaluationBt = VariableBaseMSM.distributedDoubleMSM(computationB);
    provingKey.queryB().unpersist();
    config.endLog("Computing evaluation to query B: summation of variable_i*B_i(t)");

    // Compute evaluationABC = variable_i*((beta*A_i(t) + alpha*B_i(t) + C_i(t)) + H(t)*Z(t))/delta.
    // In Groth16 notation, this is used for the computation of C:
    // [\sum_{i=l+1}^{m} a_i*((beta*u_i(x) + alpha*v_i(x) + w_i(x))] /delta.
    // where m = total number of wires
    //       a_i = ith wire/variable
    config.beginLog("Computing evaluation to deltaABC");
    final JavaRDD<Tuple2<FieldT, G1T>> deltaABCAuxiliary =
        fullAssignment.join(provingKey.deltaABCG1(), numPartitions).values();
    G1T evaluationABC = VariableBaseMSM.distributedMSM(deltaABCAuxiliary);
    provingKey.deltaABCG1().unpersist();
    fullAssignment.unpersist();
    config.endLog("Computing evaluation to deltaABC");

    config.beginLog("Computing evaluation to query H");
    // In Groth16 notations, `queryH` is the encoding in G1 of the vector <(x^i * t(x))/delta>, for
    // i \in [0, n-2]
    // As such, the value of `evaluationHtZtOverDelta` actually is: (h(x)t(x))/delta if we follow
    // Groth's notations
    final JavaRDD<Tuple2<FieldT, G1T>> computationH =
        qapWitness.coefficientsH().join(provingKey.queryH(), numPartitions).values();
    final G1T evaluationHtZtOverDelta = VariableBaseMSM.distributedMSM(computationH);
    provingKey.queryH().unpersist();
    // Add H(t)*Z(t)/delta to `evaluationABC` to get the first term of C, namely (following Groth's
    // notations):
    // [\sum_{i = l+1}^{m} a_i * (beta * u_i(x) + alpha * v_i(x) + w_i(x) + h(x)t(x))]/delta
    evaluationABC = evaluationABC.add(evaluationHtZtOverDelta);
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

    config.endRuntime("Generate proof");

    return new Proof<>(A, B._2, C);
  }
}
