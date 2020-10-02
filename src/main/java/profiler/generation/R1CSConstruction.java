/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package profiler.generation;

import algebra.fields.AbstractFieldElementExpanded;
import common.Utils;
import configuration.Configuration;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.spark.api.java.JavaPairRDD;
import relations.objects.*;
import relations.r1cs.R1CSRelation;
import relations.r1cs.R1CSRelationRDD;
import scala.Tuple2;
import scala.Tuple3;

public class R1CSConstruction implements Serializable {

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Tuple3<R1CSRelation<FieldT>, Assignment<FieldT>, Assignment<FieldT>> serialConstruct(
          final int numConstraints,
          final int numInputs,
          final FieldT fieldFactory,
          final Configuration config) {
    assert (numInputs <= numConstraints + 1);

    final int numAuxiliary = 3 + numConstraints - numInputs;
    final int numVariables = numInputs + numAuxiliary;

    final FieldT one = fieldFactory.one();
    FieldT a = fieldFactory.random(config.seed(), config.secureSeed());
    FieldT b = fieldFactory.random(config.seed(), config.secureSeed());

    final Assignment<FieldT> oneFullAssignment = new Assignment<>();
    oneFullAssignment.add(one);
    oneFullAssignment.add(a);
    oneFullAssignment.add(b);

    final R1CSConstraints<FieldT> constraints = new R1CSConstraints<>();
    for (int i = 0; i < numConstraints - 1; i++) {
      final LinearCombination<FieldT> A = new LinearCombination<>();
      final LinearCombination<FieldT> B = new LinearCombination<>();
      final LinearCombination<FieldT> C = new LinearCombination<>();

      if (i % 2 != 0) {
        // a * b = c.
        A.add(new LinearTerm<>((long) i + 1, one));
        B.add(new LinearTerm<>((long) i + 2, one));
        C.add(new LinearTerm<>((long) i + 3, one));

        final FieldT tmp = a.mul(b);
        a = b;
        b = tmp;
        oneFullAssignment.add(tmp);
      } else {
        // a + b = c
        A.add(new LinearTerm<>((long) i + 1, one));
        A.add(new LinearTerm<>((long) i + 2, one));
        B.add(new LinearTerm<>((long) 0, one));
        C.add(new LinearTerm<>((long) i + 3, one));

        final FieldT tmp = a.add(b);
        a = b;
        b = tmp;
        oneFullAssignment.add(tmp);
      }

      constraints.add(new R1CSConstraint<>(A, B, C));
    }

    final LinearCombination<FieldT> A = new LinearCombination<>();
    final LinearCombination<FieldT> B = new LinearCombination<>();
    final LinearCombination<FieldT> C = new LinearCombination<>();

    FieldT res = fieldFactory.zero();
    for (int i = 1; i < numVariables - 1; i++) {
      A.add(new LinearTerm<>((long) i, one));
      B.add(new LinearTerm<>((long) i, one));

      res = res.add(oneFullAssignment.get(i));
    }
    C.add(new LinearTerm<>((long) numVariables - 1, one));
    oneFullAssignment.add(res.square());

    constraints.add(new R1CSConstraint<>(A, B, C));

    final R1CSRelation<FieldT> r1cs = new R1CSRelation<>(constraints, numInputs, numAuxiliary);
    final Assignment<FieldT> primary = new Assignment<>(oneFullAssignment.subList(0, numInputs));
    final Assignment<FieldT> auxiliary =
        new Assignment<>(oneFullAssignment.subList(numInputs, oneFullAssignment.size()));

    assert (r1cs.numInputs() == numInputs);
    assert (r1cs.numVariables() >= numInputs);
    assert (r1cs.numVariables() == oneFullAssignment.size());
    assert (r1cs.numConstraints() == numConstraints);
    assert (r1cs.isSatisfied(primary, auxiliary));

    return new Tuple3<>(r1cs, primary, auxiliary);
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Tuple3<R1CSRelationRDD<FieldT>, Assignment<FieldT>, JavaPairRDD<Long, FieldT>>
          parallelConstruct(
              final long numConstraints,
              final int numInputs,
              final FieldT fieldFactory,
              final Configuration config) {
    assert (numInputs <= numConstraints + 1);

    final long numAuxiliary = 3 + numConstraints - numInputs;
    final long numVariables = numInputs + numAuxiliary;

    final FieldT one = fieldFactory.one();

    final long totalSize = numConstraints - 1;
    final int numPartitions = config.numPartitions();
    final ArrayList<Integer> partitions = new ArrayList<>();
    for (int i = 0; i < numPartitions; i++) {
      partitions.add(i);
    }
    if (totalSize % 2 != 0) {
      partitions.add(numPartitions);
    }

    JavaPairRDD<Long, LinearTerm<FieldT>> linearCombinationA =
        config
            .sparkContext()
            .parallelize(partitions, numPartitions)
            .flatMapToPair(
                part -> {
                  final long partSize =
                      part == numPartitions
                          ? totalSize % (totalSize / numPartitions)
                          : totalSize / numPartitions;

                  final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> A = new ArrayList<>();
                  for (long i = 0; i < partSize; i++) {
                    final long index = part * (totalSize / numPartitions) + i;
                    if (index % 2 != 0) {
                      // a * b = c
                      A.add(new Tuple2<>(index, new LinearTerm<>(index + 1, one)));
                    } else {
                      // a + b = c
                      A.add(new Tuple2<>(index, new LinearTerm<>(index + 1, one)));
                      A.add(new Tuple2<>(index, new LinearTerm<>(index + 2, one)));
                    }
                  }
                  return A.iterator();
                });
    JavaPairRDD<Long, LinearTerm<FieldT>> linearCombinationB =
        config
            .sparkContext()
            .parallelize(partitions, numPartitions)
            .flatMapToPair(
                part -> {
                  final long partSize =
                      part == numPartitions
                          ? totalSize % (totalSize / numPartitions)
                          : totalSize / numPartitions;

                  final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> B = new ArrayList<>();
                  for (long i = 0; i < partSize; i++) {
                    final long index = part * (totalSize / numPartitions) + i;
                    if (index % 2 != 0) {
                      // a * b = c
                      B.add(new Tuple2<>(index, new LinearTerm<>(index + 2, one)));
                    } else {
                      // a + b = c
                      B.add(new Tuple2<>(index, new LinearTerm<>(0, one)));
                    }
                  }
                  return B.iterator();
                });
    JavaPairRDD<Long, LinearTerm<FieldT>> linearCombinationC =
        config
            .sparkContext()
            .parallelize(partitions, numPartitions)
            .flatMapToPair(
                part -> {
                  final long partSize =
                      part == numPartitions
                          ? totalSize % (totalSize / numPartitions)
                          : totalSize / numPartitions;

                  final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> C = new ArrayList<>();
                  for (long i = 0; i < partSize; i++) {
                    final long index = part * (totalSize / numPartitions) + i;
                    if (index % 2 != 0) {
                      // a * b = c
                      C.add(new Tuple2<>(index, new LinearTerm<>(index + 3, one)));
                    } else {
                      // a + b = c
                      C.add(new Tuple2<>(index, new LinearTerm<>(index + 3, one)));
                    }
                  }
                  return C.iterator();
                });

    final long totalVariableSize = numVariables - 2;
    final ArrayList<Integer> variablePartitions = new ArrayList<>();
    for (int i = 0; i < numPartitions; i++) {
      variablePartitions.add(i);
    }
    if (totalSize % 2 != 0) {
      variablePartitions.add(numPartitions);
    }

    linearCombinationA =
        config
            .sparkContext()
            .parallelize(variablePartitions, numPartitions)
            .flatMapToPair(
                part -> {
                  final long partSize =
                      part == numPartitions
                          ? totalVariableSize % (totalVariableSize / numPartitions)
                          : totalVariableSize / numPartitions;

                  final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> A = new ArrayList<>();
                  for (long i = 0; i < partSize; i++) {
                    final long index = part * (totalVariableSize / numPartitions) + i;
                    A.add(new Tuple2<>(numConstraints - 1, new LinearTerm<>(index + 1, one)));
                  }
                  return A.iterator();
                })
            .union(linearCombinationA)
            .persist(config.storageLevel());
    linearCombinationB =
        config
            .sparkContext()
            .parallelize(variablePartitions, numPartitions)
            .flatMapToPair(
                part -> {
                  final long partSize =
                      part == numPartitions
                          ? totalVariableSize % (totalVariableSize / numPartitions)
                          : totalVariableSize / numPartitions;

                  final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> B = new ArrayList<>();
                  for (long i = 0; i < partSize; i++) {
                    final long index = part * (totalVariableSize / numPartitions) + i;
                    B.add(new Tuple2<>(numConstraints - 1, new LinearTerm<>(index + 1, one)));
                  }
                  return B.iterator();
                })
            .union(linearCombinationB)
            .persist(config.storageLevel());
    linearCombinationC =
        linearCombinationC
            .union(
                config
                    .sparkContext()
                    .parallelizePairs(
                        Collections.singletonList(
                            new Tuple2<>(
                                numConstraints - 1, new LinearTerm<>(numVariables - 1, one)))))
            .persist(config.storageLevel());

    final FieldT a = fieldFactory.random(config.seed(), config.secureSeed());
    final FieldT b = fieldFactory.random(config.seed(), config.secureSeed());

    final int numExecutors = config.numExecutors();
    final ArrayList<Integer> assignmentPartitions = new ArrayList<>();
    for (int i = 0; i < numExecutors; i++) {
      assignmentPartitions.add(i);
    }
    if (totalSize % 2 != 0) {
      assignmentPartitions.add(numExecutors);
    }
    JavaPairRDD<Long, FieldT> oneFullAssignment =
        config
            .sparkContext()
            .parallelize(assignmentPartitions, numExecutors)
            .flatMapToPair(
                part -> {
                  final ArrayList<Tuple2<Long, FieldT>> assignment = new ArrayList<>();
                  if (part == 0) {
                    assignment.add(new Tuple2<>((long) 0, one));
                    assignment.add(new Tuple2<>((long) 1, a));
                    assignment.add(new Tuple2<>((long) 2, b));
                  }

                  final long startIndex = part * (totalSize / numExecutors);
                  final long partSize =
                      part == numExecutors
                          ? totalSize % (totalSize / numExecutors)
                          : totalSize / numExecutors;

                  FieldT inputA = a;
                  FieldT inputB = b;

                  // If this is the last partition, sum the assignments to get the last assignment.
                  final boolean lastPartition =
                      (totalSize % 2 != 0 && part == numExecutors)
                          || (totalSize % 2 == 0 && part == numExecutors - 1);
                  if (lastPartition) {
                    FieldT res = a.add(b);
                    for (long i = 0; i < startIndex + partSize; i++) {
                      FieldT tmp;
                      if (i % 2 != 0) {
                        // a * b = c
                        tmp = inputA.mul(inputB);
                      } else {
                        // a + b = c
                        tmp = inputA.add(inputB);
                      }

                      if (i >= startIndex) {
                        assignment.add(new Tuple2<>(i + 3, tmp));
                      }

                      res = res.add(tmp);

                      inputA = inputB;
                      inputB = tmp;
                    }

                    assignment.add(new Tuple2<>(numConstraints + 2, res.square()));
                    return assignment.iterator();
                  } else {
                    for (long i = 0; i < startIndex + partSize; i++) {
                      FieldT tmp;
                      if (i % 2 != 0) {
                        // a * b = c
                        tmp = inputA.mul(inputB);
                      } else {
                        // a + b = c
                        tmp = inputA.add(inputB);
                      }

                      if (i >= startIndex) {
                        assignment.add(new Tuple2<>(i + 3, tmp));
                      }

                      inputA = inputB;
                      inputB = tmp;
                    }
                    return assignment.iterator();
                  }
                })
            .persist(config.storageLevel());

    // Compute the primary assignment.
    FieldT serialA = a;
    FieldT serialB = b;
    final Assignment<FieldT> primary = new Assignment<>();
    primary.add(one);
    primary.add(serialA);
    primary.add(serialB);
    for (int i = 4; i <= numInputs; i++) {
      if (i % 2 != 0) {
        // a * b = c
        final FieldT tmp = serialA.mul(serialB);
        serialA = serialB;
        serialB = tmp;
        primary.add(tmp);
      } else {
        // a + b = c
        final FieldT tmp = serialA.add(serialB);
        serialA = serialB;
        serialB = tmp;
        primary.add(tmp);
      }
    }

    // This action will store oneFullAssignment and the linear combinations into persistent storage.
    final long oneFullAssignmentSize = oneFullAssignment.count();
    linearCombinationA.count();
    linearCombinationB.count();
    linearCombinationC.count();

    final R1CSConstraintsRDD<FieldT> constraints =
        new R1CSConstraintsRDD<>(
            linearCombinationA, linearCombinationB, linearCombinationC, numConstraints);

    final R1CSRelationRDD<FieldT> r1cs =
        new R1CSRelationRDD<>(constraints, numInputs, numAuxiliary);

    assert (r1cs.numInputs() == numInputs);
    assert (r1cs.numVariables() >= numInputs);
    assert (r1cs.numVariables() == oneFullAssignmentSize);
    assert (r1cs.numConstraints() == numConstraints);
    assert (r1cs.isSatisfied(primary, oneFullAssignment));

    return new Tuple3<>(r1cs, primary, oneFullAssignment);
  }

  /** Linear algebra applications */

  // Matrix multiplication
  // We have to prove that AB = C
  // For each value in matrix C, we need n2 multiplication gates to calculate the products
  // We also need n2 - 1 summation gates to verify the summation

  public enum ConstraintType {
    constraintA,
    constraintB,
    constraintC
  };

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>> void constraintAssignment(
      ArrayList<Tuple2<Long, LinearTerm<FieldT>>> constraintArray,
      final FieldT fieldFactory,
      long index,
      final long n1,
      final long n2,
      final long n3,
      ConstraintType t) {

    // first, find the correct row/col of C
    final long numConstraintsPerValue = n2 + n2 - 1;
    final long cIndex = index / numConstraintsPerValue;
    final long row = cIndex / n3;
    final long col = cIndex % n3;

    // next, test to see what is the constraint number
    long i = index % numConstraintsPerValue;

    long aOffset = 1;
    long bOffset = aOffset + n1 * n2;
    long cOffset = bOffset + n2 * n3;
    long zOffset = cOffset + n1 * n3;
    long sOffset = zOffset + n1 * n2 * n3;

    final FieldT one = fieldFactory.one();

    if (i < n2) {
      // this is a product gate constraint
      switch (t) {
        case constraintA:
          constraintArray.add(new Tuple2<>(index, new LinearTerm<>(aOffset + row * n2 + i, one)));
          break;
        case constraintB:
          constraintArray.add(new Tuple2<>(index, new LinearTerm<>(bOffset + i * n3 + col, one)));
          break;
        case constraintC:
          constraintArray.add(
              new Tuple2<>(index, new LinearTerm<>(zOffset + (row * n3 + col) * (n2) + i, one)));
          break;
        default:
          System.out.println("[R1CSConstruction.constraintAssignment] unknown constraint");
          break;
      }

    } else {
      // this is a summation gate constraint; we subtract n2 - 1
      i -= (n2);

      if (i == (n2 - 1) || (i == 0 && n2 == 2)) {
        // the last summation
        switch (t) {
          case constraintA:
            if (n2 == 2) {
              constraintArray.add(
                  new Tuple2<>(index, new LinearTerm<>(zOffset + (row * n3 + col) * n2, one)));
              constraintArray.add(
                  new Tuple2<>(index, new LinearTerm<>(zOffset + (row * n3 + col) * n2 + 1, one)));
            } else {
              constraintArray.add(
                  new Tuple2<>(
                      index, new LinearTerm<>(zOffset + (row * n3 + col) * n2 + i + 1, one)));
              constraintArray.add(
                  new Tuple2<>(
                      index, new LinearTerm<>(sOffset + (row * n3 + col) * (n2 - 1) + i - 1, one)));
            }
            break;
          case constraintB:
            constraintArray.add(new Tuple2<>(index, new LinearTerm<>(0, one)));
            break;
          case constraintC:
            constraintArray.add(
                new Tuple2<>(index, new LinearTerm<>(cOffset + row * n3 + col, one)));
            break;
          default:
            System.out.println("[R1CSConstruction.constraintAssignment] unknown constraint");
            break;
        }

      } else if (i == 0 && n2 > 2) {

        switch (t) {
          case constraintA:
            constraintArray.add(
                new Tuple2<>(index, new LinearTerm<>(zOffset + (row * n3 + col) * n2, one)));
            constraintArray.add(
                new Tuple2<>(index, new LinearTerm<>(zOffset + (row * n3 + col) * n2 + 1, one)));
            break;
          case constraintB:
            constraintArray.add(new Tuple2<>(index, new LinearTerm<>(0, one)));
            break;
          case constraintC:
            constraintArray.add(
                new Tuple2<>(index, new LinearTerm<>(sOffset + (row * n3 + col) * (n2 - 1), one)));
            break;
          default:
            System.out.println("[R1CSConstruction.constraintAssignment] unknown constraint");
            break;
        }

      } else {
        switch (t) {
          case constraintA:
            constraintArray.add(
                new Tuple2<>(
                    index, new LinearTerm<>(zOffset + (row * n3 + col) * n2 + i + 1, one)));
            constraintArray.add(
                new Tuple2<>(
                    index, new LinearTerm<>(sOffset + (row * n3 + col) * (n2 - 1) + i - 1, one)));
            break;
          case constraintB:
            constraintArray.add(new Tuple2<>(index, new LinearTerm<>(0, one)));
            break;
          case constraintC:
            constraintArray.add(
                new Tuple2<>(
                    index, new LinearTerm<>(sOffset + (row * n3 + col) * (n2 - 1) + i, one)));
            break;
          default:
            System.out.println("[R1CSConstruction.constraintAssignment] unknown constraint");
            break;
        }
      }
    }
  }

  // For debugging purposes
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>> void printMatrix(
      ArrayList<Tuple2<Long, FieldT>> data, long offset, long row, long col) {
    // The matrix is row x col
    for (long i = 0; i < row; i++) {
      System.out.print("Row " + i + "  ---   ");
      for (long j = 0; j < col; j++) {
        int idx = (int) (offset + i * col + j);
        System.out.print("[" + idx + "]" + data.get((int) idx)._2() + " ");
      }
      System.out.println("");
    }
  }

  // Constructs matrix multiplication constraints
  // We want to verify that AB = C
  // A: n1 by n2
  // B: n2 by n3
  // C: n1 by n3
  // Z are the partial products, and S are the partial sums
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Tuple3<R1CSRelationRDD<FieldT>, Assignment<FieldT>, JavaPairRDD<Long, FieldT>>
          matmulConstruct(
              final int n1,
              final int n2,
              final int n3,
              final FieldT fieldFactory,
              final Configuration config) {

    System.out.println("\t Partitions: " + config.numPartitions());

    final long numConstraints = n1 * n2 * n3 + n1 * n3 + (n2 - 1);
    final int numInputs = n1 * n2 + n2 * n3 + n1 * n3; // A, B, and C
    // the first one is "one"
    final long numAuxiliary = 1 + n1 * n3 * n2 + n1 * n3 * (n2 - 1); // Z and S
    final long numVariables = numInputs + numAuxiliary; // UNUSED

    final FieldT one = fieldFactory.one();

    final long totalSize = numConstraints - 1;
    final int numPartitions = config.numPartitions();
    final ArrayList<Integer> partitions = new ArrayList<>();
    for (int i = 0; i < numPartitions; i++) {
      partitions.add(i);
    }
    if (totalSize % 2 != 0) {
      partitions.add(numPartitions);
    }

    JavaPairRDD<Long, LinearTerm<FieldT>> ALC =
        config
            .sparkContext()
            .parallelize(partitions, numPartitions)
            .flatMapToPair(
                part -> {
                  final long partSize =
                      (part == numPartitions && totalSize % 2 != 0)
                          ? totalSize % (totalSize / numPartitions)
                          : totalSize / numPartitions;

                  final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> A = new ArrayList<>();
                  for (long i = 0; i < partSize; i++) {
                    final long index = part * partSize + i;
                    constraintAssignment(
                        A, fieldFactory, index, n1, n2, n3, ConstraintType.constraintA);
                  }
                  return A.iterator();
                });

    JavaPairRDD<Long, LinearTerm<FieldT>> BLC =
        config
            .sparkContext()
            .parallelize(partitions, numPartitions)
            .flatMapToPair(
                part -> {
                  final long partSize =
                      (part == numPartitions && totalSize % 2 != 0)
                          ? totalSize % (totalSize / numPartitions)
                          : totalSize / numPartitions;

                  final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> B = new ArrayList<>();
                  for (long i = 0; i < partSize; i++) {
                    final long index = part * partSize + i;
                    constraintAssignment(
                        B, fieldFactory, index, n1, n2, n3, ConstraintType.constraintB);
                  }
                  return B.iterator();
                });

    JavaPairRDD<Long, LinearTerm<FieldT>> CLC =
        config
            .sparkContext()
            .parallelize(partitions, numPartitions)
            .flatMapToPair(
                part -> {
                  final long partSize =
                      (part == numPartitions && totalSize % 2 != 0)
                          ? totalSize % (totalSize / numPartitions)
                          : totalSize / numPartitions;

                  final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> C = new ArrayList<>();
                  for (long i = 0; i < partSize; i++) {
                    final long index = part * partSize + i;
                    constraintAssignment(
                        C, fieldFactory, index, n1, n2, n3, ConstraintType.constraintC);
                  }
                  return C.iterator();
                });

    // We're going to construct the assignment serially for now. Probably need to implement
    // block-based matrix multiplication.
    // The first one is to assign A, B
    // The second one is to assign C (C = AB), Z (the products) and S (the sums)

    // A, B assignments
    final long aSize = n1 * n2;
    final long bSize = n2 * n3;
    final long cSize = n1 * n3;
    final long zSize = n1 * n3 * n2;
    final long sSize = n1 * n3 * (n2 - 1);
    final ArrayList<Tuple2<Long, FieldT>> assignment = new ArrayList<>();

    assignment.add(new Tuple2<>((long) 0, one));
    FieldT tmp = one;
    for (long i = 1; i < 1 + aSize + bSize; i++) {
      assignment.add(new Tuple2<>(i, tmp));
      tmp = tmp.add(one);
    }

    // C, Z, S assignments
    final long assignmentSize = n1 * n3 + n1 * n3 * n2 + n1 * n3 * (n2 - 2); // UNUSED

    long startIndex = aSize + bSize + 1;

    // Construct matrix C (n1 x n3)
    for (long i = startIndex; i < startIndex + cSize; i++) {
      long index = i - startIndex;
      long row = index / n3;
      long col = index % n3;
      FieldT result = fieldFactory.zero();
      for (long j = 0; j < n2; j++) {
        // A's row * B's column
        result =
            result.add(
                assignment
                    .get((int) (1 + row * n2 + j))
                    ._2()
                    .mul(assignment.get((int) (1 + n1 * n2 + j * n3 + col))._2()));
      }
      assignment.add(new Tuple2<>(i, result));
    }

    startIndex += cSize;
    // Construct Z, the products
    for (long i = startIndex; i < startIndex + zSize; i++) {
      long index = (i - startIndex) / n2; // Index into C
      long row = index / n3; // row of A
      long col = index % n3; // column of B
      long idx = (i - startIndex) % (n2); // Index into row of A/column of B
      FieldT result =
          assignment
              .get((int) (1 + row * n2 + idx))
              ._2()
              .mul(assignment.get((int) (1 + n1 * n2 + idx * n3 + col))._2());
      // Z stores the products
      assignment.add(new Tuple2<>(i, result));
    }

    startIndex += zSize;
    // Construct S, the sums
    // We can calculate from Z
    long zIndex = 1 + aSize + bSize + cSize;
    for (long i = startIndex; i < startIndex + sSize; i++) {
      long index = (i - startIndex); // Index into Z
      long sIndex = index % (n2 - 1); // logical index into S, for each value in C

      if (sIndex == 0) {
        // want to add 2 values from Z together
        FieldT result =
            assignment.get((int) (zIndex))._2().add(assignment.get((int) (zIndex + 1))._2());
        zIndex += 2;
        assignment.add(new Tuple2<>(i, result));
      } else {
        // want to add 1 value from Z together with 1 value from S
        FieldT result = assignment.get((int) (i - 1))._2().add(assignment.get((int) (zIndex))._2());
        zIndex += 1;
        assignment.add(new Tuple2<>(i, result));
      }
    }

    JavaPairRDD<Long, FieldT> oneFullAssignment =
        JavaPairRDD.fromJavaRDD(config.sparkContext().parallelize(assignment, numPartitions));
    final R1CSConstraintsRDD<FieldT> constraints =
        new R1CSConstraintsRDD<>(ALC, BLC, CLC, numConstraints);
    final R1CSRelationRDD<FieldT> r1cs =
        new R1CSRelationRDD<>(constraints, numInputs, numAuxiliary);

    final Assignment<FieldT> primary =
        new Assignment<>(
            Utils.convertFromPairs(
                oneFullAssignment.filter(e -> e._1 >= 0 && e._1 < numInputs).collect(), numInputs));

    assert (r1cs.numInputs() == numInputs);
    assert (r1cs.numVariables() >= numInputs);
    assert (r1cs.numVariables() == oneFullAssignment.count());
    assert (r1cs.numConstraints() == numConstraints);
    assert (r1cs.isSatisfied(primary, oneFullAssignment));

    return new Tuple3<>(r1cs, primary, oneFullAssignment);
  }

  // Again, we have A, B, and C where AB = C
  // Z = the products
  // S = the sums
  // Let's assume that we have P^2 partitions. We divide C into P by P blocks, where each block is
  // (n1 / p) x (n3 / p).
  // Each block needs rows from A and columns from B in the right range to be correctly calculated.
  // A is n1 by n2, and B is n2 by n3.
  // Each machine stores (n1 / p) x (n2 / p) + (n2 / p) x (n3 / p) and shuffles (n1 / p) x (n2 / p)
  // * p + (n2 / p) x (n3 / p) * p.
  // Total traffic has p blowup, which is sqrt(M), where M = # of machines.
  //
  // Perhaps not very applicable to our setting but: note that within each machine, if the memory is
  // limited, then outer-product is the right way to calculate the C block matrix
  //
  // The functions here are:
  // - matmulParAssignHelper() actually takes in the shuffled blocks and does the matrix
  // multiplication
  // - matmulParAssignShuffleHelper() helps to find the correct partition each item should be
  // shuffled to for block matrix multiplication
  // - matmulParConstraintGen() generates the constraints for each block matrix
  // - matmulParContruct() constructs a matrix multiplication proof in parallel

  // This helper function does the actual matrix multiplication
  // We're going to shuffle a list of rows or columns <partition_number, <index_number, int that
  // shows whether this is A, B, or C, value>>
  // for the integer, 0 = A, 1 = B, 2 = C

  public enum ShufflePattern {
    ShuffleLeft,
    ShuffleRight,
    ShuffleOutput
  };

  public static int getRow(long index, int nRows, int nCols) {
    return ((int) index) / nCols;
  }

  public static int getCol(long index, int nRows, int nCols) {
    return ((int) index) % nCols;
  }

  // Given the partition/block number, the total number of partitions, as well as the matrix
  // dimension
  // return the block row and column boundaries
  public static Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> getPartitionRowsCols(
      long partNumber, int b1, int b2, int nRows, int nCols) {

    int cLowerRows = 0;
    int cHigherRows = 0;
    int cLowerCols = 0;
    int cHigherCols = 0;

    int partRow = ((int) partNumber) / b2;
    int partCol = ((int) partNumber) % b2;

    if (nRows % b1 == 0) {
      cLowerRows = partRow * (nRows / b1);
      cHigherRows = (partRow + 1) * (nRows / b1);
    } else {
      if (partRow < nRows % b1) {
        cLowerRows = partRow * (nRows / b1 + 1);
        cHigherRows = partRow * (nRows / b1 + 1);
      } else {
        int offset = nRows % b1;
        cLowerRows = (offset) * (nRows / b1 + 1) + (partRow - offset) * (nRows / b1);
        cHigherRows = (offset) * (nRows / b1 + 1) + (partRow - offset + 1) * (nRows / b1);
      }
    }

    if (nCols % b2 == 0) {
      cLowerCols = partCol * (nCols / b2);
      cHigherCols = (partCol + 1) * (nCols / b2);
    } else {
      if (partCol < nCols % b2) {
        cLowerCols = partCol * (nCols / b2 + 1);
        cHigherCols = partCol * (nCols / b2 + 1);
      } else {
        int offset = nCols % b2;
        cLowerCols = (offset) * (nCols / b2 + 1) + (partCol - offset) * (nCols / b2);
        cHigherCols = (offset) * (nCols / b2 + 1) + (partCol - offset + 1) * (nCols / b2);
      }
    }

    return new Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>>(
        new Tuple2<Integer, Integer>(cLowerRows, cHigherRows),
        new Tuple2<Integer, Integer>(cLowerCols, cHigherCols));
  }

  public static int getPartNumber(long index, int b1, int b2, int nRows, int nCols) {
    int row = getRow(index, nRows, nCols);
    int col = getCol(index, nRows, nCols);
    int totalPart = b1 * b2;

    // System.out.println("[getPartNumber] row is " + row + ", col is " + col);

    // just iterate through the partitions and find the correct partition number
    for (long partNum = 0; partNum < totalPart; partNum++) {
      Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> t =
          getPartitionRowsCols(partNum, b1, b2, nRows, nCols);
      int cLowerRows = t._1()._1();
      int cHigherRows = t._1()._2();
      int cLowerCols = t._2()._1();
      int cHigherCols = t._2()._2();

      // System.out.println("partNum: " + partNum + ", cLowerRows: " + cLowerRows + ", cHigherRows:
      // " + cHigherRows + ", cLowerCols: " + cLowerCols + ", cHigherCols: " + cHigherCols);
      if (row >= cLowerRows && row < cHigherRows && col >= cLowerCols && col < cHigherCols) {
        // System.out.println("[getPartNumber] partNum is " + partNum);
        return ((int) partNum);
      }
    }

    System.out.println("[getPartNumber] no part number chosen");
    assert (false);
    return 0;
  }

  // These indexer classes are used to translate indexes when we compose the constraints and
  // assignments together
  // This is useful when the assignment indexes are spread out as a result of parallelization
  // getIndex(long index): given the virtual input index, what is the actual index in the assignment
  // RDD
  // getVirtualIndex(): given the real index in the assignment RDD, what is the virtual input index
  public static class Indexer implements Serializable {
    public long getIndex(long index) {
      assert (false);
      return 0;
    }

    public long getVirtualIndex(long index) {
      assert (false);
      return 0;
    }

    public void print() {}
  }
  ;

  public static class LinearIndexer extends Indexer {
    public LinearIndexer(long offset_) {
      offset = offset_;
    }

    public long getIndex(long index) {
      return offset + index;
    }

    public long getVirtualIndex(long index) {
      if (index < offset) {
        return -1;
      }
      return index - offset;
    }

    public void print() {
      System.out.println("[LinearIndexer::print] offset is " + offset);
    }

    public long offset;
  }
  ;

  // This class is used to translate parallelization of block matrices into the inputs of the next
  // In block matrix multiplication, we arrange the assignments to be in the following format:
  // offset | [C11 Z11 S11] [C12 Z12 S12] ...
  // BlockIndexerC assumes that C is the input to the next set of constraints
  public static class BlockIndexerC extends Indexer {
    public BlockIndexerC(long offset_, int n1_, int n2_, int n3_, int b1_, int b2_, int b3_) {
      n1 = n1_;
      n2 = n2_;
      n3 = n3_;
      b1 = b1_;
      b2 = b2_;
      b3 = b3_;
      offset = offset_;
      blockOffsets = new ArrayList<Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>>>();
      // we just go ahead and store all of the offsets
      for (int i = 0; i < b1 * b3; i++) {
        Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> t =
            getPartitionRowsCols(i, b1, b3, n1, n3);
        blockOffsets.add(t);
      }
    }

    public long getIndex(long index) {
      // find the block number; also keep track of the current offsets
      long curOffset = 0;
      long retIndex = 0;
      long row = index / n3;
      long col = index % n3;
      for (int i = 0; i < b1 * b3; i++) {
        Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> t = blockOffsets.get(i);
        int cLowerRows = t._1()._1();
        int cHigherRows = t._1()._2();
        int cLowerCols = t._2()._1();
        int cHigherCols = t._2()._2();

        int c1 = cHigherRows - cLowerRows;
        int c2 = cHigherCols - cLowerCols;

        if (row >= cLowerRows && row < cHigherRows && col >= cLowerCols && col < cHigherCols) {
          retIndex = (row - cLowerRows) * c2 + (col - cLowerCols);
          break;
        } else {
          curOffset += c1 * c2 + c1 * c2 * (n2 + n2 - 1);
        }
      }
      return offset + curOffset + retIndex;
    }

    public long getVirtualIndex(long index) {
      long blockIndex = index - offset;
      long prevOffset = 0;
      long curOffset = 0;

      for (int i = 0; i < b1 * b3; i++) {
        Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> t = blockOffsets.get(i);
        int cLowerRows = t._1()._1();
        int cHigherRows = t._1()._2();
        int cLowerCols = t._2()._1();
        int cHigherCols = t._2()._2();

        int c1 = cHigherRows - cLowerRows;
        int c2 = cHigherCols - cLowerCols;

        prevOffset = curOffset;
        curOffset += c1 * c2 + c1 * c2 * (n2 + n2 - 1);

        // System.out.println("[BlockIndexerC::getVirtualIndex] curOffset is " + curOffset + ",
        // cLowerRows: " + cLowerRows + ", cHigherRows: " + cHigherRows + ", cLowerCols: " +
        // cLowerCols + ", cHigherCols: " + cHigherCols);

        if (blockIndex < curOffset && blockIndex >= prevOffset) {
          if (blockIndex - prevOffset < c1 * c2) {
            // index belongs in block i
            blockIndex -= prevOffset;
            long row = blockIndex / c2 + cLowerRows;
            long col = blockIndex % c2 + cLowerCols;
            return row * n3 + col;
          } else {
            return -1;
          }
        }
      }

      System.out.println("[BlockIndexerC::getVirtualIndex] block not found");
      assert (false);
      return (long) 0;
    }

    public long offset;
    public int n1, n2, n3, b1, b2, b3;
    public ArrayList<Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>>> blockOffsets;
  }
  ;

  public static class TransposeIndexer extends Indexer {
    public TransposeIndexer(long offset_, int n1_, int n2_) {
      offset = offset_;
      n1 = n1_;
      n2 = n2_;
    }

    public long getIndex(long index) {
      long internalIndex = index;
      long row = internalIndex / n1;
      long col = internalIndex % n1;
      // we want to generate the transpose index!
      long transposeIndex = col * n2 + row;
      // System.out.println("[TransposeIndexer::getIndex] offset: " + offset + ", virtual index: " +
      // index + ", real index: " + (offset + transposeIndex));
      return offset + transposeIndex;
    }

    public long getVirtualIndex(long index_) {
      // there is no offsetting needed when it is a virtual index!
      long index = index_ - offset;
      long row = index / n2;
      long col = index % n2;

      long virtualIndex = col * n1 + row;
      return virtualIndex;
    }

    int n1;
    int n2;
    long offset;
  }
  ;

  // Repeat indexers: use this when we need to expand a block of assignments
  // For example, expand a column/row into a matrix
  public static class RowRepeatIndexer extends Indexer {
    public RowRepeatIndexer(long offset_, long size_) {
      offset = offset_;
      size = size_;
    }

    public long getIndex(long index) {
      return (index % size) + offset;
    }

    // We can't get a virtual index, given a physical index :(
    public long getVirtualIndex(long index) {
      assert (false);
      return 0;
    }

    long offset;
    long size;
  }
  ;

  // This class allows you to compose different indexer classes together
  // Lower indexer = lower in the assignment indexes
  public static class CompositeIndexer extends Indexer {
    public CompositeIndexer(ArrayList<Indexer> indexers_) {
      indexers = new ArrayList<Indexer>();
      for (int i = 0; i < indexers_.size(); i++) {
        indexers.add(indexers_.get(i));
      }
    }

    public long getIndex(long index) {
      long curIndex = index;
      for (int i = indexers.size() - 1; i >= 0; i--) {
        curIndex = indexers.get(i).getIndex(curIndex);
      }
      return curIndex;
    }

    public long getVirtualIndex(long index) {
      long curIndex = index;
      for (int i = 0; i < indexers.size(); i++) {
        curIndex = indexers.get(i).getVirtualIndex(curIndex);
      }
      return curIndex;
    }

    ArrayList<Indexer> indexers;
  }
  ;

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Iterator<Tuple2<Long, FieldT>> matmulParAssignHelper(
          final FieldT fieldFactory,
          Long blockNumber,
          Tuple2<Iterable<Tuple2<Long, FieldT>>, Iterable<Tuple2<Long, FieldT>>> input,
          final int n1,
          final int n2,
          final int n3,
          int b1,
          int b2,
          int b3,
          Indexer assignmentOffsetIndexerInput) {

    // Separate the blocks into A and B
    // pre-allocate A and B
    final int numBlockRows = 4; // UNUSED
    final int numBlockCols = 4; // UNUSED

    Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> t =
        getPartitionRowsCols(blockNumber, b1, b3, n1, n3);

    int cLowerRows = t._1()._1();
    int cHigherRows = t._1()._2();
    int cLowerCols = t._2()._1();
    int cHigherCols = t._2()._2();

    // this means we want rows in [cLowerRows, cHigherRows) from A, and columns in [cLowerCols,
    // cHigherCols) from B
    int aSize = (cHigherRows - cLowerRows) * n2;
    int bSize = (cHigherCols - cLowerCols) * n2;
    int cSize = (cHigherCols - cLowerCols) * (cHigherRows - cLowerRows);
    int zSize = (cHigherRows - cLowerRows) * (cHigherCols - cLowerCols) * n2;
    int sSize = (cHigherRows - cLowerRows) * (cHigherCols - cLowerCols) * (n2 - 1);

    // Using the blockNumber and the b's and n's, we can calculate the assignment offset number
    int c1 = cHigherRows - cLowerRows;
    int c2 = cHigherCols - cLowerCols;
    long offset =
        1 + n1 * n2 + n2 * n3 + (c1 * c2 + c1 * c2 * (n2 + n2 - 1)) * blockNumber; // UNUSED
    LinearIndexer assignmentOffsetIndexer =
        new LinearIndexer(
            assignmentOffsetIndexerInput.getIndex(0)
                + (c1 * c2 + c1 * c2 * (n2 + n2 - 1)) * blockNumber);

    final FieldT one = fieldFactory.one();

    ArrayList<FieldT> inputAssignment = new ArrayList<>();
    ArrayList<Tuple2<Long, FieldT>> assignment = new ArrayList<>();

    for (int i = 0; i < aSize + bSize; i++) {
      inputAssignment.add(one);
    }

    for (int i = 0; i < cSize + zSize + sSize; i++) {
      assignment.add(new Tuple2<Long, FieldT>((long) 0, one));
    }

    Iterable<Tuple2<Long, FieldT>> AList = input._1();
    Iterable<Tuple2<Long, FieldT>> BList = input._2();

    // Put all values from A & B in the right place
    for (Tuple2<Long, FieldT> x : AList) {
      int index = (int) (long) x._1();
      FieldT value = x._2();
      int newIndex = index - cLowerRows * n2;
      // System.out.println("[AList] index is " + index + ", newIndex is " + newIndex + ", value is
      // " + value);
      inputAssignment.set(newIndex, value);
    }

    for (Tuple2<Long, FieldT> x : BList) {
      int index = (int) (long) x._1();
      FieldT value = x._2();
      // this is harder to calculate
      int r = index / n3;
      int c = index % n3;
      int newIndex = c * n2 + r - (cLowerCols * n2);
      // System.out.println("[BList] index is " + index + ", r is " + r + ", c is " + c + ",
      // newIndex is " + newIndex  + ", value is " + value);
      inputAssignment.set(aSize + newIndex, value);
    }

    // System.out.println("cSize is " + cSize + ", zSize is " + zSize + ", sSize is " + sSize + ",
    // offset is " + offset);

    // Create the C, Z and S assignments
    for (int c = 0; c < cSize; c++) {
      FieldT result = fieldFactory.zero();
      for (int i = 0; i < n2; i++) {
        int row = c / c2;
        int col = c % c2;
        int aIndex = row * n2 + i;
        int bIndex = aSize + col * n2 + i;
        int zIndex = cSize + c * n2 + i;
        // System.out.println("[C] c " + c + ", row is " + row + ", col is " + col + ", aIndex is "
        // + aIndex + ", bIndex is " + bIndex + ", zIndex is " +
        // assignmentOffsetIndexer.getIndex(zIndex) + ", z value is " +
        // (inputAssignment.get(aIndex).mul(inputAssignment.get(bIndex))) + ", a: " +
        // inputAssignment.get(aIndex) + ", b: " + inputAssignment.get(bIndex));
        assignment.set(
            zIndex,
            new Tuple2<>(
                assignmentOffsetIndexer.getIndex(zIndex),
                // offset + zIndex,
                inputAssignment.get(aIndex).mul(inputAssignment.get(bIndex))));
        result = result.add(assignment.get(zIndex)._2());
        if (i > 0) {
          if (i == 1) {
            int z1Index = cSize + c * n2;
            int z2Index = cSize + c * n2 + 1;
            int sIndex = cSize + zSize + c * (n2 - 1);
            // System.out.println("[C] c " + c + ", z1Index is " + z1Index + ", z2Index is " +
            // z2Index);
            assignment.set(
                sIndex,
                new Tuple2<>(
                    assignmentOffsetIndexer.getIndex(sIndex),
                    // offset + sIndex,
                    assignment.get(z1Index)._2().add(assignment.get(z2Index)._2())));
          } else {
            // int z1Index = cSize + c * n2 + i;
            int s1Index = cSize + zSize + c * (n2 - 1) + i - 2;
            int s2Index = cSize + zSize + c * (n2 - 1) + i - 1;
            // System.out.println("[C] c " + c + ", zIndex is " + zIndex + ", s1Index is " + s1Index
            // + ", s2Index is " + s2Index);
            assignment.set(
                s2Index,
                new Tuple2<>(
                    assignmentOffsetIndexer.getIndex(s2Index),
                    // offset + s2Index,
                    assignment.get(s1Index)._2().add(assignment.get(zIndex)._2())));
          }
        }
      }
      assignment.set(
          c,
          new Tuple2<>(
              assignmentOffsetIndexer.getIndex(c),
              // offset + c,
              result));
    }

    return assignment.iterator();
  }

  // The format of the output should be (shuffling index, (Block row/col number, matrix type [A/B],
  // [list of values in the matrix]))
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Iterator<Tuple2<Long, Tuple2<Long, FieldT>>> matmulParAssignShuffleHelper(
          Tuple2<Long, FieldT> rec,
          int matType,
          final int n1,
          final int n2,
          final int n3,
          final int b1,
          final int b2,
          final int b3) {

    int numBlockRows = b1; // UNUSED
    int numBlockCols = b2; // UNUSED
    // TODO: set the shuffle pattern to be ShuffleOutput for now, which means there is no extra
    // summation step
    ShufflePattern shuffleType = ShufflePattern.ShuffleOutput;

    ArrayList<Tuple2<Long, Tuple2<Long, FieldT>>> data =
        new ArrayList<Tuple2<Long, Tuple2<Long, FieldT>>>();

    // (index, value)
    long index = (long) rec._1();
    FieldT value = rec._2(); // UNUSED

    // Each index should be mapped to a row and a col
    // Blocks are determined by the number of partitions
    // for each row and col, we need to determine the correct partition number
    // then we need to actually shuffle along the block rows
    if (matType == 0) {
      // long row = index / n2;
      // long col = index % n2;
      // long cIndex = row * n2 + col;
      long cIndex = index;
      int partNum = getPartNumber(cIndex, b1, b2, n1, n2);
      int partBlockRow = partNum / b2;
      int partBlockCol = partNum % b2; // UNUSED

      if (shuffleType == ShufflePattern.ShuffleLeft) {
        data.add(new Tuple2<Long, Tuple2<Long, FieldT>>((long) partNum, rec));
      } else {
        for (int i = 0; i < b3; i++) {
          long shufflePartNum = partBlockRow * b3 + i;
          // System.out.println("[A] index is " + cIndex + ", shufflePartnum is " + shufflePartNum);
          data.add(new Tuple2<Long, Tuple2<Long, FieldT>>(shufflePartNum, rec));
        }
      }
    } else {
      // long row = index / n3;
      // long col = index % n3;
      // long cIndex = row * n3 + col;
      long cIndex = index;
      int partNum = getPartNumber(cIndex, b2, b3, n2, n3);
      int partBlockRow = partNum / b3; // UNUSED
      int partBlockCol = partNum % b3;

      if (shuffleType == ShufflePattern.ShuffleRight) {
        data.add(new Tuple2<Long, Tuple2<Long, FieldT>>((long) partNum, rec));
      } else {
        for (int i = 0; i < b1; i++) {
          long shufflePartNum = i * b3 + partBlockCol;
          // System.out.println("[B] index is " + cIndex + ", shufflePartnum is " + shufflePartNum);
          data.add(new Tuple2<Long, Tuple2<Long, FieldT>>(shufflePartNum, rec));
        }
      }
    }

    return data.iterator();
  }

  // This function is very similar to the non-block-based version, except now we take into account
  // that the
  // C, Z, and S variables are numbered based on blocks
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      void matmulParConstraintGenHelper(
          ArrayList<Tuple2<Long, LinearTerm<FieldT>>> constraintArray,
          final FieldT fieldFactory,
          long index,
          final long n1,
          final long n2,
          final long n3,
          final long c1,
          final long c2,
          ConstraintType t,
          long aOffset,
          long bOffset,
          Indexer aOffsetIndexer,
          Indexer bOffsetIndexer,
          Indexer cOffsetIndexer,
          Indexer constraintOffsetIndexer) {

    // first, find the correct row/col of C
    final long numConstraintsPerValue = n2 + n2 - 1;
    final long cIndex = index / numConstraintsPerValue;

    final long row = cIndex / c2;
    final long col = cIndex % c2;

    // next, test to see what is the constraint number
    long i = index % numConstraintsPerValue;

    long cOffset = cOffsetIndexer.getIndex(0);
    long zOffset = cOffset + c1 * c2;
    long sOffset = zOffset + c1 * n2 * c2;

    final FieldT one = fieldFactory.one();

    if (i < n2) {
      // // this is a product gate constraint
      // System.out.println("[Prod gate - constraintA] cIndex: " + cIndex + ", row: " + row + ",
      // col: " + col + ", constraint i is " + constraintOffsetIndexer.getIndex(index));
      // System.out.println("[Prod gate - constraintA] A index: " + aOffsetIndexer.getIndex(aOffset
      // + row * n2 + i));
      // System.out.println("[Prod gate - constraintB] B index: " + bOffsetIndexer.getIndex(bOffset
      // + i * n3 + col));
      // System.out.println("[Prod gate - constraintC] Z index: " + (zOffset + (row * c2 + col) *
      // (n2) + i));

      switch (t) {
        case constraintA:
          constraintArray.add(
              new Tuple2<>(
                  constraintOffsetIndexer.getIndex(index),
                  new LinearTerm<>(aOffsetIndexer.getIndex(aOffset + row * n2 + i), one)));
          break;
        case constraintB:
          constraintArray.add(
              new Tuple2<>(
                  constraintOffsetIndexer.getIndex(index),
                  new LinearTerm<>(bOffsetIndexer.getIndex(bOffset + i * n3 + col), one)));
          break;
        case constraintC:
          constraintArray.add(
              new Tuple2<>(
                  constraintOffsetIndexer.getIndex(index),
                  new LinearTerm<>(zOffset + (row * c2 + col) * n2 + i, one)));
          break;
        default:
          System.out.println("[matmulParConstraintGenHelper] unknown constraint");
          assert (false);
          break;
      }

    } else {
      // this is a summation gate constraint; we subtract n2 - 1
      i -= (n2);

      if (i == (n2 - 1) || (i == 0 && n2 == 2)) {
        // the last summation

        // System.out.println("[Sum gate - constraintA] cIndex: " + cIndex + ", row: " + row + ",
        // col: " + col + ", constraint i is " + (index + constraintOffset));
        // System.out.println("[Sum gate - constraintA] Z1 index: " + (zOffset + (row * c2 + col) *
        // n2));
        // System.out.println("[Sum gate - constraintB] Z2 index: " + (zOffset + (row * c2 + col) *
        // n2 + 1));
        // System.out.println("[Sum gate - constraintC] S index: " + (sOffset + (row * c2 + col) *
        // (n2 - 1) + i - 1));

        switch (t) {
          case constraintA:
            if (n2 == 2) {
              constraintArray.add(
                  new Tuple2<>(
                      constraintOffsetIndexer.getIndex(index),
                      new LinearTerm<>(zOffset + (row * c2 + col) * n2, one)));
              constraintArray.add(
                  new Tuple2<>(
                      constraintOffsetIndexer.getIndex(index),
                      new LinearTerm<>(zOffset + (row * c2 + col) * n2 + 1, one)));
            } else {
              constraintArray.add(
                  new Tuple2<>(
                      constraintOffsetIndexer.getIndex(index),
                      new LinearTerm<>(zOffset + (row * c2 + col) * n2 + i + 1, one)));
              constraintArray.add(
                  new Tuple2<>(
                      constraintOffsetIndexer.getIndex(index),
                      new LinearTerm<>(sOffset + (row * c2 + col) * (n2 - 1) + i - 1, one)));
            }
            break;
          case constraintB:
            constraintArray.add(
                new Tuple2<>(constraintOffsetIndexer.getIndex(index), new LinearTerm<>(0, one)));
            break;
          case constraintC:
            constraintArray.add(
                new Tuple2<>(
                    constraintOffsetIndexer.getIndex(index),
                    new LinearTerm<>(cOffset + row * c2 + col, one)));
            break;
          default:
            System.out.println("[matmulParConstraintGenHelper] unknown constraint");
            assert (false);
            break;
        }

      } else if (i == 0 && n2 > 2) {

        switch (t) {
          case constraintA:
            constraintArray.add(
                new Tuple2<>(
                    constraintOffsetIndexer.getIndex(index),
                    new LinearTerm<>(zOffset + (row * c2 + col) * n2, one)));
            constraintArray.add(
                new Tuple2<>(
                    constraintOffsetIndexer.getIndex(index),
                    new LinearTerm<>(zOffset + (row * c2 + col) * n2 + 1, one)));
            break;
          case constraintB:
            constraintArray.add(
                new Tuple2<>(constraintOffsetIndexer.getIndex(index), new LinearTerm<>(0, one)));
            break;
          case constraintC:
            constraintArray.add(
                new Tuple2<>(
                    constraintOffsetIndexer.getIndex(index),
                    new LinearTerm<>(sOffset + (row * c2 + col) * (n2 - 1), one)));
            break;
          default:
            System.out.println("[matmulParConstraintGenHelper] unknown constraint");
            assert (false);
            break;
        }

      } else {
        // System.out.println("[Sum gate - constraintA] cIndex: " + cIndex + ", row: " + row + ",
        // col: " + col + ", constraint i is " + (index + constraintOffset));
        // System.out.println("[Sum gate - constraintA] Z1 index: " + (zOffset + (row * c2 + col) *
        // n2 + i + 1));
        // System.out.println("[Sum gate - constraintB] S1 index: " + (sOffset + (row * c2 + col) *
        // (n2 - 1) + i - 1));
        // System.out.println("[Sum gate - constraintC] S2 index: " + (sOffset + (row * c2 + col) *
        // (n2 - 1) + i ));

        switch (t) {
          case constraintA:
            constraintArray.add(
                new Tuple2<>(
                    constraintOffsetIndexer.getIndex(index),
                    new LinearTerm<>(zOffset + (row * c2 + col) * n2 + i + 1, one)));
            constraintArray.add(
                new Tuple2<>(
                    constraintOffsetIndexer.getIndex(index),
                    new LinearTerm<>(sOffset + (row * c2 + col) * (n2 - 1) + i - 1, one)));
            break;
          case constraintB:
            constraintArray.add(
                new Tuple2<>(constraintOffsetIndexer.getIndex(index), new LinearTerm<>(0, one)));
            break;
          case constraintC:
            constraintArray.add(
                new Tuple2<>(
                    constraintOffsetIndexer.getIndex(index),
                    new LinearTerm<>(sOffset + (row * c2 + col) * (n2 - 1) + i, one)));
            break;
          default:
            System.out.println("[matmulParConstraintGenHelper] unknown constraint");
            assert (false);
            break;
        }
      }
    }
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Iterator<Tuple2<Long, LinearTerm<FieldT>>> matmulParConstraintGen(
          final FieldT fieldFactory,
          ConstraintType cType,
          Indexer assignmentOffsetA,
          Indexer assignmentOffsetB,
          Indexer assignmentOffsetC,
          Indexer inputConstraintOffset,
          final int n1,
          final int n2,
          final int n3,
          final int b1,
          final int b2,
          final int b3,
          int blockNum) {

    // System.out.println("[matmulParConstraintGen] blockNum is " + blockNum);

    Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> t =
        getPartitionRowsCols(blockNum, b1, b3, n1, n3);
    int cLowerRows = t._1()._1();
    int cHigherRows = t._1()._2();
    int cLowerCols = t._2()._1();
    int cHigherCols = t._2()._2();

    int c1 = cHigherRows - cLowerRows;
    int c2 = cHigherCols - cLowerCols;

    // Calculate the assignment offsets for Z and S
    int numConstraints = (c1 * c2) * (n2 + n2 - 1);
    // int aOffset_ = 1 + cLowerRows * n2;
    // int bOffset_ = 1 + n1 * n2 + cLowerCols;
    // int cOffset_ = 1 + n1 * n2 + n2 * n3 + blockNum * (numConstraints + c1 * c2);

    int aOffset = cLowerRows * n2;
    int bOffset = cLowerCols;

    // assignment indexers
    // LinearIndexer newAssignmentOffsetA = new LinearIndexer(assignmentOffsetA.getIndex(aOffset));
    // LinearIndexer newAssignmentOffsetB = new LinearIndexer(assignmentOffsetB.getIndex(bOffset));
    LinearIndexer newAssignmentOffsetC =
        new LinearIndexer(assignmentOffsetC.getIndex(0) + blockNum * (numConstraints + c1 * c2));

    // constraint indexer
    long offset = inputConstraintOffset.getIndex(blockNum * numConstraints);
    LinearIndexer constraintOffset = new LinearIndexer(offset);

    // System.out.println("[matmulParConstraintGen] constraintOffset is " +
    // constraintOffset.getIndex(0));

    // the sizes of the matrices are c1 x n2, n2 x c2
    final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> constraintList = new ArrayList<>();
    for (long i = 0; i < numConstraints; i++) {
      matmulParConstraintGenHelper(
          constraintList,
          fieldFactory,
          i,
          n1,
          n2,
          n3,
          c1,
          c2,
          cType,
          aOffset,
          bOffset,
          assignmentOffsetA,
          assignmentOffsetB,
          newAssignmentOffsetC,
          constraintOffset);
    }
    return constraintList.iterator();
  }

  // TODO: automatically determine the block dimensions given n1, n2, n3
  // This is a function for generating the assignment and constraints of tmatrix multiplication
  // It takes in the dimensions of the matrices and the number of blocks per dimension
  // It also takes in the actual input matrices A and B
  // Finally, in order to construct the constraints and the assignments, it takes in a constraint
  // offset and an assignment offset (so we can later union the RDDs)
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Tuple3<R1CSConstraintsRDD<FieldT>, JavaPairRDD<Long, FieldT>, Long> matmulParConstruct(
          final Configuration config,
          final FieldT fieldFactory,
          JavaPairRDD<Long, FieldT> A,
          JavaPairRDD<Long, FieldT> B,
          final int b1,
          final int b2,
          final int b3,
          final int n1,
          final int n2,
          final int n3,
          Indexer assignmentOffsetA,
          Indexer assignmentOffsetB,
          Indexer assignmentOffsetOutput,
          Indexer constraintOffset) {

    final long numConstraints = n1 * n2 * n3 + n1 * n3 * (n2 - 1);
    final int numInputs = n1 * n2 + n2 * n3 + n1 * n3; // A, B, and C // UNUSED
    final long numAuxiliary = n1 * n3 * n2 + n1 * n3 * (n2 - 1); // Z and S
    final long numAssignments = n1 * n3 + numAuxiliary;

    // By default, we partition according to the size of the output matrix
    // We want to map each RDD to a partition for block shuffling

    final int numPartitions = config.numPartitions();

    JavaPairRDD<Long, Tuple2<Long, FieldT>> aFullRDD =
        A.<Long, Tuple2<Long, FieldT>>flatMapToPair(
            data ->
                R1CSConstruction.<FieldT>matmulParAssignShuffleHelper(
                    data, 0, n1, n2, n3, b1, b2, b3));

    JavaPairRDD<Long, Tuple2<Long, FieldT>> bFullRDD =
        B.<Long, Tuple2<Long, FieldT>>flatMapToPair(
            data ->
                R1CSConstruction.<FieldT>matmulParAssignShuffleHelper(
                    data, 1, n1, n2, n3, b1, b2, b3));

    // Shuffle to cogroup the tuples together
    // Map each group to do the matrix multiplication locally
    // config.beginLog("[R1CSConstruction] cogroup");
    JavaPairRDD<Long, Tuple2<Iterable<Tuple2<Long, FieldT>>, Iterable<Tuple2<Long, FieldT>>>>
        cogroupResult = aFullRDD.cogroup(bFullRDD);
    // cogroupResult.cache();
    // cogroupResult.count();
    // config.endLog("[R1CSConstruction] cogroup");

    // config.beginLog("[R1CSConstruction] witness calculation");
    JavaPairRDD<Long, FieldT> oneFullAssignment =
        cogroupResult.flatMapToPair(
            x ->
                matmulParAssignHelper(
                    fieldFactory, x._1(), x._2(), n1, n2, n3, b1, b2, b3, assignmentOffsetOutput));

    // long assignmentCount = oneFullAssignment.count();
    // cogroupResult.unpersist();

    // config.endLog("[R1CSConstruction] witness calculation (oneFullAssignment count is " +
    // assignmentCount + ")");
    ArrayList<Integer> intList = new ArrayList<>();
    for (int i = 0; i < b1 * b3; i++) {
      intList.add(i);
    }

    // config.beginLog("[R1CSConstruction] constraint generation");
    // Generate the constraints in parallel
    JavaPairRDD<Long, LinearTerm<FieldT>> ALC =
        config
            .sparkContext()
            .parallelize(intList, numPartitions)
            .flatMapToPair(
                blockNum ->
                    matmulParConstraintGen(
                        fieldFactory,
                        ConstraintType.constraintA,
                        assignmentOffsetA,
                        assignmentOffsetB,
                        assignmentOffsetOutput,
                        constraintOffset,
                        n1,
                        n2,
                        n3,
                        b1,
                        b2,
                        b3,
                        blockNum));

    JavaPairRDD<Long, LinearTerm<FieldT>> BLC =
        config
            .sparkContext()
            .parallelize(intList, numPartitions)
            .flatMapToPair(
                blockNum ->
                    matmulParConstraintGen(
                        fieldFactory,
                        ConstraintType.constraintB,
                        assignmentOffsetA,
                        assignmentOffsetB,
                        assignmentOffsetOutput,
                        constraintOffset,
                        n1,
                        n2,
                        n3,
                        b1,
                        b2,
                        b3,
                        blockNum));

    JavaPairRDD<Long, LinearTerm<FieldT>> CLC =
        config
            .sparkContext()
            .parallelize(intList, numPartitions)
            .flatMapToPair(
                blockNum ->
                    matmulParConstraintGen(
                        fieldFactory,
                        ConstraintType.constraintC,
                        assignmentOffsetA,
                        assignmentOffsetB,
                        assignmentOffsetOutput,
                        constraintOffset,
                        n1,
                        n2,
                        n3,
                        b1,
                        b2,
                        b3,
                        blockNum));

    // ALC.cache().count();
    // BLC.cache().count();
    // CLC.cache().count();

    // config.endLog("[R1CSConstruction] constraint generation");

    // Iterable<Tuple2<Long, FieldT>> assignments = oneFullAssignment.collect();
    // for (Tuple2<Long, FieldT> a : assignments) {
    // 	System.out.println("ASSIGNMENT [" + a._1() + "]: " + a._2());
    // }

    final R1CSConstraintsRDD<FieldT> constraints =
        new R1CSConstraintsRDD<>(ALC, BLC, CLC, numConstraints);

    // final Assignment<FieldT> primary = new Assignment<>(
    //         Utils.convertFromPairs(
    //                 oneFullAssignment.filter(e -> e._1 >= 0 && e._1 < numInputs).collect(),
    //                 numInputs));

    System.out.println("[R1CSConstruction::matmulParConstruct] numConstraints = " + numConstraints);

    return new Tuple3<>(constraints, oneFullAssignment, numAssignments);
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Tuple3<R1CSRelationRDD<FieldT>, Assignment<FieldT>, JavaPairRDD<Long, FieldT>>
          matmulParConstructApp(
              final FieldT fieldFactory,
              final int b1,
              final int b2,
              final int b3,
              final int n1,
              final int n2,
              final int n3,
              final Configuration config) {

    final long numConstraints = n1 * n2 * n3 + n1 * n3 * (n2 - 1);
    final int numInputs = n1 * n2 + n2 * n3 + n1 * n3; // A, B, and C
    // the first one is "one"
    final long numAuxiliary = 1 + n1 * n3 * n2 + n1 * n3 * (n2 - 1); // Z and S
    final long numVariables = numInputs + numAuxiliary;

    final FieldT one = fieldFactory.one();

    final long totalSize = numConstraints - 1;
    final ArrayList<Integer> partitions = new ArrayList<>();
    final int numPartitions = config.numPartitions();
    for (int i = 0; i < numPartitions; i++) {
      partitions.add(i);
    }
    if (totalSize % 2 != 0) {
      partitions.add(numPartitions);
    }

    // Construct A and B matrices
    final ArrayList<Tuple2<Long, FieldT>> A = new ArrayList<>();
    final ArrayList<Tuple2<Long, FieldT>> B = new ArrayList<>();

    long aSize = n1 * n2;
    long bSize = n2 * n3;

    FieldT tmp = one;
    for (long i = 0; i < aSize; i++) {
      A.add(new Tuple2<>(i, tmp));
      tmp = tmp.add(one);
    }

    for (long i = 0; i < bSize; i++) {
      B.add(new Tuple2<>(i, tmp));
      tmp = tmp.add(one);
    }

    LinearIndexer assignmentOffsetA = new LinearIndexer(1);
    LinearIndexer assignmentOffsetB = new LinearIndexer(1 + n1 * n2);
    LinearIndexer assignmentOffsetC = new LinearIndexer(1 + n1 * n2 + n2 * n3);
    LinearIndexer constraintOffset = new LinearIndexer(0);

    JavaPairRDD<Long, FieldT> aRDD =
        JavaPairRDD.fromJavaRDD(config.sparkContext().parallelize(A, numPartitions));
    JavaPairRDD<Long, FieldT> bRDD =
        JavaPairRDD.fromJavaRDD(config.sparkContext().parallelize(B, numPartitions));

    Tuple3<R1CSConstraintsRDD<FieldT>, JavaPairRDD<Long, FieldT>, Long> ret =
        matmulParConstruct(
            config,
            fieldFactory,
            aRDD,
            bRDD,
            b1,
            b2,
            b3,
            n1,
            n2,
            n3,
            assignmentOffsetA,
            assignmentOffsetB,
            assignmentOffsetC,
            constraintOffset);

    final R1CSConstraintsRDD<FieldT> constraints = ret._1();
    JavaPairRDD<Long, FieldT> oneFullAssignment = ret._2();

    JavaPairRDD<Long, FieldT> newARDD =
        aRDD.mapToPair(
            x -> {
              return new Tuple2<Long, FieldT>(x._1() + 1, x._2());
            });

    JavaPairRDD<Long, FieldT> newBRDD =
        bRDD.mapToPair(
            x -> {
              return new Tuple2<Long, FieldT>(x._1() + 1 + n1 * n2, x._2());
            });

    oneFullAssignment = oneFullAssignment.union(newARDD).union(newBRDD);
    oneFullAssignment =
        oneFullAssignment.union(
            config
                .sparkContext()
                .parallelizePairs(Collections.singletonList(new Tuple2<>((long) 0, one))));

    config.beginLog("[matmulApp] oneFullAssignment");
    long numVariables2 = oneFullAssignment.cache().count();
    config.endLog("[matmulApp] oneFullAssignment");

    assert (numVariables2 == numVariables);

    config.beginLog("[matmulApp] constraints generation");
    constraints.A().cache().count();
    constraints.B().cache().count();
    long totalNumConstraints = constraints.C().cache().count();
    config.endLog("[matmulApp] constraints generation");
    System.out.println("[numconstraints: " + totalNumConstraints + "]");

    config.beginLog("[matmulApp] primary generation");
    final Assignment<FieldT> primary =
        new Assignment<>(
            Utils.convertFromPairs(
                oneFullAssignment.filter(e -> e._1 >= 0 && e._1 < numInputs).collect(), numInputs));
    config.endLog("[matmulApp] primary generation");

    final R1CSRelationRDD<FieldT> r1cs =
        new R1CSRelationRDD<>(constraints, numInputs, numAuxiliary);

    return new Tuple3<>(r1cs, primary, oneFullAssignment);
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      R1CSConstraintsRDD<FieldT> equalityConstraintGen(
          final FieldT fieldFactory,
          final Configuration config,
          Indexer xOffset,
          Indexer yOffset,
          Indexer constraintOffset,
          long numVariables) {

    final int numPartitions = config.numPartitions();
    long numConstraints = numVariables;
    final FieldT one = fieldFactory.one();

    ArrayList<Integer> partitions = new ArrayList<Integer>();
    for (int i = 0; i < numPartitions; i++) {
      partitions.add(i);
    }

    // parallelize
    JavaPairRDD<Long, LinearTerm<FieldT>> constraintA =
        config
            .sparkContext()
            .parallelize(partitions, numPartitions)
            .flatMapToPair(
                part -> {
                  final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> A = new ArrayList<>();
                  long partSize =
                      (part < numVariables % numPartitions)
                          ? numVariables / numPartitions + 1
                          : numVariables / numPartitions;
                  long offset = 0;
                  if (numVariables % numPartitions == 0) {
                    offset = (numVariables / numPartitions) * part;
                  } else if (part < numVariables % numPartitions) {
                    offset = (numVariables / numPartitions + 1) * part;
                  } else {
                    offset =
                        (numVariables / numPartitions + 1) * (numVariables % numPartitions)
                            + (numVariables / numPartitions)
                                * (part - numVariables % numPartitions);
                  }
                  for (long i = offset; i < offset + partSize; i++) {
                    A.add(
                        new Tuple2<>(
                            constraintOffset.getIndex(i),
                            new LinearTerm<>(xOffset.getIndex(i), one)));
                  }
                  return A.iterator();
                });

    JavaPairRDD<Long, LinearTerm<FieldT>> constraintB =
        config
            .sparkContext()
            .parallelize(partitions, numPartitions)
            .flatMapToPair(
                part -> {
                  final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> A = new ArrayList<>();
                  long partSize =
                      (part < numVariables % numPartitions)
                          ? numVariables / numPartitions + 1
                          : numVariables / numPartitions;
                  long offset = 0;
                  if (numVariables % numPartitions == 0) {
                    offset = (numVariables / numPartitions) * part;
                  } else if (part < numVariables % numPartitions) {
                    offset = (numVariables / numPartitions + 1) * part;
                  } else {
                    offset =
                        (numVariables / numPartitions + 1) * (numVariables % numPartitions)
                            + (numVariables / numPartitions)
                                * (part - numVariables % numPartitions);
                  }
                  for (long i = offset; i < offset + partSize; i++) {
                    A.add(
                        new Tuple2<>(
                            constraintOffset.getIndex(i), new LinearTerm<>((long) 0, one)));
                  }
                  return A.iterator();
                });

    JavaPairRDD<Long, LinearTerm<FieldT>> constraintC =
        config
            .sparkContext()
            .parallelize(partitions, numPartitions)
            .flatMapToPair(
                part -> {
                  final ArrayList<Tuple2<Long, LinearTerm<FieldT>>> A = new ArrayList<>();
                  long partSize =
                      (part < numVariables % numPartitions)
                          ? numVariables / numPartitions + 1
                          : numVariables / numPartitions;
                  long offset = 0;
                  if (numVariables % numPartitions == 0) {
                    offset = (numVariables / numPartitions) * part;
                  } else if (part < numVariables % numPartitions) {
                    offset = (numVariables / numPartitions + 1) * part;
                  } else {
                    offset =
                        (numVariables / numPartitions + 1) * (numVariables % numPartitions)
                            + (numVariables / numPartitions)
                                * (part - numVariables % numPartitions);
                  }
                  for (long i = offset; i < offset + partSize; i++) {
                    A.add(
                        new Tuple2<>(
                            constraintOffset.getIndex(i),
                            new LinearTerm<>(yOffset.getIndex(i), one)));
                  }
                  return A.iterator();
                });

    final R1CSConstraintsRDD<FieldT> constraints =
        new R1CSConstraintsRDD<FieldT>(constraintA, constraintB, constraintC, numConstraints);

    return constraints;
  }

  // Linear regression
  // Input:
  // X: n sample points, each has d dimensions. Size: n x d
  // w: the weights that the prover has calculated: Size: d x 1
  // y: the output for X. We assume that it's n x 1
  // Proof: we have to verify that (X^T y) = (X^T X) w
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Tuple3<R1CSRelationRDD<FieldT>, Assignment<FieldT>, JavaPairRDD<Long, FieldT>>
          linearRegressionApp(
              final FieldT fieldFactory,
              final Configuration config,
              final int n,
              final int d,
              final int bn,
              final int bd) {

    final int numPartitions = config.numPartitions();
    System.out.println("[linearRegressionApp] numPartitions: " + numPartitions);
    final int numInputs = n * d + d * 1 + n * 1;

    // Construct X, w, y matrices
    final ArrayList<Tuple2<Long, FieldT>> XList = new ArrayList<>();
    final ArrayList<Tuple2<Long, FieldT>> wList = new ArrayList<>();
    final ArrayList<Tuple2<Long, FieldT>> yList = new ArrayList<>();

    final int XSize = n * d;
    final int wSize = d * 1;
    final int ySize = n * 1;

    final FieldT one = fieldFactory.one();
    final FieldT zero = fieldFactory.zero();

    FieldT tmp = one;
    for (long i = 0; i < XSize; i++) {
      XList.add(new Tuple2<>(i, tmp));
      tmp = tmp.add(one);
    }

    for (long i = 0; i < wSize; i++) {
      wList.add(new Tuple2<>(i, tmp));
      tmp = tmp.add(one);
    }

    // make sure that Xw = y!
    for (long i = 0; i < ySize; i++) {
      tmp = zero;
      for (long j = 0; j < d; j++) {
        tmp = tmp.add(XList.get((int) (i * d + j))._2().mul(wList.get((int) j)._2()));
      }
      yList.add(new Tuple2<>(i, tmp));
    }

    JavaPairRDD<Long, FieldT> X =
        JavaPairRDD.fromJavaRDD(config.sparkContext().parallelize(XList, numPartitions));
    JavaPairRDD<Long, FieldT> w =
        JavaPairRDD.fromJavaRDD(config.sparkContext().parallelize(wList, numPartitions));
    JavaPairRDD<Long, FieldT> y =
        JavaPairRDD.fromJavaRDD(config.sparkContext().parallelize(yList, numPartitions));

    JavaPairRDD<Long, FieldT> oneFullAssignment =
        config
            .sparkContext()
            .parallelizePairs(Collections.singletonList(new Tuple2<>((long) 0, one)));

    // We have the [X | w | y] in the first part of the assignment RDD
    long assignmentOffset = 1 + n * d + d * 1 + n * 1;
    long constraintOffset = 0;

    // Transpose X
    LinearIndexer xIndexer = new LinearIndexer(1);
    TransposeIndexer xtIndexer = new TransposeIndexer(1, n, d);

    TransposeIndexer xtIndexerForMap = new TransposeIndexer(0, n, d);
    JavaPairRDD<Long, FieldT> XT =
        X.mapToPair(
            x -> {
              long xtIndex = xtIndexerForMap.getVirtualIndex(x._1());
              return new Tuple2<Long, FieldT>(xtIndex, x._2());
            });
    XT.cache().count();

    // Iterable<Tuple2<Long, FieldT>> xtList = XT.collect();
    // for (Tuple2<Long, FieldT> v : xtList) {
    //     System.out.println("v is " + v._1() + " - " + v._2());
    // }

    // X^T X
    LinearIndexer constraintOffsetX2 = new LinearIndexer(constraintOffset);
    LinearIndexer outputOffsetX2 = new LinearIndexer(assignmentOffset);
    Tuple3<R1CSConstraintsRDD<FieldT>, JavaPairRDD<Long, FieldT>, Long> retX2 =
        matmulParConstruct(
            config,
            fieldFactory,
            XT,
            X,
            bd,
            bn,
            bd,
            d,
            n,
            d,
            xtIndexer,
            xIndexer,
            outputOffsetX2,
            constraintOffsetX2);

    R1CSConstraintsRDD<FieldT> X2Constraints = retX2._1();
    JavaPairRDD<Long, FieldT> X2Assignments = retX2._2();
    long rhsAssignmentsOffset = assignmentOffset + retX2._3();

    long rhsConstraintOffset = constraintOffset + X2Constraints.size();

    // System.out.println("rhsConstraintOffset is " + rhsConstraintOffset + ", rhsAssignmentsOffset
    // is " + rhsAssignmentsOffset);

    R1CSConstraintsRDD<FieldT> constraints = X2Constraints;
    oneFullAssignment = oneFullAssignment.union(X2Assignments);

    // (X^T X) w, map to get the results of (X^T X)
    BlockIndexerC RHSBlockIndexer = new BlockIndexerC(assignmentOffset, d, n, d, bd, bn, bd);
    LinearIndexer wIndexer = new LinearIndexer(1 + n * d);
    LinearIndexer outputOffsetRHS = new LinearIndexer(rhsAssignmentsOffset);
    LinearIndexer constraintOffsetRHS = new LinearIndexer(rhsConstraintOffset);
    JavaPairRDD<Long, FieldT> X2 =
        X2Assignments.flatMapToPair(
            x -> {
              // we will do the filter here as well
              ArrayList<Tuple2<Long, FieldT>> ret = new ArrayList<Tuple2<Long, FieldT>>();
              long vIndex = RHSBlockIndexer.getVirtualIndex(x._1());
              // System.out.println("index is " + x._1() + ", vIndex is " + vIndex);
              if (vIndex > -1) {
                ret.add(new Tuple2<Long, FieldT>(vIndex, x._2()));
              }
              return ret.iterator();
            });

    Tuple3<R1CSConstraintsRDD<FieldT>, JavaPairRDD<Long, FieldT>, Long> retRHS =
        matmulParConstruct(
            config,
            fieldFactory,
            X2,
            w,
            bd,
            bd,
            1,
            d,
            d,
            1,
            RHSBlockIndexer,
            wIndexer,
            outputOffsetRHS,
            constraintOffsetRHS);
    R1CSConstraintsRDD<FieldT> RHSConstraints = retRHS._1();
    JavaPairRDD<Long, FieldT> RHSAssignments = retRHS._2();
    long lhsAssignmentsOffset = rhsAssignmentsOffset + retRHS._3();

    long lhsConstraintOffset = rhsConstraintOffset + RHSConstraints.size();
    constraints.union(RHSConstraints);
    oneFullAssignment = oneFullAssignment.union(RHSAssignments);

    // Calculate X^T y
    LinearIndexer yIndexer = new LinearIndexer(1 + n * d + d * 1);
    LinearIndexer outputOffsetLHS = new LinearIndexer(lhsAssignmentsOffset);
    LinearIndexer constraintOffsetLHS = new LinearIndexer(lhsConstraintOffset);
    Tuple3<R1CSConstraintsRDD<FieldT>, JavaPairRDD<Long, FieldT>, Long> retLHS =
        matmulParConstruct(
            config,
            fieldFactory,
            XT,
            y,
            bd,
            bn,
            1,
            d,
            n,
            1,
            xtIndexer,
            yIndexer,
            outputOffsetLHS,
            constraintOffsetLHS);
    R1CSConstraintsRDD<FieldT> LHSConstraints = retLHS._1();
    JavaPairRDD<Long, FieldT> LHSAssignments = retLHS._2();
    long numLHSAssignments = retLHS._3(); // UNUSED
    long numLHSConstraints = LHSConstraints.size();

    constraints.union(LHSConstraints);
    oneFullAssignment = oneFullAssignment.union(LHSAssignments);
    XT.unpersist();

    long finalConstraintOffsetNumber = lhsConstraintOffset + numLHSConstraints;
    // Finally, we need to assert that RHS = LHS
    BlockIndexerC RHSResultIndexer = new BlockIndexerC(rhsAssignmentsOffset, d, d, 1, bd, bd, 1);
    BlockIndexerC LHSResultIndexer = new BlockIndexerC(lhsAssignmentsOffset, d, n, 1, bd, bn, 1);
    LinearIndexer finalConstraintOffset = new LinearIndexer(finalConstraintOffsetNumber);
    R1CSConstraintsRDD<FieldT> finalConstraints =
        equalityConstraintGen(
            fieldFactory, config, RHSResultIndexer, LHSResultIndexer, finalConstraintOffset, d);
    constraints.union(finalConstraints);

    // We need to construct the final assignments
    JavaPairRDD<Long, FieldT> newXRDD =
        X.mapToPair(
            x -> {
              return new Tuple2<Long, FieldT>(x._1() + 1, x._2());
            });
    JavaPairRDD<Long, FieldT> newWRDD =
        w.mapToPair(
            x -> {
              return new Tuple2<Long, FieldT>(x._1() + 1 + n * d, x._2());
            });

    JavaPairRDD<Long, FieldT> newYRDD =
        y.mapToPair(
            x -> {
              return new Tuple2<Long, FieldT>(x._1() + 1 + n * d + d * 1, x._2());
            });

    oneFullAssignment = oneFullAssignment.union(newXRDD).union(newWRDD).union(newYRDD);
    config.beginLog("[Linear regression app] oneFullAssignment");
    long numVariables = oneFullAssignment.cache().count(); // UNUSED
    config.endLog("[Linear regression app] oneFullAssignment");

    config.beginLog("[Linear regression app] constraints generation");
    constraints.A().cache().count();
    constraints.B().cache().count();
    long totalNumConstraints = constraints.C().cache().count();
    config.endLog("[Linear regression app] constraints generation");
    System.out.println("[numconstraints: " + totalNumConstraints + "]");

    config.beginLog("[Linear regression app] primary generation");
    final Assignment<FieldT> primary =
        new Assignment<>(
            Utils.convertFromPairs(
                oneFullAssignment.filter(e -> e._1 >= 0 && e._1 < numInputs).collect(), numInputs));
    config.endLog("[Linear regression app] primary generation");

    long numVariables2 = 1 + numInputs + d * d * (2 * n) + d * (2 * d) + d * (2 * n);
    // assert(numVariables == numVariables2);

    final R1CSRelationRDD<FieldT> r1cs =
        new R1CSRelationRDD<>(constraints, numInputs, numVariables2 - numInputs);

    return new Tuple3<R1CSRelationRDD<FieldT>, Assignment<FieldT>, JavaPairRDD<Long, FieldT>>(
        r1cs, primary, oneFullAssignment);
  }

  // Does a linear combination of matrices/vectors
  // a1 M1 + a2 M2 + ... + an Mn = M
  // M can be a matrix or a vector
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      ArrayList<Tuple2<Long, LinearTerm<FieldT>>> linearCombinationConstraintsGen(
          final FieldT fieldFactory,
          ConstraintType cType,
          ArrayList<Indexer> inputOffset,
          ArrayList<FieldT> scalars,
          Indexer outputOffset,
          Indexer constraintOffset,
          int numInputs,
          int length) {

    final FieldT one = fieldFactory.one();

    ArrayList<Tuple2<Long, LinearTerm<FieldT>>> output =
        new ArrayList<Tuple2<Long, LinearTerm<FieldT>>>();

    for (int i = 0; i < numInputs; i++) {
      for (int j = 0; j < length; j++) {
        switch (cType) {
          case constraintA:
            output.add(
                new Tuple2<Long, LinearTerm<FieldT>>(
                    constraintOffset.getIndex(j),
                    new LinearTerm<FieldT>(inputOffset.get(i).getIndex(j), scalars.get(i))));
            break;
          case constraintB:
            output.add(
                new Tuple2<Long, LinearTerm<FieldT>>(
                    constraintOffset.getIndex(j), new LinearTerm<FieldT>((long) 0, one)));
            break;
          case constraintC:
            output.add(
                new Tuple2<Long, LinearTerm<FieldT>>(
                    constraintOffset.getIndex(j),
                    new LinearTerm<FieldT>(outputOffset.getIndex(j), scalars.get(i))));
            // System.out.println("[linearCombinationConstraintsGen - C] constraint " +
            // constraintOffset.getIndex(j) + ", assign index " + outputOffset.getIndex(j) + ",
            // scalar is " + scalars.get(i));
            break;
          default:
            System.out.println(
                "[R1CSConstruction.linearCombinationConstraintGen] unknown constraint");
            break;
        }
      }
    }

    return output;
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Iterator<Tuple2<Long, FieldT>> xMinusMeanAssignment(
          final FieldT fieldFactory,
          Long rowNumber,
          Tuple2<Iterable<Tuple2<Long, FieldT>>, Iterable<Tuple2<Long, FieldT>>> input,
          final int n,
          final int d,
          Indexer outputIndexer) {

    Comparator<Tuple2<Long, FieldT>> comparator =
        new Comparator<Tuple2<Long, FieldT>>() {
          public int compare(Tuple2<Long, FieldT> tupleA, Tuple2<Long, FieldT> tupleB) {
            if (tupleA._1() == tupleB._1()) {
              return 0;
            } else if (tupleA._1() < tupleB._1()) {
              return -1;
            }
            return 1;
          }
        };

    ArrayList<Tuple2<Long, FieldT>> X = new ArrayList<Tuple2<Long, FieldT>>();
    ArrayList<Tuple2<Long, FieldT>> mean = new ArrayList<Tuple2<Long, FieldT>>();
    ArrayList<Tuple2<Long, FieldT>> xMinusMean = new ArrayList<Tuple2<Long, FieldT>>();

    for (Tuple2<Long, FieldT> x : input._1()) {
      X.add(x);
    }

    Collections.sort(X, comparator);

    for (Tuple2<Long, FieldT> x : input._2()) {
      mean.add(x);
    }

    Collections.sort(mean, comparator);

    // subtracting
    for (int i = 0; i < d; i++) {
      FieldT ret = X.get(i)._2().sub(mean.get(i)._2());
      xMinusMean.add(new Tuple2<Long, FieldT>(outputIndexer.getIndex(rowNumber * d + i), ret));

      // System.out.println("Idx is " + outputIndexer.getIndex(rowNumber * d + i) + ", X[" + i + "]
      // is " + X.get(i)._2() +  ", mean[" + i + "] is " + mean.get(i)._2() + ", sub ret is " +
      // ret);
    }

    return xMinusMean.iterator();
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Iterator<Tuple2<Long, LinearTerm<FieldT>>> xMinusMeanConstraintsHelper(
          final FieldT fieldFactory,
          ConstraintType cType,
          Indexer inputOffset,
          Indexer outputOffset,
          Indexer constraintOffset,
          final int n,
          final int d,
          final int bn,
          final int bd,
          int blockNumber) {
    final FieldT one = fieldFactory.one();

    Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> t =
        getPartitionRowsCols(blockNumber, bn, bd, n, d);

    int cLowerRows = t._1()._1();
    int cHigherRows = t._1()._2();
    int cLowerCols = t._2()._1();
    int cHigherCols = t._2()._2();

    int numRows = cHigherRows - cLowerRows;
    int samplePointOffset = cLowerCols; // UNUSED

    ArrayList<FieldT> scalars = new ArrayList<FieldT>();
    scalars.add(one);

    ArrayList<Tuple2<Long, LinearTerm<FieldT>>> ret = new ArrayList<>();

    for (int i = 0; i < numRows; i++) {
      LinearIndexer li = new LinearIndexer((long) ((i + cLowerRows) * d + cLowerCols));
      // System.out.println("LinearIndexer offset is " + ((i + cLowerRows) * d + cLowerCols));
      ArrayList<Indexer> indexerList = new ArrayList<Indexer>();
      indexerList.add(inputOffset);
      indexerList.add(li);

      ArrayList<Indexer> inputOffsets = new ArrayList<Indexer>();
      inputOffsets.add(new CompositeIndexer(indexerList));

      // TODO: this makes the assumption that the matrix can be divided into equal-sized blocks
      int cOffset = blockNumber * (n / bn) * (d / bd) + i * (cHigherCols - cLowerCols);
      LinearIndexer newConstraintOffset = new LinearIndexer(constraintOffset.getIndex(0) + cOffset);

      LinearIndexer newOutputOffset = new LinearIndexer(outputOffset.getIndex(0) + li.getIndex(0));

      ArrayList<Tuple2<Long, LinearTerm<FieldT>>> tmp =
          R1CSConstruction.<FieldT>linearCombinationConstraintsGen(
              fieldFactory,
              cType,
              inputOffsets,
              scalars,
              newOutputOffset,
              newConstraintOffset,
              1,
              cHigherCols - cLowerCols);
      for (int j = 0; j < tmp.size(); j++) {
        ret.add(tmp.get(j));
      }
    }

    return ret.iterator();
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Tuple3<R1CSConstraintsRDD<FieldT>, JavaPairRDD<Long, FieldT>, Long> covVerify(
          final FieldT fieldFactory,
          final Configuration config,
          JavaPairRDD<Long, FieldT> X,
          JavaPairRDD<Long, FieldT> mean,
          JavaPairRDD<Long, FieldT> cov,
          final int n,
          final int d,
          final int bn,
          final int bd,
          Indexer xOffset,
          Indexer meanOffset,
          Indexer covIndexer,
          Indexer outputAssignmentIndexer,
          Indexer constraintOffset) {

    // UNUSED
    Comparator<Tuple2<Long, FieldT>> comparator =
        new Comparator<Tuple2<Long, FieldT>>() {
          public int compare(Tuple2<Long, FieldT> tupleA, Tuple2<Long, FieldT> tupleB) {
            if (tupleA._1() == tupleB._1()) {
              return 0;
            } else if (tupleA._1() < tupleB._1()) {
              return -1;
            }
            return 1;
          }
        };

    final int numPartitions = config.numPartitions();

    final FieldT one = fieldFactory.one();

    // Find the assignments for X - mean, and n * cov

    JavaPairRDD<Long, Tuple2<Long, FieldT>> xTransformed =
        X.mapToPair(
            x -> {
              Long idx = x._1();
              FieldT value = x._2(); // UNUSED

              long row = idx / d;
              long col = idx % d; // UNUSED

              return new Tuple2<Long, Tuple2<Long, FieldT>>(row, x);
            });

    JavaPairRDD<Long, Tuple2<Long, FieldT>> meanTransformed =
        mean.flatMapToPair(
            x -> {
              ArrayList<Tuple2<Long, Tuple2<Long, FieldT>>> ret =
                  new ArrayList<Tuple2<Long, Tuple2<Long, FieldT>>>();

              for (long i = 0; i < n; i++) {
                ret.add(new Tuple2<Long, Tuple2<Long, FieldT>>(i, x));
              }

              return ret.iterator();
            });

    // Get the (X-mean) assignment
    JavaPairRDD<Long, FieldT> xm =
        xTransformed
            .cogroup(meanTransformed)
            .flatMapToPair(
                x -> {
                  return xMinusMeanAssignment(
                      fieldFactory, x._1(), x._2(), n, d, outputAssignmentIndexer);
                });

    int xmOffset = n * d;

    ArrayList<Integer> intList = new ArrayList<>();
    for (int i = 0; i < bn * bd; i++) {
      intList.add(i);
    }

    RowRepeatIndexer newMeanOffset = new RowRepeatIndexer(meanOffset.getIndex(0), d);

    // Generate the constraints to assert that xm + mean = x
    JavaPairRDD<Long, LinearTerm<FieldT>> ALCXm =
        config
            .sparkContext()
            .parallelize(intList, numPartitions)
            .flatMapToPair(
                blockNumber -> {
                  return xMinusMeanConstraintsHelper(
                      fieldFactory,
                      ConstraintType.constraintA,
                      outputAssignmentIndexer,
                      xOffset,
                      constraintOffset,
                      n,
                      d,
                      bn,
                      bd,
                      blockNumber);
                });

    // JavaPairRDD<Long, LinearTerm<FieldT>> BLCXm =
    // config.sparkContext().parallelize(intList).flatMapToPair(blockNumber -> {
    //         return xMinusMeanConstraintsHelper(fieldFactory, ConstraintType.constraintB,
    //                                            outputAssignmentIndexer, xOffset,
    // constraintOffset,
    //                                            n, d, bn, bd,
    //                                            blockNumber);
    //     });

    JavaPairRDD<Long, LinearTerm<FieldT>> ALCMean =
        config
            .sparkContext()
            .parallelize(intList, numPartitions)
            .flatMapToPair(
                blockNumber -> {
                  return xMinusMeanConstraintsHelper(
                      fieldFactory,
                      ConstraintType.constraintA,
                      newMeanOffset,
                      xOffset,
                      constraintOffset,
                      n,
                      d,
                      bn,
                      bd,
                      blockNumber);
                });

    JavaPairRDD<Long, LinearTerm<FieldT>> BLC =
        config
            .sparkContext()
            .parallelize(intList, numPartitions)
            .flatMapToPair(
                blockNumber -> {
                  return xMinusMeanConstraintsHelper(
                      fieldFactory,
                      ConstraintType.constraintB,
                      newMeanOffset,
                      xOffset,
                      constraintOffset,
                      n,
                      d,
                      bn,
                      bd,
                      blockNumber);
                });

    JavaPairRDD<Long, LinearTerm<FieldT>> CLC =
        config
            .sparkContext()
            .parallelize(intList, numPartitions)
            .flatMapToPair(
                blockNumber -> {
                  return xMinusMeanConstraintsHelper(
                      fieldFactory,
                      ConstraintType.constraintC,
                      meanOffset,
                      xOffset,
                      constraintOffset,
                      n,
                      d,
                      bn,
                      bd,
                      blockNumber);
                });

    R1CSConstraintsRDD<FieldT> constraints =
        new R1CSConstraintsRDD<FieldT>(ALCXm.union(ALCMean), BLC, CLC, n * d);

    // (X-mean)^T
    LinearIndexer xmIndexer = new LinearIndexer(outputAssignmentIndexer.getIndex(0));
    TransposeIndexer xmtIndexer = new TransposeIndexer(xmIndexer.getIndex(0), n, d);
    JavaPairRDD<Long, FieldT> xmT =
        xm.mapToPair(
            x -> {
              long xmtIndex = xmtIndexer.getVirtualIndex(x._1());
              return new Tuple2<Long, FieldT>(xmtIndex, x._2());
            });

    // Iterable<Tuple2<Long, FieldT>> xmTListItr = xmT.collect();
    // ArrayList<Tuple2<Long, FieldT>> xmTList = new ArrayList<>();
    // for (Tuple2<Long, FieldT> v : xmTListItr) {
    //     xmTList.add(v);
    // }
    // Collections.sort(xmTList, comparator);
    // for (int i = 0; i < xmTList.size(); i++) {
    //     System.out.println("[xmTList] v is " + xmTList.get(i));
    // }

    JavaPairRDD<Long, FieldT> xmInput =
        xm.mapToPair(
            x -> {
              return new Tuple2<Long, FieldT>(
                  outputAssignmentIndexer.getVirtualIndex(x._1()), x._2());
            });

    // Iterable<Tuple2<Long, FieldT>> xmList = xmInput.collect();
    // for (Tuple2<Long, FieldT> v : xmList) {
    //     System.out.println("[xm] v is " + v);
    // }

    LinearIndexer xm2OutputAssignmentIndexer = new LinearIndexer(xmIndexer.getIndex(0) + xmOffset);
    LinearIndexer xm2ConstraintOffset = new LinearIndexer(constraintOffset.getIndex(n * d));
    // System.out.println("xm2Constraint offset is " + xm2ConstraintOffset.getIndex(0) + ", xm2
    // assignment offset is " + xm2OutputAssignmentIndexer.getIndex(0) + ", xm assignment offset is
    // " + xmIndexer.getIndex(0));
    Tuple3<R1CSConstraintsRDD<FieldT>, JavaPairRDD<Long, FieldT>, Long> xm2 =
        matmulParConstruct(
            config,
            fieldFactory,
            xmT,
            xmInput,
            bd,
            bn,
            bd,
            d,
            n,
            d,
            xmtIndexer,
            xmIndexer,
            xm2OutputAssignmentIndexer,
            xm2ConstraintOffset);

    constraints.union(xm2._1());
    // Iterable<Tuple2<Long, FieldT>> xm2Iter = xm2._2().collect();
    // ArrayList<Tuple2<Long, FieldT>> xm2List = new ArrayList<>();
    // for (Tuple2<Long, FieldT> v: xm2Iter) {
    //     xm2List.add(v);
    // }
    // Collections.sort(xm2List, comparator);
    // for (int i = 0; i < xm2List.size(); i++) {
    //     System.out.println("[xm2List] v is " + xm2List.get(i));
    // }

    long xm2NumAssignments = xm2._3();
    // Multiply cov by n - 1
    FieldT multiple = fieldFactory.zero();
    for (int i = 0; i < n - 1; i++) {
      multiple = multiple.add(one);
    }
    final FieldT N = multiple;
    LinearIndexer covNOutputAssignmentIndexer =
        new LinearIndexer(xm2OutputAssignmentIndexer.getIndex(0) + xm2NumAssignments);
    System.out.println();
    JavaPairRDD<Long, FieldT> covN =
        cov.mapToPair(
            x -> {
              long k = covNOutputAssignmentIndexer.getIndex(x._1());
              FieldT v = x._2().mul(N);
              return new Tuple2<Long, FieldT>(k, v);
            });

    // Assert equality (that xm2 == covN)
    BlockIndexerC rhsAssignmentIndexer =
        new BlockIndexerC(xm2OutputAssignmentIndexer.getIndex(0), d, n, d, bd, bn, bd);
    long finalConstraintOffsetNumber = xm2ConstraintOffset.getIndex(0) + xm2._1().size();
    LinearIndexer finalConstraintOffset = new LinearIndexer(finalConstraintOffsetNumber);
    R1CSConstraintsRDD<FieldT> finalConstraints =
        equalityConstraintGen(
            fieldFactory,
            config,
            rhsAssignmentIndexer,
            covNOutputAssignmentIndexer,
            finalConstraintOffset,
            d * d);

    constraints.union(finalConstraints);
    JavaPairRDD<Long, FieldT> oneFullAssignment = xm.union(xm2._2()).union(covN);

    long numAssignments = n * d + xm2NumAssignments + d * d;

    return new Tuple3<>(constraints, oneFullAssignment, numAssignments);
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Iterator<Tuple2<Long, LinearTerm<FieldT>>> meanVerifyHelper(
          final FieldT fieldFactory,
          ConstraintType cType,
          Indexer xOffset,
          Indexer meanOffset,
          Indexer constraintOffset,
          final int n,
          final int d,
          final int bn,
          final int bd,
          FieldT scalar,
          int blockNumber) {

    Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> t =
        getPartitionRowsCols(blockNumber, bn, bd, n, d);

    int cLowerRows = t._1()._1();
    int cHigherRows = t._1()._2();
    int cLowerCols = t._2()._1();
    int cHigherCols = t._2()._2();

    int numInputs = cHigherRows - cLowerRows;

    ArrayList<Indexer> inputOffsets = new ArrayList<Indexer>();
    ArrayList<FieldT> scalars = new ArrayList<FieldT>();

    // final FieldT one = fieldFactory.one();
    // FieldT N = fieldFactory.zero();
    // for (int i = 0; i < n; i++) {
    //     N = N.add(one);
    // }

    for (int i = 0; i < numInputs; i++) {
      LinearIndexer li = new LinearIndexer(d * (cLowerRows + i) + cLowerCols);
      ArrayList<Indexer> indexers = new ArrayList<Indexer>();
      indexers.add(xOffset);
      indexers.add(li);
      inputOffsets.add(new CompositeIndexer(indexers));
      if (cType == ConstraintType.constraintC) {
        // System.out.println("constraint type C! N is " + scalar);
        scalars.add(scalar);
      } else {
        scalars.add(scalar);
      }
    }

    int cOffset = cLowerCols;
    LinearIndexer newConstraintOffset = new LinearIndexer(constraintOffset.getIndex(0) + cOffset);
    // if (cType == ConstraintType.constraintC) {
    //     System.out.println("cOffset: " + cOffset + ", newConstraintOffset's offset is " +
    // newConstraintOffset.getIndex(0));
    // }
    ArrayList<Indexer> indexers = new ArrayList<Indexer>();
    indexers.add(meanOffset);
    indexers.add(new LinearIndexer(cLowerCols));
    CompositeIndexer newMeanOffset = new CompositeIndexer(indexers);
    ArrayList<Tuple2<Long, LinearTerm<FieldT>>> ret =
        R1CSConstruction.<FieldT>linearCombinationConstraintsGen(
            fieldFactory,
            cType,
            inputOffsets,
            scalars,
            newMeanOffset,
            newConstraintOffset,
            numInputs,
            cHigherCols - cLowerCols);
    return ret.iterator();
  }

  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      R1CSConstraintsRDD<FieldT> meanVerify(
          final Configuration config,
          final FieldT fieldFactory,
          Indexer xOffset,
          Indexer meanOffset,
          Indexer constraintOffset,
          final int n,
          final int d,
          final int bn,
          final int bd) {

    // we actually don't need more witnesses for this
    // we only need to generate the constraints
    // Total number of constraints is d, each constraint has n terms
    // Total number of linear terms: n * d

    final int numPartitions = config.numPartitions();

    FieldT N = fieldFactory.zero();
    FieldT one = fieldFactory.one();
    for (int i = 0; i < n; i++) {
      N = N.add(one);
    }
    final FieldT NInput = N;

    ArrayList<Integer> intList = new ArrayList<>();
    for (int i = 0; i < bn * bd; i++) {
      intList.add(i);
    }

    ArrayList<Integer> intList2 = new ArrayList<>();
    for (int i = 0; i < bd; i++) {
      intList2.add(i);
    }

    JavaPairRDD<Long, LinearTerm<FieldT>> ALC =
        config
            .sparkContext()
            .parallelize(intList, numPartitions)
            .flatMapToPair(
                blockNumber -> {
                  return meanVerifyHelper(
                      fieldFactory,
                      ConstraintType.constraintA,
                      xOffset,
                      meanOffset,
                      constraintOffset,
                      n,
                      d,
                      bn,
                      bd,
                      one,
                      blockNumber);
                });

    JavaPairRDD<Long, LinearTerm<FieldT>> BLC =
        config
            .sparkContext()
            .parallelize(intList2, numPartitions)
            .flatMapToPair(
                blockNumber -> {
                  return meanVerifyHelper(
                      fieldFactory,
                      ConstraintType.constraintB,
                      xOffset,
                      meanOffset,
                      constraintOffset,
                      1,
                      d,
                      1,
                      bd,
                      one,
                      blockNumber);
                });

    JavaPairRDD<Long, LinearTerm<FieldT>> CLC =
        config
            .sparkContext()
            .parallelize(intList2, numPartitions)
            .flatMapToPair(
                blockNumber -> {
                  return meanVerifyHelper(
                      fieldFactory,
                      ConstraintType.constraintC,
                      xOffset,
                      meanOffset,
                      constraintOffset,
                      1,
                      d,
                      1,
                      bd,
                      NInput,
                      blockNumber);
                });

    // ALC.cache().count();
    // BLC.cache().count();
    // CLC.cache().count();

    // Iterable<Tuple2<Long, LinearTerm<FieldT>>> clcList = CLC.collect();
    // for (Tuple2<Long, LinearTerm<FieldT>> v : clcList) {
    //     System.out.println("clcList: " + v._1() + ": " + v._2().index() + " - " +
    // v._2().value());
    // }
    // System.out.println("Mean calculation done");

    final R1CSConstraintsRDD<FieldT> constraints = new R1CSConstraintsRDD<>(ALC, BLC, CLC, d);

    return constraints;
  }

  // Verify whether the mean and covariance matrix are indeed correctly calculated
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Tuple2<R1CSConstraintsRDD<FieldT>, JavaPairRDD<Long, FieldT>> gaussianFit(
          final Configuration config,
          final FieldT fieldFactory,
          JavaPairRDD<Long, FieldT> X,
          JavaPairRDD<Long, FieldT> mean,
          JavaPairRDD<Long, FieldT> cov,
          final int n,
          final int d,
          final int bn,
          final int bd,
          Indexer xOffset,
          Indexer meanOffset,
          Indexer covOffset,
          Indexer outputAssignmentOffset,
          Indexer constraintOffset) {

    // Get the mean constraints; we don't need extra witnesses since we only need X and the mean

    R1CSConstraintsRDD<FieldT> meanConstraints =
        meanVerify(config, fieldFactory, xOffset, meanOffset, constraintOffset, n, d, bn, bd);

    // Iterable<Tuple2<Long, LinearTerm<FieldT>>> meanALC = meanConstraints.A().collect();
    // for (Tuple2<Long, LinearTerm<FieldT>> v : meanALC) {
    //     System.out.println("mean alcList: "  + v._1() + ": " +  v._2().index() + " - " +
    // v._2().value());
    // }

    // Iterable<Tuple2<Long, LinearTerm<FieldT>>> meanBLC = meanConstraints.B().collect();
    // for (Tuple2<Long, LinearTerm<FieldT>> v : meanBLC) {
    //     System.out.println("mean blcList: " + v._1() + ": " + v._2().index() + " - " +
    // v._2().value());
    // }

    // Iterable<Tuple2<Long, LinearTerm<FieldT>>> meanCLC = meanConstraints.C().collect();
    // for (Tuple2<Long, LinearTerm<FieldT>> v : meanCLC) {
    //     System.out.println("mean clcList: "  + v._1() + ": " +  v._2().index() + " - " +
    // v._2().value());
    // }

    LinearIndexer covConstraintOffset =
        new LinearIndexer(constraintOffset.getIndex(meanConstraints.size()));
    Tuple3<R1CSConstraintsRDD<FieldT>, JavaPairRDD<Long, FieldT>, Long> covRet =
        covVerify(
            fieldFactory,
            config,
            X,
            mean,
            cov,
            n,
            d,
            bn,
            bd,
            xOffset,
            meanOffset,
            covOffset,
            outputAssignmentOffset,
            covConstraintOffset);

    // Iterable<Tuple2<Long, LinearTerm<FieldT>>> alcList = covRet._1().A().collect();
    // for (Tuple2<Long, LinearTerm<FieldT>> v : alcList) {
    //     System.out.println("cov alcList: "  + v._1() + ": " +  v._2().index() + " - " +
    // v._2().value());
    // }

    // Iterable<Tuple2<Long, LinearTerm<FieldT>>> blcList = covRet._1().B().collect();
    // for (Tuple2<Long, LinearTerm<FieldT>> v : blcList) {
    //     System.out.println("cov blcList: " + v._1() + ": " + v._2().index() + " - " +
    // v._2().value());
    // }

    // Iterable<Tuple2<Long, LinearTerm<FieldT>>> clcList = covRet._1().C().collect();
    // for (Tuple2<Long, LinearTerm<FieldT>> v : clcList) {
    //     System.out.println("cov clcList: "  + v._1() + ": " +  v._2().index() + " - " +
    // v._2().value());
    // }

    meanConstraints.union(covRet._1());
    return new Tuple2<>(meanConstraints, covRet._2());
  }

  // Given X, a list of n sample points of dimension d, we want to fit to a Gaussian distribution
  // We calculate the mean and the covariance
  // Here we are assuming that all of these points are from the same class
  public static <FieldT extends AbstractFieldElementExpanded<FieldT>>
      Tuple3<R1CSRelationRDD<FieldT>, Assignment<FieldT>, JavaPairRDD<Long, FieldT>> gaussianFitApp(
          final FieldT fieldFactory,
          final Configuration config,
          final int n,
          final int d,
          final int bn,
          final int bd) {

    final int numPartitions = config.numPartitions();
    System.out.println("[gaussianFitApp] numPartitions: " + numPartitions);

    // Generate the sample points
    final ArrayList<Tuple2<Long, FieldT>> XList = new ArrayList<>();
    final ArrayList<Tuple2<Long, FieldT>> meanList = new ArrayList<>();
    final ArrayList<Tuple2<Long, FieldT>> covList = new ArrayList<>();

    final ArrayList<Long> meanLong = new ArrayList<Long>();
    final ArrayList<Long> covLong = new ArrayList<>();

    final int XSize = n * d;
    final int meanSize = d; // UNUSED
    final int covSize = d * d; // UNUSED

    final FieldT one = fieldFactory.one();
    final FieldT zero = fieldFactory.zero();

    // initialize mean and cov
    for (int i = 0; i < d; i++) {
      meanList.add(new Tuple2<Long, FieldT>((long) i, zero));
      meanLong.add((long) 0);
      for (int j = 0; j < d; j++) {
        covList.add(new Tuple2<Long, FieldT>((long) i * d + j, zero));
        covLong.add((long) 0);
      }
    }

    // we want to create numbers that are divisible by n... sigh
    // then calculate the actual mean and covariance matrix
    FieldT multipleN = fieldFactory.zero();
    for (int i = 0; i < n; i++) {
      multipleN = multipleN.add(one);
    }

    FieldT multipleNMinusOne = fieldFactory.zero();
    for (int i = 0; i < n - 1; i++) {
      multipleNMinusOne = multipleNMinusOne.add(one);
    }

    // FieldT tmp = multiple.mul(multiple);
    FieldT tmp = one;
    for (long i = 0; i < XSize; i++) {
      XList.add(new Tuple2<>(i, tmp));
      int idx = (int) (i % d);
      meanList.set(
          idx,
          new Tuple2<Long, FieldT>(
              (long) idx, meanList.get(idx)._2().add(tmp.mul(multipleNMinusOne))));
      meanLong.set(idx, meanLong.get(idx) + (i + 1) * (n - 1));
      tmp = tmp.add(one);
    }

    // cov = (1/n) (x - mean) (x - mean)^T
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < d * d; j++) {
        int row = j / d;
        int col = j % d;
        long rowValue = (i * d + row + 1) * (n - 1) * n - meanLong.get(row);
        long colValue = (i * d + col + 1) * (n - 1) * n - meanLong.get(col);
        // FieldT subResult1 = fieldFactory.construct((i * d + row) *
        // n).sub(fieldFactory.construct(meanLong.get(row)));
        // FieldT subResult2 = fieldFactory.construct((i * d + col) *
        // n).sub(fieldFactory.construct(meanLong.get(col)));
        // FieldT prod = subResult1.mul(subResult2);
        covLong.set(j, covLong.get(j) + rowValue * colValue);
      }
    }

    for (int i = 0; i < d * d; i++) {
      FieldT temp = fieldFactory.construct(covLong.get(i) / (n - 1));
      // System.out.println("covLong[" + i + "]: " + covLong.get(i) + ", temp is " + temp);
      covList.set(i, new Tuple2<Long, FieldT>((long) i, temp));
    }

    // re-multiply everything by n, so that we don't have to deal with the damn fractions
    for (int i = 0; i < XSize; i++) {
      XList.set(
          i,
          new Tuple2<Long, FieldT>(
              (long) i, XList.get(i)._2().mul(multipleN).mul(multipleNMinusOne)));
    }

    // for (int i = 0; i < n; i++) {
    //     for (int j = 0; j < d; j++) {
    //         int idx = i * d + j;
    //         System.out.println("X[" + i + "][" + j + "]" + XList.get(idx));
    //     }
    // }

    // for (int i = 0; i < meanList.size(); i++) {
    //     System.out.println("mean[" + i + "]: " + meanList.get(i));
    //     System.out.println("meanLong[" + i + "]: " + meanLong.get(i));
    // }

    // for (int i = 0; i < d; i++) {
    //     for (int j = 0; j < d; j++) {
    //         int idx = i * d + j;
    //         System.out.println("cov[" + i + "][" + j + "]" + covList.get(idx));
    //     }
    // }

    JavaPairRDD<Long, FieldT> X =
        JavaPairRDD.fromJavaRDD(config.sparkContext().parallelize(XList, numPartitions));
    JavaPairRDD<Long, FieldT> mean =
        JavaPairRDD.fromJavaRDD(config.sparkContext().parallelize(meanList, numPartitions));
    JavaPairRDD<Long, FieldT> cov =
        JavaPairRDD.fromJavaRDD(config.sparkContext().parallelize(covList, numPartitions));

    LinearIndexer xOffset = new LinearIndexer(1);
    LinearIndexer meanOffset = new LinearIndexer(1 + n * d);
    LinearIndexer covOffset = new LinearIndexer(1 + n * d + d);
    LinearIndexer outputOffset = new LinearIndexer(1 + n * d + d + d * d);
    LinearIndexer constraintOffset = new LinearIndexer(0);

    Tuple2<R1CSConstraintsRDD<FieldT>, JavaPairRDD<Long, FieldT>> ret =
        gaussianFit(
            config,
            fieldFactory,
            X,
            mean,
            cov,
            n,
            d,
            bn,
            bd,
            xOffset,
            meanOffset,
            covOffset,
            outputOffset,
            constraintOffset);

    R1CSConstraintsRDD<FieldT> constraints = ret._1();
    JavaPairRDD<Long, FieldT> oneFullAssignment = ret._2();

    // remap X, mean, and covariance to have the correct indices
    JavaPairRDD<Long, FieldT> newX =
        X.mapToPair(
            x -> {
              return new Tuple2<Long, FieldT>(x._1() + 1, x._2());
            });
    JavaPairRDD<Long, FieldT> newMean =
        mean.mapToPair(
            x -> {
              return new Tuple2<Long, FieldT>(x._1() + 1 + n * d, x._2());
            });
    JavaPairRDD<Long, FieldT> newCov =
        cov.mapToPair(
            x -> {
              return new Tuple2<Long, FieldT>(x._1() + 1 + n * d + d, x._2());
            });

    oneFullAssignment = oneFullAssignment.union(newX).union(newMean).union(newCov);
    oneFullAssignment =
        oneFullAssignment.union(
            config
                .sparkContext()
                .parallelizePairs(Collections.singletonList(new Tuple2<>((long) 0, one))));

    // UNUSED
    Comparator<Tuple2<Long, FieldT>> comparator =
        new Comparator<Tuple2<Long, FieldT>>() {
          public int compare(Tuple2<Long, FieldT> tupleA, Tuple2<Long, FieldT> tupleB) {
            if (tupleA._1() == tupleB._1()) {
              return 0;
            } else if (tupleA._1() < tupleB._1()) {
              return -1;
            }
            return 1;
          }
        };

    config.beginLog("[gaussianFitApp] oneFullAssignment");
    long numVariables = oneFullAssignment.cache().count();
    config.endLog("[gaussianFitApp] oneFullAssignment");

    config.beginLog("[gaussianFitApp] constraints generation");
    constraints.A().cache().count();
    constraints.B().cache().count();
    long totalNumConstraints = constraints.C().cache().count();
    config.endLog("[gaussianFitApp] constraints generation");
    System.out.println("[numconstraints: " + totalNumConstraints + "]");

    int numInputs = n * d + d + d * d;

    config.beginLog("[gaussianFitApp] primary generation");
    final Assignment<FieldT> primary =
        new Assignment<FieldT>(
            Utils.<FieldT>convertFromPairs(
                oneFullAssignment.filter(e -> e._1 >= 0 && e._1 < numInputs).collect(), numInputs));
    config.endLog("[gaussianFitApp] primary generation");

    final R1CSRelationRDD<FieldT> r1cs =
        new R1CSRelationRDD<FieldT>(constraints, numInputs, numVariables - numInputs);

    return new Tuple3<R1CSRelationRDD<FieldT>, Assignment<FieldT>, JavaPairRDD<Long, FieldT>>(
        r1cs, primary, oneFullAssignment);
  }
}
