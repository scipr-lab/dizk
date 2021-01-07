/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package zk_proof_systems.zkSNARK;

import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.curves.barreto_naehrig.*;
import algebra.curves.barreto_naehrig.BNFields.*;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG1Parameters;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG2Parameters;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNGTParameters;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFr;
import algebra.curves.barreto_naehrig.bn254b.BN254bG1;
import algebra.curves.barreto_naehrig.bn254b.BN254bG2;
import algebra.curves.barreto_naehrig.bn254b.BN254bPairing;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG1Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG2Parameters;
import algebra.curves.fake.*;
import configuration.Configuration;
import java.io.Serializable;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import profiler.generation.R1CSConstructor;
import profiler.utils.SparkUtils;
import relations.objects.Assignment;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple3;
import zk_proof_systems.zkSNARK.objects.CRS;
import zk_proof_systems.zkSNARK.objects.Proof;

public class DistributedzkSNARKTest implements Serializable {
  private transient JavaSparkContext sc;
  private Configuration config;

  @BeforeEach
  public void setUp() {
    final SparkConf conf = new SparkConf().setMaster("local").setAppName("default");
    conf.set("spark.files.overwrite", "true");
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
    conf.registerKryoClasses(SparkUtils.zksparkClasses());

    sc = new JavaSparkContext(conf);

    config = new Configuration(1, 1, 1, 2, sc, StorageLevel.MEMORY_ONLY());
    config.setRuntimeFlag(false);
    config.setDebugFlag(true);
  }

  @AfterEach
  public void tearDown() {
    sc.stop();
    sc = null;
  }

  private <
          BNFrT extends BNFr<BNFrT>,
          BNFqT extends BNFq<BNFqT>,
          BNFq2T extends BNFq2<BNFqT, BNFq2T>,
          BNFq6T extends BNFq6<BNFqT, BNFq2T, BNFq6T>,
          BNFq12T extends BNFq12<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
          BNG1T extends BNG1<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
          BNG2T extends BNG2<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
          BNGTT extends BNGT<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
          BNG1ParametersT extends AbstractBNG1Parameters<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
          BNG2ParametersT extends
              AbstractBNG2Parameters<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
          BNGTParametersT extends
              AbstractBNGTParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
          BNPublicParametersT extends BNPublicParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
          BNPairingT extends
              BNPairing<
                      BNFrT,
                      BNFqT,
                      BNFq2T,
                      BNFq6T,
                      BNFq12T,
                      BNG1T,
                      BNG2T,
                      BNGTT,
                      BNG1ParametersT,
                      BNG2ParametersT,
                      BNGTParametersT,
                      BNPublicParametersT>>
      void DistributedBNProofSystemTest(
          final int numInputs,
          final int numConstraints,
          BNFrT fieldFactory,
          BNG1T g1Factory,
          BNG2T g2Factory,
          BNPairingT pairing) {
    final Tuple3<R1CSRelationRDD<BNFrT>, Assignment<BNFrT>, JavaPairRDD<Long, BNFrT>> construction =
        R1CSConstructor.parallelConstruct(numConstraints, numInputs, fieldFactory, config);
    final R1CSRelationRDD<BNFrT> r1cs = construction._1();
    final Assignment<BNFrT> primary = construction._2();
    final JavaPairRDD<Long, BNFrT> fullAssignment = construction._3();

    final CRS<BNFrT, BNG1T, BNG2T> CRS =
        DistributedSetup.generate(r1cs, fieldFactory, g1Factory, g2Factory, pairing, config);

    final Proof<BNG1T, BNG2T> proof =
        DistributedProver.prove(CRS.provingKeyRDD(), primary, fullAssignment, fieldFactory, config);

    final boolean isValid = Verifier.verify(CRS.verificationKey(), primary, proof, pairing, config);

    System.out.println(isValid);
    assertTrue(isValid);
  }

  // TODO:
  // Remove this comment when: https://github.com/clearmatics/dizk/issues/1
  // is fixed.
  /*
  @Test
  public void DistributedFakeProofSystemTest() {
      final int numInputs = 1023;
      final int numConstraints = 1024;

      FakeInitialize.init();
      final Fp fieldFactory = new FakeFqParameters().ONE();
      final FakeG1 fakeG1Factory = new FakeG1Parameters().ONE();
      final FakeG2 fakeG2Factory = new FakeG2Parameters().ONE();
      final FakePairing fakePairing = new FakePairing();

      final Tuple3<R1CSRelationRDD<Fp>, Assignment<Fp>, JavaPairRDD<Long, Fp>> construction =
              R1CSConstructor.parallelConstruct(numConstraints, numInputs, fieldFactory, config);
      final R1CSRelationRDD<Fp> r1cs = construction._1();
      final Assignment<Fp> primary = construction._2();
      final JavaPairRDD<Long, Fp> fullAssignment = construction._3();

      final CRS<Fp, FakeG1, FakeG2, FakeGT> CRS = DistributedSetup.generate(r1cs, fieldFactory,
              fakeG1Factory, fakeG2Factory, fakePairing, config);
      final Proof<FakeG1, FakeG2> proof = DistributedProver.prove(CRS.provingKeyRDD(), primary,
              fullAssignment, fieldFactory, config);
      final boolean isValid = Verifier.verify(CRS.verificationKey(), primary, proof,
              fakePairing, config);

      System.out.println(isValid);
      assertTrue(isValid);
  }

  @Test
  public void DistributedBN254aProofSystemTest() {
      final int numInputs = 1023;
      final int numConstraints = 1024;
      final BN254aFr fieldFactory = new BN254aFr(1);
      final BN254aG1 g1Factory = BN254aG1Parameters.ONE;
      final BN254aG2 g2Factory = BN254aG2Parameters.ONE;
      final BN254aPairing pairing = new BN254aPairing();

      DistributedBNProofSystemTest(numInputs, numConstraints, fieldFactory, g1Factory, g2Factory, pairing);
  }
  */

  @Test
  public void DistributedBN254bProofSystemTest() {
    final int numInputs = 1023;
    final int numConstraints = 1024;
    final BN254bFr fieldFactory = new BN254bFr(1);
    final BN254bG1 g1Factory = BN254bG1Parameters.ONE;
    final BN254bG2 g2Factory = BN254bG2Parameters.ONE;
    final BN254bPairing pairing = new BN254bPairing();

    DistributedBNProofSystemTest(
        numInputs, numConstraints, fieldFactory, g1Factory, g2Factory, pairing);
  }
}
