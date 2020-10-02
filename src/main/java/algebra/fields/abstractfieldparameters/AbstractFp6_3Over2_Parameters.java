/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields.abstractfieldparameters;

import algebra.fields.Fp2;
import algebra.fields.Fp6_3Over2;

public abstract class AbstractFp6_3Over2_Parameters {

  public abstract AbstractFp2Parameters Fp2Parameters();

  public abstract Fp6_3Over2 ZERO();

  public abstract Fp6_3Over2 ONE();

  public abstract Fp2 nonresidue();

  public abstract Fp2[] FrobeniusMapCoefficientsC1();

  public abstract Fp2[] FrobeniusMapCoefficientsC2();
}
