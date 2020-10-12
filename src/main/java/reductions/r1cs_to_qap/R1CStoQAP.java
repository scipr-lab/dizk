/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package reductions.r1cs_to_qap;

import algebra.fft.SerialFFT;
import algebra.fields.AbstractFieldElementExpanded;
import configuration.Configuration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import relations.objects.Assignment;
import relations.objects.R1CSConstraint;
import relations.qap.QAPRelation;
import relations.qap.QAPWitness;
import relations.r1cs.R1CSRelation;

public class R1CStoQAP {

  /**
   * Instance map for the R1CSRelation-to-QAP reduction followed by evaluation of the resulting QAP
   * instance.
   *
   * <p>Namely, given a R1CSRelation constraint system r1cs and a field element x, construct a QAP
   * instance (evaluated at t) for which: At := (A_0(t),A_1(t),...,A_m(t)), Bt :=
   * (B_0(t),B_1(t),...,B_m(t)), Ct := (C_0(t),C_1(t),...,C_m(t)), Ht := (1,t,t^2,...,t^n), Zt :=
   * Z(t) ("vanishing polynomial of a certain set S, evaluated at t"); where m = number of variables
   * of the QAP
   */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      QAPRelation<FieldT> R1CStoQAPRelation(final R1CSRelation<FieldT> r1cs, final FieldT t) {
    final int numInputs = r1cs.numInputs();
    final int numVariables = r1cs.numVariables();
    final int numConstraints = r1cs.numConstraints();
    final SerialFFT<FieldT> domain = new SerialFFT<>(numConstraints + numInputs, t);
    final FieldT zero = t.zero();

    final List<FieldT> At = new ArrayList<>(Collections.nCopies(numVariables, zero));
    final List<FieldT> Bt = new ArrayList<>(Collections.nCopies(numVariables, zero));
    final List<FieldT> Ct = new ArrayList<>(Collections.nCopies(numVariables, zero));
    final List<FieldT> Ht = new ArrayList<>(domain.domainSize + 1);

    // Construct in Lagrange basis; add and process the constraints,
    // input_i * 0 = 0, to ensure soundness of input consistency.
    final List<FieldT> lagrangeCoefficients = domain.lagrangeCoefficients(t);
    for (int i = 0; i < r1cs.numInputs(); i++) {
      At.set(i, lagrangeCoefficients.get(numConstraints + i));
    }

    // Process all other constraints; evaluate A(t), B(t), and C(t).
    for (int i = 0; i < numConstraints; i++) {
      final R1CSConstraint<FieldT> constraint = r1cs.constraints(i);
      final FieldT ithLagrange = lagrangeCoefficients.get(i);

      for (int j = 0; j < constraint.A().size(); j++) {
        final int index = (int) constraint.A(j).index();
        final FieldT value = constraint.A(j).value();

        At.set(index, At.get(index).add(ithLagrange.mul(value)));
      }

      for (int j = 0; j < constraint.B().size(); j++) {
        final int index = (int) constraint.B(j).index();
        final FieldT value = constraint.B(j).value();

        Bt.set(index, Bt.get(index).add(ithLagrange.mul(value)));
      }

      for (int j = 0; j < constraint.C().size(); j++) {
        final int index = (int) constraint.C(j).index();
        final FieldT value = constraint.C(j).value();

        Ct.set(index, Ct.get(index).add(ithLagrange.mul(value)));
      }
    }

    // Compute H(t)
    FieldT ti = t.one();
    for (int i = 0; i < domain.domainSize + 1; i++) {
      Ht.add(ti);
      ti = ti.mul(t);
    }

    // Compute vanishing polynomial at t
    final FieldT Zt = domain.computeZ(t);

    return new QAPRelation<>(At, Bt, Ct, Ht, Zt, t, numInputs, numVariables, domain.domainSize);
  }

  /**
   * Witness map for the R1CSRelation-to-QAP reduction.
   *
   * <p>More precisely, compute the coefficients <h_0,h_1,...,h_n> of the polynomial H(z) :=
   * (A(z)*B(z)-C(z))/Z(z) where: A(z) := A_0(z) + \sum_{k=1}^{m} w_k A_k(z) + d1 * Z(z), B(z) :=
   * B_0(z) + \sum_{k=1}^{m} w_k B_k(z) + d2 * Z(z), C(z) := C_0(z) + \sum_{k=1}^{m} w_k C_k(z) + d3
   * * Z(z), Z(z) := "vanishing polynomial of set S" and m = number of variables of the QAP n =
   * degree of the QAP
   *
   * <p>This is done as follows: (1) compute evaluations of A,B,C on S = {sigma_1,...,sigma_n} (2)
   * compute coefficients of A,B,C (3) compute evaluations of A,B,C on T = "coset of S" (4) compute
   * evaluation of H on T (5) compute coefficients of H
   *
   * <p>The code below is not as simple as the above high-level description due to some reshuffling
   * to save space.
   */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      QAPWitness<FieldT> R1CStoQAPWitness(
          final R1CSRelation<FieldT> r1cs,
          final Assignment<FieldT> primary,
          final Assignment<FieldT> auxiliary,
          final FieldT fieldFactory,
          final Configuration config) {

    if (config.debugFlag()) {
      assert (r1cs.isSatisfied(primary, auxiliary));
    }

    final FieldT multiplicativeGenerator = fieldFactory.multiplicativeGenerator();
    final FieldT zero = fieldFactory.zero();
    final SerialFFT<FieldT> domain =
        new SerialFFT<>(r1cs.numConstraints() + r1cs.numInputs(), fieldFactory);

    final List<FieldT> A = new ArrayList<>(Collections.nCopies(domain.domainSize, zero));
    final List<FieldT> B = new ArrayList<>(Collections.nCopies(domain.domainSize, zero));
    final Assignment<FieldT> oneFullAssignment = new Assignment<>(primary, auxiliary);

    // Account for the additional constraints input_i * 0 = 0.
    config.beginLog("Account for the additional constraints input_i * 0 = 0.");
    for (int i = 0; i < r1cs.numInputs(); i++) {
      A.set(i + r1cs.numConstraints(), oneFullAssignment.get(i));
    }
    config.endLog("Account for the additional constraints input_i * 0 = 0.");

    // Account for all other constraints: A and B.
    config.beginLog("Compute evaluation of polynomial A and B on set S.");
    for (int i = 0; i < r1cs.numConstraints(); i++) {
      A.set(i, r1cs.constraints(i).A().evaluate(oneFullAssignment).add(A.get(i)));
      B.set(i, r1cs.constraints(i).B().evaluate(oneFullAssignment));
    }
    config.endLog("Compute evaluation of polynomial A and B on set S.");

    // Perform radix-2 inverse FFT to determine the coefficients of A and B.
    config.beginLog("Perform radix-2 inverse FFT to determine the coefficients of A and B.");
    domain.radix2InverseFFT(A);
    domain.radix2InverseFFT(B);
    config.endLog("Perform radix-2 inverse FFT to determine the coefficients of A and B.");

    // Compute evaluation of polynomials A and B on set T.
    config.beginLog("Compute evaluation of polynomials A and B on set T.");
    domain.radix2CosetFFT(A, multiplicativeGenerator);
    domain.radix2CosetFFT(B, multiplicativeGenerator);
    config.endLog("Compute evaluation of polynomials A and B on set T.");

    // Compute the evaluation of polynomial H on a set T.
    config.beginLog("Compute the evaluation, (A * B), for polynomial H on a set T.");
    List<FieldT> coefficientsH = new ArrayList<>();
    for (int i = 0; i < domain.domainSize; i++) {
      coefficientsH.add(A.get(i).mul(B.get(i)));
    }
    config.endLog("Compute the evaluation, (A * B), for polynomial H on a set T.");

    // Clear memory for A and B.
    A.clear();
    B.clear();

    // Account for all other constraints: C.
    config.beginLog("Compute evaluation of polynomial C on set S.");
    final List<FieldT> C = new ArrayList<>(Collections.nCopies(domain.domainSize, zero));
    for (int i = 0; i < r1cs.numConstraints(); i++) {
      C.set(i, r1cs.constraints(i).C().evaluate(oneFullAssignment));
    }
    config.endLog("Compute evaluation of polynomial C on set S.");

    // Perform radix-2 inverse FFT to determine the coefficients of C.
    config.beginLog("Perform radix-2 inverse FFT to determine the coefficients of C.");
    domain.radix2InverseFFT(C);
    config.endLog("Perform radix-2 inverse FFT to determine the coefficients of C.");

    // Compute evaluation of polynomials C on set T.
    config.beginLog("Compute evaluation of polynomials C on set T.");
    domain.radix2CosetFFT(C, multiplicativeGenerator);
    config.endLog("Compute evaluation of polynomials C on set T.");

    // Compute the evaluation of polynomial H on a set T.
    config.beginLog("Compute the evaluation, (A * B) - C, for polynomial H on a set T.");
    for (int i = 0; i < domain.domainSize; i++) {
      // Compute (A * B) - C into H.
      coefficientsH.set(i, coefficientsH.get(i).sub(C.get(i)));
    }
    config.endLog("Compute the evaluation, (A * B) - C, for polynomial H on a set T.");

    // Clear memory for C.
    C.clear();

    // Divide by Z on set T.
    config.beginLog("Divide by Z on set T.");
    domain.divideByZOnCoset(multiplicativeGenerator, coefficientsH);
    config.endLog("Divide by Z on set T.");

    // Compute coefficients of polynomial H.
    config.beginLog("Compute coefficients of polynomial H.");
    domain.radix2CosetInverseFFT(coefficientsH, multiplicativeGenerator);
    coefficientsH.add(zero);
    config.endLog("Compute coefficients of polynomial H.");

    return new QAPWitness<>(
        oneFullAssignment, coefficientsH, r1cs.numInputs(), r1cs.numVariables(), domain.domainSize);
  }
}
