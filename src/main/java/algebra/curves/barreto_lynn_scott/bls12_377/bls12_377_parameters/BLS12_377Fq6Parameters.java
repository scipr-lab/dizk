package algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters;

import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSFq6Parameters;
import algebra.fields.Fp;
import algebra.fields.Fp2;
import algebra.fields.Fp6_3Over2;
import java.io.Serializable;

// Checked

/** Parameters for Fq6 = (Fq2)^3 */
public class BLS12_377Fq6Parameters extends AbstractBLSFq6Parameters implements Serializable {
  public BLS12_377Fq2Parameters Fq2Parameters;

  public Fp6_3Over2 ZERO;
  public Fp6_3Over2 ONE;

  public Fp2 nonresidue;
  public Fp2[] FrobeniusCoefficientsC1;
  public Fp2[] FrobeniusCoefficientsC2;

  public BLS12_377Fq6Parameters() {
    final BLS12_377FqParameters FqParameters = new BLS12_377FqParameters();
    this.Fq2Parameters = new BLS12_377Fq2Parameters();

    this.ZERO =
        new Fp6_3Over2(Fq2Parameters.ZERO(), Fq2Parameters.ZERO(), Fq2Parameters.ZERO(), this);
    this.ONE =
        new Fp6_3Over2(Fq2Parameters.ONE(), Fq2Parameters.ZERO(), Fq2Parameters.ZERO(), this);
    this.nonresidue = new Fp2(new Fp("0", FqParameters), new Fp("1", FqParameters), Fq2Parameters);

    this.FrobeniusCoefficientsC1 = new Fp2[6];
    this.FrobeniusCoefficientsC1[0] =
        new Fp2(new Fp("1", FqParameters), new Fp("0", FqParameters), Fq2Parameters);
    this.FrobeniusCoefficientsC1[1] =
        new Fp2(
            new Fp(
                "80949648264912719408558363140637477264845294720710499478137287262712535938301461879813459410946",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[2] =
        new Fp2(
            new Fp(
                "80949648264912719408558363140637477264845294720710499478137287262712535938301461879813459410945",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[3] =
        new Fp2(
            new Fp(
                "258664426012969094010652733694893533536393512754914660539884262666720468348340822774968888139573360124440321458176",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[4] =
        new Fp2(
            new Fp("258664426012969093929703085429980814127835149614277183275038967946009968870203535512256352201271898244626862047231", FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[5] =
        new Fp2(
            new Fp(
                "258664426012969093929703085429980814127835149614277183275038967946009968870203535512256352201271898244626862047232",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);

    this.FrobeniusCoefficientsC2 = new Fp2[6];
    this.FrobeniusCoefficientsC2[0] =
        new Fp2(new Fp("1", FqParameters), new Fp("0", FqParameters), Fq2Parameters);
    this.FrobeniusCoefficientsC2[1] =
        new Fp2(
            new Fp(
                "80949648264912719408558363140637477264845294720710499478137287262712535938301461879813459410945",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC2[2] =
        new Fp2(
            new Fp("258664426012969093929703085429980814127835149614277183275038967946009968870203535512256352201271898244626862047231", FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC2[3] =
        new Fp2(new Fp("1", FqParameters), new Fp("0", FqParameters), Fq2Parameters);
    this.FrobeniusCoefficientsC2[4] =
        new Fp2(
            new Fp(
                "80949648264912719408558363140637477264845294720710499478137287262712535938301461879813459410945",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC2[5] =
        new Fp2(
            new Fp(
                "258664426012969093929703085429980814127835149614277183275038967946009968870203535512256352201271898244626862047231",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
  }

  public BLS12_377Fq2Parameters Fp2Parameters() {
    return Fq2Parameters;
  }

  public Fp6_3Over2 ZERO() {
    return ZERO;
  }

  public Fp6_3Over2 ONE() {
    return ONE;
  }

  public Fp2 nonresidue() {
    return nonresidue;
  }

  public Fp2[] FrobeniusMapCoefficientsC1() {
    return FrobeniusCoefficientsC1;
  }

  public Fp2[] FrobeniusMapCoefficientsC2() {
    return FrobeniusCoefficientsC2;
  }
}
