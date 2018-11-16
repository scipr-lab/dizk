FROM java:latest

ENV MAVEN_VERSION 3.3.9

RUN mkdir -p /usr/share/maven
RUN curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar -xzC /usr/share/maven --strip-components=1
RUN ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
WORKDIR /home/dizk

COPY pom.xml .
RUN mvn compile

COPY . .

VOLUME /root/.m2

CMD ["mvn"] 