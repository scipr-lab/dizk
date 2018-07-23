<h1 align="center">DIZK</h1>
<p align="center">
    <a href="https://travis-ci.org/scipr-lab/dizk"><img src="https://travis-ci.org/scipr-lab/dizk.svg?branch=master"></a>
    <a href="https://github.com/scipr-lab/dizk/AUTHORS"><img src="https://img.shields.io/badge/authors-SCIPR%20Lab-orange.svg"></a>
    <a href="https://github.com/scipr-lab/dizk/LICENSE"><img src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
</p>
<h4 align="center">Java library for distributed zero knowledge proof systems</h4>

___DIZK___ (pronounced */'disək/*) is a Java library for distributed zero knowledge proof systems. The library implements distributed multipoint polynomial evaluation, distributed polynomial interpolation, and distributed computation of Lagrange polynomials.
Using these scalable arithmetic computations, the library provides a distributed zkSNARK proof system that enables verifiable computations of up to billions of logical gates, far exceeding the scale of previous state-of-the-art solutions.

The library is developed by [SCIPR Lab](http://www.scipr-lab.org/) and contributors (see [AUTHORS](AUTHORS) file) and is released under the MIT License (see [LICENSE](LICENSE) file).

The library is developed as part of a conference paper called, *"[DIZK: A Distributed Zero Knowledge Proof System](https://eprint.iacr.org/2018/691)."*

**WARNING:** This is an academic proof-of-concept prototype. This implementation is not ready for production use. It does not yet contain all the features, careful code review, tests, and integration that are needed for a deployment!

## Table of contents

- [Directory structure](#directory-structure)
- [Overview](#overview)
- [Build guide](#build-guide)
- [Profiler](#profiler)
- [Benchmarks](#benchmarks)
- [References](#references)
- [License](#license)

## Directory structure

The directory structure is as follows:

* [__src__](src): Java directory for source code and unit tests
  * [__main/java__](src/main/java): Java source code, containing the following modules:
    * [__algebra__](src/main/java/algebra): Fields, groups, elliptic curves, FFT, multi-scalar multiplication
    * [__bace__](src/main/java/bace): Batch arithmetic circuit evaluation
    * [__common__](src/main/java/common): Standard arithmetic and Spark computation utilities
    * [__configuration__](src/main/java/configuration): Configuration settings for the Spark cluster
    * [__profiler__](src/main/java/profiler): Profiling infrastructure for zero-knowledge proof systems
    * [__reductions__](src/main/java/reductions): Reductions between languages (used internally)
    * [__relations__](src/main/java/relations): Interfaces for expressing statement (relations between instances and witnesses) as various NP-complete languages
    * [__zk_proof_systems__](src/main/java/zk_proof_systems): Serial and distributed implementations of zero-knowledge proof systems
  * [__test/java__](src/test/java): Java unit tests for the provided modules and infrastructure

## Overview

This library implements distributed zero knowledge proof systems that enable scalable approaches for proving and verifying,
in zero knowledge, the integrity of computations.

A prover who knows the witness for an NP statement (i.e., a satisfying input/assignment) can produce a short proof attesting to the truth of the NP statement. This proof can then be verified by anyone, and offers the following properties.

- **Zero knowledge** - the verifier learns nothing from the proof besides the truth of the statement.
- **Succinctness** - the proof is small in size and quick to verify.
- **Non-interactivity** - the proof does not require back-and-forth interaction between the prover and the verifier.
- **Soundness** - the proof is computationally sound (such a proof system is called an argument).
- **Proof of knowledge** - the proof attests not just that the NP statement is true, but also that the prover knows why.

These properties comprise a **zkSNARK**, which stands for Zero-Knowledge Succinct Non-interactive ARgument of Knowledge
(though zkSNARKs are also knows as succinct non-interactive computationally-sound zero-knowledge proofs of knowledge).
For formal definitions and theoretical discussions about these, see [BCCT12], [BCIOP13], and the references therein.

**DIZK** provides Java-based implementations using Apache Spark [Apa17] for:

1. General-purpose proof systems
    - A serial and distributed preprocessing zkSNARK for the NP-complete language, *R1CS* (Rank-1 Constraint Systems), a language that resembles arithmetic circuit satisfiability, see [Gro16].
    - A distributed Merlin-Arthur proof system for evaluating arithmetic circuits on batches of inputs, see [Wil16].
2. Scalable arithmetic computations
    - A serial and distributed radix-2 fast Fourier transform (FFT), see [Sze11].
    - A serial and distributed multi-scalar multiplication (MSM), see [BGMW93] [Pip76] [Pip80].
    - A serial and distributed Lagrange interpolation (Lag), see [BT04].
3. Applications using the above proof systems for
    - Authenticity of photos on three transformations - crop, rotation, and blur, see [NT16].
    - Integrity of machine learning models with support for linear regression and covariance matrices, see [Bis06] [Can69] [LRF97] [vW97].

## Build guide

The library has the following dependencies:

- [Java SE 8+](http://www.oracle.com/technetwork/java/javase/overview/index.html)
- [Apache Maven](https://maven.apache.org/)
- Fetched from `pom.xml` via Maven:
    - [Spark Core 2.10](https://mvnrepository.com/artifact/org.apache.spark/spark-core_2.10/1.0.0)
    - [Spark SQL 2.10](https://mvnrepository.com/artifact/org.apache.spark/spark-sql_2.10/2.1.0)
    - [JUnit 4.11](https://mvnrepository.com/artifact/junit/junit/4.11)
    - [Google Java Format](https://github.com/google/google-java-format)
- Fetched via Git submodules:
    - [spark-ec2](https://github.com/amplab/spark-ec2/tree/branch-2.0)
    
### Why Java?

This library was designed and implemented in Java to utilize Apache Spark, which currently supports Java, Python, and Scala.
Our choice of Java lies in our need to architect a system that is accurate and high performance. This meant foregoing potential
higher-level code optimizations in favor of unambiguous lower-level program execution. Our study of the three language candidates
resulted in our choice of Java as the most viable candidate for enforcing these requirements.

### Installation

Start by cloning this repository and entering the repository working directory:
```$xslt
git clone https://github.com/scipr-lab/dizk.git
cd dizk
```

Next, fetch the dependency modules:
```$xslt
git submodule init && git submodule update
```

Lastly, compile the source code:
```$xslt
mvn compile
```

### Testing

This library comes with unit tests for each of the provided modules. Run the tests with:
```$xslt
mvn test
``` 

## Profiler

Using Amazon EC2, the profiler benchmarks the performance of serial and distributed zero-knowledge proof systems, as well as its underlying primitives.
The profiler makes use of `spark-ec2` to manage the cluster compute environment and a set of provided scripts for easy launch, profiling, and shutdown.

### Spark EC2

To manage the cluster compute environment, DIZK makes use of [`spark-ec2@branch-2.0`](https://github.com/amplab/spark-ec2/tree/branch-2.0).
`spark-ec2` is a tool to launch, maintain, and terminate [Apache Spark](https://spark.apache.org/docs/latest/) clusters on Amazon EC2.

To setup `spark-ec2`, run the following commands:
```$xslt
git clone https://github.com/amplab/spark-ec2.git
cd spark-ec2
git checkout branch-2.0
pwd
```

Remember where the directory for `spark-ec2` is located, as this will need to be provided as an environment variable for the scripts as part of the next step.

### Profiling Scripts

To begin, set the environment variables required to initialize the profiler in [init.sh](src/main/java/profiler/scripts/init.sh).
The profiling infrastructure will require access to an AWS account access key and secret key, which can be created with
the [instructions provided by AWS](https://docs.aws.amazon.com/general/latest/gr/aws-sec-cred-types.html#access-keys-and-secret-access-keys).

```$xslt
export AWS_ACCESS_KEY_ID={Insert your AWS account access key}
export AWS_SECRET_ACCESS_KEY={Insert your AWS account secret key}

export AWS_KEYPAIR_NAME="{Insert your AWS keypair name, e.g. spark-ec2-oregon}"
export AWS_KEYPAIR_PATH="{Insert the path to your AWS keypair .pem file, e.g. /Users/johndoe/Downloads/spark-ec2-oregon.pem}"

export AWS_REGION_ID={Insert your AWS cluster region choice, e.g. us-west-2}
export AWS_CLUSTER_NAME={Insert your AWS cluster name, e.g. spark-ec2}

export SPOT_PRICE={Insert your spot price for summoning an EC2 instance, e.g. 0.1}
export SLAVES_COUNT={Insert the number of EC2 instances to summon for the cluster, e.g. 2}
export INSTANCE_TYPE={Insert the instance type you would like to summon, e.g. r3.large}

export DIZK_REPO_PATH="{Insert the path to your local DIZK repository, e.g. /Users/johndoe/dizk}"
export SPARK_EC2_PATH="{Insert the path to your local spark-ec2 repository, e.g. /Users/johndoe/dizk/depends/spark-ec2}"
```

Next, start the profiler by running:
```$xslt
./launch.sh
```

The launch script uses `spark-ec2` and the environment variables to setup the initial cluster environment.
This process takes around 20-30 minutes depending on the choice of cluster configuration.

After the launch is complete, upload the DIZK JAR file to the master node and SSH into the cluster with the following command:
```$xslt
./upload_and_login.sh
```

Once you have successfully logged in to the cluster, navigate to the uploaded `scripts` folder and setup the initial cluster environment.

```$xslt
cd ../scripts
./setup_environment.sh
```

This creates a logging directory for Spark events and installs requisite dependencies, such as Java 8.

Lastly, with the cluster environment fully setup, set the desired parameters for benchmarking in [profile.sh](src/main/java/profiler/scripts/profile.sh) and run the following command to begin profiling:
```$xslt
./profile.sh
```

## Benchmarks

We evaluate the distributed implementation of the zkSNARK setup and prover.
Below we use *instance size* to denote the number of constraints in an R1CS instance.

### libsnark *vs* DIZK

First, we measure the largest instance size (as a power of 2) that is supported by:

- the serial implementation of Groth’s protocol in [libsnark](https://github.com/scipr-lab/libsnark) [SCI17]
- the distributed implementation of Groth's protocol in **DIZK**

<p align="center"><img src="https://user-images.githubusercontent.com/9260812/43099291-9203db9a-8e76-11e8-8d68-528d903500e1.png" width="68%"></p>

We see that using more executors allows us to support larger instance sizes,
in particular supporting billions of constraints with sufficiently many executors.
Instances of this size are much larger than what was previously possible via serial techniques.

### Distributed zkSNARK

Next, we benchmark the running time of the setup and the prover on an increasing number of constraints and with an increasing number of executors.
Note that we do not need to evaluate the zkSNARK verifier as it is a simple and fast algorithm that can be run even on a smartphone.

<p align="center"><img src="https://user-images.githubusercontent.com/9260812/43099290-91ec40c0-8e76-11e8-8391-c30fbddc4acd.png" width="67%"></p>

<p align="center"><img src="https://user-images.githubusercontent.com/9260812/43099289-91d1d2b2-8e76-11e8-9a25-f06103903290.png" width="59%"></p>

Our benchmarks of the setup and the prover show us that:
 
1. For a given number of executors, running times increase nearly linearly as expected, demonstrating scalability over a wide range of instance sizes.

2. For a given instance size, running times decrease nearly linearly as expected, demonstrating parallelization over a wide range of number of executors.

## References

[Apa17] [_Apache Spark_](http://spark.apache.org/), Apache Spark, 2017

[Bis06] [_Pattern recognition and machine learning_](https://www.springer.com/us/book/9780387310732), Christopher M. Bishop, 2006

[BCCT12] [_From extractable collision resistance to succinct non-interactive arguments of knowledge, and back again_](http://eprint.iacr.org/2011/443),
  Nir Bitansky, Ran Canetti, Alessandro Chiesa, Eran Tromer,
  Innovations in Computer Science (ITCS) 2012

[BCIOP13] [_Succinct non-interactive arguments via linear interactive proofs_](http://eprint.iacr.org/2012/718),
Nir Bitansky, Alessandro Chiesa, Yuval Ishai, Rafail Ostrovsky, Omer Paneth,
Theory of Cryptography Conference (TCC) 2013

[BGMW93] [_Fast exponentiation with precomputation_](https://link.springer.com/chapter/10.1007/3-540-47555-9_18),
Ernest F. Brickell, Daniel M. Gordon, Kevin S. McCurley, and David B. Wilson,
International Conference on the Theory and Applications of Cryptographic Techniques (EUROCRYPT) 1992

[BT04] [_Barycentric Lagrange interpolation_](https://people.maths.ox.ac.uk/trefethen/barycentric.pdf), Jean-Paul Berrut and Lloyd N. Trefethen, 2004

[Can69] [_A cellular computer to implement the Kalman filter algorithm_](https://dl.acm.org/citation.cfm?id=905686), Lynn E Cannon, 1969

[Gro16] [_On the size of pairing-based non-interactive arguments_](https://eprint.iacr.org/2016/260.pdf),
Jens Groth,
International Conference on the Theory and Applications of Cryptographic Techniques (EUROCRYPT) 2016

[LRF97] [_Generalized cannon’s algorithm for parallel matrix multiplication_](https://dl.acm.org/citation.cfm?id=263591),
Hyuk-Jae Lee, James P. Robertson, and Jose ́ A. B. Fortes,
International Conference on Supercomputing 1997

[NT16] [_Photoproof: Cryptographic image authentication for any set of permissible transformations_](https://www.cs.tau.ac.il/~tromer/papers/photoproof-oakland16.pdf).
Assa Naveh and Eran Tromer,
IEEE Symposium on Security and Privacy 2016

[Pip76] [_On the evaluation of powers and related problems_](https://ieeexplore.ieee.org/document/4567910/),
Nicholas Pippenger,
Symposium on Foundations of Computer Science 1976

[Pip80] [_On the evaluation of powers and monomials_](https://pdfs.semanticscholar.org/7d65/53e185fd90a855717ee915992e17f38c99ae.pdf)
Nicholas Pippenger,
SIAM Journal on Computing 1980

[SCI17] [_libsnark: a C++ library for zkSNARK proofs_](https://github.com/scipr-lab/libsnark), SCIPR Lab, 2017

[Sze11] [_Schönhage-Strassen algorithm with MapReduce for multiplying terabit integers_](https://people.apache.org/~szetszwo/ssmr20110429.pdf),
Tsz-Wo Sze, 2011

[vW97] [_SUMMA: scalable universal matrix multiplication algorithm_](https://dl.acm.org/citation.cfm?id=899248), Robert A. van de Geijn and Jerrell Watts, 1997

[Wil16] [_Strong ETH breaks with Merlin and Arthur: Short non-interactive proofs of batch evaluation_](https://arxiv.org/pdf/1601.04743.pdf),
Ryan Williams, 2016

## Acknowledgements

This work was supported by Intel/NSF CPS-Security grants,
the [UC Berkeley Center for Long-Term Cybersecurity](https://cltc.berkeley.edu/),
and gifts to the [RISELab](https://rise.cs.berkeley.edu/) from Amazon, Ant Financial, CapitalOne, Ericsson, GE, Google, Huawei, IBM, Intel, Microsoft, and VMware.
The authors thank Amazon for donating compute credits to RISELab, which were extensively used in this project.

## License

[MIT License](LICENSE)
