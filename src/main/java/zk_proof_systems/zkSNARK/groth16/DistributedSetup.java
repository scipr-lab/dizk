/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package zk_proof_systems.zkSNARK.groth16;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.curves.AbstractGT;
import algebra.curves.AbstractPairing;
import algebra.fields.AbstractFieldElementExpanded;
import algebra.msm.FixedBaseMSM;
import common.Utils;
import configuration.Configuration;
import java.util.List;
import org.apache.spark.api.java.JavaPairRDD;
import reductions.r1cs_to_qap.R1CStoQAPRDD;
import relations.qap.QAPRelationRDD;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple2;
import zk_proof_systems.zkSNARK.groth16.objects.CRS;
import zk_proof_systems.zkSNARK.groth16.objects.ProvingKeyRDD;
import zk_proof_systems.zkSNARK.groth16.objects.VerificationKey;

public class DistributedSetup {
  public static <
          FieldT extends AbstractFieldElementExpanded<FieldT>,
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>,
          GTT extends AbstractGT<GTT>,
          PairingT extends AbstractPairing<G1T, G2T, GTT>>
      CRS<FieldT, G1T, G2T, GTT> generate(
          final R1CSRelationRDD<FieldT> r1cs,
          final FieldT fieldFactory,
          final G1T g1Factory,
          final G2T g2Factory,
          final PairingT pairing,
          final Configuration config) {
    // Generate secret randomness.
    final FieldT t = fieldFactory.random(config.seed(), config.secureSeed());
    final FieldT alpha = fieldFactory.random(config.seed(), config.secureSeed());
    final FieldT beta = fieldFactory.random(config.seed(), config.secureSeed());
    final FieldT gamma = fieldFactory.random(config.seed(), config.secureSeed());
    final FieldT delta = fieldFactory.random(config.seed(), config.secureSeed());
    final FieldT inverseGamma = gamma.inverse();
    final FieldT inverseDelta = delta.inverse();

    // A quadratic arithmetic program evaluated at t.
    final QAPRelationRDD<FieldT> qap = R1CStoQAPRDD.R1CStoQAPRelation(r1cs, t, config);

    final int numInputs = qap.numInputs();
    final long numVariables = qap.numVariables();
    final int numPartitions = config.numPartitions();

    System.out.println("\tQAP - primary input size: " + numInputs);
    System.out.println("\tQAP - total input size: " + numVariables);
    System.out.println("\tQAP - pre degree: " + r1cs.numConstraints());
    System.out.println("\tQAP - degree: " + qap.degree());

    // The gamma inverse product component: (beta*A_i(t) + alpha*B_i(t) + C_i(t)) * gamma^{-1}
    // The delta inverse product component: (beta*A_i(t) + alpha*B_i(t) + C_i(t)) * delta^{-1}
    config.beginLog("Computing deltaABC and gammaABC for R1CS proving key and verification key");
    final JavaPairRDD<Long, FieldT> betaAt = qap.At().mapValues(a -> a.mul(beta));
    final JavaPairRDD<Long, FieldT> alphaBt = qap.Bt().mapValues(b -> b.mul(alpha));
    final JavaPairRDD<Long, FieldT> ABC =
        betaAt
            .union(alphaBt)
            .union(qap.Ct())
            .reduceByKey(FieldT::add)
            .persist(config.storageLevel());
    final JavaPairRDD<Long, FieldT> gammaABC =
        ABC.filter(e -> e._1 < numInputs).mapValues(e -> e.mul(inverseGamma));
    final JavaPairRDD<Long, FieldT> deltaABC =
        ABC.filter(e -> e._1 >= numInputs).mapValues(e -> e.mul(inverseDelta));
    config.endLog("Computing deltaABC and gammaABC for R1CS proving key and verification key");

    config.beginLog("Computing query densities");
    final long numNonZeroAt = qap.At().filter(e -> !e._2.isZero()).count();
    final long numNonZeroBt = qap.Bt().filter(e -> !e._2.isZero()).count();
    config.endLog("Computing query densities");

    config.beginLog("Generating G1 MSM Window Table");
    final G1T generatorG1 = g1Factory.random(config.seed(), config.secureSeed());
    final int scalarSizeG1 = generatorG1.bitSize();
    final long scalarCountG1 = numNonZeroAt + numNonZeroBt + numVariables;
    final int windowSizeG1 = FixedBaseMSM.getWindowSize(scalarCountG1 / numPartitions, generatorG1);
    final List<List<G1T>> windowTableG1 =
        FixedBaseMSM.getWindowTable(generatorG1, scalarSizeG1, windowSizeG1);
    config.endLog("Generating G1 MSM Window Table");

    config.beginLog("Generating G2 MSM Window Table");
    final G2T generatorG2 = g2Factory.random(config.seed(), config.secureSeed());
    final int scalarSizeG2 = generatorG2.bitSize();
    final long scalarCountG2 = numNonZeroBt;
    final int windowSizeG2 = FixedBaseMSM.getWindowSize(scalarCountG2 / numPartitions, generatorG2);
    final List<List<G2T>> windowTableG2 =
        FixedBaseMSM.getWindowTable(generatorG2, scalarSizeG2, windowSizeG2);
    config.endLog("Generating G2 MSM Window Table");

    config.beginLog("Generating R1CS proving key");
    config.beginRuntime("Proving Key");

    final G1T alphaG1 = generatorG1.mul(alpha);
    final G1T betaG1 = generatorG1.mul(beta);
    final G2T betaG2 = generatorG2.mul(beta);
    final G1T deltaG1 = generatorG1.mul(delta);
    final G2T deltaG2 = generatorG2.mul(delta);

    config.beginLog("Encoding deltaABC for R1CS proving key");
    final JavaPairRDD<Long, G1T> deltaABCG1 =
        FixedBaseMSM.distributedBatchMSM(
                scalarSizeG1, windowSizeG1, windowTableG1, deltaABC, config.sparkContext())
            .persist(config.storageLevel());
    deltaABCG1.count();
    qap.Ct().unpersist();
    config.endLog("Encoding deltaABC for R1CS proving key");

    config.beginLog("Computing query A");
    final JavaPairRDD<Long, G1T> queryA =
        FixedBaseMSM.distributedBatchMSM(
                scalarSizeG1, windowSizeG1, windowTableG1, qap.At(), config.sparkContext())
            .persist(config.storageLevel());
    queryA.count();
    qap.At().unpersist();
    config.endLog("Computing query A");

    config.beginLog("Computing query B");
    final JavaPairRDD<Long, Tuple2<G1T, G2T>> queryB =
        FixedBaseMSM.distributedDoubleBatchMSM(
                scalarSizeG1,
                windowSizeG1,
                windowTableG1,
                scalarSizeG2,
                windowSizeG2,
                windowTableG2,
                qap.Bt(),
                config.sparkContext())
            .persist(config.storageLevel());
    queryB.count();
    qap.Bt().unpersist();
    config.endLog("Computing query B");

    config.beginLog("Computing query H");
    final FieldT inverseDeltaZt = qap.Zt().mul(delta.inverse());
    final JavaPairRDD<Long, FieldT> inverseDeltaHtZt =
        qap.Ht().mapValues((e) -> e.mul(inverseDeltaZt));
    final JavaPairRDD<Long, G1T> queryH =
        FixedBaseMSM.distributedBatchMSM(
                scalarSizeG1, windowSizeG1, windowTableG1, inverseDeltaHtZt, config.sparkContext())
            .persist(config.storageLevel());
    queryH.count();
    qap.Ht().unpersist();
    config.endLog("Computing query H");

    config.endLog("Generating R1CS proving key");
    config.endRuntime("Proving Key");

    config.beginLog("Computing gammaABC for R1CS verification key");
    config.beginRuntime("Verification Key");
    final GTT alphaG1betaG2 = pairing.reducedPairing(alphaG1, betaG2);
    final G2T gammaG2 = generatorG2.mul(gamma);
    final JavaPairRDD<Long, G1T> gammaABCG1 =
        FixedBaseMSM.distributedBatchMSM(
                scalarSizeG1, windowSizeG1, windowTableG1, gammaABC, config.sparkContext())
            .persist(config.storageLevel());
    final JavaPairRDD<Long, G1T> fullGammaABCG1 =
        Utils.fillRDD(numInputs, generatorG1.zero(), config)
            .union(gammaABCG1)
            .reduceByKey(G1T::add);
    final List<G1T> UVWGammaG1 = Utils.convertFromPair(fullGammaABCG1.collect(), numInputs);
    ABC.unpersist();
    config.endLog("Computing gammaABC for R1CS verification key");
    config.endRuntime("Verification Key");

    // Construct the proving key.
    final ProvingKeyRDD<FieldT, G1T, G2T> provingKey =
        new ProvingKeyRDD<>(
            alphaG1, betaG1, betaG2, deltaG1, deltaG2, deltaABCG1, queryA, queryB, queryH, r1cs);

    // Construct the verification key.
    final VerificationKey<G1T, G2T, GTT> verificationKey =
        new VerificationKey<>(alphaG1betaG2, gammaG2, deltaG2, UVWGammaG1);

    return new CRS<>(provingKey, verificationKey);
  }
}
