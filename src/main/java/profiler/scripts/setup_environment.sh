#!/usr/bin/env bash

# Create logging directory
mkdir /tmp/spark-events
export SPARK_EVENTS_DIR="/tmp/spark-events"
/root/spark-ec2/copy-dir /tmp/spark-events

# Install dependencies
sudo yum install -y tmux
sudo yum install -y java-1.8.0-openjdk-devel
sudo yum remove -y java-1.7.0-openjdk
export JAVA_HOME="/usr/lib/jvm/java-1.8.0"

pssh -h /root/spark-ec2/slaves yum install -y java-1.8.0-openjdk-devel
pssh -h /root/spark-ec2/slaves yum remove -y java-1.7.0-openjdk
pssh -h /root/spark-ec2/slaves export JAVA_HOME="/usr/lib/jvm/java-1.8.0"
