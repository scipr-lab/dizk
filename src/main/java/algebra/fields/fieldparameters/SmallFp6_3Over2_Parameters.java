/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields.fieldparameters;

import algebra.fields.Fp2;
import algebra.fields.Fp6_3Over2;
import algebra.fields.abstractfieldparameters.AbstractFp6_3Over2_Parameters;
import java.io.Serializable;

public class SmallFp6_3Over2_Parameters extends AbstractFp6_3Over2_Parameters
    implements Serializable {
  private Fp6_3Over2 ZERO;
  private Fp6_3Over2 ONE;

  private Fp2 nonresidue;
  private Fp2[] FrobeniusCoefficientsC1;
  private Fp2[] FrobeniusCoefficientsC2;

  private SmallFpParameters FpParameters;
  private SmallFp2Parameters Fp2Parameters;

  public SmallFp6_3Over2_Parameters() {
    FpParameters = new SmallFpParameters();
    Fp2Parameters = new SmallFp2Parameters();
  }

  public SmallFp2Parameters Fp2Parameters() {
    return Fp2Parameters;
  }

  public Fp6_3Over2 ZERO() {
    if (ZERO == null) {
      ZERO = new Fp6_3Over2(Fp2Parameters.ZERO(), Fp2Parameters.ZERO(), Fp2Parameters.ZERO(), this);
    }

    return ZERO;
  }

  public Fp6_3Over2 ONE() {
    if (ONE == null) {
      ONE = new Fp6_3Over2(Fp2Parameters.ONE(), Fp2Parameters.ZERO(), Fp2Parameters.ZERO(), this);
    }

    return ONE;
  }

  public Fp2 nonresidue() {
    if (nonresidue == null) {
      nonresidue = new Fp2(6, 5, Fp2Parameters);
    }

    return nonresidue;
  }

  public Fp2[] FrobeniusMapCoefficientsC1() {
    if (FrobeniusCoefficientsC1 == null) {
      FrobeniusCoefficientsC1 = new Fp2[6];
      FrobeniusCoefficientsC1[0] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[1] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[2] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[3] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[4] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[5] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
    }

    return FrobeniusCoefficientsC1;
  }

  public Fp2[] FrobeniusMapCoefficientsC2() {
    if (FrobeniusCoefficientsC2 == null) {
      FrobeniusCoefficientsC2 = new Fp2[6];
      FrobeniusCoefficientsC2[0] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC2[1] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC2[2] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC2[3] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC2[4] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC2[5] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
    }

    return FrobeniusCoefficientsC2;
  }
}
