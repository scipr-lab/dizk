/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package reductions;

import algebra.fields.Fp;
import algebra.fields.fieldparameters.LargeFpParameters;
import configuration.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import profiler.generation.R1CSConstruction;
import reductions.r1cs_to_qap.R1CStoQAP;
import reductions.r1cs_to_qap.R1CStoQAPRDD;
import relations.objects.Assignment;
import relations.qap.QAPRelation;
import relations.qap.QAPRelationRDD;
import relations.qap.QAPWitness;
import relations.qap.QAPWitnessRDD;
import relations.r1cs.R1CSRelation;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple3;

import java.io.Serializable;

import static org.junit.Assert.assertTrue;

public class R1CStoQAPRDDTest implements Serializable {
    private transient JavaSparkContext sc;
    private Fp fieldFactory;
    private Configuration config;
    private Tuple3<R1CSRelation<Fp>, Assignment<Fp>, Assignment<Fp>> R1CSExample;
    private Tuple3<R1CSRelationRDD<Fp>, Assignment<Fp>, JavaPairRDD<Long, Fp>> R1CSExampleRDD;

    @Before
    public void setUp() {
        sc = new JavaSparkContext("local", "ZKSparkTestSuite");
        fieldFactory = new LargeFpParameters().ONE();

        config = new Configuration(1, 1, 1, 2, sc, StorageLevel.MEMORY_ONLY());

        final int numInputs = 1023;
        final int numConstraints = 1024;
        R1CSExample = R1CSConstruction.serialConstruct(numConstraints, numInputs, fieldFactory, config);
        R1CSExampleRDD = R1CSConstruction
                .parallelConstruct(numConstraints, numInputs, fieldFactory, config);
    }

    @After
    public void tearDown() {
        sc.stop();
        sc = null;
    }

    @Test
    public void VerifyQAPRelationTest() {
        final R1CSRelation<Fp> r1cs = R1CSExample._1();
        final R1CSRelationRDD<Fp> r1csRDD = R1CSExampleRDD._1();

        // A quadratic arithmetic program evaluated at t.
        final Fp t = fieldFactory.random(config.seed(), config.secureSeed());
        final QAPRelation<Fp> qap = R1CStoQAP.R1CStoQAPRelation(r1cs, t);
        final QAPRelationRDD<Fp> qapRDD = R1CStoQAPRDD.R1CStoQAPRelation(r1csRDD, t, config);

        // Verify relation parameter sizes.
        assertTrue(qap.numInputs() == qapRDD.numInputs());
        assertTrue(qap.numVariables() == qapRDD.numVariables());
        assertTrue(qap.degree() == qapRDD.degree());

        // Verify vanishing evaluation Z(t).
        assertTrue(qap.Zt().equals(qapRDD.Zt()));

        // Verify evaluation A(t).
        qapRDD.At().foreach(elem -> {
            System.out.println("A(" + elem._1 + "): " + qap.At(elem._1.intValue()) + " == " + elem._2);
            assertTrue(qap.At(elem._1.intValue()).equals(elem._2));
        });

        // Verify evaluation B(t).
        qapRDD.Bt().foreach(elem -> {
            System.out.println("B(" + elem._1 + "): " + qap.Bt(elem._1.intValue()) + " == " + elem._2);
            assertTrue(qap.Bt(elem._1.intValue()).equals(elem._2));
        });

        // Verify evaluation C(t).
        qapRDD.Ct().foreach(elem -> {
            System.out.println("C(" + elem._1 + "): " + qap.Ct(elem._1.intValue()) + " == " + elem._2);
            assertTrue(qap.Ct(elem._1.intValue()).equals(elem._2));
        });

        // Verify evaluation H(t).
        qapRDD.Ht().foreach(elem -> {
            System.out.println("H(" + elem._1 + "): " + qap.Ht(elem._1.intValue()) + " == " + elem._2);
            assertTrue(qap.Ht(elem._1.intValue()).equals(elem._2));
        });
    }

    @Test
    public void VerifyQAPWitnessTest() {
        final R1CSRelation<Fp> r1cs = R1CSExample._1();
        final Assignment<Fp> primary = R1CSExample._2();
        final Assignment<Fp> auxiliary = R1CSExample._3();

        final R1CSRelationRDD<Fp> r1csRDD = R1CSExampleRDD._1();
        final Assignment<Fp> primaryTwo = R1CSExampleRDD._2();
        final JavaPairRDD<Long, Fp> fullAssignmentRDD = R1CSExampleRDD._3();

        final QAPWitness<Fp> qapWitness = R1CStoQAP
                .R1CStoQAPWitness(r1cs, primary, auxiliary, fieldFactory, config);
        final QAPWitnessRDD<Fp> qapWitnessRDD = R1CStoQAPRDD
                .R1CStoQAPWitness(r1csRDD, primaryTwo, fullAssignmentRDD, fieldFactory, config);

        // Verify relation parameter sizes.
        assertTrue(qapWitness.numInputs() == qapWitnessRDD.numInputs());
        assertTrue(qapWitness.numVariables() == qapWitnessRDD.numVariables());
        assertTrue(qapWitness.degree() == qapWitnessRDD.degree());

        // Verify coefficientsABC and coefficientsH size matches.
        assertTrue(qapWitness.coefficientsABC().size() == qapWitnessRDD.coefficientsABC().count());
        assertTrue(qapWitness.coefficientsH().size() == qapWitnessRDD.coefficientsH().count());

        // Verify assignment matches.
        qapWitnessRDD.coefficientsABC().foreach(term -> {
            System.out.println(qapWitness.coefficientsABC().get(term._1.intValue()) + " == " + term._2);
            assertTrue(qapWitness.coefficientsABC().get(term._1.intValue()).equals(term._2));
        });

        // Verify coefficientsH match.
        qapWitnessRDD.coefficientsH().foreach(term -> {
            System.out.println(qapWitness.coefficientsH().get(term._1.intValue()) + " == " + term._2);
            assertTrue(qapWitness.coefficientsH().get(term._1.intValue()).equals(term._2));
        });
    }
}
