/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields;

import java.math.BigInteger;

public abstract class AbstractFieldElementExpanded<
        FieldT extends AbstractFieldElementExpanded<FieldT>>
    extends AbstractFieldElement<FieldT> {

  /* Returns omega s.t. omega^order == one() */
  public abstract FieldT rootOfUnity(final long order);

  /* Returns a generator of the multiplicative subgroup of the field */
  public abstract FieldT multiplicativeGenerator();

  /* Returns field element as FieldT(value) */
  public abstract FieldT construct(final long value);

  /* Returns field element as FieldT(value) */
  public abstract FieldT construct(final BigInteger value);

  /* Returns this as a BigInteger */
  public abstract BigInteger toBigInteger();
}
