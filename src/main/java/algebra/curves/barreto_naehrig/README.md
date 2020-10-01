# Barreto-Naehrig curves

This folder contains implementations and parameters for the following Barreto-Naehrig curves:

|    BN254a    | Order                                                              | # Bits     | 2-adicity |
|--------------|--------------------------------------------------------------------|------------|-----------|
| Base Field   | 0x30644e72e131a029b85045b68181585d97816a916871ca8d3c208c16d87cfd47 | 254        | 1         |
| Scalar Field | 0x30644e72e131a029b85045b68181585d2833e84879b9709143e1f593f0000001 | 254        | 28        |

|    BN254b    | Order                                                              | # Bits     | 2-adicity |
|--------------|--------------------------------------------------------------------|------------|-----------|
| Base Field   | 0x277a0785027142409a4f160886e00001921d70600000000188f4000000000001 | 254        | 50        |
| Scalar Field | 0x277a0785027142409a4f160886e000012d9614480000000188f4000000000001 | 254        | 50        |

The first curve - `BN254a` - is used mostly for testing.
The second curve - `BN254b` - is used for zkSNARKs on large instances. Indeed, the high 2-adicity of the scalar field enables efficient FFTs for very large domains (of size up to `2^50`) over the scalar field.