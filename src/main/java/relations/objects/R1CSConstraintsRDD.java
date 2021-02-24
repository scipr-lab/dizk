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
 * <p>NOTE: The 0-th variable (i.e., "x_{0}") always represents the constant 1. Thus, the 0-th
 * variable is not included in num_variables.
 */
public class R1CSConstraintsRDD<FieldT extends AbstractFieldElementExpanded<FieldT>>
    implements Serializable {

  // Linear combinations represent additive sub-circuits.  In other words, each
  // LinearCombination can be represented as a dot product of X (the "wire"
  // vector) and another vector "selecting each wires" to "route" them.
  //
  // An R1CS is a set of 3 matrices A,B,C that are represented here by 3 sets
  // of linear combinations.
  //
  // Unlike the regular R1CSConstraints, the distributed constraints are stored
  // as flattened collections (RDDs) of pairs:
  //
  //   (constraint_idx, LinearTerm(var_idx, coefficient))
  //
  // with a collection for each of the A, B and C inputs. Multiple entries may
  // appear in each collection for a single constraint (where one of the inputs
  // is a linear combination involving multiple terms). For example, given the
  // inputs or "wire" vector (x0, x1, x2, ...), the i-th constraint:
  //
  //   (2 + 3*x1 + x4) * (7*x2 + x5) = (4 * x3)
  //
  // would appear as the following entries in A, B and C:
  //
  //            A                        B                        C
  //           ~~~                      ~~~                      ~~~
  //   ...                      ...                      ...
  //   (i, LinearTerm(0, 2))    (i, LinearTerm(2, 7))    (i, LinearTerm(0, 4))
  //   (i, LinearTerm(1, 3))    (i, LinearTerm(5, 1))    (i, LinearTerm(3, 1))
  //   (i, LinearTerm(4, 1))    ...                      ...
  //   ...                      ...                      ...

  private JavaPairRDD<Long, LinearTerm<FieldT>> A;
  private JavaPairRDD<Long, LinearTerm<FieldT>> B;
  private JavaPairRDD<Long, LinearTerm<FieldT>> C;
  private long numConstraints;

  public R1CSConstraintsRDD(
      final JavaPairRDD<Long, LinearTerm<FieldT>> _A,
      final JavaPairRDD<Long, LinearTerm<FieldT>> _B,
      final JavaPairRDD<Long, LinearTerm<FieldT>> _C,
      final long _numConstraints) {
    A = _A;
    B = _B;
    C = _C;
    numConstraints = _numConstraints;
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
    return numConstraints;
  }

  public void union(R1CSConstraintsRDD<FieldT> inputRDD) {
    A = A.union(inputRDD.A());
    B = B.union(inputRDD.B());
    C = C.union(inputRDD.C());
  }
}
