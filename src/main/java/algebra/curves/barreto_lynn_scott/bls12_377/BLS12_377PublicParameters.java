/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_lynn_scott.bls12_377;

import algebra.curves.barreto_lynn_scott.BLSPublicParameters;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq12;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fq6;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377Fq2Parameters;
import java.math.BigInteger;

public class BLS12_377PublicParameters
    extends BLSPublicParameters<BLS12_377Fq, BLS12_377Fq2, BLS12_377Fq6, BLS12_377Fq12> {

  public BLS12_377PublicParameters() {
    final BLS12_377Fq2Parameters Fq2Parameters = new BLS12_377Fq2Parameters();

    coefficientB = new BLS12_377Fq("3");
    twist = new BLS12_377Fq2(new BLS12_377Fq("9"), new BLS12_377Fq("1"));
    twistCoefficientB = twist.inverse().mul(coefficientB);
    bC0MulTwist = coefficientB.mul(new BLS12_377Fq(Fq2Parameters.nonresidue()));
    bC1MulTwist = coefficientB.mul(new BLS12_377Fq(Fq2Parameters.nonresidue()));
    qXMulTwist =
        new BLS12_377Fq2(
            new BLS12_377Fq(
                "21575463638280843010398324269430826099269044274347216827212613867836435027261"),
            new BLS12_377Fq(
                "10307601595873709700152284273816112264069230130616436755625194854815875713954"));
    qYMulTwist =
        new BLS12_377Fq2(
            new BLS12_377Fq(
                "2821565182194536844548159561693502659359617185244120367078079554186484126554"),
            new BLS12_377Fq(
                "3505843767911556378687030309984248845540243509899259641013678093033130930403"));

    // Pairing parameters
    ateLoopCount = new BigInteger("29793968203157093288");
    isAteLoopCountNegative = false;
    finalExponent =
        new BigInteger(
            "552484233613224096312617126783173147097382103762957654188882734314196910839907541213974502761540629817009608548654680343627701153829446747810907373256841551006201639677726139946029199968412598804882391702273019083653272047566316584365559776493027495458238373902875937659943504873220554161550525926302303331747463515644711876653177129578303191095900909191624817826566688241804408081892785725967931714097716709526092261278071952560171111444072049229123565057483750161460024353346284167282452756217662335528813519139808291170539072125381230815729071544861602750936964829313608137325426383735122175229541155376346436093930287402089517426973178917569713384748081827255472576937471496195752727188261435633271238710131736096299798168852925540549342330775279877006784354801422249722573783561685179618816480037695005515426162362431072245638324744480");
    finalExponentZ = new BigInteger("4965661367192848881");
    isFinalExponentZNegative = false;

    blsFq12Factory = BLS12_377Fq12.ONE;
  }
}
