/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.msm;

import algebra.fields.AbstractFieldElementExpanded;
import algebra.groups.AbstractGroup;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.spark.api.java.JavaPairRDD;

/**
 * Class exposing naive methods for MSM (i.e. no speedup used to compute the sum of multiple EC
 * multiplications, the MSM result is obtained by summing the individual multiplications.)
 */
public class NaiveMSM {

  public static <GroupT extends AbstractGroup<GroupT>> ArrayList<GroupT> fixedBaseMSM(
      List<BigInteger> scalars, GroupT base) {
    ArrayList<GroupT> result = new ArrayList<>(scalars.size());

    for (int i = 0; i < scalars.size(); i++) {
      result.add(base.mul(scalars.get(i)));
    }

    return result;
  }

  /**
   * Computes a Multi-Scalar Multiplication. i.e. on input bases <P1, ..., Pn> and scalars <s1, ...,
   * sn>, the function returns the group element R = s1 * P1 + ... + sn * Pn
   */
  public static <GroupT extends AbstractGroup<GroupT>> GroupT variableBaseMSM(
      ArrayList<BigInteger> scalars, ArrayList<GroupT> bases) {
    assert (scalars.size() == bases.size());
    assert (scalars.size() > 0);

    GroupT result = bases.get(0).zero();

    for (int i = 0; i < scalars.size(); i++) {
      result = result.add(bases.get(i).mul(scalars.get(i)));
    }

    return result;
  }

  /**
   * Computes a Multi-Scalar Multiplication. i.e. on input bases <P1, ..., Pn> and scalars <s1, ...,
   * sn>, the function returns the field element R = s1 * P1 + ... + sn * Pn
   */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>> FieldT variableBaseMSM(
      List<FieldT> scalars, List<FieldT> bases) {
    assert (scalars.size() == bases.size());
    assert (scalars.size() > 0);

    FieldT result = bases.get(0).zero();

    for (int i = 0; i < scalars.size(); i++) {
      result = result.add(scalars.get(i).mul(bases.get(i)));
    }

    return result;
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      FieldT distributedVariableBaseMSM(
          final JavaPairRDD<Long, FieldT> scalars, final JavaPairRDD<Long, FieldT> bases) {
    return scalars.join(bases).map(pair -> pair._2._1.mul(pair._2._2)).reduce(FieldT::add);
  }
}
