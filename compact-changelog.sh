#!/bin/bash
# Compile all release notes from the `changes` directory into the changelog

set -euo pipefail

if [[ ! $# -eq 1 ]]; then
 echo "ERROR: Bad command. Usage example: ${0} v1.2.3"
 exit 1
fi

CHANGE_DIR=changes
CHANGELOG=RELEASE_NOTES.md
RELEASE_VERSION=$1
DATE=$(date +%Y-%m-%d)

BAD_FILES=$(find "${CHANGE_DIR}" -mindepth 1 -maxdepth 1 -not -name "README.md" -and -not -name "add_*" \
  -and -not -name "change_*" -and -not -name "remove_*" -and -not -name "fix_*" \
  -and -not -name "note*")

if [[ ! -z "$BAD_FILES" ]]; then
  echo -e "ERROR: Bad change files found:\n${BAD_FILES}"
  exit 2
fi

if [[ -z $(find "${CHANGE_DIR}" -mindepth 1 -maxdepth 1 -name "add_*" -or -name "change_*" \
  -or -name "remove_*" -or -name "fix_*") ]]; then
  echo "ERROR: No add/change/remove/fix changes found"
  exit 3
fi

if [[ $(grep -c "^----------" "${CHANGELOG}") -ne 1 ]]; then
  echo "ERROR: Couldn't determine where to insert changes"
  exit 4
fi

CHANGES="\n\n## [${RELEASE_VERSION}] - ${DATE}"

add_section() {
  # $1: title
  # $2: change type prefix

  FILES=$(find "${CHANGE_DIR}" -mindepth 1 -maxdepth 1 -name "$2_*")
  if [[ ! -z "${FILES}" ]]; then
    CHANGES="${CHANGES}\n\n### ${1}\n"

    for FILE in ${FILES}; do
      PREFIX="* "
      while read LINE; do
        CHANGES="${CHANGES}\n${PREFIX}${LINE}"
        PREFIX="  "
      done <"${FILE}"
    done
  fi
}

add_section "Added" "add"
add_section "Changed" "change"
add_section "Removed" "remove"
add_section "Fixed" "fix"
add_section "Upgrade Notes" "note"

sed -i "s/^\(-\{10,\}\)/\1${CHANGES}/" "${CHANGELOG}"
rm -f -- "${CHANGE_DIR}"/add_* "${CHANGE_DIR}"/change_* "${CHANGE_DIR}"/remove_* \
"${CHANGE_DIR}"/fix_* "${CHANGE_DIR}"/note_*
