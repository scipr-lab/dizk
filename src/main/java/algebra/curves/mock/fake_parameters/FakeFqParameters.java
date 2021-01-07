/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.mock.fake_parameters;

import algebra.fields.Fp;
import algebra.fields.abstractfieldparameters.AbstractFpParameters;
import java.io.Serializable;
import java.math.BigInteger;

public class FakeFqParameters extends AbstractFpParameters implements Serializable {

  private Fp ZERO;
  private Fp ONE;

  private BigInteger modulus;
  private BigInteger root;
  private Fp multiplicativeGenerator;

  /* The following variables are arbitrarily defined for testing purposes. */
  private BigInteger euler;
  private BigInteger t;
  private BigInteger tMinus1Over2;
  private Fp nqr;
  private Fp nqrTot;

  public Fp ZERO() {
    if (ZERO == null) {
      ZERO = new Fp(BigInteger.ZERO, this);
    }

    return ZERO;
  }

  public Fp ONE() {
    if (ONE == null) {
      ONE = new Fp(BigInteger.ONE, this);
    }

    return ONE;
  }

  public BigInteger modulus() {
    if (modulus == null) {
      modulus = new BigInteger("1532495540865888858358347027150309183618765510462668801");
    }

    return modulus;
  }

  public BigInteger root() {
    if (root == null) {
      root = new BigInteger("6");
    }

    return root;
  }

  public Fp multiplicativeGenerator() {
    if (multiplicativeGenerator == null) {
      multiplicativeGenerator = new Fp("6", this);
    }

    return multiplicativeGenerator;
  }

  public long numBits() {
    return modulus.bitLength();
  }

  public BigInteger euler() {
    if (euler == null) {
      euler = new BigInteger("5");
    }

    return euler;
  }

  public long s() {
    return numBits();
  }

  public BigInteger t() {
    if (t == null) {
      t = new BigInteger("5");
    }

    return t;
  }

  public BigInteger tMinus1Over2() {
    if (tMinus1Over2 == null) {
      tMinus1Over2 = new BigInteger("5");
    }

    return tMinus1Over2;
  }

  public Fp nqr() {
    if (nqr == null) {
      nqr = new Fp("6", this);
    }

    return nqr;
  }

  public Fp nqrTot() {
    if (nqrTot == null) {
      nqrTot = new Fp("6", this);
    }

    return nqrTot;
  }
}
