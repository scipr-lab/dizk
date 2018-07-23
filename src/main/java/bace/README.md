# Batch Arithmetic Circuit Evaluation (BACE)

The BACE package provides a parallel implementation of the Merlin-Arthur proof system presented in the paper "Strong ETH Breaks With Merlin and Arthur: Short Non-Interactive Proofs of Batch Evaluation" (Williams, CCC 2016). An efficient, serial implementation is available from [bace](https://github.com/scipr-lab/bace), a C++ library for evaluating arithmetic circuits on batches of inputs.


## Arithmetic Circuit

An arithmetic circuit is a graph-based representation of a polynomial using sum, product, and constant gates. Given a set of inputs, the arithmetic circuit will evaluate the inputs over its set of gates, returning a single output as the result.

In this package, we provide the following concrete gates: `ConstantGate`, `InputGate`, `ProductGate`, and `SumGate`, that can be used to construct an arithmetic circuit `Circuit`. Given an input vector _inputs_, the circuit evaluation is performed on a constructed circuit _C_ by invoking `C.compute(inputs)`.


## Proof System

The proof system consists of a single message from the (deterministic) prover to the (probabilistic) verifier. The statement being proved is about the correctness of evaluating an arithmetic circuit on a batch of inputs.

Our implementation uses Apache Spark to perform distributed operations on a Resilient Distributed Dataset (RDD) and uses the MapReduce-based FFT implementation provided by the FFT package. In addition, a naive evaluator of the input RDD is provided for benchmarking purposes.