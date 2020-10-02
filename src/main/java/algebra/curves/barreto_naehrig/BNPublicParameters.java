/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig;

import algebra.curves.barreto_naehrig.BNFields.BNFq;
import algebra.curves.barreto_naehrig.BNFields.BNFq12;
import algebra.curves.barreto_naehrig.BNFields.BNFq2;
import algebra.curves.barreto_naehrig.BNFields.BNFq6;
import java.math.BigInteger;

public abstract class BNPublicParameters<
    BNFqT extends BNFq<BNFqT>,
    BNFq2T extends BNFq2<BNFqT, BNFq2T>,
    BNFq6T extends BNFq6<BNFqT, BNFq2T, BNFq6T>,
    BNFq12T extends BNFq12<BNFqT, BNFq2T, BNFq6T, BNFq12T>> {
  protected BNFqT coefficientB;
  protected BNFq2T twist;
  protected BNFq2T twistCoefficientB;
  protected BNFqT bC0MulTwist;
  protected BNFqT bC1MulTwist;
  protected BNFq2T qXMulTwist;
  protected BNFq2T qYMulTwist;

  protected BigInteger ateLoopCount;
  protected boolean isAteLoopCountNegative;
  protected BigInteger finalExponent;
  protected BigInteger finalExponentZ;
  protected boolean isFinalExponentZNegative;

  protected BNFq12T bnFq12Factory;

  BNFqT coefficientB() {
    return coefficientB;
  }

  BNFq2T twist() {
    return twist;
  }

  BNFq2T twistCoefficientB() {
    return twistCoefficientB;
  }

  BNFqT bC0MulTwist() {
    return bC0MulTwist;
  }

  BNFqT bC1MulTwist() {
    return bC1MulTwist;
  }

  BNFq2T qXMulTwist() {
    return qXMulTwist;
  }

  BNFq2T qYMulTwist() {
    return qYMulTwist;
  }

  BigInteger ateLoopCount() {
    return ateLoopCount;
  }

  boolean isAteLoopCountNegative() {
    return isAteLoopCountNegative;
  }

  public BigInteger finalExponent() {
    return finalExponent;
  }

  public BigInteger finalExponentZ() {
    return finalExponentZ;
  }

  boolean isFinalExponentZNegative() {
    return isFinalExponentZNegative;
  }

  BNFq12T bnFq12Factory() {
    return bnFq12Factory;
  }
}
