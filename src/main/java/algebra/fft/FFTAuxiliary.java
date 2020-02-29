package algebra.fft;

import algebra.fields.AbstractFieldElementExpanded;
import common.Combiner;
import common.MathUtils;
import common.Utils;
import configuration.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FFTAuxiliary {

    /**
     * Compute the radix-2 FFT of the vector a over the set S={omega^{0},...,omega^{m-1}}. Result is
     * stored in ArrayList input.
     */
    static <FieldT extends AbstractFieldElementExpanded<FieldT>> void serialRadix2FFT(
            final List<FieldT> input,
            final FieldT omega) {
        final int n = input.size();
        final int logn = MathUtils.log2(n);
        if (n == 1) {
            return;
        }

        assert (n == (1 << logn));

        /* swapping in place (from Storer's book) */
        for (int k = 0; k < n; ++k) {
            final int rk = MathUtils.bitreverse(k, logn);
            if (k < rk) {
                Collections.swap(input, k, rk);
            }
        }

        int m = 1; // invariant: m = 2^{s-1}
        for (int s = 1; s <= logn; ++s) {
            // w_m is 2^s-th root of unity now
            final FieldT w_m = omega.pow(n / (2 * m));

            for (int k = 0; k < n; k += 2 * m) {
                FieldT w = omega.one();
                for (int j = 0; j < m; ++j) {
                    final FieldT t = w.mul(input.get(k + j + m));
                    input.set(k + j + m, input.get(k + j).sub(t));
                    input.set(k + j, input.get(k + j).add(t));
                    w = w.mul(w_m);
                }
            }
            m *= 2;
        }
    }

    /**
     * A distributed version of serialRadix2FFT.
     */
    static <FieldT extends AbstractFieldElementExpanded<FieldT>> JavaPairRDD<Long, FieldT>
    distributedRadix2FFT(
            final JavaPairRDD<Long, FieldT> input,
            final long rows,
            final long columns,
            final boolean inverse,
            final FieldT fieldFactory) {
        assert (MathUtils.isPowerOfTwo(rows));
        assert (MathUtils.isPowerOfTwo(columns));

        /* Initialization */
        final long size = rows * columns;
        final FieldT omegaShift = fieldFactory.rootOfUnity(size);
        final Combiner<FieldT> combine = new Combiner<>();
        final SerialFFT<FieldT> rowDomain = new SerialFFT<>(rows, fieldFactory);
        final SerialFFT<FieldT> columnDomain = new SerialFFT<>(columns, fieldFactory);

        /* Algorithm 1: Forward FFT, Mapper */
        final JavaPairRDD<Long, FieldT> columnGroups = input.mapToPair(element -> {
            /* AbstractGroup the array of inputs into rows using the combiner. */
            final long group = element._1 % rows;
            final long index = element._1 / rows;

            return new Tuple2<>(group, new Tuple2<>(index, element._2));
        }).combineByKey(combine.createGroup, combine.mergeElement, combine.mergeCombiner)
                .mapValues(partition -> {
                    /* Compute the forward FFT, on domain size columns, for each group. */
                    ArrayList<FieldT> groupArray = Utils.convertFromPairs(partition, (int) columns);

                    if (inverse) {
                        columnDomain.radix2InverseFFT(groupArray);
                    } else {
                        columnDomain.radix2FFT(groupArray);
                    }

                    return groupArray;
                }).flatMapToPair(element -> {
                    /* Bitshift and map to key j. */
                    final long index = element._1;

                    ArrayList<Tuple2<Long, FieldT>> combinedNumbers = new ArrayList<>();
                    for (int i = 0; i < columns; i++) {
                        final FieldT nthRoot =
                                inverse ? omegaShift.pow(index * i).inverse() : omegaShift.pow(index * i);
                        combinedNumbers.add(new Tuple2<>(i * rows + index, nthRoot.mul(element._2.get(i))));
                    }

                    return combinedNumbers.iterator();
                });

        /* Algorithm 2: Forward FFT, Reducer */
        return columnGroups.mapToPair(element -> {
            /* AbstractGroup the array of inputs into columns using the combiner. */
            final long group = element._1 / rows;
            final long index = element._1 % rows;

            return new Tuple2<>(group, new Tuple2<>(index, element._2));
        }).combineByKey(combine.createGroup, combine.mergeElement, combine.mergeCombiner)
                .mapValues(partition -> {
                    /* Compute the forward FFT, on domain size rows, for each group. */
                    ArrayList<FieldT> groupArray = Utils.convertFromPairs(partition, (int) rows);

                    if (inverse) {
                        rowDomain.radix2InverseFFT(groupArray);
                    } else {
                        rowDomain.radix2FFT(groupArray);
                    }

                    return groupArray;
                }).flatMapToPair(element -> {
                    /* Serialize and order evaluation results. */
                    final long index = element._1;

                    ArrayList<Tuple2<Long, FieldT>> outputs = new ArrayList<>();
                    for (int i = 0; i < rows; i++) {
                        outputs.add(new Tuple2<>(i * columns + index, element._2.get(i)));
                    }

                    return outputs.iterator();
                });
    }

    /**
     * Translate the vector input to a coset defined by g. Result is stored in ArrayList input.
     */
    static <FieldT extends AbstractFieldElementExpanded<FieldT>> void multiplyByCoset(
            final List<FieldT> input,
            final FieldT g) {
        FieldT coset = g;
        for (int i = 1; i < input.size(); ++i) {
            input.set(i, input.get(i).mul(coset));
            coset = coset.mul(g);
        }
    }

    /**
     * A distributed version of multiplyByCoset.
     */
    static <FieldT extends AbstractFieldElementExpanded<FieldT>> JavaPairRDD<Long, FieldT>
    distributedMultiplyByCoset(
            final JavaPairRDD<Long, FieldT> input,
            final FieldT g) {

        return input.mapToPair(term -> new Tuple2<>(term._1, term._2.mul(g.pow(term._1))));
    }

    /**
     * Compute the m Lagrange coefficients, relative to the set S={omega^{0},...,omega^{m-1}}, at the
     * field element t.
     */
    static <FieldT extends AbstractFieldElementExpanded<FieldT>> List<FieldT>
    serialRadix2LagrangeCoefficients(
            final FieldT t,
            final int m) {
        if (m == 1) {
            final List<FieldT> coeff = new ArrayList<>();
            coeff.add(t.one());
            return coeff;
        }

        assert (m == (1 << MathUtils.log2(m)));

        final FieldT omega = t.rootOfUnity(m);
        final FieldT one = t.one();

        List<FieldT> lagrangeCoefficients = new ArrayList<>(Collections.nCopies(m, t.zero()));

        /*
         * If t equals one of the roots of unity in S={omega^{0},...,omega^{m-1}}
         * then output 1 at the right place, and 0 elsewhere
         */
        if (t.pow(m).equals(one)) {
            FieldT omega_i = one;
            for (int i = 0; i < m; i++) {
                /* i.e., t equals omega^i */
                if (omega_i.equals(t)) {
                    lagrangeCoefficients.set(i, one);
                    return lagrangeCoefficients;
                }

                omega_i = omega_i.mul(omega);
            }
        }

        /*
         * Otherwise, if t does not equal any of the roots of unity in S,
         * then compute each L_{i,S}(t) as Z_{S}(t) * v_i / (t-\omega^i)
         * where:
         * - Z_{S}(t) = \prod_{j} (t-\omega^j) = (t^m-1), and
         * - v_{i} = 1 / \prod_{j \neq i} (\omega^i-\omega^j).
         * Below we use the fact that v_{0} = 1/m and v_{i+1} = \omega * v_{i}.
         */
        final FieldT Z = t.pow(m).sub(one);
        final FieldT mInverse = t.construct(m).inverse();
        FieldT l = Z.mul(mInverse);
        FieldT r = one;
        for (int i = 0; i < m; ++i) {
            lagrangeCoefficients.set(i, l.mul(t.sub(r).inverse()));
            l = l.mul(omega);
            r = r.mul(omega);
        }

        return lagrangeCoefficients;
    }

    /**
     * A distributed version of serialRadix2LagrangeCoefficients.
     */
    static <FieldT extends AbstractFieldElementExpanded<FieldT>> JavaPairRDD<Long, FieldT>
    distributedRadix2LagrangeCoefficients(
            final FieldT t,
            final long m,
            final Configuration config) {
        if (m == 1) {
            return config.sparkContext()
                    .parallelizePairs(Collections.singletonList(new Tuple2<>((long) 0, t.one())));
        }

        assert (m == (1 << MathUtils.log2(m)));

        final FieldT omega = t.rootOfUnity(m);
        final FieldT one = t.one();
        final FieldT zero = t.zero();

        /*
         * If t equals one of the roots of unity in S={omega^{0},...,omega^{m-1}}
         * then output 1 at the right place, and 0 elsewhere
         */
        if (t.pow(m).equals(one)) {
            return Utils.fillRDD(m, zero, config).mapToPair(term -> {
                if (!omega.pow(term._1).equals(t)) {
                    return term;
                }
                return new Tuple2<>(term._1, one);
            });
        }

        /*
         * Otherwise, if t does not equal any of the roots of unity in S,
         * then compute each L_{i,S}(t) as Z_{S}(t) * v_i / (t-\omega^i)
         * where:
         * - Z_{S}(t) = \prod_{j} (t-\omega^j) = (t^m-1), and
         * - v_{i} = 1 / \prod_{j \neq i} (\omega^i-\omega^j).
         * Below we use the fact that v_{0} = 1/m and v_{i+1} = \omega * v_{i}.
         */
        final FieldT Z = t.pow(m).sub(one);
        final FieldT mInverse = t.construct(m).inverse();
        final FieldT l = Z.mul(mInverse);

        return Utils.fillRDD(m, zero, config).mapToPair(term -> {
            final FieldT omega_i = omega.pow(term._1);
            final FieldT l_i = l.mul(omega_i);

            return new Tuple2<>(term._1, l_i.mul(t.sub(omega_i).inverse()));
        });
    }

    public static <FieldT extends AbstractFieldElementExpanded<FieldT>> HashMap<Long, FieldT>
    subsequenceRadix2LagrangeCoefficients(
            final FieldT t,
            final long m,
            final List<Long> indices) {

        assert (m == (1 << MathUtils.log2(m)));

        final HashMap<Long, FieldT> coefficients = new HashMap<>();

        final FieldT omega = t.rootOfUnity(m);
        final FieldT one = t.one();
        final FieldT zero = t.zero();

        /*
         * If t equals one of the roots of unity in S={omega^{0},...,omega^{m-1}}
         * then output 1 at the right place, and 0 elsewhere
         */
        if (t.pow(m).equals(one)) {
            for (Long index : indices) {
                if (omega.pow(index).equals(t)) {
                    coefficients.put(index, one);
                } else {
                    coefficients.put(index, zero);
                }
            }
            return coefficients;
        }

        /*
         * Otherwise, if t does not equal any of the roots of unity in S,
         * then compute each L_{i,S}(t) as Z_{S}(t) * v_i / (t-\omega^i)
         * where:
         * - Z_{S}(t) = \prod_{j} (t-\omega^j) = (t^m-1), and
         * - v_{i} = 1 / \prod_{j \neq i} (\omega^i-\omega^j).
         * Below we use the fact that v_{0} = 1/m and v_{i+1} = \omega * v_{i}.
         */
        final FieldT Z = t.pow(m).sub(one);
        final FieldT mInverse = t.construct(m).inverse();
        final FieldT l = Z.mul(mInverse);

        for (Long index : indices) {
            final FieldT omega_i = omega.pow(index);
            final FieldT l_i = l.mul(omega_i);

            coefficients.put(index, l_i.mul(t.sub(omega_i).inverse()));
        }
        return coefficients;
    }
}
