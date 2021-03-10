/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations.objects;

import algebra.fields.AbstractFieldElementExpanded;
import java.io.Serializable;
import java.util.ArrayList;

public class LinearCombination<FieldT extends AbstractFieldElementExpanded<FieldT>>
    implements Serializable {

  private ArrayList<LinearTerm<FieldT>> terms;

  public LinearCombination() {
    terms = new ArrayList<>();
  }

  public boolean add(final LinearTerm<FieldT> term) {
    return terms.add(term);
  }

  public boolean isValid(final int numVariables) {
    // Check that the variables are in proper range.
    for (int i = 0; i < terms.size(); i++) {
      if (terms.get(i).index() > numVariables) {
        return false;
      }
    }

    return true;
  }

  /// WARNING: There are a few differences between how the protoboard is implemented in libsnark and
  // how
  /// the constraint system is handled here. In libsnark, the variable ONE is added by default as
  // the first variable
  /// on the protoboard (the user does not have to bother specifying ONE in their assignement). This
  // is why
  /// in the linear combination evaluation (see here:
  // https://github.com/clearmatics/libsnark/blob/master/libsnark/relations/variable.tcc#L267)
  /// there is shift by one (in `assignment[lt.index-1]`) because the linear_combination.size() =
  // assignment.size() + 1 since
  /// the linear combination's first entry will be for ONE, while the first entry in `assignment`
  // will be for the next/first user-defined
  /// variable. As such we have, for instance:
  ///  ___
  /// | 5 |   ____
  /// | 0 |  | 12 |
  /// | 2 |  | 1  |
  /// | 4 |  | 1  |
  /// | 1 |  | 1  |
  ///   ^       ^
  ///   |       |
  /// LinComb  Assignment
  ///
  /// Will return: 5*ONE + 12*0 + 2*1 + 4*1 + 1*1 = 5+2+4+1 = 12
  /// when evaluated by the function below (i.e. the first entry in the linear combination is
  // interpreted as factor of ONE)
  ///
  /// HOWEVER, here in DIZK, things are managed differently, and the ONE variable needs to be given
  // as part of the assignment.
  /// As such, there is not such "shift-by-one", hence, the ith entry in LinComb gets multiplied by
  // the ith entry in assignment
  /// (and not by the (i-1)th entry as in libsnark).
  public FieldT evaluate(final Assignment<FieldT> input) {
    FieldT result = input.get(0).zero();
    final FieldT one = result.one();

    // Note: Assuming the "coefficient" representation of the linear combination A = [0 0 1 0 0]
    // then the 0 terms are not represented in the LinearCombination object. In fact, in this
    // example, A will be an Array of LinearTerms of size 1, with a single term LinearTerm(index =
    // 2, value = 1)
    // Hence, the ternary operation in the `for` loop below will assign `one` to `value` only when
    // the `ONE` variable
    // is selected in the linear combination.
    for (int i = 0; i < terms.size(); i++) {
      final long index = terms.get(i).index();
      final FieldT value = (index == 0 ? one : input.get((int) index)).mul(terms.get(i).value());

      result = result.add(value);
    }
    return result;
  }

  public FieldT getValue(final int i) {
    if (terms.size() == 0) {
      return null;
    }

    for (LinearTerm<FieldT> term : terms) {
      if (term.index() == i) {
        return term.value();
      }
    }
    return null;
  }

  public LinearTerm<FieldT> get(final int i) {
    return terms.get(i);
  }

  public ArrayList<LinearTerm<FieldT>> terms() {
    return terms;
  }

  public int size() {
    return terms.size();
  }

  public boolean equals(final LinearCombination<?> o) {
    return terms.equals(o.terms);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (!(o instanceof LinearCombination<?>)) {
      return false;
    }
    return (equals((LinearCombination<?>) o));
  }
}
