package profiler.profiling;

import algebra.fields.Fp;
import algebra.fields.fieldparameters.LargeFpParameters;
import configuration.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import profiler.generation.R1CSConstructor;
import reductions.r1cs_to_qap.R1CStoQAPRDD;
import relations.objects.Assignment;
import relations.qap.QAPWitnessRDD;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple3;

public class MatrixMultiplicationProfiling {

  public static void MatrixMultiplicationProfile(
      final Configuration config, int n1, int n2, int n3, int b1, int b2, int b3) {

    final LargeFpParameters FpParameters = new LargeFpParameters();
    final Fp fieldFactory = new Fp(1, FpParameters);

    config.beginLog("Matmul circuit generation");
    final Tuple3<R1CSRelationRDD<Fp>, Assignment<Fp>, JavaPairRDD<Long, Fp>> R1CSExampleRDD =
        R1CSConstructor.matmulParConstructApp(fieldFactory, b1, b2, b3, n1, n2, n3, config);
    config.endLog("Matmul circuit generation");

    final R1CSRelationRDD<Fp> r1csRDD = R1CSExampleRDD._1();
    final Assignment<Fp> primary = R1CSExampleRDD._2();
    final JavaPairRDD<Long, Fp> fullAssignmentRDD = R1CSExampleRDD._3();

    config.beginLog("Matmul witness");
    final QAPWitnessRDD<Fp> qapWitnessRDD =
        R1CStoQAPRDD.R1CStoQAPWitness(r1csRDD, primary, fullAssignmentRDD, fieldFactory, config);
    qapWitnessRDD.coefficientsH().count();
    config.endLog("Matmul witness");

    config.writeRuntimeLog(config.context());
  }

  public static void LRProfile(final Configuration config, int n, int d, int bn, int bd) {
    final LargeFpParameters FpParameters = new LargeFpParameters();
    final Fp fieldFactory = new Fp(1, FpParameters);

    config.beginLog("Linear regression circuit generation");
    final Tuple3<R1CSRelationRDD<Fp>, Assignment<Fp>, JavaPairRDD<Long, Fp>> R1CSExampleRDD =
        R1CSConstructor.linearRegressionApp(fieldFactory, config, n, d, bn, bd);
    config.endLog("Linear regression circuit generation");

    final R1CSRelationRDD<Fp> r1csRDD = R1CSExampleRDD._1();
    final Assignment<Fp> primary = R1CSExampleRDD._2();
    final JavaPairRDD<Long, Fp> fullAssignmentRDD = R1CSExampleRDD._3();

    config.beginLog("Linear regression witness");
    final QAPWitnessRDD<Fp> qapWitnessRDD =
        R1CStoQAPRDD.R1CStoQAPWitness(r1csRDD, primary, fullAssignmentRDD, fieldFactory, config);
    qapWitnessRDD.coefficientsH().count();
    config.endLog("Linear regression witness");

    config.writeRuntimeLog(config.context());
  }

  public static void GaussianProfile(final Configuration config, int n, int d, int bn, int bd) {
    final LargeFpParameters FpParameters = new LargeFpParameters();
    final Fp fieldFactory = new Fp(1, FpParameters);

    config.beginLog("Gaussian circuit generation");
    final Tuple3<R1CSRelationRDD<Fp>, Assignment<Fp>, JavaPairRDD<Long, Fp>> R1CSExampleRDD =
        R1CSConstructor.gaussianFitApp(fieldFactory, config, n, d, bn, bd);
    config.endLog("Gaussian circuit generation");

    final R1CSRelationRDD<Fp> r1csRDD = R1CSExampleRDD._1();
    final Assignment<Fp> primary = R1CSExampleRDD._2();
    final JavaPairRDD<Long, Fp> fullAssignmentRDD = R1CSExampleRDD._3();

    config.beginLog("Linear regression witness");
    final QAPWitnessRDD<Fp> qapWitnessRDD =
        R1CStoQAPRDD.R1CStoQAPWitness(r1csRDD, primary, fullAssignmentRDD, fieldFactory, config);
    qapWitnessRDD.coefficientsH().count();
    config.endLog("Linear regression witness");

    config.writeRuntimeLog(config.context());
  }
}
