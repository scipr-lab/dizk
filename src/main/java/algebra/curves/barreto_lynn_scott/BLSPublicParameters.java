package algebra.curves.barreto_lynn_scott;

import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq12;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq2;
import algebra.curves.barreto_lynn_scott.BLSFields.BLSFq6;
import java.math.BigInteger;

public abstract class BLSPublicParameters<
    BLSFqT extends BLSFq<BLSFqT>,
    BLSFq2T extends BLSFq2<BLSFqT, BLSFq2T>,
    BLSFq6T extends BLSFq6<BLSFqT, BLSFq2T, BLSFq6T>,
    BLSFq12T extends BLSFq12<BLSFqT, BLSFq2T, BLSFq6T, BLSFq12T>> {
  protected BLSFqT coefficientB;
  protected BLSFq2T twist;
  protected BLSFq2T twistCoefficientB;
  protected BLSFqT bC0MulTwist;
  protected BLSFqT bC1MulTwist;
  protected BLSFq2T qXMulTwist;
  protected BLSFq2T qYMulTwist;

  protected BigInteger ateLoopCount;
  protected boolean isAteLoopCountNegative;
  protected BigInteger finalExponent;
  protected BigInteger finalExponentZ;
  protected boolean isFinalExponentZNegative;

  protected BLSFq12T blsFq12Factory;

  BLSFqT coefficientB() {
    return coefficientB;
  }

  BLSFq2T twist() {
    return twist;
  }

  BLSFq2T twistCoefficientB() {
    return twistCoefficientB;
  }

  BLSFqT bC0MulTwist() {
    return bC0MulTwist;
  }

  BLSFqT bC1MulTwist() {
    return bC1MulTwist;
  }

  BLSFq2T qXMulTwist() {
    return qXMulTwist;
  }

  BLSFq2T qYMulTwist() {
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

  BLSFq12T blsFq12Factory() {
    return blsFq12Factory;
  }
}
