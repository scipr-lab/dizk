# algebra.groups

This folder contains the definition of `AbstractGroup` which defines the interface that needs to be implemented by groups.

An example implementation of a group can be found in `mock`.
As illustrated there, to implement a new group, one needs to:
1. Create a new folder (e.g. `mygroup`).
1. Create `mygroup/MyGroup.java` that holds the generic implementation of the group. To do so, inherit from the `AbstractGroup` class and implement all its functions, e.g.
```java
public class MyGroup extends AbstractGroup<MyGroup> {
    // Implement the abstract functions defined in AbstractGroup.java
    // in order to define the group operations etc.
}
```
2. Instantiate the specific group of interest by setting its parameters. *Note:* if the group is a particular instantiation in a family (e.g. a specific curve in a curve family) it is worth exposing some generic interface to manipulate the group parameters outside of the package. To do so:
    - Create `mygroup/abstractparameters/parameters.java` to hold the common interface to expose the parameters of the various group instantiation
    - Instantiate the specific group's (e.g. `MyGroup1`) parameters `mygroup/mygroup1parameters/parameters.java` by inheriting from the abstract class `mygroup.AbstractParameters.Parameters` and implementing the appropriate functions, e.g.
```java
public class MyGroupParameters extends AbstractGroupParameters {
    // Implement the abstract functions defined in AbstractGroupParameters.java
    // in order to define the specific group's parameters etc.
}
```
