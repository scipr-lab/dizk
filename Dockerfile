FROM ubuntu:20.04

# https://github.com/moby/moby/issues/4032#issuecomment-163689851
# https://github.com/moby/moby/issues/4032#issuecomment-192327844
ARG DEBIAN_FRONTEND=noninteractive

# Configure Java
RUN apt update && apt upgrade -y
RUN apt install -y \
    openjdk-11-jdk \
    curl \
    tar

# Fetch Maven
ENV MAVEN_VERSION 3.6.3
RUN mkdir -p /usr/share/maven
RUN curl -fsSL https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    | tar -xzC /usr/share/maven --strip-components=1
RUN ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

# Create /home/dizk and use it as working directory
WORKDIR /home/dizk
COPY . /home/dizk
# RUN git submodule update --init --recursive
# RUN mvn compile

# Mount the user-specific Maven configuration for Maven in the container
# See: https://maven.apache.org/ref/3.6.3/maven-settings/settings.html for more information
VOLUME /root/.m2

CMD ["/bin/bash"]
