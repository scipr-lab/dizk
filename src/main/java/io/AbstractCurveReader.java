package io;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.fields.AbstractFieldElement;
import java.io.IOException;

/**
 * The interface for a reader class, reading elements of the scalar field and groups G1 and G2 for
 * some curve.
 */
public interface AbstractCurveReader<
    FrT extends AbstractFieldElement<FrT>,
    G1T extends AbstractG1<G1T>,
    G2T extends AbstractG2<G2T>> {
  public abstract FrT readFr() throws IOException;

  public abstract G1T readG1() throws IOException;

  public abstract G2T readG2() throws IOException;
}
