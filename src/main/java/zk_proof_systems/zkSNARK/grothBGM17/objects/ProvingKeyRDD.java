package zk_proof_systems.zkSNARK.grothBGM17.objects;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.fields.AbstractFieldElementExpanded;
import java.io.Serializable;
import org.apache.spark.api.java.JavaPairRDD;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple2;

/** Groth16-BGM17 Proving key for distributed computation */
public class ProvingKeyRDD<
        FieldT extends AbstractFieldElementExpanded<FieldT>,
        G1T extends AbstractG1<G1T>,
        G2T extends AbstractG2<G2T>>
    implements Serializable {

  // Below, [x]_1 (resp. [x]_2 and [x]_T) represents the encoding of x in G1 (resp. G2 and GT)
  // We do not follow the notations in Groth16 (namely, polynomials are denoted A, B, C, H, Z
  // instead of u, v, w, h, t. Moreover the evaluation point is denoted by t)
  //
  // [alpha]_1
  private final G1T alphaG1;
  // [beta]
  private final G1T betaG1;
  private final G2T betaG2;
  // [delta]
  private final G1T deltaG1;
  private final G2T deltaG2;
  // {[(beta * A_i(t) + alpha * B_i(t) + C_i(t))/delta]_1}_{i=numInputs+1}^{numVariables}
  private final JavaPairRDD<Long, G1T> deltaABCG1;
  // {[A_i(t)]_1}_{i=0}^{numVariables}
  private final JavaPairRDD<Long, G1T> queryA;
  // {[B_i(t)]}_{i=0}^{numVariables}
  // Note: queryB is taken in both G1 and G2 because its fastens the prover:
  // - the G2 part is used in the computation of proof.B (encoded in G2)
  // - the G1 part is used in the computation of proof.C (i.e. the  + rB term, encoded in G1)
  private final JavaPairRDD<Long, Tuple2<G1T, G2T>> queryB;
  // {[t^i * Z(t)/delta]_1}_{i=0}^{deg(Z(x)) - 2}
  private final JavaPairRDD<Long, G1T> queryH;
  // The proving key contains an arithmetized relation
  private final R1CSRelationRDD<FieldT> r1cs;

  public ProvingKeyRDD(
      final G1T _alphaG1,
      final G1T _betaG1,
      final G2T _betaG2,
      final G1T _deltaG1,
      final G2T _deltaG2,
      final JavaPairRDD<Long, G1T> _deltaABCG1,
      final JavaPairRDD<Long, G1T> _queryA,
      final JavaPairRDD<Long, Tuple2<G1T, G2T>> _queryB,
      final JavaPairRDD<Long, G1T> _queryH,
      final R1CSRelationRDD<FieldT> _r1cs) {
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

  public JavaPairRDD<Long, G1T> deltaABCG1() {
    return deltaABCG1;
  }

  public JavaPairRDD<Long, G1T> queryA() {
    return queryA;
  }

  public JavaPairRDD<Long, Tuple2<G1T, G2T>> queryB() {
    return queryB;
  }

  public JavaPairRDD<Long, G1T> queryH() {
    return queryH;
  }

  public R1CSRelationRDD<FieldT> r1cs() {
    return r1cs;
  }
}
