/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fields.abstractfieldparameters;

import algebra.fields.Fp;
import java.math.BigInteger;

public abstract class AbstractFpParameters {
  public abstract BigInteger modulus();

  public abstract BigInteger root();

  public abstract Fp multiplicativeGenerator();

  public abstract long numBits();

  public abstract BigInteger euler();

  public abstract long s();

  public abstract BigInteger t();

  public abstract BigInteger tMinus1Over2();

  public abstract Fp nqr();

  public abstract Fp nqrTot();

  public abstract Fp ZERO();

  public abstract Fp ONE();
}
