/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations.objects;

import algebra.fields.AbstractFieldElementExpanded;
import java.io.Serializable;
import org.apache.spark.api.java.JavaPairRDD;

/**
 * A system of R1CSRelation constraints looks like
 *
 * <p>{ < A_k , X > * < B_k , X > = < C_k , X > }_{k=1}^{n}
 *
 * <p>In other words, the system is satisfied if and only if there exist a USCS variable assignment
 * for which each R1CSRelation constraint is satisfied.
 *
 * <p>NOTE: The 0-th variable (i.e., "x_{0}") always represents the constant 1 Thus, the 0-th
 * variable is not included in num_variables.
 */
public class R1CSConstraintsRDD<FieldT extends AbstractFieldElementExpanded<FieldT>>
    implements Serializable {

  private JavaPairRDD<Long, LinearTerm<FieldT>> A;
  private JavaPairRDD<Long, LinearTerm<FieldT>> B;
  private JavaPairRDD<Long, LinearTerm<FieldT>> C;
  private long constraintSize;

  public R1CSConstraintsRDD(
      final JavaPairRDD<Long, LinearTerm<FieldT>> _A,
      final JavaPairRDD<Long, LinearTerm<FieldT>> _B,
      final JavaPairRDD<Long, LinearTerm<FieldT>> _C,
      final long _constraintSize) {
    A = _A;
    B = _B;
    C = _C;
    constraintSize = _constraintSize;
  }

  public JavaPairRDD<Long, LinearTerm<FieldT>> A() {
    return A;
  }

  public JavaPairRDD<Long, LinearTerm<FieldT>> B() {
    return B;
  }

  public JavaPairRDD<Long, LinearTerm<FieldT>> C() {
    return C;
  }

  public long size() {
    return constraintSize;
  }

  public void union(R1CSConstraintsRDD<FieldT> inputRDD) {
    A = A.union(inputRDD.A());
    B = B.union(inputRDD.B());
    C = C.union(inputRDD.C());
  }
}
