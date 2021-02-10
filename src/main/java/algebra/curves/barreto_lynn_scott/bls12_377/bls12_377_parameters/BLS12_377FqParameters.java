package algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters;

import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSFqParameters;
import algebra.fields.Fp;
import java.io.Serializable;
import java.math.BigInteger;

public class BLS12_377FqParameters extends AbstractBLSFqParameters implements Serializable {
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

  public BLS12_377FqParameters() {
    this.modulus =
        new BigInteger(
            "258664426012969094010652733694893533536393512754914660539884262666720468348340822774968888139573360124440321458177");
    this.root =
        new BigInteger(
            "32863578547254505029601261939868325669770508939375122462904745766352256812585773382134936404344547323199885654433");
    this.multiplicativeGenerator = new Fp("15", this);
    this.numBits = 377;

    this.euler =
        new BigInteger(
            "129332213006484547005326366847446766768196756377457330269942131333360234174170411387484444069786680062220160729088");
    this.s = 46;
    this.t =
        new BigInteger(
            "3675842578061421676390135839012792950148785745837396071634149488243117337281387659330802195819009059");
    this.tMinus1Over2 =
        new BigInteger(
            "1837921289030710838195067919506396475074392872918698035817074744121558668640693829665401097909504529");
    this.nqr = new Fp("5", this);
    this.nqrTot =
        new Fp(
            "33774956008227656219775876656288133547078610493828613777258829345740556592044969439504850374928261397247202212840",
            this);

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
