/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fft;

import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.fields.ComplexField;
import algebra.fields.Fp;
import algebra.fields.fieldparameters.LargeFpParameters;
import common.NaiveEvaluation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SerialFFTTest implements Serializable {
  private LargeFpParameters FpParameters;

  @BeforeEach
  public void setUp() {
    FpParameters = new LargeFpParameters();
  }

  @Test
  public void FFTTest() {
    final int m = 4;
    final ComplexField FieldFactory = new ComplexField(1);
    final SerialFFT<ComplexField> domain = new SerialFFT<>(m, FieldFactory);

    ArrayList<ComplexField> serial = new ArrayList<>(m);
    serial.add(new ComplexField(2));
    serial.add(new ComplexField(5));
    serial.add(new ComplexField(3));
    serial.add(new ComplexField(8));

    ArrayList<ComplexField> naiveIn = new ArrayList<>(serial);

    domain.radix2FFT(serial);

    final ComplexField omega = new ComplexField(0).rootOfUnity(m);
    for (int i = 0; i < m; i++) {
      final ComplexField naive = NaiveEvaluation.evaluatePolynomial(naiveIn, omega.pow(i));
      System.out.println(naive + " == " + serial.get(i));
      assertTrue(naive.equals(serial.get(i)));
    }
  }

  @Test
  public void FFTofInverseFFTTest() {
    final int m = 4;
    final ComplexField FieldFactory = new ComplexField(1);
    final SerialFFT<ComplexField> domain = new SerialFFT<>(m, FieldFactory);

    ArrayList<ComplexField> serial = new ArrayList<>(m);
    serial.add(new ComplexField(2));
    serial.add(new ComplexField(5));
    serial.add(new ComplexField(3));
    serial.add(new ComplexField(8));

    ArrayList<ComplexField> original = new ArrayList<>(serial);

    domain.radix2FFT(serial);
    domain.radix2InverseFFT(serial);

    for (int i = 0; i < m; i++) {
      System.out.println(original.get(i) + " == " + serial.get(i));
      assertTrue(original.get(i).equals(serial.get(i)));
    }
  }

  @Test
  public void CosetFFTTest() {
    final int m = 4;
    final ComplexField FieldFactory = new ComplexField(1);
    final SerialFFT<ComplexField> domain = new SerialFFT<>(m, FieldFactory);

    ArrayList<ComplexField> serial = new ArrayList<>(m);
    serial.add(new ComplexField(2));
    serial.add(new ComplexField(5));
    serial.add(new ComplexField(3));
    serial.add(new ComplexField(8));

    final ComplexField shift = new ComplexField(3);
    ArrayList<ComplexField> naiveIn = new ArrayList<>(serial);
    FFTAuxiliary.multiplyByCoset(naiveIn, shift);

    domain.radix2CosetFFT(serial, shift);

    final ComplexField omega = new ComplexField(0).rootOfUnity(m);
    for (int i = 0; i < m; i++) {
      final ComplexField naive = NaiveEvaluation.evaluatePolynomial(naiveIn, omega.pow(i));
      System.out.println(naive + " == " + serial.get(i));
      assertTrue(naive.equals(serial.get(i)));
    }
  }

  @Test
  public void CosetFFTofCosetInverseFFTTest() {
    int m = 4;
    final ComplexField FieldFactory = new ComplexField(1);
    final SerialFFT<ComplexField> domain = new SerialFFT<>(m, FieldFactory);

    ArrayList<ComplexField> serial = new ArrayList<>(m);
    serial.add(new ComplexField(2));
    serial.add(new ComplexField(5));
    serial.add(new ComplexField(3));
    serial.add(new ComplexField(8));

    ArrayList<ComplexField> original = new ArrayList<>(serial);

    final ComplexField shift = new ComplexField(3);

    domain.radix2CosetFFT(serial, shift);
    domain.radix2CosetInverseFFT(serial, shift);

    for (int i = 0; i < m; i++) {
      System.out.println(original.get(i) + " == " + serial.get(i));
      assertTrue(original.get(i).equals(serial.get(i)));
    }
  }

  @Test
  public void LagrangeTest() {
    final int m = 4;
    final ComplexField FieldFactory = new ComplexField(1);
    final SerialFFT<ComplexField> domain = new SerialFFT<>(m, FieldFactory);

    final ComplexField t = new ComplexField(7);
    final List<ComplexField> lagrange = domain.lagrangeCoefficients(t);

    List<ComplexField> omega = new ArrayList<>(m);
    for (int i = 0; i < m; i++) {
      omega.add(domain.getDomainElement(i));
    }

    for (int i = 0; i < m; i++) {
      final ComplexField naive = NaiveEvaluation.evaluateLagrangePolynomial(m, omega, t, i);
      System.out.println(naive + " == " + lagrange.get(i));
      assertTrue(naive.equals(lagrange.get(i)));
    }
  }

  @Test
  public void ComputeZTest() {
    final int m = 4;
    final ComplexField FieldFactory = new ComplexField(1);
    final SerialFFT<ComplexField> domain = new SerialFFT<>(m, FieldFactory);

    final ComplexField t = new ComplexField(7);

    final ComplexField Zt = domain.computeZ(t);

    ComplexField ZNaive = new ComplexField(1);
    for (int i = 0; i < m; i++) {
      ZNaive = ZNaive.mul(t.sub(domain.getDomainElement(i)));
    }

    assertTrue(Zt.equals(ZNaive));
  }

  @Test
  public void PrimeFieldSerialFFTTest() {
    final int m = 4;
    final Fp FieldFactory = new Fp(1, FpParameters);
    final SerialFFT<Fp> domain = new SerialFFT<>(m, FieldFactory);

    ArrayList<Fp> serial = new ArrayList<>(m);
    serial.add(new Fp(2, FpParameters));
    serial.add(new Fp(5, FpParameters));
    serial.add(new Fp(3, FpParameters));
    serial.add(new Fp(8, FpParameters));

    ArrayList<Fp> naiveIn = new ArrayList<>(serial);

    domain.radix2FFT(serial);

    final Fp omega = FieldFactory.rootOfUnity(m);
    for (int i = 0; i < m; i++) {
      final Fp naive = NaiveEvaluation.evaluatePolynomial(naiveIn, omega.pow(i));
      System.out.println(naive + " == " + serial.get(i));
      assertTrue(naive.equals(serial.get(i)));
    }
  }

  @Test
  public void PrimeFieldFFTofInverseFFTTest() {
    final int m = 4;
    final Fp FieldFactory = new Fp(1, FpParameters);
    final SerialFFT<Fp> domain = new SerialFFT<>(m, FieldFactory);

    ArrayList<Fp> serial = new ArrayList<>(m);
    serial.add(new Fp(2, FpParameters));
    serial.add(new Fp(5, FpParameters));
    serial.add(new Fp(3, FpParameters));
    serial.add(new Fp(8, FpParameters));

    ArrayList<Fp> original = new ArrayList<>(serial);

    domain.radix2FFT(serial);
    domain.radix2InverseFFT(serial);

    for (int i = 0; i < m; i++) {
      System.out.println(original.get(i) + " == " + serial.get(i));
      assertTrue(original.get(i).equals(serial.get(i)));
    }
  }

  @Test
  public void PrimeFieldSerialCosetFFTTest() {
    final int m = 4;
    final Fp FieldFactory = new Fp(1, FpParameters);
    final SerialFFT<Fp> domain = new SerialFFT<>(m, FieldFactory);

    ArrayList<Fp> serial = new ArrayList<>(m);
    serial.add(new Fp(2, FpParameters));
    serial.add(new Fp(5, FpParameters));
    serial.add(new Fp(3, FpParameters));
    serial.add(new Fp(8, FpParameters));

    final Fp shift = new Fp(3, FpParameters);
    ArrayList<Fp> naiveIn = new ArrayList<>(serial);
    FFTAuxiliary.multiplyByCoset(naiveIn, shift);

    domain.radix2CosetFFT(serial, shift);

    final Fp omega = FieldFactory.rootOfUnity(m);
    for (int i = 0; i < m; i++) {
      final Fp naive = NaiveEvaluation.evaluatePolynomial(naiveIn, omega.pow(i));
      System.out.println(naive + " == " + serial.get(i));
      assertTrue(naive.equals(serial.get(i)));
    }
  }

  @Test
  public void PrimeFieldCosetFFTofCosetInverseFFTTest() {
    final int m = 4;
    final Fp FieldFactory = new Fp(1, FpParameters);
    final SerialFFT<Fp> domain = new SerialFFT<>(m, FieldFactory);

    ArrayList<Fp> serial = new ArrayList<>(m);
    serial.add(new Fp(2, FpParameters));
    serial.add(new Fp(5, FpParameters));
    serial.add(new Fp(3, FpParameters));
    serial.add(new Fp(8, FpParameters));

    ArrayList<Fp> original = new ArrayList<>(serial);

    final Fp shift = new Fp(3, FpParameters);

    domain.radix2CosetFFT(serial, shift);
    domain.radix2CosetInverseFFT(serial, shift);

    for (int i = 0; i < m; i++) {
      System.out.println(original.get(i) + " == " + serial.get(i));
      assertTrue(original.get(i).equals(serial.get(i)));
    }
  }
}
