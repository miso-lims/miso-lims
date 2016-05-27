# MISO Migration Utility

Migrates data from a source to a target.

## Targets

The only target currently available is the default. This is a standard MISO target with some limitations:

* OICR naming schemes are used
* IdentificationBarcodes are not auto-generated
* JDBC authentication is used

These may be made configurable in the future.

The properties file (see below for source-specific property files) includes database connection settings, 
and a MISO user to whom object creation should be attributed.

## Sources

### Load Generator

Used to generate bulk data for load testing. Configuration includes settings for the number of projects, 
samples, libraries, pools, and runs to create, as well as some required information about some entities 
which must already exist in the migration target. See the 
[example properties file](src/main/resources/load-generator.properties) for configuration details.

## Building

From the migration directory, using Maven 3:

```
mvn clean install
```

## Running

From the migration directory after building:

```
java -jar target/migration-{version}-jar-with-dependencies.jar {properties-file}
```

Replace `{version}` with the MISO version. Replace `{properties-file}` with the path to the properties file 
containing configuration for the migration you wish to perform