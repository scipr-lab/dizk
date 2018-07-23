package profiler.generation;

import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import configuration.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import profiler.utils.SparkUtils;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Random;

public class FixedBaseMSMGenerator {

    public static JavaPairRDD<Long, BN254aFr> generateData(
            final Configuration config,
            final long size) {
        final int numPartitions = config.numPartitions();

        final ArrayList<Integer> partitions = new ArrayList<>(numPartitions);
        for (int i = 0; i < numPartitions; i++) {
            partitions.add(i);
        }

        final BN254aFr fieldFactory = new BN254aFr(2L);
        final long totalSize = size;
        final Random rand = new Random(System.nanoTime());

        final JavaPairRDD<Long, BN254aFr> input = config.sparkContext()
                .parallelize(partitions, numPartitions).flatMapToPair(part -> {
                    final long partSize = totalSize / numPartitions;

                    final ArrayList<Tuple2<Long, BN254aFr>> data = new ArrayList<>();
                    for (long i = 0; i < partSize; i++) {
                        data.add(new Tuple2<>(
                                part * partSize + i,
                                fieldFactory.random(rand.nextLong(), null)));
                    }

                    return data.iterator();
                }).persist(config.storageLevel());

        input.count();

        return input;
    }

    public static void generateData(final Configuration config, final long size, final String path) {
        final String objectPath = path + "/fmsm-" + size;
        SparkUtils.cleanDirectory(objectPath);
        generateData(config, size).saveAsObjectFile(objectPath);
    }

}