#!/usr/bin/env sh

# only install openssl if generating a self-signed certificate
apk add openssl

# relies on the file at /run/secrets/ssl_password that has the SSL password
mkdir -p /etc/nginx/ssl && cd /etc/nginx/ssl
# modify the -subj line to fit your organization
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 \
    -passout file:/run/secrets/ssl_password \
    -subj "/C=CA/ST=ON/L=Toronto/O=OICR/OU=Genome Sequence Informatics"
