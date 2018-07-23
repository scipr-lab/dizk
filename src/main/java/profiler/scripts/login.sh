#!/usr/bin/env bash

. init.sh

# Login
$SPARK_EC2_PATH \
  --key-pair=$AWS_KEYPAIR_NAME \
  --identity-file=$AWS_KEYPAIR_PATH \
  --region=$AWS_REGION_ID \
  login $AWS_CLUSTER_NAME