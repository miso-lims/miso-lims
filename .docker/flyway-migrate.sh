#!/usr/bin/env bash

set -e 

password=$(cat ${MISO_DB_PASS_FILE})

/flyway/flyway  -user="${MISO_DB_USER}" -password="${password}" -url="jdbc:mysql://${MISO_DB_URL}/${MISO_DB}?autoReconnect=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8" -outOfOrder=true -locations=classpath:db/migration,classpath:uk.ac.bbsrc.tgac.miso.db.migration -placeholders.filesDir=$MISO_FILES_DIR migrate