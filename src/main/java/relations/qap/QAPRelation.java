/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations.qap;

import algebra.fft.SerialFFT;
import algebra.fields.AbstractFieldElementExpanded;
import algebra.msm.NaiveMSM;

import java.io.Serializable;
import java.util.List;

public class QAPRelation<FieldT extends AbstractFieldElementExpanded<FieldT>> implements
        Serializable {

    private final List<FieldT> At;
    private final List<FieldT> Bt;
    private final List<FieldT> Ct;
    private final List<FieldT> Ht;
    private final FieldT Zt;
    private final FieldT t;

    private final int numInputs;
    private final int numVariables;
    private final int degree;

    public QAPRelation(
            final List<FieldT> _At,
            final List<FieldT> _Bt,
            final List<FieldT> _Ct,
            final List<FieldT> _Ht,
            final FieldT _Zt,
            final FieldT _t,
            final int _numInputs,
            final int _numVariables,
            final int _degree) {
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

    public boolean isSatisfied(final QAPWitness<FieldT> witness) {
        if (numVariables() != witness.numVariables()) {
            return false;
        }

        if (degree() != witness.degree()) {
            return false;
        }

        if (numInputs() != witness.numInputs()) {
            return false;
        }

        if (numVariables() != witness.coefficientsABC().size()) {
            return false;
        }

        if (degree() + 1 != witness.coefficientsH().size()) {
            return false;
        }

        if (At.size() != numVariables() || Bt.size() != numVariables() || Ct.size() != numVariables()) {
            return false;
        }

        if (Ht.size() != this.degree() + 1) {
            return false;
        }

        FieldT powerOfT = t.one();
        for (FieldT powerOfH : Ht) {
            if (!powerOfH.equals(powerOfT)) {
                return false;
            }
            powerOfT = powerOfT.mul(t);
        }

        final SerialFFT<FieldT> domain = new SerialFFT<>(this.degree(), this.t);
        if (!this.Zt.equals(domain.computeZ(this.t))) {
            return false;
        }

        final FieldT ansA = NaiveMSM.variableBaseMSM(At, witness.coefficientsABC().elements());
        final FieldT ansB = NaiveMSM.variableBaseMSM(Bt, witness.coefficientsABC().elements());
        final FieldT ansC = NaiveMSM.variableBaseMSM(Ct, witness.coefficientsABC().elements());
        final FieldT ansH = NaiveMSM.variableBaseMSM(Ht, witness.coefficientsH());

        return ansA.mul(ansB).sub(ansC).equals(ansH.mul(this.Zt));
    }

    public List<FieldT> At() {
        return At;
    }

    public List<FieldT> Bt() {
        return Bt;
    }

    public List<FieldT> Ct() {
        return Ct;
    }

    public List<FieldT> Ht() {
        return Ht;
    }

    public FieldT At(final int i) {
        return At.get(i);
    }

    public FieldT Bt(final int i) {
        return Bt.get(i);
    }

    public FieldT Ct(final int i) {
        return Ct.get(i);
    }

    public FieldT Ht(final int i) {
        return Ht.get(i);
    }

    public FieldT Zt() {
        return Zt;
    }

    public int numInputs() {
        return numInputs;
    }

    public int numVariables() {
        return numVariables;
    }

    public int degree() {
        return degree;
    }
}
