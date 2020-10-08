package io;

import relations.r1cs.R1CSRelation;
import relations.r1cs.R1CSRelationRDD;
import algebra.fields.AbstractFieldElementExpanded;
import configuration.Configuration;
import relations.objects.LinearTerm;
import relations.objects.R1CSConstraintsRDD;
import relations.objects.R1CSConstraints;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.arrow.memory.BaseAllocator.Reservation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.math.BigInteger;

import relations.objects.LinearCombination;
import relations.objects.R1CSConstraint;

/**
 * Class that implements all necessary functions to import and load an R1CS in JSON format
 */
public class JSONR1CSLoader {
    // File to parse and load in memory
    private String filename;

    public JSONR1CSLoader(){};
    public JSONR1CSLoader(String jsonFile){
        this.filename = jsonFile;
    };

    private <FieldT extends AbstractFieldElementExpanded<FieldT>>
    LinearCombination<FieldT> loadLinearCombination(FieldT fieldONE, JSONArray linearComb){
        LinearCombination<FieldT> res = new LinearCombination<FieldT>();
        for (int j = 0; j < linearComb.size(); j++) {
            JSONObject jsonTerm = (JSONObject) linearComb.get(j);
            String valueStr = (String) jsonTerm.get("value");
            // Wire values are exported as hexadecimal strings
            // (we remove the '0x' prefix using `substring`)
            BigInteger value = new BigInteger(valueStr.substring(2), 16);
            // FieldT extends `AbstractFieldElementExpanded` which has `construct` from BigInteger
            FieldT valueField = fieldONE.construct(value);
            res.add(new LinearTerm<FieldT>((Long) jsonTerm.get("index"), valueField));
        }

        return res;
    }

    private <FieldT extends AbstractFieldElementExpanded<FieldT>>
    R1CSConstraint<FieldT> loadConstraint(FieldT fieldONE, JSONObject jsonConstraint){
        JSONObject jsonLinCombs = (JSONObject) jsonConstraint.get("linear_combination");
    
        // Load `linear_combination.A` in memory
        JSONArray jsonLinCombA = (JSONArray) jsonLinCombs.get("A");
        LinearCombination<FieldT> linearCombA = loadLinearCombination(fieldONE, jsonLinCombA);
        System.out.println("linearCombA = " + linearCombA.toString());
    
        // Load `linear_combination.B` in memory
        JSONArray jsonLinCombB = (JSONArray) jsonLinCombs.get("B");
        LinearCombination<FieldT> linearCombB = loadLinearCombination(fieldONE, jsonLinCombB);
        System.out.println("linearCombB = " + linearCombB.toString());
    
        // Load `linear_combination.C` in memory
        JSONArray jsonLinCombC = (JSONArray) jsonLinCombs.get("C");
        LinearCombination<FieldT> linearCombC = loadLinearCombination(fieldONE, jsonLinCombC);
        System.out.println("linearCombC = " + linearCombC.toString());
    
        R1CSConstraint<FieldT> res = new R1CSConstraint<FieldT>(linearCombA, linearCombB, linearCombC);
        return res;
    }

    // Loads the file to a "local" (i.e. non-distributed) R1CS instance
    // Need to pass `fieldONE` as a was to bypass the limitations of java generics.
    // The `construct` function is used to instantiate elements of type FieldT from `fieldONE`
    public <FieldT extends AbstractFieldElementExpanded<FieldT>>
    void loadSerial(FieldT fieldONE){
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
         
        try (FileReader reader = new FileReader(this.filename)) {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonR1CS = (JSONObject) obj;

            // TODO: Retrieve the field characteristic for type safety
            // Once recovered, we can assert that r = FieldT::r to make sure types match
            // Long fieldChar = (Long) jsonR1CS.get("scalar_field_characteristic");
            // assert (fieldChar == FieldT::mod);

            JSONArray jsonConstraintArray = (JSONArray) jsonR1CS.get("constraints");
            R1CSConstraints<FieldT> constraintArray =  new R1CSConstraints<FieldT>();
            for (int i = 0; i < jsonConstraintArray.size(); i++) {
                R1CSConstraint<FieldT> constraint = loadConstraint(fieldONE, (JSONObject) jsonConstraintArray.get(i));
                constraintArray.add(constraint);
            }

            //Long numInputs = (Long) jsonR1CS.get("num_inputs");
            // TODO: Compute numAuxiliay inputs
            //R1CSRelation<FieldT> relation = new R1CSRelation<FieldT>(constraintArray, numInputs, 42/*numAuxiliary*/);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Loads the file to an RDD (i.e. distributed) R1CS instance
    /*
    public R1CSRelationRDD<FieldT extends AbstractFieldElementExpanded<FieldT>> loadRDD(
        final FieldT fieldFactory,
        final Configuration config
    ){
        final R1CSConstraintsRDD<FieldT> loadedConstraints;
        final int loadedNumInputs;
        final long loadedNumAuxiliary;

        final R1CSRelationRDD<FieldT> loadedRelationRDD = new R1CSRelationRDD<FieldT>(loadedConstraints, loadedNumInputs, loadedNumAuxiliary);
        return loadedRelationRDD;
    }
    */
}
