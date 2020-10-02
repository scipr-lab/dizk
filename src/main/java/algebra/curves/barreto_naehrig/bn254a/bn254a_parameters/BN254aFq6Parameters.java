/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254a.bn254a_parameters;

import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNFq6Parameters;
import algebra.fields.Fp;
import algebra.fields.Fp2;
import algebra.fields.Fp6_3Over2;
import java.io.Serializable;

public class BN254aFq6Parameters extends AbstractBNFq6Parameters implements Serializable {
  public BN254aFq2Parameters Fq2Parameters;

  public Fp6_3Over2 ZERO;
  public Fp6_3Over2 ONE;

  public Fp2 nonresidue;
  public Fp2[] FrobeniusCoefficientsC1;
  public Fp2[] FrobeniusCoefficientsC2;

  public BN254aFq6Parameters() {
    final BN254aFqParameters FqParameters = new BN254aFqParameters();
    this.Fq2Parameters = new BN254aFq2Parameters();

    this.ZERO =
        new Fp6_3Over2(Fq2Parameters.ZERO(), Fq2Parameters.ZERO(), Fq2Parameters.ZERO(), this);
    this.ONE =
        new Fp6_3Over2(Fq2Parameters.ONE(), Fq2Parameters.ZERO(), Fq2Parameters.ZERO(), this);
    this.nonresidue = new Fp2(9, 1, Fq2Parameters);

    this.FrobeniusCoefficientsC1 = new Fp2[6];
    this.FrobeniusCoefficientsC1[0] =
        new Fp2(FqParameters.ONE(), FqParameters.ZERO(), Fq2Parameters);
    this.FrobeniusCoefficientsC1[1] =
        new Fp2(
            new Fp(
                "21575463638280843010398324269430826099269044274347216827212613867836435027261",
                FqParameters),
            new Fp(
                "10307601595873709700152284273816112264069230130616436755625194854815875713954",
                FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[2] =
        new Fp2(
            new Fp(
                "21888242871839275220042445260109153167277707414472061641714758635765020556616",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[3] =
        new Fp2(
            new Fp(
                "3772000881919853776433695186713858239009073593817195771773381919316419345261",
                FqParameters),
            new Fp(
                "2236595495967245188281701248203181795121068902605861227855261137820944008926",
                FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[4] =
        new Fp2(
            new Fp("2203960485148121921418603742825762020974279258880205651966", FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC1[5] =
        new Fp2(
            new Fp(
                "18429021223477853657660792034369865839114504446431234726392080002137598044644",
                FqParameters),
            new Fp(
                "9344045779998320333812420223237981029506012124075525679208581902008406485703",
                FqParameters),
            Fq2Parameters);

    this.FrobeniusCoefficientsC2 = new Fp2[6];
    this.FrobeniusCoefficientsC2[0] =
        new Fp2(FqParameters.ONE(), FqParameters.ZERO(), Fq2Parameters);
    this.FrobeniusCoefficientsC2[1] =
        new Fp2(
            new Fp(
                "2581911344467009335267311115468803099551665605076196740867805258568234346338",
                FqParameters),
            new Fp(
                "19937756971775647987995932169929341994314640652964949448313374472400716661030",
                FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC2[2] =
        new Fp2(
            new Fp("2203960485148121921418603742825762020974279258880205651966", FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC2[3] =
        new Fp2(
            new Fp(
                "5324479202449903542726783395506214481928257762400643279780343368557297135718",
                FqParameters),
            new Fp(
                "16208900380737693084919495127334387981393726419856888799917914180988844123039",
                FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC2[4] =
        new Fp2(
            new Fp(
                "21888242871839275220042445260109153167277707414472061641714758635765020556616",
                FqParameters),
            new Fp("0", FqParameters),
            Fq2Parameters);
    this.FrobeniusCoefficientsC2[5] =
        new Fp2(
            new Fp(
                "13981852324922362344252311234282257507216387789820983642040889267519694726527",
                FqParameters),
            new Fp(
                "7629828391165209371577384193250820201684255241773809077146787135900891633097",
                FqParameters),
            Fq2Parameters);
  }

  public BN254aFq2Parameters Fp2Parameters() {
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
