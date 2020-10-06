/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package zk_proof_systems.zkSNARK.objects;

import algebra.curves.AbstractG1;
import algebra.curves.AbstractG2;
import algebra.fields.AbstractFieldElementExpanded;
import java.io.Serializable;
import org.apache.spark.api.java.JavaPairRDD;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple2;

public class ProvingKeyRDD<
        FieldT extends AbstractFieldElementExpanded<FieldT>,
        G1T extends AbstractG1<G1T>,
        G2T extends AbstractG2<G2T>>
    implements Serializable {

  // Below, [x]_1 (resp. [x]_2 and []_T) represents the encoding of x in G1 (resp. G2 and GT)
  // We follow the notations in Groth16 (namely, polynomials are denoted u, v, w, h, t instead of A,
  // B, C, H, Z. Moreover the evaluation point is denoted by x)
  //
  // [alpha]_1
  private final G1T alphaG1;
  // [beta]_1
  private final G1T betaG1;
  // [beta]_2
  private final G2T betaG2;
  // [delta]_1
  private final G1T deltaG1;
  // [delta]_2
  private final G2T deltaG2;
  // {[(beta * u_i(x) + alpha * v_i(x) + w_i(x))/delta]_1}
  private final JavaPairRDD<Long, G1T> deltaABCG1;
  // {[u_i(x)]_1}
  private final JavaPairRDD<Long, G1T> queryA;
  // {[v_i(x)]_1}
  private final JavaPairRDD<Long, Tuple2<G1T, G2T>> queryB;
  // {[(x^i * t(x))/delta]_1}
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
