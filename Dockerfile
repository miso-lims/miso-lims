FROM    tomcat
LABEL   maintainer="Justin Payne <justin.payne@fda.hhs.gov"

EXPOSE  8080

VOLUME  /storage/miso
VOLUME  /src
VOLUME  $HOME/.m2

ENV     SECURITY_METHOD ad

RUN     apt-get -y update && apt-get -y install --no-install-recommends     \
            unzip xmlstarlet maven default-jdk                                                              &&\
        apt-get purge --auto-remove -q -y                                   \
            unzip xmlstarlet                                                                                &&\
#        echo "JAVA_OPTS=/"$JAVA_OPTS -Dsecurity.method=${SECURITY_METHOD} -Xmx768M/"" \
#            >> $CATALINA_HOME/bin/setenv.sh                                                                 &&\
        cd $CATALINA_HOME/lib                                                                               &&\
        curl -kO https://repos.tgac.ac.uk/miso/common/mysql-connector-java-5.1.10.jar                       &&\
        curl -kO https://repos.tgac.ac.uk/miso/common/jndi-file-factory-1.0.jar                             &&\
        rm -rf $CATALINA_HOME/webapps/ROOT 


COPY    miso /src

RUN     cd /src && mvn clean package -P external


COPY    miso/miso-web/target/ROOT.war $CATALINA_HOME/webapps/
COPY    tomcat_conf/ $CATALINA_HOME/conf/Catalina/localhost/
COPY    cfsan-miso-properties/ /storage/miso/

