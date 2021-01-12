package algebra.curves;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.groups.AbstractGroup;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class GenericCurvesTest {
  protected <GroupT extends AbstractGroup<GroupT>> void GroupTest(final GroupT groupFactory) {
    final BigInteger rand1 = new BigInteger("76749407");
    final BigInteger rand2 = new BigInteger("44410867");
    final BigInteger randsum = new BigInteger("121160274");

    final GroupT zero = groupFactory.zero();
    assertTrue(zero.equals(zero));

    final GroupT one = groupFactory.one();
    assertTrue(one.equals(one));

    final GroupT two = one.mul(new BigInteger("2"));
    assertTrue(two.equals(two));

    final GroupT three = one.mul(new BigInteger("3"));
    final GroupT four = one.mul(new BigInteger("4"));
    final GroupT five = one.mul(new BigInteger("5"));
    assertTrue(two.add(five).equals(three.add(four)));

    final long seed1 = 4;
    final long seed2 = 7;
    assertTrue(groupFactory.random(seed1, null).equals(groupFactory.random(seed1, null)));
    assertFalse(groupFactory.random(seed1, null).equals(groupFactory.random(seed2, null)));
    final byte[] secureSeed1 = "xz8f5j".getBytes();
    final byte[] secureSeed2 = "f5gh9c".getBytes();
    assertFalse(
        groupFactory.random(null, secureSeed1).equals(groupFactory.random(null, secureSeed1)));
    assertFalse(
        groupFactory.random(null, secureSeed1).equals(groupFactory.random(null, secureSeed2)));

    final GroupT A = groupFactory.random(seed1, null);
    final GroupT B = groupFactory.random(seed2, null);

    assertFalse(one.equals(zero));
    assertFalse(A.equals(zero));
    assertFalse(A.equals(one));

    assertFalse(B.equals(zero));
    assertFalse(B.equals(one));

    // Point doubling
    assertTrue(A.dbl().equals(A.add(A)));
    assertTrue(B.dbl().equals(B.add(B)));
    // Addition
    assertTrue(one.add(two).equals(three));
    assertTrue(two.add(one).equals(three));
    assertTrue(A.add(B).equals(B.add(A)));
    // Subtraction (addition by inverses)
    assertTrue(A.sub(A).equals(zero));
    assertTrue(A.sub(B).equals(A.add(B.negate())));
    assertTrue(A.sub(B).equals(B.negate().add(A)));
    // Handle special cases
    assertTrue(zero.add(A.negate()).equals(A.negate()));
    assertTrue(zero.sub(A).equals(A.negate()));
    assertTrue(A.sub(zero).equals(A));
    assertTrue(A.add(zero).equals(A));
    assertTrue(zero.add(A).equals(A));

    // (A+B)*2 = (A+B) + (A+B)
    assertTrue(A.add(B).dbl().equals(A.add(B).add(B.add(A))));
    assertTrue(A.add(B).mul(new BigInteger("2")).equals(A.add(B).add(B.add(A))));

    // A*s + A*r = A*(r+s)
    assertTrue(A.mul(rand1).add(A.mul(rand2)).equals(A.mul(randsum)));
  }

  /*
  @Test
  public void FakeTest() {
    FakeInitialize.init();
    final FakeG1 g1Factory = new FakeG1Parameters().ONE();
    final FakeG2 g2Factory = new FakeG2Parameters().ONE();

    GroupTest(g1Factory);
    GroupTest(g2Factory);
  }
  */
}
