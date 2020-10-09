package io;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import algebra.fields.AbstractFieldElementExpanded;
import algebra.fields.abstractfieldparameters.AbstractFpParameters;
import relations.objects.LinearCombination;
import relations.objects.LinearTerm;
import relations.objects.R1CSConstraint;
import relations.objects.R1CSConstraints;
import relations.r1cs.R1CSRelation;

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
    
        // Load `linear_combination.B` in memory
        JSONArray jsonLinCombB = (JSONArray) jsonLinCombs.get("B");
        LinearCombination<FieldT> linearCombB = loadLinearCombination(fieldONE, jsonLinCombB);
    
        // Load `linear_combination.C` in memory
        JSONArray jsonLinCombC = (JSONArray) jsonLinCombs.get("C");
        LinearCombination<FieldT> linearCombC = loadLinearCombination(fieldONE, jsonLinCombC);
    
        R1CSConstraint<FieldT> res = new R1CSConstraint<FieldT>(linearCombA, linearCombB, linearCombC);
        return res;
    }

    // Loads the file to a "local" (i.e. non-distributed) R1CS instance
    // Need to pass `fieldONE` as a was to bypass the limitations of java generics.
    // The `construct` function is used to instantiate elements of type FieldT from `fieldONE`
    public <FieldT extends AbstractFieldElementExpanded<FieldT>, FieldParamsT extends AbstractFpParameters>
    R1CSRelation<FieldT> loadSerial(FieldT fieldONE, FieldParamsT fieldParams){
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        R1CSRelation<FieldT> empty = new R1CSRelation<FieldT>(new R1CSConstraints<FieldT>(), 0, 0);
        try (FileReader reader = new FileReader(this.filename)) {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonR1CS = (JSONObject) obj;

            // Retrieve the field characteristic for type safety
            // Once recovered, we assert that r = FieldT::r to make sure types match
            String jsonMod = (String) jsonR1CS.get("scalar_field_characteristic");
            BigInteger mod = new BigInteger(jsonMod.substring(2), 16);
            assert (mod.equals(fieldParams.modulus())) : "Modulus mismatch while loading R1CS";

            JSONArray jsonConstraintArray = (JSONArray) jsonR1CS.get("constraints");
            R1CSConstraints<FieldT> constraintArray =  new R1CSConstraints<FieldT>();
            for (int i = 0; i < jsonConstraintArray.size(); i++) {
                R1CSConstraint<FieldT> constraint = loadConstraint(fieldONE, (JSONObject) jsonConstraintArray.get(i));
                constraintArray.add(constraint);
            }

            // Convert Long to int "safely": an exception is raised if Long overflows the int
            // see: https://docs.oracle.com/javase/10/docs/api/java/lang/Math.html#toIntExact(long)
            // Add +1 to `numInputs` to account for the manual handling of ONE
            int numInputs = Math.toIntExact((Long) jsonR1CS.get("num_inputs")) + 1;
            // Add +1 to `numVariables` to account for the manual handling of ONE
            int numVariables = Math.toIntExact((Long) jsonR1CS.get("num_variables")) + 1;
            int numAuxiliary = numVariables - numInputs;
            R1CSRelation<FieldT> relation = new R1CSRelation<FieldT>(constraintArray, numInputs, numAuxiliary);

            assert relation.isValid(): "Loaded relation is invalid";
            return relation;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Return the empty relation if the body of the `try` threw
        return empty;
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
