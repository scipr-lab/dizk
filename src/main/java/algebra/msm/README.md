# Multi-Scalar Multiplication (MSM)

The MSM package provides serial and parallel implementations of fixed-base and variable-base multi-scalar multiplication using Apache Spark.


## Fixed Base

The fixed-base multi-scalar multiplication is defined as follows: given a group _G_, an element _P_ in _G_, and scalars [_a1, ... , an_] in _Z^n_, compute [_a1 * P_, ... , _an * P_] in G^n.


## Variable Base

The variable-base multi-scalar multiplication is defined as follows: given a group _G_, elements [_P1_, ... , _Pn_] in _G^n_, and scalars [_a1, ... , an_] in _Z^n_, compute [_a1 * P1_, ... , _an * Pn_] in G^n.