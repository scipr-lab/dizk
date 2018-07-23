# Fast Fourier Transform (FFT)

The FFT package provides a serial and parallel implementation of the radix-2 fast Fourier transform (FFT) using Apache Spark. For FFT evaluations over radix-2 and other domains, [libfqfft](https://github.com/scipr-lab/libfqfft) is a C++ library that implements FFT for finite fields and provides fast multipoint evaluation and interpolation with multithreading support usng OpenMP.

## Serial FFT
Serial FFT implements the iterative [Cooley-Tukey](https://en.wikipedia.org/wiki/Cooley%E2%80%93Tukey_FFT_algorithm) radix-2 FFT algorithm. In addition, FFT on a coset _g_ of the input is provided. The radix-2 domain implementation pseudocode can be found in [CLRS pp. 864].

The following are declarations of the static, in-place methods:

```java
void radix2FFT(ArrayList<FieldT> input);
void radix2InverseFFT(ArrayList<FieldT> input);
void radix2CosetFFT(ArrayList<FieldT> input, final FieldT g);
void radix2CosetInverseFFT(ArrayList<FieldT> input, final FieldT g);
ArrayList<FieldT> lagrangeCoeffs(final FieldT t, final int size);
FieldT computeZ(final FieldT t, final int size);
void addPolyZ(final FieldT coeff, ArrayList<FieldT> input, final int size);
void divideByZOnCoset(final FieldT coset, ArrayList<FieldT> input, final int size);
```

## Parallel FFT

Parallel FFT implements the MapReduce-based radix-2 FFT algorithm in [S11]. As with serial FFT, a coset FFT implementation is also provided. Our implementation uses Apache Spark to perform FFT evaluations in parallel across a compute cluster. In the parlance of Spark, our input is stored as a Resilient Distributed Dataset (RDD), where each field element is mapped to its index.

Given an input RDD of size _s_, the input is partitioned into an array of _r_ rows and _c_ columns, where _r_ and _c_ are powers of two with _rc = s_. The algorithm is broken into two parts: mapping and reducing. In the mapping phase, each row is evaluated on a _c_-point FFT, bitshifted, and multiplied by its respective root of unity.
In the reducing phase, the columns are evaluated on an _r_-point FFT and recombined into a . Observe that these evaluations can be performed in parallel.

The following are declarations of the static methods:
```java
JavaPairRDD<Long, FieldT> radix2FFT(final JavaPairRDD<Long, FieldT> input, final int rows, final int columns);
JavaPairRDD<Long, FieldT> radix2InverseFFT(final JavaPairRDD<Long, FieldT> input, final int rows, final int columns);
JavaPairRDD<Long, FieldT> radix2CosetFFT(final JavaPairRDD<Long, FieldT> input, final FieldT g, final int rows, final int columns);
JavaPairRDD<Long, FieldT> radix2CosetInverseFFT(final JavaPairRDD<Long, FieldT> input, final FieldT g, final int rows, final int columns);
```

## References

[CLRS] [_Introduction to Algorithms_](https://mitpress.mit.edu/books/introduction-algorithms), Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, and Clifford Stein, 2009

[S11] [_Schönhage-Strassen Algorithm with MapReduce
for Multiplying Terabit Integers_](https://people.apache.org/~szetszwo/ssmr20110429.pdf), Tsz-Wo Sze, 2011