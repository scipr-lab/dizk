package io;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.fields.AbstractFieldElementExpanded;
import java.io.IOException;
import java.util.ArrayList;
import relations.objects.LinearCombination;
import relations.objects.LinearTerm;
import relations.objects.R1CSConstraint;
import relations.objects.R1CSConstraints;
import relations.r1cs.R1CSRelation;

/**
 * Read binary encoded R1CS objects from a stream.
 *
 * <p>(TODO remove group parameters. These are only required for the binary reader - there is
 * currently only a single reader interface for both field and group elements).
 */
public class R1CSReader<
    FieldT extends AbstractFieldElementExpanded<FieldT>,
    G1T extends AbstractG1<G1T>,
    G2T extends AbstractG2<G2T>> {

  final BinaryCurveReader<FieldT, G1T, G2T> reader;

  public R1CSReader(final BinaryCurveReader<FieldT, G1T, G2T> reader_) {
    reader = reader_;
  }

  public R1CSRelation<FieldT> readR1CSRDD() {
    return null;
  }

  public R1CSRelation<FieldT> readR1CS() throws IOException {
    final int num_primary_inputs = Math.toIntExact(reader.readLongLE());
    final int num_auxiliary_inputs = Math.toIntExact(reader.readLongLE());
    final R1CSConstraints<FieldT> constraints = readConstraints();
    return new R1CSRelation<FieldT>(constraints, num_primary_inputs, num_auxiliary_inputs);
  }

  protected R1CSConstraints<FieldT> readConstraints() throws IOException {
    // Note, the non-throwing version is used here since support for exceptions
    // in Function, Supplier, etc is so bad.
    return new R1CSConstraints<FieldT>(reader.readArrayList(() -> readConstraintNoThrow()));
  }

  protected R1CSConstraint<FieldT> readConstraintNoThrow() {
    return new R1CSConstraint<FieldT>(
        readLinearCombinationNoThrow(),
        readLinearCombinationNoThrow(),
        readLinearCombinationNoThrow());
  }

  protected LinearCombination<FieldT> readLinearCombinationNoThrow() {
    try {
      final int size = reader.readIntLE();
      ArrayList<LinearTerm<FieldT>> terms = new ArrayList<LinearTerm<FieldT>>(size);
      for (int i = 0; i < size; ++i) {
        LinearTerm<FieldT> value = readLinearTermNoThrow();
        if (value == null) {
          return null;
        }
        terms.add(value);
      }
      return new LinearCombination<FieldT>(terms);
    } catch (IOException e) {
      System.out.println("ERROR: failed to read linear combination");
      return null;
    }
  }

  protected LinearTerm<FieldT> readLinearTermNoThrow() {
    try {
      final long idx = reader.readLongLE();
      final FieldT coeff = reader.readFr();
      return new LinearTerm<FieldT>(idx, coeff);
    } catch (IOException e) {
      System.out.println("ERROR: failed to read linear term");
      return null;
    }
  }
}
