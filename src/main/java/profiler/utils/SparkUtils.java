package profiler.utils;

import algebra.fields.*;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.*;
import algebra.curves.barreto_naehrig.bn254a.BN254aG1;
import algebra.curves.barreto_naehrig.bn254a.BN254aG2;
import algebra.curves.barreto_naehrig.bn254a.BN254aGT;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.*;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.*;
import algebra.curves.barreto_naehrig.bn254b.BN254bG1;
import algebra.curves.barreto_naehrig.bn254b.BN254bG2;
import algebra.curves.barreto_naehrig.bn254b.BN254bGT;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.util.AccumulatorContext;
import relations.objects.LinearTerm;
import scala.Tuple2;
import scala.collection.mutable.WrappedArray;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.atomic.LongAccumulator;

public class SparkUtils {

    public static String appName(final String s) {
        if (s.equals("fft")) {
            return "SparkFFT";
        } else if (s.equals("lagrange")) {
            return "Lagrange";
        } else if (s.equals("fmsm-g1")) {
            return "SparkFixedMSMG1";
        } else if (s.equals("fmsm-g2")) {
            return "SparkFixedMSMG2";
        } else if (s.equals("vmsm-g1")) {
            return "SparkVariableMSMG1";
        } else if (s.equals("vmsm-g2")) {
            return "SparkVariableMSMG2";
        } else if (s.equals("relation")) {
            return "SparkQAPRelation";
        } else if (s.equals("witness")) {
            return "SparkQAPWitness";
        } else if (s.equals("zksnark")) {
            return "SparkZKSNARK";
        } else if (s.equals("zksnark-large")) {
            return "SparkZKSNARKLarge";
        } else if (s.equals("vmsm-sorted-g1")) {
            return "SparkVMSMSortedG1";
        }
        return "default";
    }

    public static Class[] zksparkClasses() {
        return new Class<?>[]{
                Fp.class,
                Fp2.class,
                Fp6_3Over2.class,
                Fp12_2Over3Over2.class,
                Fp[].class,
                AbstractFieldElementExpanded.class,
                BN254aFr.class,
                BN254aFq.class,
                BN254aFq2.class,
                BN254aFq6.class,
                BN254aFq12.class,
                BN254aFrParameters.class,
                BN254aFqParameters.class,
                BN254aFq2Parameters.class,
                BN254aFq6Parameters.class,
                BN254aFq12Parameters.class,
                BN254aG1.class,
                BN254aG2.class,
                BN254aGT.class,
                BN254aG1Parameters.class,
                BN254aG2Parameters.class,
                BN254aGTParameters.class,
                BN254bFr.class,
                BN254bFq.class,
                BN254bFq2.class,
                BN254bFq6.class,
                BN254bFq12.class,
                BN254bFrParameters.class,
                BN254bFqParameters.class,
                BN254bFq2Parameters.class,
                BN254bFq6Parameters.class,
                BN254bFq12Parameters.class,
                BN254bG1.class,
                BN254bG2.class,
                BN254bGT.class,
                BN254bG1Parameters.class,
                BN254bG2Parameters.class,
                BN254bGTParameters.class,
                LinearTerm.class,
                Tuple2.class,
                JavaPairRDD.class,
                BigInteger.class,
                ArrayList.class,
                WrappedArray.ofRef.class,
                Object[].class,
                AccumulatorContext.class,
                LongAccumulator.class,
                java.lang.Class.class,
                scala.reflect.ClassTag.class
        };
    }

    public static int numPartitions(final int numExecutors, final long size) {
        return size >= (long) Math.pow(2, 25) ? 8 * numExecutors : numExecutors;
    }

    public static void cleanDirectory(final String path) {
        try {
            File f = new File(path);
            if (f.isDirectory()) {
                org.apache.commons.io.FileUtils.cleanDirectory(f);
                org.apache.commons.io.FileUtils.forceDelete(f);
            }

            Configuration conf = new Configuration();
            Path output = new Path(path);
            FileSystem hdfs = FileSystem.get(conf);
            if (hdfs.exists(output)) {
                hdfs.delete(output, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
