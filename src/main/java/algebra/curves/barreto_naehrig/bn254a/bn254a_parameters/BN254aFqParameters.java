/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254a.bn254a_parameters;

import algebra.fields.Fp;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNFqParameters;

import java.io.Serializable;
import java.math.BigInteger;

public class BN254aFqParameters extends AbstractBNFqParameters implements Serializable {
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

    public BN254aFqParameters() {
        this.modulus = new BigInteger("21888242871839275222246405745257275088696311157297823662689037894645226208583");
        this.root = new BigInteger("21888242871839275222246405745257275088696311157297823662689037894645226208582");
        this.multiplicativeGenerator = new Fp("3", this);
        this.numBits = 254;

        this.euler = new BigInteger("10944121435919637611123202872628637544348155578648911831344518947322613104291");
        this.s = 1;
        this.t = new BigInteger("10944121435919637611123202872628637544348155578648911831344518947322613104291");
        this.tMinus1Over2 = new BigInteger("5472060717959818805561601436314318772174077789324455915672259473661306552145");
        this.nqr = new Fp("3", this);
        this.nqrTot = new Fp("21888242871839275222246405745257275088696311157297823662689037894645226208582", this);

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
