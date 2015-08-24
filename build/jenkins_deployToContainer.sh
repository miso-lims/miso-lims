#!/bin/bash -e

DIR=$( cd "$( dirname ${BASH_SOURCE[0]} )" && pwd )
if [ -e "${DIR}/ROOT/META-INF/context.xml" ]; then
	echo 'Context file already exits'
	# Application Context (edit and add to META-INF)
	echo 'Editing context.xml'
	sed -i -r \
		-e 's|localhost:3306/lims|miso-db.res.oicr.on.ca:3306/devlims|1' \
		-e 's|(username=)[^=]*$|\1'\"$MYSQL_USER\"'|1' \
		-e 's|(password=)([^= >][^ />]*)|\1'\"$MYSQL_PASS\"'|1' \
		"${DIR}/ROOT/META-INF/context.xml"
else
	# Unpack ROOT.war
	echo 'Unpacking WAR'
	$( mkdir -p "${DIR}/ROOT"; cd "${DIR}/ROOT" && jar xfv "${WORKSPACE}/miso-web/target/ROOT.war" )
	# Application Context (edit and add to META-INF)
	echo 'Editing context.xml'
	sed -r \
		-e 's|localhost:3306/lims|miso-db.res.oicr.on.ca:3306/devlims|1' \
		-e 's|(username=)[^=]*$|\1'\"$MYSQL_USER\"'|1' \
		-e 's|(password=)([^=][^ />]*)|\1'\"$MYSQL_PASS\"'|1' \
		<"${DIR}/context.xml" >"${DIR}/ROOT/META-INF/context.xml"
fi
if [ -e "${DIR}/ROOT.war" ]; then
	jar ufv "${DIR}/ROOT.war" -C "${DIR}/ROOT" .
else
	jar cfv "${DIR}/ROOT.war" -C "${DIR}/ROOT" .
fi
curl --user "jenkins:deployer" --upload-file "${DIR}/ROOT.war" --url "http://miso-dev.res.oicr.on.ca:8080/manager/text/deploy?path=/ROOT&update=true"
