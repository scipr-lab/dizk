/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254b.bn254b_parameters;

import algebra.fields.Fp;
import algebra.fields.Fp2;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNFq2Parameters;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq;

import java.io.Serializable;
import java.math.BigInteger;

public class BN254bFq2Parameters extends AbstractBNFq2Parameters implements Serializable {
    public BN254bFqParameters FqParameters;
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

    public BN254bFq2Parameters() {
        this.FqParameters = new BN254bFqParameters();
        this.euler = new BigInteger("159414945644644118800073292129041626604626750566804270203022964393707780359252029928990713085903083893311782286933981288553742977296016413844614638731264");
        this.s = 51;
        this.t = new BigInteger("141588914499241375111247702393272641967160608237822812152571362881399413033324945424765240430089971018389667178402274750718119890976268861");
        this.tMinus1Over2 = new BigInteger("70794457249620687555623851196636320983580304118911406076285681440699706516662472712382620215044985509194833589201137375359059945488134430");
        this.nonresidue = new BN254bFq("17855808334804902850260923831770255773779740579862519338010824535856509878268").element();
        this.nqr = new Fp2(new Fp("2", FqParameters), new Fp("1", FqParameters), this);
        this.nqrTot = new Fp2(
                new Fp("0", FqParameters),
                new Fp("4528477034828452419408251792321847194159838177818062251130215191457192224016", FqParameters),
                this);
        this.FrobeniusCoefficientsC1 = new Fp[2];
        this.FrobeniusCoefficientsC1[0] = new Fp("1", FqParameters);
        this.FrobeniusCoefficientsC1[1] = new Fp("17855808334804902850260923831770255773779740579862519338010824535856509878272", FqParameters);

        this.ZERO = new Fp2(BigInteger.ZERO, BigInteger.ZERO, this);
        this.ONE = new Fp2(BigInteger.ONE, BigInteger.ZERO, this);
    }

    public BN254bFqParameters FpParameters() {
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
