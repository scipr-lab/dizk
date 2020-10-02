/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.msm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.fields.Fp;
import algebra.fields.fieldparameters.LargeFpParameters;
import algebra.groups.AdditiveIntegerGroup;
import algebra.groups.integergroupparameters.LargeAdditiveIntegerGroupParameters;
import java.io.Serializable;
import java.util.ArrayList;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scala.Tuple2;

public class DistributedVariableBaseMSMTest implements Serializable {
  private transient JavaSparkContext sc;
  private LargeAdditiveIntegerGroupParameters GroupParameters;

  @BeforeEach
  public void setUp() {
    sc = new JavaSparkContext("local", "ZKSparkTestSuite");
    GroupParameters = new LargeAdditiveIntegerGroupParameters();
  }

  @AfterEach
  public void tearDown() {
    sc.stop();
    sc = null;
  }

  @Test
  public void DistributedSortedMSMTest() {
    final LargeFpParameters FpParameters = new LargeFpParameters();
    ArrayList<Tuple2<Fp, AdditiveIntegerGroup>> input = new ArrayList<>(4);
    input.add(
        new Tuple2<>(new Fp("3", FpParameters), new AdditiveIntegerGroup(5, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("11", FpParameters), new AdditiveIntegerGroup(2, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("2", FpParameters), new AdditiveIntegerGroup(7, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("8", FpParameters), new AdditiveIntegerGroup(3, GroupParameters)));

    JavaRDD<Tuple2<Fp, AdditiveIntegerGroup>> sparkInput = sc.parallelize(input);

    AdditiveIntegerGroup result = VariableBaseMSM.distributedSortedMSM(sparkInput);
    AdditiveIntegerGroup answer = new AdditiveIntegerGroup(75, GroupParameters);
    System.out.println(result.toString() + " == " + answer.toString());
    assertTrue(result.equals(answer));
  }

  @Test
  public void DistributedBosCosterMSMTest() {
    final LargeFpParameters FpParameters = new LargeFpParameters();
    ArrayList<Tuple2<Fp, AdditiveIntegerGroup>> input = new ArrayList<>(4);
    input.add(
        new Tuple2<>(new Fp("3", FpParameters), new AdditiveIntegerGroup(5, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("11", FpParameters), new AdditiveIntegerGroup(2, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("2", FpParameters), new AdditiveIntegerGroup(7, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("8", FpParameters), new AdditiveIntegerGroup(3, GroupParameters)));

    JavaRDD<Tuple2<Fp, AdditiveIntegerGroup>> sparkInput = sc.parallelize(input);

    AdditiveIntegerGroup result = VariableBaseMSM.distributedBosCosterMSM(sparkInput);
    AdditiveIntegerGroup answer = new AdditiveIntegerGroup(75, GroupParameters);
    System.out.println(result.toString() + " == " + answer.toString());
    assertTrue(result.equals(answer));
  }

  @Test
  public void DistributedPippengerMSMTest() {
    final LargeFpParameters FpParameters = new LargeFpParameters();
    ArrayList<Tuple2<Fp, AdditiveIntegerGroup>> input = new ArrayList<>(4);
    input.add(
        new Tuple2<>(new Fp("3", FpParameters), new AdditiveIntegerGroup(5, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("11", FpParameters), new AdditiveIntegerGroup(2, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("2", FpParameters), new AdditiveIntegerGroup(7, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("8", FpParameters), new AdditiveIntegerGroup(3, GroupParameters)));

    JavaRDD<Tuple2<Fp, AdditiveIntegerGroup>> sparkInput = sc.parallelize(input, 10);

    AdditiveIntegerGroup result = VariableBaseMSM.distributedPippengerMSM(sparkInput);
    AdditiveIntegerGroup answer = new AdditiveIntegerGroup(75, GroupParameters);
    System.out.println(result.toString() + " == " + answer.toString());
    assertTrue(result.equals(answer));
  }

  @Test
  public void DistributedMSMTest() {
    final LargeFpParameters FpParameters = new LargeFpParameters();
    ArrayList<Tuple2<Fp, AdditiveIntegerGroup>> input = new ArrayList<>(4);
    input.add(
        new Tuple2<>(new Fp("3", FpParameters), new AdditiveIntegerGroup(5, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("11", FpParameters), new AdditiveIntegerGroup(2, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("2", FpParameters), new AdditiveIntegerGroup(7, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("8", FpParameters), new AdditiveIntegerGroup(3, GroupParameters)));

    JavaRDD<Tuple2<Fp, AdditiveIntegerGroup>> sparkInput = sc.parallelize(input);

    AdditiveIntegerGroup result = VariableBaseMSM.distributedMSM(sparkInput);
    AdditiveIntegerGroup answer = new AdditiveIntegerGroup(75, GroupParameters);
    System.out.println(result.toString() + " == " + answer.toString());
    assertTrue(result.equals(answer));
  }

  @Test
  public void DistributedMSMDuplicatesTest() {
    final LargeFpParameters FpParameters = new LargeFpParameters();
    ArrayList<Tuple2<Fp, AdditiveIntegerGroup>> input = new ArrayList<>(4);
    input.add(
        new Tuple2<>(new Fp("3", FpParameters), new AdditiveIntegerGroup(5, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("3", FpParameters), new AdditiveIntegerGroup(5, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("3", FpParameters), new AdditiveIntegerGroup(5, GroupParameters)));
    input.add(
        new Tuple2<>(new Fp("3", FpParameters), new AdditiveIntegerGroup(5, GroupParameters)));

    JavaRDD<Tuple2<Fp, AdditiveIntegerGroup>> sparkInput = sc.parallelize(input);

    AdditiveIntegerGroup result = VariableBaseMSM.distributedMSM(sparkInput);
    AdditiveIntegerGroup answer = new AdditiveIntegerGroup(60, GroupParameters);
    System.out.println(result.toString() + " == " + answer.toString());
    assertTrue(result.equals(answer));
  }
}
