package algebra.curves.mock;

//import algebra.curves.mock.fake_parameters.FakeG1Parameters;
//import algebra.curves.mock.fake_parameters.FakeG2Parameters;
//import algebra.curves.mock.fake_parameters.FakeGTParameters;
//import algebra.fields.Fp;
//import algebra.fields.mock.fieldparameters.LargeFpParameters;
import org.junit.jupiter.api.Test;

import algebra.curves.GenericBilinearityTest;

public class MockBilinearityTest extends GenericBilinearityTest {
  @Test
  public void FakeTest() {
    FakeInitialize.init();
    //final FakeG1 g1Factory = new FakeG1Parameters().ONE();
    //final FakeG2 g2Factory = new FakeG2Parameters().ONE();
    //final FakeGT gTFactory = new FakeGTParameters().ONE();
    //final LargeFpParameters fieldParameters = new LargeFpParameters();
    //FakePairing pairing = new FakePairing();
    //PairingTest(g1Factory, g2Factory, gTFactory, fieldParameters, pairing);
  }
}
