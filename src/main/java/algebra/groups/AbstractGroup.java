/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.groups;

import algebra.fields.AbstractFieldElementExpanded;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

public abstract class AbstractGroup<GroupT extends AbstractGroup<GroupT>> implements Serializable {

  /* Returns self element */
  public abstract GroupT self();

  /* Returns this + other */
  public abstract GroupT add(final GroupT other);

  /* Returns this - other */
  public abstract GroupT sub(final GroupT other);

  /* Returns (BigInteger) scalar * this */
  public GroupT mul(final BigInteger scalar) {
    GroupT base = this.self();

    if (scalar.equals(BigInteger.ONE)) {
      return base;
    }

    GroupT result = base.zero();

    boolean found = false;
    for (int i = scalar.bitLength() - 1; i >= 0; i--) {
      if (found) {
        result = result.dbl();
      }

      if (scalar.testBit(i)) {
        found = true;
        result = result.add(base);
      }
    }

    return result;
  }

  /* Returns (FieldT) scalar * this */
  public GroupT mul(final AbstractFieldElementExpanded other) {
    return this.mul(other.toBigInteger());
  }

  /* Returns the zero element of the group */
  public abstract GroupT zero();

  /* Returns the one element of the group */
  public abstract GroupT one();

  /* Returns this == zero */
  public abstract boolean isZero();

  /* Returns this == one */
  public abstract boolean isOne();

  /* Returns -this */
  public abstract GroupT negate();

  /* Returns this+this */
  public abstract GroupT dbl();

  /* Fixed base window table for G1 and G2. */
  public abstract ArrayList<Integer> fixedBaseWindowTable();

  /**
   * If secureSeed is provided, returns cryptographically secure random group element using byte[].
   * Else if seed is provided, returns pseudorandom group element using long as seed. Else, returns
   * a pseudorandom group element without a seed.
   */
  public abstract GroupT random(final Long seed, final byte[] secureSeed);

  /* Returns this == other */
  public abstract boolean equals(final GroupT other);
}
