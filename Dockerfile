#######################################################
## Build miso-lims from current directory
#######################################################

FROM maven:3.8-eclipse-temurin-17 as builder
# only re-initialize Maven when there's a POM change
COPY pom.xml /miso-lims/
COPY ./integration-tools/pom.xml /miso-lims/integration-tools/pom.xml
COPY ./core/pom.xml /miso-lims/core/pom.xml
COPY ./miso-web/pom.xml /miso-lims/miso-web/pom.xml
COPY ./miso-service/pom.xml /miso-lims/miso-service/pom.xml
COPY ./sqlstore/pom.xml /miso-lims/sqlstore/pom.xml
COPY ./miso-dto/pom.xml /miso-lims/miso-dto/pom.xml
COPY ./pinery-miso/pom.xml /miso-lims/pinery-miso/pom.xml
WORKDIR /miso-lims
# cache the .m2 repository so it doesn't have to download the internet
# every single time it runs. It runs with --fail-never because this command
# doesn't work well with modules. This is best-effort.
RUN mvn dependency:go-offline --fail-never


# this way the image only rebuilds when code changes
COPY ./integration-tools/ /miso-lims/integration-tools/
COPY ./core/ /miso-lims/core/
COPY ./miso-web/ /miso-lims/miso-web/
COPY ./miso-service/ /miso-lims/miso-service/
COPY ./sqlstore/ /miso-lims/sqlstore/
COPY ./miso-dto/ /miso-lims/miso-dto/
COPY ./pinery-miso/ /miso-lims/pinery-miso/

RUN mvn clean && mvn package -DskipTests


#######################################################
## Flyway database migration
## Adapted from https://github.com/flyway/flyway-docker/blob/v7.7.1/Dockerfile
#######################################################
FROM eclipse-temurin:17-jdk-jammy as flyway-migration

# Add the flyway user and step in the directory
RUN adduser --system --home /flyway --disabled-password --group flyway
WORKDIR /flyway

# Change to the flyway user
USER flyway

ENV FLYWAY_VERSION 5.2.4

RUN curl -L https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/${FLYWAY_VERSION}/flyway-commandline-${FLYWAY_VERSION}.tar.gz -o flyway-commandline-${FLYWAY_VERSION}.tar.gz \
  && tar -xzf flyway-commandline-${FLYWAY_VERSION}.tar.gz --strip-components=1 \
  && rm flyway-commandline-${FLYWAY_VERSION}.tar.gz

ENV PATH="/flyway:${PATH}"

COPY --from=builder /miso-lims/sqlstore/target/classes/db/migration/*.sql /flyway/sql/
COPY --from=builder /miso-lims/sqlstore/target/sqlstore*.jar /flyway/jars/
COPY ./.docker/run-flyway /

ENV MISO_DB lims
ENV MISO_DB_HOST_PORT db:3306
ENV MISO_DB_ROOT_PASS_FILE /run/secrets/root_password
ENV MISO_FILES_DIR /storage/miso/files/

ENTRYPOINT ["/run-flyway"]

#######################################################
## Tomcat webapp
#######################################################

FROM tomcat:9-jdk17-temurin as webapp

COPY ./.docker/tomcat/setenv.sh /usr/local/tomcat/bin/
COPY ./.docker/tomcat/logging.properties ${CATALINA_HOME}/conf/
COPY ./.docker/tomcat/ROOT.xml ${CATALINA_HOME}/conf/Catalina/localhost/

RUN mkdir -p /storage/miso/log
COPY --from=builder /miso-lims/miso-web/src/main/resources/miso.properties  ${CATALINA_HOME}/conf/Catalina/localhost
COPY --from=builder /miso-lims/miso-web/src/main/resources/security.properties /storage/miso/
COPY --from=builder /miso-lims/miso-web/src/main/resources/submission.properties /storage/miso/

COPY --from=builder /miso-lims/miso-web/target/ROOT.war ${CATALINA_HOME}/webapps/

ENV MISO_DB_USER tgaclims
ENV MISO_DB lims
ENV MISO_DB_HOST_PORT db:3306
ENV MISO_DB_PASS_FILE /run/secrets/lims_password
ENV MISO_FILES_DIR /storage/miso/files/

CMD ["catalina.sh", "run"]
