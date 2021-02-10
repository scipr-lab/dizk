#!/usr/bin/env bash

# Expect 2 non-empty arguments
if [ "$#" -ne 1 ] || [ "$1" == "" ] ; then
    echo "Error: invalid number of arguments"
    echo "Usage: $0 <cluster-name>"
    echo ""
    exit 1
fi

CLUSTER_NAME=$1

# Install ganglia dependencies
echo "INFO: install ganglia dependencies"
yum update -y
amazon-linux-extras install -y epel
yum install -y ganglia rrdtool ganglia-gmetad ganglia-gmond ganglia-web

echo "INFO: edit /etc/ganglia/gmetad.conf"
# Keep the default port (8649) and default polling interval (15s)
sed -i "s/my cluster/$CLUSTER_NAME/g" /etc/ganglia/gmetad.conf

echo "INFO: edit /etc/ganglia/gmond.conf"
sed -i 's/  name = "unspecified"/  name = '\"$CLUSTER_NAME\"'/g' /etc/ganglia/gmond.conf
# Ports in gmond are already set by default so no need to change
sed -i  "0,/  mcast_join = /! {0,/  mcast_join = / s/  mcast_join =/  #mcast_join = /}" /etc/ganglia/gmond.conf
sed -i "s/  mcast_join = .*/  host = localhost/g" /etc/ganglia/gmond.conf
sed -i "s/  bind = /  #bind = /g" /etc/ganglia/gmond.conf
sed -i "s/  retry_bind = /  #retry_bind = /g" /etc/ganglia/gmond.conf

# Restart the services after editing the config files
echo "INFO: restarting the services"
service httpd restart
service gmond restart
service gmetad restart

# Edit the default config `/etc/httpd/conf.d/ganglia.conf`
echo "INFO: make sure to correctly configure the webserver (then restart the httpd service)"
# E.g. set up some auth to access the ganglia dashboard
# $ htpasswd -c /etc/httpd/auth.basic adminganglia
# $ vi /etc/httpd/conf.d/ganglia.conf
## Alias /ganglia /usr/share/ganglia
## <Location /ganglia>
##     AuthType basic
##     AuthName "Ganglia web UI"
##     AuthBasicProvider file
##     AuthUserFile "/etc/httpd/auth.basic"
##     Require user adminganglia
## </Location>

echo "INFO: make sure to correctly configure the AWS security-group to be able to access the ganglia dashboard"
