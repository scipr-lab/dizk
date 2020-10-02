/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package common;

import java.util.ArrayList;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;

/**
 * Combiner implements a set of three functions used by JavaPairRDD.combineByKey() to partition
 * elements into the specified organization.
 */
public class Combiner<T> {

  // The following functions are used for combineByKey() in FFTAuxiliary, NaiveEvaluation, and BACE.
  public final Function<Tuple2<Long, T>, ArrayList<Tuple2<Long, T>>> createGroup =
      element -> {
        final ArrayList<Tuple2<Long, T>> a = new ArrayList<>();
        a.add(element);
        return a;
      };
  public final Function2<ArrayList<Tuple2<Long, T>>, Tuple2<Long, T>, ArrayList<Tuple2<Long, T>>>
      mergeElement =
          (element1, element2) -> {
            element1.add(element2);
            return element1;
          };
  public final Function2<
          ArrayList<Tuple2<Long, T>>, ArrayList<Tuple2<Long, T>>, ArrayList<Tuple2<Long, T>>>
      mergeCombiner =
          (element1, element2) -> {
            element1.addAll(element2);
            return element1;
          };
}
