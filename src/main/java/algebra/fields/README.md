# algebra.fields

This folder contains:
- The definition of `AbstractFieldElement` which defines the interface that needs to be implemented by fields (this interface is extended in `AbstractFieldElementExpanded`)
- The implementation of:
    - Finite field `Fp`, p prime
    - Extension fields `Fp^n`, p prime, n \in \NN
    - The field of complex numbers \CC

In order to instantiate the field implementations above, one needs to provide the "field parameters" for the specific instantiation (e.g. provide a value for `p`, the identities etc.). This is done by inheriting and instantiating the classes in `abstractfieldparameters`.

An example implementation of fields can be found in `mock`.

