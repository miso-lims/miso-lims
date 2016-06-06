# Running a MISO Instance
MISO requires some configuration directly in the source code. While we plan to
change this over time, running an instance of MISO will require building and
deploying a fork of the code base with customisations.

# Prerequisites
For each service, which may be put on the same machine, the following tools are
required:

All:
- JDK 7

Application Server:
- Tomcat 8

Database Server:
- mySQL 5
- Flyway

Notification Server:
- Nothing extra

Development Machine(s):
- Maven
- git
- Eclipse
- A merge tool such as Meld

# Creating a Fork and Setting Configuration
Use the GitHub interface or a private instance to create a forked repository.

Proceed to set up a build environment, then edit
`miso-web/src/main/webapp/WEB-INF/miso-config.xml` and set the naming schemes.
The naming schemes will determine how MISO checks if object names (especially
samples, libraries) are valid. If you do not want to use one of the supplied
ones (TGAC's standard, OICR's standard, or no checks), you will have to write
one or more specific to your organisation.

# Setting Up the Build Environment
One or machines should be set up to build MISO. A typical Linux system will
work.

You will need:

- JDK 7.0
- [Maven 3.0.5](http://maven.apache.org/download.html) or later
- Git

For development purposes, Eclipse is recommended and it might be useful to set
up the server environments for testing. There is an automatic code formatting
configuration available for Eclipse.

Locally, create a checkout:

    git clone you@server:your-miso.git
    git remote add tgac git@github.com:TGAC/miso-lims.git

# Setting Up the Database Server
The database server needs to have [mySQL 5](https://www.mysql.com/). The tool
[Flyway](https://flywaydb.org/) must also be present to migrate the database as
the application is developed, but it can be installed on a different server so
long as it can access the database server.

The default password in the following `IDENTIFIED BY` clauses should be
changed.

Once installed, start the mySQL console and create the database:

    CREATE DATABASE lims;
    USE lims;

Then add a user that has all grant access on the 'lims' db:

    GRANT ALL ON `lims`.* TO 'tgaclims'@'localhost';
    GRANT ALL ON `lims`.* TO 'tgaclims'@'localhost' IDENTIFIED BY 'tgaclims';

If your database and Tomcat install are on different machines, then you will
need to add a grant privilege to the MISO database from your remote machine:

    GRANT ALL ON `lims`.* TO 'tgaclims'@'your.tomcat.install.server';
    GRANT ALL ON `lims`.* TO 'tgaclims'@'your.tomcat.install.server' IDENTIFIED BY 'tgaclims';

Download the Flyway command line tool and install it.

# Setting Up the Application Server
The application server needs [Tomcat 8](https://tomcat.apache.org/download-80.cgi).

Create a file called `ROOT.xml` in the following directory
`$CATALINA_HOME/conf/Catalina/localhost`, creating the directory if necessary,
and populate it with the following information:

    <Context path="/ROOT" docBase="${catalina.home}/webapps/ROOT" debug="1">
      <Resource name="jdbc/MISODB" type="javax.sql.DataSource"
      driverClassName="com.mysql.jdbc.Driver"
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
      url="jdbc:mysql://localhost:3306/lims?autoReconnect=true&amp;zeroDateTimeBehavior=convertToNull&amp;useUnicode=true&amp;characterEncoding=UTF-8"
      username="tgaclims"
      password="tgaclims"/>
      <Parameter name="miso.propertiesFile" value="file:${CATALINA_HOME}/conf/Catalina/localhost/miso.properties" override="false"/>
    </Context>

If your Tomcat install has the `autoDeploy="true"` flag set in `server.xml`, if
you delete the `webapps/ROOT` directory and the `ROOT.war` file, Tomcat will
delete the context `ROOT.xml` file. Either set autoDeploy to false, and
manually deploy your webapp, or make the `ROOT.xml` file undeletable by using
`chattr +i` (`chattr -i` will undo this operation). [Upstream
bug](https://issues.apache.org/bugzilla/show_bug.cgi?id=40050)

Make sure the database path in the `server.xml` is correct for your install:

    url="jdbc:mysql://your.database.server:3306/lims"

Copy `$MISO_SRC/miso-web/src/main/resources/internal/miso.properties` to
`${CATALINA_HOME}/conf/Catalina/localhost/miso.properties`. Review and edit
this file as appropriate.

Download some supporting JARs:

    cd $CATALINA_HOME/lib
    curl -O https://repos.tgac.ac.uk/miso/common/mysql-connector-java-5.1.10.jar
    curl -O https://repos.tgac.ac.uk/miso/common/jndi-file-factory-1.0.jar

Append the following line to `$CATALINA_HOME/bin/setenv.sh` or, if using Tomcat from Debian or Ubuntu, `/etc/default/tomcat8`:

    JAVA_OPTS="$JAVA_OPTS -Dsecurity.method=jdbc -Xmx768M"

Create the directory `/storage/miso` and download the default MISO configuration files.

	cd /storage/miso
    curl https://repos.tgac.ac.uk/miso/latest/miso_userspace_properties.tar.gz | tar xvfz -

The configuration files are:

| File                      | Purpose                                                    |
|---------------------------|------------------------------------------------------------|
| `issuetracker.properties` | settings for an issue tracking system, such as JIRA or RT. |
| `mail.properties`         | email settings so that MISO can send emails to users.      |
| `security.properties`     | properties to set the security environment (see below).    |
| `submission.properties`   | properties to set the submission environment (see below).  |

## Security Environment
MISO can use either LDAP or SQL as an authentication mechanism. The mechanism
is set in both `/storage/miso/security.properties` and the
`$CATALINA_HOME/bin/setenv.sh` or `/etc/default/tomcat8` files and both must be
the same.

If you are using the database (aka JDBC), set the security method to `jdbc`.
The default configuration should work properly.

For using LDAP, set the security method to `ldap`. Additional settings are
needed for LDAP in the `security.properties`. Talk to your LDAP administrator.

If using JDBC, once running, you should change the passwords of the `admin` and
`notification` accounts.

# Setting Up the Notification Server
The notification server is a Java daemon that scans the paths containing
sequencer output. It is not required for a functioning MISO install, but
without it, sequencer runs must be added manually. Configuration for
`systemd`-based Linux systems is provided.

Create the default configuration:

    mkdir /srv/notification-server
    cp $MISO_SRC/notification-server/service/notification.properties /srv/notification-server
    cp $MISO_SRC/notification-server/service/miso-notification.service /etc/systemd/system

Edit `notification.properties`:

1. Uncomment necessary `<service>.dataPaths` line and add comma-separated paths to instrument directories to scan.
1. Replace `localhost:8080` with URL to MISO web server.

After building and deploying the JAR, start the notification server:

    sudo systemctl daemon-reload
    sudo systemctl enable miso-notification.service
    sudo systemctl start miso-notification.service

The service should start up. You can inspect `stdout` in
`/srv/notification-server/notification/notification.log` file, and `stderr` by
`sudo journalctl -f -u miso-notification`.

# Building the Applicaton
Building the application is done by:

    mvn clean package -P external

There will be two important build artefacts:

- `miso-web/target/ROOT.war`
- `notification-server/target/notification-server-*.one-jar.jar`

# Releasing and Upgrading

To install or upgrade, perform the following steps:

1. Backup your existing database.
1. Stop Tomcat.
1. Migrate the database to the newest version. (Described below.)
1. Copy the `ROOT.war` from the build to `$CATALINA_HOME/webapps`.
1. Remove `$CATALINA_HOME/webapps/ROOT`.
1. Start Tomcat.
1. Stop the notification server.
1. Copy the `notification-server-*.one-jar.jar` to `/srv/notification-server/notification-server.jar`.
1. Restart the notification server.

## Migrating the database
Updating the database (or setting it up initially) will apply patches to the database using Flyway using the `ROOT.war`.

    cd ${FLYWAY}
    rm -f lib/sqlstore-*.jar
    unzip -xjo $CATALINA_HOME/webapps/ROOT.war 'WEB-INF/lib/sqlstore-*.jar' -d lib
    ./flyway -user=$MISO_DB_USER -password=$MISO_DB_PASS -url=$MISO_DB_URL -outOfOrder=true -locations=classpath:db/migration migrate
