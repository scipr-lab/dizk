/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters;

import algebra.curves.barreto_lynn_scott.abstract_bls_parameters.AbstractBLSFq12Parameters;
import algebra.fields.Fp;
import algebra.fields.Fp12_2Over3Over2;
import algebra.fields.Fp2;
import java.io.Serializable;

public class BLS12_377Fq12Parameters extends AbstractBLSFq12Parameters implements Serializable {
  public BLS12_377FqParameters FqParameters;
  public BLS12_377Fq2Parameters Fq2Parameters;
  public BLS12_377Fq6Parameters Fq6Parameters;

  public Fp12_2Over3Over2 ZERO;
  public Fp12_2Over3Over2 ONE;

  public Fp2 nonresidue;

  public Fp2[] FrobeniusCoefficientsC1;

  public BLS12_377Fq12Parameters() {
    this.FqParameters = new BLS12_377FqParameters();
    this.Fq2Parameters = new BLS12_377Fq2Parameters();
    this.Fq6Parameters = new BLS12_377Fq6Parameters();

    this.ZERO = new Fp12_2Over3Over2(Fq6Parameters.ZERO(), Fq6Parameters.ZERO(), this);
    this.ONE = new Fp12_2Over3Over2(Fq6Parameters.ONE(), Fq6Parameters.ZERO(), this);

    this.nonresidue = new Fp2(FqParameters.ZERO(), FqParameters.ONE(), Fq2Parameters);

    this.FrobeniusCoefficientsC1 = new Fp2[12];
    this.FrobeniusCoefficientsC1[0] =
        new Fp2(FqParameters.ONE(), FqParameters.ZERO(), Fq2Parameters);
    this.FrobeniusCoefficientsC1[1] =
        new Fp2(
            new Fp(
                "92949345220277864758624960506473182677953048909283248980960104381795901929519566951595905490535835115111760994353",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[2] =
        new Fp2(
            new Fp(
                "80949648264912719408558363140637477264845294720710499478137287262712535938301461879813459410946",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[3] =
        new Fp2(
            new Fp(
                "216465761340224619389371505802605247630151569547285782856803747159100223055385581585702401816380679166954762214499",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[4] =
        new Fp2(
            new Fp(
                "80949648264912719408558363140637477264845294720710499478137287262712535938301461879813459410945",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[5] =
        new Fp2(
            new Fp(
                "123516416119946754630746545296132064952198520638002533875843642777304321125866014634106496325844844051843001220146",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[6] =
        new Fp2(
            new Fp(
                "258664426012969094010652733694893533536393512754914660539884262666720468348340822774968888139573360124440321458176",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[7] =
        new Fp2(
            new Fp(
                "165715080792691229252027773188420350858440463845631411558924158284924566418821255823372982649037525009328560463824",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[8] =
        new Fp2(
            new Fp("258664426012969093929703085429980814127835149614277183275038967946009968870203535512256352201271898244626862047231", FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[9] =
        new Fp2(
            new Fp(
                "42198664672744474621281227892288285906241943207628877683080515507620245292955241189266486323192680957485559243678",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[10] =
        new Fp2(
            new Fp("258664426012969093929703085429980814127835149614277183275038967946009968870203535512256352201271898244626862047232", FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[11] =
        new Fp2(
            new Fp(
                "135148009893022339379906188398761468584194992116912126664040619889416147222474808140862391813728516072597320238031",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
  }

  public BLS12_377FqParameters FpParameters() {
    return FqParameters;
  }

  public BLS12_377Fq2Parameters Fp2Parameters() {
    return Fq2Parameters;
  }

  public BLS12_377Fq6Parameters Fp6Parameters() {
    return Fq6Parameters;
  }

  public Fp12_2Over3Over2 ZERO() {
    return ZERO;
  }

  public Fp12_2Over3Over2 ONE() {
    return ONE;
  }

  public Fp2 nonresidue() {
    return nonresidue;
  }

  public Fp2[] FrobeniusMapCoefficientsC1() {
    return FrobeniusCoefficientsC1;
  }
}
