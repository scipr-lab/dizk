/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254b.bn254b_parameters;

import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNFq6Parameters;
import algebra.fields.Fp;
import algebra.fields.Fp2;
import algebra.fields.Fp6_3Over2;
import java.io.Serializable;

public class BN254bFq6Parameters extends AbstractBNFq6Parameters implements Serializable {
  public BN254bFq2Parameters Fq2Parameters;

  public Fp6_3Over2 ZERO;
  public Fp6_3Over2 ONE;

  public Fp2 nonresidue;
  public Fp2[] FrobeniusCoefficientsC1;
  public Fp2[] FrobeniusCoefficientsC2;

  public BN254bFq6Parameters() {
    final BN254bFqParameters FqParameters = new BN254bFqParameters();
    this.Fq2Parameters = new BN254bFq2Parameters();

    this.ZERO =
        new Fp6_3Over2(Fq2Parameters.ZERO(), Fq2Parameters.ZERO(), Fq2Parameters.ZERO(), this);
    this.ONE =
        new Fp6_3Over2(Fq2Parameters.ONE(), Fq2Parameters.ZERO(), Fq2Parameters.ZERO(), this);
    this.nonresidue = new Fp2(3, 1, Fq2Parameters);

    this.FrobeniusCoefficientsC1 = new Fp2[6];
    this.FrobeniusCoefficientsC1[0] =
        new Fp2(FqParameters.ONE(), FqParameters.ZERO(), Fq2Parameters);
    this.FrobeniusCoefficientsC1[1] =
        new Fp2(
            new Fp(
                "4219883591014163367905881574021031215493473653793212010160902933860880418057",
                FqParameters),
            new Fp(
                "2651212873738315799336967630323287311248564116383877570845186714307597874614",
                FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[2] =
        new Fp2(
            new Fp(
                "17855808334804902848369101855237350698508645978270700521827123261939669532671",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[3] =
        new Fp2(
            new Fp(
                "17156602702563040441956597667703934649889437457556816425002141516445646920759",
                FqParameters),
            new Fp(
                "6164285361305781227881271892385338132215448056277898957819151303653762824528",
                FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[4] =
        new Fp2(
            new Fp("1891821976532905075271094601591818816183701273916840345601", FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[5] =
        new Fp2(
            new Fp(
                "14335130376032601890659368421815545682176570048375010240858604621406492417730",
                FqParameters),
            new Fp(
                "9040310099760805823042684309061630330315728407200742809346486517895149179131",
                FqParameters),
            Fq2Parameters);

    this.FrobeniusCoefficientsC2 = new Fp2[6];
    this.FrobeniusCoefficientsC2[0] =
        new Fp2(FqParameters.ONE(), FqParameters.ZERO(), Fq2Parameters);
    this.FrobeniusCoefficientsC2[1] =
        new Fp2(
            new Fp(
                "10231956086243163562612530075058456306773517112480581674518308118992977990175",
                FqParameters),
            new Fp(
                "6578405525887142826214581301007376705073896645664085669542308616447040522598",
                FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC2[2] =
        new Fp2(
            new Fp("1891821976532905075271094601591818816183701273916840345601", FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC2[3] =
        new Fp2(
            new Fp(
                "14650171428397641159720347347937157040080688469958260360687776328684777196865",
                FqParameters),
            new Fp(
                "1089264929845561088487336934171220411574250547746261451628367283825939014537",
                FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC2[4] =
        new Fp2(
            new Fp(
                "17855808334804902848369101855237350698508645978270700521827123261939669532671",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC2[5] =
        new Fp2(
            new Fp(
                "10829489154969000978188970240544898200705275577286196640815564624035264569506",
                FqParameters),
            new Fp(
                "10188137879072198935559005596591658657131593386452172216840148635583530341138",
                FqParameters),
            Fq2Parameters);
  }

  public BN254bFq2Parameters Fp2Parameters() {
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
