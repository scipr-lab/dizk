/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations;

import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.fields.Fp;
import algebra.fields.fieldparameters.LargeFpParameters;
import configuration.Configuration;
import java.io.Serializable;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import profiler.generation.R1CSConstruction;
import relations.objects.Assignment;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple3;

public class MatMulTest implements Serializable {
  private transient JavaSparkContext sc;
  private Configuration config;
  private Fp fieldFactory;

  @BeforeEach
  public void setUp() {
    sc = new JavaSparkContext("local", "ZKSparkTestSuite");
    config = new Configuration(4, 4, 1, 4, sc, StorageLevel.MEMORY_ONLY());
    fieldFactory = new LargeFpParameters().ONE();
  }

  @AfterEach
  public void tearDown() {
    sc.stop();
    sc = null;
  }

  @Test
  public void runMatmul() {
    // OLD: serial matmul witness generation
    // final Tuple3<R1CSRelationRDD<Fp>, Assignment<Fp>, JavaPairRDD<Long, Fp>> constructionRDD =
    // R1CSConstruction.matmulConstruct(n1, n2, n3, fieldFactory, config);
    // final R1CSRelationRDD<Fp> r1csRDD = constructionRDD._1();
    // final Assignment<Fp> primaryTwo = constructionRDD._2();
    // final JavaPairRDD<Long, Fp> oneFullAssignmentRDD = constructionRDD._3();

    // assertTrue(r1csRDD.isValid());
    // assertTrue(r1csRDD.isSatisfied(oneFullAssignmentRDD));

    // Parallel matmul generation test
    int n1 = 10;
    int n2 = 10;
    int n3 = 10;

    int b1 = 2;
    int b2 = 2;
    int b3 = 2;

    final Tuple3<R1CSRelationRDD<Fp>, Assignment<Fp>, JavaPairRDD<Long, Fp>> parConstructionRDD =
        R1CSConstruction.matmulParConstructApp(fieldFactory, b1, b2, b3, n1, n2, n3, config);
    // Retrieve each elements of the tuple by their index
    R1CSRelationRDD<Fp> r1csRDDPar = parConstructionRDD._1();
    Assignment<Fp> primaryTwoPar = parConstructionRDD._2();
    JavaPairRDD<Long, Fp> oneFullAssignmentRDDPar = parConstructionRDD._3();

    // Verify r1cs is valid
    assertTrue(r1csRDDPar.isValid());
    // Verify r1cs is satisfied with full assignment
    assertTrue(r1csRDDPar.isSatisfied(primaryTwoPar, oneFullAssignmentRDDPar));

    // Matrix vector product
    n1 = 20;
    n2 = 10;
    n3 = 1;

    b1 = 5;
    b2 = 5;
    b3 = 1;

    final Tuple3<R1CSRelationRDD<Fp>, Assignment<Fp>, JavaPairRDD<Long, Fp>> matVectorProduct =
        R1CSConstruction.matmulParConstructApp(fieldFactory, b1, b2, b3, n1, n2, n3, config);
    r1csRDDPar = matVectorProduct._1();
    primaryTwoPar = matVectorProduct._2();
    oneFullAssignmentRDDPar = matVectorProduct._3();

    // /* Verify r1cs is valid */
    assertTrue(r1csRDDPar.isValid());
    /* Verify r1cs is satisfied with full assignment */
    assertTrue(r1csRDDPar.isSatisfied(primaryTwoPar, oneFullAssignmentRDDPar));
  }

  @Test
  public void runLR() {
    final int n = 10;
    final int d = 10;
    final int bn = 5;
    final int bd = 5;

    final BN254aFr fieldFactory = new BN254aFr(2L);

    final Tuple3<R1CSRelationRDD<BN254aFr>, Assignment<BN254aFr>, JavaPairRDD<Long, BN254aFr>>
        LRProduct = R1CSConstruction.linearRegressionApp(fieldFactory, config, n, d, bn, bd);
    R1CSRelationRDD<BN254aFr> r1csRDDPar = LRProduct._1();
    Assignment<BN254aFr> primaryTwoPar = LRProduct._2();
    JavaPairRDD<Long, BN254aFr> oneFullAssignmentRDDPar = LRProduct._3();

    // /* Verify r1cs is valid */
    assertTrue(r1csRDDPar.isValid());
    /* Verify r1cs is satisfied with full assignment */
    assertTrue(r1csRDDPar.isSatisfied(primaryTwoPar, oneFullAssignmentRDDPar));
  }

  @Test
  public void runGaussian() {
    final int n = 10;
    final int d = 6;
    final int bn = 2;
    final int bd = 3;

    final Tuple3<R1CSRelationRDD<Fp>, Assignment<Fp>, JavaPairRDD<Long, Fp>> meanCov =
        R1CSConstruction.gaussianFitApp(fieldFactory, config, n, d, bn, bd);
    R1CSRelationRDD<Fp> r1csRDDPar = meanCov._1();
    Assignment<Fp> primaryTwoPar = meanCov._2();
    JavaPairRDD<Long, Fp> oneFullAssignmentRDDPar = meanCov._3();

    // /* Verify r1cs is valid */
    assertTrue(r1csRDDPar.isValid());
    /* Verify r1cs is satisfied with full assignment */
    assertTrue(r1csRDDPar.isSatisfied(primaryTwoPar, oneFullAssignmentRDDPar));
  }
}
