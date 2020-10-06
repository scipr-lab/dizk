package io;

import relations.r1cs.R1CSRelation;
import relations.r1cs.R1CSRelationRDD;
import algebra.fields.AbstractFieldElementExpanded;
import configuration.Configuration;
import relations.objects.LinearTerm;
import relations.objects.R1CSConstraintsRDD;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class that implements all necessary functions to import and load an R1CS in JSON format
 */
public class JSONR1CSLoader {
    private String filename;

    public JSONR1CSLoader(){};
    public JSONR1CSLoader(String jsonFile){
        this.filename = jsonFile;
    };

    // Loads the file to a "local" (i.e. non-distributed) R1CS instance
    public void loadSerial(){
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
         
        try (FileReader reader = new FileReader(this.filename)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONObject r1cs = (JSONObject) obj;
            System.out.println(r1cs);

            JSONObject constraints = (JSONObject) r1cs.get("constraints");
            System.out.println(constraints);

            JSONObject numInputs = (JSONObject) r1cs.get("num_inputs");
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
