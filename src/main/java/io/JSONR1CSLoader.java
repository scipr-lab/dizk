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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.math.BigInteger;

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

            JSONArray jsonConstraints = (JSONArray) jsonR1CS.get("constraints");
            System.out.println(jsonConstraints); // DEBUG ONLY - to remove
            for (int i = 0; i < jsonConstraints.size(); i++) {
                JSONObject constraint = (JSONObject) jsonConstraints.get(i);
                JSONObject linCombs = (JSONObject) constraint.get("linear_combination");
                JSONArray linCombA = (JSONArray) linCombs.get("A");
                ArrayList<LinearTerm<FieldT>> termsA = new ArrayList<LinearTerm<FieldT>>();
                for (int j = 0; j < linCombA.size(); j++) {
                    JSONObject jsonTerm = (JSONObject) linCombA.get(j);
                    String valueStr = (String) jsonTerm.get("value");
                    // Wire values are exported as hexadecimal strings
                    // (we remove the '0x' prefix using `substring`)
                    BigInteger value = new BigInteger(valueStr.substring(2), 16);
                    System.out.println("value = " + value.toString());
                    // FieldT is restricted to extend `AbstractFieldElementExpanded`
                    // which has a constructor from BigInteger
                    FieldT valueField = fieldONE.construct(value);
                    termsA.add(new LinearTerm<FieldT>((Long) jsonTerm.get("index"), valueField));
                }
                System.out.println("termsA = " + termsA.toString());
                JSONArray linCombB = (JSONArray) linCombs.get("B");
                System.out.println("linCombB = " + linCombB.toString());
                JSONArray linCombC = (JSONArray) linCombs.get("C");
                System.out.println("linCombC = " + linCombC.toString());
            }
            //R1CSConstraints<FieldT> constrainsts;

            Long numInputs = (Long) jsonR1CS.get("num_inputs");
            System.out.println(numInputs);
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
