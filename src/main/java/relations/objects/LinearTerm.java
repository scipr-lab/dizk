/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations.objects;

import algebra.fields.AbstractFieldElement;
import java.io.Serializable;

// Equivalent to:
// https://github.com/clearmatics/libsnark/blob/master/libsnark/relations/variable.hpp#L92
public class LinearTerm<FieldT extends AbstractFieldElement<FieldT>> implements Serializable {

  private final long index;
  private final FieldT value;

  public LinearTerm(final long _index, final FieldT _value) {
    index = _index;
    value = _value;
  }

  public long index() {
    return index;
  }

  public FieldT value() {
    return value;
  }

  public boolean equals(final LinearTerm<?> o) {
    return (index == o.index) && value.equals(o.value);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (!(o instanceof LinearTerm<?>)) {
      return false;
    }
    return (equals((LinearTerm<?>) o));
  }
}
