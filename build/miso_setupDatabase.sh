#!/bin/bash

set -e
# Get script directory
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
# Set Environment variables
source "${DIR}/context.properties"
# Create/Update MySQL db with credentials set in properties file
MYSQL=$( which mysql )
Q1="DROP DATABASE IF EXISTS $DB_NAME;"
Q2="CREATE DATABASE IF NOT EXISTS $DB_NAME;"
Q3="GRANT ALL ON $DB_NAME.* TO $MYSQL_USER@$DB_HOST;"
Q4="FLUSH PRIVILEGES;"
SQL="${Q1}${Q2}${Q3}${Q4}"
$MYSQL --login-path=client -e "$SQL"
echo "Database $DB_NAME and user $DB_USER created"
# Add schemas to lims db
$MYSQL --login-path=miso -D $DB_NAME < "$DIR/../sqlstore/src/main/resources/schemas/lims-schema-20150617.sql"
$MYSQL --login-path=miso -D $DB_NAME < "$DIR/../sqlstore/src/main/resources/schemas/miso_type_data_20120921.sql"
