#######################################################
## Build miso-lims from current directory
#######################################################

FROM maven:3.8-eclipse-temurin-17 AS builder
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
#######################################################
FROM flyway/flyway:13.7.0-alpine AS flyway-migration
COPY --from=builder /miso-lims/sqlstore/target/classes/db/migration/*.sql /flyway/sql/
COPY --from=builder /miso-lims/sqlstore/target/sqlstore*.jar /flyway/jars/
COPY --from=builder /miso-lims/miso-web/target/ROOT/WEB-INF/lib/mysql-connector-j-*.jar /flyway/drivers/
COPY ./.docker/run-flyway /

ENV MISO_DB=lims
ENV MISO_DB_HOST_PORT=db:3306
ENV MISO_FILES_DIR=/storage/miso/files/
ENV FLYWAY_PASSWORD_FILE=/run/secrets/flyway_password

ENTRYPOINT ["/run-flyway"]

#######################################################
## Tomcat webapp
#######################################################

FROM tomcat:10.1-jdk17-temurin AS webapp

COPY ./.docker/tomcat/setenv.sh /usr/local/tomcat/bin/
COPY ./.docker/tomcat/logging.properties ${CATALINA_HOME}/conf/
COPY ./.docker/tomcat/ROOT.xml ${CATALINA_HOME}/conf/Catalina/localhost/

RUN mkdir -p /storage/miso/log
COPY --from=builder /miso-lims/miso-web/src/main/resources/miso.properties  ${CATALINA_HOME}/conf/Catalina/localhost
COPY --from=builder /miso-lims/miso-web/src/main/resources/security.properties /storage/miso/
COPY --from=builder /miso-lims/miso-web/src/main/resources/submission.properties /storage/miso/

COPY --from=builder /miso-lims/miso-web/target/ROOT.war ${CATALINA_HOME}/webapps/

ENV MISO_DB_USER=tgaclims
ENV MISO_DB=lims
ENV MISO_DB_HOST_PORT=db:3306
ENV MISO_DB_PASS_FILE=/run/secrets/lims_password
ENV MISO_FILES_DIR=/storage/miso/files/

CMD ["catalina.sh", "run"]
