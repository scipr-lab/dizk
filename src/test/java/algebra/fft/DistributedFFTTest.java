/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.fft;

import algebra.fields.ComplexField;
import common.Utils;
import configuration.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DistributedFFTTest implements Serializable {
    private transient JavaSparkContext sc;

    @Before
    public void setUp() {
        sc = new JavaSparkContext("local", "ZKSparkTestSuite");
    }

    @After
    public void tearDown() {
        sc.stop();
        sc = null;
    }

    @Test
    public void VerifyFFTTest() {
        int m = 4;
        int rows = 2;
        int columns = 2;

        final ComplexField fieldFactory = new ComplexField(1);
        final SerialFFT<ComplexField> serialDomain = new SerialFFT<>(m, fieldFactory);

        ArrayList<ComplexField> input = new ArrayList<>(m);
        input.add(new ComplexField(2));
        input.add(new ComplexField(5));
        input.add(new ComplexField(3));
        input.add(new ComplexField(8));

        JavaPairRDD<Long, ComplexField> inputRDD = sc.parallelizePairs(Utils.convertToPairs(input));

        serialDomain.radix2FFT(input);
        inputRDD = DistributedFFT.radix2FFT(inputRDD, rows, columns, fieldFactory);

        System.out.println(input.size() + ", " + inputRDD.count());
        assert (input.size() == inputRDD.count());

        inputRDD.foreach(term -> {
            System.out.println(input.get(term._1.intValue()) + " == " + term._2);
            assertTrue(input.get(term._1.intValue()).equals(term._2));
        });
    }

    @Test
    public void VerifyFFTofInverseFFTTest() {
        int m = 4;
        int rows = 2;
        int columns = 2;

        ArrayList<ComplexField> init = new ArrayList<>(m);
        init.add(new ComplexField(2));
        init.add(new ComplexField(5));
        init.add(new ComplexField(3));
        init.add(new ComplexField(8));

        final ComplexField fieldFactory = new ComplexField(1);

        JavaPairRDD<Long, ComplexField> parallelSpark = sc.parallelizePairs(Utils.convertToPairs(init));

        JavaPairRDD<Long, ComplexField> parallelFFT = DistributedFFT
                .radix2FFT(parallelSpark, rows, columns, fieldFactory);
        JavaPairRDD<Long, ComplexField> parallelFFTofInverseFFT = DistributedFFT
                .radix2InverseFFT(parallelFFT, columns, rows, fieldFactory);
        parallelFFTofInverseFFT.foreachPartition(element -> {
            while (element.hasNext()) {
                Tuple2<Long, ComplexField> elem = element.next();

                final ComplexField originalElement = init.get(elem._1.intValue());
                final ComplexField parallelElement = elem._2;
                System.out.println(
                        elem._2.toString() + ": " + originalElement.toString() + " == " + parallelElement
                                .toString());
                assertTrue(originalElement.equals(parallelElement));
            }
        });
    }

    @Test
    public void VerifyCosetFFTTest() {
        int m = 4;
        int rows = 2;
        int columns = 2;

        final ComplexField fieldFactory = new ComplexField(1);
        final SerialFFT<ComplexField> serialDomain = new SerialFFT<>(m, fieldFactory);

        final ComplexField shift = new ComplexField(3);

        ArrayList<ComplexField> serial = new ArrayList<>(m);
        serial.add(new ComplexField(2));
        serial.add(new ComplexField(5));
        serial.add(new ComplexField(3));
        serial.add(new ComplexField(8));

        JavaPairRDD<Long, ComplexField> parallelSpark = sc
                .parallelizePairs(Utils.convertToPairs(serial));

        serialDomain.radix2CosetFFT(serial, shift);
        JavaPairRDD<Long, ComplexField> parallel = DistributedFFT
                .radix2CosetFFT(parallelSpark, shift, rows, columns, fieldFactory);
        parallel.foreachPartition(element -> {
            while (element.hasNext()) {
                Tuple2<Long, ComplexField> elem = element.next();

                final ComplexField serialElement = serial.get(elem._1.intValue());
                final ComplexField parallelElement = elem._2;
                System.out.println(serialElement.toString() + " == " + parallelElement.toString());
                assertTrue(serialElement.equals(parallelElement));
            }
        });
    }

    @Test
    public void VerifyCosetFFTofCosetInverseFFTTest() {
        int m = 4;
        int rows = 2;
        int columns = 2;

        ArrayList<ComplexField> init = new ArrayList<>(m);
        init.add(new ComplexField(2));
        init.add(new ComplexField(5));
        init.add(new ComplexField(3));
        init.add(new ComplexField(8));

        JavaPairRDD<Long, ComplexField> parallelSpark = sc.parallelizePairs(Utils.convertToPairs(init));

        final ComplexField fieldFactory = new ComplexField(1);
        final ComplexField shift = new ComplexField(3);

        JavaPairRDD<Long, ComplexField> parallelFFT = DistributedFFT
                .radix2CosetFFT(parallelSpark, shift, rows, columns, fieldFactory);
        JavaPairRDD<Long, ComplexField> parallelFFTofInverseFFT = DistributedFFT
                .radix2CosetInverseFFT(parallelFFT, shift, columns, rows, fieldFactory);
        parallelFFTofInverseFFT.foreachPartition(element -> {
            while (element.hasNext()) {
                Tuple2<Long, ComplexField> elem = element.next();

                final ComplexField originalElement = init.get(elem._1.intValue());
                final ComplexField parallelElement = elem._2;
                System.out.println(
                        elem._1.toString() + ": " + originalElement.toString() + " == " + parallelElement
                                .toString());
                assertTrue(originalElement.equals(parallelElement));
            }
        });
    }

    @Test
    public void VerifyLagrangeTest() {
        int m = 8;
        final ComplexField FieldFactory = new ComplexField(1);
        final SerialFFT<ComplexField> serialDomain = new SerialFFT<>(m, FieldFactory);
        final Configuration config = new Configuration(1, 1, 1, 2, sc, StorageLevel.MEMORY_ONLY());

        final ComplexField t = new ComplexField(7);
        final List<ComplexField> lagrange = serialDomain.lagrangeCoefficients(t);
        final JavaPairRDD<Long, ComplexField> lagrangeRDD = DistributedFFT.lagrangeCoeffs(t, m, config);

        assert (lagrange.size() == lagrangeRDD.count());

        lagrangeRDD.foreach(coeff -> {
            System.out.println(lagrange.get(coeff._1.intValue()) + " == " + coeff._2);
            assertTrue(lagrange.get(coeff._1.intValue()).equals(coeff._2));
        });
    }

    @Test
    public void verifyComputeZTest() {
        int m = 4;
        final ComplexField FieldFactory = new ComplexField(1);
        final SerialFFT<ComplexField> serialDomain = new SerialFFT<>(m, FieldFactory);

        final ComplexField t = new ComplexField(7);
        final ComplexField answerZ = serialDomain.computeZ(t);
        final ComplexField Z = DistributedFFT.computeZ(t, m);

        System.out.println(answerZ + " == " + Z);
        assertTrue(answerZ.equals(Z));
    }

    @Test
    public void verifyAddPolyZTest() {
        int m = 4;
        final ComplexField FieldFactory = new ComplexField(1);
        final SerialFFT<ComplexField> serialDomain = new SerialFFT<>(m, FieldFactory);

        ArrayList<ComplexField> input = new ArrayList<>(m + 1);
        input.add(new ComplexField(2));
        input.add(new ComplexField(5));
        input.add(new ComplexField(3));
        input.add(new ComplexField(8));
        input.add(new ComplexField(13));

        JavaPairRDD<Long, ComplexField> inputRDD = sc.parallelizePairs(Utils.convertToPairs(input));

        final ComplexField t = new ComplexField(7);

        serialDomain.addPolyZ(t, input);
        inputRDD = DistributedFFT.addPolyZ(t, inputRDD, m);

        assert (input.size() == inputRDD.count());

        inputRDD.foreach(coeff -> {
            System.out.println(input.get(coeff._1.intValue()) + " == " + coeff._2);
            assertTrue(input.get(coeff._1.intValue()).equals(coeff._2));
        });
    }

    @Test
    public void verifyDivideByZOnCosetTest() {
        int m = 4;
        final ComplexField FieldFactory = new ComplexField(1);
        final SerialFFT<ComplexField> serialDomain = new SerialFFT<>(m, FieldFactory);

        ArrayList<ComplexField> input = new ArrayList<>(m);
        input.add(new ComplexField(2));
        input.add(new ComplexField(5));
        input.add(new ComplexField(3));
        input.add(new ComplexField(8));

        JavaPairRDD<Long, ComplexField> inputRDD = sc.parallelizePairs(Utils.convertToPairs(input));

        final ComplexField t = new ComplexField(7);

        serialDomain.divideByZOnCoset(t, input);
        inputRDD = DistributedFFT.divideByZOnCoset(t, inputRDD, m);

        assert (input.size() == inputRDD.count());

        inputRDD.foreach(coeff -> {
            System.out.println(input.get(coeff._1.intValue()) + " == " + coeff._2);
            assertTrue(input.get(coeff._1.intValue()).equals(coeff._2));
        });
    }
}