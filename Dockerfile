#######################################################
## Build miso-lims from current directory
#######################################################

FROM maven:3.6.0-jdk-8 as builder

# this way the image only rebuilds when code changes
COPY pom.xml /miso-lims/
COPY ./integration-tools/ /miso-lims/integration-tools/
COPY ./core/ /miso-lims/core/
COPY ./miso-web/ /miso-lims/miso-web/
COPY ./miso-service/ /miso-lims/miso-service/
COPY ./sqlstore/ /miso-lims/sqlstore/
COPY ./miso-dto/ /miso-lims/miso-dto/
COPY ./pinery-miso/ /miso-lims/pinery-miso/
COPY ./migration/ /miso-lims/migration/

WORKDIR /miso-lims
RUN mvn package -P external -DskipTests


#######################################################
## Flyway database migration
#######################################################
FROM openjdk:8-jre-alpine as flyway-migration

# set up flyway's dependencies and MISO's jar
ARG FLYWAY_VERSION=3.2.1
RUN apk --no-cache add --update bash openssl unzip

COPY ./.docker/run-flyway ./.docker/wait-for-it.sh /

# Add the flyway user and step in the directory
RUN adduser -S -h /flyway -D flyway
WORKDIR /flyway

# Change to the flyway user
USER flyway

COPY --from=builder /miso-lims/miso-web/target/ROOT.war .

RUN wget https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/${FLYWAY_VERSION}/flyway-commandline-${FLYWAY_VERSION}.tar.gz \
  && tar -xzf flyway-commandline-${FLYWAY_VERSION}.tar.gz \
  && mv flyway-${FLYWAY_VERSION}/* . \
  && rm flyway-commandline-${FLYWAY_VERSION}.tar.gz \
  && unzip -xjo ROOT.war 'WEB-INF/lib/sqlstore-*.jar' -d lib \
  && mkdir /flyway/migrations

WORKDIR /

ENV MISO_DB_USER tgaclims
ENV MISO_DB lims
ENV MISO_DB_HOST_PORT db:3306
ENV MISO_DB_PASS_FILE /run/secrets/lims_password
ENV MISO_FILES_DIR /storage/miso/files/

CMD bash /wait-for-it.sh -t 0 ${MISO_DB_HOST_PORT} && bash /run-flyway migrate


#######################################################
## Tomcat webapp
#######################################################

FROM tomcat:8.5.38-alpine as webapp

ARG MYSQL_JBDC_VERSION=8.0.15

COPY ./.docker/tomcat/setenv.sh /usr/local/tomcat/bin/

RUN wget https://repo1.maven.org/maven2/mysql/mysql-connector-java/${MYSQL_JBDC_VERSION}/mysql-connector-java-${MYSQL_JBDC_VERSION}.jar -O /usr/local/tomcat/lib/mysql-connector-java-${MYSQL_JBDC_VERSION}.jar && \
  wget https://artifacts.oicr.on.ca/artifactory/gsi-dependencies/uk/ac/ebi/fgpt/jndi-file-factory/1.0/jndi-file-factory-1.0.jar -O /usr/local/tomcat/lib/jndi-file-factory-1.0.jar

COPY ./.docker/tomcat/logging.properties ${CATALINA_HOME}/conf/
COPY ./.docker/tomcat/ROOT.xml ${CATALINA_HOME}/conf/Catalina/localhost/

RUN mkdir -p /storage/miso/log && rm -r ${CATALINA_HOME}/webapps/ROOT*
COPY --from=builder /miso-lims/miso-web/src/main/resources/external/miso.properties  ${CATALINA_HOME}/conf/Catalina/localhost
COPY --from=builder /miso-lims/miso-web/src/main/resources/security.properties /storage/miso/
COPY --from=builder /miso-lims/miso-web/src/main/resources/submission.properties /storage/miso/

COPY --from=builder /miso-lims/miso-web/target/ROOT.war ${CATALINA_HOME}/webapps/

ENV MISO_DB_USER tgaclims
ENV MISO_DB lims
ENV MISO_DB_HOST_PORT db:3306
ENV MISO_DB_PASS_FILE /run/secrets/lims_password
ENV MISO_FILES_DIR /storage/miso/files/

CMD ["catalina.sh", "run"]
