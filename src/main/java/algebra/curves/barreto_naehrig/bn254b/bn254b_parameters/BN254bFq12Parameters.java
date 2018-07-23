/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254b.bn254b_parameters;

import algebra.fields.Fp;
import algebra.fields.Fp12_2Over3Over2;
import algebra.fields.Fp2;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNFq12Parameters;

import java.io.Serializable;

public class BN254bFq12Parameters extends AbstractBNFq12Parameters implements Serializable {
    public BN254bFqParameters FqParameters;
    public BN254bFq2Parameters Fq2Parameters;
    public BN254bFq6Parameters Fq6Parameters;

    public Fp12_2Over3Over2 ZERO;
    public Fp12_2Over3Over2 ONE;

    public Fp2 nonresidue;

    public Fp2[] FrobeniusCoefficientsC1;

    public BN254bFq12Parameters() {
        this.FqParameters = new BN254bFqParameters();
        this.Fq2Parameters = new BN254bFq2Parameters();
        this.Fq6Parameters = new BN254bFq6Parameters();

        this.ZERO = new Fp12_2Over3Over2(Fq6Parameters.ZERO(), Fq6Parameters.ZERO(), this);
        this.ONE = new Fp12_2Over3Over2(Fq6Parameters.ONE(), Fq6Parameters.ZERO(), this);

        this.nonresidue = new Fp2(3, 1, Fq2Parameters);

        this.FrobeniusCoefficientsC1 = new Fp2[12];
        this.FrobeniusCoefficientsC1[0] = new Fp2(FqParameters.ONE(), FqParameters.ZERO(), Fq2Parameters);
        this.FrobeniusCoefficientsC1[1] = new Fp2(
                new Fp("13706659523669426446600851453713573680963642381392467499145752523652919858862", FqParameters),
                new Fp("4515056027473182360097694130858956802944681130858260603989466526277158912215", FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[2] = new Fp2(
                new Fp("17855808334804902848369101855237350698508645978270700521827123261939669532672", FqParameters),
                new Fp("0", FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[3] = new Fp2(
                new Fp("13407551336177838341547697331611214663151044798963617862093261068776950984136", FqParameters),
                new Fp("7112905773970595014787737132689208625336092960980057219647915627574967553259", FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[4] = new Fp2(
                new Fp("17855808334804902848369101855237350698508645978270700521827123261939669532671", FqParameters),
                new Fp("0", FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[5] = new Fp2(
                new Fp("17556700147313314745207769709667896755967142997433669700958333080980541003547", FqParameters),
                new Fp("2597849746497412654690043001830251822391411830121796615658449101297808641044", FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[6] = new Fp2(
                new Fp("17855808334804902850260923831770255773779740579862519338010824535856509878272", FqParameters),
                new Fp("0", FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[7] = new Fp2(
                new Fp("4149148811135476403660072378056682092816098198470051838865072012203590019411", FqParameters),
                new Fp("13340752307331720490163229700911298970835059449004258734021358009579350966058", FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[8] = new Fp2(
                new Fp("1891821976532905075271094601591818816183701273916840345601", FqParameters),
                new Fp("0", FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[9] = new Fp2(
                new Fp("4448256998627064508713226500159041110628695780898901475917563467079558894137", FqParameters),
                new Fp("10742902560834307835473186699081047148443647618882462118362908908281542325014", FqParameters),
                Fq2Parameters);
        this.FrobeniusCoefficientsC1[10] = new Fp2(
                new Fp("1891821976532905075271094601591818816183701273916840345602", FqParameters),
                new Fp("0", FqParameters), Fq2Parameters);
        this.FrobeniusCoefficientsC1[11] = new Fp2(
                new Fp("299108187491588105053154122102359017812597582428849637052491454875968874726", FqParameters),
                new Fp("15257958588307490195570880829940003951388328749740722722352375434558701237229", FqParameters),
                Fq2Parameters);
    }

    public BN254bFqParameters FpParameters() {
        return FqParameters;
    }

    public BN254bFq2Parameters Fp2Parameters() {
        return Fq2Parameters;
    }

    public BN254bFq6Parameters Fp6Parameters() {
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
