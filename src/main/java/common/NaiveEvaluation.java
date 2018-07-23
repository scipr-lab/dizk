/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package common;

import algebra.fields.AbstractFieldElementExpanded;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

import java.util.List;

public class NaiveEvaluation {

    public static <FieldT extends AbstractFieldElementExpanded<FieldT>> FieldT evaluatePolynomial(
            final List<FieldT> input,
            final FieldT t) {
        final int m = input.size();

        FieldT result = t.zero();
        for (int i = m - 1; i >= 0; i--) {
            result = result.mul(t).add(input.get(i));
        }

        return result;
    }

    public static <FieldT extends AbstractFieldElementExpanded<FieldT>> FieldT
    parallelEvaluatePolynomial(
            final JavaPairRDD<Long, FieldT> input,
            final FieldT element,
            final int partitionSize) {
        final FieldT groupPartitionSize = element.pow(partitionSize);
        final Combiner<FieldT> combine = new Combiner<>();

        return input.mapToPair(item -> {
            final long group = item._1 / partitionSize;
            final long index = item._1 % partitionSize;
            return new Tuple2<>(group, new Tuple2<>(index, item._2));
        }).combineByKey(combine.createGroup, combine.mergeElement, combine.mergeCombiner).map(group -> {
            final List<FieldT> subPolynomial = Utils.convertFromPairs(group._2, group._2.size());
            return evaluatePolynomial(subPolynomial, element).mul(groupPartitionSize.pow(group._1));
        }).reduce(FieldT::add);
    }

    public static <FieldT extends AbstractFieldElementExpanded<FieldT>> FieldT
    evaluateLagrangePolynomial(
            final int m,
            final List<FieldT> domain,
            final FieldT t,
            final int idx) {
        assert (m == domain.size());
        assert (idx < m);

        FieldT num = t.one();
        FieldT denom = t.one();

        for (int k = 0; k < m; ++k) {
            if (k == idx) {
                continue;
            }

            num = num.mul(t.sub(domain.get(k)));
            denom = denom.mul(domain.get(idx).sub(domain.get(k)));
        }

        return num.mul(denom.inverse());
    }

}
