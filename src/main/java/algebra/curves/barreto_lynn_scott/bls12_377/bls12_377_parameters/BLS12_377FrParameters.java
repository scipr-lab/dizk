package algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters;

import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSFrParameters;
import algebra.fields.Fp;
import java.io.Serializable;
import java.math.BigInteger;

// Checked

public class BLS12_377FrParameters extends AbstractBLSFrParameters implements Serializable {
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

  public BLS12_377FrParameters() {
    this.modulus =
        new BigInteger(
            "8444461749428370424248824938781546531375899335154063827935233455917409239041");
    this.root =
        new BigInteger(
            "8065159656716812877374967518403273466521432693661810619979959746626482506078");
    this.multiplicativeGenerator = new Fp("22", this);
    this.numBits = 253;

    this.euler =
        new BigInteger(
            "4222230874714185212124412469390773265687949667577031913967616727958704619520");
    this.s = 47;
    this.t = new BigInteger("60001509534603559531609739528203892656505753216962260608619555");
    this.tMinus1Over2 =
        new BigInteger("30000754767301779765804869764101946328252876608481130304309777");
    this.nqr = new Fp("11", this);
    this.nqrTot =
        new Fp(
            "6924886788847882060123066508223519077232160750698452411071850219367055984476", this);

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
