#!/bin/bash

set -euo pipefail

PROJECT="MISO"
MAIN_BRANCH="develop"
CHANGE_DIR="changes"

RELEASE_TYPE=""
RELEASE_VERSION=""

# validate arguments
usage_error() {
  echo "Error: bad arguments" >&2
  echo "Usage: $0 [major|minor]" >&2
  exit 1
}

if [[ "$#" -eq 1 ]]; then
  if [[ "$1" = "major" ]] || [[ "$1" = "minor" ]]; then
    RELEASE_TYPE="$1"
  else
    usage_error
  fi
elif [ "$#" -gt 1 ]; then
  usage_error
fi

# validate prerequisites
if [[ ! $(command -v xmlstarlet) ]]; then
  echo "Error: xmlstarlet not found"
  exit 2
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

determine_version() {
  CURRENT_VERSION=$(xmlstarlet sel -t -v /_:project/_:version pom.xml | sed -e s/-SNAPSHOT//g)

  MAJOR=$(echo $CURRENT_VERSION | cut -d . -f 1 -)
  MINOR=$(echo $CURRENT_VERSION | cut -d . -f 2 -)
  PATCH=$(echo $CURRENT_VERSION | cut -d . -f 3 -)

  if [[ "${RELEASE_TYPE}" = "major" ]]; then
    MAJOR=$((MAJOR+1))
    MINOR=0
    PATCH=0
  elif [[ "${RELEASE_TYPE}" = "minor" ]] || [[ -n $(find "${CHANGE_DIR}" -mindepth 1 -maxdepth 1 \
      -name "add_*" -or -name "change_*" -or -name "remove_*") ]]; then
    RELEASE_TYPE="minor"
    MINOR=$((MINOR+1))
    PATCH=0
  elif [[ -n $(find "${CHANGE_DIR}" -mindepth 1 -maxdepth 1 -name "fix_*") ]]; then
    RELEASE_TYPE="patch"
    # use patch version from snapshot version
  else
    echo "No changes found in 'changes' directory. Aborting release." >&2
    exit 4
  fi

  RELEASE_VERSION="${MAJOR}.${MINOR}.${PATCH}"
}

prepare() {
  echo "Preparing ${RELEASE_TYPE} release ${RELEASE_VERSION}..."
  ./compact-migrations.sh || return 1 # MISO-specific
  ./compact-changelog.sh ${RELEASE_VERSION} || return 2
  mvn versions:set -DnewVersion=${RELEASE_VERSION} -DgenerateBackupPoms=false && \
  git commit -a -m "${PROJECT} v${RELEASE_VERSION} release" && \
  git tag -a v${RELEASE_VERSION} -m "${PROJECT} v${RELEASE_VERSION} release" && \
  TAGGED=true && \
  mvn clean install && \
  mvn versions:set -DnextSnapshot=true -DgenerateBackupPoms=false && \
  git commit -a -m "prepared for next development iteration"
}

rollback_local() {
  # undoes all changes from prepare function
  git reset --hard origin/${MAIN_BRANCH}
  if [[ ${TAGGED} = true ]]; then
    git tag -d v${RELEASE_VERSION}
  fi
  echo "Release failed. Changes reset." >&2
  exit 7
}

push() {
  echo "Pushing release..."
  # print these commands as they are not automatically rolled back
  set -x
  git push origin ${MAIN_BRANCH} && \
  git push origin v${RELEASE_VERSION} && \
  git checkout tags/v${RELEASE_VERSION} && \
  mvn deploy && \
  git checkout ${MAIN_BRANCH} && \
  set +x
}

push_error() {
  set +x
  echo "An error occurred while pushing the release. The process should probably be completed manually."
  exit 8
}

determine_version
prepare || rollback_local
push || push_error
ADDITIONAL_EXPORTS=" PINERY_VERSION=$(xmlstarlet sel -t -v /_:project/_:properties/_:pinery.version pinery-miso/pom.xml)" # MISO-specific
echo "Release completed. Copy this export into your shell before running the deploy scripts:"
echo "$(echo ${PROJECT} | tr '[:lower:]' '[:upper:]')_VERSION=${RELEASE_VERSION}${ADDITIONAL_EXPORTS}"
