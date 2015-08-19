#!/bin/bash

set -e
Q1="CREATE DATABASE IF NOT EXISTS $DB_NAME;"
Q2="GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO ${MYSQL_USER}@${DB_HOST};"
Q3="FLUSH PRIVILEGES;"
SQL="${Q1}${Q2}${Q3}"
$( mysql --host=${DB_HOST} --user=${MYSQL_USER} --password=${MYSQL_PASS} -e "$SQL" )
echo "User $MYSQL_USER added to $DB_NAME database"
# Add schemas to lims db
$( mysql --host=${DB_HOST} --user=${MYSQL_USER} --password=${MYSQL_PASS} -D $DB_NAME < /home/tdebat/jenkins/lims-schema-20150617.sql )
$( mysql --host=${DB_HOST} --user=${MYSQL_USER} --password=${MYSQL_PASS} -D $DB_NAME < /home/tdebat/jenkins/miso_type_data_20120921.sql )
