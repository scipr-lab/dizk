/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.groups.mock;

import algebra.groups.AbstractGroup;
import algebra.groups.mock.abstractintegergroupparameters.AbstractAdditiveIntegerGroupParameters;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

/*
 * AdditiveIntegerGroup is an additive cyclic group of order r.
 * If the order is not provided for the constructor, the group behaves as the integer group Z.
 */
public class AdditiveIntegerGroup extends AbstractGroup<AdditiveIntegerGroup> {

  protected final BigInteger number;
  private final AbstractAdditiveIntegerGroupParameters GroupParameters;
  private final ArrayList<Integer> fixedBaseWindowTable = new ArrayList<>(0);

  public AdditiveIntegerGroup(
      final BigInteger number, final AbstractAdditiveIntegerGroupParameters GroupParameters) {
    if (GroupParameters.modulus() == null) {
      this.number = number;
    } else {
      this.number = number.mod(GroupParameters.modulus());
    }

    this.GroupParameters = GroupParameters;
  }

  public AdditiveIntegerGroup(
      final String number, final AbstractAdditiveIntegerGroupParameters GroupParameters) {
    this(new BigInteger(number), GroupParameters);
  }

  public AdditiveIntegerGroup(
      final long number, final AbstractAdditiveIntegerGroupParameters GroupParameters) {
    this(new BigInteger(Long.toString(number)), GroupParameters);
  }

  public AdditiveIntegerGroup self() {
    return this;
  }

  public AdditiveIntegerGroup add(final AdditiveIntegerGroup other) {
    return new AdditiveIntegerGroup(this.number.add(other.number), GroupParameters);
  }

  public AdditiveIntegerGroup sub(final AdditiveIntegerGroup other) {
    return new AdditiveIntegerGroup(this.number.subtract(other.number), GroupParameters);
  }

  public AdditiveIntegerGroup mul(final BigInteger scalar) {
    if (scalar.equals(BigInteger.ONE)) {
      return this;
    }

    AdditiveIntegerGroup result = zero();

    boolean found = false;
    for (int i = scalar.bitLength() - 1; i >= 0; i--) {
      if (found) {
        result = result.dbl();
      }

      if (scalar.testBit(i)) {
        found = true;
        result = result.add(this);
      }
    }

    return result;
  }

  public AdditiveIntegerGroup zero() {
    return GroupParameters.ZERO();
  }

  public AdditiveIntegerGroup one() {
    return GroupParameters.ONE();
  }

  public boolean isZero() {
    return this.equals(GroupParameters.ZERO());
  }

  public boolean isOne() {
    return this.equals(GroupParameters.ONE());
  }

  public AdditiveIntegerGroup random(final Long seed, final byte[] secureSeed) {
    if (secureSeed != null && secureSeed.length > 0) {
      return new AdditiveIntegerGroup(new SecureRandom(secureSeed).nextLong(), GroupParameters);
    } else if (seed != null) {
      return new AdditiveIntegerGroup(new Random(seed).nextLong(), GroupParameters);
    } else {
      return new AdditiveIntegerGroup(new Random().nextLong(), GroupParameters);
    }
  }

  public AdditiveIntegerGroup negate() {
    if (GroupParameters.modulus() == null) {
      return new AdditiveIntegerGroup(this.number.negate(), GroupParameters);
    }
    return new AdditiveIntegerGroup(GroupParameters.modulus().subtract(number), GroupParameters);
  }

  public AdditiveIntegerGroup dbl() {
    return new AdditiveIntegerGroup(this.number.add(number), GroupParameters);
  }

  public ArrayList<Integer> fixedBaseWindowTable() {
    return fixedBaseWindowTable;
  }

  public String toString() {
    return this.number.toString();
  }

  public boolean equals(final AdditiveIntegerGroup other) {
    if (other == null) {
      return false;
    }
    return this.number.equals(other.number);
  }
}
