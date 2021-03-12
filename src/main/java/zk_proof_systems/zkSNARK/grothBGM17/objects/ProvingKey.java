package zk_proof_systems.zkSNARK.grothBGM17.objects;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.fields.AbstractFieldElementExpanded;
import java.io.Serializable;
import java.util.List;
import relations.r1cs.R1CSRelation;
import scala.Tuple2;

/** Groth16-BGM17 Proving key */
public class ProvingKey<
        FieldT extends AbstractFieldElementExpanded<FieldT>,
        G1T extends AbstractG1<G1T>,
        G2T extends AbstractG2<G2T>>
    implements Serializable {

  // [alpha]_1
  private final G1T alphaG1;
  // [beta]
  private final G1T betaG1;
  private final G2T betaG2;
  // [delta]
  private final G1T deltaG1;
  private final G2T deltaG2;
  // {[(beta * A_i(t) + alpha * B_i(t) + C_i(t))/delta]_1}_{i=numInputs+1}^{numVariables}
  private final List<G1T> deltaABCG1;
  // {[A_i(t)]_1}_{i=0}^{numVariables}
  private final List<G1T> queryA;
  // {[B_i(t)]}_{i=0}^{numVariables}
  // Note: queryB is taken in both G1 and G2 because its fastens the prover:
  // - the G2 part is used in the computation of proof.B (encoded in G2)
  // - the G1 part is used in the computation of proof.C (i.e. the  + rB term, encoded in G1)
  private final List<Tuple2<G1T, G2T>> queryB;
  // {[t^i * Z(t)/delta]_1}_{i=0}^{deg(Z(x)) - 2}
  private final List<G1T> queryH;
  // The proving key holds the arithmetized relation
  private final R1CSRelation<FieldT> r1cs;

  public ProvingKey(
      final G1T _alphaG1,
      final G1T _betaG1,
      final G2T _betaG2,
      final G1T _deltaG1,
      final G2T _deltaG2,
      final List<G1T> _deltaABCG1,
      final List<G1T> _queryA,
      final List<Tuple2<G1T, G2T>> _queryB,
      final List<G1T> _queryH,
      final R1CSRelation<FieldT> _r1cs) {
    alphaG1 = _alphaG1;
    betaG1 = _betaG1;
    betaG2 = _betaG2;
    deltaG1 = _deltaG1;
    deltaG2 = _deltaG2;
    deltaABCG1 = _deltaABCG1;
    queryA = _queryA;
    queryB = _queryB;
    queryH = _queryH;
    r1cs = _r1cs;
  }

  public G1T alphaG1() {
    return alphaG1;
  }

  public G1T betaG1() {
    return betaG1;
  }

  public G2T betaG2() {
    return betaG2;
  }

  public G1T deltaG1() {
    return deltaG1;
  }

  public G2T deltaG2() {
    return deltaG2;
  }

  public List<G1T> deltaABCG1() {
    return deltaABCG1;
  }

  public List<G1T> queryA() {
    return queryA;
  }

  public List<Tuple2<G1T, G2T>> queryB() {
    return queryB;
  }

  public List<G1T> queryH() {
    return queryH;
  }

  public R1CSRelation<FieldT> r1cs() {
    return r1cs;
  }

  public boolean equals(final ProvingKey<?, ?, ?> o) {
    return alphaG1.equals(o.alphaG1)
        && betaG1.equals(o.betaG1)
        && betaG2.equals(o.betaG2)
        && deltaG1.equals(o.deltaG1)
        && deltaG2.equals(o.deltaG2)
        && deltaABCG1.equals(o.deltaABCG1)
        && queryA.equals(o.queryA)
        && queryB.equals(o.queryB)
        && queryH.equals(o.queryH)
        && r1cs.equals(o.r1cs);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (!(o instanceof ProvingKey<?, ?, ?>)) {
      return false;
    }
    return (equals((ProvingKey<?, ?, ?>) o));
  }
}
