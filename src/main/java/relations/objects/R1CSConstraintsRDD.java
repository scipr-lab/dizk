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
 * <p>In other words, the system is satisfied if and only if there exist a USCS (Unitary-Square
 * Constraint System) variable assignment for which each R1CSRelation constraint is satisfied.
 *
 * <p>NOTE: The 0-th variable (i.e., "x_{0}") always represents the constant 1 Thus, the 0-th
 * variable is not included in num_variables.
 */
public class R1CSConstraintsRDD<FieldT extends AbstractFieldElementExpanded<FieldT>>
    implements Serializable {

  // Linear combinations represent additive sub-circuits
  // In other words, each LinearCombination can be represented as a dot product between X (the
  // "wire" vector)
  // and another vector "selecting each wires" to "route" them.
  //
  // An R1CS is a set of 3 matrices A,B,C that are represented here by a set of 3 sets of
  // linear combinations.
  //
  // NOTE: `R1CSConstraintsRDD` is handled differently than "normal"/non-distributed R1CS which is a
  // set of "constraints" each of which being a triple of linear combinations.
  // In fact, here the linear combinations are represented using "lists" of Linear Terms. The `Long`
  // key of the tuple/pair `Tuple2<Long, LinearTerm<FieldT>>` (which is "implied" by the
  // `JavaPairRDD`) represents the "index" of the linear combination of interest.
  // For instance, the 1*a+2*b+5*d represented by <1,2,0,5> . <a,b,c,d>, leading to the ith matrix
  // line [1 2 0 5] in the R1CS can be represented by:
  // < Tuple2<Long, LinearTerm<Field>>(i, LinearTerm(0, 1)), Tuple2<Long, LinearTerm<Field>>(i,
  // LinearTerm(1, 2)), Tuple2<Long, LinearTerm<Field>>(i, LinearTerm(3, 0)), Tuple2<Long,
  // LinearTerm<Field>>(i, LinearTerm(4, 5)) >
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
