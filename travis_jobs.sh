#!/bin/bash
set -ev

DOCKER_LOGIN="-Ddocker.username=${DOCKER_USERNAME} -Ddocker.password=${DOCKER_PASSWORD}"

if [ "$JOB" = "UNIT_TESTS" ]; then
    mvn clean test
elif [ "$JOB" = "SQLSTORE_IT" ]; then
    cd sqlstore
    mvn clean verify -DskipUTs=true -DskipITs=false ${DOCKER_LOGIN}
elif [ "$JOB" = "PLAIN_WEB_IT" ]; then
    cd miso-web
    mvn clean verify -DskipUTs=true -DrunPlainITs ${DOCKER_LOGIN}
elif [ "$JOB" = "BULK_WEB_IT" ]; then
    cd miso-web
    mvn clean verify -DskipUTs=true -DskipITs=false -Dit.test='Bulk*' ${DOCKER_LOGIN}
elif [ "$JOB" = "OTHER_WEB_IT" ]; then
    cd miso-web
    mvn clean verify -DskipUTs=true -DskipITs=false -Dit.test='!PlainSampleITs, !Bulk*' ${DOCKER_LOGIN}
elif [ "$JOB" = "PINERY_IT" ]; then
    cd pinery-miso
    mvn clean verify -DskipUTs=true -DskipITs=false ${DOCKER_LOGIN}
else
    echo "unknown job"
    exit 1
fi

