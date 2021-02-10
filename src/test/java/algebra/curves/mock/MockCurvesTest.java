package algebra.curves.mock;

import algebra.curves.GenericCurvesTest;
import algebra.curves.mock.fake_parameters.FakeG1Parameters;
import algebra.curves.mock.fake_parameters.FakeG2Parameters;
import org.junit.jupiter.api.Test;

public class MockCurvesTest extends GenericCurvesTest {
  @Test
  public void FakeTest() {
    FakeInitialize.init();
    final FakeG1 g1Factory = new FakeG1Parameters().ONE();
    final FakeG2 g2Factory = new FakeG2Parameters().ONE();

    GroupTest(g1Factory);
    GroupTest(g2Factory);
  }
}
