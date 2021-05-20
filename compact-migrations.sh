#!/bin/sh
# Compact all 8xxx migrations into a single migration as part of the release process.

set -eu

cd "$(dirname $0)/sqlstore/src/main/resources/db/migration"

# Determine the last migration in the working directory
LAST="$(basename "$(ls V[0-5][0-9][0-9][0-9]_*.sql | sort | tail -n 1)")"
echo "Previous migration is ${LAST}."
LAST_ID="$(echo "${LAST}" | cut -c 2-5 | sed -e 's/^0*//')"

ID=$(((${LAST_ID} / 10 + 1) * 10))
NEXT="V$(printf "%04d" ${ID})__auto_main.sql"
echo "Next migration will be ${NEXT}."

# Concatenate all the 8xxx files and remove them from git
rm -f "${NEXT}"
for FILE in $(find . -name "V8[0-9][0-9][0-9]_*.sql" | sort)
do
	NAME=$(basename "${FILE}" .sql)
	echo -- ${NAME#V*__} >> "${NEXT}"
	# This sed command forces a newline at the end of the file
	sed -e '$a\' "${FILE}" >> "${NEXT}"
	echo >> "${NEXT}"
	git rm "${FILE}"
done

DANGLING_MIGRATION_COUNT=$(ls | grep "^V8.*" | wc -l)

if [ "$DANGLING_MIGRATION_COUNT" -gt 0 ]; then
  echo ""
	echo "************* Found migrations with malformed file names. ****************"
	ls | grep "^V8.*"
	echo "************* You should undo all migration changes before fixing. *******"
	exit 1
fi


if [ -f "${NEXT}" ]
then
	git add "${NEXT}"
	echo "Created migration ${NEXT}."
else
	echo "There were no migrations to compact."
fi
