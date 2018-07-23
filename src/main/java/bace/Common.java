/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package bace;

import algebra.fft.SerialFFT;
import algebra.fields.AbstractFieldElementExpanded;
import bace.circuit.Circuit;
import common.Combiner;
import common.Utils;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

import java.util.ArrayList;

public class Common {

    /* Returns the column LDEs. */
    protected static <FieldT extends AbstractFieldElementExpanded<FieldT>> JavaPairRDD<Long, ArrayList<FieldT>> getInputPolynomials(final Circuit<FieldT> circuit,
                                                                                                                                    final JavaPairRDD<Long, FieldT> input,
                                                                                                                                    final int numInputs) {
        final SerialFFT<FieldT> domain = new SerialFFT<>(numInputs, input.first()._2);

        return getInputColumns(circuit, input, false).mapValues(column -> {
            ArrayList<FieldT> columnArray = Utils.convertFromPairs(column, numInputs);
            domain.radix2InverseFFT(columnArray);
            return columnArray;
        });
    }

    protected static <FieldT extends AbstractFieldElementExpanded<FieldT>> JavaPairRDD<Long, ArrayList<Tuple2<Long, FieldT>>> getInputColumns(final Circuit<FieldT> circuit,
                                                                                                                                              final JavaPairRDD<Long, FieldT> input,
                                                                                                                                              final boolean transpose) {
        final long inputSize = circuit.inputSize;
        final Combiner<FieldT> combine = new Combiner<>();

        return input.mapToPair(element -> {
            final long group = transpose ? element._1 / inputSize : element._1 % inputSize;
            final long index = transpose ? element._1 % inputSize : element._1 / inputSize;

            return new Tuple2<>(group, new Tuple2<>(index, element._2));
        }).combineByKey(combine.createGroup, combine.mergeElement, combine.mergeCombiner);
    }
}
