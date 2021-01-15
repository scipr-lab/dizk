/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields.mock.fieldparameters;

import algebra.fields.Fp12_2Over3Over2;
import algebra.fields.Fp2;
import algebra.fields.abstractfieldparameters.AbstractFp12_2Over3Over2_Parameters;
import java.io.Serializable;

public class SmallFp12_2Over3Over2_Parameters extends AbstractFp12_2Over3Over2_Parameters
    implements Serializable {
  private Fp12_2Over3Over2 ZERO;
  private Fp12_2Over3Over2 ONE;

  private Fp2 nonresidue;
  private Fp2[] FrobeniusCoefficientsC1;

  private SmallFpParameters FpParameters;
  private SmallFp2Parameters Fp2Parameters;
  private SmallFp6_3Over2_Parameters Fp6Parameters;

  public SmallFp12_2Over3Over2_Parameters() {
    FpParameters = new SmallFpParameters();
    Fp2Parameters = new SmallFp2Parameters();
    Fp6Parameters = new SmallFp6_3Over2_Parameters();
  }

  public SmallFpParameters FpParameters() {
    return FpParameters;
  }

  public SmallFp2Parameters Fp2Parameters() {
    return Fp2Parameters;
  }

  public SmallFp6_3Over2_Parameters Fp6Parameters() {
    return Fp6Parameters;
  }

  public Fp12_2Over3Over2 ZERO() {
    if (ZERO == null) {
      ZERO = new Fp12_2Over3Over2(Fp6Parameters.ZERO(), Fp6Parameters.ZERO(), this);
    }

    return ZERO;
  }

  public Fp12_2Over3Over2 ONE() {
    if (ONE == null) {
      ONE = new Fp12_2Over3Over2(Fp6Parameters.ONE(), Fp6Parameters.ZERO(), this);
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
      FrobeniusCoefficientsC1 = new Fp2[12];
      FrobeniusCoefficientsC1[0] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[1] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[2] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[3] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[4] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[5] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[6] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[7] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[8] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[9] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[10] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
      FrobeniusCoefficientsC1[11] = new Fp2(FpParameters.ONE(), FpParameters.ZERO(), Fp2Parameters);
    }

    return FrobeniusCoefficientsC1;
  }
}
