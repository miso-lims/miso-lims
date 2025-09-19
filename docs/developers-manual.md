# Developer's Manual

This manual is intended to be an overview of all MISO's major development areas. If you feel that
anything is missing, please[let us know](https://github.com/miso-lims/miso-lims/issues).

Before developing MISO, the best first step is to set up your development environment and get MISO
running. You can do that by following the
[Bare Metal Install Guide](https://miso-lims.readthedocs.io/projects/docs/en/latest/admin/baremetal-installation-guide/).

## Module Overview

MISO is divided into several modules, some of which are separate applications or libraries which may also be used in external
applications.

|Module|Subdirectory|Description|
|------|------------|-----------|
|MISO Core|core|the heart of MISO which models the domain of NGS metadata and underpins the other modules|
|miso-dto|miso-dto|data transfer objects primarily to be JSON-serialized in the REST APIs|
|MISO Integration Tools|integration-tools|miscellaneous utilities for working with external libraries/software/APIs|
|MISO MVC|miso-web|web front-end, including Spring MVC and REST Controllers, JSPs, and Javascript|
|miso-service|miso-service|service layer containing business logic between the persistence layer and front-end|
|MISO SQL Store|sqlstore|persistence layer|
|pinery-miso|pinery-miso|Implementation of [Pinery](https://github.com/oicr-gsi/pinery) REST API for serving MISO LIMS data to other applications|

## Core

Data model classes make up the bulk of the core module and are detailed more below. Other things
in the core module include

- Naming schemes
  - Also detailed below
- Interfaces for the service layer
  - This is so that other modules don't need to depend on the service module, which contains the implementations
  - Detailed more in the [Service Layer](#service-layer) section
- Some miscellaneous management/integration interfaces
- Utility classes and interfaces

### Domain Model

#### Core Behavioral Interfaces

|Interface|Description|
|-------|-----------|
|[Aliasable](https://github.com/miso-lims/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Aliasable.java)|Defines objects that have a human-readable identifying 'alias' field|
|[Barcodable](https://github.com/miso-lims/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Barcodable.java)|Defines whether an implementing object is able to be identified by a barcode string. This interface also defines a label text property which can be used to provide abstraction of a number of member properties into a printable string.|
|[Boxable](https://github.com/miso-lims/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Boxable.java)|Defines whether an implementing object can be stored in a [Box](https://github.com/miso-lims/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Box.java)|
|[ChangeLoggable](https://github.com/miso-lims/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/ChangeLoggable.java)|Defines objects that have change logs written to the database|
|[Deletable](https://github.com/miso-lims/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Deletable.java)|Defines objects that are deletable via standard interfaces and tracked as deletions in the Deletion Log. Not all items that can be deleted should implement this interface - only those that should have their deletion logged.|
|[Identifiable](https://github.com/miso-lims/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Identifiable.java)|Defines objects which have an unique ID field. This ID is used as the database primary key|
|[Nameable](https://github.com/miso-lims/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Nameable.java)|Defines whether an implementing object is able to be identified by a unique long and named by a string. This name may or may not be unique depending on the given [NamingScheme](https://github.com/miso-lims/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/NamingScheme.java) applied (see [Naming Schemes](#naming-schemes)). This interface is heavily used in MISO for many persistable objects.|

#### Core Models

The core models define the types of data that can be stored in MISO. MISO contains many model
classes, but some of the main ones are listed below as examples.

|Class|Description|
|------|-----------|
|Project|a collection of studies, samples, and libraries|
|Sample|the physical material upon which sample preparation, QC and library preparations are carried out|
|Library|the first step in constructing sequenceable material from a Sample|
|LibraryAliquot|portion of a library, possibly diluted, ready to be added to a pool|
|Pool|contains one or more library aliquots that are ready to be sequenced|
|SequencerPartitionContainer|the physical unit that holds pools during sequencing (Flowcell/Slide)|
|Run|a sequencer run|
|Instrument|a sequencer or other lab instrument|
|Box|storage box which holds sample/library/library aliquot/pool tubes|

Some model classes have interfaces with separate implementation classes. When creating new models,
we don't typically bother with the interface unless it's necessary or helpful for some reason.

The model implementations contain JPA/Hibernate annotations describing how to map between the
database and Java model. Each class should include at least the `@Entity` annotation at class level
to mark it as a persisted class, and the `@Id` annotation to mark the primary key. Other things such
as joins and columns names can also be defined as necessary using annotations.

### Naming Schemes

All [Nameable](https://github.com/miso-lims/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Nameable.java)
entities in MISO should conform to a Naming Scheme. This ensures consistency of human-readable names
across entity space, and allows centralised validation. Besides the name field, there are a few
others that are covered by the naming scheme, including sample, library, and library aliquot
aliases, and project codes.

#### Interfaces

|Interface|Description|
|---------|-----------|
|NameGenerator<T>|provides automatic naming for a specific object field|
|NameValidator|ensures correctness of a specific object field, whether generated or entered manually|
|NamingScheme|coordinates usage of a collection of generators and validators|
|NamingSchemeResolverService|resolves naming schemes, generators, and validators given a configured property value. The default implementation simply uses static String mappings|

## DTOs

The DTOs (Data Transfer Objects) module contains alternate versions of the core model classes that
are used mainly in MISO's REST APIs. These are simplified, lightweight models that only contain the
data required by the front-end or client.

The DTO classes should be kept very simple - usually just a set of fields with getters and setters.
Jackson annotations may be used if necessary to control serialization. Some DTO classes contain
methods to convert between the entity model and DTO, while others have this conversion defined in
`Dtos.java`.

## SQLStore

The sqlstore module contains all of the classes that make up MISO's persistence layer as well as the
database setup scripts.

### Database Setup

The MISO database is created and updated using [Flyway](https://flywaydb.org/) migrations. For
development purposes, you do not need to install Flyway, and can instead use the Maven Flyway
plugin that is already configured for MISO.

1. Use git to check out the branch you'd like to build
2. Build the entire MISO project

        mvn clean install

3. You should already have a database and user from your initial MISO setup. If not, create them
now. See the
[Bare Metal Install Guide](https://miso-lims.readthedocs.io/projects/docs/en/latest/admin/baremetal-installation-guide/).
4. Configure the Maven Flyway plugin to access your database. This is done by creating the file:
`miso-lims/sqlstore/flyway.properties`. Replace the port, username, password, and database name
below if necessary. This file is gitignored, so you don't have to worry about your credentials
ending up on Github.

        flyway.user=dbadmin
        flyway.password=dbadmin
        flyway.url=jdbc:mysql://localhost:3306/lims?autoReconnect=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&sslMode=DISABLED&connectionTimeZone=SERVER&cacheDefaultTimeZone=false&useSSL=false
        flyway.placeholders.filesDir=/var/miso/files/

5. If you have an existing database that you'd like to clear and rebuild, run Flyway Clean to wipe it out

        mvn -pl sqlstore flyway:clean

6. Finally, run the migrations

        mvn -pl sqlstore flyway:migrate

### Adding New Database Migrations

There are a few things to keep in mind when adding Flyway migrations

* Migrations are used to modify the schema and for the most part should not contain data. Some
  schema changes may require changes to existing data to conform to the new model, however. When new
  features are added, default values may be added via the migration as well.
* SQL Migrations are located in `miso-lims/sqlstore/src/main/resources/db/migration`
* Migrations are named in the format `V####__description.sql` where `####` is the version number and
  `description` is the name of the migration.
* New migrations should be added with a version in the 8000-8999 range. When we do a release,
  these development migrations are compacted into a release migration in the normal range.
* All SQL must be valid in MySQL 8.0. The user running the migration should have full permissions on
  the database, so you can assume this is the case.
* Triggers, stored procedures, and views should be created either in a beforeMigrate or afterMigrate
  script. These can be found in the `migration_beforeMigrate` and `migration_afterMigrate`
  subdirectories respectively. These scripts are run during every Flyway Migrate run, which means the
  triggers, procedures, and views are recreated each time in-case of schema changes. This means that
  the `CREATE` statements must be preceded by `DROP` statements, or something similar.

### Persistence Layer

Hibernate is used for Object-Relational Mapping (ORM). JPA/Hibernate annotations are used in the
entity model classes to define columns, joins, constraints, and other database info. The Hibernate
Metamodel Generator is used to build static metamodel classes that can be used to make JPA Criteria
queries completely typesafe. These should used whenever possible instead of referring to fields
using Strings. The metamodel class is built into the same package as the entity class, with a "_"
appended to the class name. e.g. the metamodel for `uk.ac.bbsrc.tgac.miso.core.data.Index` will be
`uk.ac.bbsrc.tgac.miso.core.data.Index_`.

The Data Access Objects (DAOs) found in the sqlstore module are only responsible for doing simple
reads and writes as dictated by the Service layer. DAOs should only be consumed by the Service
layer, and other components should use the service layer in order to read and write data.

There are a few useful interfaces and abstract classes used to simplify a lot of the DAO
implementations:

- `ProviderDao`: Defines standard get and list methods
- `SaveDao`: Extends `ProviderDao` and defines standard create and update methods
- `HibernateProviderDao`: Base Hibernate implementation of ProviderDao. Also includes some useful
  helper methods for creating common types of queries, such as `getBy(property, value)`
- `HibernateSavoDao`: Extends `HibernateProviderDao`. Base Hibernate implementation of SaveDao. Most
  DAO classes should extend this.
- `JpaCriteriaPaginatedDataSource`: Should be implemented by any DAO that is expected to have a lot
  of data that we would want to paginate for performance reasons, and data for which we want to
  enable advanced search. Base Hibernate/JPA implementation of
  - `PaginatedDataSource`, which defines how data is retrieved with pagination for use in the
    front-end tables.
  - `PaginationFilterSink`, which controls the different types of search that can be done using
    those same tables.

There is also a `QueryBuilder` class that can be used to help construct JPA queries.

All DAO implementations should include the following two class-level annotations:

- `@Repository`: Informs Spring of the role this class serves and allows it to be wired into the
  system automatically
- `@Transactional(rollbackFor = Exception.class)`: Ensures that operations aren't left
  half-completed if an exception occurs mid-way through


## Service Layer

The Service layer, found in the miso-service module, contains all the business logic involved in
correctly storing and retrieving things from the database. To ensure data consistency, all database
access in MISO should be done through a Service class rather than using the DAOs directly. Beyond
data storage and retrieval via the DAO's, the Service layer's responsibilities include

- Authorization checks using an `AuthorizationManager`
- Name generation using a `NamingScheme`
- Validation, providing clear error messages back to the client
- Ensuring that only fields which should be modifiable can be modified
- Updating timestamps and user ID fields such as `creationDate` and `lastModifier`
- Writing changelogs, for cases where that can't be done using database triggers

The service layer interfaces are all included in the core module, while the implementations are in
the service module. This makes it so that other modules don't need to depend on the service module,
which prevents them from indirectly depending on the sqlstore module too, which is good because only
the service implementations should be accessing the DAOs.

Frequently used interfaces and abstract classes in the service layer:

- `ProviderService`: Defines a standard `get` method
- `SaveService`: Extends `ProviderService` and defines standard `create` and `update` methods
- `BulkSaveService`: Extend `SaveService` and supports asynchronous bulk create and update
  operations
- `DeleterService`: Supports deleting `Deletable` entities
- `PaginatedDataSource`: Should be implemented by any DAO that is expected to have a lot of data
  that we would want to paginate for performance reasons, and data for which we want to enable
  advanced search
- `AbstractSaveService`: Base implementation of `SaveService` that standardizes the save process and
  offers overrideable protected methods to customize authentication, validation, and other aspects
  of saving

All service implementations should include the following two class-level annotations:

- `@Service`: Informs Spring of the role this class serves and allows it to be wired into the system
  automatically
- `@Transactional(rollbackFor = Exception.class)`: Ensures that operations aren't left
  half-completed if an exception occurs mid-way through, even if the operation involves multiple
  DAOs

## Web Application

The main MISO web application is powered by the [Spring framework](https://spring.io/). This allows
powerful webapp configuration and tailoring via Spring XML, Java configuration, and annotations.
Here, we will go through the MISO elements that comprise the web application layer.

### Configuration

Most of MISO now uses Java configuration, but there is some Spring XML configuration remaining in
the [WEB-INF directory](https://github.com/miso-lims/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/).

A few important Spring annotations that are used throughout MISO:

- `@Bean`: Adds an object to the Spring context
- `@Component`: Marks a class for auto-detection by Spring, also adding it to the context.
  Specializations of this annotation include `@Controller`, `@Service`, `@Repository`, and others
- `@Autowired`: Use on a field or method parameter to automatically populate with a bean from the
  Spring context
- `@Value`: Populate the value of a field from the `miso.properties` file or other system properties

### MVC Controllers

Controller classes define endpoints for receiving and responding to requests. The MVC controllers in
the `uk.ac.bbsrc.tgac.miso.webapp.controller.view` package are responsible for processing input/
parameters, fetching any necessary data from the service layer, populating it into the page model,
and usually returning a `ModelAndView` containing the model and a reference to the appropriate JSP.
The JSP and model are combined to render the page that is sent back to the client.

All MVC controllers must be annotated with `@Controller` at the class level so that Spring wires
them up correctly. Most should also have a `@RequestMapping` annotation at class level to specify
the base path for all endpoints within that controller. For example, the `EditSampleController` has
`@RequestMapping("/sample")` at class level, which means all endpoints within the controller will be
relative to that. Endpoint methods should use a more specific mapping annotation, such as
`@GetMapping` or `@PostMapping`.

When defining these mapped endpoint methods, there are several objects that you can add as method
parameters to have Spring automatically wire them in for you. The one that is most frequently used
in MVC controllers is a `ModelMap` which allows you to add data into the page model, as mentioned
above. There are a few annotations you can use to get other data in the method parameters as well:

- `@PathVariable`: take a value from the mapped URL. In a method mapped with
  `@GetMapping(value = "/{sampleId}")`, you can get the sample ID that was specified in the URL by
  adding a `@PathVariable long sampleId` parameter to the method.
- `@RequestParam`: take a value from the request parameters

#### JSP

JSP files use a combination of HTML and JSP-specific tags to lay out a page. Model attributes that
have been injected from the controller may be accessed within `${}` or used within JSP tag
properties. In addition to the standard tag libraries, MISO provides some of its own tags to reduce
the boilerplate needed for common things such as creating lists and common tables. These definitions
can be found in `uk.ac.bbsrc.tgac.miso.webapp.util.form`.

#### JavaScript

MISO has a lot of JavaScript, which can be divided into the 5 categories detailed below. MISO's
JavaScript must conform to ECMAScript 5 currently, though that is something we should update at
some point! jQuery is also used.

##### Utilities

There are some important utility scripts that are used throughout the other scripts:

- `0-urls.js`: If you need to use a URL in JavaScript, it should be defined here in order to
  minimize duplication and make it easy to find usage and change URLs in the future
- `lims.js`: Contains all sorts of utility functions for UI, browser navigation, sorting, dialogs,
  working with arrays, validation, and many other miscellaneous things
- `list.js`: Utilities for building lists. Detailed more below
- `form.js`: Utilities for building forms. Detailed more below
- `bulk.js`: Utilities for building bulk tables. Detailed more below

##### List Target Definitions

Lists in MISO include all of the list pages that show all of the items of a particular type within
MISO, and lists within form pages, which show all of the items related to the main object of the
page. [list.js](https://github.com/miso-lims/miso-lims/blob/develop/miso-web/src/main/webapp/scripts/list.js)
contains utilities for building these lists, as well as some documentation on the `ListTarget`
structure.

List target definitions describe how a particular list is built, including the columns, sorting,
action buttons, and more. Each type of list has its own target definition in a file named
`list_x.js`, where `x` is the type name. e.g. `list_sample.js` defines sample lists.

##### Form Definitions

Form pages in MISO are used for editing individual items. The top part of the page is typically a
list of fields, which is the form part.
[form.js](https://github.com/miso-lims/miso-lims/blob/develop/miso-web/src/main/webapp/scripts/form.js)
contains utilities for building these forms, as well as documentation on the `FormTarget` structure.

Form target definitions describe how a particular form is built and how it behaves. They are created
in files named `form_x.js`, where `x` is the type name. e.g. `form_project.js` defines the project
form.

##### Bulk Table Definitions

Bulk tables are the spreadsheet-like tables within MISO that allow creating and editing many items
at once. [bulk.js](https://github.com/miso-lims/miso-lims/blob/develop/miso-web/src/main/webapp/scripts/bulk.js)
contains utilities for building these tables, as well as documentation on the `BulkTarget`
structure.

Bulk target definitions describe how a particular bulk table is built and how it behaves. They are
created in files named `bulk_x.js`, where `x` is the type name. e.g. `bulk_library.js` defines the
bulk Create/Edit Libraries table.

##### Misc AJAX

Other miscellaneous JavaScript relating to a particular object type goes into a script called
`x_ajax.js`, where `x` is the type name. e.g. `box_ajax.js` contains miscellaneous JavaScript for
the box pages and working with boxes.

### REST API

MISO's internal REST API is used to support AJAX on the front-end, and is not intended to be used by
other services/clients/users.

Because the model classes may be very complex and sometimes contain a deep graph of nested objects,
they are converted to simpler Data Transfer Objects (DTOs) before serializion to JSON (See the
[DTOs section](#dtos)).

REST API endpoints are handled by controllers in the `uk.ac.bbsrc.tgac.miso.webapp.controller.rest`
package. The difference between these and the MVC controllers is that they respond with DTOs that
are serialized (via Jackson) to JSON, instead of a model and view that are rendered for the client.

All REST controllers should extend `AbstractRestController`, which adds appropriate exception
handling. They may either include the `@RestController` annotation at class level, or use
`@Controller`, but if using `@Controller`, every endpoint method that responds with JSON must also
be annotated with `@ResponseBody` on the method or return type to indicate that it should be
serialized. Classes annotated with `@RestController` will do this automatically.

The annotations mentioned above for MVC controllers are all useful for REST controllers too. Another
useful annotation is `@RequestBody`, which can be added to a method parameter to use the value from
the body of the request.

## Pinery-MISO

This is MISO's implementation of [Pinery](https://github.com/oicr-gsi/pinery), a read-only REST API
for accessing LIMS data. `MisoClient.java` contains most of the implementation. Database queries are
stored in separate scripts in the `resources` directory.

## Documentation

The `docs` directory contains the user manual, admin guides, and this developer guide, all of which
is hosted on [Read the Docs](https://about.readthedocs.com/). It is mostly written in markdown. The
presence of the documentation within the MISO repository means that documentation updates should
accompany any related code changes.

## Testing

### Unit Tests

Tests are run through jUnit 4 via Maven. It is configured to run any classes with names ending in
"Test" in the test sources. Consider adding unit tests for any new code.

### DAO Testing

Integration tests are set up for the data access layer using Docker and Flyway to build a real MySQL
database. The data access layer should have full test coverage.

#### Test Data

The following scripts are run automatically to reset the test data before each test. Any initial
data is first cleared from the database using the script
[V9000__clear_data.sql](https://github.com/miso-lims/miso-lims/blob/develop/sqlstore/src/it/resources/db/migration/V9000__clear_data.sql),
and then the test data is populated from
[V9010__test_data.sql](https://github.com/miso-lims/miso-lims/blob/develop/sqlstore/src/it/resources/db/migration/V9010__test_data.sql).


#### Tests

DAO test classes should all extend `AbstractDAOTest`. The abstract class includes the annotations
necessary to make the test database available. It also makes your tests transactional, so any
database changes made in a test case are rolled back before the next test case runs. In your test
class, you can use methods from the abstract class to access the `EntityManager`, and pass that to
your DAO to make it access the test database. You can access the `Session` if you need to pull any
data from the database without using a DAO. You can also create mocks for the DAO's other
dependencies using Mockito.

If the class you're testing extends `HibernateSaveDao`, extending `AbstractHibernateSaveDaoTest`
simplifies testing all the standard methods. If the class extends `PaginatedDataSource`, you should
create a second test class that extends `PaginationFilterSinkIT` specifically for testing all
possible search methods.

After building the full MISO project, the database integration tests can be run with:

    mvn clean verify -pl sqlstore -DskipITs=false

### UI Integration Testing

#### Test Data

The following scripts are run automatically to reset the test data before each test. First, existing
data is cleared using [clear_test_data.sql](https://github.com/miso-lims/miso-lims/blob/develop/miso-web/src/it/resources/db/migration/clear_test_data.sql).
Data is then populated from [integration_test_data.sql](https://github.com/miso-lims/miso-lims/blob/develop/miso-web/src/it/resources/db/migration/integration_test_data.sql).
There are a set of tests specifically for plain sample mode ([PlainSampleITs.java](https://github.com/miso-lims/miso-lims/blob/develop/miso-web/src/it/java/uk/ac/bbsrc/tgac/miso/webapp/integrationtest/PlainSampleITs.java)) that use [plainSample_integration_test_data.sql](https://github.com/miso-lims/miso-lims/blob/develop/miso-web/src/it/resources/db/migration/plainSample_integration_test_data.sql) instead.

#### Tests

UI integration tests should all extend `AbstractIT` for similar reasons as the DAO tests listed
above. The UI tests run Selenium against a Tomcat instance using MySQL in Docker. After building the
full MISO project, UI integration tests can be run using:

    mvn clean verify -pl miso-web -DskipITs=false

To run a specific IT test class, use:

    mvn clean verify -pl miso-web -DskipITs=false -Dit.test=NameOfITTestClass

To run the plain sample mode tests, use:

    mvn clean verify -pl miso-web -DrunPlainITs

To spin up the test environment for manual testing, use:

    mvn clean verify -pl miso-web -DskipITs=false -DcargoInitGoal=run

This will cause Tomcat to start but will not run any tests. You can access this Tomcat at
`http://localhost:$PORT`, where $PORT is the port listed in the console output once the Tomcat has
finished starting up. As Tomcat is not running tests, it will have to be killed with `Ctrl-C` and
the MySQL Docker container will have to be manually cleaned up.

You can also populate the test data into the test environment using Docker:

    sudo docker exec -i ${CONTAINER_ID} sh -c 'exec mysql -uroot -pabc123 -D lims' \
    < ./src/it/resources/db/migration/clear_test_data.sql;
    sudo docker exec -i ${CONTAINER_ID} sh -c 'exec mysql -uroot -pabc123 -D lims' \
    < ./src/it/resources/db/migration/integration_test_data.sql

Make sure to refresh constants manually after doing this by logging in as admin, and clicking that
option on the My Account page.

### Spring Controller Testing

#### Test Data

The Spring controller tests use the same `clear_test_data.sql` and `integration_test_data.sql`
files as the UI integration tests (see above) to automatically reset the test data before each test.

#### Tests

Spring controller tests should all extend `AbstractST`. The abstract class contains all the
annotations needed to set up the test context/config/resources, as well as the mock user to run the
tests. It also includes several template tests for common endpoints, for both the rest controllers
and the model/view controllers. The javadocs in `AbstractST.java` have more specific information on
these.

#### Running the Tests

After building the full MISO project, the Spring controller tests can be run with:

```
mvn clean verify -pl miso-web -DskipSTs=false
```

To run a specific test, add `-Dit.test=<test_name>`. You can run multiple by delimiting the names
with commas (e.g. `-Dit.test=test1,test2...`).

The tests use the Docker Maven Plugin to run a MySQL container and create a new database each time
you run them, which is time consuming. If you are working on a lot of tests or going to be running
them a lot, you can save significant time by setting up a local database to use instead. You'll
need to run Flyway migrate on your database before running the tests. Here's an example of how you
can do that using Docker:

```
# Create Docker container
docker run --name=<test-db-name> -e MYSQL_ROOT_PASSWORD=<root_password> -e MYSQL_USER=<username> -e MYSQL_PASSWORD=<password> -e MYSQL_DATABASE=<test_db_name> -p <port>:3306 -d mysql:8.0

# Run Flyway migrate
mvn flyway:migrate -pl sqlstore -Dflyway.url=jdbc:mysql://localhost:<port>/<test_db_name> -Dflyway.driver=com.mysql.cj.jdbc.Driver -Dflyway.user=root -Dflyway.password=<root_password>

# Run the tests
mvn test-compile failsafe:integration-test -pl miso-web -Dmiso.it.mysql.url=jdbc:mysql://localhost:<port>/<test_db_name> -Dmiso.it.mysql.user=<user> -Dmiso.it.mysql.pw=<password> -DskipSTs=false
```

#### Debugging

To debug your controller test, set `-Dst.debug=true` on the command line. This will enable printing
of the response JSON for all template tests used. To look at this printed response, navigate to
`{miso_base_directory}/miso-web/target/failsafe-reports`, then open `*.SomeControllerST-output.txt`
to view the printed response from the request.

#### Code Coverage Plugin

The Spring controller tests use the Jacoco plugin to test endpoint coverage. This plugin is
disabled by default. To run the code coverage plugin, run

```
mvn clean verify -pl miso-web -DskipSTs=false -Djacoco.skip=false
```

The default target output directory for the coverage report is `/miso-web/target/site/`. To view
the report, open the `index.html` file in the `site` folder in a browser. For VSCode, the "live
server" plugin is quite useful for this, allowing you to right click and open the `index.html` with
the live server plugin.
