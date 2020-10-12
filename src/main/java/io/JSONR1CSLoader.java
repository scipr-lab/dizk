package io;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

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
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple2;
import relations.objects.R1CSConstraintsRDD;

import configuration.Configuration;
import org.apache.spark.api.java.JavaPairRDD;

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

    /// Loads the file to a "local" (i.e. non-distributed) R1CS instance
    /// Need to pass `fieldONE` as a was to bypass the limitations of java generics.
    /// The `construct` function is used to instantiate elements of type FieldT from `fieldONE`
    public <
        FieldT extends AbstractFieldElementExpanded<FieldT>,
        FieldParamsT extends AbstractFpParameters>
    R1CSRelation<FieldT> loadSerial(
        final FieldT fieldONE,
        final FieldParamsT fieldParams
    ){
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        R1CSRelation<FieldT> empty = new R1CSRelation<FieldT>(new R1CSConstraints<FieldT>(), 0, 0);
        // TODO: Mark the function with a `throws` instead of having such a big try/catch
        // i.e. add `throws IOException, ParseException`
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

    /// RDD

    private <FieldT extends AbstractFieldElementExpanded<FieldT>>
    JavaPairRDD<Long, LinearTerm<FieldT>> loadLinearCombinationRDD(
        final FieldT fieldONE,
        final ArrayList<Integer> partitionSet,
        final JSONArray jsonConstraintArray,
        final String linearCombKey,
        final Configuration config
    ){
        final int nbPartitions = config.numPartitions();
        final int nbConstraints = jsonConstraintArray.size();

        // Normally, Spark tries to set the number of partitions automatically based on the cluster.
        // However, it is possible to pass it manually via a second parameter to parallelize
        // (e.g. sc.parallelize(data, 10))
        // See: https://spark.apache.org/docs/3.0.0/rdd-programming-guide.html#parallelized-collections
        //
        // NOTE1: Here "the initial dataset" is just an array of integers (the "partitions" which is
        // an ArrayList<Integer>). This array (e.g. [0,1,2]) is used to build the data in each partitions
        // via the use of a lambda expression in `flatMapToPair` which maps an Integer to the output
        // of the block below (i.e. `return linearCombinationChunk.iterator();`).
        // In other words, for each "partition index" a partition "content" is returned.
        // See: https://www.w3schools.com/java/java_lambda.asp for syntax of java lambda functions
        //
        // NOTE2: The signature of the `flatMapToPair` function is:
        // `flatMapToPair[K2, V2](f: PairFlatMapFunction[T, K2, V2]): JavaPairRDD[K2, V2]`
        // Hence, it returns a JavaRDDPair just as expected.
        return config.sparkContext().parallelize(partitionSet, nbPartitions)
                .flatMapToPair(partitionID -> {
                    // Compute the size of the data set processed by the partition `partitionID`
                    // Note: all partitions will have data sets of the same size, apart from the last one
                    // (if it exists) which will have a dataset made of the remaining entries (i.e. the
                    // cardinality of this set will be < than the one of the other partitions)
                    final long partitionSize = (partitionID == nbPartitions ?
                    nbConstraints % nbPartitions : nbConstraints / nbPartitions);

                    final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> linearCombinationChunk = new ArrayList<>();
                    // Build the partition data set
                    // For each constraints in the data set
                    for (long i = 0; i < partitionSize; i++) {
                        final long constraintIndex = partitionID * (nbConstraints / nbPartitions) + i;

                        // Here `constraintIndex` determines the constraint ID
                        // and `constraintArrayIndex` determines the lin comb of this constraint
                        // Hence, `next` contains the linear combination to parse
                        JSONObject jsonConstraint = (JSONObject) jsonConstraintArray.get((int) constraintIndex);
                        JSONObject jsonLinCombs = (JSONObject) jsonConstraint.get("linear_combination");
                        JSONArray jsonLinComb = (JSONArray) jsonLinCombs.get(linearCombKey);

                        // Parse all LinearTerms of the linear combination of interest
                        for (int j = 0; j < jsonLinComb.size(); j++) {
                            JSONObject jsonTerm = (JSONObject) jsonLinComb.get(j);
                            String valueStr = (String) jsonTerm.get("value");
                            // Wire values are exported as hexadecimal strings
                            // (we remove the '0x' prefix using `substring`)
                            BigInteger value = new BigInteger(valueStr.substring(2), 16);
                            // FieldT extends `AbstractFieldElementExpanded` which has `construct` from BigInteger
                            FieldT valueField = fieldONE.construct(value);
                            linearCombinationChunk.add(new Tuple2<>(constraintIndex, new LinearTerm<FieldT>((Long) jsonTerm.get("index"), valueField)));
                        }
                    }
                    return linearCombinationChunk.iterator();
                });
    }

    // TODO: Spend some time to properly understand the behavior of the JSON library and the file reader
    // we don't want to load all the file in memory, ideally everything should be "streamed".
    // This may lead to several passes on the file (as we need to navigate all constraints) multiple
    // times to load each individual linear combination, but this may be more desirable than reading
    // and keeping an enormous file in memory (e.g. an R1CS of billion gates).
    // Else, refactor the code to replace `loadLinearCombinationsRDD` by `loadConstraintsRDD` and
    // only navigate the constraint set once. 
    
    // Loads the file to an RDD (i.e. distributed) R1CS instance
    public <FieldT extends AbstractFieldElementExpanded<FieldT>, FieldParamsT extends AbstractFpParameters>
    R1CSRelationRDD<FieldT> loadRDD(
        final FieldT fieldONE,
        final FieldParamsT fieldParams,
        final Configuration config
    ) throws FileNotFoundException, IOException, ParseException {
        // JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        FileReader reader = new FileReader(this.filename);
        Object obj = jsonParser.parse(reader);
        JSONObject jsonR1CS = (JSONObject) obj;
        
        // Retrieve the field characteristic for type safety
        // Once recovered, we assert that r = FieldT::r to make sure types match
        String jsonMod = (String) jsonR1CS.get("scalar_field_characteristic");
        BigInteger mod = new BigInteger(jsonMod.substring(2), 16);
        assert (mod.equals(fieldParams.modulus())) : "Modulus mismatch while loading R1CS";
        
        long nbJSONConstraintArray = (Long) jsonR1CS.get("num_constraints");
        final int numPartitions = config.numPartitions();

        // This array of partitions is useful to feed into the lambda function to map
        // a partition ID (an index in [numPartition]) to the corresponding R1CS chunk
        // See in `loadLinearCombinationRDD`.
        //
        // The default config has 2 partitions per RDD.
        // We add "partition 0" and "partition 1" to the array of partitions.
        final ArrayList<Integer> partitions = new ArrayList<>();
        for (int i = 0; i < numPartitions; i++) {
            partitions.add(i);
        }
        // If the number of constraints is not a multiple of `numPartitions`,
        // we add an extra partition to process the remaining part of the dataset.
        // NOTE: This only makes sense when the # constraints > # partitions
        // which will be the case in most (realistic) scenarios
        if (nbJSONConstraintArray % numPartitions != 0) {
            partitions.add(numPartitions);
        }

        // Get a ref to the array of constraints
        JSONArray jsonConstraintArray = (JSONArray) jsonR1CS.get("constraints");

        // Load all LinearCombinations "A"
        JavaPairRDD<Long, LinearTerm<FieldT>> linCombinationA = loadLinearCombinationRDD(
            fieldONE, partitions, jsonConstraintArray, "A", config);

        // Load all LinearCombinations "B"
        JavaPairRDD<Long, LinearTerm<FieldT>> linCombinationB = loadLinearCombinationRDD(
            fieldONE, partitions, jsonConstraintArray, "B", config);

        // Load all LinearCombinations "C"
        JavaPairRDD<Long, LinearTerm<FieldT>> linCombinationC = loadLinearCombinationRDD(
            fieldONE, partitions, jsonConstraintArray, "C", config);

        // Make sure that the loaded data is sound
        // `.count()` returns the number of elements in the RDD.

        // [BEGIN DEBUG BLOCK]
        // Be careful, these operations are very expensive, only do with very small datasets!
        //System.out.println("linCombinationA.groupByKey().count() = " + linCombinationA.groupByKey().count());
        //System.out.println("linCombinationB.groupByKey().count() = " + linCombinationB.groupByKey().count());
        //System.out.println("linCombinationC.groupByKey().count() = " + linCombinationC.groupByKey().count());
        //for(Tuple2<Long,Iterable<LinearTerm<FieldT>>> entry:linCombinationA.groupByKey().collect()){
        //    System.out.println("* " + entry);
        //}
        assert linCombinationA.groupByKey().count() == linCombinationB.groupByKey().count();
        assert linCombinationB.groupByKey().count() == linCombinationC.groupByKey().count();
        assert linCombinationC.groupByKey().count() == nbJSONConstraintArray;
        // [END DEBUG BLOCK]
        
        final R1CSConstraintsRDD<FieldT> constraints = new R1CSConstraintsRDD<>(
            linCombinationA, linCombinationB, linCombinationC, nbJSONConstraintArray);
        
        // Convert Long to int "safely": an exception is raised if Long overflows the int
        // see: https://docs.oracle.com/javase/10/docs/api/java/lang/Math.html#toIntExact(long)
        // Add +1 to `numInputs` to account for the manual handling of ONE
        int numInputs = Math.toIntExact((Long) jsonR1CS.get("num_inputs")) + 1;
        // Add +1 to `numVariables` to account for the manual handling of ONE
        int numVariables = Math.toIntExact((Long) jsonR1CS.get("num_variables")) + 1;
        int numAuxiliary = numVariables - numInputs;

        R1CSRelationRDD<FieldT> relation = new R1CSRelationRDD<FieldT>(constraints, numInputs, numAuxiliary);
        assert relation.isValid(): "Loaded relation is invalid";

        return relation;
    }
}
