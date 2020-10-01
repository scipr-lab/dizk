/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254b.bn254b_parameters;

import algebra.fields.Fp;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNFqParameters;

import java.io.Serializable;
import java.math.BigInteger;

// Reference: See parameters page 19 of DIZK paper.
public class BN254bFqParameters extends AbstractBNFqParameters implements Serializable {
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

    public BN254bFqParameters() {
        this.modulus = new BigInteger("17855808334804902850260923831770255773779740579862519338010824535856509878273");
        this.root = new BigInteger("8794480323307618088839840625900989315206881983614277345799820743772850434426");
        this.multiplicativeGenerator = new Fp("5", this);
        this.numBits = 254;

        this.euler = new BigInteger("8927904167402451425130461915885127886889870289931259669005412267928254939136");
        this.s = 50;
        this.t = new BigInteger("15859143629275343245997586045144217136186651177942614177505853");
        this.tMinus1Over2 = new BigInteger("7929571814637671622998793022572108568093325588971307088752926");
        this.nqr = new Fp("5", this);
        this.nqrTot = new Fp("8794480323307618088839840625900989315206881983614277345799820743772850434426", this);

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
