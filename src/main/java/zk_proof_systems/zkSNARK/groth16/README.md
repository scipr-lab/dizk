# zkSNARK.groth16

This folder contains an implementation of a publicly-verifiable preprocessing zkSNARK of Groth [\[Gro16\]](https://eprint.iacr.org/2016/260).
The NP relation supported by this protocol is *Rank-1 Constraint Satisfaction* (R1CS).

```
Let \REL be defined as:
\REL = (\FF, inp, {A_i(x), B_i(x), C_i(x)}_{i=0}^{m}, t(x))

where:
- inp = prim_inputs \cup aux_inputs and where \inp_0 = \FF.mult_id (i.e. one)
- prim_inputs = (inp_1, ..., inp_l), i.e. l = |prim_inputs|
- aux_inputs = (inp_{l+1}, ..., inp_m), i.e. m-l = |aux_inputs|, m = |wires|

\sum_{i=0}^{m} inp_i * A_i(x) * \sum_{i=0}^{m} inp_i * B_i(x) = \sum_{i=0}^{m} inp_i * C_i(x) + h(x)t(x)

- deg(h(x)) = n-2
- deg(t(x)) = n
```

## Setup

```
(\crs, \trap) \gets Setup(R)

where:
- \trap \gets (\alpha, \beta, \gamma, \delta, x)
- \crs \gets (\crs_1, \crs_2), where \crs_1 (resp. \crs_2) is a set of \GG_1 (resp. G2) elements

\crs_1 = \alpha, \beta, \delta, {x^i}_{i=0}^{n-1}, {(\beta * A_i + \alpha * B_i + C_i) / \gamma}_{i=0}^{l}, {(\beta * A_i + \alpha * B_i + C_i) / \delta}_{i=l+1}^{m}, {(x^i * t(x)) / \delta}_{i=0}^{n-2}

\crs_2 = \beta, \gamma, \delta, {x^i}_{i=0}^{n-1}
```

## Prove

```
(\pi) \gets Prove(R, \crs, {inp_1, ..., inp_m})

\pi = (A, B, C)

where:
- r, s \sample \FF
- A = \alpha + \sum_{i=0}^{m} inp_i*A_i(x) + r*\delta, A in \GG_1
- B = \beta + \sum_{i=0}^{m} inp_i*B_i(x) + s*\delta, B in \GG_2
- C = [\sum_{i=l+1}^{m} inp_i * (\beta * A_i(x) + \alpha * B_i(x) + C_i(x)) + h(x)t(x)] / \delta + A*s + B*r - rs*\delta
```

## Verify

```
(\res) \gets Verify(R, \crs, {inp_1, ..., inp_l}, \pi)

\res \in \{0,1\}

and where:
\res = Pairing(\pi.A, \pi.B) * Pairing(\sum_{i=0}^{l} inp_i *(\beta*A_i(x) + \alpha * B_i(x) + C_i(x)) / \gamma, \gamma) * Pairing(\pi.C, \delta)
```