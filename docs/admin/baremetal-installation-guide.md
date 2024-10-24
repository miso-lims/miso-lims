# Installing MISO on baremetal

This installation guide is intended to be used if you cannot use Docker and
Docker compose, and is not trivial to set up. We recommend using Docker compose
if possible by following the
[Docker compose installation guide](../compose-installation-guide).

Refer [here](#server-deployment-troubleshooting) in the event that you run into issues during installation.

## Prerequisites

For each service, which may be put on the same machine, the following tools are
required:

Application Server:

* JDK 17
* Tomcat 10

Database Server:

* MySQL 8.0
* [Flyway 5.2.4](https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/5.2.4/) (newer versions may cause issues)

*Important Note*: MISO requires SSL (HTTPS) to function correctly over a
network. It is usually ideal to configure SSL using a proxy server. This guide
will not cover SSL configuration, but you can see an example configuration for
NGINX [here](https://github.com/miso-lims/miso-lims/blob/develop/.docker/nginx/ssl.conf).
SSL is not required to access MISO over a localhost connection (development
purposes)

## Downloading the latest release

Use the GitHub interface to download the following files for the
[latest release](https://github.com/miso-lims/miso-lims/releases/latest):

* ROOT.war
* sqlstore.jar
* setup_files.tar.gz (for new installations only)

## Setting Up the Database Server

The database server needs to have [MySQL 8.0](https://www.mysql.com/). The tool
[Flyway](https://flywaydb.org/) must also be present to migrate the database as
the application is developed, but it can be installed on a different server so
long as it can access the database server.

It is best to set a default timezone for MySQL. You can configure this in
`my.cnf`. The simplest and recommended option is to set it to UTC by adding the
following lines:

```
[mysqld]
default-time-zone='+00:00'
```

You could use a named timezone instead if you've populated the timezone tables.
See the [MySQL docs](https://dev.mysql.com/doc/refman/8.0/en/time-zone-support.html)
for more information.

The default password in the following `IDENTIFIED BY` clauses should be
changed.

Once installed, start the MySQL console and create the database:

    CREATE DATABASE lims;
    USE lims;

Then add a user that has all grant access on the 'lims' db:

    CREATE USER 'tgaclims'@'localhost' IDENTIFIED BY 'tgaclims';
    GRANT ALL ON `lims`.* TO 'tgaclims'@'localhost';

If your database and Tomcat install are on different machines, then you will
need to add a grant privilege to the MISO database from your remote machine:

    GRANT ALL ON `lims`.* TO 'tgaclims'@'your.tomcat.install.server';
    GRANT ALL ON `lims`.* TO 'tgaclims'@'your.tomcat.install.server' IDENTIFIED BY 'tgaclims';

Refer to [Development Alternatives](#development-alternatives) for a different way to do this step.


## Setting Up the Application Server

Download the [Flyway command line tool](https://flywaydb.org/download/community) version 5.2.4 and install it.
Newer versions of Flyway may cause issues, and are not recommended.

The application server needs [Tomcat 10](https://tomcat.apache.org/download-10.cgi).

Create a file called `ROOT.xml` in the following directory
`$CATALINA_HOME/conf/Catalina/localhost`, creating the directory if necessary,
and populate it with the following information:

    <Context path="/ROOT" docBase="${catalina.home}/webapps/ROOT" reloadable="false">
      <Resources allowLinking="true"/>
      <Resource name="jdbc/MISODB" type="javax.sql.DataSource"
      driverClassName="com.mysql.cj.jdbc.Driver"
      initialSize="32"
      maxIdle="10"
      maxActive="100"
      maxWait="1000"
      removeAbandoned="true"
      removeAbandonedTimeout="120"
      logAbandoned="true"
      testWhileIdle="true"
      testOnBorrow="true"
      testOnReturn="true"
      validationQuery="select 1"
      url="jdbc:mysql://localhost:3306/lims?autoReconnect=true&amp;characterEncoding=UTF-8&amp;allowPublicKeyRetrieval=true&amp;sslMode=DISABLED&amp;connectionTimeZone=SERVER&amp;cacheDefaultTimeZone=false"
      username="tgaclims"
      password="tgaclims"/>
      <Parameter name="miso.propertiesFile" value="file:${catalina.home}/conf/Catalina/localhost/miso.properties" override="false"/>
    </Context>

Make sure the database path in `ROOT.xml` is correct for your install:

    url="jdbc:mysql://your.database.server:3306/dbname"

Note that the connection parameters suggested above disable SSL between the web and database server,
meaning that the communications will not be encrypted. You should consider the security implications
of this -- especially if the connection is over a public network -- and configure SSL properly if
required.

Extract the `setup_files.tar.gz` file and copy `miso.properties` to
`$CATALINA_HOME/conf/Catalina/localhost/miso.properties`. This file contains site-specific configuration that you should
review and modify as appropriate. See [Site Configuration](../site-configuration) for more information.

Append the following line to `$CATALINA_HOME/bin/setenv.sh` or, if you installed Tomcat through apt, `/etc/default/tomcat10`:

    JAVA_OPTS="$JAVA_OPTS -Dsecurity.method=jdbc -Xmx768M"

(Update the security method if you are using LDAP or Active Directory LDAP.)

Create the directory `/storage/miso` and subdirectories `/storage/miso/log` and `/storage/miso/files`. Note that these
directories can be changed in `miso.properties`, so if you changed them, make sure to create the directories you
specified instead.

    mkdir -p /storage/miso/log
    mkdir -p /storage/miso/files

Ensure that the user that Tomcat runs as has write permission to the storage directory. For example, if the user is in the 'tomcat' group:

    chgrp -R tomcat /storage/miso/

Copy the following configuration files from `setup_files.tar.gz` into
the `/storage/miso/` directory:

| File                      | Purpose                                                    |
|---------------------------|------------------------------------------------------------|
| `security.properties`     | properties to set the security environment (see below).    |
| `submission.properties`   | properties to set the submission environment.              |

### Security Environment (updating `/storage/miso/security.properties`)

MISO can use either LDAP (`ldap`), Active Directory LDAP (`ad`), or JDBC
(`jdbc`) as an authentication mechanism. This is set by the `-Dsecurity.method`
noted in the previous section, and the same value must be set in `security.properties`.

If you are using JDBC (i.e. storing usernames and passwords in the MISO database), set the security method to `jdbc`. No
additional configuration is necessary.

For using LDAP, set the security method to `ldap`. Additional settings are needed for LDAP in the `security.properties`.
Talk to your LDAP administrator.

To use Active Directory, a specific kind of LDAP, set the security method to `ad`. Some active directory settings are
needed in addition to the LDAP settings in the `security.properties` file.

The search for a user is done against `userPrincipalName` which takes the form of
an email address. To log in, the user will type their username and to do the lookup
their username will be added to the domain specified in the property
`security.ad.emailDomain`.

Use the `security.ad.url` property to indicate the url for the Active Directory.
Some valid examples are: `ad.oicr.on.ca`, `ldap://ad.oicr.on.ca:389` and
`ldaps://ad.oicr.on.ca:636`.

The groups used by MISO are `ROLE_INTERNAL` for regular users
and `ROLE_ADMIN` for administrators. If you find these names
too general you may wish to add a prefix before adding these groups to your Active
Directory. For example `MISO_ROLE_INTERNAL` gives a clearer indication as to what
the group is used for. In this case you will need to set the property
`security.ldap.stripRolePrefix` to the value `MISO_` to allow MISO to ignore the
prefix.

If using JDBC, once running, you should change the passwords of the `admin` and
`notification` accounts.


## Setting Up the Run Scanner

[Run Scanner](https://github.com/miso-lims/runscanner) is a webservice that scans the paths containing sequencer output.
It is not required for a functioning MISO install, but without it, sequencer runs must be added manually.

Please see the
[Run Scanner Installation and Setup Guide](https://miso-lims.readthedocs.io/projects/runscanner/en/latest/installation/)
for setup instructions.

Once complete, edit `$CATALINA_HOME/conf/Catalina/localhost/miso.properties` of the MISO Tomcat server and set
`miso.runscanner.urls` to the URL of the Run Scanner instance. It is possible to set up multiple run scanners managing
different sequencers and add all the URLs to `miso.properties`. If you are adding Run Scanner to a
previously-established MISO environment, restart MISO.


## Installing and Upgrading

Prior to the installation, ensure that you have followed the instructions in the above and have WAR files for both MISO
(`ROOT.war`) and, if desired, [Run Scanner](https://github.com/miso-lims/runscanner)(`runscanner-*.war`).

To install or upgrade, perform the following steps:

1. If performing an upgrade, make sure to read all release notes since the previous version you were running. Some
   releases indicate additional upgrade instructions, which may include
    * Configuration changes (in `miso.properties` or elsewhere)
    * Database preparation or cleanup steps
    * Other necessary steps or warnings
1. Backup your existing database.
1. Stop MISO's Tomcat.
1. Remove `$CATALINA_HOME/webapps/ROOT` directory and `$CATALINA_HOME/webapps/ROOT.war` file if they exist.
1. Copy the new `ROOT.war` to `$CATALINA_HOME/webapps`.
1. Migrate the database to the newest version. (Described below.)
1. If also releasing Run Scanner, deploy it:
    1. Stop Run Scanner's Tomcat.
    1. Deploy the Run Scanner WAR.
    1. Restart Run Scanner's Tomcat.
1. Restart MISO's Tomcat.

## Migrating the database

Flyway is used to apply patches to your database to make it compatible with the new MISO version.
The same path should be used for `MISO_FILES_DIR` as is set for `miso.fileStorageDirectory` in
`miso.properties` (`/storage/miso/files/` by default). `SQLSTORE.JAR` should be the `sqlstore.jar`
file you downloaded.

The MySQL root user, or another user with similar privileges, must be used to run Flyway and create
and restore backups. `GRANT ALL PRIVILEGES ON *.* TO 'username'@'host';` will grant all the
necessary privileges. Only a subset of these is required, but we have not investigated specifics.
The user specified in `ROOT.xml`, which the web server will use to communicate with the database,
should **NOT** be granted global (`*.*`) privileges, so this should be a different user.

    cd ${FLYWAY}
    rm -f jars/sqlstore-*.jar
    cp ${SQLSTORE.JAR} jars
    ./flyway -user=root -password=$MYSQL_ROOT_PASSWORD -url=$MISO_DB_URL -outOfOrder=true -locations=classpath:db/migration,classpath:uk.ac.bbsrc.tgac.miso.db.migration migrate -placeholders.filesDir=${MISO_FILES_DIR}

`$MISO_DB_URL` should be in the same format as in the `ROOT.xml`, except replacing `&amp;` with just
`&`, and adding `&useSSL=false` (redundant, but prevents some warning messages):

```
jdbc:mysql://localhost:3306/lims?autoReconnect=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&sslMode=DISABLED&connectionTimeZone=SERVER&cacheDefaultTimeZone=false&useSSL=false
```

If you run into an issue with migration `V0611`, ensure that the user running Flyway has read and write permissions on
`MISO_FILES_DIR`.

If you encounter other errors migrating the database, make sure that you are using the recommended version of Flyway
(see [Prerequisites](#prerequisites)).

# Server Deployment Troubleshooting

* Whenever unexpected behaviour arises, be sure to check the build logs, located at `${CATALINA_BASE}/logs`. Read through the output of `catalina.out`, `catalina.<date>.log`, and `localhost.<date>.log`.
* Ensure you are using the correct version of Java with Tomcat. This can be checked in `catalina.out`. In case you are not, append/modify the value of `$JAVA_HOME` in `setenv.sh` or `/etc/default/tomcat10`. 
* If file access for `/storage/miso` is causing an error, [this](https://stackoverflow.com/questions/56827735/how-to-allow-tomcat-war-app-to-write-in-folder) may help.


# Development Alternatives

 If you can't or don't want to install the correct version of MySQL, this alternative allows the same result without you downloading it. To do this, download [Docker](https://docs.docker.com/get-docker/) to use a Docker container. Creating the container:

    docker run --name $CONTAINER_NAME -e MYSQL_ROOT_PASSWORD=$ROOT_PASSWORD -e MYSQL_DATABASE=$DB_NAME -e MYSQL_USER=$DB_USERNAME -e MYSQL_PASSWORD=$DB_PASSWORD -p 3306:3306 -d mysql:8.0


Where:

* `$CONTAINER_NAME` is your desired Docker container name.
* `$ROOT_PASSWORD` is the root password to your MySQL.
* `$DB_NAME` is the name of the database (e.g. "lims").
* `$DB_USERNAME` is the username to access the database (e.g. "tgaclims").
* `$DB_PASSWORD` is the password to access the database (e.g. "tgaclims").

To map to a different port, change `-p 3306:3306` to `-p $PORT:3306`, where `$PORT` is your desired port.

If you use this container method, you can skip the [database configuration](#setting-up-the-database-server) step entirely.