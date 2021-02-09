#!/bin/bash
set -ev

if [ "$JOB" = "UNIT_TESTS" ]; then
    mvn clean test
elif [ "$JOB" = "SQLSTORE_IT" ]; then
    cd sqlstore
    mvn clean verify -DskipUTs=true -DskipITs=false
elif [ "$JOB" = "PLAIN_WEB_IT" ]; then
    cd miso-web
    mvn clean verify -DskipUTs=true -DrunPlainITs
elif [ "$JOB" = "BULK_WEB_IT" ]; then
    cd miso-web
    mvn clean verify -DskipUTs=true -DskipITs=false -Dit.test='Bulk*'
elif [ "$JOB" = "OTHER_WEB_IT" ]; then
    cd miso-web
    mvn clean verify -DskipUTs=true -DskipITs=false -Dit.test='!PlainSampleITs, !Bulk*'
elif [ "$JOB" = "PINERY_IT" ]; then
    cd pinery-miso
    mvn clean verify -DskipUTs=true -DskipITs=false
else
    echo "unknown job"
    exit 1
fi

