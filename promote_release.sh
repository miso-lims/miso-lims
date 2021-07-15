#!/bin/bash

set -euo pipefail

MAIN_BRANCH="develop"

# validate prerequisites
if [[ ! $(command -v gh) ]]; then
  echo "Error: GitHub CLI not found"
  exit 1
fi

# validate git state
if [[ ! $(git branch | grep \* | cut -d ' ' -f2) = "${MAIN_BRANCH}" ]]; then
  echo "Error: Not on ${MAIN_BRANCH} branch" >&2
  exit 2
fi
git fetch
if (( $(git log HEAD..origin/${MAIN_BRANCH} --oneline | wc -l) > 0 )); then
  echo "Error: Branch is not up-to-date with remote origin" >&2
  exit 3
fi

MISO_VERSION_OLD=$(git tag -l --sort -version:refname | head -n1); export MISO_VERSION_OLD=${MISO_VERSION_OLD:1}

# Post official release to GitHub
git fetch --tags
git checkout tags/v${MISO_VERSION_OLD}
mvn clean package
pushd miso-web/src/main/resources
SETUP_FILES="setup_files.tar.gz"
tar -czf "../../../../${SETUP_FILES}" *.properties
popd
ANCHOR=$(grep "^## \[${MISO_VERSION_OLD}\]" RELEASE_NOTES.md | sed -e 's/ - /---/; s/[# \.\[]//g; s/\]//g')
gh release create v${MISO_VERSION_OLD} \
    --notes "[Release Notes](https://github.com/miso-lims/miso-lims/blob/develop/RELEASE_NOTES.md#${ANCHOR})" \
    miso-web/target/ROOT.war \
    sqlstore/target/sqlstore-*.jar \
    ${SETUP_FILES}
rm ${SETUP_FILES}
echo "GitHub release v${MISO_VERSION_OLD} posted"

# Update master branch
git checkout master
git reset origin/master --hard
git rebase --onto v${MISO_VERSION_OLD} master
git push origin master
echo "Master branch updated"

echo "v${MISO_VERSION_OLD} promoted to latest official release. Copy this export into your shell before doing the Docker build:"
echo "export MISO_VERSION_OLD=${MISO_VERSION_OLD}"
