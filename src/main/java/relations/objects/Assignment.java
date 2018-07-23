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
import java.util.List;

public class Assignment<FieldT extends AbstractFieldElementExpanded<FieldT>> implements
        Serializable {

    private ArrayList<FieldT> elements;

    public Assignment() {
        elements = new ArrayList<>();
    }

    public Assignment(final List<FieldT> input) {
        elements = new ArrayList<>(input);
    }

    public Assignment(final Assignment<FieldT> primary, final Assignment<FieldT> auxiliary) {
        elements = new ArrayList<>();
        elements.addAll(primary.elements());
        elements.addAll(auxiliary.elements());
    }

    public ArrayList<FieldT> elements() {
        return elements;
    }

    public List<FieldT> subList(final int i, final int j) {
        return elements.subList(i, j);
    }

    public boolean add(final FieldT element) {
        return elements.add(element);
    }

    public boolean addAll(final Assignment<FieldT> input) {
        return elements.addAll(input.elements());
    }

    public FieldT set(final int i, final FieldT element) {
        return elements.set(i, element);
    }

    public FieldT get(final int i) {
        return elements.get(i);
    }

    public int size() {
        return elements.size();
    }

}
