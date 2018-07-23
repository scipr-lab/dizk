/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.msm;

import algebra.fields.AbstractFieldElementExpanded;
import algebra.groups.AbstractGroup;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class VariableBaseMSM {

    public static final BigInteger BOS_COSTER_MSM_THRESHOLD = new BigInteger("1048576");

    /**
     * The algorithm starts by sorting the input in order of scalar size. For each iteration, it
     * computes the difference of the two largest scalars, and multiplies the accrued base by this
     * difference of exponents. This result is then summed and lastly returned.
     */
    public static <GroupT extends AbstractGroup<GroupT>> GroupT sortedMSM(
            final List<Tuple2<BigInteger, GroupT>> input) {
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
     * The Bos-Coster algorithm works by repeatedly recursively computing
     * (e1 - e2) * b1 + e2 * (b1 + b2) where the scalars are ordered such that
     * e1 >= e2 >= ... >= en in a priority queue. The result is summed and
     * returned after all scalar-base pairs have been computed.
     */
    public static <GroupT extends AbstractGroup<GroupT>> GroupT bosCosterMSM(
            final List<Tuple2<BigInteger, GroupT>> input) {
        final PriorityQueue<Tuple2<BigInteger, GroupT>> sortedScalarPairs = new PriorityQueue<>(
                input.size(),
                (v1, v2) -> v2._1.compareTo(v1._1));
        input.forEach(sortedScalarPairs::add);

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

    public static <GroupT extends AbstractGroup<GroupT>,
            FieldT extends AbstractFieldElementExpanded<FieldT>> GroupT serialMSM(
            final List<FieldT> scalars, final List<GroupT> bases) {
        assert (bases.size() == scalars.size());

        GroupT acc = bases.get(0).zero();
        final List<Tuple2<BigInteger, GroupT>> filteredInput = new ArrayList<>();
        for (int i = 0; i < bases.size(); i++) {
            final BigInteger scalar = scalars.get(i).toBigInteger();
            if (scalar.equals(BigInteger.ZERO)) {
                continue;
            }

            final GroupT base = bases.get(i);

            // Mixed addition
            if (scalar.equals(BigInteger.ONE)) {
                acc = acc.add(base);
            } else {
                filteredInput.add(new Tuple2<>(scalar, base));
            }
        }

        if (!filteredInput.isEmpty()) {
            acc = acc.add(bosCosterMSM(filteredInput));
        }

        return acc;
    }

    public static <T1 extends AbstractGroup<T1>,
            T2 extends AbstractGroup<T2>,
            FieldT extends AbstractFieldElementExpanded<FieldT>> Tuple2<T1, T2> doubleMSM(
            final List<FieldT> scalars, final List<Tuple2<T1, T2>> bases) {
        assert (bases.size() == scalars.size());

        final int size = bases.size();
        assert (size > 0);

        final ArrayList<Tuple2<BigInteger, T1>> converted1 = new ArrayList<>(size);
        final ArrayList<Tuple2<BigInteger, T2>> converted2 = new ArrayList<>(size);

        T1 acc1 = bases.get(0)._1.zero();
        T2 acc2 = bases.get(0)._2.zero();

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
            }
        }

        return new Tuple2<>(
                converted1.isEmpty() ? acc1 : acc1.add(VariableBaseMSM.bosCosterMSM(converted1)),
                converted2.isEmpty() ? acc2 : acc2.add(VariableBaseMSM.bosCosterMSM(converted2)));
    }

    public static <T1 extends AbstractGroup<T1>, T2 extends AbstractGroup<T2>> Tuple2<T1, T2>
    doubleMSM(
            final List<Tuple2<BigInteger, Tuple2<T1, T2>>> input) {
        final int size = input.size();
        assert (size > 0);

        ArrayList<Tuple2<BigInteger, T1>> converted1 = new ArrayList<>(size);
        ArrayList<Tuple2<BigInteger, T2>> converted2 = new ArrayList<>(size);

        T1 acc1 = input.get(0)._2._1.zero();
        T2 acc2 = input.get(0)._2._2.zero();

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
            }
        }

        return new Tuple2<>(
                converted1.isEmpty() ? acc1 : acc1.add(VariableBaseMSM.bosCosterMSM(converted1)),
                converted2.isEmpty() ? acc2 : acc2.add(VariableBaseMSM.bosCosterMSM(converted2)));
    }

    public static <GroupT extends AbstractGroup<GroupT>,
            FieldT extends AbstractFieldElementExpanded<FieldT>> GroupT distributedMSM(
            final JavaRDD<Tuple2<FieldT, GroupT>> input) {
        return input.mapPartitions(partition -> {
            final List<Tuple2<BigInteger, GroupT>> pairs = new ArrayList<>();
            while (partition.hasNext()) {
                final Tuple2<FieldT, GroupT> pair = partition.next();
                final BigInteger scalar = pair._1.toBigInteger();
                if (scalar.equals(BigInteger.ZERO)) {
                    continue;
                }
                pairs.add(new Tuple2<>(scalar, pair._2));
            }
            return Collections.singletonList(bosCosterMSM(pairs)).iterator();
        }).reduce(GroupT::add);
    }

    public static <G1T extends AbstractGroup<G1T>,
            G2T extends AbstractGroup<G2T>,
            FieldT extends AbstractFieldElementExpanded<FieldT>> Tuple2<G1T, G2T> distributedDoubleMSM(
            final JavaRDD<Tuple2<FieldT, Tuple2<G1T, G2T>>> input) {
        return input.mapPartitions(partition -> {
            final List<Tuple2<BigInteger, Tuple2<G1T, G2T>>> pairs = new ArrayList<>();
            while (partition.hasNext()) {
                Tuple2<FieldT, Tuple2<G1T, G2T>> pair = partition.next();
                final BigInteger scalar = pair._1.toBigInteger();
                if (scalar.equals(BigInteger.ZERO)) {
                    continue;
                }
                pairs.add(new Tuple2<>(scalar, pair._2));
            }
            return Collections.singletonList(doubleMSM(pairs)).iterator();
        }).reduce((e1, e2) -> new Tuple2<>(e1._1.add(e2._1), e1._2.add(e2._2)));
    }

    /* Used for profiling only */
    public static <GroupT extends AbstractGroup<GroupT>,
            FieldT extends AbstractFieldElementExpanded<FieldT>> GroupT distributedSortedMSM(
            final JavaRDD<Tuple2<FieldT, GroupT>> input) {
        return input.mapPartitions(partition -> {
            final List<Tuple2<BigInteger, GroupT>> pairs = new ArrayList<>();
            while (partition.hasNext()) {
                final Tuple2<FieldT, GroupT> pair = partition.next();
                final BigInteger scalar = pair._1.toBigInteger();
                if (scalar.equals(BigInteger.ZERO)) {
                    continue;
                }
                pairs.add(new Tuple2<>(scalar, pair._2));
            }
            return Collections.singletonList(sortedMSM(pairs)).iterator();
        }).reduce(GroupT::add);
    }

    /* Used for profiling only */
    static <GroupT extends AbstractGroup<GroupT>> GroupT distributedSortedMSM(
            final JavaPairRDD<BigInteger, GroupT> input) {
        return input.mapPartitions(partition -> {
            final List<Tuple2<BigInteger, GroupT>> pairs = new ArrayList<>();
            while (partition.hasNext()) {
                pairs.add(partition.next());
            }
            return Collections.singletonList(sortedMSM(pairs)).iterator();
        }).reduce(GroupT::add);
    }

    /* Used for profiling only */
    static <GroupT extends AbstractGroup<GroupT>> GroupT distributedBosCosterMSM(
            final JavaPairRDD<BigInteger, GroupT> input) {
        return input.mapPartitions(partition -> {
            final List<Tuple2<BigInteger, GroupT>> pairs = new ArrayList<>();
            while (partition.hasNext()) {
                pairs.add(partition.next());
            }
            return Collections.singletonList(bosCosterMSM(pairs)).iterator();
        }).reduce(GroupT::add);
    }

}
