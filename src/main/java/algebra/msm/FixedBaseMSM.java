/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.msm;

import algebra.fields.AbstractFieldElementExpanded;
import algebra.groups.AbstractGroup;
import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FixedBaseMSM {

    /**
     * Compute table of window sizes.
     */
    public static <GroupT extends AbstractGroup<GroupT>> int getWindowSize(
            final long numScalars, final GroupT groupFactory) {

        if (groupFactory.fixedBaseWindowTable().isEmpty()) {
            return 17;
        }

        long window = 1;
        for (int i = groupFactory.fixedBaseWindowTable().size() - 1; i >= 0; i--) {
            final int value = groupFactory.fixedBaseWindowTable().get(i);
            if (value != 0 && numScalars >= value) {
                window = i + 1;
                break;
            }
        }

        return window > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) window;
    }

    /**
     * Computes the window table for a given base element.
     */
    public static <GroupT extends AbstractGroup<GroupT>> List<List<GroupT>> getWindowTable(
            final GroupT base, final int scalarSize, final int windowSize) {

        final int numWindows =
                (scalarSize % windowSize == 0) ? scalarSize / windowSize : scalarSize / windowSize + 1;
        final int innerLimit = (int) Math.pow(2, windowSize);

        final List<List<GroupT>> windowTable = new ArrayList<>();

        // If window table size is 0, return just the zero element.
        if (numWindows == 0) {
            return Collections.singletonList(Collections.singletonList(base.zero()));
        }

        GroupT baseOuter = base;
        for (int outer = 0; outer < numWindows; outer++) {
            windowTable.add(new ArrayList<>(innerLimit));
            GroupT baseInner = base.zero();
            for (int inner = 0; inner < innerLimit; inner++) {
                windowTable.get(outer).add(baseInner);
                baseInner = baseInner.add(baseOuter);
            }

            for (int w = 0; w < windowSize; w++) {
                baseOuter = baseOuter.dbl();
            }
        }

        return windowTable;
    }

    public static <T extends AbstractGroup<T>, FieldT extends AbstractFieldElementExpanded<FieldT>>
    T serialMSM(
            final int scalarSize,
            final int windowSize,
            final List<List<T>> multiplesOfBase,
            final FieldT scalar) {

        final int outerc = (scalarSize + windowSize - 1) / windowSize;
        final BigInteger bigScalar = scalar.toBigInteger();

        T res = multiplesOfBase.get(0).get(0);

        for (int outer = 0; outer < outerc; ++outer) {
            int inner = 0;
            for (int i = 0; i < windowSize; ++i) {
                if (bigScalar.testBit(outer * windowSize + i)) {
                    inner |= 1 << i;
                }
            }

            res = res.add(multiplesOfBase.get(outer).get(inner));
        }

        return res;
    }

    public static <T extends AbstractGroup<T>, FieldT extends AbstractFieldElementExpanded<FieldT>>
    List<T> batchMSM(
            final int scalarSize,
            final int windowSize,
            final List<List<T>> multiplesOfBase,
            final List<FieldT> scalars) {
        final List<T> res = new ArrayList<>(scalars.size());

        for (FieldT scalar : scalars) {
            res.add(serialMSM(scalarSize, windowSize, multiplesOfBase, scalar));
        }

        return res;
    }

    public static <GroupT extends AbstractGroup<GroupT>,
            FieldT extends AbstractFieldElementExpanded<FieldT>> JavaPairRDD<Long, GroupT>
    distributedBatchMSM(
            final int scalarSize,
            final int windowSize,
            final List<List<GroupT>> multiplesOfBase,
            final JavaPairRDD<Long, FieldT> scalars,
            final JavaSparkContext sc) {

        final Broadcast<List<List<GroupT>>> baseBroadcast = sc.broadcast(multiplesOfBase);

        return scalars.mapToPair(scalar -> new Tuple2<>(
                scalar._1,
                serialMSM(scalarSize, windowSize, baseBroadcast.getValue(), scalar._2)));
    }

    public static <G1T extends AbstractGroup<G1T>,
            G2T extends AbstractGroup<G2T>,
            FieldT extends AbstractFieldElementExpanded<FieldT>> List<Tuple2<G1T, G2T>> doubleBatchMSM(
            final int scalarSize1,
            final int windowSize1,
            final List<List<G1T>> multiplesOfBase1,
            final int scalarSize2,
            final int windowSize2,
            final List<List<G2T>> multiplesOfBase2,
            final List<FieldT> scalars) {

        final List<Tuple2<G1T, G2T>> res = new ArrayList<>(scalars.size());

        for (FieldT scalar : scalars) {
            res.add(new Tuple2<>(
                    serialMSM(scalarSize1, windowSize1, multiplesOfBase1, scalar),
                    serialMSM(scalarSize2, windowSize2, multiplesOfBase2, scalar)));
        }

        return res;
    }

    public static <G1T extends AbstractG1<G1T>,
            G2T extends AbstractG2<G2T>,
            FieldT extends AbstractFieldElementExpanded<FieldT>> JavaPairRDD<Long, Tuple2<G1T, G2T>>
    distributedDoubleBatchMSM(
            final int scalarSize1,
            final int windowSize1,
            final List<List<G1T>> multiplesOfBase1,
            final int scalarSize2,
            final int windowSize2,
            final List<List<G2T>> multiplesOfBase2,
            final JavaPairRDD<Long, FieldT> scalars,
            final JavaSparkContext sc) {

        final Broadcast<List<List<G1T>>> baseBroadcast1 = sc.broadcast(multiplesOfBase1);
        final Broadcast<List<List<G2T>>> baseBroadcast2 = sc.broadcast(multiplesOfBase2);

        return scalars.mapToPair(scalar -> new Tuple2<>(
                scalar._1,
                new Tuple2<>(
                        serialMSM(scalarSize1, windowSize1, baseBroadcast1.value(), scalar._2),
                        serialMSM(scalarSize2, windowSize2, baseBroadcast2.value(), scalar._2))));
    }
}
