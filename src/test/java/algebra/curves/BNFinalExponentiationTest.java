/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves;

import algebra.curves.barreto_naehrig.BNFields.*;
import algebra.curves.barreto_naehrig.*;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG1Parameters;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG2Parameters;
import algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNGTParameters;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq12;
import algebra.curves.barreto_naehrig.bn254a.BN254aPairing;
import algebra.curves.barreto_naehrig.bn254a.BN254aPublicParameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aGTParameters;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq12;
import algebra.curves.barreto_naehrig.bn254b.BN254bPairing;
import algebra.curves.barreto_naehrig.bn254b.BN254bPublicParameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bGTParameters;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertTrue;

public class BNFinalExponentiationTest {
    private <
            BNFrT extends BNFr<BNFrT>,
            BNFqT extends BNFq<BNFqT>,
            BNFq2T extends BNFq2<BNFqT, BNFq2T>,
            BNFq6T extends BNFq6<BNFqT, BNFq2T, BNFq6T>,
            BNFq12T extends BNFq12<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
            BNG1T extends BNG1<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
            BNG2T extends BNG2<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
            BNGTT extends BNGT<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
            BNG1ParametersT extends AbstractBNG1Parameters<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
            BNG2ParametersT extends AbstractBNG2Parameters<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
            BNGTParametersT extends AbstractBNGTParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
            BNPublicParametersT extends BNPublicParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
            BNPairingT extends BNPairing<BNFrT, BNFqT, BNFq2T, BNFq6T, BNFq12T, BNG1T, BNG2T, BNGTT, BNG1ParametersT, BNG2ParametersT, BNGTParametersT, BNPublicParametersT>>
    void FinalExponentiationTest(
            final BNFq12T elt,
            final BNPublicParametersT publicParameters,
            final BNPairingT pairing) {
        BNFq12T fastExp = pairing.finalExponentiation(elt);

        BigInteger one = BigInteger.ONE;
        BigInteger two = one.add(one);
        BigInteger three = two.add(one);
        BigInteger six = three.add(three);

        BigInteger z = publicParameters.finalExponentZ();
        BigInteger z2 = z.multiply(z);

        BigInteger polynomial =
                two.multiply(z.multiply(six.multiply(z2).add(three.multiply(z).add(one))));
        BNFq12T slowExp = elt.pow(publicParameters.finalExponent().multiply(polynomial));

        System.out.println(fastExp);
        System.out.println(slowExp);

        assertTrue(fastExp.equals(slowExp));
    }

    @Test
    public void BN254aTest() {
        final BN254aFq12 elt = BN254aGTParameters.ONE.element.random(7L, null);
        final BN254aPublicParameters publicParameters = new BN254aPublicParameters();
        final BN254aPairing pairing = new BN254aPairing();
        FinalExponentiationTest(elt, publicParameters, pairing);
    }

    @Test
    public void BN254bTest() {
        final BN254bFq12 elt = BN254bGTParameters.ONE.element.random(7L, null);
        final BN254bPublicParameters publicParameters = new BN254bPublicParameters();
        final BN254bPairing pairing = new BN254bPairing();
        FinalExponentiationTest(elt, publicParameters, pairing);
    }
}
