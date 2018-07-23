/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations.qap;

import algebra.fields.AbstractFieldElementExpanded;
import org.apache.spark.api.java.JavaPairRDD;

import java.io.Serializable;

public class QAPWitnessRDD<FieldT extends AbstractFieldElementExpanded<FieldT>> implements
        Serializable {

    private final JavaPairRDD<Long, FieldT> coefficientsABC;
    private final JavaPairRDD<Long, FieldT> coefficientsH;

    private final int numInputs;
    private final long numVariables;
    private final long degree;

    public QAPWitnessRDD(
            final JavaPairRDD<Long, FieldT> _coefficientsABC,
            final JavaPairRDD<Long, FieldT> _coefficientsH,
            final int _numInputs,
            final long _numVariables,
            final long _degree) {

        coefficientsABC = _coefficientsABC;
        coefficientsH = _coefficientsH;

        numInputs = _numInputs;
        numVariables = _numVariables;
        degree = _degree;
    }

    public JavaPairRDD<Long, FieldT> coefficientsABC() {
        return coefficientsABC;
    }

    public JavaPairRDD<Long, FieldT> coefficientsH() {
        return coefficientsH;
    }

    public int numInputs() {
        return numInputs;
    }

    public long numVariables() {
        return numVariables;
    }

    public long degree() {
        return degree;
    }

}
