MISO: An open source LIMS for small-to-large scale sequencing centres
=====================================================================

&copy; 2013. [The Genome Analysis Centre] [1], Norwich, UK

> MISO project contacts: Robert Davey (robert.davey@tgac.ac.uk), Mario Caccamo (mario.caccamo@tgac.ac.uk)
>
> MISO is free software: you can redistribute it and/or modify
> it under the terms of the GNU General Public License as published by
> the Free Software Foundation, either version 3 of the License, or
> (at your option) any later version.
>
> MISO is distributed in the hope that it will be useful,
> but WITHOUT ANY WARRANTY; without even the implied warranty of
> MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
> GNU General Public License for more details.
>
> You should have received a copy of the GNU General Public License
> along with MISO.  If not, see <http://www.gnu.org/licenses/>.
> 
> You can follow MISO development on Twitter: @misolims (https://twitter.com/misolims), @froggleston (https://twitter.com/froggleston)

VERSION 0.1.9 (Flourine)

1) BUILDING
-----------

You will need [Maven 2.2.1] [3] to build MISO (*NOTE Not Maven 3!*). Once you have grabbed the code and installed Maven, in the root of the project (you should see a pom.xml file and module directories like `analysis-server` and `core` etc) call:

    mvn clean install -P external

This will *NOT* run unit tests. To enable unit tests of database sanity, use:

    mvn -DsqlTests=true install -P external

In order for the tests to work, you will need a MISO database set up as per the instructions below, and the correct properties specified in `sqlstore/src/test/resources/test.db.properties`.

All being well after a few minutes (it can take a while to download all the required artifacts given the speed of your connection), you should see `BUILD SUCCESSFUL`.

2) UPGRADING
------------

If you are upgrading from a previous version of MISO, you will need to follow these steps:

* Backup your existing database and apply any database patches in https://repos.tgac.bbsrc.ac.uk/miso/latest/sql/patches
* Stop Tomcat
* Delete (or move) the old `<tomcat>/webapps/ROOT.war`
* Delete the `<tomcat>webapps/ROOT` directory
* Copy the newly built `miso-web/target/ROOT.war` to `<tomcat>/webapps`
* Restart Tomcat

Done!

3) INSTALLING
-------------

3.1 ) Setting up the MISO database

You will need to install MySQL v5 or greater. You will then need the two latest MISO database dumps. These are available
from our repository here:

https://repos.tgac.bbsrc.ac.uk/miso/latest/sql/lims-schema-20130301.sql

https://repos.tgac.bbsrc.ac.uk/miso/latest/sql/miso_type_data_20120921.sql

Log in to your local MySQL install, and create a database called 'lims':

    CREATE DATABASE lims;
    USE lims;

Then add a user that has all grant access on the 'lims' db:

    GRANT ALL ON `lims`.* TO 'tgaclims'@'localhost';
    GRANT ALL ON `lims`.* TO 'tgaclims'@'localhost' IDENTIFIED BY 'tgaclims';

If your database and Tomcat install are on different machines, then you will obviously need to add a grant privilege to the MISO
database from your remote machine:

    GRANT ALL ON `lims`.* TO 'tgaclims'@'your.tomcat.install.server';
    GRANT ALL ON `lims`.* TO 'tgaclims'@'your.tomcat.install.server' IDENTIFIED BY 'tgaclims';

Then populate the database with the two dumps by running the following commands at a shell prompt:

    mysql -u tgaclims -p -D lims < lims-schema-20130301.sql
    mysql -u tgaclims -p -D lims < miso_type_data_20120921.sql

3.2 ) Setting up the MISO web application

You will need a suitable Java web application container, such as Tomcat 6.x (http://tomcat.apache.org/download-60.cgi),
to deploy MISO. Once Tomcat has been installed, download the latest MISO WAR file. The latest WAR file can be found here:

https://repos.tgac.bbsrc.ac.uk/miso/latest/ROOT.war

If you're the thrill-seeking type, nightly builds from the develop branch which are "bleeding edge" can be found here, following the format ROOT-{version}_{buildNumber}_{revision}.war:

https://repos.tgac.bbsrc.ac.uk/miso/nightly/

Copy this file to your `<tomcat-install>/webapps/` directory. DO NOT START TOMCAT YET. Instead, follow the configuration steps below.

4 ) CONFIGURATION
-----------------

MISO is configured using the Tomcat context xml system, the main miso.properties file that resides in the
`<tomcat-install>/webapps/ROOT/WEB-INF/classes` directory, and properties files that live in the default MISO storage directory.

Make sure that these options are correct for your system at the time of container startup.

4.1 ) Setting the database environment

MISO uses JNDI (Java Naming and Directory Interface) to configure the connection to the underlying MySQL database that you set
up in the step 1.1. To configure Tomcat to manage the JNDI datasource, please create a file called `ROOT.xml` in the following
directory `<tomcat-install>/conf/Catalina/localhost` and populate it with the following information:

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

     <Resource name="file/CONAN_PROPERTIES" type="java.util.File"
                factory="uk.ac.ebi.fgpt.naming.factory.FileFactory"
                 path="../webapps/ROOT/WEB-INF/conan.properties"/>

     <!-- uncomment if using the Stats DB -->
     <!--
     <Resource name="jdbc/STATSDB" type="javax.sql.DataSource"
      driverClassName="com.mysql.jdbc.Driver" initialSize="32" maxIdle="32" maxActive="100" maxWait="-1"
      url="jdbc:mysql://your_stats_db_server:3306/run_statistics?zeroDateTimeBehavior=convertToNull&amp;useUnicode=true&amp;characterEncoding=UTF-8"
      username="statsuser"
      password="statspass"/>
      -->
    </Context>

If the `<tomcat-install>/conf/Catalina/localhost` directory doesn't exist (usually due to a fresh Tomcat install), either create
it yourself, or cycle a Tomcat startup/shutdown and it should appear.

If your Tomcat install has the autoDeploy="true" flag set in server.xml, if you delete the `webapps/ROOT` directory and the ROOT.war
file, Tomcat will delete the context ROOT.xml file. Either set autoDeploy to false, and manually deploy your webapp, or make
the ROOT.xml file undeletable by using 'chattr +i' ('chattr -i' will undo this operation). See: https://issues.apache.org/bugzilla/show_bug.cgi?id=40050

Again, if your database and Tomcat install are on different machines, then you will need to change the connection URL above to
the name of your remote database server:

    url="jdbc:mysql://your.database.server:3306/lims"

You will need to copy the mysql connector library and the JNDI File Factory library to your Tomcat install to ensure the JNDI system can see the MISO database.
Grab the jar files from:

https://repos.tgac.bbsrc.ac.uk/miso/common/mysql-connector-java-5.1.10.jar

https://repos.tgac.bbsrc.ac.uk/miso/common/jndi-file-factory-1.0.jar

And copy them to <tomcat-install>/lib/

4.2 ) Setting up miso.properties file

The main miso.properties file is located in the `<tomcat-install>/lib/classes/` directory.

The miso.properties file specifies a number of general options. The only option that needs attention is `miso.baseDirectory`.
Please set this to the absolute path of where you would like MISO to store files on disk, i.e. logs, uploaded files, etc.

The default path is `/storage/miso`. It is recommended that create this path on disk and that you do not change this location as some things will break.

> NB: You shouldn't need to change the miso.properties file as long as the /storage/miso directory exists on your filesystem and is writeable!

4.3 ) Setting up userspace properties

MISO achieves userspace configuration via extra properties files kept in the storage directory specified by the `miso.baseDirectory`
property (see 2.2 above). Default versions of these files are available here:

https://repos.tgac.bbsrc.ac.uk/miso/latest/miso_userspace_properties.tar.gz

Unpack this file to your MISO storage directory, which again is `/storage/miso` by default. You should see 4 files:

* issuetracker.properties - settings for an issue tracking system, such as JIRA or RT.
* mail.properties - email settings so that MISO can send emails to users.
* security.properties - properties to set the security environment (see 2.4 below).
* submission.properties - properties to set the submission environment (see 2.5 below).

4.4 ) Setting the security environment

MISO can use either LDAP or SQL as an authentication mechanism, and will need to know where your chosen method resides. These
properties need to be set before the container is started, and relate to properties files stored on disk.

Firstly, the security options for both auth systems are specified in the `security.properties` file. If you are using JDBC,
then it is unlikely that you will need to change any options, but the LDAP setup is far more complex.
Please ask your sysadmin to match up the MISO LDAP options with your local LDAP server options.

Secondly, the `security.method` environment property relates to the security mechanism, i.e. 'ldap' or 'jdbc', where 'ldap' auths
against an LDAP server, and 'jdbc' auths against a local database (usually the MISO database itself). Please use 'jdbc' if you
are unsure. There is a default admin user that is shipped with the MISO database, and the username/password login is admin/admin.

To set the `security.method` property, use local environment variables on Tomcat startup. Please see the "Starting MISO" section below.

> NB: You are likely to see startup errors like the one below if you do not set this property, and start Tomcat as outlined in section 3!

> 20-Oct-2010 11:08:28 org.apache.catalina.core.StandardContext start

> SEVERE: Error listenerStart

> 20-Oct-2010 11:08:28 org.apache.catalina.core.StandardContext start

> SEVERE: Context [/tgac] startup failed due to previous errors

4.5 ) Submission properties

MISO is able to submit sequence data to the major sequence archives, i.e. the SRA at the EBI, and the EMBL GenBank. Currently,
only ERA submissions are supported out-of-the-box. The settings for these services are specified in the `submission.properties` file.

5 ) STARTING MISO
-----------------

To set the `security.method` property to use the local MISO database and start the Tomcat instance, run the following command, which will use the JDBC security.method and assign 768MB RAM to the Java heap:

    cd <tomcat-install>/bin/
    JAVA_OPTS="$JAVA_OPTS -Dsecurity.method=jdbc -Xmx768M" ./startup.sh

All going well, you should be able to go to http://localhost:8080/ and log in to MISO using the admin/admin username and password combination.

> NB: Please remember that if you need to set any properties in the userspace properties files, you will need to make sure that you stop Tomcat, make the changes and then restart it.

  [1]: http://www.tgac.ac.uk/        "TGAC"
  [2]: http://www.tgac.ac.uk/miso/  "MISO"
  [3]: http://maven.apache.org/download.html    "Maven"
