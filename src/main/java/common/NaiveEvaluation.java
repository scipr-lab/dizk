/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package common;

import algebra.fields.AbstractFieldElementExpanded;
import java.util.List;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

public class NaiveEvaluation {

  /**
   * Function evaluating polynomials using Horner's rule. i.e. a polynomial of the form: p(x) = c_m
   * * x^m + ... + c_1 * x + c_0 is decomposed as: p(x) = (c_m * x^{m-1} + ... + c_1) * x + c_0,
   * where q(x) = c_m * x^{m-1} + ... c_2 * x + c_1 is also defined as the product of a polynomial
   * r(x) times x, deg(r) = deg(q) - 1. Example: p(x) = 3x^3 + 7x^2 + x + 10 = (3x^2 + 7x + 1)*x +
   * 10 = ((3x + 7)*x + 1)*x + 10 = (((3)*x + 7)*x + 1)*x + 10 which is equal to: (((0*x + 3)*x +
   * 7)*x + 1)*x + 10
   *
   * <p>Hence a polynomial of degree m will be evaluated via m iterations of a loop in which a
   * single muliplication by the evaluation point is done + a coefficient addition.
   */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>> FieldT evaluatePolynomial(
      final List<FieldT> input, final FieldT t) {
    final int m = input.size();

    FieldT result = t.zero();
    for (int i = m - 1; i >= 0; i--) {
      result = result.mul(t).add(input.get(i));
    }

    return result;
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      FieldT parallelEvaluatePolynomial(
          final JavaPairRDD<Long, FieldT> input, final FieldT element, final int partitionSize) {
    final FieldT groupPartitionSize = element.pow(partitionSize);
    final Combiner<FieldT> combine = new Combiner<>();

    return input
        .mapToPair(
            item -> {
              final long group = item._1 / partitionSize;
              final long index = item._1 % partitionSize;
              return new Tuple2<>(group, new Tuple2<>(index, item._2));
            })
        .combineByKey(combine.createGroup, combine.mergeElement, combine.mergeCombiner)
        .map(
            group -> {
              final List<FieldT> subPolynomial = Utils.convertFromPairs(group._2, group._2.size());
              return evaluatePolynomial(subPolynomial, element)
                  .mul(groupPartitionSize.pow(group._1));
            })
        .reduce(FieldT::add);
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      FieldT evaluateLagrangePolynomial(
          final int m, final List<FieldT> domain, final FieldT t, final int idx) {
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
