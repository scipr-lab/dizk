/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields.abstractfieldparameters;

import algebra.fields.Fp;
import algebra.fields.Fp2;
import java.math.BigInteger;

public abstract class AbstractFp2Parameters {

  public abstract AbstractFpParameters FpParameters();

  public abstract Fp2 ZERO();

  public abstract Fp2 ONE();

  public abstract BigInteger euler();

  public abstract long s();

  public abstract BigInteger t();

  public abstract BigInteger tMinus1Over2();

  public abstract Fp nonresidue();

  public abstract Fp2 nqr();

  public abstract Fp2 nqrTot();

  public abstract Fp[] FrobeniusMapCoefficientsC1();
}
