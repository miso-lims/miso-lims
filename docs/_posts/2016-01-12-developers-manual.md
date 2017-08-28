---
layout: page
title: "Developer's Manual"
category: dev
date: 2016-01-12 13:38:53
---

This manual is intended to be an overview of all MISO's major development areas. If you feel that anything is missing, please
[let us know](https://github.com/TGAC/miso-lims/issues).

# Module Overview

MISO is divided into several modules, some of which are separate applications or libraries which may also be used in external
applications.

|Module|Subdirectory|Description|
|------|------------|-----------|
|migration|migration|a separate application which can be extended to facilitate migration of data from a different LIMS|
|MISO Core|core|the heart of MISO which models the domain of NGS metadata and underpins the other modules|
|miso-dto|miso-dto|data transfer objects primarily to be JSON-serialized in the REST API|
|miso-external-web|miso-external-web|a limited-feature version of MISO for use by external collaborators. **WARNING: not currently maintained**|
|MISO Integration Tools|integration-tools|miscellaneous utilities for working with external libraries|
|MISO MVC|miso-web|web front-end, including Spring MVC and REST Controllers, JSPs, and Javascript|
|MISO Run Scanner|runscanner|a separate server application that scans sequencer run directories. Integrated with MISO to automate entry of run data|
|MISO Run Tools|run-tools|miscellaneous utitilies for working with sequencer runs|
|miso-service|miso-service|service layer containing business logic between the persistence layer and front-end|
|MISO Spring IoC|spring|additional AJAX utilities, slowly being phased out in preference to the REST API|
|MISO SQL Store|sqlstore|persistence layer|

# Core

## Domain Model

### Core Behavioral Interfaces

|Interface|Description|
|-------|-----------|
|[Barcodable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Barcodable.java)|Defines whether an implementing object is able to be identified by a barcode string. This interface also defines a label text property which can be used to provide abstraction of a number of member properties into a printable string.|
|[Boxable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Boxable.java)|Defines whether an implementing object can be stored in a [Box](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Box.java)|
|[ChangeLoggable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/ChangeLoggable.java)|Defines objects that have change logs written to the database|
|[Deletable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Deletable.java)|Defines whether an implementing object is deletable by the system. The isDeletable() method defines a contract for ascertaining whether the object has any dependencies that prevent it from being removed, e.g. child members.|
|[Locatable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Locatable.java)|Defines whether an implementing object is able to be located by a barcode string, e.g. a freezer shelf barcode.|
|[Nameable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Nameable.java)|Defines whether an implementing object is able to be identified by a unique long and named by a string. This name may or may not be unique depending on the given [MisoNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/MisoNamingScheme.java) applied (see [Naming Schemes](#DeveloperManual-NamingSchemes)). This interface is heavily used in MISO for all persistable objects.|
|[Securable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/com/eaglegenomics/simlims/core/Securable.java)|Defines whether an object can be read from or written to, given a User.|
|[SecurableByProfile](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/security/SecurableByProfile.java)|An extension of Securable, defines objects that are Secured via [SecurityProfile](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/com/eaglegenomics/simlims/core/SecurityProfile.java)|
|[Watchable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Watchable.java)|Defines whether an implementing object can be assigned watchers that will receive alerts upon the occurrence of defined events.|

### Core Model Interfaces

These interfaces represent the objects that store state inherent to the MISO model, and are a superset of the
[EBI SRA domain model schema](http://www.ebi.ac.uk/ena/about/sra_format). This means that as object fields are inputted by
technicians/auxiliary tools using MISO, the submission schema for the SRA is being populated behind-the-scenes. Decorators are then used
to wrap up the synonymous objects so that SRA XMLs can be generated (see [ENA Decorators](#DeveloperManual-ENADecorators)).

|Object|Description|
|------|-----------|
|Project|a collection of studies, samples, and libraries|
|Study|represents more fine-grained information about the sequencing Project|
|Sample|the physical material received upon which sample preparation, QC and library preparations are carried out|
|Library|the first step in constructing sequenceable material from a Sample|
|LibraryDilution|diluted portion of a library ready to be added to a pool|
|Pool|contains one or more Dilutions that are ready to be sequenced|
|Order|a request for sequencing a specific pool using specific run parameters|
|SequencerPartitionContainer|the physical unit that holds pools during sequencing (Flowcell/Slide)|
|Run|a sequencer run|
|SequencerReference|a hardware sequencer|
|Box|storage box which holds barcoded sample/library/dilution/pool tubes|

### Core Security Interfaces

MISO objects are secured based on a SecurityProfile associated with that object. SecurityProfiles control access based on a single
overarching owner, which always has full access (read/write/delete). Users can then be registered with that object's SecurityProfile that
enable them to read or write information. Similarly, read and write access can be specified at the Group level. The _allowAllInternal_ flag
states that any User with the ROLE_INTERNAL role (see LINK User Roles) is able to read and write to this object, regardless of the Group
that the User is a part of.

## Enumerated Types

MISO has two categories of enumerated types: those that are actual Java enums, and those that are database-defined.

### Enums

These concrete enums are intended to provide collections of relatively static instances of descriptive definitions.

* HealthType
* IlluminaChemistry
* KitType
* MisoAuthority
* PlatformType
* ProgressType
* StrStatus
* SubmissionActionType

### Database definition types

Unlike their enum conterparts, these type definitions are instances of database entities, and as such are user-editable from within the
MISO interface. The reason for this is because these types follow the enumerations specified in the
[Experiment SRA common schema](ftp://ftp.sra.ebi.ac.uk/meta/xsd/sra_1_5/SRA.experiment.xsd), and are more liable to change.

* LibrarySelectionType
* LibraryStrategyType
* LibraryType
* QcType

## Naming Schemes

All [Nameable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Nameable.java) entities
in MISO should conform to a Naming Scheme. This ensures consistency of human-readable names across entity space, and allows centralised
validation with no requirement of extra code (backend or frontend) on an external developer's part. Sample and Library has an alias
attribute which are also handled by naming scheme.

### Interfaces

|Interface|Description|
|---------|-----------|
|NameGenerator<T>|provides automatic naming for a specific object field|
|NameValidator|ensures correctness of a specific object field, whether generated or entered manually|
|NamingScheme|coordinates usage of a collection of generators and validators|
|NamingSchemeResolverService|resolves naming schemes, generators, and validators given a configured property value. The default implementation simply uses static String mappings|

## Managers

### Files

API access to the underlying filesystem is made available through implementors of the
[FilesManager](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/manager/FilesManager.java)
interface. This interface defines a contract to, based on a properties-supplied base directory (see
[Web Application Configuration](#DeveloperManual-Configuration) and
[Installation Readme](https://documentation.tgac.ac.uk/pages/viewpage.action?pageId=950282#Installation&AdministrationManual-Settingupmiso.propertiesfile)),
generate temporary files, store files and retrieve files from disk, and list files within a given storage directory. The default file
storage path is:

```
/storage/miso/files
```

It is very important that all the underlying directories exist and are writable by the user that runs the MISO instance, but at any rate
MISO will attempt to check and create these paths if not.

This manager provides a mechanism to standardise file output into specific directories based on Java object types (simple class names,
lowercased) and qualifiers. These qualifiers are a simple string which can be kept constant by the implementor for a given field, e.g.
object entity ID. So, for example, sample delivery forms can be generated and stored under a Project type and qualifier (see LINK Sample
Delivery Forms). The code to do this looks something like:


```java
File f = misoFileManager.getNewFile(
           Project.class,
           projectId.toString(),
           "SampleDeliveryForm-" + LimsUtils.getCurrentDateAsString() + ".odt");
```

A project with ID 1 and stored in the default file storage directory on the 31st May 2013 will result in the following path structure:

```
/storage/miso/files/project/1/SampleDeliveryForm-20130531.odt
```

MISO also obfuscates the actual filename and path within the web application user interfaces by using the file object's hashcode.

### IssueTrackers

API access to any registered Issue trackers, e.g. JIRA, RT, Redmine, Mantis, is made available through implementors of the
[IssueTrackerManager](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/manager/IssueTrackerManager.java)
interface. The IssueTrackerManager interface has the @Spi annotation, allowing any custom managers to be automatically resolved at runtime
by using the @ServiceProvider annotation on any concrete classes. The interface itself is very simple, with only three methods to
override: **getType()** which represents the underlying issue tracker enum, e.g. "JIRA", **getBaseTrackerUrl()** which represents the REST
API base URL of the tracker service, and **getIssue(String issueKey)** which actually does the work of grabbing the issue and representing
it as a JSONObject.

Issue tracker managers allow integration with external issue trackers, removing the need to context switch between MISO and said tracker
by a user. An example of this feature is in the Project page, where one or more issue IDs can be supplied which will import the issue
details from the tracker's API. Currently the only default supported implementation is JIRA, as provided by the
[JiraIssueManager](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/java/uk/ac/bbsrc/tgac/miso/webapp/service/integration/jira/JiraIssueManager.java)
class.

### Submissions

API access to the submission workflow is made available through implementors of the
[SubmissionManager](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/manager/SubmissionManager.java)
interface.

# Database and Schema

The MISO database is created and updated using [Flyway](https://flywaydb.org/) migrations.

## Creating a New Database

1. First, check out the branch you'd like to build the DB for via Github
    * If you want the OICR base data to be included, this should be oicr/oicr, or another branch derived from it
    * If you just want the base MISO data, this should be tgac/develop, or another branch derived from it
2. Next, build the entire MISO project

        mvn clean install -P external

3. If you don't yet have a MySQL Database, create one. Also create a user with write access. e.g.

        CREATE DATABASE miso;
        CREATE USER misouser@localhost IDENTIFIED BY 'password';
        GRANT ALL ON miso.* TO misouser@localhost;

4. Configure Flyway in MISO. This is done by creating the file: `miso-lims/sqlstore/flyway.properties`:

        flyway.user=misouser
        flyway.password=password
        flyway.url=jdbc:mysql://localhost:3306/miso
        
    This file is gitignored, so you don't have to worry about your credentials ending up on Github
5. If you have an existing database that you'd like to clear and rebuild, run Flyway Clean to wipe it out

        cd sqlstore
        mvn flyway:clean

6. Finally, run the migrations

        cd sqlstore
        mvn flyway:migrate

## Updating an Existing Database

1. Check out and build MISO as described in steps 1-2 of Creating a New Database (above)
2. Run Flyway Migrate. Only new migrations will be run

        cd sqlstore
        mvn flyway:migrate

## Adding New Database Migrations

There are a few things to keep in mind when adding Flyway migrations

* SQL Migrations are located in `miso-lims/sqlstore/src/main/resources/db/migration`
* All **schema** updates should be added to the tgac/develop branch
* institute-specific **data** (not schema) should be added in an institute-specific fork/branch
* There should be no institute-specific schema
* **Schema** changes must be done using SQL that is valid in both MySQL and the MySQL mode of H2. Refer to the
  [H2 documentation](http://www.h2database.com/html/grammar.html), as it does not include 100% of the MySQL syntax. Some of the minor
  differences are automatically translated to work with H2 during test build. This is done by a Groovy script run via Maven
* Most **data** changes should be between `-- StartNoTest` and `-- EndNoTest` comments to ensure that they don't affect unit or
  integration tests. This also means that data changes do not have to be compatible with H2
* **Schema** changes should never be excluded from tests
* Triggers, stored procedures, and views should be created either in a beforeMigrate or afterMigrate script. These can be found in the
  `migration_beforeMigrate` and `migration_afterMigrate` subdirectories respectively. These scripts are run during every Flyway Migrate
  run, which means the triggers, procedures and views are recreated each time in-case of schema changes. This means that the `CREATE`
  statements must be preceded by `DROP` statements, or something similar
* **Data** additions (Tissue Origins, Kits, etc.) should usually be in an afterMigrate script

# Persistence Layer

Hibernate is used for Object-Relational Mapping (ORM). JPA/Hibernate annotations are used in the model classes to define columns, joins,
constraints, and other database info. The Data Access Objects (DAOs) found in the sqlstore module are then only responsible for doing
simple reads and writes as (usually) dictated by the Service layer. DAOs should only be consumed by the Service layer.

# Service Layer

The Service layer, found in the miso-service layer, contains all the business logic involved in correctly storing and retrieving things
from the database. To ensure data consistency, all database access in MISO should be done through a Service class rather than using the
DAOs directly. Beyond data storage and retrieval via the DAO's, the Service layer's responsibilities include

* Authorization checks using an AuthorizationManager
* Name generation via using a NamingScheme
* Validation in cases where database constraints are not strong enough
* Updating timestamps and userId fields such as creationDate and lastModifier
* Ensuring that only fields which should be modifiable can be modified

# Web Application

The main MISO web application is powered by the [Spring framework](http://www.springsource.org/), notably
[Spring MVC](http://static.springsource.org/spring/docs/4.3.x/spring-framework-reference/html/mvc.html). This allows powerful webapp
configuration and tailoring via Spring XML and annotations, making functionality like the REST API a breeze. Here, we will go through the
MISO elements that comprise the web application layer.

## Configuration

A great deal of MISO can be configured at the Spring XML level, making it easy for developers to swap out existing MISO implementations for their own, via [Dependency Injection](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/overview.html#overview-dependency-injection). MISO uses the usual web.xml to define properties relevant to the webapp container, and a number of Spring configuration XML files for the core application itself:

|Configuration file|Description|
|------------------|-----------|
|[web.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/web.xml)|Configures the web application with respect to the web container, e.g. Tomcat. Allows mapping of URLs to DispatcherServlets, and the inclusion of any relevant filters or logging framework configuration property files|
|[applicationContext.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/applicationContext.xml)|Configures the central application miso.properties location and pulls in the configuration files below|
|[miso-servlet.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/miso-servlet.xml)|Defines low-level MISO webapp-centric elements. Very little configuration should go in here|
|[miso-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/miso-config.xml)|High-level user-space MISO bean configuration|
|[db-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/resources/sql/db-config.xml)|Configures access to the underlying datasource|
|[jdbc-security-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/jdbc-security-config.xml) / [ldap-security-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/ldap-security-config.xml)|Database and LDAP specific configuration, respectively. JDBC configuration is the initial default, and is the simplest mechanism to get started. If you would like more fine-grained access to a directory-style authentication and role assignment mechanism, then LDAP support is also available|
|[integration-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/integration-config.xml)|Configures elements in the integration layer, e.g. analysis server|

## REST API

The REST API is used primarily to support AJAX on the front-end. Because the model classes may be very complex and sometimes contain a
deep graph of nested objects, they are converted to simpler Data Transfer Objects (DTO) before serializion to JSON.

### Request Signing

Signing requests requires 3 elements:

* REST API URL of the service you wish to request (see [REST API]({{ site.baseurl }}{% post_url 2016-01-12-rest-api %}) )
* Your MISO username
* Your MISO API key - this can be found by logging in to MISO as normal, clicking "My Account" and the key is in the top box

Producing HMAC keys from these elements for your request is easy:

```
echo -n "<REST-url>?x-url=<REST-url>@x-user=<miso_user_name>" | openssl sha1 -binary -hmac "<your_key_from_miso>" | openssl base64 | tr -d = | tr +/ -_
```

You can then use curl to initiate the request, using the REST API URL, your username, and the signed fragment produced above:

```
curl --request GET 'http://<miso_url>/<REST-url>'--header 'x-user:<miso_user_name>'--header 'x-signature:<hmac_string_from_above>'--header 'x-url:<REST-url>'
```

Putting the two together, here's an example shell script that can grab a list of libraries associated with a project:

```
#!/bin/bash

PROJECTID=$1
USER=$2
KEY=$3

SIGNATURE=`echo -n "/miso/rest/project/$PROJECTID/libraries?x-url=/miso/rest/project/$PROJECTID/libraries@x-user=$USER" | openssl sha1 -binary -hmac "$KEY" | openssl base64 | tr -d = | tr +/ -_`

curl --request GET "http://your.miso.url/miso/rest/project/$PROJECTID/libraries" --header "x-user:$USER" --header "x-signature:$SIGNATURE" --header "x-url:/miso/rest/project/$PROJECTID/libraries"
```

# Run Scanner
(TODO)

# Testing

## Unit Tests

Tests are run through jUnit 4 via Maven. It is configured to run any classes with names ending in "Test" in the test sources. Generally,
all new code should be unit tested. The Maven Cobertura plugin can be used to generate a coverage report. SonarQube also analyses test
coverage.

## DAO Testing

### Test Database

A test database is created in memory for DAO testing. It is an H2 database running in MySQL mode. The database understands most MySQL
syntax, but is not perfect. A Groovy script is used to copy the production schemas and make a few changes to them so that H2 will be happy.
Flyway is used to migrate the test schemas into the test database.

**WARNING**: Eclipse will show a lifecycle mapping error in sqlstore/pom.xml. This is because the groovy-maven-plugin isn't compatible with Eclipse's
m2e (Maven) plugin. The tests can still be run in Eclipse, but you must first do a `mvn clean install` outside of Eclipse, so that Maven
will run the schema translator.

### Test Data

Test data is populated via Flyway from the script
[V9000__miso_test_data.test.sql](https://github.com/TGAC/miso-lims/blob/develop/sqlstore/src/test/resources/db/test_migration/V9000__miso_test_data.test.sql)

Flyway is configured to look for any files in the migration directory ending with .test.sql. They must include version numbers higher than
the production schema versions so that they are run after the tables are created.

### Tests

DAO test classes should all extend `AbstractDAOTest`. The abstract class includes the annotations necessary to make the test database
available. It also makes your tests transactional, so any database changes made in a test case are rolled back before the next test case
runs. In your test class, you can autowire the SessionFactory and JdbcTemplate, and pass those to your DAO to make it access the test
database. You can also create mocks for the DAO's other dependencies using Mockito:

```
public class SQLRunDAOTest extends AbstractDAOTest {
 
  @Autowired
  private JdbcTemplate jdbcTemplate;
 
  @Mock private Store<SecurityProfile> securityProfileDAO;
 
  @InjectMocks private SQLRunDAO dao;
 
  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
  }
 
}
```

## UI Integration Testing
(TODO)
