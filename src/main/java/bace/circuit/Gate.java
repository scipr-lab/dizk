/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package bace.circuit;

import algebra.fields.AbstractFieldElementExpanded;
import java.math.BigInteger;
import java.util.Map;

/* Gate is the abstract declaration of an arithmetic circuit gate, extendable
 * to sum gate SumGate, product gate ProductGate, constant gate ConstantGate,
 * and input gate InputGate. */
public abstract class Gate<FieldT extends AbstractFieldElementExpanded<FieldT>> {
  public Gate<FieldT> left;
  public Gate<FieldT> right;
  public boolean evaluated = false;
  FieldT evaluatedResult = null;

  public Gate(Gate<FieldT> _left, Gate<FieldT> _right) {
    left = _left;
    right = _right;
  }

  /* For circuit-driven evaluation, to prevent reliance on the JVM call stack. */
  public abstract FieldT compute();

  public FieldT evaluate() {
    this.evaluated = true;
    return this.evaluatedResult;
  }

  public boolean leftEvaluated() {
    return this.left == null || this.left.evaluated;
  }

  public boolean rightEvaluated() {
    return this.right == null || this.right.evaluated;
  }

  /* Clears the evaluation state. */
  public void clear() {
    this.evaluated = false;
    this.evaluatedResult = null;
  }

  /* Returns the degree of the circuit from root to children. */
  public abstract long totalDegree();

  public abstract Map<Long, BigInteger> degrees();

  public <GateT extends Gate<FieldT>> Gate<FieldT> add(GateT other) {
    return new SumGate<FieldT>(this, other);
  }

  public <GateT extends Gate<FieldT>> Gate<FieldT> mul(GateT other) {
    return new ProductGate<FieldT>(this, other);
  }
}
