#!/usr/bin/env bash

. init.sh

$SPARK_EC2_PATH \
  --key-pair=$AWS_KEYPAIR_NAME \
  --identity-file=$AWS_KEYPAIR_PATH \
  --region=$AWS_REGION_ID \
  destroy $AWS_CLUSTER_NAME
