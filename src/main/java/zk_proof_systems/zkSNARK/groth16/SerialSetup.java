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
import configuration.Configuration;
import java.util.ArrayList;
import java.util.List;
import reductions.r1cs_to_qap.R1CStoQAP;
import relations.qap.QAPRelation;
import relations.r1cs.R1CSRelation;
import scala.Tuple2;
import zk_proof_systems.zkSNARK.groth16.objects.CRS;
import zk_proof_systems.zkSNARK.groth16.objects.ProvingKey;
import zk_proof_systems.zkSNARK.groth16.objects.VerificationKey;

public class SerialSetup {
  public static <
          FieldT extends AbstractFieldElementExpanded<FieldT>,
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>,
          GTT extends AbstractGT<GTT>,
          PairingT extends AbstractPairing<G1T, G2T, GTT>>
      CRS<FieldT, G1T, G2T, GTT> generate(
          final R1CSRelation<FieldT> r1cs,
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
    final QAPRelation<FieldT> qap = R1CStoQAP.R1CStoQAPRelation(r1cs, t);

    System.out.println("\tQAP - primary input size: " + qap.numInputs());
    System.out.println("\tQAP - total input size: " + qap.numVariables());
    System.out.println("\tQAP - pre degree: " + r1cs.numConstraints());
    System.out.println("\tQAP - degree: " + qap.degree());

    final int numInputs = qap.numInputs();
    final int numVariables = qap.numVariables();

    // The gamma inverse product component: (beta*A_i(t) + alpha*B_i(t) + C_i(t)) * gamma^{-1}.
    config.beginLog("Computing gammaABC for R1CS verification key");
    final List<FieldT> gammaABC = new ArrayList<>(numInputs);
    for (int i = 0; i < numInputs; i++) {
      gammaABC.add(beta.mul(qap.At(i)).add(alpha.mul(qap.Bt(i))).add(qap.Ct(i)).mul(inverseGamma));
    }
    config.endLog("Computing gammaABC for R1CS verification key");

    // The delta inverse product component: (beta*A_i(t) + alpha*B_i(t) + C_i(t)) * delta^{-1}.
    config.beginLog("Computing deltaABC for R1CS proving key");
    final List<FieldT> deltaABC = new ArrayList<>(numVariables - numInputs);
    for (int i = numInputs; i < numVariables; i++) {
      deltaABC.add(beta.mul(qap.At(i)).add(alpha.mul(qap.Bt(i))).add(qap.Ct(i)).mul(inverseDelta));
    }
    config.endLog("Computing deltaABC for R1CS proving key");

    config.beginLog("Computing query densities");
    int nonZeroAt = 0;
    int nonZeroBt = 0;
    for (int i = 0; i < qap.numVariables(); i++) {
      if (!qap.At(i).isZero()) {
        nonZeroAt++;
      }
      if (!qap.Bt(i).isZero()) {
        nonZeroBt++;
      }
    }
    config.endLog("Computing query densities");

    config.beginLog("Generating G1 MSM Window Table");
    final G1T generatorG1 = g1Factory.random(config.seed(), config.secureSeed());
    final int scalarCountG1 = nonZeroAt + nonZeroBt + numVariables;
    final int scalarSizeG1 = generatorG1.bitSize();
    final int windowSizeG1 = FixedBaseMSM.getWindowSize(scalarCountG1, generatorG1);
    final List<List<G1T>> windowTableG1 =
        FixedBaseMSM.getWindowTable(generatorG1, scalarSizeG1, windowSizeG1);
    config.endLog("Generating G1 MSM Window Table");

    config.beginLog("Generating G2 MSM Window Table");
    final G2T generatorG2 = g2Factory.random(config.seed(), config.secureSeed());
    final int scalarCountG2 = nonZeroBt;
    final int scalarSizeG2 = generatorG2.bitSize();
    final int windowSizeG2 = FixedBaseMSM.getWindowSize(scalarCountG2, generatorG2);
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

    config.beginLog("Encode deltaABC for R1CS proving key", false);
    final List<G1T> deltaABCG1 =
        FixedBaseMSM.batchMSM(scalarSizeG1, windowSizeG1, windowTableG1, deltaABC);
    config.endLog("Encode deltaABC for R1CS proving key", false);

    config.beginLog("Computing query A", false);
    final List<G1T> queryA =
        FixedBaseMSM.batchMSM(scalarSizeG1, windowSizeG1, windowTableG1, qap.At());
    config.endLog("Computing query A", false);

    config.beginLog("Computing query B", false);
    final List<Tuple2<G1T, G2T>> queryB =
        FixedBaseMSM.doubleBatchMSM(
            scalarSizeG1,
            windowSizeG1,
            windowTableG1,
            scalarSizeG2,
            windowSizeG2,
            windowTableG2,
            qap.Bt());
    config.endLog("Computing query B", false);

    config.beginLog("Computing query H", false);
    final FieldT inverseDeltaZt = qap.Zt().mul(delta.inverse());
    for (int i = 0; i < qap.Ht().size(); i++) {
      qap.Ht().set(i, qap.Ht().get(i).mul(inverseDeltaZt));
    }
    final List<G1T> queryH =
        FixedBaseMSM.batchMSM(scalarSizeG1, windowSizeG1, windowTableG1, qap.Ht());
    config.endLog("Computing query H", false);

    config.endLog("Generating R1CS proving key");
    config.endRuntime("Proving Key");

    config.beginLog("Generating R1CS verification key");
    config.beginRuntime("Verification Key");

    final GTT alphaG1betaG2 = pairing.reducedPairing(alphaG1, betaG2);
    final G2T gammaG2 = generatorG2.mul(gamma);

    config.beginLog("Encoding gammaABC for R1CS verification key");
    final List<G1T> gammaABCG1 =
        FixedBaseMSM.batchMSM(scalarSizeG1, windowSizeG1, windowTableG1, gammaABC);
    config.endLog("Encoding gammaABC for R1CS verification key");

    config.endLog("Generating R1CS verification key");
    config.endRuntime("Verification Key");

    // Construct the proving key.
    final ProvingKey<FieldT, G1T, G2T> provingKey =
        new ProvingKey<>(
            alphaG1, betaG1, betaG2, deltaG1, deltaG2, deltaABCG1, queryA, queryB, queryH, r1cs);

    // Construct the verification key.
    final VerificationKey<G1T, G2T, GTT> verificationKey =
        new VerificationKey<>(alphaG1betaG2, gammaG2, deltaG2, gammaABCG1);

    return new CRS<>(provingKey, verificationKey);
  }
}
