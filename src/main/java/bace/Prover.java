/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package bace;

import algebra.fft.DistributedFFT;
import algebra.fft.SerialFFT;
import algebra.fields.AbstractFieldElementExpanded;
import bace.circuit.Circuit;
import common.Combiner;
import common.MathUtils;
import common.Utils;
import java.util.ArrayList;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

/* Compute a proof given a K groups of inputs, each of size N. */
public class Prover<FieldT extends AbstractFieldElementExpanded<FieldT>> {

  private final Circuit<FieldT> circuit;
  private final JavaPairRDD<Long, FieldT> input;
  private final int numInputs;

  public Prover(
      final Circuit<FieldT> _circuit,
      final JavaPairRDD<Long, FieldT> _input,
      final int _numInputs) {
    circuit = _circuit;
    input = _input;
    numInputs = _numInputs;
  }

  /* Returns a proof given numInputs groups of inputs, each of size inputSize. */
  public Tuple2<Long, JavaPairRDD<Long, FieldT>> computeProof(
      final FieldT fieldFactory, final int numPartitions) throws Exception {
    final int proofSize = MathUtils.lowestPowerOfTwo((int) circuit.totalDegree() * numInputs);
    final Combiner<FieldT> combine = new Combiner<>();

    /* Evaluate inputSize inputPolynomials(i) in a domain of size D. */
    final JavaPairRDD<Long, ArrayList<FieldT>> evaluatedInputPolynomials =
        Common.getInputPolynomials(circuit, input, numInputs)
            .mapValues(
                element -> {
                  final SerialFFT<FieldT> domain = new SerialFFT<>(proofSize, element.get(0));

                  ArrayList<FieldT> paddedElement = Utils.padArray(element, proofSize);
                  domain.radix2FFT(paddedElement);
                  return paddedElement;
                });

    /* Transpose evaluated-inputPolynomials into D groups, each of size inputSize. */
    final JavaPairRDD<Long, ArrayList<Tuple2<Long, FieldT>>> transposedPolynomials =
        evaluatedInputPolynomials
            .flatMapToPair(
                element -> {
                  /* Bitshift and map to key j. */
                  ArrayList<Tuple2<Long, Tuple2<Long, FieldT>>> combinedNumbers = new ArrayList<>();
                  for (int i = 0; i < proofSize; i++) {
                    Tuple2<Long, FieldT> indexedElement = new Tuple2<>((long) i, element._2.get(i));
                    combinedNumbers.add(new Tuple2<>(element._1, indexedElement));
                  }
                  return combinedNumbers.iterator();
                })
            .combineByKey(combine.createGroup, combine.mergeElement, combine.mergeCombiner);

    /* Pass the transposed column LDEs to the circuit. */
    final JavaPairRDD<Long, FieldT> evaluatedPolynomials =
        transposedPolynomials.mapValues(
            column -> {
              ArrayList<FieldT> columnArray = Utils.convertFromPairs(column, circuit.inputSize);
              return circuit.compute(columnArray);
            });

    /* Compute R(z) of size D. */
    final int dimension1 = MathUtils.lowestPowerOfTwo((int) Math.sqrt(proofSize));
    final int dimension2 = proofSize / dimension1;
    try {
      return new Tuple2<>(
          (long) proofSize,
          DistributedFFT.radix2InverseFFT(
              evaluatedPolynomials, dimension1, dimension2, fieldFactory));
    } catch (Exception e) {
      System.out.println("Prover.computeProof() error: " + e.toString());
      throw e;
    }
  }
}
