#!/bin/bash
set -e
source script.properties
if [ -d "$CONTEXT_PATH" ]; then
	echo "$CONTEXT_PATH directory already exits"
	# Application Context (edit and add to META-INF)
	echo Editing context.xml;
	sed -i -r \
		-e 's|ROOT|'"$CONTEXT_PATH"'|g' \
		-e 's|(username=)[^=]*$|\1'\"$DB_USER\"'|1' \
		-e 's|(password=)([^= >][^ />]*)|\1'\"$DB_PASS\"'|1' \
		$PWD/$CONTEXT_PATH/META-INF/context.xml;
else
	# Unpack ROOT.war
	unzip ROOT.war -d $CONTEXT_PATH;
	# Application Context (edit and add to META-INF)
	echo Editing context.xml;
	sed -r \
		-e 's|ROOT|'"$CONTEXT_PATH"'|g' \
		-e 's|(username=)[^=]*$|\1'\"$DB_USER\"'|1' \
		-e 's|(password=)([^=][^ />]*)|\1'\"$DB_PASS\"'|1' \
		<context.xml >$PWD/$CONTEXT_PATH/META-INF/context.xml;
fi;
# Application Properties (edit in WEB-INF/classes)
echo Editing miso.properties;
sed -i -r 's|(baseDirectory:)[^:]*$|\1'$STORAGE_PATH'|1' $PWD/$CONTEXT_PATH/WEB-INF/classes/miso.properties;
# Package Application
if [-e ${CONTEXT_PATH}.war]; then
	jar ufv "${CONTEXT_PATH}.war" -C $PWD/$CONTEXT_PATH/ .;
else
	jar cfv "${CONTEXT_PATH}.war" -C $PWD/$CONTEXT_PATH/ .;
fi;
curl --user $TOMCAT_USER:$TOMCAT_PASS --upload-file ${CONTEXT_PATH}.war "http://$TOMCAT_SERVER/manager/text/deploy?path=/$CONTEXT_PATH&update=true"
