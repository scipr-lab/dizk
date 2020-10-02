package profiler;

import configuration.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.storage.StorageLevel;
import profiler.profiling.*;
import profiler.utils.SparkUtils;

public class Profiler {

  public static void serialApp(final String app, final Configuration config, final long size) {
    System.out.format("\n[Profiler] - Start Serial %s - %d size\n", SparkUtils.appName(app), size);

    if (app.equals("fft")) {
      FFTProfiling.serialFFTProfiling(config, size);
    } else if (app.equals("lagrange")) {
      LagrangeProfiling.serialLagrangeProfiling(config, size);
    } else if (app.equals("fmsm-g1")) {
      FixedBaseMSMProfiling.serialFixedBaseMSMG1Profiling(config, size);
    } else if (app.equals("fmsm-g2")) {
      FixedBaseMSMProfiling.serialFixedBaseMSMG2Profiling(config, size);
    } else if (app.equals("vmsm-g1")) {
      VariableBaseMSMProfiling.serialVariableBaseMSMG1Profiling(config, size);
    } else if (app.equals("vmsm-g2")) {
      VariableBaseMSMProfiling.serialVariableBaseMSMG2Profiling(config, size);
    } else if (app.equals("relation")) {
      R1CStoQAPRelationProfiling.serialQAPRelation(config, size);
    } else if (app.equals("witness")) {
      R1CStoQAPWitnessProfiling.serialQAPWitness(config, size);
    } else if (app.equals("zksnark")) {
      ZKSNARKProfiling.serialzkSNARKProfiling(config, size);
    } else if (app.equals("zksnark-large")) {
      ZKSNARKProfiling.serialzkSNARKLargeProfiling(config, size);
    }

    System.out.format("\n[Profiler] - End Serial %s - %d size\n", SparkUtils.appName(app), size);
  }

  public static void distributedApp(final String app, final Configuration config, final long size) {
    System.out.format(
        "\n[Profiler] - Start Distributed %s - %d executors - %d partitions - %d size\n\n",
        SparkUtils.appName(app), config.numExecutors(), config.numPartitions(), size);

    if (app.equals("fft")) {
      FFTProfiling.distributedFFTProfiling(config, size);
    } else if (app.equals("lagrange")) {
      LagrangeProfiling.distributedLagrangeProfiling(config, size);
    } else if (app.equals("fmsm-g1")) {
      FixedBaseMSMProfiling.distributedFixedBaseMSMG1Profiling(config, size);
    } else if (app.equals("fmsm-g2")) {
      FixedBaseMSMProfiling.distributedFixedBaseMSMG2Profiling(config, size);
    } else if (app.equals("vmsm-g1")) {
      VariableBaseMSMProfiling.distributedVariableBaseMSMG1Profiling(config, size);
    } else if (app.equals("vmsm-g2")) {
      VariableBaseMSMProfiling.distributedVariableBaseMSMG2Profiling(config, size);
    } else if (app.equals("relation")) {
      R1CStoQAPRelationProfiling.distributedQAPRelation(config, size);
    } else if (app.equals("witness")) {
      R1CStoQAPWitnessProfiling.distributedQAPWitness(config, size);
    } else if (app.equals("zksnark")) {
      ZKSNARKProfiling.distributedzkSNARKProfiling(config, size);
    } else if (app.equals("zksnark-large")) {
      ZKSNARKProfiling.distributedzkSNARKLargeProfiling(config, size);
    } else if (app.equals("vmsm-sorted-g1")) {
      VariableBaseMSMProfiling.distributedSortedVariableBaseMSMG1Profiling(config, size);
    }

    System.out.format(
        "\n[Profiler] - End Distributed %s - %d executors - %d partitions - %d size\n\n",
        SparkUtils.appName(app), config.numExecutors(), config.numPartitions(), size);
  }

  public static void matmulTest(
      final Configuration config, int n1, int n2, int n3, int b1, int b2, int b3, String app) {
    if (app.equals("matmul")) {
      MatrixMultiplicationProfiling.MatrixMultiplicationProfile(config, n1, n2, n3, b1, b2, b3);
    } else {
      MatrixMultiplicationProfiling.MatrixMultiplicationProfile(config, n1, n2, n3, b1, b2, b3);
    }
  }

  public static void lrTest(final Configuration config, int n, int d, int bn, int bd, String app) {
    if (app.equals("regression")) {
      MatrixMultiplicationProfiling.LRProfile(config, n, d, bn, bd);
    }
  }

  public static void matrixMultiplicationTest(
      final Configuration config, int n1, int n2, int n3, int b1, int b2, int b3) {
    MatrixMultiplicationProfiling.MatrixMultiplicationProfile(config, n1, n2, n3, b1, b2, b3);
  }

  public static void lrTest(final Configuration config, int n, int d, int bn, int bd) {
    MatrixMultiplicationProfiling.LRProfile(config, n, d, bn, bd);
  }

  public static void gaussianTest(final Configuration config, int n, int d, int bn, int bd) {
    MatrixMultiplicationProfiling.GaussianProfile(config, n, d, bn, bd);
  }

  public static void main(String[] args) {
    if (args.length > 0) {
      String input = args[0].toLowerCase();
      if (input.equals("matmul") || input.equals("matmul_full")) {
        final String app = args[0].toLowerCase();
        final int numExecutors = Integer.parseInt(args[1]);
        final int numCores = Integer.parseInt(args[2]);
        final int numMemory = Integer.parseInt(args[3].substring(0, args[3].length() - 1));
        final int numPartitions = Integer.parseInt(args[4]);

        final SparkSession spark =
            SparkSession.builder().appName(SparkUtils.appName(app)).getOrCreate();
        spark.sparkContext().conf().set("spark.files.overwrite", "true");
        spark
            .sparkContext()
            .conf()
            .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        spark.sparkContext().conf().registerKryoClasses(SparkUtils.zksparkClasses());

        JavaSparkContext sc;
        sc = new JavaSparkContext(spark.sparkContext());
        final Configuration config =
            new Configuration(
                numExecutors,
                numCores,
                numMemory,
                numPartitions,
                sc,
                StorageLevel.MEMORY_AND_DISK_SER());

        final int n1 = Integer.parseInt(args[5]);
        final int n2 = Integer.parseInt(args[6]);
        final int n3 = Integer.parseInt(args[7]);
        final int b1 = Integer.parseInt(args[8]);
        final int b2 = Integer.parseInt(args[9]);
        final int b3 = Integer.parseInt(args[10]);

        matmulTest(config, n1, n2, n3, b1, b2, b3, input);
      } else if (input.equals("regression")) {
        final String app = args[0].toLowerCase();
        final int numExecutors = Integer.parseInt(args[1]);
        final int numCores = Integer.parseInt(args[2]);
        final int numMemory = Integer.parseInt(args[3].substring(0, args[3].length() - 1));
        final int numPartitions = Integer.parseInt(args[4]);

        final SparkSession spark =
            SparkSession.builder().appName(SparkUtils.appName(app)).getOrCreate();
        spark.sparkContext().conf().set("spark.files.overwrite", "true");
        spark
            .sparkContext()
            .conf()
            .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        spark.sparkContext().conf().registerKryoClasses(SparkUtils.zksparkClasses());

        JavaSparkContext sc;
        sc = new JavaSparkContext(spark.sparkContext());
        final Configuration config =
            new Configuration(
                numExecutors,
                numCores,
                numMemory,
                numPartitions,
                sc,
                StorageLevel.MEMORY_AND_DISK_SER());

        final int n = Integer.parseInt(args[5]);
        final int d = Integer.parseInt(args[6]);
        final int bn = Integer.parseInt(args[7]);
        final int bd = Integer.parseInt(args[8]);

        lrTest(config, n, d, bn, bd, input);

      } else if (input.equals("gaussian")) {
        final String app = args[0].toLowerCase();
        final int numExecutors = Integer.parseInt(args[1]);
        final int numCores = Integer.parseInt(args[2]);
        final int numMemory = Integer.parseInt(args[3].substring(0, args[3].length() - 1));
        final int numPartitions = Integer.parseInt(args[4]);

        final SparkSession spark =
            SparkSession.builder().appName(SparkUtils.appName(app)).getOrCreate();
        spark.sparkContext().conf().set("spark.files.overwrite", "true");
        spark
            .sparkContext()
            .conf()
            .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        spark.sparkContext().conf().registerKryoClasses(SparkUtils.zksparkClasses());

        JavaSparkContext sc;
        sc = new JavaSparkContext(spark.sparkContext());
        final Configuration config =
            new Configuration(
                numExecutors,
                numCores,
                numMemory,
                numPartitions,
                sc,
                StorageLevel.MEMORY_AND_DISK_SER());

        final int n = Integer.parseInt(args[5]);
        final int d = Integer.parseInt(args[6]);
        final int bn = Integer.parseInt(args[7]);
        final int bd = Integer.parseInt(args[8]);

        gaussianTest(config, n, d, bn, bd);
      } else if (args.length == 2) {
        final String app = args[0].toLowerCase();
        final long size = (long) Math.pow(2, Long.parseLong(args[1]));

        final Configuration config = new Configuration();
        serialApp(app, config, size);
      } else if (args.length == 5 || args.length == 6) {
        final int numExecutors = Integer.parseInt(args[0]);
        final int numCores = Integer.parseInt(args[1]);
        final int numMemory = Integer.parseInt(args[2].substring(0, args[2].length() - 1));
        final String app = args[3].toLowerCase();
        final long size = (long) Math.pow(2, Long.parseLong(args[4]));
        int numPartitions;
        if (args.length == 5) {
          numPartitions = SparkUtils.numPartitions(numExecutors, size);
        } else {
          numPartitions = Integer.parseInt(args[5]);
        }

        final SparkSession spark =
            SparkSession.builder().appName(SparkUtils.appName(app)).getOrCreate();
        spark.sparkContext().conf().set("spark.files.overwrite", "true");
        spark
            .sparkContext()
            .conf()
            .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        spark.sparkContext().conf().registerKryoClasses(SparkUtils.zksparkClasses());

        JavaSparkContext sc;
        sc = new JavaSparkContext(spark.sparkContext());

        final Configuration config =
            new Configuration(
                numExecutors,
                numCores,
                numMemory,
                numPartitions,
                sc,
                StorageLevel.MEMORY_AND_DISK_SER());

        distributedApp(app, config, size);
      } else {
        System.out.println(
            "Args: {numExecutors} {numCores} {numMemory} {app} {size (log2)} {numPartitions(opt)}");
      }
    } else {
      final String app = "zksnark-large";
      final int numExecutors = 1;
      final int numCores = 1;
      final int numMemory = 8;
      final int size = 1024;

      final int numPartitions = SparkUtils.numPartitions(numExecutors, size);

      final SparkConf conf = new SparkConf().setMaster("local").setAppName("default");
      conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
      conf.set("spark.kryo.registrationRequired", "true");
      conf.registerKryoClasses(SparkUtils.zksparkClasses());

      JavaSparkContext sc;
      sc = new JavaSparkContext(conf);

      final Configuration config =
          new Configuration(
              numExecutors,
              numCores,
              numMemory,
              numPartitions,
              sc,
              StorageLevel.MEMORY_AND_DISK_SER());
      distributedApp(app, config, size);
    }
  }
}
