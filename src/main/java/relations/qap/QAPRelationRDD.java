/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations.qap;

import algebra.fft.DistributedFFT;
import algebra.fields.AbstractFieldElementExpanded;
import algebra.msm.NaiveMSM;
import java.io.Serializable;
import org.apache.spark.api.java.JavaPairRDD;

public class QAPRelationRDD<FieldT extends AbstractFieldElementExpanded<FieldT>>
    implements Serializable {

  private final JavaPairRDD<Long, FieldT> At;
  private final JavaPairRDD<Long, FieldT> Bt;
  private final JavaPairRDD<Long, FieldT> Ct;
  private final JavaPairRDD<Long, FieldT> Ht;
  private final FieldT Zt;
  private final FieldT t;

  private final int numInputs;
  private final long numVariables;
  private final long degree;

  public QAPRelationRDD(
      final JavaPairRDD<Long, FieldT> _At,
      final JavaPairRDD<Long, FieldT> _Bt,
      final JavaPairRDD<Long, FieldT> _Ct,
      final JavaPairRDD<Long, FieldT> _Ht,
      final FieldT _Zt,
      final FieldT _t,
      final int _numInputs,
      final long _numVariables,
      final long _degree) {
    At = _At;
    Bt = _Bt;
    Ct = _Ct;
    Ht = _Ht;
    Zt = _Zt;
    t = _t;

    numInputs = _numInputs;
    numVariables = _numVariables;
    degree = _degree;
  }

  public boolean isSatisfied(final QAPWitnessRDD<FieldT> witness) {

    if (this.numVariables() != witness.numVariables()) {
      return false;
    }

    if (this.degree() != witness.degree()) {
      return false;
    }

    if (this.numInputs() != witness.numInputs()) {
      return false;
    }

    if (this.numVariables() != witness.coefficientsABC().count()) {
      return false;
    }

    if (this.degree() + 1 != witness.coefficientsH().count()) {
      return false;
    }

    if (this.Ht.count() != this.degree() + 1) {
      return false;
    }

    final long numInvalidHt = this.Ht.filter((e) -> !this.t.pow(e._1).equals(e._2)).count();
    if (numInvalidHt > 0) {
      return false;
    }

    if (!this.Zt.equals(DistributedFFT.computeZ(this.t, this.degree()))) {
      return false;
    }

    final FieldT ansA = NaiveMSM.distributedVariableBaseMSM(At, witness.coefficientsABC());
    final FieldT ansB = NaiveMSM.distributedVariableBaseMSM(Bt, witness.coefficientsABC());
    final FieldT ansC = NaiveMSM.distributedVariableBaseMSM(Ct, witness.coefficientsABC());
    final FieldT ansH = NaiveMSM.distributedVariableBaseMSM(Ht, witness.coefficientsH());

    return ansA.mul(ansB).sub(ansC).equals(ansH.mul(this.Zt));
  }

  public JavaPairRDD<Long, FieldT> At() {
    return At;
  }

  public JavaPairRDD<Long, FieldT> Bt() {
    return Bt;
  }

  public JavaPairRDD<Long, FieldT> Ct() {
    return Ct;
  }

  public JavaPairRDD<Long, FieldT> Ht() {
    return Ht;
  }

  public FieldT Zt() {
    return Zt;
  }

  public FieldT t() {
    return t;
  }

  public int numInputs() {
    return numInputs;
  }

  public long numVariables() {
    return numVariables;
  }

  public long degree() {
    return degree;
  }
}
