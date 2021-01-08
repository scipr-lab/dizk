/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters;

import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSFq2Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq;
import algebra.fields.Fp;
import algebra.fields.Fp2;
import java.io.Serializable;
import java.math.BigInteger;


public class BLS12_377Fq2Parameters extends AbstractBLSFq2Parameters implements Serializable {
  public BLS12_377FqParameters FqParameters;
  public BigInteger euler;
  public long s;
  public BigInteger t;
  public BigInteger tMinus1Over2;
  public Fp nonresidue;
  public Fp2 nqr;
  public Fp2 nqrTot;
  public Fp[] FrobeniusCoefficientsC1;

  public Fp2 ZERO;
  public Fp2 ONE;

  public BLS12_377Fq2Parameters() {
    this.FqParameters = new BLS12_377FqParameters();
    this.euler =
        new BigInteger(
            "239547588008311421220994022608339370399626158265550411218223901127035046843189118723920525909718935985594116157406550130918127817069793474323196511433944");
    this.s = 4;
    this.t =
        new BigInteger(
            "29943448501038927652624252826042421299953269783193801402277987640879380855398639840490065738714866998199264519675818766364765977133724184290399563929243");
    this.tMinus1Over2 =
        new BigInteger(
            "14971724250519463826312126413021210649976634891596900701138993820439690427699319920245032869357433499099632259837909383182382988566862092145199781964621");
    this.nonresidue =
        new BLS12_377Fq(
                "21888242871839275222246405745257275088696311157297823662689037894645226208582")
            .element();
    this.nqr = new Fp2(new Fp("2", FqParameters), new Fp("1", FqParameters), this);
    this.nqrTot =
        new Fp2(
            new Fp(
                "5033503716262624267312492558379982687175200734934877598599011485707452665730",
                FqParameters),
            new Fp(
                "314498342015008975724433667930697407966947188435857772134235984660852259084",
                FqParameters),
            this);
    this.FrobeniusCoefficientsC1 = new Fp[2];
    this.FrobeniusCoefficientsC1[0] = new Fp("1", FqParameters);
    this.FrobeniusCoefficientsC1[1] =
        new Fp(
            "21888242871839275222246405745257275088696311157297823662689037894645226208582",
            FqParameters);

    this.ZERO = new Fp2(BigInteger.ZERO, BigInteger.ZERO, this);
    this.ONE = new Fp2(BigInteger.ONE, BigInteger.ZERO, this);
  }

  public BLS12_377FqParameters FpParameters() {
    return FqParameters;
  }

  public Fp2 ZERO() {
    return ZERO;
  }

  public Fp2 ONE() {
    return ONE;
  }

  public BigInteger euler() {
    return euler;
  }

  public long s() {
    return s;
  }

  public BigInteger t() {
    return t;
  }

  public BigInteger tMinus1Over2() {
    return tMinus1Over2;
  }

  public Fp nonresidue() {
    return nonresidue;
  }

  public Fp2 nqr() {
    return nqr;
  }

  public Fp2 nqrTot() {
    return nqrTot;
  }

  public Fp[] FrobeniusMapCoefficientsC1() {
    return FrobeniusCoefficientsC1;
  }
}