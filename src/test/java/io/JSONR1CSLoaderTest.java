package io;

import relations.r1cs.R1CSRelation;
import relations.r1cs.R1CSRelationRDD;
import algebra.fields.AbstractFieldElementExpanded;
import configuration.Configuration;
import relations.objects.LinearTerm;
import relations.objects.R1CSConstraintsRDD;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFr;
import io.JSONR1CSLoader;

public class JSONR1CSLoaderTest {
    
    @Test
    public void loadSerialTest(){
        // Load the test data file
        // TODO: Use the java equivalent to boost::filesystem to support path manip on all platforms
        String pathToTestFile = "test/java/data/simple_gadget_r1cs.json";
        JSONR1CSLoader loader = new JSONR1CSLoader(pathToTestFile);
        loader.loadSerial();
    }
}
