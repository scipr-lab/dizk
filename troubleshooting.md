


# DIZK profiling

## macOS issues

Some users have reported issues running DIZK on macOS. A Docker container can be used to work around this issue.

```
cd your_dizk_project_directory

docker build -t dizk-container .
docker run -it dizk-container bash
```

- Launch script requires editing `dizk/depends/spark-ec2/spark_ec2.py' because of depreciated boto version.
- Launching spark on AWS-EC2 requires specific region where spark images are deployable: 
	- See [AMI-LIST](https://github.com/amplab/spark-ec2/tree/branch-2.0/ami-list)
- Also the launch script expects to have credentials for using **AWS EC2 spot instances** (not just EC2).
- profiler.sh had wrong specification path (depending on your `pwd`) and line 4 was changed to 

```
/root/spark-ec2/copy-dir /home/ec2-user/
```
- Line 11 in [profile.sh](https://github.com/scipr-lab/dizk/blob/e98dd9cba0a3ec99403b191133003aeef94b1e8a/src/main/java/profiler/scripts/profile.sh#L11) should be set to a string from the list described in the switch statement in [Profiler.java](https://github.com/scipr-lab/dizk/blob/e98dd9cba0a3ec99403b191133003aeef94b1e8a/src/main/java/profiler/Profiler.java#L41-L71)
	- [FixMe] This should throw an error.

## Profiler hanging

- When running the distributed zkSNARK algorithms, the profiler (and your interactions from the terminal) may hang. This is often a sign that your cluster has hit its memory limit (read: you are running an instance size that is too big for the given memory resources). You should try summoning larger instances or specify more memory for each instance in order to progress from this issue.

# Tips - debugging inside EC2-cluster

## Clone and recompile inside the server

It could be useful to clone and recompile the project right inside the server

Install Maven (from .tar)
```
export MAVEN_VERSION=3.3.9
mkdir -p /usr/share/maven
curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar -xzC /usr/share/maven --strip-components=1
ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
export MAVEN_HOME=/usr/share/maven
```

## Setting `JAVA_HOME`

Because of how `spark-ec2` instantiates the environment, an older version of Java is installed by default. We override this by introducing Java 8 (see `setup_environment.sh`). Some users have reported that the setup script fails to set the correct `JAVA_HOME`. If you run into this issue, set the following command:

```
export JAVA_HOME="/usr/lib/jvm/java-1.8.0"
```

## Compile without running or testing

Compile the project without running or compiling unit tests.

- To skip running and compiling `mvn -Dmaven.test.skip=true install`
- To compile, but not run unit tests `mvn install -DskipTests`

## Miscellaneous

Copy the **jar** file to `/home/ec2-user/` (because this is where the profiler scripts are looking for it.

Notice that the $SIZE in profile.sh must be greater than the hard-coded values for number of inputs (1023) that are provided in the R1CSConstruction class. 

[This](https://spark.apache.org/docs/1.6.2/ec2-scripts.html) is useful.

Mostly because you can view the status of the cluster via: `http://<master-hostname>:8080`
