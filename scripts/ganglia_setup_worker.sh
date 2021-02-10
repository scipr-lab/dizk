#!/usr/bin/env bash

# Expect 2 non-empty arguments
if [ "$#" -ne 2 ] || [ "$1" == "" ] || [ "$2" == "" ] ; then
    echo "Error: invalid number of arguments"
    echo "Usage: $0 <cluster-name> <gmetad-host>"
    echo ""
    exit 1
fi

CLUSTER_NAME=$1
GMETAD_HOST=$2

# Install ganglia dependencies
echo "INFO: install ganglia dependencies"
yum update -y
amazon-linux-extras install -y epel
yum install -y ganglia ganglia-gmond

echo "INFO: edit gmond.conf"
sed -i 's/  name = "unspecified"/  name = '\"$CLUSTER_NAME\"'/g' /etc/ganglia/gmond.conf
sed -i "0,/  mcast_join = /! {0,/  mcast_join = / s/  mcast_join = /#mcast_join = /}" /etc/ganglia/gmond.conf
sed -i "s/  mcast_join = .*/  host = $GMETAD_HOST/g" /etc/ganglia/gmond.conf
sed -i "s/  bind = /  #bind = /g" /etc/ganglia/gmond.conf
sed -i "s/  retry_bind = /  #retry_bind = /g" /etc/ganglia/gmond.conf

echo "INFO: restarting the services"
service gmond restart
