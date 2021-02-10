/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations;

import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.fields.Fp;
import algebra.fields.mock.fieldparameters.LargeFpParameters;
import configuration.Configuration;
import java.io.Serializable;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import profiler.generation.R1CSConstructor;
import relations.objects.Assignment;
import relations.r1cs.R1CSRelation;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple3;

public class R1CSConstructorTest implements Serializable {
  private transient JavaSparkContext sc;
  private Configuration config;
  private Fp fieldFactory;

  @BeforeEach
  public void setUp() {
    sc = new JavaSparkContext("local", "ZKSparkTestSuite");
    config = new Configuration(1, 1, 1, 2, sc, StorageLevel.MEMORY_ONLY());
    fieldFactory = new LargeFpParameters().ONE();
  }

  @AfterEach
  public void tearDown() {
    sc.stop();
    sc = null;
  }

  @Test
  public void ConstructionTest() {
    final int numInputs = 1023;
    final int numConstraints = 1024;

    final Tuple3<R1CSRelation<Fp>, Assignment<Fp>, Assignment<Fp>> construction =
        R1CSConstructor.serialConstruct(numConstraints, numInputs, fieldFactory, config);
    final R1CSRelation<Fp> r1cs = construction._1();
    final Assignment<Fp> primary = construction._2();
    final Assignment<Fp> auxiliary = construction._3();
    final Assignment<Fp> oneFullAssignment = new Assignment<>(primary, auxiliary);

    final Tuple3<R1CSRelationRDD<Fp>, Assignment<Fp>, JavaPairRDD<Long, Fp>> constructionRDD =
        R1CSConstructor.parallelConstruct(numConstraints, numInputs, fieldFactory, config);
    final R1CSRelationRDD<Fp> r1csRDD = constructionRDD._1();
    final Assignment<Fp> primaryTwo = constructionRDD._2();
    final JavaPairRDD<Long, Fp> oneFullAssignmentRDD = constructionRDD._3();

    // Verify primary input match.
    assertTrue(primary.size() == primaryTwo.size());
    for (int i = 0; i < primary.size(); i++) {
      System.out.println(primary.get(i) + " == " + primaryTwo.get(i));
      assertTrue(primary.get(i).equals(primaryTwo.get(i)));
    }

    // Verify full assignment match.
    assertTrue(oneFullAssignment.size() == oneFullAssignmentRDD.count());
    oneFullAssignmentRDD.foreach(
        term -> {
          System.out.println(oneFullAssignment.get(term._1.intValue()) + " == " + term._2);
          assertTrue(oneFullAssignment.get(term._1.intValue()).equals(term._2));
        });

    // Verify constraints A match.
    r1csRDD
        .constraints()
        .A()
        .foreach(
            term -> {
              System.out.println(
                  r1cs.constraints(term._1.intValue()).A().getValue((int) term._2.index())
                      + " == "
                      + term._2.value());
              assertTrue(
                  r1cs.constraints(term._1.intValue())
                      .A()
                      .getValue((int) term._2.index())
                      .equals(term._2.value()));
            });

    // Verify constraints B match.
    r1csRDD
        .constraints()
        .B()
        .foreach(
            term -> {
              System.out.println(
                  r1cs.constraints(term._1.intValue()).B().getValue((int) term._2.index())
                      + " == "
                      + term._2.value());
              assertTrue(
                  r1cs.constraints(term._1.intValue())
                      .B()
                      .getValue((int) term._2.index())
                      .equals(term._2.value()));
            });

    // Verify constraints C match.
    r1csRDD
        .constraints()
        .C()
        .foreach(
            term -> {
              System.out.println(
                  r1cs.constraints(term._1.intValue()).C().getValue((int) term._2.index())
                      + " == "
                      + term._2.value());
              assertTrue(
                  r1cs.constraints(term._1.intValue())
                      .C()
                      .getValue((int) term._2.index())
                      .equals(term._2.value()));
            });

    // Verify r1cs is valid.
    assertTrue(r1cs.isValid());
    assertTrue(r1csRDD.isValid());

    // Verify r1cs is satisfied with full assignment.
    assertTrue(r1cs.isSatisfied(primary, auxiliary));
    assertTrue(r1csRDD.isSatisfied(primary, oneFullAssignmentRDD));
  }
}
