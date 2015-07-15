#!/bin/bash
set -e
source script.properties
DB_ARGS=($DB_HOST $DB_NAME $DB_USER $DB_PASS)
MYSQL=`which mysql`
Q1="CREATE DATABASE IF NOT EXISTS $DB_NAME;"
Q2="GRANT ALL ON $DB_NAME.* TO '$DB_USER'@'$DB_HOST' IDENTIFIED BY '$DB_PASS';"
Q3="FLUSH PRIVILEGES;"
SQL="${Q1}${Q2}${Q3}"
for (( i=0; i<4; i++));
do
	if [ -z "${DB_ARGS[i]}" ]; then
		echo "Missing arguements"
		exit 65
	fi
done
$MYSQL -u root -e "$SQL"
echo "Database $DB_NAME and user $DB_USER created with password $DB_PASS"
