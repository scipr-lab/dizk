package io;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.fields.AbstractFieldElement;
import algebra.fields.AbstractFieldElementExpanded;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Base class for binary readers. Handles reading fixed-width BigInteger values (in big-endian
 * form).
 */
public abstract class BinaryCurveReader<
        FrT extends AbstractFieldElementExpanded<FrT>,
        G1T extends AbstractG1<G1T>,
        G2T extends AbstractG2<G2T>>
    implements AbstractCurveReader<FrT, G1T, G2T> {

  private final DataInputStream inStream;

  public DataInputStream getStream() {
    return inStream;
  }

  protected BinaryCurveReader(InputStream inStream_) {
    inStream = new DataInputStream(inStream_);
  }

  protected BigInteger readBigInteger(final int numBytes) throws IOException {
    final byte[] bytes = inStream.readNBytes(numBytes);
    if (bytes.length != numBytes) {
      throw new IOException("unexpected end of stream");
    }
    return new BigInteger(bytes);
  }

  protected static <FieldT extends AbstractFieldElement<FieldT>> int computeSizeBytes(FieldT one) {
    FieldT minusOne = one.zero().sub(one);
    final int sizeBits = minusOne.bitSize();
    return (sizeBits + 7) / 8;
  }
}
