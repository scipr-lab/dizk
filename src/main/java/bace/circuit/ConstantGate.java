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

/* ConstantGate implements class Gate with the specified behavior of
 * a constant gate in an arithmetic circuit. */
public class ConstantGate<FieldT extends AbstractFieldElementExpanded<FieldT>>
    extends Gate<FieldT> {
  private final FieldT value;

  public ConstantGate(FieldT v) {
    super(null, null);
    value = v;
  }

  public FieldT compute() {
    return value;
  }

  public FieldT evaluate() {
    this.evaluatedResult = value;
    return super.evaluate();
  }

  public Map<Long, BigInteger> degrees() {
    return new HashMap<>();
  }

  public long totalDegree() {
    return 0;
  }
}
