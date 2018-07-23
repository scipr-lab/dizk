/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package common;

import algebra.fields.Fp;
import algebra.fields.fieldparameters.LargeFpParameters;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DistributedNaiveEvaluationTest implements Serializable {
    private transient JavaSparkContext sc;
    private LargeFpParameters FpParameters;

    @Before
    public void setUp() {
        sc = new JavaSparkContext("local", "ZKSparkTestSuite");
        FpParameters = new LargeFpParameters();
    }

    @After
    public void tearDown() {
        sc.stop();
        sc = null;
    }

    @Test
    public void DistributedNaiveTest() {
        final List<Fp> input = new ArrayList<>(4);
        input.add(new Fp(7, FpParameters));
        input.add(new Fp(2, FpParameters));
        input.add(new Fp(5, FpParameters));
        input.add(new Fp(3, FpParameters));

        final JavaPairRDD<Long, Fp> inputRDD = sc.parallelizePairs(Utils.convertToPairs(input));

        final Fp x = new Fp(2, FpParameters);

        final Fp answer = new Fp(55, FpParameters);
        final Fp result = NaiveEvaluation.parallelEvaluatePolynomial(inputRDD, x, 1);

        System.out.println(result + " == " + answer);
        assertTrue(result.equals(answer));
    }
}