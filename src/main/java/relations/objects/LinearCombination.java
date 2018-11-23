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

public class LinearCombination<FieldT extends AbstractFieldElementExpanded<FieldT>> implements
        Serializable {

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

    public FieldT evaluate(final Assignment<FieldT> input) {
        FieldT result = input.get(0).zero();

        for (int i = 0; i < terms.size(); i++) {
            final long index = terms.get(i).index();
            final FieldT value = input.get((int) index).mul(terms.get(i).value());

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
}
