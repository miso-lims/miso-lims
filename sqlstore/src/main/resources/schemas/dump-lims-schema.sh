#!/bin/bash
HOST="localhost"
DB=
USER="root"
PASS=
while getopts "h:d:u:p:" opt; do
  case $opt in
    h)
	HOST=$OPTARG
      ;;
    d)
        DB=$OPTARG
      ;;
    u)
        USER=$OPTARG
      ;;
    p)
        PASS=$OPTARG
      ;;

    \?)
      echo "Invalid option: -$OPTARG" >&2
      ;;
  esac
done

mysqldump -h $HOST -u $USER -p $PASS --no-data $DB | sed 's/AUTO_INCREMENT=[0-9]*\b//' > "$DB-schema-$(date +%Y%m%d).sql"
