#!/bin/bash

echo "IT WORKED OK"

if [ "${TRAVIS_PULL_REQUEST}" != "false" ]; then
  echo "Running sonar scanner in change preview mode"

  sonar-scanner \
    -Dsonar.analysis.mode=preview \
    -Dsonar.profile=Java \
    -Dsonar.projectVersion=$PACKAGE_VERSION \
    -Dsonar.login=$SONAR_TOKEN \
    -Dsonar.github.pullRequest=$TRAVIS_PULL_REQUEST \
    -Dsonar.github.oauth=$SONAR_GITHUB_TOKEN \
    -Dsonar.github.repository=$TRAVIS_REPO_SLUG
    -Dsonar.branch=$TRAVIS_PULL_REQUEST_BRANCH
fi
