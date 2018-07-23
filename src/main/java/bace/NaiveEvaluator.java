/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package bace;

import algebra.fields.AbstractFieldElementExpanded;
import bace.circuit.Circuit;
import common.Utils;
import org.apache.spark.api.java.JavaPairRDD;

import java.util.ArrayList;

/* Naive evaluation of the given input on the arithmetic circuit. */
public class NaiveEvaluator<FieldT extends AbstractFieldElementExpanded<FieldT>> {
    private final Circuit<FieldT> circuit;
    private final JavaPairRDD<Long, FieldT> input;
    private final int numInputs;

    public NaiveEvaluator(final Circuit<FieldT> _circuit, final JavaPairRDD<Long, FieldT> _input, final int _numInputs) {
        circuit = _circuit;
        input = _input;
        numInputs = _numInputs;
    }

    public JavaPairRDD<Long, FieldT> getResult() {
        return Common.getInputColumns(circuit, input, true).mapValues(input -> {
            final ArrayList<FieldT> sortedInput = Utils.convertFromPairs(input, circuit.inputSize);
            return circuit.compute(sortedInput);
        });
    }
}