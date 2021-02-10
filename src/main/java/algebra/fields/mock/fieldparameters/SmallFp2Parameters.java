/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields.mock.fieldparameters;

import algebra.fields.Fp;
import algebra.fields.Fp2;
import algebra.fields.abstractfieldparameters.AbstractFp2Parameters;
import java.io.Serializable;
import java.math.BigInteger;

public class SmallFp2Parameters extends AbstractFp2Parameters implements Serializable {
  private Fp2 ZERO;
  private Fp2 ONE;

  private BigInteger euler;
  private BigInteger t;
  private BigInteger tMinus1Over2;
  private Fp nonresidue;
  private Fp2 nqr;
  private Fp2 nqrTot;
  private Fp[] FrobeniusCoefficientsC1;

  private SmallFpParameters FpParameters;

  public SmallFp2Parameters() {
    FpParameters = new SmallFpParameters();
  }

  public SmallFpParameters FpParameters() {
    return FpParameters;
  }

  public Fp2 ZERO() {
    if (ZERO == null) {
      ZERO = new Fp2(BigInteger.ZERO, BigInteger.ZERO, this);
    }

    return ZERO;
  }

  public Fp2 ONE() {
    if (ONE == null) {
      ONE = new Fp2(BigInteger.ONE, BigInteger.ZERO, this);
    }

    return ONE;
  }

  public BigInteger euler() {
    if (euler == null) {
      euler = new BigInteger("10");
    }

    return euler;
  }

  public long s() {
    return 4;
  }

  public BigInteger t() {
    if (t == null) {
      t = new BigInteger("10");
    }

    return t;
  }

  public BigInteger tMinus1Over2() {
    if (tMinus1Over2 == null) {
      tMinus1Over2 = new BigInteger("10");
    }

    return tMinus1Over2;
  }

  public Fp nonresidue() {
    if (nonresidue == null) {
      nonresidue = new Fp("7", FpParameters);
    }

    return nonresidue;
  }

  public Fp2 nqr() {
    if (nqr == null) {
      nqr = new Fp2(new Fp("2", FpParameters), new Fp("1", FpParameters), this);
    }

    return nqr;
  }

  public Fp2 nqrTot() {
    if (nqrTot == null) {
      nqrTot =
          new Fp2(
              new Fp(
                  "5033503716262624267312492558379982687175200734934877598599011485707452665730",
                  FpParameters),
              new Fp(
                  "314498342015008975724433667930697407966947188435857772134235984660852259084",
                  FpParameters),
              this);
    }

    return nqrTot;
  }

  public Fp[] FrobeniusMapCoefficientsC1() {
    if (FrobeniusCoefficientsC1 == null) {
      FrobeniusCoefficientsC1 = new Fp[2];
      FrobeniusCoefficientsC1[0] = new Fp("1", FpParameters);
      FrobeniusCoefficientsC1[1] =
          new Fp(
              "21888242871839275222246405745257275088696311157297823662689037894645226208582",
              FpParameters);
    }

    return FrobeniusCoefficientsC1;
  }
}
