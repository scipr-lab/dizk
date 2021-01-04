/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package common;

import algebra.fields.AbstractFieldElementExpanded;
import algebra.groups.AbstractGroup;
import configuration.Configuration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

public class Utils {

  /**
   * Pads the given array input to length size. Note, if the input is already of length size or
   * greater, then nothing is done.
   */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>> ArrayList<FieldT> padArray(
      final ArrayList<FieldT> input, final int size) {
    if (input.size() >= size) {
      return input;
    } else {
      ArrayList<FieldT> paddedArray = new ArrayList<>(size);
      paddedArray.addAll(input);

      final FieldT zero = input.get(0).zero();
      int i = input.size();
      while (i < size) {
        paddedArray.add(zero);
        i++;
      }
      return paddedArray;
    }
  }

  /** Indexes the given list of inputs and returns an ArrayList of (Long, T) pairs. */
  public static <T> ArrayList<Tuple2<Long, T>> convertToPairs(final List<T> input) {
    ArrayList<Tuple2<Long, T>> result = new ArrayList<>(input.size());

    for (int i = 0; i < input.size(); i++) {
      result.add(new Tuple2<>((long) i, input.get(i)));
    }

    return result;
  }

  /**
   * Given a list of (Long, FieldT) pairs, convertFromPairs() proceeds to set the first size
   * elements of input indexed by the Long value storing the FieldT element.
   */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      ArrayList<FieldT> convertFromPairs(final List<Tuple2<Long, FieldT>> input, final int size) {
    // assert (input.size() == size);
    final FieldT zero = input.get(0)._2.zero();
    ArrayList<FieldT> result = new ArrayList<>(Collections.nCopies(size, zero));

    for (final Tuple2<Long, FieldT> element : input) {
      result.set(element._1.intValue(), element._2);
    }

    return result;
  }

  public static <GroupT extends AbstractGroup<GroupT>> ArrayList<GroupT> convertFromPair(
      final List<Tuple2<Long, GroupT>> input, final int size) {
    final GroupT zero = input.get(0)._2.zero();
    ArrayList<GroupT> result = new ArrayList<>(Collections.nCopies(size, zero));

    for (int i = 0; i < size; i++) {
      final Tuple2<Long, GroupT> element = input.get(i);
      result.set(element._1.intValue(), element._2);
    }

    return result;
  }

  /** Initializes a new JavaPairRDD of length size, indexed and filled with the given element. */
  public static <T> JavaPairRDD<Long, T> fillRDD(
      final long size, final T element, final Configuration config) {
    final int numPartitions = size >= config.numPartitions() ? config.numPartitions() : (int) size;

    final ArrayList<Integer> partitions = new ArrayList<>(numPartitions);
    for (int i = 0; i < numPartitions; i++) {
      partitions.add(i);
    }
    if (size % 2 != 0) {
      partitions.add(numPartitions);
    }

    return config
        .sparkContext()
        .parallelize(partitions, numPartitions)
        .flatMapToPair(
            part -> {
              if (part == numPartitions) {
                final long standardPartSize = size / numPartitions;
                final long partSize = size - (standardPartSize * numPartitions);

                final ArrayList<Tuple2<Long, T>> data = new ArrayList<>();
                for (long i = 0; i < partSize; i++) {
                  data.add(new Tuple2<>(part * standardPartSize + i, element));
                }

                return data.iterator();
              } else {
                final long partSize = size / numPartitions;

                final ArrayList<Tuple2<Long, T>> data = new ArrayList<>();
                for (long i = 0; i < partSize; i++) {
                  data.add(new Tuple2<>(part * partSize + i, element));
                }

                return data.iterator();
              }
            });
  }
}
