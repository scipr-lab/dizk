package algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters;

import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSFq2Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq;
import algebra.fields.Fp;
import algebra.fields.Fp2;
import java.io.Serializable;
import java.math.BigInteger;

// Checked

public class BLS12_377Fq2Parameters extends AbstractBLSFq2Parameters implements Serializable {
  public BLS12_377FqParameters FqParameters;
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

  public BLS12_377Fq2Parameters() {
    this.FqParameters = new BLS12_377FqParameters();
    this.euler =
        new BigInteger(
            "33453642642309381258089625946249069288005760010886479253070957453297957116339370141113413635838485065209570299254148838549585056123015878375022724998041828785227090063466658233059433323033772513321990316560167027213559780081664");
    this.s = 47;
    this.t =
        new BigInteger(
            "475404855284145089315325463221726483993816145966867441829193658311651761271425728823393990805904040047516478740222806302278755994777496288961383541476974255391881599499962735436887347234371823579436839914935817251");
    this.tMinus1Over2 =
        new BigInteger(
            "237702427642072544657662731610863241996908072983433720914596829155825880635712864411696995402952020023758239370111403151139377997388748144480691770738487127695940799749981367718443673617185911789718419957467908625");
    this.nonresidue =
        new BLS12_377Fq(
                "258664426012969094010652733694893533536393512754914660539884262666720468348340822774968888139573360124440321458172")
            .element();
    this.nqr = new Fp2(new Fp("0", FqParameters), new Fp("1", FqParameters), this);
    this.nqrTot =
        new Fp2(
            new Fp("0", FqParameters),
            new Fp(
                "257286236321774568987262729980034669694531728092793737444525294935421142460394028155736019924956637466133519652786",
                FqParameters),
            this);
    this.FrobeniusCoefficientsC1 = new Fp[2];
    this.FrobeniusCoefficientsC1[0] = new Fp("1", FqParameters);
    this.FrobeniusCoefficientsC1[1] =
        new Fp(
            "258664426012969094010652733694893533536393512754914660539884262666720468348340822774968888139573360124440321458176",
            FqParameters);

    this.ZERO = new Fp2(BigInteger.ZERO, BigInteger.ZERO, this);
    this.ONE = new Fp2(BigInteger.ONE, BigInteger.ZERO, this);
  }

  public BLS12_377FqParameters FpParameters() {
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