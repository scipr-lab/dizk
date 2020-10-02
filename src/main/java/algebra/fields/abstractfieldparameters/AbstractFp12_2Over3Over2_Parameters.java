/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields.abstractfieldparameters;

import algebra.fields.Fp12_2Over3Over2;
import algebra.fields.Fp2;

public abstract class AbstractFp12_2Over3Over2_Parameters {

  public abstract AbstractFpParameters FpParameters();

  public abstract AbstractFp2Parameters Fp2Parameters();

  // TODO: generalize to Fp6ParametersT
  public abstract AbstractFp6_3Over2_Parameters Fp6Parameters();

  public abstract Fp12_2Over3Over2 ZERO();

  public abstract Fp12_2Over3Over2 ONE();

  public abstract Fp2 nonresidue();

  public abstract Fp2[] FrobeniusMapCoefficientsC1();
}
