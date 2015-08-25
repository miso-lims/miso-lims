#!/bin/bash -e

# Get script directory
DIR=$( cd "$( dirname ${BASH_SOURCE[0]} )" && pwd )
if [ -e "${DIR}/ROOT/META-INF/context.xml" ]; then #check to see if ROOT directory already exists and contains context.xml file
	echo 'Context file already exits'
	# Application Context (edit and add to META-INF)
	echo 'Editing context.xml'
	sed -i -r \
		-e 's|localhost:3306/lims|miso-db.res.oicr.on.ca:3306/devlims|1' \
		-e 's|(username=)[^=]*$|\1'\"$MYSQL_USER\"'|1' \
		-e 's|(password=)([^= >][^ />]*)|\1'\"$MYSQL_PASS\"'|1' \
		"${DIR}/ROOT/META-INF/context.xml" #edit in place since context.xml file already exists
else
	# Unpack ROOT.war
	echo 'Unpacking WAR file'
	$( mkdir -p "${DIR}/ROOT"; cd "${DIR}/ROOT" && jar xfv "${WORKSPACE}/miso-web/target/ROOT.war" ) #create ROOT directory and unpack WAR file there
	# Application Context (edit and add to META-INF)
	echo 'Editing context.xml'
	sed -r \
		-e 's|localhost:3306/lims|miso-db.res.oicr.on.ca:3306/devlims|1' \
		-e 's|(username=)[^=]*$|\1'\"$MYSQL_USER\"'|1' \
		-e 's|(password=)([^=][^ />]*)|\1'\"$MYSQL_PASS\"'|1' \
		<"${DIR}/context.xml" >"${DIR}/ROOT/META-INF/context.xml" #make edits and create context.xml file under ROOT directory
fi
if [ -e "${DIR}/ROOT.war" ]; then
	jar ufv "${DIR}/ROOT.war" -C "${DIR}/ROOT" . #update WAR file if exists
else
	jar cfv "${DIR}/ROOT.war" -C "${DIR}/ROOT" . #create new WAR file
fi
curl --user "jenkins:deployer" --upload-file "${DIR}/ROOT.war" --url "http://miso-dev.res.oicr.on.ca:8080/manager/text/deploy?path=/ROOT&update=true" #deploy WAR file to Tomcat servlet using credentials set in Tomcat configurations
