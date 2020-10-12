package profiler.profiling;

import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.BN254aG1;
import algebra.curves.barreto_naehrig.bn254a.BN254aG2;
import algebra.curves.barreto_naehrig.bn254a.BN254aGT;
import algebra.curves.barreto_naehrig.bn254a.BN254aPairing;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFr;
import algebra.curves.barreto_naehrig.bn254b.BN254bG1;
import algebra.curves.barreto_naehrig.bn254b.BN254bG2;
import algebra.curves.barreto_naehrig.bn254b.BN254bGT;
import algebra.curves.barreto_naehrig.bn254b.BN254bPairing;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG1Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG2Parameters;
import configuration.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import profiler.generation.R1CSConstructor;
import relations.objects.Assignment;
import relations.r1cs.R1CSRelation;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple3;
import zk_proof_systems.zkSNARK.*;
import zk_proof_systems.zkSNARK.objects.CRS;
import zk_proof_systems.zkSNARK.objects.Proof;

public class ZKSNARKProfiling {

  public static void serialzkSNARKProfiling(final Configuration config, final long numConstraints) {
    final int numInputs = 1023;

    final BN254aFr fieldFactory = new BN254aFr(2L);
    final BN254aG1 g1Factory = new BN254aG1Parameters().ONE();
    final BN254aG2 g2Factory = new BN254aG2Parameters().ONE();
    final BN254aPairing pairing = new BN254aPairing();

    final Tuple3<R1CSRelation<BN254aFr>, Assignment<BN254aFr>, Assignment<BN254aFr>> construction =
        R1CSConstructor.serialConstruct((int) numConstraints, numInputs, fieldFactory, config);
    final R1CSRelation<BN254aFr> r1cs = construction._1();
    final Assignment<BN254aFr> primary = construction._2();
    final Assignment<BN254aFr> auxiliary = construction._3();

    config.setContext("Setup-Serial");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Setup");
    final CRS<BN254aFr, BN254aG1, BN254aG2, BN254aGT> CRS =
        SerialSetup.generate(r1cs, fieldFactory, g1Factory, g2Factory, pairing, config);
    config.endLog(config.context());
    config.endRuntime("Setup");

    config.writeRuntimeLog(config.context());

    config.setContext("Prover-Serial");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Prover");
    final Proof<BN254aG1, BN254aG2> proof =
        SerialProver.prove(CRS.provingKey(), primary, auxiliary, fieldFactory, config);
    config.endLog(config.context());
    config.endRuntime("Prover");

    config.writeRuntimeLog(config.context());

    config.setContext("Verifier-for-Serial");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Verifier");
    final boolean isValid = Verifier.verify(CRS.verificationKey(), primary, proof, pairing, config);
    config.beginRuntimeMetadata("isValid", isValid ? 1L : 0L);
    config.endLog(config.context());
    config.endRuntime("Verifier");

    config.writeRuntimeLog(config.context());

    System.out.println(isValid);
    assert (isValid);
  }

  public static void serialzkSNARKLargeProfiling(
      final Configuration config, final long numConstraints) {
    final int numInputs = 1023;

    final BN254bFr fieldFactory = new BN254bFr(2L);
    final BN254bG1 g1Factory = new BN254bG1Parameters().ONE();
    final BN254bG2 g2Factory = new BN254bG2Parameters().ONE();
    final BN254bPairing pairing = new BN254bPairing();

    final Tuple3<R1CSRelation<BN254bFr>, Assignment<BN254bFr>, Assignment<BN254bFr>> construction =
        R1CSConstructor.serialConstruct((int) numConstraints, numInputs, fieldFactory, config);
    final R1CSRelation<BN254bFr> r1cs = construction._1();
    final Assignment<BN254bFr> primary = construction._2();
    final Assignment<BN254bFr> auxiliary = construction._3();

    config.setContext("Setup-Serial");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Setup");
    final CRS<BN254bFr, BN254bG1, BN254bG2, BN254bGT> CRS =
        SerialSetup.generate(r1cs, fieldFactory, g1Factory, g2Factory, pairing, config);
    config.endLog(config.context());
    config.endRuntime("Setup");

    config.writeRuntimeLog(config.context());

    config.setContext("Prover-Serial");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Prover");
    final Proof<BN254bG1, BN254bG2> proof =
        SerialProver.prove(CRS.provingKey(), primary, auxiliary, fieldFactory, config);
    config.endLog(config.context());
    config.endRuntime("Prover");

    config.writeRuntimeLog(config.context());

    config.setContext("Verifier-for-Serial");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Verifier");
    final boolean isValid = Verifier.verify(CRS.verificationKey(), primary, proof, pairing, config);
    config.beginRuntimeMetadata("isValid", isValid ? 1L : 0L);
    config.endLog(config.context());
    config.endRuntime("Verifier");

    config.writeRuntimeLog(config.context());

    System.out.println(isValid);
    assert (isValid);
  }

  public static void distributedzkSNARKProfiling(
      final Configuration config, final long numConstraints) {
    final int numInputs = 1023;

    final BN254aFr fieldFactory = new BN254aFr(2L);
    final BN254aG1 g1Factory = new BN254aG1Parameters().ONE();
    final BN254aG2 g2Factory = new BN254aG2Parameters().ONE();
    final BN254aPairing pairing = new BN254aPairing();

    final Tuple3<R1CSRelationRDD<BN254aFr>, Assignment<BN254aFr>, JavaPairRDD<Long, BN254aFr>>
        construction =
            R1CSConstructor.parallelConstruct(numConstraints, numInputs, fieldFactory, config);
    final R1CSRelationRDD<BN254aFr> r1cs = construction._1();
    final Assignment<BN254aFr> primary = construction._2();
    final JavaPairRDD<Long, BN254aFr> fullAssignment = construction._3();

    config.setContext("Setup");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Setup");
    final CRS<BN254aFr, BN254aG1, BN254aG2, BN254aGT> CRS =
        DistributedSetup.generate(r1cs, fieldFactory, g1Factory, g2Factory, pairing, config);
    config.endLog(config.context());
    config.endRuntime("Setup");

    config.writeRuntimeLog(config.context());

    config.setContext("Prover");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Prover");
    final Proof<BN254aG1, BN254aG2> proof =
        DistributedProver.prove(CRS.provingKeyRDD(), primary, fullAssignment, fieldFactory, config);
    config.endLog(config.context());
    config.endRuntime("Prover");

    config.writeRuntimeLog(config.context());

    config.setContext("Verifier-for-");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Verifier");
    final boolean isValid = Verifier.verify(CRS.verificationKey(), primary, proof, pairing, config);
    config.beginRuntimeMetadata("isValid", isValid ? 1L : 0L);
    config.endLog(config.context());
    config.endRuntime("Verifier");

    config.writeRuntimeLog(config.context());

    System.out.println(isValid);
    assert (isValid);
  }

  public static void distributedzkSNARKLargeProfiling(
      final Configuration config, final long numConstraints) {
    final int numInputs = 1023;

    final BN254bFr fieldFactory = new BN254bFr(2L);
    final BN254bG1 g1Factory = new BN254bG1Parameters().ONE();
    final BN254bG2 g2Factory = new BN254bG2Parameters().ONE();
    final BN254bPairing pairing = new BN254bPairing();

    final Tuple3<R1CSRelationRDD<BN254bFr>, Assignment<BN254bFr>, JavaPairRDD<Long, BN254bFr>>
        construction =
            R1CSConstructor.parallelConstruct(numConstraints, numInputs, fieldFactory, config);
    final R1CSRelationRDD<BN254bFr> r1cs = construction._1();
    final Assignment<BN254bFr> primary = construction._2();
    final JavaPairRDD<Long, BN254bFr> fullAssignment = construction._3();

    config.setContext("Setup");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Setup");
    final CRS<BN254bFr, BN254bG1, BN254bG2, BN254bGT> CRS =
        DistributedSetup.generate(r1cs, fieldFactory, g1Factory, g2Factory, pairing, config);
    config.endLog(config.context());
    config.endRuntime("Setup");

    config.writeRuntimeLog(config.context());

    config.setContext("Prover");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Prover");
    final Proof<BN254bG1, BN254bG2> proof =
        DistributedProver.prove(CRS.provingKeyRDD(), primary, fullAssignment, fieldFactory, config);
    config.endLog(config.context());
    config.endRuntime("Prover");

    config.writeRuntimeLog(config.context());

    config.setContext("Verifier-for-");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Verifier");
    final boolean isValid = Verifier.verify(CRS.verificationKey(), primary, proof, pairing, config);
    config.beginRuntimeMetadata("isValid", isValid ? 1L : 0L);
    config.endLog(config.context());
    config.endRuntime("Verifier");

    config.writeRuntimeLog(config.context());

    System.out.println(isValid);
    assert (isValid);
  }
}
