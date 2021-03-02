/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package zk_proof_systems.zkSNARK.grothBGM17;

import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq12;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq2;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq6;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFr;
import algebra.curves.barreto_lynn_scott.BLSG1;
import algebra.curves.barreto_lynn_scott.BLSG2;
import algebra.curves.barreto_lynn_scott.BLSGT;
import algebra.curves.barreto_lynn_scott.BLSPairing;
import algebra.curves.barreto_lynn_scott.BLSPublicParameters;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSG1Parameters;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSG2Parameters;
import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSGTParameters;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G1;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Pairing;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G1Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G2Parameters;
// import algebra.curves.barreto_naehrig.*;
// import algebra.curves.barreto_naehrig.BNFields.*;
// import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG1Parameters;
// import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG2Parameters;
// import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNGTParameters;
// import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFr;
// import algebra.curves.barreto_naehrig.bn254b.BN254bG1;
// import algebra.curves.barreto_naehrig.bn254b.BN254bG2;
// import algebra.curves.barreto_naehrig.bn254b.BN254bPairing;
// import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG1Parameters;
// import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG2Parameters;
// import algebra.curves.mock.*;
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
import zk_proof_systems.zkSNARK.grothBGM17.objects.CRS;
import zk_proof_systems.zkSNARK.grothBGM17.objects.Proof;

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

  /*
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
            final BNFrT fieldFactory,
            final BNG1T g1Factory,
            final BNG2T g2Factory,
            final BNPairingT pairing) {
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
    //@Test
    //public void DistributedFakeProofSystemTest() {
    //    final int numInputs = 1023;
    //    final int numConstraints = 1024;
  //
    //    FakeInitialize.init();
    //    final Fp fieldFactory = new FakeFqParameters().ONE();
    //    final FakeG1 fakeG1Factory = new FakeG1Parameters().ONE();
    //    final FakeG2 fakeG2Factory = new FakeG2Parameters().ONE();
    //    final FakePairing fakePairing = new FakePairing();
  //
    //    final Tuple3<R1CSRelationRDD<Fp>, Assignment<Fp>, JavaPairRDD<Long, Fp>> construction =
    //            R1CSConstructor.parallelConstruct(numConstraints, numInputs, fieldFactory, config);
    //    final R1CSRelationRDD<Fp> r1cs = construction._1();
    //    final Assignment<Fp> primary = construction._2();
    //    final JavaPairRDD<Long, Fp> fullAssignment = construction._3();
  //
    //    final CRS<Fp, FakeG1, FakeG2, FakeGT> CRS = DistributedSetup.generate(r1cs, fieldFactory,
    //            fakeG1Factory, fakeG2Factory, fakePairing, config);
    //    final Proof<FakeG1, FakeG2> proof = DistributedProver.prove(CRS.provingKeyRDD(), primary,
    //            fullAssignment, fieldFactory, config);
    //    final boolean isValid = Verifier.verify(CRS.verificationKey(), primary, proof,
    //            fakePairing, config);
  //
    //    System.out.println(isValid);
    //    assertTrue(isValid);
    //}
    //

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

    // This test is also commented because running it along with `DistributedBLSProofSystemTest`
    // below triggers a NullPtr exception as reported in https://github.com/clearmatics/dizk/issues/1
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
    */

  private <
          BLSFrT extends BLSFr<BLSFrT>,
          BLSFqT extends BLSFq<BLSFqT>,
          BLSFq2T extends BLSFq2<BLSFqT, BLSFq2T>,
          BLSFq6T extends BLSFq6<BLSFqT, BLSFq2T, BLSFq6T>,
          BLSFq12T extends BLSFq12<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T>,
          BLSG1T extends BLSG1<BLSFrT, BLSFqT, BLSG1T, BLSG1ParametersT>,
          BLSG2T extends BLSG2<BLSFrT, BLSFqT, BLSFq2T, BLSG2T, BLSG2ParametersT>,
          BLSGTT extends BLSGT<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T, BLSGTT, BLSGTParametersT>,
          BLSG1ParametersT extends
              AbstractBLSG1Parameters<BLSFrT, BLSFqT, BLSG1T, BLSG1ParametersT>,
          BLSG2ParametersT extends
              AbstractBLSG2Parameters<BLSFrT, BLSFqT, BLSFq2T, BLSG2T, BLSG2ParametersT>,
          BLSGTParametersT extends
              AbstractBLSGTParameters<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T, BLSGTT, BLSGTParametersT>,
          BLSPublicParametersT extends BLSPublicParameters<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T>,
          BLSPairingT extends
              BLSPairing<
                      BLSFrT,
                      BLSFqT,
                      BLSFq2T,
                      BLSFq6T,
                      BLSFq12T,
                      BLSG1T,
                      BLSG2T,
                      BLSGTT,
                      BLSG1ParametersT,
                      BLSG2ParametersT,
                      BLSGTParametersT,
                      BLSPublicParametersT>>
      void DistributedBLSProofSystemTest(
          final int numInputs,
          final int numConstraints,
          final BLSFrT fieldFactory,
          final BLSG1T g1Factory,
          final BLSG2T g2Factory,
          final BLSPairingT pairing) {
    final Tuple3<R1CSRelationRDD<BLSFrT>, Assignment<BLSFrT>, JavaPairRDD<Long, BLSFrT>>
        construction =
            R1CSConstructor.parallelConstruct(numConstraints, numInputs, fieldFactory, config);
    final R1CSRelationRDD<BLSFrT> r1cs = construction._1();
    final CRS<BLSFrT, BLSG1T, BLSG2T> CRS =
        DistributedSetup.generate(r1cs, fieldFactory, g1Factory, g2Factory, pairing, config);

    final Assignment<BLSFrT> primary = construction._2();
    final JavaPairRDD<Long, BLSFrT> fullAssignment = construction._3();

    final Proof<BLSG1T, BLSG2T> proof =
        DistributedProver.prove(CRS.provingKeyRDD(), primary, fullAssignment, fieldFactory, config);

    final boolean isValid = Verifier.verify(CRS.verificationKey(), primary, proof, pairing, config);

    System.out.println(isValid);
    assertTrue(isValid);
  }

  @Test
  public void DistributedBLS12_377ProofSystemTest() {
    final int numInputs = 1023;
    final int numConstraints = 1024;
    final BLS12_377Fr fieldFactory = new BLS12_377Fr(1);
    final BLS12_377G1 g1Factory = BLS12_377G1Parameters.ONE;
    final BLS12_377G2 g2Factory = BLS12_377G2Parameters.ONE;
    final BLS12_377Pairing pairing = new BLS12_377Pairing();

    DistributedBLSProofSystemTest(
        numInputs, numConstraints, fieldFactory, g1Factory, g2Factory, pairing);
  }
}
