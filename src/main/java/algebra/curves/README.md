# algebra.curves

The set of points on an elliptic curve along with the usual addition rule ("chord and tangent") define a algebraic group.
As such, implementing a curve family (and/or a unique curve in a family) can be done by following the process described in [algebra.groups](../groups/README.md).
However, a few extra functions are added in the `AbstractG1`, `AbstractG2` etc. files that extend `AbstractGroup` to provide useful functions w.r.t elliptic curves.
As such, these classes are those that need to be inherited and instantiated when implementing a "concrete" elliptic curve.
