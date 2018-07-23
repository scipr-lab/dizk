/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254a.bn254a_parameters;

import algebra.fields.Fp;
import algebra.fields.Fp12_2Over3Over2;
import algebra.fields.Fp2;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNFq12Parameters;

import java.io.Serializable;

public class BN254aFq12Parameters extends AbstractBNFq12Parameters implements Serializable {
    public BN254aFqParameters FqParameters;
    public BN254aFq2Parameters Fq2Parameters;
    public BN254aFq6Parameters Fq6Parameters;

    public Fp12_2Over3Over2 ZERO;
    public Fp12_2Over3Over2 ONE;

    public Fp2 nonresidue;

    public Fp2[] FrobeniusCoefficientsC1;

    public BN254aFq12Parameters() {
        this.FqParameters = new BN254aFqParameters();
        this.Fq2Parameters = new BN254aFq2Parameters();
        this.Fq6Parameters = new BN254aFq6Parameters();

        this.ZERO = new Fp12_2Over3Over2(Fq6Parameters.ZERO(), Fq6Parameters.ZERO(), this);
        this.ONE = new Fp12_2Over3Over2(Fq6Parameters.ONE(), Fq6Parameters.ZERO(), this);

        this.nonresidue = new Fp2(9, 1, Fq2Parameters);

        this.FrobeniusCoefficientsC1 = new Fp2[12];
        this.FrobeniusCoefficientsC1[0] =
                new Fp2(FqParameters.ONE(), FqParameters.ZERO(), Fq2Parameters);
        this.FrobeniusCoefficientsC1[1] = new Fp2(
                new Fp(
                        "8376118865763821496583973867626364092589906065868298776909617916018768340080",
                        FqParameters),
                new Fp(
                        "16469823323077808223889137241176536799009286646108169935659301613961712198316",
                        FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[2] = new Fp2(new Fp(
                "21888242871839275220042445260109153167277707414472061641714758635765020556617",
                FqParameters), new Fp("0", FqParameters), Fq2Parameters);
        this.FrobeniusCoefficientsC1[3] = new Fp2(
                new Fp(
                        "11697423496358154304825782922584725312912383441159505038794027105778954184319",
                        FqParameters),
                new Fp(
                        "303847389135065887422783454877609941456349188919719272345083954437860409601",
                        FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[4] = new Fp2(new Fp(
                "21888242871839275220042445260109153167277707414472061641714758635765020556616",
                FqParameters), new Fp("0", FqParameters), Fq2Parameters);
        this.FrobeniusCoefficientsC1[5] = new Fp2(
                new Fp(
                        "3321304630594332808241809054958361220322477375291206261884409189760185844239",
                        FqParameters),
                new Fp(
                        "5722266937896532885780051958958348231143373700109372999374820235121374419868",
                        FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[6] = new Fp2(new Fp(
                "21888242871839275222246405745257275088696311157297823662689037894645226208582",
                FqParameters), new Fp("0", FqParameters), Fq2Parameters);
        this.FrobeniusCoefficientsC1[7] = new Fp2(
                new Fp(
                        "13512124006075453725662431877630910996106405091429524885779419978626457868503",
                        FqParameters),
                new Fp(
                        "5418419548761466998357268504080738289687024511189653727029736280683514010267",
                        FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[8] = new Fp2(new Fp(
                "2203960485148121921418603742825762020974279258880205651966",
                FqParameters), new Fp("0", FqParameters), Fq2Parameters);
        this.FrobeniusCoefficientsC1[9] = new Fp2(
                new Fp(
                        "10190819375481120917420622822672549775783927716138318623895010788866272024264",
                        FqParameters),
                new Fp(
                        "21584395482704209334823622290379665147239961968378104390343953940207365798982",
                        FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[10] = new Fp2(new Fp(
                "2203960485148121921418603742825762020974279258880205651967",
                FqParameters), new Fp("0", FqParameters), Fq2Parameters);
        this.FrobeniusCoefficientsC1[11] = new Fp2(
                new Fp(
                        "18566938241244942414004596690298913868373833782006617400804628704885040364344",
                        FqParameters),
                new Fp(
                        "16165975933942742336466353786298926857552937457188450663314217659523851788715",
                        FqParameters),
                Fq2Parameters);
    }

    public BN254aFqParameters FpParameters() {
        return FqParameters;
    }

    public BN254aFq2Parameters Fp2Parameters() {
        return Fq2Parameters;
    }

    public BN254aFq6Parameters Fp6Parameters() {
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
