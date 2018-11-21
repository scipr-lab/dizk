#!/usr/bin/env bash

. init.sh

# Get master node URL
readonly MASTER=`$SPARK_EC2_PATH -k $AWS_KEYPAIR_NAME -i $AWS_KEYPAIR_PATH --region=$AWS_REGION_ID get-master $AWS_CLUSTER_NAME | grep amazonaws.com`

# Transfer logs back to local
scp -i $AWS_KEYPAIR_PATH -r ec2-user@$MASTER:/tmp/spark-events/src/main/resources/logs/ $DIZK_REPO_PATH/out/