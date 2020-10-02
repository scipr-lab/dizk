package profiler.profiling;

import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import configuration.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import profiler.generation.R1CSConstruction;
import reductions.r1cs_to_qap.R1CStoQAP;
import reductions.r1cs_to_qap.R1CStoQAPRDD;
import relations.objects.Assignment;
import relations.qap.QAPWitness;
import relations.qap.QAPWitnessRDD;
import relations.r1cs.R1CSRelation;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple3;

public class R1CStoQAPWitnessProfiling {

  public static void serialQAPWitness(final Configuration config, final long numConstraints) {
    final BN254aFr fieldFactory = new BN254aFr(2L);
    final int numInputs = 1023;

    final Tuple3<R1CSRelation<BN254aFr>, Assignment<BN254aFr>, Assignment<BN254aFr>> R1CSExample =
        R1CSConstruction.serialConstruct((int) numConstraints, numInputs, fieldFactory, config);
    final R1CSRelation<BN254aFr> r1cs = R1CSExample._1();
    final Assignment<BN254aFr> primary = R1CSExample._2();
    final Assignment<BN254aFr> auxiliary = R1CSExample._3();

    config.setContext("Witness-Serial");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginRuntime("Witness");
    final QAPWitness<BN254aFr> qapWitness =
        R1CStoQAP.R1CStoQAPWitness(r1cs, primary, auxiliary, fieldFactory, config); // UNUSED
    config.endRuntime("Witness");

    config.writeRuntimeLog(config.context());
  }

  public static void distributedQAPWitness(final Configuration config, final long numConstraints) {
    final BN254aFr fieldFactory = new BN254aFr(2L);
    final int numInputs = 1023;

    final Tuple3<R1CSRelationRDD<BN254aFr>, Assignment<BN254aFr>, JavaPairRDD<Long, BN254aFr>>
        R1CSExampleRDD =
            R1CSConstruction.parallelConstruct(numConstraints, numInputs, fieldFactory, config);
    final R1CSRelationRDD<BN254aFr> r1csRDD = R1CSExampleRDD._1();
    final Assignment<BN254aFr> primary = R1CSExampleRDD._2();
    final JavaPairRDD<Long, BN254aFr> fullAssignmentRDD = R1CSExampleRDD._3();

    config.setContext("Witness");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("Witness");
    final QAPWitnessRDD<BN254aFr> qapWitnessRDD =
        R1CStoQAPRDD.R1CStoQAPWitness(r1csRDD, primary, fullAssignmentRDD, fieldFactory, config);
    qapWitnessRDD.coefficientsH().count();
    config.endRuntime("Witness");
    config.endLog(config.context());

    config.writeRuntimeLog(config.context());
  }
}
