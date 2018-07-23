# Non-Interactive Zero Knowledge Proof (NIZK)


An arithmetic circuit consists of a set of gates and wires that evaluate inputs over a field _F_, returning a single field element representing the evaluation of the circuit. With respect to an arbitrary basis, an arithmetic circuit can be represented by a __Rank-1 Constraint System (R1CS)__ as follows.

For each product gate, we consider polynomials _U, V,_ and _W_ where _U_ represents the left inputs, _V_ represents the right inputs, and _W_ represents the evaluation output. Given _i_ inputs and _j_ product gates, we consider _m = i + j_ wires, where for any _k_ in _range(m)_ and root _r_, we have  _U\_k(r)_ = 1 if the _k_-th wire contributes to the product gate with root _r_, otherwise _U\_k(r)_ = 0. Similarly _V\_k(r)_ = 1 for relevant right inputs and _W\_k(r)_ = 1 if the _k_-th wire corresponds to the output of the product gate associated with the root _r_. Now the circuit is equivalent to a system of quadratic equations where,
```
{ < U_k , X > * < V_k , X > = < W_k , X > }_{k=1}^{m}.
```
In this representation, we see that addition is given for free from the sums the define the equation. If _x\_i_ + _x\_j_ = _x\_k_ and _x\_k_ is multiplied with _x\_l_, then our representation disregards the calculation of _x\_k_ by simply computing (_x\_i_ + _x\_j_) * _x\_l_. Hence, this _k_-th wire is not considered in the _m_ wires.

The above Rank-1 Constraint System can be converted into a __Quadratic Arithmetic Program (QAP)__ consisting of three sets of _m+1_ polynomials _U(x)_, _V(x)_, and _W(x)_, and a target polynomial _t(x)_ of degree _m_. Given _m_ arbitrary roots _r\_1_, ... , _r\_m_ in our field _F_, if _Y = [y_1, ... , y_N]_ is a valid input assignment, then,
```
(sum_{k=0}^{m} (U_k(x) * y_k)) * (sum_{k=0}^{m} (V_k(x) * y_k)) - (sum_{k=0}^{m} (W_k(x) * y_k)) = h(x) * t(x)
```
where _t(x) = (x - r_1) ... (x - r_m)_ and _t(x)_ divides the left hand side.

## References

[Groth16] [_On the Size of Pairing-based Non-interactive Arguments_](https://eprint.iacr.org/2016/260.pdf), Jens Groth, IACR 2016

[GGPR13] [_Quadratic Span Programs and Succinct NIZKs without PCPs_](https://eprint.iacr.org/2012/215.pdf), Rosario Gennaro, Craig Gentry, Bryan Parno, Mariana Raykova, EUROCRYPT 2013

[PGHR13] [_Pinocchio: Nearly Practical Verifiable Computation_](https://eprint.iacr.org/2013/279.pdf), Bryan Parno, Craig Gentry, Jon Howell, Mariana Raykova, IEEE Symposium on Security and Privacy (Oakland) 2013