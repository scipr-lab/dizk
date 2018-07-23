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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class NaiveMSM {

    public static <GroupT extends AbstractGroup<GroupT>> ArrayList<GroupT> fixedBaseMSM(
            List<BigInteger> scalars,
            GroupT base) {
        ArrayList<GroupT> result = new ArrayList<>(scalars.size());

        for (int i = 0; i < scalars.size(); i++) {
            result.add(base.mul(scalars.get(i)));
        }

        return result;
    }

    public static <GroupT extends AbstractGroup<GroupT>> GroupT variableBaseMSM(
            ArrayList<BigInteger> scalars,
            ArrayList<GroupT> bases) {
        assert (scalars.size() == bases.size());
        assert (scalars.size() > 0);

        GroupT result = bases.get(0).zero();

        for (int i = 0; i < scalars.size(); i++) {
            result = result.add(bases.get(i).mul(scalars.get(i)));
        }

        return result;
    }

    public static <FieldT extends AbstractFieldElementExpanded<FieldT>> FieldT variableBaseMSM(
            List<FieldT> scalars,
            List<FieldT> bases) {
        assert (scalars.size() == bases.size());
        assert (scalars.size() > 0);

        FieldT result = bases.get(0).zero();

        for (int i = 0; i < scalars.size(); i++) {
            result = result.add(scalars.get(i).mul(bases.get(i)));
        }

        return result;
    }

    public static <FieldT extends AbstractFieldElementExpanded<FieldT>> FieldT
    distributedVariableBaseMSM(
            final JavaPairRDD<Long, FieldT> scalars,
            final JavaPairRDD<Long, FieldT> bases) {
        return scalars.join(bases).map(pair -> pair._2._1.mul(pair._2._2)).reduce(FieldT::add);
    }
}
