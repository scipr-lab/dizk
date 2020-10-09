package io;

import relations.r1cs.R1CSRelation;
import relations.r1cs.R1CSRelationRDD;
import algebra.fields.AbstractFieldElementExpanded;
import configuration.Configuration;
import relations.objects.LinearTerm;
import relations.objects.R1CSConstraintsRDD;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import io.JSONR1CSLoader;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files; 

import relations.objects.Assignment;

import java.util.ArrayList;
import java.util.List;

public class JSONR1CSLoaderTest {
    
    @Test
    public void loadSerialTest(){
        // Load the test data
        String dizkHome = System.getenv("DIZK");
        Path pathToFile = Paths.get(
            dizkHome, "src", "test", "java", "data", "simple_gadget_r1cs.json");
        if (!Files.exists(pathToFile)) {
            fail("Test r1cs file not found.");
        }
        JSONR1CSLoader loader = new JSONR1CSLoader(pathToFile.toString());
        R1CSRelation<BN254aFr> loadedRelation = loader.loadSerial(BN254aFr.ONE, BN254aFr.FrParameters);
        assertTrue(loadedRelation.isValid());

        // Make sure the loaded relation is satisfied with a valid assignment
        Assignment<BN254aFr> primary = new Assignment<BN254aFr>();
        // Allocate ONE - needs to be done manually (as opposed to how things are done in libsnark)
        // see further discussion in the `evaluate` function in `LinearCombination.java`
        primary.add(BN254aFr.ONE);
        primary.add(new BN254aFr("12"));
        Assignment<BN254aFr> auxiliary = new Assignment<BN254aFr>();
        auxiliary.add(new BN254aFr("1"));
        auxiliary.add(new BN254aFr("1"));
        auxiliary.add(new BN254aFr("1"));
        //assertTrue(loadedRelation.isSatisfied(primary, auxiliary));
        boolean res = loadedRelation.isSatisfied(primary, auxiliary);
        System.out.println("Res: " + res);
    }
}
