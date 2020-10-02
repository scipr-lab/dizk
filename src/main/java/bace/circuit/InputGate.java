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

/* InputGate implements class Gate with the specified behavior of an input gate
 * in an arithmetic circuit. The coefficient value is stored in VALUE and input
 * gate index in INDEX. */
public class InputGate<FieldT extends AbstractFieldElementExpanded<FieldT>> extends Gate<FieldT> {
  private FieldT value;
  private final long index;

  public InputGate(FieldT _value, long _index) {
    super(null, null);
    value = _value;
    index = _index;
  }

  public FieldT compute() {
    return value;
  }

  public FieldT evaluate() {
    this.evaluatedResult = value;
    return super.evaluate();
  }

  public void loadValue(FieldT newValue) {
    this.value = newValue;
  }

  public Map<Long, BigInteger> degrees() {
    HashMap<Long, BigInteger> hm = new HashMap<>();
    hm.put(index, BigInteger.ONE);
    return hm;
  }

  public long totalDegree() {
    return 1;
  }
}
