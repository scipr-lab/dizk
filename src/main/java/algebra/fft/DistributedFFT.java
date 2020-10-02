package algebra.fft;

import algebra.fields.AbstractFieldElementExpanded;
import common.MathUtils;
import configuration.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

public class DistributedFFT {

  /** Compute the distributed FFT, over the domain S, of the vector input. */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      JavaPairRDD<Long, FieldT> radix2FFT(
          final JavaPairRDD<Long, FieldT> input,
          final long rows,
          final long columns,
          final FieldT fieldFactory) {
    return FFTAuxiliary.distributedRadix2FFT(input, rows, columns, false, fieldFactory);
  }

  /** Compute the distributed inverse FFT, over the domain S, of the vector input. */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      JavaPairRDD<Long, FieldT> radix2InverseFFT(
          final JavaPairRDD<Long, FieldT> input,
          final long rows,
          final long columns,
          final FieldT fieldFactory) {
    return FFTAuxiliary.distributedRadix2FFT(input, rows, columns, true, fieldFactory);
  }

  /** Compute the distributed FFT, over the domain g*S, of the vector input. */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      JavaPairRDD<Long, FieldT> radix2CosetFFT(
          final JavaPairRDD<Long, FieldT> input,
          final FieldT g,
          final long rows,
          final long columns,
          final FieldT fieldFactory) {
    return radix2FFT(
        FFTAuxiliary.distributedMultiplyByCoset(input, g), rows, columns, fieldFactory);
  }

  /** Compute the distributed inverse FFT, over the domain g*S, of the vector input. */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      JavaPairRDD<Long, FieldT> radix2CosetInverseFFT(
          final JavaPairRDD<Long, FieldT> input,
          final FieldT g,
          final long rows,
          final long columns,
          final FieldT fieldFactory) {
    return FFTAuxiliary.distributedMultiplyByCoset(
        radix2InverseFFT(input, rows, columns, fieldFactory), g.inverse());
  }

  /**
   * Evaluate all Lagrange polynomials.
   *
   * <p>The inputs are: - an integer m - an element t The output is a vector (b_{0},...,b_{m-1})
   * where b_{i} is the evaluation of L_{i,S}(z) at z = t.
   */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      JavaPairRDD<Long, FieldT> lagrangeCoeffs(
          final FieldT t, final long size, final Configuration config) {
    final long radix2Size = MathUtils.lowestPowerOfTwo(size);
    return FFTAuxiliary.distributedRadix2LagrangeCoefficients(t, radix2Size, config);
  }

  /** Evaluate the vanishing polynomial of S at the field element t. */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>> FieldT computeZ(
      final FieldT t, final long size) {
    final long radix2Size = MathUtils.lowestPowerOfTwo(size);
    final FieldT one = t.one();
    return t.pow(radix2Size).sub(one);
  }

  /**
   * Add the coefficients of the vanishing polynomial of S to the coefficients of the polynomial H.
   *
   * @param H: H.count() is expected to equal (size + 1). For Spark performance reasons, this is not
   *     explicitly checked here.
   */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      JavaPairRDD<Long, FieldT> addPolyZ(
          final FieldT coeff, final JavaPairRDD<Long, FieldT> H, final long size) {
    return H.mapToPair(
        element -> {
          if (element._1 == 0) {
            return new Tuple2<>(element._1, element._2.sub(coeff));
          } else if (element._1 == size) {
            return new Tuple2<>(element._1, element._2.add(coeff));
          } else {
            return element;
          }
        });
  }

  /**
   * Multiply by the evaluation, on a coset of S, of the inverse of the vanishing polynomial of S.
   */
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      JavaPairRDD<Long, FieldT> divideByZOnCoset(
          final FieldT coset, final JavaPairRDD<Long, FieldT> input, final long size) {
    final FieldT inverseZCoset = computeZ(coset, size).inverse();
    return input.mapValues(element -> element.mul(inverseZCoset));
  }
}
