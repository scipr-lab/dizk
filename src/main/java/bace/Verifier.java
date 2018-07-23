/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package bace;

import algebra.fft.DistributedFFT;
import algebra.fields.AbstractFieldElementExpanded;
import bace.circuit.Circuit;
import common.MathUtils;
import common.NaiveEvaluation;
import common.Utils;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* Verify the proof computed by Prover. */
public class Verifier<FieldT extends AbstractFieldElementExpanded<FieldT>> {
    private final Circuit<FieldT> circuit;
    private final Tuple2<Long, JavaPairRDD<Long, FieldT>> proof;
    private final JavaPairRDD<Long, FieldT> input;
    private final int numInputs;


    public Verifier(final Circuit<FieldT> _circuit, final Tuple2<Long, JavaPairRDD<Long, FieldT>> _proof, final JavaPairRDD<Long, FieldT> _input, final int _numInputs) {
        circuit = _circuit;
        proof = _proof;
        input = _input;
        numInputs = _numInputs;
    }

    /* Returns the proof result if valid, else throws an Exception. Verify whether R is valid or not. */
    public Tuple2<Map<String, Long>, Boolean> verifyProof(final Long seed, final byte[] secureSeed) throws Exception {
        final FieldT randField = proof._2.first()._2.random(seed, secureSeed);
        final int partitionSize = MathUtils.lowestPowerOfTwo((int) Math.sqrt(proof._1));

        /* Evaluate proof(randField). */
        long start = System.currentTimeMillis();
        FieldT claimedResult = NaiveEvaluation.parallelEvaluatePolynomial(proof._2, randField, partitionSize);
        final long claimEvaluationTime = System.currentTimeMillis() - start;

        /* Column LDEs. */
        start = System.currentTimeMillis();
        final ArrayList<Tuple2<Long, FieldT>> evaluatedPolynomials = new ArrayList<>(Common.getInputPolynomials(circuit, input, numInputs).mapValues(column -> {
            return NaiveEvaluation.evaluatePolynomial(column, randField);
        }).collect());
        final long polynomialEvaluationTime = System.currentTimeMillis() - start;

        /* Evaluate circuit(..., beta(j), ...). */
        start = System.currentTimeMillis();
        boolean isValid = false;
        try {
            isValid = claimedResult == circuit.compute(Utils.convertFromPairs(evaluatedPolynomials, circuit.inputSize));
        } catch (Exception e) {
            System.out.println("Verifier.verifyProof(): error " + e.toString());
            throw e;
        }
        final long circuitEvaluationTime = System.currentTimeMillis() - start;

        HashMap<String, Long> profileResults = new HashMap<>();
        profileResults.put("claimEvaluationTime", claimEvaluationTime);
        profileResults.put("polynomialEvaluationTime", polynomialEvaluationTime);
        profileResults.put("circuitEvaluationTime", circuitEvaluationTime);

        return new Tuple2<>(profileResults, isValid);
    }

    public JavaPairRDD<Long, FieldT> getResult(final FieldT fieldFactory, final int numPartitions) throws Exception {
        final int partitionSize = MathUtils.lowestPowerOfTwo((int) Math.sqrt(proof._1));
        JavaPairRDD<Long, FieldT> resultRDD;
        try {
            resultRDD = DistributedFFT.radix2FFT(proof._2, partitionSize, (int) (proof._1 / partitionSize), fieldFactory);
        } catch (Exception e) {
            System.out.println("Verifier.getResult(): error " + e.toString());
            throw e;
        }

        final long stepSize = proof._1 / numInputs;

        // Pick the result every stepSize of items.
        return resultRDD.filter(item -> (item._1 % stepSize == 0)).mapToPair(item -> new Tuple2<>(item._1 / stepSize, item._2));
    }

}
