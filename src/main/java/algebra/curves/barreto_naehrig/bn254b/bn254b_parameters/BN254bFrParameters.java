/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254b.bn254b_parameters;

import algebra.fields.Fp;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNFrParameters;

import java.io.Serializable;
import java.math.BigInteger;

public class BN254bFrParameters extends AbstractBNFrParameters implements Serializable {
    public BigInteger modulus;
    public BigInteger root;
    public Fp multiplicativeGenerator;
    public long numBits;

    public BigInteger euler;
    public long s;
    public BigInteger t;
    public BigInteger tMinus1Over2;
    public Fp nqr;
    public Fp nqrTot;

    public Fp ZERO;
    public Fp ONE;

    public BN254bFrParameters() {
        this.modulus = new BigInteger("17855808334804902850260923831770255773646114952324966112694569107431857586177");
        this.root = new BigInteger("17729182186811642101367803457606981491707192560180443271869306861118266354030");
        this.multiplicativeGenerator = new Fp("7", this);
        this.numBits = 254;

        this.euler = new BigInteger("8927904167402451425130461915885127886823057476162483056347284553715928793088");
        this.s = 50;
        this.t = new BigInteger("15859143629275343245997586045144217136067967779244872617189949");
        this.tMinus1Over2 = new BigInteger("7929571814637671622998793022572108568033983889622436308594974");
        this.nqr = new Fp("5", this);
        this.nqrTot = new Fp("5268021713498987005639044372516656612533114292950057210251689723141480226975", this);

        this.ZERO = new Fp(BigInteger.ZERO, this);
        this.ONE = new Fp(BigInteger.ONE, this);
    }

    public BigInteger modulus() {
        return modulus;
    }

    public BigInteger root() {
        return root;
    }

    public Fp multiplicativeGenerator() {
        return multiplicativeGenerator;
    }

    public long numBits() {
        return numBits;
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

    public Fp nqr() {
        return nqr;
    }

    public Fp nqrTot() {
        return nqrTot;
    }

    public Fp ZERO() {
        return ZERO;
    }

    public Fp ONE() {
        return ONE;
    }
}
