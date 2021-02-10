# DIZK profiling

## Profiler hanging

- When running the distributed zkSNARK algorithms, the profiler (and your interactions from the terminal) may hang. This is often a sign that your cluster has hit its memory limit (read: you are running an instance size that is too big for the given memory resources). You should try summoning larger instances or specify more memory for each instance in order to progress from this issue.

# Tips - debugging inside EC2-cluster

## Clone and recompile inside the server

It could be useful to clone and recompile the project right inside the server

Install Maven (from .tar)
```
export MAVEN_VERSION=3.6.0
mkdir -p /usr/share/maven
curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar -xzC /usr/share/maven --strip-components=1
ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
export MAVEN_HOME=/usr/share/maven
```

## Compile without running or testing

Compile the project without running or compiling unit tests.

- To skip running and compiling `mvn -Dmaven.test.skip=true install`
- To compile, but not run unit tests `mvn install -DskipTests`
