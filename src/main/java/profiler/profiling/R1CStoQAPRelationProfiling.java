package profiler.profiling;

import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import configuration.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import profiler.generation.R1CSConstruction;
import reductions.r1cs_to_qap.R1CStoQAP;
import reductions.r1cs_to_qap.R1CStoQAPRDD;
import relations.objects.Assignment;
import relations.qap.QAPRelation;
import relations.qap.QAPRelationRDD;
import relations.r1cs.R1CSRelation;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple3;

public class R1CStoQAPRelationProfiling {

  public static void serialQAPRelation(final Configuration config, final long numConstraints) {
    final BN254aFr fieldFactory = new BN254aFr(2L);
    final int numInputs = 1023;

    final Tuple3<R1CSRelation<BN254aFr>, Assignment<BN254aFr>, Assignment<BN254aFr>> R1CSExample =
        R1CSConstruction.serialConstruct((int) numConstraints, numInputs, fieldFactory, config);
    final R1CSRelation<BN254aFr> r1cs = R1CSExample._1();

    config.setContext("QAPRelation-Serial");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginRuntime("QAPRelation");
    final QAPRelation<BN254aFr> qapWitness =
        R1CStoQAP.R1CStoQAPRelation(r1cs, fieldFactory); // UNUSED
    config.endRuntime("QAPRelation");

    config.writeRuntimeLog(config.context());
  }

  public static void distributedQAPRelation(final Configuration config, final long numConstraints) {
    final BN254aFr fieldFactory = new BN254aFr(2L);
    final int numInputs = 1023;

    final Tuple3<R1CSRelationRDD<BN254aFr>, Assignment<BN254aFr>, JavaPairRDD<Long, BN254aFr>>
        R1CSExampleRDD =
            R1CSConstruction.parallelConstruct(numConstraints, numInputs, fieldFactory, config);
    final R1CSRelationRDD<BN254aFr> r1csRDD = R1CSExampleRDD._1();

    config.setContext("QAPRelation");
    config.beginRuntimeMetadata("Size (inputs)", numConstraints);

    config.beginLog(config.context());
    config.beginRuntime("QAPRelation");
    final QAPRelationRDD<BN254aFr> qapWitnessRDD =
        R1CStoQAPRDD.R1CStoQAPRelation(r1csRDD, fieldFactory, config);
    qapWitnessRDD.At().count();
    qapWitnessRDD.Bt().count();
    qapWitnessRDD.Ct().count();
    config.endRuntime("QAPRelation");
    config.endLog(config.context());

    config.writeRuntimeLog(config.context());
  }
}
