#! /bin/sh

echo 'Setting base urls in proxy conf'
envsubst '$BASE_URL_V1 $BASE_URL_V2' < /etc/nginx/proxy.conf.template > /etc/nginx/conf.d/proxy.conf
export PORT="8081"

echo 'Building v1 documentation'
export DOC_VERSION="v1"
export BASE_URL="$BASE_URL_V1"
/kit/buildDocumentation.sh
mkdir -p /usr/share/nginx/v1/html$BASE_URL_V1
cp -R /usr/share/nginx/html/. /usr/share/nginx/v1/html$BASE_URL_V1/

echo 'Building v2 documentation'
export DOC_VERSION="v2"
export BASE_URL="$BASE_URL_V2"
/kit/buildDocumentation.sh
mkdir -p /usr/share/nginx/v2/html$BASE_URL_V2
cp -R /usr/share/nginx/html/. /usr/share/nginx/v2/html$BASE_URL_V2/

echo "Restarting nginx"
nginx -g 'daemon off;'