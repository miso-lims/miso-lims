# Pinery-MISO

Pinery is a webservice for reading LIMS data. Pinery-MISO is a Pinery webservice implementation
that reads data from MISO by connecting directly to the MISO database. The Pinery API is maintained
in the [Pinery project](https://github.com/oicr-gsi/pinery).

Pinery is meant to be used as a data source for downstream applications such as reporting and
analysis pipeline automation. The main advantage to using Pinery instead of reading directly from
the MISO database is to avoid tightly coupling your other applications with MISO. If you write
applications to use MISO data, then you would have to rewrite all of those applications if you ever
want to use a different data source. If you instead write your applications to use Pinery data, then
you only need to write a new Pinery implementation (or use an existing one) and all of your other
applications remain functional.

The Pinery project contains some other useful modules for working with Pinery-MISO

* **pinery-to-flatfile utility**: dumps all Pinery data to *.tsv flat files
* **pinery-lims-flatfile**: another Pinery webservice implementation that reads data from *.tsv flat files
* **pinery-client**: A client library for consuming Pinery data

If the pinery-miso webapp is under heavy load, a good option is to use pinery-to-flatfile to dump the
pinery-miso data into files on an hourly basis, and deploy a pinery-lims-flatfile webapp to read
from these. Clients can then use the flatfile webapp instead, to reduce strain on the MISO DB.

## Deploying Pinery-MISO

1. Build the entire MISO project

    ```
    cd miso-lims
    mvn clean install -P external
    ```

2. Stop tomcat

3. Configure database connection. Copy the [properties file](src/main/resources/pinery-miso.properties) to
   `${CATALINA_HOME}/conf/Catalina/localhost/` and modify it as appropriate. Since this is a
   read-only service, the database user only requires read access.

4. Deploy the webapp:
  * Copy the [example context file](src/main/resources/context-example.xml) to
    `${CATALINA_HOME}/conf/Catalina/localhost/`. Rename this file to match your desired webapp
    name (e.g. `pinery-miso.xml`). There is no need to modify the contents of this file unless you
    need to specify a different properties file (when deploying multiple Pinery-MISO webapps on the
    same server, for example)
  * Copy the built war file (`pinery-miso/target/pinery-miso-<version>.war`) into
    `${CATALINA_HOME}/webapps/`. Rename it to match your desired webapp name (e.g. `pinery-miso.war`)

5. Restart tomcat. You can check out the API documentation to test that deployment was successful.
   These are available at `http://<server>/<webapp-name>/api-docs/index.html` (e.g.
   `http://localhost:8080/pinery-miso/api-docs/index.html`)

## Upgrading Pinery-MISO

The Pinery-MISO webapp should be upgraded whenever the MISO webapp is upgraded.

1. Build the entire MISO project
2. Stop Tomcat
3. Delete the `${CATALINA_HOME}/webapps/<webapp-name>` directory
4. Delete `${CATALINA_HOME}/webapps/<webapp-name>.war`
3. Copy the new war file (`pinery-miso/target/pinery-miso-<version>.war`) into
   `${CATALINA_HOME}/webapps/`. Rename it to match your desired webapp name (e.g. `pinery-miso.war`)
5. Restart tomcat
