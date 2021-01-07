# zkSNARK.grothBGM17

This folder contains an implementation of **a variant** of the publicly-verifiable preprocessing zkSNARK [\[Gro16\]](https://eprint.iacr.org/2016/260) proposed by Bowe et al. [\[BGM17\]](https://eprint.iacr.org/2017/1050.pdf).
The NP relation supported by this protocol is *Rank-1 Constraint Satisfaction* (R1CS).

## CRS structure modifications

```
- \gamma elements are removed
- Additional "powers of tau" are computed during the MPC
- {[A_i(x)]_1}_{i=0}^{m}, {[B_i(x)]_2}_{i=0}^{m} are added to the SRS to fasten the Prover algorithm
```