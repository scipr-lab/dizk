package io;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.fields.AbstractFieldElementExpanded;
import java.io.IOException;
import org.apache.spark.api.java.JavaPairRDD;
import relations.objects.Assignment;
import scala.Tuple2;

/** Read assignment objects given for Assignment objects. */
public class AssignmentReader<
    FieldT extends AbstractFieldElementExpanded<FieldT>,
    G1T extends AbstractG1<G1T>,
    G2T extends AbstractG2<G2T>> {

  final BinaryCurveReader<FieldT, G1T, G2T> reader;

  public AssignmentReader(final BinaryCurveReader<FieldT, G1T, G2T> reader_) {
    reader = reader_;
  }

  public Tuple2<Assignment<FieldT>, Assignment<FieldT>> readPrimaryAuxiliary(
      final long primaryInputSize) throws IOException {
    // Assignments are written as a single collection. Here, we split it into
    // primary and auxiliary Assignment objects.
    final long numEntries = reader.readLongLE();
    if (numEntries < primaryInputSize) {
      throw new IOException(
          "insufficient entries reading Assignment (" + String.valueOf(numEntries) + ")");
    }
    final int auxInputSize = Math.toIntExact(numEntries - primaryInputSize);
    final var primary =
        new Assignment<FieldT>(
            reader.readArrayListN(() -> reader.readFrNoThrow(), Math.toIntExact(primaryInputSize)));
    final var aux =
        new Assignment<FieldT>(reader.readArrayListN(() -> reader.readFrNoThrow(), auxInputSize));
    return new Tuple2<Assignment<FieldT>, Assignment<FieldT>>(primary, aux);
  }

  public Tuple2<Assignment<FieldT>, JavaPairRDD<Long, FieldT>> readPrimaryAuxiliaryRDD(
      final long primaryInputSize, final long numPartitions) {
    return null;
  }
}
