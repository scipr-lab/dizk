/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations.qap;

import algebra.fields.AbstractFieldElementExpanded;
import java.io.Serializable;
import java.util.List;
import relations.objects.Assignment;

public class QAPWitness<FieldT extends AbstractFieldElementExpanded<FieldT>>
    implements Serializable {

  private final Assignment<FieldT> coefficientsABC;
  private final List<FieldT> coefficientsH;

  private final int numInputs;
  private final int numVariables;
  private final int degree;

  public QAPWitness(
      final Assignment<FieldT> _coefficientsABC,
      final List<FieldT> _coefficientsH,
      final int _numInputs,
      final int _numVariables,
      final int _degree) {

    coefficientsABC = _coefficientsABC;
    coefficientsH = _coefficientsH;

    numInputs = _numInputs;
    numVariables = _numVariables;
    degree = _degree;
  }

  public Assignment<FieldT> coefficientsABC() {
    return coefficientsABC;
  }

  public FieldT coefficientsH(final int i) {
    return coefficientsH.get(i);
  }

  public List<FieldT> coefficientsH() {
    return coefficientsH;
  }

  public int numInputs() {
    return numInputs;
  }

  public int numVariables() {
    return numVariables;
  }

  public int degree() {
    return degree;
  }
}
