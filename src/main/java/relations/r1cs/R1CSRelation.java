/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations.r1cs;

import algebra.fields.AbstractFieldElementExpanded;
import java.io.Serializable;
import relations.objects.Assignment;
import relations.objects.R1CSConstraint;
import relations.objects.R1CSConstraints;

/**
 * A system of R1CSRelation constraints looks like
 *
 * <p>{ < A_k , X > * < B_k , X > = < C_k , X > }_{k=1}^{n}
 *
 * <p>In other words, the system is satisfied if and only if there exist a USCS variable assignment
 * for which each R1CSRelation constraint is satisfied.
 *
 * <p>NOTE: The 0-th variable (i.e., "x_{0}") always represents the constant 1. Thus, the 0-th
 * variable is not included in num_variables.
 *
 * <p>A R1CSRelation variable assignment is a vector of <FieldT> elements that represents a
 * candidate solution to a R1CSRelation constraint system.
 */
public class R1CSRelation<FieldT extends AbstractFieldElementExpanded<FieldT>>
    implements Serializable {

  private final R1CSConstraints<FieldT> constraints;

  private final int numInputs;
  private final int numAuxiliary;
  private final int numConstraints;

  public R1CSRelation(
      final R1CSConstraints<FieldT> _constraints, final int _numInputs, final int _numAuxiliary) {
    constraints = _constraints;
    numInputs = _numInputs;
    numAuxiliary = _numAuxiliary;
    numConstraints = _constraints.size();
  }

  public boolean isValid() {
    if (this.numInputs() > this.numVariables()) {
      return false;
    }

    final int numVariables = this.numVariables();
    for (int i = 0; i < numConstraints; i++) {
      final R1CSConstraint<FieldT> constraint = constraints.get(i);

      if (!(constraint.A().isValid(numVariables)
          && constraint.B().isValid(numVariables)
          && constraint.C().isValid(numVariables))) {
        return false;
      }
    }
    return true;
  }

  public boolean isSatisfied(final Assignment<FieldT> primary, final Assignment<FieldT> auxiliary) {
    assert (primary.size() == this.numInputs());
    assert (primary.size() + auxiliary.size() == this.numVariables());

    // Assert first element == FieldT.one().
    final FieldT firstElement = primary.elements().get(0);
    final FieldT one = firstElement.one();
    assert (firstElement.equals(one));

    final Assignment<FieldT> oneFullAssignment = new Assignment<>(primary, auxiliary);

    for (int i = 0; i < numConstraints; i++) {
      final FieldT a = constraints.get(i).A().evaluate(oneFullAssignment);
      final FieldT b = constraints.get(i).B().evaluate(oneFullAssignment);
      final FieldT c = constraints.get(i).C().evaluate(oneFullAssignment);

      if (!a.mul(b).equals(c)) {
        System.out.println("R1CSConstraint unsatisfied:");
        System.out.println("<a,(1,x)> = " + a);
        System.out.println("<b,(1,x)> = " + b);
        System.out.println("<c,(1,x)> = " + c);
        return false;
      }
    }
    return true;
  }

  public R1CSConstraint<FieldT> constraints(final int i) {
    return constraints.get(i);
  }

  public int numInputs() {
    return numInputs;
  }

  public int numVariables() {
    return numInputs + numAuxiliary;
  }

  public int numConstraints() {
    return numConstraints;
  }
}
