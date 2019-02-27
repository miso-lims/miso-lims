#!/usr/bin/env bash

mpass=$(cat ${MISO_DB_PASS_FILE}) 
export CATALINA_OPTS="-Dmisodb=${MISO_DB} -Dmisouser=${MISO_DB_USER} -Dmisopass=${mpass} -Dmisohostport=${MISO_DB_HOST_PORT} $CATALINA_OPTS "

JAVA_OPTS="${JAVA_OPTS} -Dsecurity.method=jdbc -Xmx768M"