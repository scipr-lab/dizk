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

/* SumGate implements class Gate with the specified behavior of a sum gate in an arithmetic circuit. */
public class SumGate<FieldT extends AbstractFieldElementExpanded<FieldT>> extends Gate<FieldT> {
    private Gate<FieldT> left;
    private Gate<FieldT> right;

    public SumGate(Gate<FieldT> _left, Gate<FieldT> _right) {
        super(_left, _right);
        left = _left;
        right = _right;
    }

    public FieldT compute() {
        return left.compute().add(right.compute());
    }

    public FieldT evaluate() {
        this.evaluatedResult = left.evaluatedResult.add(right.evaluatedResult);
        return super.evaluate();
    }

    public Map<Long, BigInteger> degrees() {
        Map<Long, BigInteger> leftDegrees = left.degrees();
        Map<Long, BigInteger> childDegrees = new HashMap<>(leftDegrees);
        childDegrees.putAll(right.degrees());

        childDegrees.forEach((k, v) -> {
            childDegrees.put(k, v.max(leftDegrees.getOrDefault(k, BigInteger.ZERO)));
        });
        return childDegrees;
    }

    public long totalDegree() {
        return Math.max(left.totalDegree(), right.totalDegree());
    }
}