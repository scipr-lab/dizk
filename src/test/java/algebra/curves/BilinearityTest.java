/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.BN254aG1;
import algebra.curves.barreto_naehrig.bn254a.BN254aG2;
import algebra.curves.barreto_naehrig.bn254a.BN254aGT;
import algebra.curves.barreto_naehrig.bn254a.BN254aPairing;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aGTParameters;
import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFr;
import algebra.curves.barreto_naehrig.bn254b.BN254bG1;
import algebra.curves.barreto_naehrig.bn254b.BN254bG2;
import algebra.curves.barreto_naehrig.bn254b.BN254bGT;
import algebra.curves.barreto_naehrig.bn254b.BN254bPairing;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG1Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bG2Parameters;
import algebra.curves.barreto_naehrig.bn254b.bn254b_parameters.BN254bGTParameters;
import algebra.curves.fake.*;
import algebra.curves.fake.fake_parameters.FakeG1Parameters;
import algebra.curves.fake.fake_parameters.FakeG2Parameters;
import algebra.curves.fake.fake_parameters.FakeGTParameters;
import algebra.fields.AbstractFieldElementExpanded;
import algebra.fields.Fp;
import algebra.fields.mock.fieldparameters.LargeFpParameters;
import org.junit.jupiter.api.Test;

public class BilinearityTest {
  private <
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>,
          GTT extends AbstractGT<GTT>,
          PairingT extends AbstractPairing<G1T, G2T, GTT>,
          FieldT extends AbstractFieldElementExpanded<FieldT>>
      void PairingTest(
          final G1T P,
          final G2T Q,
          final GTT gtOne,
          final FieldT fieldFactory,
          final PairingT pairing) {
    final long seed1 = 4;
    final long seed2 = 7;

    final GTT one = gtOne;
    final FieldT s = fieldFactory.random(seed1 + seed2, null);

    G1T sP = P.mul(s);
    G2T sQ = Q.mul(s);

    GTT ans1 = pairing.reducedPairing(sP, Q);
    GTT ans2 = pairing.reducedPairing(P, sQ);
    GTT ans3 = pairing.reducedPairing(P, Q).mul(s.toBigInteger());

    assertTrue(ans1.equals(ans2));
    assertTrue(ans2.equals(ans3));
    assertFalse(ans1.equals(one));
  }

  @Test
  public void BN254aTest() {
    final BN254aG1 g1One = BN254aG1Parameters.ONE;
    final BN254aG2 g2One = BN254aG2Parameters.ONE;
    final BN254aGT gtOne = BN254aGTParameters.ONE;
    final BN254aFr fieldFactory = new BN254aFr(6);
    final BN254aPairing pairing = new BN254aPairing();

    final BN254aG1 P = g1One.mul(fieldFactory.random(5L, null));
    final BN254aG2 Q = g2One.mul(fieldFactory.random(6L, null));

    PairingTest(P, Q, gtOne, fieldFactory, pairing);
    PairingTest(g1One, g2One, gtOne, fieldFactory, pairing);
  }

  @Test
  public void BN254bTest() {
    final BN254bG1 g1One = BN254bG1Parameters.ONE;
    final BN254bG2 g2One = BN254bG2Parameters.ONE;
    final BN254bGT gtOne = BN254bGTParameters.ONE;
    final BN254bFr fieldFactory = new BN254bFr(6);
    final BN254bPairing pairing = new BN254bPairing();

    final BN254bG1 P = g1One.mul(fieldFactory.random(5L, null));
    final BN254bG2 Q = g2One.mul(fieldFactory.random(6L, null));

    PairingTest(P, Q, gtOne, fieldFactory, pairing);
    PairingTest(g1One, g2One, gtOne, fieldFactory, pairing);
  }

  @Test
  public void FakeTest() {
    FakeInitialize.init();
    final FakeG1 g1Factory = new FakeG1Parameters().ONE();
    final FakeG2 g2Factory = new FakeG2Parameters().ONE();
    final FakeGT gTFactory = new FakeGTParameters().ONE();
    final Fp fieldFactory = new LargeFpParameters().ONE();

    FakePairing pairing = new FakePairing();

    PairingTest(g1Factory, g2Factory, gTFactory, fieldFactory, pairing);
  }
}
