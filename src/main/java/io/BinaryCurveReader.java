package io;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.fields.AbstractFieldElement;
import algebra.fields.AbstractFieldElementExpanded;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Base class for binary readers. Handles reading fixed-width BigInteger values (in big-endian
 * form).
 */
public abstract class BinaryCurveReader<
        FrT extends AbstractFieldElementExpanded<FrT>,
        G1T extends AbstractG1<G1T>,
        G2T extends AbstractG2<G2T>>
    extends DataInputStream implements AbstractCurveReader<FrT, G1T, G2T> {

  protected BinaryCurveReader(InputStream inStream_) {
    super(inStream_);
  }

  /**
   * Version of readFr which returns null instead of throwing on error (for use in functional-style
   * function composition.
   */
  public FrT readFrNoThrow() {
    try {
      return readFr();
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Version of readG1 which returns null instead of throwing on error (for use in functional-style
   * function composition.
   */
  public G1T readG1NoThrow() {
    try {
      return readG1();
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Version of readG2 which returns null instead of throwing on error (for use in functional-style
   * function composition.
   */
  public G2T readG2NoThrow() {
    try {
      return readG2();
    } catch (IOException e) {
      return null;
    }
  }

  protected BigInteger readBigInteger(final int numBytes) throws IOException {
    final byte[] bytes = readNBytes(numBytes);
    if (bytes.length != numBytes) {
      throw new IOException("unexpected end of stream");
    }
    return new BigInteger(bytes);
  }

  public int readIntLE() throws IOException {
    final int vBE = readInt();
    return (vBE << 24) | ((vBE << 8) & 0x00ff0000) | ((vBE >> 8) & 0x0000ff00) | (vBE >> 24);
  }

  public long readLongLE() throws IOException {
    final long iL = (long) readIntLE();
    final long iH = (long) readIntLE();
    return (iH << 32) | iL;
  }

  public <T> ArrayList<T> readArrayList(final Supplier<T> reader) throws IOException {
    final long size = readLongLE();
    return readArrayListN(reader, Math.toIntExact(size));
  }

  public <T> ArrayList<T> readArrayListN(final Supplier<T> reader, final int numElements)
      throws IOException {
    ArrayList<T> elements = new ArrayList<T>(numElements);
    for (long i = 0; i < numElements; ++i) {
      // It seems exceptions are not supported through Function / Supplier,
      // etc. Workaround using nulls.
      T value = reader.get();
      if (value == null) {
        throw new IOException("failed to read element");
      }
      elements.add(value);
    }
    return elements;
  }

  public <T> ArrayList<T> readArrayListNoThrow(Supplier<T> reader) {
    try {
      return readArrayList(reader);
    } catch (IOException e) {
      return null;
    }
  }

  protected static <FieldT extends AbstractFieldElement<FieldT>> int computeSizeBytes(FieldT one) {
    FieldT minusOne = one.zero().sub(one);
    final int sizeBits = minusOne.bitSize();
    return (sizeBits + 7) / 8;
  }
}
