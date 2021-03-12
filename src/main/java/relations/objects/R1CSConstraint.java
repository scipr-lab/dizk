/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations.objects;

import algebra.fields.AbstractFieldElementExpanded;
import java.io.Serializable;

/**
 * A R1CSRelation constraint is a formal expression of the form
 *
 * <p>< A , X > * < B , X > = < C , X > ,
 *
 * <p>where X = (x_0,x_1,...,x_m) is a vector of formal variables and A,B,C each consist of 1+m
 * elements in <FieldT>.
 *
 * <p>A R1CSRelation constraint is used to construct a R1CSRelation constraint system.
 */
// Similar to
// https://github.com/clearmatics/libsnark/blob/master/libsnark/relations/constraint_satisfaction_problems/r1cs/r1cs.tcc#L31-L37
public class R1CSConstraint<FieldT extends AbstractFieldElementExpanded<FieldT>>
    implements Serializable {

  private final LinearCombination<FieldT> A;
  private final LinearCombination<FieldT> B;
  private final LinearCombination<FieldT> C;

  public R1CSConstraint(
      LinearCombination<FieldT> _A, LinearCombination<FieldT> _B, LinearCombination<FieldT> _C) {
    A = _A;
    B = _B;
    C = _C;
  }

  public LinearTerm<FieldT> A(final int i) {
    return A.get(i);
  }

  public LinearTerm<FieldT> B(final int i) {
    return B.get(i);
  }

  public LinearTerm<FieldT> C(final int i) {
    return C.get(i);
  }

  public LinearCombination<FieldT> A() {
    return A;
  }

  public LinearCombination<FieldT> B() {
    return B;
  }

  public LinearCombination<FieldT> C() {
    return C;
  }

  public boolean equals(final R1CSConstraint<?> o) {
    return A.equals(o.A) && B.equals(o.B) && C.equals(o.C);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (!(o instanceof R1CSConstraint<?>)) {
      return false;
    }
    return (equals((R1CSConstraint<?>) o));
  }
}
