package profiler.generation;

import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.BN254aG1;
import algebra.curves.barreto_naehrig.bn254a.BN254aG2;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import configuration.Configuration;
import org.apache.spark.api.java.JavaRDD;
import profiler.utils.SparkUtils;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Random;

public class VariableBaseMSMGenerator {

    public static JavaRDD<Tuple2<BN254aFr, BN254aG1>> generateG1Data(
            final Configuration config,
            final long size) {
        final int numPartitions = config.numPartitions();

        final ArrayList<Integer> partitions = new ArrayList<>(numPartitions);
        for (int i = 0; i < numPartitions; i++) {
            partitions.add(i);
        }

        final long totalSize = size;
        final BN254aFr fieldFactory = new BN254aFr(2L);
        final BN254aG1 groupFactory = new BN254aG1Parameters().ONE();
        final Random rand = new Random(System.nanoTime());

        final JavaRDD<Tuple2<BN254aFr, BN254aG1>> input = config.sparkContext()
                .parallelize(partitions, numPartitions).flatMap(part -> {
                    final long partSize = totalSize / numPartitions;

                    final ArrayList<Tuple2<BN254aFr, BN254aG1>> data = new ArrayList<>();
                    for (long i = 0; i < partSize; i++) {
                        data.add(new Tuple2<>(
                                fieldFactory.random(rand.nextLong(), null),
                                groupFactory.random(rand.nextLong(), null)));
                    }

                    return data.iterator();
                }).persist(config.storageLevel());

        input.count();

        return input;
    }

    public static JavaRDD<Tuple2<BN254aFr, BN254aG2>> generateG2Data(
            final Configuration config,
            final long size) {
        final int numPartitions = config.numPartitions();

        final ArrayList<Integer> partitions = new ArrayList<>(numPartitions);
        for (int i = 0; i < numPartitions; i++) {
            partitions.add(i);
        }

        final long totalSize = size;
        final BN254aFr fieldFactory = new BN254aFr(2L);
        final BN254aG2 groupFactory = new BN254aG2Parameters().ONE();
        final Random rand = new Random(System.nanoTime());

        final JavaRDD<Tuple2<BN254aFr, BN254aG2>> input = config.sparkContext()
                .parallelize(partitions, numPartitions).flatMap(part -> {
                    final long partSize = totalSize / numPartitions;

                    final ArrayList<Tuple2<BN254aFr, BN254aG2>> data = new ArrayList<>();
                    for (long i = 0; i < partSize; i++) {
                        data.add(new Tuple2<>(
                                fieldFactory.random(rand.nextLong(), null),
                                groupFactory.random(rand.nextLong(), null)));
                    }

                    return data.iterator();
                }).persist(config.storageLevel());

        input.count();

        return input;
    }

    public static void generateG1Data(final Configuration config, final long size, final String path) {
        final String objectPath = path + "/vmsm-" + size;
        SparkUtils.cleanDirectory(objectPath);
        generateG1Data(config, size).saveAsObjectFile(objectPath);
    }

    public static void generateG2Data(final Configuration config, final long size, final String path) {
        final String objectPath = path + "/vmsm-" + size;
        SparkUtils.cleanDirectory(objectPath);
        generateG2Data(config, size).saveAsObjectFile(objectPath);
    }
}

