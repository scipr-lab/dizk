/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.barreto_naehrig.bn254b;

import algebra.curves.barreto_naehrig.BNPublicParameters;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq12;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq2;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq6;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bFq2Parameters;

import java.math.BigInteger;

public class BN254bPublicParameters
        extends BNPublicParameters<BN254bFq, BN254bFq2, BN254bFq6, BN254bFq12> {

    public BN254bPublicParameters() {
        final BN254bFq2Parameters Fq2Parameters = new BN254bFq2Parameters();

        coefficientB = new BN254bFq("13");
        twist = new BN254bFq2(new BN254bFq("3"), new BN254bFq("1"));
        twistCoefficientB = twist.inverse().mul(coefficientB);
        bC0MulTwist = coefficientB.mul(new BN254bFq(Fq2Parameters.nonresidue()));
        bC1MulTwist = coefficientB.mul(new BN254bFq(Fq2Parameters.nonresidue()));
        qXMulTwist = new BN254bFq2(
                new BN254bFq("4219883591014163367905881574021031215493473653793212010160902933860880418057"),
                new BN254bFq("2651212873738315799336967630323287311248564116383877570845186714307597874614"));
        qYMulTwist = new BN254bFq2(
                new BN254bFq("13912320527472757632173617297762501558452172370347548758566367565831315297252"),
                new BN254bFq("1205068982561693439199800845949398625559407190263521519935490725156185227042"));

        // Pairing parameters
        ateLoopCount = new BigInteger("28315256757185150978");
        isAteLoopCountNegative = false;
        finalExponent = new BigInteger("58826724991670869544109313981577958244743152849142047699203855693730455421773506387463086131577711144104664649664026310896762025515924896730313219653631254589100015896513952648588781299459067848548989638959157380655332622128514638000845171829983714781974389655291416927867788082504571363229686947137839336872369834655400555074566583248727148368785687092879882363030860699954823601199938887471363762490408139625930119231579615866011921059983190165338988596687772671512637271098380497957663088074822654805460388099806017262919626846989921881633377540825795256034620442019256549777278559495072405782622799914353449819501669060129394044103849962920649917454290303782249402374184300222840142338465792259620650076802219667554623481258201601247041790707961415215821372481141096550644850419243758779120434539819757528011969878056470743359852380160");
        finalExponentZ = new BigInteger("4719209459530858496");
        isFinalExponentZNegative = false;

        bnFq12Factory = BN254bFq12.ONE;
    }
}
