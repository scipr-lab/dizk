package algebra.curves;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.fields.AbstractFieldElementExpanded;

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
          final FieldT fieldFactory,
          final PairingT pairing) {
    final long seed1 = 4;
    final long seed2 = 7;

    final GTT one = oneGT;
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
}
