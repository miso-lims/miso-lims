#!/bin/bash

# Get script directory
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
set -e
# Set Environment variables
source "$DIR/context.properties"
DB_ARGS=($DB_HOST $DB_NAME $DB_USER $DB_PASS)
# Create/Update MySQL db with credentials set in properties file
MYSQL=`which mysql`
Q1="CREATE DATABASE IF NOT EXISTS $DB_NAME;"
Q2="GRANT ALL ON $DB_NAME.* TO '$DB_USER'@'$DB_HOST' IDENTIFIED BY '$DB_PASS';"
Q3="FLUSH PRIVILEGES;"
SQL="${Q1}${Q2}${Q3}"
# Check for missing variables required to create/update db
for (( i=0; i<4; i++));
do
	if [ -z "${DB_ARGS[i]}" ]; then
		echo "Missing arguements"
		exit 65
	fi
done
$MYSQL -u root -e "$SQL"
echo "Database $DB_NAME and user $DB_USER created"
# Add schemas to lims db
$MYSQL -D $DB_NAME < "$DIR/../sqlstore/src/main/resources/schemas/lims-schema-20150617.sql"
$MYSQL -D $DB_NAME < "$DIR/../sqlstore/src/main/resources/schemas/miso_type_data_20120921.sql"
