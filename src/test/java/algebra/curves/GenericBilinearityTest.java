package algebra.curves;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.fields.AbstractFieldElementExpanded;
import algebra.fields.Fp;
import algebra.fields.abstractfieldparameters.AbstractFpParameters;

public class GenericBilinearityTest {
  protected <
          G1T extends AbstractG1<G1T>,
          G2T extends AbstractG2<G2T>,
          GTT extends AbstractGT<GTT>,
          PairingT extends AbstractPairing<G1T, G2T, GTT>,
          FieldT extends AbstractFieldElementExpanded<FieldT>>
      void PairingTest(
          final G1T P,
          final G2T Q,
          final GTT oneGT,
          final AbstractFpParameters ffpp,
          final PairingT pairing) {
    Fp fieldFactory = ffpp.ONE();
    final long seed1 = 4;
    final long seed2 = 7;

    final Fp s = fieldFactory.random(seed1, null);

    G1T sP = P.mul(s);
    G2T sQ = Q.mul(s);

    GTT ans1 = pairing.reducedPairing(sP, Q);
    GTT ans2 = pairing.reducedPairing(P, sQ);
    GTT ans3 = pairing.reducedPairing(P, Q).mul(s.toBigInteger());

    assertTrue(ans1.equals(ans2));
    assertTrue(ans2.equals(ans3));
    // Test no-degeneracy
    assertFalse(ans1.equals(oneGT));
    // G1, G2, GT are order r
    assert (ans1.mul(ffpp.modulus()).equals(oneGT));

    final Fp r = fieldFactory.random(seed2, null);
    final Fp oneFr = fieldFactory.construct(1);
    G1T rP = P.mul(r);
    G2T rMinus1Q = Q.mul(r.sub(oneFr));
    GTT res1 = pairing.reducedPairing(rP, Q);
    GTT res2 = pairing.reducedPairing(P, rMinus1Q);
    assertFalse(res1.equals(res2));
  }
}
