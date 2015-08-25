#!/bin/bash -e

# Get script directory
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
# Create/Update MySQL db with credentials set in properties file
Q1="DROP DATABASE IF EXISTS $DB_NAME;" #if database with name <DB_NAME> already exists, it is dropped
Q2="CREATE DATABASE IF NOT EXISTS $DB_NAME;" #if database with name <DB_NAME> is not present, it is created
Q3="GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO ${MYSQL_USER}@${DB_HOST};" #database privileges are given to <MY_SQL> user on <DB_NAME>
Q4="FLUSH PRIVILEGES;" #database privileges are refreshed
SQL="${Q1}${Q2}${Q3}${Q4}"
$( mysql --host=${DB_HOST} --user=${MYSQL_USER} --password=${MYSQL_PASS} -e "${SQL}" ) #SQL statements are gathered and executed sequentially
echo "User $MYSQL_USER added to $DB_NAME database"
# Add schemas to lims db
$( mysql --host=${DB_HOST} --user=${MYSQL_USER} --password=${MYSQL_PASS} -D $DB_NAME < "${DIR}/lims-schema-20150617.sql" )
$( mysql --host=${DB_HOST} --user=${MYSQL_USER} --password=${MYSQL_PASS} -D $DB_NAME < "${DIR}/miso_type_data_20120921.sql" )
