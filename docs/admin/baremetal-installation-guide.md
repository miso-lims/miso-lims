# Installing MISO on baremetal

This installation guide is intended to be used if you cannot use Docker and
Docker compose, and is not trivial to set up. We recommend using Docker compose
if possible by following the
[Docker compose installation guide](../compose-installation-guide).

## Prerequisites
For each service, which may be put on the same machine, the following tools are
required:

Application Server:

* JDK 8
* Tomcat 8

Database Server:

* MySQL 5.7.7 or MariaDB 10.2
* [Flyway 5.2.4](https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/5.2.4/) (newer versions may cause issues)

Development Machine(s):

* Maven
* git
* Eclipse
* A merge tool such as Meld

## Downloading the latest release
Use the GitHub interface to download the [latest release](https://github.com/miso-lims/miso-lims/releases/latest).
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

    CREATE USER 'tgaclims'@'localhost' IDENTIFIED BY 'tgaclims';
    GRANT ALL ON `lims`.* TO 'tgaclims'@'localhost';

If your database and Tomcat install are on different machines, then you will
need to add a grant privilege to the MISO database from your remote machine:

    GRANT ALL ON `lims`.* TO 'tgaclims'@'your.tomcat.install.server';
    GRANT ALL ON `lims`.* TO 'tgaclims'@'your.tomcat.install.server' IDENTIFIED BY 'tgaclims';


# Setting Up the Application Server

Download the [Flyway command line tool](https://flywaydb.org/download/community) version 5.2.4 and install it.
Newer versions of Flyway may cause issues, and are not recommended.

The application server needs [Tomcat 8](https://tomcat.apache.org/download-80.cgi).

Create a file called `ROOT.xml` in the following directory
`$CATALINA_HOME/conf/Catalina/localhost`, creating the directory if necessary,
and populate it with the following information:

    <Context path="/ROOT" docBase="${catalina.home}/webapps/ROOT">
      <Resources allowLinking="true"/>
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

Copy `$MISO_SRC/miso-web/src/main/resources/miso.properties` to
`$CATALINA_HOME/conf/Catalina/localhost/miso.properties`. Review and edit
this file as appropriate (see [Naming Schemes](#naming-schemes) below).

* The naming schemes will determine how MISO checks if object names (especially
samples, libraries) are valid. If you do not want to use one of the supplied
ones (default standard, OICR's standard, or no checks), you will have to write
one or more specific to your organisation. See [Naming Schemes](#naming-schemes)
below for more information.
* Optional: If using any bulk barcode scanners (only VisionMate is supported at present),
define `miso.visionmate.servers` as specified in the properties file
* Optional: Update `miso.bugUrl` to the URL for your internal issue tracker or other
method for users to report issues using the "Report a problem" link in the header.
* Update `miso.instanceName` to update the instance name displayed in the header.


Download some supporting JARs:

    cd $CATALINA_HOME/lib
    curl -O https://search.maven.org/remotecontent?filepath=mysql/mysql-connector-java/5.1.10/mysql-connector-java-5.1.10.jar
    curl -O https://artifacts.oicr.on.ca/artifactory/gsi-dependencies/uk/ac/ebi/fgpt/jndi-file-factory/1.0/jndi-file-factory-1.0.jar

Append the following line to `$CATALINA_HOME/bin/setenv.sh` or, if you installed Tomcat through apt, `/etc/default/tomcat8`:

    JAVA_OPTS="$JAVA_OPTS -Dsecurity.method=jdbc -Xmx768M"

(Update the security method if you are using LDAP or Active Directory LDAP.)

Create the directory `/storage/miso` and subdirectories `/storage/miso/log` and `/storage/miso/files`.

    mkdir -p /storage/miso/log
    mkdir -p /storage/miso/files

Ensure that the user that Tomcat runs as has write permission to the storage directory. For example, if the user is in the 'tomcat' group:

    chgrp -R tomcat /storage/miso/

Move the following configuration files from `miso-lims/miso-web/src/main/resources` into
the `/storage/miso/` directory:

| File                      | Purpose                                                    |
|---------------------------|------------------------------------------------------------|
| `security.properties`     | properties to set the security environment (see below).    |
| `submission.properties`   | properties to set the submission environment.              |

## Security Environment (updating `/storage/miso/security.properties`)
MISO can use either LDAP (`ldap`), Active Directory LDAP (`ad`), or JDBC
(`jdbc`) as an authentication mechanism. This is set by the `-Dsecurity.method`
noted in the previous section, and the same value must be set in `security.properties`.

If you are using JDBC (aka storing usernames and passwords in the database), set the
security method to `jdbc`.
No additional configuration is necessary.

For using LDAP, set the security method to `ldap`. Additional settings are
needed for LDAP in the `security.properties`. Talk to your LDAP administrator.

To use Active Directory, a specific kind of LDAP, set the security method to `ad`.
Some active directory settings are needed in addition to the LDAP settings in the
`security.properties` file.

The search for a user is done against `userPrincipalName` which takes the form of
an email address. To login the user will type their username and to do the lookup
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

## Naming Schemes

(updating `$CATALINA_HOME/conf/Catalina/localhost/miso.properties`)
MISO Naming Schemes are used to validate and generate entity String fields. They are
used for all `name` fields, and some `alias` fields. You may configure a base naming
scheme, and customize it by switching validators and generators in `miso.properties` in
`$CATALINA_HOME/conf/Catalina/localhost/`.

The options for `miso.naming.scheme` are `default`, `oicr`, and `v2`, which have the
default configurations shown below. Only `default` supports plain sample mode. For detailed
sample mode, `v2` is recommended.

|                             | default                      | oicr                          | v2                   |
|-----------------------------|------------------------------|-------------------------------|----------------------|
| Name generator              | DefaultNameGenerator         | DefaultNameGenerator          | DefaultNameGenerator |
| Name Validator              | DefaultNameValidator         | DefaultNameValidator          | DefaultNameValidator |
| Sample Alias Generator      | none                         | OicrSampleAliasGenerator      | V2SampleAliasGenerator |
| Sample Alias Validator      | DefaultSampleAliasValidator  | OicrSampleAliasValidator      | V2SampleAliasValidator |
| Library Alias Generator     | DefaultLibraryAliasGenerator | OicrLibraryAliasGenerator     | V2LibraryAliasGenerator |
| Library Alias Validator     | DefaultLibraryAliasValidator | OicrLibraryAliasValidator     | V2LibraryAliasValidator |
| Library Aliquot Alias Generator | DefaultLibraryAliquotAliasGenerator | OicrLibraryAliquotAliasGenerator | V2LibraryAliquotAliasGenerator |
| Library Aliquot Alias Validator | DefaultLibraryAliquotAliasValidator | OicrLibraryAliasValidator | V2LibraryAliquotAliasValidator |
| Project ShortName Validator | AllowAnythingValidator       | OicrProjectShortNameValidator | OicrProjectShortNameValidator |
| Configurable components     | all                          | none                          | none |

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
| v2      | PROJ_0001_02_SG03_04  | for use with DetailedSample only |

### `miso.naming.generator.library.alias`

| Option  | Example                  | Note                                                                                                     |
|---------|--------------------------|----------------------------------------------------------------------------------------------------------|
| default | XX_LYY-1                 | XX and YY taken from sample alias - depends on sample alias passing default validator with default regex |
| oicr    | PROJ_0001_Ad_P_PE_300_WG | For use with DetailedSample only. Depends on sample alias passing oicr validator                         |
| v2      | PROJ_0001_02_LB05        | For use with DetailedSample only. Depends on tissue alias passing v2 validator                           |

### `miso.naming.generator.libraryaliquot.alias`

| Option  | Example                  | Note                                                                             |
|---------|--------------------------|----------------------------------------------------------------------------------|
| default | XX_LYY-1                 | This generator just copies the library's alias                                   |
| oicr    | PROJ_0001_Ad_P_PE_300_WG | For use with DetailedSample only. Depends on sample alias passing oicr validator |
| v2      | PROJ_0001_02_LB05-06     | For use with DetailedSample only. Depends on library alias passing v2 validator  |

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
| v2       | Matches 'v2' generator                         | no         | no               | no           | no                 |

### `miso.naming.validator.library.alias`

| Option   | Detail                                 | Allow null | Allow duplicates | Custom Regex | Custom Duplication |
|----------|----------------------------------------|------------|------------------|--------------|--------------------|
| default  | Matches 'default' generator            | no         | no               | yes          | no                 |
| allowany | Only checks that the alias is not null | no         | yes              | no           | no                 |
| oicr     | Matches 'oicr' generator               | no         | no               | no           | no                 |
| v2       | Matches 'v2' generator                 | no         | no               | no           | no                 |

### `miso.naming.validator.libraryaliquot.alias`

| Option   | Detail                                 | Allow null | Allow duplicates | Custom Regex | Custom Duplication |
|----------|----------------------------------------|------------|------------------|--------------|--------------------|
| default  | Matches 'default' generator            | no         | yes              | yes          | yes                |
| allowany | Only checks that the alias is not null | no         | yes              | no           | no                 |
| oicr     | Matches 'oicr' generator               | no         | no               | no           | no                 |
| v2       | Matches 'v2' generator                 | no         | no               | no           | no                 |

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

### Secondary Naming Scheme

It is possible to configure a second naming scheme using the same properties above, except replacing `naming` with `naming2`,
e.g. `miso.naming2.scheme=v2`. When two naming schemes are configured, an extra field called "Use Secondary Naming Scheme"
is shown on the Edit Project page. If unselected, the primary naming scheme will be used for the project; if selected, the
secondary scheme is used instead. The secondary scheme will only ever be used for items that are tied to a single project. This
includes samples, libraries, and library aliquots. Other items, such as pools, boxes, and studies will always use the primary
naming scheme. When using two naming schemes, it is ideal to use the same name generator (`nameable.name`) in both in order
to maintain consistent names throughout MISO.

# Setting Up the Run Scanner
[Run Scanner](https://github.com/miso-lims/runscanner) is a webservice that scans the paths containing
sequencer output. It is not required for a functioning MISO install, but
without it, sequencer runs must be added manually.

Please see the Run Scanner readme for setup instructions.

Once complete, edit `$CATALINA_HOME/conf/Catalina/localhost/miso.properties` of the MISO Tomcat server and set `miso.runscanner.urls` to the URL of the Run Scanner instance. It is possible to set up multiple run scanners managing different sequencers and add all the URLs to `miso.properties`. If you are adding Run Scanner to a previously-established MISO environment, restart MISO.


# Building the Application

`cd` into `$MISO_SRC`.
Build the application using:

    mvn clean package

There will be an important build artefact: `miso-web/target/ROOT.war`

# Releasing and Upgrading

Prior to release, ensure that you have followed the instructions in the
above and have WAR files for both MISO (`ROOT.war`) and, if desired, [Run Scanner](https://github.com/miso-lims/runscanner)(`runscanner-*.war`).

To install or upgrade, perform the following steps:

1. Backup your existing database.
1. Stop MISO's Tomcat.
1. Remove `$CATALINA_HOME/webapps/ROOT` directory and `$CATALINA_HOME/webapps/ROOT.war` file.
1. Copy the `ROOT.war` from the build to `$CATALINA_HOME/webapps`.
1. Make any necessary configuration changes to `$CATALINA_HOME/conf/Catalina/localhost/miso.properties`.
1. Migrate the database to the newest version. (Described below.)
1. If also releasing Run Scanner, deploy it:
    1. Stop Run Scanner's Tomcat.
    1. Deploy the Run Scanner WAR.
    1. Restart Run Scanner's Tomcat.
1. Restart MISO's Tomcat.

## Migrating the database

Updating the database (or setting it up initially) will apply patches to the database using Flyway using the `ROOT.war`.
The same path should be used for `MISO_FILES_DIR` as is set for `miso.fileStorageDirectory` in `miso.properties`
(`/storage/miso/files/` by default)

    cd ${FLYWAY}
    rm -f jars/sqlstore-*.jar
    unzip -xjo $CATALINA_HOME/webapps/ROOT.war 'WEB-INF/lib/sqlstore-*.jar' -d jars
    ./flyway -user=$MISO_DB_USER -password=$MISO_DB_PASS -url=$MISO_DB_URL -outOfOrder=true -locations=classpath:db/migration,classpath:uk.ac.bbsrc.tgac.miso.db.migration migrate -placeholders.filesDir=${MISO_FILES_DIR}

`$DB_URL` should be in the same format as in the `ROOT.xml`, except replacing `&amp;` with just `&`
and replacing `zeroDateTimeBehavior=convertToNull` with `zeroDateTimeBehavior=CONVERT_TO_NULL`:

```
jdbc:mysql://localhost:3306/lims?autoReconnect=true&zeroDateTimeBehavior=CONVERT_TO_NULL&useUnicode=true&characterEncoding=UTF-8
```

### If you have run into an issue with migration `V0320` with MariaDB:

This migration contains some syntax which is not compatible with MariaDB. You can skip over the `Printer` code at issue, and manually copy the remainder of the migration into the MySQL console (as seen below). The last command changes the Flyway state from failed to succeeded. You can then run Flyway again from the terminal and it will resume with the next migration.

```
CREATE TABLE PlatformSizes (
  platform_platformId bigint(20) NOT NULL,
  partitionSize int NOT NULL,
  PRIMARY KEY (platform_platformId, partitionSize),
  CONSTRAINT fk_platform_size_platform FOREIGN KEY (platform_platformId) REFERENCES Platform (platformId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO PlatformSizes(platform_platformId, partitionSize)
  SELECT DISTINCT platform, COUNT(*) AS c FROM SequencerPartitionContainer JOIN SequencerPartitionContainer_Partition ON containerId = container_containerId GROUP BY containerId
  UNION SELECT platformId, 1 FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%MiSeq%'
  UNION SELECT platformId, 2 FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%HiSeq%'
  UNION SELECT platformId, 8 FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%HiSeq%'
  UNION SELECT platformId, 4 FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%NextSeq%'
  UNION SELECT platformId, 4 FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%Genome Analyzer%'
  UNION SELECT platformId, 1 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 2 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 3 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 4 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 5 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 6 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 7 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 8 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 9 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 10 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 11 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 12 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 13 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 14 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 15 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 16 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 1 FROM Platform WHERE name = 'LS454'
  UNION SELECT platformId, 2 FROM Platform WHERE name = 'LS454'
  UNION SELECT platformId, 4 FROM Platform WHERE name = 'LS454'
  UNION SELECT platformId, 8 FROM Platform WHERE name = 'LS454'
  UNION SELECT platformId, 16 FROM Platform WHERE name = 'LS454'
  UNION SELECT platformId, 6 FROM Platform WHERE name = 'SOLID' AND instrumentModel = 'AB SOLiD 5500xl'
  UNION SELECT platformId, 1 FROM Platform WHERE name = 'SOLID' AND instrumentModel <> 'AB SOLiD 5500xl'
  UNION SELECT platformId, 2 FROM Platform WHERE name = 'SOLID' AND instrumentModel <> 'AB SOLiD 5500xl'
  UNION SELECT platformId, 4 FROM Platform WHERE name = 'SOLID' AND instrumentModel <> 'AB SOLiD 5500xl'
  UNION SELECT platformId, 8 FROM Platform WHERE name = 'SOLID' AND instrumentModel <> 'AB SOLiD 5500xl'
  UNION SELECT platformId, 16 FROM Platform WHERE name = 'SOLID' AND instrumentModel <> 'AB SOLiD 5500xl'
;

CREATE TABLE PartitionQCType (
  partitionQcTypeId bigint(20) NOT NULL AUTO_INCREMENT,
  description varchar(255) NOT NULL,
  noteRequired boolean DEFAULT false,
  PRIMARY KEY (partitionQcTypeId),
  UNIQUE KEY uk_partitionqctype_description (description)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

CREATE TABLE Run_Partition_QC (
  runId bigint(20) NOT NULL,
  partitionId bigint(20) NOT NULL,
  partitionQcTypeId bigint(20) NOT NULL,
  notes varchar(1024),
  PRIMARY KEY(runId, partitionId),
  CONSTRAINT fk_rpq_run_runId FOREIGN KEY (runId) REFERENCES Run (runId),
  CONSTRAINT fk_rpq_partition_partitionId FOREIGN KEY (partitionId) REFERENCES _Partition (partitionId),
  CONSTRAINT fk_rpq_partitiontypeqc_partitiontypeqc FOREIGN KEY (partitionQcTypeId) REFERENCES PartitionQCType (partitionQcTypeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO PartitionQCType(description, noteRequired) VALUES
  ('OK', false),
  ('OK\'d by collaborator', false),
  ('Failed: Instrument problem', false),
  ('Failed: Library preparation problem', false),
  ('Failed: Analysis problem', false),
  ('Failed: Other problem', true);
INSERT INTO PartitionQCType(description, noteRequired)
    SELECT CONCAT('Failed: ', name), true FROM QCType WHERE qcTarget = 'Run';
INSERT INTO Run_Partition_QC(runId, partitionId, partitionQcTypeId, notes)
  SELECT RunQC.run_runId, RunQC_Partition.partition_partitionId, partitionQcTypeId, information
  FROM RunQC
    JOIN RunQC_Partition ON RunQC.qcId = RunQC_Partition.runQc_runQcId
    JOIN QCType ON RunQC.qcMethod = QCType.qcTypeId
    JOIN PartitionQCType ON PartitionQCType.description = CONCAT('Failed :', QCType.name);
DROP TABLE RunQC_Partition;
DROP TABLE RunQC;
DELETE FROM QCType WHERE qcTarget = 'Run';


UPDATE flyway_schema_history SET success = 1 WHERE version = '0320';
```
### If you have run into an issue with migration `V0611`:

Ths command changes the Flyway state from failed to succeeded. You can then run Flyway again from the terminal and it will resume with the next migration.

    UPDATE flyway_schema_history SET success = 1 WHERE version = '0611';



If you encounter other errors migrating the database, make sure that you are using the recommended version of Flyway (see
[Prerequisites](#prerequisites)).
