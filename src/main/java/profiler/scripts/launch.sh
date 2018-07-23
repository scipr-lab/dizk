#!/usr/bin/env bash

. init.sh

# Instantiate EC2 cluster
$SPARK_EC2_PATH \
  --use-existing-master \
  --key-pair=$AWS_KEYPAIR_NAME \
  --identity-file=$AWS_KEYPAIR_PATH \
  --region=$AWS_REGION_ID \
  --spot-price=$SPOT_PRICE \
  --slaves=$SLAVES_COUNT \
  --instance-type=$INSTANCE_TYPE \
  --deploy-root-dir=$DIZK_REPO_PATH/src/main/java/profiler/scripts \
  --spark-version=2.1.0 \
  launch $AWS_CLUSTER_NAME
