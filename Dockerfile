FROM misolims/miso-base

ARG     version

COPY    miso-web/target/ROOT.war /var/lib/tomcat8/webapps/

RUN     apt-get -y update && apt-get -y install --no-install-recommends     \
            unzip xmlstarlet                                                &&\
        unzip -xjo /var/lib/tomcat8/webapps/ROOT.war 'WEB-INF/lib/sqlstore-*.jar' -d /home/lib &&\
        chmod 0444 /var/lib/tomcat8/webapps/ROOT.war 		&&\
	    chown tomcat8 /var/lib/tomcat8/webapps/ROOT.war 	&&\
	    chgrp tomcat8 /var/lib/tomcat8/webapps/ROOT.war		&&\
	    rm -rf /var/lib/tomcat8/webapps/ROOT &&\
        cd /home/ && bash /root/miso-ansible/files/miso-install.sh $version   &&\
        apt-get purge --auto-remove -q -y                                   \
            unzip xmlstarlet                                                &&\
        rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

CMD	["/root/miso-ansible/misoStart.sh"]

