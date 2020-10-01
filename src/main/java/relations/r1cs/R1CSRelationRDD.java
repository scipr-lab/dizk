/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package relations.r1cs;

import algebra.fields.AbstractFieldElementExpanded;
import org.apache.spark.api.java.JavaPairRDD;
import relations.objects.Assignment;
import relations.objects.LinearTerm;
import relations.objects.R1CSConstraintsRDD;
import scala.Tuple2;

import java.io.Serializable;

/**
 * A system of R1CSRelation constraints looks like
 * <p>
 * { < A_k , X > * < B_k , X > = < C_k , X > }_{k=1}^{n}
 * <p>
 * In other words, the system is satisfied if and only if there exist a
 * USCS variable assignment for which each R1CSRelation constraint is satisfied.
 * <p>
 * NOTE:
 * The 0-th variable (i.e., "x_{0}") always represents the constant 1.
 * Thus, the 0-th variable is not included in num_variables.
 * <p>
 * A R1CSRelation variable assignment is a vector of <FieldT> elements that represents
 * a candidate solution to a R1CSRelation constraint system.
 */
public class R1CSRelationRDD<FieldT extends AbstractFieldElementExpanded<FieldT>> implements
        Serializable {

    private final R1CSConstraintsRDD<FieldT> constraints;

    private final int numInputs;
    private final long numAuxiliary;
    private final long numConstraints;

    public R1CSRelationRDD(
            final R1CSConstraintsRDD<FieldT> _constraints,
            final int _numInputs,
            final long _numAuxiliary) {
        constraints = _constraints;
        numInputs = _numInputs;
        numAuxiliary = _numAuxiliary;
        numConstraints = _constraints.size();
    }

    public boolean isValid() {
        if (this.numInputs() > this.numVariables()) {
            return false;
        }

        final long numVariables = this.numVariables();
        final long countA = constraints.A().filter(term -> term._2.index() > numVariables).count();
        final long countB = constraints.B().filter(term -> term._2.index() > numVariables).count();
        final long countC = constraints.C().filter(term -> term._2.index() > numVariables).count();

        return countA == 0 && countB == 0 && countC == 0;
    }

    public boolean isSatisfied(
            final Assignment<FieldT> primary,
            final JavaPairRDD<Long, FieldT> oneFullAssignment) {
        assert (oneFullAssignment.count() == this.numVariables());

        // Assert first element == FieldT.one().
        final FieldT firstElement = oneFullAssignment.lookup(0L).get(0);
        final FieldT one = primary.get(0).one();
        assert (firstElement.equals(one));
        assert (primary.get(0).equals(one));

        // Compute the assignment evaluation for each constraint.
        final JavaPairRDD<Long, FieldT> zeroIndexedA = constraints().A()
                .filter(e -> e._2.index() == 0)
                .mapValues(LinearTerm::value)
                .reduceByKey(FieldT::add);

        final JavaPairRDD<Long, FieldT> zeroIndexedB = constraints().B()
                .filter(e -> e._2.index() == 0)
                .mapValues(LinearTerm::value)
                .reduceByKey(FieldT::add);

        final JavaPairRDD<Long, FieldT> zeroIndexedC = constraints().C()
                .filter(e -> e._2.index() == 0)
                .mapValues(LinearTerm::value)
                .reduceByKey(FieldT::add);

        final JavaPairRDD<Long, FieldT> A = constraints().A().filter(e -> e._2.index() != 0)
                .mapToPair(term -> {
                    // Swap the constraint and term index positions.
                    return new Tuple2<>(term._2.index(), new Tuple2<>(term._1, term._2.value()));
                }).join(oneFullAssignment).mapToPair(term -> {
                    // Multiply the constraint value by the input value.
                    return new Tuple2<>(term._2._1._1, term._2._1._2.mul(term._2._2));
                }).union(zeroIndexedA).reduceByKey(FieldT::add);

        final JavaPairRDD<Long, FieldT> B = constraints().B().filter(e -> e._2.index() != 0)
                .mapToPair(term -> {
                    // Swap the constraint and term index positions.
                    return new Tuple2<>(term._2.index(), new Tuple2<>(term._1, term._2.value()));
                }).join(oneFullAssignment).mapToPair(term -> {
                    // Multiply the constraint value by the input value.
                    return new Tuple2<>(term._2._1._1, term._2._1._2.mul(term._2._2));
                }).union(zeroIndexedB).reduceByKey(FieldT::add);

        final JavaPairRDD<Long, FieldT> C = constraints().C().filter(e -> e._2.index() != 0)
                .mapToPair(term -> {
                    // Swap the constraint and term index positions.
                    return new Tuple2<>(term._2.index(), new Tuple2<>(term._1, term._2.value()));
                }).join(oneFullAssignment).mapToPair(term -> {
                    // Multiply the constraint value by the input value.
                    return new Tuple2<>(term._2._1._1, term._2._1._2.mul(term._2._2));
                }).union(zeroIndexedC).reduceByKey(FieldT::add);

        final long count = A.join(B).join(C).filter(term -> {
            final FieldT a = term._2._1._1;
            final FieldT b = term._2._1._2;
            final FieldT c = term._2._2;

            if (!a.mul(b).equals(c)) {
                System.out.println("R1CSConstraint unsatisfied (index " + term._1 + "):");
                System.out.println("<a,(1,x)> = " + a);
                System.out.println("<b,(1,x)> = " + b);
                System.out.println("<c,(1,x)> = " + c);
                return true;
            }

            return false;
        }).count();

        return count == 0;
    }


    public R1CSConstraintsRDD<FieldT> constraints() {
        return constraints;
    }

    public int numInputs() {
        return numInputs;
    }

    public long numVariables() {
        return numInputs + numAuxiliary;
    }

    public long numConstraints() {
        return numConstraints;
    }

}
