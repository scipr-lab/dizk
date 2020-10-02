/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.msm;

import algebra.fields.AbstractFieldElementExpanded;
import algebra.groups.AbstractGroup;
import common.MathUtils;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import javax.annotation.Nonnull;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

public class VariableBaseMSM {

  public static final BigInteger BOS_COSTER_MSM_THRESHOLD = new BigInteger("1048576");

  /**
   * The algorithm starts by sorting the input in order of scalar size. For each iteration, it
   * computes the difference of the two largest scalars, and multiplies the accrued base by this
   * difference of exponents. This result is then summed and lastly returned.
   */
  public static <GroupT extends AbstractGroup<GroupT>> GroupT sortedMSM(
      @Nonnull final List<Tuple2<BigInteger, GroupT>> input) {

    GroupT result = input.get(0)._2.zero();
    GroupT base = input.get(0)._2.zero();
    BigInteger scalar;

    input.sort((v1, v2) -> v1._1.compareTo(v2._1));

    for (int i = input.size() - 1; i >= 0; i--) {
      scalar = i != 0 ? input.get(i)._1.subtract(input.get(i - 1)._1) : input.get(i)._1;
      base = base.add(input.get(i)._2);
      result = result.add(base.mul(scalar));
    }
    return result;
  }

  /**
   * The Bos-Coster algorithm works by repeatedly recursively computing (e1 - e2) * b1 + e2 * (b1 +
   * b2) where the scalars are ordered such that e1 >= e2 >= ... >= en in a priority queue. The
   * result is summed and returned after all scalar-base pairs have been computed.
   *
   * <p>BigInteger values must be positive.
   */
  public static <GroupT extends AbstractGroup<GroupT>> GroupT bosCosterMSM(
      @Nonnull final List<Tuple2<BigInteger, GroupT>> input) {

    final PriorityQueue<Tuple2<BigInteger, GroupT>> sortedScalarPairs =
        new PriorityQueue<>(input.size(), (v1, v2) -> v2._1.compareTo(v1._1));
    sortedScalarPairs.addAll(input);

    GroupT result = input.get(0)._2.zero();
    Tuple2<BigInteger, GroupT> e1;
    Tuple2<BigInteger, GroupT> e2;

    while ((e1 = sortedScalarPairs.poll()) != null && (e2 = sortedScalarPairs.poll()) != null) {

      if (e1._1.divide(e2._1).compareTo(BOS_COSTER_MSM_THRESHOLD) >= 0) {
        result = result.add(e1._2.mul(e1._1));
        sortedScalarPairs.add(e2);
      } else {
        final BigInteger value = e1._1.subtract(e2._1);

        if (!value.equals(BigInteger.ZERO)) {
          sortedScalarPairs.add(new Tuple2<>(value, e1._2));
        }

        sortedScalarPairs.add(new Tuple2<>(e2._1, e1._2.add(e2._2)));
      }
    }

    while (e1 != null) {
      result = result.add(e1._2.mul(e1._1));
      e1 = sortedScalarPairs.poll();
    }

    return result;
  }

  public static <GroupT extends AbstractGroup<GroupT>> GroupT pippengerMSM(
      @Nonnull final List<Tuple2<BigInteger, GroupT>> input, final int numBits) {

    final int length = input.size();
    final int log2Length = Math.max(1, MathUtils.log2(length));
    final int c = log2Length - (log2Length / 3);

    final int numBuckets = 1 << c;
    final int numGroups = (numBits + c - 1) / c;

    final GroupT zero = input.get(0)._2.zero();
    final ArrayList<GroupT> bucketsModel = new ArrayList<>(Collections.nCopies(numBuckets, zero));

    GroupT result = zero;

    for (int k = numGroups - 1; k >= 0; k--) {
      if (k < numGroups - 1) {
        for (int i = 0; i < c; i++) {
          result = result.dbl();
        }
      }

      final ArrayList<GroupT> buckets = new ArrayList<>(bucketsModel);

      for (int i = 0; i < length; i++) {
        int id = 0;
        for (int j = 0; j < c; j++) {
          if (input.get(i)._1.testBit(k * c + j)) {
            id |= 1 << j;
          }
        }

        if (id == 0) {
          continue;
        }

        // Potentially use mixed addition here.
        buckets.set(id, buckets.get(id).add(input.get(i)._2));
      }

      GroupT runningSum = zero;

      for (int i = numBuckets - 1; i > 0; i--) {
        // Potentially use mixed addition here.
        runningSum = runningSum.add(buckets.get(i));
        result = result.add(runningSum);
      }
    }

    return result;
  }

  public static <
          GroupT extends AbstractGroup<GroupT>, FieldT extends AbstractFieldElementExpanded<FieldT>>
      GroupT serialMSM(final List<FieldT> scalars, final List<GroupT> bases) {

    assert (bases.size() == scalars.size());

    final List<Tuple2<BigInteger, GroupT>> filteredInput = new ArrayList<>();

    GroupT acc = bases.get(0).zero();

    int numBits = 0;
    for (int i = 0; i < bases.size(); i++) {
      final BigInteger scalar = scalars.get(i).toBigInteger();
      if (scalar.equals(BigInteger.ZERO)) {
        continue;
      }

      final GroupT base = bases.get(i);

      if (scalar.equals(BigInteger.ONE)) {
        acc = acc.add(base);
      } else {
        filteredInput.add(new Tuple2<>(scalar, base));
        numBits = Math.max(numBits, scalar.bitLength());
      }
    }

    if (!filteredInput.isEmpty()) {
      acc = acc.add(pippengerMSM(filteredInput, numBits));
    }

    return acc;
  }

  public static <
          T1 extends AbstractGroup<T1>,
          T2 extends AbstractGroup<T2>,
          FieldT extends AbstractFieldElementExpanded<FieldT>>
      Tuple2<T1, T2> doubleMSM(final List<FieldT> scalars, final List<Tuple2<T1, T2>> bases) {

    assert (bases.size() == scalars.size());

    final int size = bases.size();
    assert (size > 0);

    final ArrayList<Tuple2<BigInteger, T1>> converted1 = new ArrayList<>(size);
    final ArrayList<Tuple2<BigInteger, T2>> converted2 = new ArrayList<>(size);

    T1 acc1 = bases.get(0)._1.zero();
    T2 acc2 = bases.get(0)._2.zero();

    int numBits = 0;
    for (int i = 0; i < size; i++) {
      final Tuple2<T1, T2> value = bases.get(i);
      final BigInteger scalar = scalars.get(i).toBigInteger();
      if (scalar.equals(BigInteger.ZERO)) {
        continue;
      }

      // Mixed addition
      if (scalar.equals(BigInteger.ONE)) {
        acc1 = acc1.add(value._1);
        acc2 = acc2.add(value._2);
      } else {
        converted1.add(new Tuple2<>(scalar, value._1));
        converted2.add(new Tuple2<>(scalar, value._2));
        numBits = Math.max(numBits, scalar.bitLength());
      }
    }

    return new Tuple2<>(
        converted1.isEmpty() ? acc1 : acc1.add(VariableBaseMSM.pippengerMSM(converted1, numBits)),
        converted2.isEmpty() ? acc2 : acc2.add(VariableBaseMSM.pippengerMSM(converted2, numBits)));
  }

  public static <T1 extends AbstractGroup<T1>, T2 extends AbstractGroup<T2>>
      Tuple2<T1, T2> doubleMSM(final List<Tuple2<BigInteger, Tuple2<T1, T2>>> input) {

    final int size = input.size();
    assert (size > 0);

    ArrayList<Tuple2<BigInteger, T1>> converted1 = new ArrayList<>(size);
    ArrayList<Tuple2<BigInteger, T2>> converted2 = new ArrayList<>(size);

    T1 acc1 = input.get(0)._2._1.zero();
    T2 acc2 = input.get(0)._2._2.zero();

    int numBits = 0;
    for (int i = 0; i < size; i++) {
      final Tuple2<T1, T2> value = input.get(i)._2;
      final BigInteger scalar = input.get(i)._1;
      if (scalar.equals(BigInteger.ZERO)) {
        continue;
      }

      // Mixed addition
      if (scalar.equals(BigInteger.ONE)) {
        acc1 = acc1.add(value._1);
        acc2 = acc2.add(value._2);
      } else {
        converted1.add(new Tuple2<>(scalar, value._1));
        converted2.add(new Tuple2<>(scalar, value._2));
        numBits = Math.max(numBits, scalar.bitLength());
      }
    }

    return new Tuple2<>(
        converted1.isEmpty() ? acc1 : acc1.add(VariableBaseMSM.pippengerMSM(converted1, numBits)),
        converted2.isEmpty() ? acc2 : acc2.add(VariableBaseMSM.pippengerMSM(converted2, numBits)));
  }

  public static <
          GroupT extends AbstractGroup<GroupT>, FieldT extends AbstractFieldElementExpanded<FieldT>>
      GroupT distributedMSM(final JavaRDD<Tuple2<FieldT, GroupT>> input) {

    return input
        .mapPartitions(
            partition -> {
              final List<Tuple2<BigInteger, GroupT>> pairs = new ArrayList<>();

              int numBits = 0;
              while (partition.hasNext()) {
                final Tuple2<FieldT, GroupT> pair = partition.next();

                final BigInteger scalar = pair._1.toBigInteger();
                if (scalar.equals(BigInteger.ZERO)) {
                  continue;
                }

                pairs.add(new Tuple2<>(scalar, pair._2));
                numBits = Math.max(numBits, scalar.bitLength());
              }

              return pairs.isEmpty()
                  ? Collections.emptyListIterator()
                  : Collections.singletonList(pippengerMSM(pairs, numBits)).iterator();
            })
        .reduce(GroupT::add);
  }

  public static <
          G1T extends AbstractGroup<G1T>,
          G2T extends AbstractGroup<G2T>,
          FieldT extends AbstractFieldElementExpanded<FieldT>>
      Tuple2<G1T, G2T> distributedDoubleMSM(final JavaRDD<Tuple2<FieldT, Tuple2<G1T, G2T>>> input) {

    return input
        .mapPartitions(
            partition -> {
              final List<Tuple2<BigInteger, Tuple2<G1T, G2T>>> pairs = new ArrayList<>();
              while (partition.hasNext()) {
                Tuple2<FieldT, Tuple2<G1T, G2T>> pair = partition.next();
                final BigInteger scalar = pair._1.toBigInteger();
                if (scalar.equals(BigInteger.ZERO)) {
                  continue;
                }
                pairs.add(new Tuple2<>(scalar, pair._2));
              }
              return pairs.isEmpty()
                  ? Collections.emptyListIterator()
                  : Collections.singletonList(doubleMSM(pairs)).iterator();
            })
        .reduce((e1, e2) -> new Tuple2<>(e1._1.add(e2._1), e1._2.add(e2._2)));
  }

  /* Used for profiling only */
  public static <
          GroupT extends AbstractGroup<GroupT>, FieldT extends AbstractFieldElementExpanded<FieldT>>
      GroupT distributedSortedMSM(final JavaRDD<Tuple2<FieldT, GroupT>> input) {

    return input
        .mapPartitions(
            partition -> {
              final List<Tuple2<BigInteger, GroupT>> pairs = new ArrayList<>();

              while (partition.hasNext()) {
                final Tuple2<FieldT, GroupT> pair = partition.next();
                final BigInteger scalar = pair._1.toBigInteger();
                if (scalar.equals(BigInteger.ZERO)) {
                  continue;
                }
                pairs.add(new Tuple2<>(scalar, pair._2));
              }

              if (pairs.isEmpty()) {
                return Collections.emptyListIterator();
              }

              return Collections.singletonList(sortedMSM(pairs)).iterator();
            })
        .reduce(GroupT::add);
  }

  /* Used for profiling only */
  public static <
          GroupT extends AbstractGroup<GroupT>, FieldT extends AbstractFieldElementExpanded<FieldT>>
      GroupT distributedBosCosterMSM(final JavaRDD<Tuple2<FieldT, GroupT>> input) {

    return input
        .mapPartitions(
            partition -> {
              final List<Tuple2<BigInteger, GroupT>> pairs = new ArrayList<>();

              while (partition.hasNext()) {
                Tuple2<FieldT, GroupT> part = partition.next();
                pairs.add(new Tuple2<>(part._1().toBigInteger(), part._2()));
              }

              if (pairs.isEmpty()) {
                return Collections.emptyListIterator();
              }

              return Collections.singletonList(bosCosterMSM(pairs)).iterator();
            })
        .reduce(GroupT::add);
  }

  /* Used for profiling only */
  public static <
          GroupT extends AbstractGroup<GroupT>, FieldT extends AbstractFieldElementExpanded<FieldT>>
      GroupT distributedPippengerMSM(final JavaRDD<Tuple2<FieldT, GroupT>> input) {

    return input
        .mapPartitions(
            partition -> {
              final List<Tuple2<BigInteger, GroupT>> pairs = new ArrayList<>();

              int numBits = 0;
              while (partition.hasNext()) {
                final Tuple2<FieldT, GroupT> part = partition.next();
                final BigInteger scalar = part._1().toBigInteger();

                pairs.add(new Tuple2<>(scalar, part._2()));
                numBits = Math.max(numBits, scalar.bitLength());
              }

              if (pairs.isEmpty()) {
                return Collections.emptyListIterator();
              }

              return Collections.singletonList(pippengerMSM(pairs, numBits)).iterator();
            })
        .reduce(GroupT::add);
  }
}
