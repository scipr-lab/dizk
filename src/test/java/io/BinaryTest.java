package io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import algebra.curves.barreto_lynn_scott.bls12_377.BLS12_377Fields.BLS12_377Fr;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class BinaryTest {

  @Test
  public void bignumBinayTest() {
    // Test expected format of bignum binary methods.

    BLS12_377Fr oneFr = BLS12_377Fr.ONE;
    BLS12_377Fr minusOneFr = BLS12_377Fr.ZERO.sub(oneFr);
    byte[] oneFrBytes = oneFr.toBigInteger().toByteArray();
    byte[] minusOneFrBytes = minusOneFr.toBigInteger().toByteArray();

    System.out.println("Fr: oneFr      length : " + String.valueOf(oneFrBytes.length));
    System.out.println("Fr: oneFr      bitSize: " + String.valueOf(oneFr.bitSize()));
    System.out.println("Fr: minusOneFr length : " + String.valueOf(minusOneFrBytes.length));
    System.out.println("Fr: minusOneFr bitSize: " + String.valueOf(minusOneFr.bitSize()));

    // assertTrue(32 == oneFrBytes.length);
    // assertTrue(0 == oneFrBytes[0]);
    // assertTrue(1 == oneFrBytes[31]);

    assertEquals(new BigInteger(new byte[] {0, 1}), new BigInteger(new byte[] {1}));
  }
}
