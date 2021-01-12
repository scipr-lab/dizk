package algebra.curves.barreto_lynn_scott;

import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G1;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377G2;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377GT;
import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Pairing;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G1Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377FrParameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377G2Parameters;
import algebra.curves.barreto_lynn_scott.bls12_377.bls12_377_parameters.BLS12_377GTParameters;
import algebra.fields.Fp;

import org.junit.jupiter.api.Test;
import algebra.curves.GenericBilinearityTest;

public class BLSBilinearityTest extends GenericBilinearityTest {
  @Test
  public void BLS12_377Test() {
    final BLS12_377G1 oneG1 = BLS12_377G1Parameters.ONE;
    final BLS12_377G2 oneG2 = BLS12_377G2Parameters.ONE;
    final BLS12_377GT oneGT = BLS12_377GTParameters.ONE;
    final BLS12_377Pairing pairing = new BLS12_377Pairing();
    final BLS12_377FrParameters FrParameters = new BLS12_377FrParameters();
    final Fp factoryFr = FrParameters.ONE();

    final BLS12_377G1 P = oneG1.mul(factoryFr.random(5L, null));
    final BLS12_377G2 Q = oneG2.mul(factoryFr.random(6L, null));

    PairingTest(P, Q, oneGT, factoryFr, pairing);
    PairingTest(oneG1, oneG2, oneGT, factoryFr, pairing);
  }
}
