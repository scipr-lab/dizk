/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package bace.circuit;

import algebra.fields.AbstractFieldElementExpanded;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/* ProductGate implements class Gate with the specified behavior of a product gate in an arithmetic circuit. */
public class ProductGate<FieldT extends AbstractFieldElementExpanded<FieldT>> extends Gate<FieldT> {
    private Gate<FieldT> left;
    private Gate<FieldT> right;

    public ProductGate(Gate<FieldT> _left, Gate<FieldT> _right) {
        super(_left, _right);
        left = _left;
        right = _right;
    }

    public FieldT compute() {
        return left.compute().mul(right.compute());
    }

    public FieldT evaluate() {
        this.evaluatedResult = left.evaluatedResult.mul(right.evaluatedResult);
        return super.evaluate();
    }

    /* Returns the sum of the degrees */
    public Map<Long, BigInteger> degrees() {
        Map<Long, BigInteger> leftDegrees = left.degrees();
        Map<Long, BigInteger> childDegrees = new HashMap<>(leftDegrees);
        childDegrees.putAll(right.degrees());

        childDegrees.forEach((k, v) -> {
            childDegrees.put(k, v.add(leftDegrees.getOrDefault(k, BigInteger.ZERO)));
        });
        return childDegrees;
    }

    public long totalDegree() {
        return left.totalDegree() + right.totalDegree();
    }
}
