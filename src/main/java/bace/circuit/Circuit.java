/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package bace.circuit;

import algebra.fields.AbstractFieldElementExpanded;
import scala.Tuple3;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/* Circuit is the implementation of an arithmetic circuit. A Circuit is composed of Gates
 * and given an input array, evaluated on the specified gates. */
public class Circuit<FieldT extends AbstractFieldElementExpanded<FieldT>> implements Serializable {
    private ArrayList<InputGate<FieldT>> inputGates;
    private Gate<FieldT> resultGate;
    public int inputSize;

    public Circuit(ArrayList<InputGate<FieldT>> _inputGates, Gate<FieldT> _resultGate) {
        inputGates = _inputGates;
        resultGate = _resultGate;
        inputSize = _inputGates.size();
    }

    /* Returns the evaluation of the circuit with the provided input. */
    public FieldT compute(ArrayList<FieldT> input) throws Exception {
        if (input.size() != this.inputSize) throw new Exception("Assignment size must match circuit size");

        for (int i = 0; i < this.inputSize; i++) {
            this.inputGates.get(i).loadValue(input.get(i));
        }
        return this.evaluate();
    }

    /* Returns the stack-based evaluation of the circuit gates. */
    private FieldT evaluate() {
        this.clear();
        Stack<Gate<FieldT>> computationStack = new Stack<>();
        computationStack.push(this.resultGate);

        while (computationStack.size() > 0) { /* Post-order traversal. */
            Gate<FieldT> topGate = computationStack.pop();
            if (!topGate.leftEvaluated()) computationStack.push(topGate.left);
            else if (!topGate.rightEvaluated()) computationStack.push(topGate.right);
            else {
                topGate.evaluate(); /* Evaluate self after its left and right child gates are evaluated. */
                computationStack.pop(); /* After evaluation, remove self from computation stack. */
            }
        }

        return this.resultGate.evaluatedResult;
    }

    /* Returns the largest degree computed by the circuit. */
    public long totalDegree() {
        return this.resultGate.totalDegree();
    }

    /* Returns the sum of the variable gate degrees. */
    public BigInteger degreeSum() {
        return this.resultGate.degrees().values().stream().reduce((a, b) -> a.add(b)).orElse(BigInteger.ZERO);
    }

    /* Returns true if the circuit has no loops. */
    public boolean isValid() {
        return !hasLoops();
    }

    /* Clears the state of the circuit in post-order traversal. */
    private void clear() {
        /* Format of element: (gate, leftTraversed, rightTraversed). */
        Stack<Tuple3<Gate<FieldT>, Boolean, Boolean>> traversalStack = new Stack<>();
        traversalStack.push(new Tuple3<>(this.resultGate, false, false));

        HashSet<Gate<FieldT>> visited = new HashSet<>();
        visited.add(this.resultGate);

        while (traversalStack.size() > 0) {
            Tuple3<Gate<FieldT>, Boolean, Boolean> top = traversalStack.pop();
            Gate<FieldT> topGate = top._1();
            boolean leftTraversed = top._2();
            boolean rightTraversed = top._3();

            if (topGate.left != null && !visited.contains(topGate.left) && !leftTraversed) {
                traversalStack.push(new Tuple3<>(topGate, true, rightTraversed));
                traversalStack.push(new Tuple3<>(topGate.left, false, false));
            } else if (topGate.right != null && !visited.contains(topGate.right) && !rightTraversed) {
                traversalStack.push(new Tuple3<>(topGate, leftTraversed, true));
                traversalStack.push(new Tuple3<>(topGate.right, false, false));
            } else {
                topGate.clear();
            }
        }
    }

    /* Returns true if any path contains a loop. */
    private boolean hasLoops() {
        HashSet<Gate<FieldT>> visited = new HashSet<>();
        return _hasLoops(visited, this.resultGate);
    }

    /* A backtracking for loops from result to input. */
    private boolean _hasLoops(HashSet<Gate<FieldT>> visited, Gate<FieldT> gate) {
        if (visited.contains(gate)) return true;

        visited.add(gate);
        if (gate.left != null) {
            if (_hasLoops(visited, gate.left)) return true;
        }
        if (gate.right != gate.left && gate.right != null) {
            if (_hasLoops(visited, gate.right)) return true;
        }
        visited.remove(gate);

        return false;
    }
}
