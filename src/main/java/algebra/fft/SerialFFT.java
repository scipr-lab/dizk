package algebra.fft;

import algebra.fields.AbstractFieldElementExpanded;
import common.MathUtils;
import java.io.Serializable;
import java.util.List;

public class SerialFFT<FieldT extends AbstractFieldElementExpanded<FieldT>>
    implements Serializable {

  public final int domainSize;
  private final FieldT omega;

  public SerialFFT(final long _domainSize, final FieldT fieldFactory) {
    assert (_domainSize > 1);
    domainSize = (int) MathUtils.lowestPowerOfTwo(_domainSize);
    omega = fieldFactory.rootOfUnity(domainSize);
  }

  /** Compute the FFT, over the domain S, of the vector input, and stores the result in input. */
  public void radix2FFT(final List<FieldT> input) {
    assert (input.size() == domainSize);

    FFTAuxiliary.serialRadix2FFT(input, omega);
  }

  /**
   * Compute the inverse FFT, over the domain S, of the vector input, and stores the result in
   * input.
   */
  public void radix2InverseFFT(final List<FieldT> input) {
    assert (input.size() == domainSize);

    FFTAuxiliary.serialRadix2FFT(input, omega.inverse());

    final FieldT constant = input.get(0).construct(domainSize).inverse();
    for (int i = 0; i < domainSize; ++i) {
      input.set(i, input.get(i).mul(constant));
    }
  }

  /** Compute the FFT, over the domain g*S, of the vector input, and stores the result in input. */
  public void radix2CosetFFT(final List<FieldT> input, final FieldT g) {
    FFTAuxiliary.multiplyByCoset(input, g);
    this.radix2FFT(input);
  }

  /**
   * Compute the inverse FFT, over the domain g*S, of the vector input, and stores the result in
   * input.
   */
  public void radix2CosetInverseFFT(final List<FieldT> input, final FieldT g) {
    this.radix2InverseFFT(input);
    FFTAuxiliary.multiplyByCoset(input, g.inverse());
  }

  /**
   * Evaluate all Lagrange polynomials.
   *
   * <p>The inputs are: - an integer m - an element t The output is a vector (b_{0},...,b_{m-1})
   * where b_{i} is the evaluation of L_{i,S}(z) at z = t.
   */
  public List<FieldT> lagrangeCoefficients(final FieldT t) {
    return FFTAuxiliary.serialRadix2LagrangeCoefficients(t, domainSize);
  }

  /** Returns the ith power of omega, omega^i. */
  public FieldT getDomainElement(final int i) {
    return omega.pow(i);
  }

  /** Evaluate the vanishing polynomial of S at the field element t. */
  public FieldT computeZ(final FieldT t) {
    return t.pow(domainSize).sub(t.one());
  }

  /**
   * Add the coefficients of the vanishing polynomial of S to the coefficients of the polynomial H.
   */
  public void addPolyZ(final FieldT coefficient, final List<FieldT> H) {
    assert (H.size() == domainSize + 1);
    H.set(domainSize, H.get(domainSize).add(coefficient));
    H.set(0, H.get(0).sub(coefficient));
  }

  /**
   * Multiply by the evaluation, on a coset of S, of the inverse of the vanishing polynomial of S,
   * and stores the result in input.
   */
  public void divideByZOnCoset(final FieldT coset, final List<FieldT> input) {
    final FieldT inverseZCoset = computeZ(coset).inverse();
    for (int i = 0; i < domainSize; i++) {
      input.set(i, input.get(i).mul(inverseZCoset));
    }
  }
}
