/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.msm;

import algebra.fields.Fp;
import algebra.fields.fieldparameters.LargeFpParameters;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq;
import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import algebra.curves.barreto_naehrig.bn254a.BN254aG1;
import algebra.curves.barreto_naehrig.bn254a.BN254aG2;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import algebra.groups.AdditiveIntegerGroup;
import algebra.groups.integergroupparameters.LargeAdditiveIntegerGroupParameters;
import common.Utils;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DistributedFixedBaseMSMTest implements Serializable {
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
    public void DistributedMSMTest() {
        final LargeAdditiveIntegerGroupParameters GroupParameters =
                new LargeAdditiveIntegerGroupParameters();
        final LargeFpParameters FpParameters = new LargeFpParameters();

        final int scalarSize = 4;
        final int windowSize = 2;
        final AdditiveIntegerGroup base = new AdditiveIntegerGroup(7, GroupParameters);
        final List<List<AdditiveIntegerGroup>> multiplesOfBase = FixedBaseMSM
                .getWindowTable(base, scalarSize, windowSize);

        final ArrayList<Fp> scalars = new ArrayList<>(4);
        scalars.add(new Fp("3", FpParameters));
        scalars.add(new Fp("11", FpParameters));
        scalars.add(new Fp("2", FpParameters));
        scalars.add(new Fp("8", FpParameters));

        final List<Tuple2<Long, AdditiveIntegerGroup>> result = FixedBaseMSM.distributedBatchMSM(
                scalarSize,
                windowSize,
                multiplesOfBase,
                sc.parallelizePairs(Utils.convertToPairs(scalars)),
                sc).collect();

        final ArrayList<AdditiveIntegerGroup> answers = new ArrayList<>(4);
        answers.add(new AdditiveIntegerGroup(21, GroupParameters));
        answers.add(new AdditiveIntegerGroup(77, GroupParameters));
        answers.add(new AdditiveIntegerGroup(14, GroupParameters));
        answers.add(new AdditiveIntegerGroup(56, GroupParameters));

        for (Tuple2<Long, AdditiveIntegerGroup> e : result) {
            final AdditiveIntegerGroup answer = answers.get(e._1.intValue());

            System.out.println(e._2 + " == " + answer);
            assert (e._2.equals(answer));
        }
    }

    @Test
    public void DistributedDoubleMSMTest() {
        final BN254aG1 base1 = BN254aG1Parameters.ONE.dbl();
        final BN254aG2 base2 = BN254aG2Parameters.ONE.dbl();

        final int scalarSize = 4;
        final int windowSize = 2;
        final List<List<BN254aG1>> multiplesOfBase1 = FixedBaseMSM
                .getWindowTable(base1, scalarSize, windowSize);
        final List<List<BN254aG2>> multiplesOfBase2 = FixedBaseMSM
                .getWindowTable(base2, scalarSize, windowSize);

        final ArrayList<BN254aFr> scalars = new ArrayList<>(4);
        scalars.add(new BN254aFr("3"));
        scalars.add(new BN254aFr("11"));
        scalars.add(new BN254aFr("2"));
        scalars.add(new BN254aFr("8"));

        final List<Tuple2<Long, Tuple2<BN254aG1, BN254aG2>>> result = FixedBaseMSM
                .distributedDoubleBatchMSM(
                        scalarSize,
                        windowSize,
                        multiplesOfBase1,
                        scalarSize,
                        windowSize,
                        multiplesOfBase2,
                        sc.parallelizePairs(Utils.convertToPairs(scalars)),
                        sc).collect();

        final ArrayList<Tuple2<BN254aG1, BN254aG2>> answers = new ArrayList<>(4);
        for (BN254aFr scalar : scalars) {
            final BN254aG1 msm1 = base1.mul(scalar);
            final BN254aG2 msm2 = base2.mul(scalar);
            answers.add(new Tuple2<>(msm1, msm2));
        }

        for (Tuple2<Long, Tuple2<BN254aG1, BN254aG2>> e : result) {
            final Tuple2<BN254aG1, BN254aG2> answer = answers.get(e._1.intValue());

            System.out.println(e._2._1 + " == " + answer._1);
            System.out.println(e._2._2 + " == " + answer._2);
            System.out.println(BN254aFq.FqParameters.modulus());
            System.out.println(e._2._1.Z.element().FpParameters.modulus());
            assert (e._2._1.equals(answer._1));
            assert (e._2._2.equals(answer._2));
        }
    }
}