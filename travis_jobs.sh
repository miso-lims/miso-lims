#!/bin/bash
set -ev

if [ "$JOB" = "SONAR" ]; then
    if [[ ${TRAVIS_PULL_REQUEST_SLUG} == ${TRAVIS_REPO_SLUG} ]] ; then 
        mvn org.jacoco:jacoco-maven-plugin:prepare-agent sonar:sonar
    else 
        echo "[WARN] SonarCloud cannot run on pull requests from forks."
    fi
elif [ "$JOB" = "UNIT_TEST" ]; then
    mvn -P external clean test
elif [ "$JOB" = "PLAIN_WEB_IT" ]; then
    cd miso-web
    mvn -P external clean verify -DskipUTs=true -DrunPlainITs
elif [ "$JOB" = "BULK_WEB_IT" ]; then
    cd miso-web
    mvn -P external clean verify -DskipUTs=true -DskipITs=false -Dit.test='Bulk*'
elif [ "$JOB" = "OTHER_WEB_IT" ]; then
    cd miso-web
    mvn -P external clean verify -DskipUTs=true -DskipITs=false -Dit.test='!PlainSampleITs, !Bulk*'
elif [ "$JOB" = "RUNSCANNER_TEST" ]; then
    pushd runscanner-illumina && ./build-illumina-interop && autoreconf -i && ./configure && make && popd;
    cd runscanner
    PATH=$PATH:$(pwd)/../runscanner-illumina mvn -P external clean test -DskipIllumina=false
elif [ "$JOB" = "PINERY_IT" ]; then
    cd pinery-miso
    mvn clean verify -DskipUTs=true -DskipITs=false
else
    echo "unknown job"
    exit 1
fi

