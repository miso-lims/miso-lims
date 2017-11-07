# Pinery-MISO

This Pinery LIMS implementation reads data from MISO by connecting directly to the MISO database.
Since this is a read-only service, the database user only requires read access.

## Configuration

The [properties file](src/main/resources/miso.properties) contains configuration options for
connecting to the MISO database - host, port, (database) name, username, and password. All are
required.

**WARNING**: Be sure not to commit your database credentials if you modify the included properties
file.

## Installation

For installation instructions, see the [Pinery Webservice](https://github.com/oicr-gsi/pinery)
documentation.
