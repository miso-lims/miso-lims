#!/bin/bash
set -ev

if [ "$JOB" = "SONAR_AND_UNIT_TESTS" ]; then
    if [[ ${TRAVIS_PULL_REQUEST} == "false" ]] || [[ ${TRAVIS_PULL_REQUEST_SLUG} == ${TRAVIS_REPO_SLUG} ]] ; then 
        # Sonar
        mvn org.jacoco:jacoco-maven-plugin:prepare-agent sonar:sonar
    else 
        echo "[WARN] SonarCloud cannot run on pull requests from forks."
    fi
    # Unit Tests
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

