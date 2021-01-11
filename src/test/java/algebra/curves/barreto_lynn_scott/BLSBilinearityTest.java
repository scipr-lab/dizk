package algebra.curves.barreto_lynn_scott;

import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G1;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377GT;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Pairing;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G1Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G2Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377GTParameters;

import org.junit.jupiter.api.Test;
import algebra.curves.GenericBilinearityTest;

public class BLSBilinearityTest {
    @Test
    public void BLS12_377Test() {
      final BLS12_377G1 g1One = BLS12_377G1Parameters.ONE;
      final BLS12_377G2 g2One = BLS12_377G2Parameters.ONE;
      final BLS12_377GT gtOne = BLS12_377GTParameters.ONE;
      final BLS12_377Fr fieldFactory = new BLS12_377Fr(6);
      final BLS12_377Pairing pairing = new BLS12_377Pairing();
  
      final BLS12_377G1 P = g1One.mul(fieldFactory.random(5L, null));
      final BLS12_377G2 Q = g2One.mul(fieldFactory.random(6L, null));
  
      GenericBilinearityTest gTest = new GenericBilinearityTest();
      gTest.PairingTest(P, Q, gtOne, fieldFactory, pairing);
      gTest.PairingTest(g1One, g2One, gtOne, fieldFactory, pairing);
    }
}
