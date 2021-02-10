/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package bace;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.fields.Fp;
import algebra.fields.mock.fieldparameters.LargeFpParameters;
import bace.circuit.Circuit;
import bace.circuit.InputGate;
import common.Utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scala.Tuple2;

public class BaceTest implements Serializable {
  private transient JavaSparkContext sc;
  private LargeFpParameters FpParameters;
  private Circuit<Fp> circuit;
  private JavaPairRDD<Long, Fp> input;
  private int numInputs;

  private final long seed = 57;
  private final byte[] secureSeed = "".getBytes();

  @BeforeEach
  public void setUp() {
    sc = new JavaSparkContext("local", "ZKSparkTestSuite");
    FpParameters = new LargeFpParameters();

    final InputGate<Fp> x1 = new InputGate<>(new Fp(1, FpParameters), 0);
    final InputGate<Fp> x2 = new InputGate<>(new Fp(2, FpParameters), 1);
    final InputGate<Fp> x3 = new InputGate<>(new Fp(3, FpParameters), 2);
    final InputGate<Fp> x4 = new InputGate<>(new Fp(4, FpParameters), 3);

    ArrayList<InputGate<Fp>> inputGates = new ArrayList<>();
    inputGates.add(x1);
    inputGates.add(x2);
    inputGates.add(x3);
    inputGates.add(x4);

    circuit = new Circuit<>(inputGates, (x1.mul(x2)).add(x3.mul(x4)));
    numInputs = 4;

    ArrayList<Tuple2<Long, Fp>> preInput = new ArrayList<>();
    for (int i = 0; i < circuit.inputSize * numInputs; i++) {
      preInput.add(new Tuple2<>((long) i, new Fp(i + 1, FpParameters)));
    }
    input = sc.parallelizePairs(preInput);
  }

  @AfterEach
  public void tearDown() {
    sc.stop();
    sc = null;
  }

  @Test
  public void ProverVerifierTest() {
    try {
      Prover<Fp> prover = new Prover<>(circuit, input, numInputs);
      Tuple2<Long, JavaPairRDD<Long, Fp>> proof = prover.computeProof(new Fp(1, FpParameters), 1);

      Verifier<Fp> verifier = new Verifier<>(circuit, proof, input, numInputs);
      Tuple2<Map<String, Long>, Boolean> verification = verifier.verifyProof(seed, secureSeed);

      assertTrue(verification._2);

      ArrayList<Tuple2<Long, Fp>> result =
          new ArrayList<>(
              verifier
                  .getResult(new Fp(1, FpParameters), 1)
                  .mapToPair(x -> new Tuple2<>(x._1, x._2))
                  .collect());
      ArrayList<Fp> inputArray = Utils.convertFromPairs(result, numInputs);
      Fp resultZero = circuit.compute(new ArrayList<>(inputArray.subList(0, circuit.inputSize)));
      Fp resultOne =
          circuit.compute(
              new ArrayList<>(inputArray.subList(circuit.inputSize, 2 * circuit.inputSize)));

      assertTrue(result.size() == numInputs);
      assertTrue(resultZero.equals(result.get(0)._2));
      assertTrue(resultOne.equals(result.get(1)._2));
    } catch (Exception e) {
      System.out.println(e.toString());
    }
  }

  @Test
  public void MaliciousProverVerifierTest() {
    try {
      Prover<Fp> prover = new Prover<>(circuit, input, numInputs);
      Tuple2<Long, JavaPairRDD<Long, Fp>> proof = prover.computeProof(new Fp(1, FpParameters), 1);

      JavaPairRDD<Long, Fp> maliciousOutput =
          proof._2.mapToPair(
              element -> {
                return (element._1 == 0) ? new Tuple2<>(0l, new Fp(100, FpParameters)) : element;
              });
      Tuple2<Long, JavaPairRDD<Long, Fp>> maliciousProof = new Tuple2<>(proof._1, maliciousOutput);

      Verifier<Fp> verifier = new Verifier<>(circuit, maliciousProof, input, numInputs);
      Tuple2<Map<String, Long>, Boolean> verification = verifier.verifyProof(seed, secureSeed);

      assertFalse(verification._2);
    } catch (Exception e) {
      System.out.println(e.toString());
    }
  }
}
