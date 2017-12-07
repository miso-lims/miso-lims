---
layout: page
title: "Administrator's Manual"
category: adm
date: 2016-01-11 13:51:46
---


# Running a MISO Instance
MISO requires some configuration directly in the source code. While we plan to
change this over time, running an instance of MISO will require building and
deploying a fork of the code base with customisations.

## Prerequisites
For each service, which may be put on the same machine, the following tools are
required:

Application Server:

* JDK 8
* Tomcat 8

Database Server:

* MySQL 5.7
* Flyway

Run Scanner:

* JDK 8
* Tomcat 8
* C++ build environment
* jsoncpp

Development Machine(s):

* Maven
* git
* Eclipse
* A merge tool such as Meld

<a id="latest-release">
## Downloading the latest release
Use the GitHub interface download the [latest release](https://github.com/TGAC/miso-lims/releases/latest).
Extract the `.zip` or `.tar.gz` file.

Proceed to set up a build environment.

# Setting Up the Build Environment
One or more machines should be set up to build MISO. A typical Linux system will
work; a typical Mac system may work. MISO is not guaranteed to work on Windows.

You will need:

* JDK 8.0
* [Maven 3.0.5](http://maven.apache.org/download.html) or later
* Git

For development purposes, Eclipse is recommended and it might be useful to set
up the server environments for testing. There is an automatic code formatting
configuration available for Eclipse.

## Setting Up the Database Server
The database server needs to have [MySQL 5.7](https://www.mysql.com/). The tool
[Flyway](https://flywaydb.org/) must also be present to migrate the database as
the application is developed, but it can be installed on a different server so
long as it can access the database server.

The default password in the following `IDENTIFIED BY` clauses should be
changed.

Once installed, start the MySQL console and create the database:

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

<a id="root">
# Setting Up the Application Server
The application server needs [Tomcat 8](https://tomcat.apache.org/download-80.cgi).

Create a file called `ROOT.xml` in the following directory
`$CATALINA_HOME/conf/Catalina/localhost`, creating the directory if necessary,
and populate it with the following information:

    <Context path="/ROOT" docBase="${catalina.home}/webapps/ROOT">
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
      <Parameter name="miso.propertiesFile" value="file:${catalina.home}/conf/Catalina/localhost/miso.properties" override="false"/>
    </Context>

Make sure the database path in `ROOT.xml` is correct for your install:

    url="jdbc:mysql://your.database.server:3306/lims"

If your Tomcat install has the `autoDeploy="true"` flag set in `server.xml`, if
you delete the `webapps/ROOT` directory and the `ROOT.war` file, Tomcat will
delete the context `ROOT.xml` file. Either set autoDeploy to false, and
manually deploy your webapp, or make the `ROOT.xml` file undeletable by using
`chattr +i` (`chattr -i` will undo this operation). [Upstream
bug](https://issues.apache.org/bugzilla/show_bug.cgi?id=40050)

Copy `$MISO_SRC/miso-web/src/main/resources/external/miso.properties` to
`$CATALINA_HOME/conf/Catalina/localhost/miso.properties`. Review and edit
this file as appropriate (see <a href="naming-schemes">Naming Schemes</a> below).

* The naming schemes will determine how MISO checks if object names (especially
samples, libraries) are valid. If you do not want to use one of the supplied
ones (TGAC's standard, OICR's standard, or no checks), you will have to write
one or more specific to your organisation. See <a href="naming-schemes">Naming Schemes</a>
below for more information.
* If using a bulk barcode scanner (only VisionMate is supported at present), 
set `miso.boxscanner.enabled` to `true` and change the host and port for your
VisionMate server.
* Optional: Update `miso.bugUrl` to the URL for your internal issue tracker or other
method for users to report issues using the "Report a problem" link in the header.
* Update `miso.instanceName` to update the instance name displayed in the header.


Download some supporting JARs:

    cd $CATALINA_HOME/lib
    curl -O https://repos.tgac.ac.uk/miso/common/mysql-connector-java-5.1.10.jar
    curl -O https://repos.tgac.ac.uk/miso/common/jndi-file-factory-1.0.jar

Append the following line to `$CATALINA_HOME/bin/setenv.sh` or, if using Tomcat from Debian or Ubuntu, `/etc/default/tomcat8`:

    JAVA_OPTS="$JAVA_OPTS -Dsecurity.method=jdbc -Xmx768M"

(Update the security method if you are using LDAP or Active Directory LDAP.)

Create the directory `/storage/miso`:

  	cd /storage/miso

Move the following configuration files from `miso-lims/miso-web/src/main/resources` into 
the `/storage/miso/` directory:

| File                      | Purpose                                                    |
|---------------------------|------------------------------------------------------------|
| `security.properties`     | properties to set the security environment (see below).    |
| `submission.properties`   | properties to set the submission environment.              |

## Security Environment (updating `/storage/miso/security.properties`)
MISO can use either LDAP (`ldap`), Active Directory LDAP (`ad`), or JDBC
(`jdbc`) as an authentication mechanism. This is set by the `-Dsecurity.method`
noted in the previous section.

If you are using JDBC (aka storing usernames and passwords in the database), set the 
security method to `jdbc`.
The default configuration should work properly.

For using LDAP, set the security method to `ldap`. Additional settings are
needed for LDAP in the `security.properties`. Talk to your LDAP administrator.

To use Active Directory, a specific kind of LDAP, set the security method to
`ad`. Three additional settings are needed for Active Directory in the 
`security.properties` file.

| Property                    | Purpose                                                    |
|-----------------------------|------------------------------------------------------------|
|`security.ad.emailDomain`    | Domain added to username for lookup (e.g. ad.oicr.on.ca)   |
|`security.ad.url`            | Url for Active Directory server (e.g. ldap://ad.oicr.on.ca)|
|`security.ad.stripRolePrefix`| Prefix to be removed from group (e.g. MISO\_)               |

The search for a user is done against `userPrincipalName` which takes the form of
an email address. To login the user will type their username and to do the lookup
their username will be added to the domain specified in the property
`security.ad.emailDomain`.

Use the `security.ad.url` property to indicate the url for the Active Directory.
Some valid examples are: `ad.oicr.on.ca`, `ldap://ad.oicr.on.ca:389` and
`ldaps://ad.oicr.on.ca:636`.

The groups used by MISO are `ROLE_INTERNAL` for regular users, `ROLE_EXTERNAL` for
external collaborators and `ROLE_ADMIN` for administrators. If you find these names
too general you may wish to add a prefix before adding these groups to your Active
Directory. For example `MISO_ROLE_INTERNAL` gives a clearer indication as to what 
the group is used for. In this case you will need to set the property
`security.ad.stripRolePrefix` to the value `MISO_` to allow MISO to ignore the
prefix.

If using JDBC, once running, you should change the passwords of the `admin` and
`notification` accounts.

<a name="naming-schemes">
## Naming Schemes (updating `$CATALINA_HOME/conf/Catalina/localhost/miso.properties`)
MISO Naming Schemes are used to validate and generate entity String fields. They are
used for all `name` fields, and some `alias` fields. You may configure a base naming
scheme, and customize it by switching validators and generators in `miso.properties` in 
`$CATALINA_HOME/conf/Catalina/localhost/`.

The options for `miso.naming.scheme` are `default` and `oicr`, which have these
default configurations:

|                             | default                      | oicr                          |
|-----------------------------|------------------------------|-------------------------------|
| Name generator              | DefaultNameGenerator         | DefaultNameGenerator          |
| Name Validator              | DefaultNameValidator         | DefaultNameValidator          |
| Sample Alias Generator      | none                         | OicrSampleAliasGenerator      |
| Sample Alias Validator      | DefaultSampleAliasValidator  | OicrSampleAliasValidator      |
| Library Alias Generator     | DefaultLibraryAliasGenerator | OicrLibraryAliasGenerator     |
| Library Alias Validator     | DefaultLibraryAliasValidator | OicrLibraryAliasValidator     |
| Project ShortName Validator | AllowAnythingValidator       | OicrProjectShortNameValidator |
| Configurable components     | all                          | none                          |

If the naming scheme you’ve selected has configurable components, you may configure them
as follows.

### `miso.naming.generator.nameable.name`

| Option    | Example     |
|-----------|-------------|
| default   | SAM1        |
| classname | SampleImpl1 |

### `miso.naming.generator.sample.alias`

| Option  | Example               | Note                             |
|---------|-----------------------|----------------------------------|
| oicr    | PROJ_0001_Ad_P_nn_1-1 | for use with DetailedSample only |

### `miso.naming.generator.library.alias`

| Option  | Example                  | Note                                                                                                     |
|---------|--------------------------|----------------------------------------------------------------------------------------------------------|
| default | XX_LYY-1                 | XX and YY taken from sample alias - depends on sample alias passing default validator with default regex |
| oicr    | PROJ_0001_Ad_P_PE_300_WG | For use with DetailedSample only. Depends on sample alias passing oicr validator                         |

### `miso.naming.validator.nameable.name`

| Option   | Detail                                       | Allow null | Allow duplicates | Custom Regex | Custom Duplication |
|----------|----------------------------------------------|------------|------------------|--------------|--------------------|
| default  | Matches 'default' generator, or custom regex | no         | no               | yes          | yes                |
| allowany | Only checks that the name is not null        | no         | yes              | no           | no                 |

### `miso.naming.validator.sample.alias`

| Option   | Detail                                         | Allow null | Allow duplicates | Custom Regex | Custom Duplication |
|----------|------------------------------------------------|------------|------------------|--------------|--------------------|
| default  | Default regex: `([A-z0-9]+)_S([A-z0-9]+)_(.*)` | no         | no               | yes          | no                 |
| allowany | Only checks that the alias is not null         | no         | yes              | no           | no                 |
| oicr     | Matches 'oicr' generator                       | no         | no               | no           | no                 |

### `miso.naming.validator.library.alias`

| Option   | Detail                                 | Allow null | Allow duplicates | Custom Regex | Custom Duplication |
|----------|----------------------------------------|------------|------------------|--------------|--------------------|
| default  | Matches 'default' generator            | no         | no               | yes          | no                 |
| allowany | Only checks that the alias is not null | no         | yes              | no           | no                 |
| oicr     | Matches 'oicr' generator               | no         | no               | no           | no                 |

### `miso.naming.validator.project.shortName`

| Option   | Detail                                 | Allow null | Allow duplicates | Custom Regex | Custom Duplication |
|----------|----------------------------------------|------------|------------------|--------------|--------------------|
| allowany | Optional field, no format specified    | yes        | yes              | no           | no                 |
| oicr     | 3-5 characters, CAPS and numbers only  | no         | no               | no           | no                 |

If a validator accepts custom regex, it can be configured via `<base property>.regex`.
e.g. `miso.naming.validator.nameable.name.regex:.*` to allow any name. A custom validator
must be specified for this property to be enabled - the naming scheme’s default validator
will not be altered.

If a validator accepts custom duplication, that can be configured via
`<base property>.duplicates`. e.g. `miso.naming.validator.library.alias.duplicates:true`
to allow duplicate library aliases. A custom validator must be specified for this
property to be enabled - the naming scheme’s default validator will not be altered.

Existing naming schemes:

| Naming Scheme              | Used for  | Generation                                                  | Validation         |
|----------------------------|-----------|-------------------------------------------------------------|--------------------|
| DefaultEntityNamingScheme  | all       | Uses 3-digit entity identifier (e.g. 'SAM' for Sample) + ID | Matches validation |
| AllowAnythingNamingScheme  | all       | Uses Java class name. Not intended for generation purposes  | None               |
| DefaultSampleNamingScheme  | Samples   | None built in                                               | TGAC/EI's standard |
| OicrSampleNamingScheme     | Samples   | None built in                                               | OICR's standard    |
| DefaultLibraryNamingScheme | Libraries | None built in                                               | TGAC/EI's standard |
| OicrLibraryNamingScheme    | Libraries | None built in                                               | OICR's standard    |

A Sample alias generator may also be configured via `miso.naming.generator.sample.alias`

The values used in these options refer to classes in the `uk.ac.bbsrc.tgac.miso.core.service.naming`
Java package. To create a new naming scheme option, create a new class in this package that extends 
`MisoNamingScheme<T>`. To create a new Sample alias generator, extend `NameGenerator<Sample>`.
Extending the functionality to validate and/or generate additional fields is possible, but will
require modifications at the Service layer as well.

# Setting Up the Run Scanner
The run scanner is a webservice that scans the paths containing
sequencer output. It is not required for a functioning MISO install, but
without it, sequencer runs must be added manually.

If run scanner is being hosted on a separate server from MISO, create a file called `ROOT.xml`
in the following directory `$CATALINA_HOME/conf/Catalina/localhost` on that server (create 
the directory if necessary), and populate it with the following information:

    <Context>
       <Parameter name="runscanner.configFile" value="/etc/runscanner.json" override="false"/>
    </Context>

If the run scanner is being hosted on the same machine as MISO is, create a file called
`runscanner.xml` and populate it with the same contents as above.

In `/etc/runscanner.json`, or another path of your choosing, put JSON data describing your instruments. You will need one record for each instrument:

    {
      "path": "/some/directory/where/sequencer/writes",
      "platformType": "ILLUMINA",
      "name": "default",
      "timeZone": "America/Toronto",
      "parameters": {}
    }

The JSON file then contains a list of instruments:

    [
      {
        "path": "/srv/sequencer/hiseq2500_1",
        "platformType": "ILLUMINA",
        "name": "default",
        "timeZone": "America/Toronto",
        "parameters": {}
      },
      {
        "path": "/srv/sequencer/hiseq2500_2",
        "platformType": "ILLUMINA",
        "name": "default",
        "timeZone": "America/Toronto",
        "parameters": {}
      }
    ]

The name/platform-type combination decide what scanner is used to interpret the sequencer's results. A list of supported scanners can be found on the status page or the debugging interface below.

The parameters are set based on the processor. Currently, PACBIO/default requires `address` to be set to the URL of the PacBio machine.

If you intend to scan Illumina runs, you will have to build `runscanner-illumina` and install it. See `runscanner-illumina/README.md` for instructions.

Start Tomcat on this machine.

Edit `$CATALINA_HOME/conf/Catalina/localhost/miso.properties` and set `miso.runscanner.urls` to the URL of the Run Scanner instance and restart MISO.

It is possible to set up multiple run scanners managing different sequencers and add all the URLs to `miso.properties`.

You can view the run scanner's state from the main page of the runscanner server (http://runscanner.url:8080).

# Building the Application

`cd` into `$MISO_SRC`. 
Build the application using:

    mvn clean package -P external

There will be two important build artefacts:

* `miso-web/target/ROOT.war`
* `runscanner/runscanner-*.war`

# Releasing and Upgrading

To install or upgrade, perform the following steps:

1. Backup your existing database.
1. Stop Tomcat.
1. Migrate the database to the newest version. (Described below.)
1. Remove `$CATALINA_HOME/webapps/ROOT`.
1. Copy the `ROOT.war` from the build to `$CATALINA_HOME/webapps`.
1. Make any necessary configuration changes to `$CATALINA_HOME/conf/Catalina/localhost/miso.properties`.
1. Deploy the runscanner:
    * If deploying to the same Tomcat as MISO:
        1. Copy the `runnscanner-*.war` from the build to `$CATALINA_HOME/webapps` and rename it to `runscanner.war`.
        1. Start Tomcat.
    * If deploying to a different server than MISO:
        1. Start Tomcat.
        1. Stop the run scanner.
        1. Deploy the run scanner.
        1. Restart the run scanner.

## Migrating the database

Updating the database (or setting it up initially) will apply patches to the database using Flyway using the `ROOT.war`.

    cd ${FLYWAY}
    rm -f lib/sqlstore-*.jar
    unzip -xjo $CATALINA_HOME/webapps/ROOT.war 'WEB-INF/lib/sqlstore-*.jar' -d lib
    ./flyway -user=$MISO_DB_USER -password=$MISO_DB_PASS -url=$MISO_DB_URL -outOfOrder=true -locations=classpath:db/migration,classpath:uk.ac.bbsrc.tgac.miso.db.migration migrate

# Monitoring

The main MISO application and Run Scanner can be monitored using [Prometheus](http://prometheus.io/).
