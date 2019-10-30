# Related Software

## Run Scanner

Run Scanner is a service that scans run directories on the file system and serves run metadata via a REST interface.
MISO features integration with Run Scanner, allowing MISO to retrieve run data from Run Scanner and automatically
create and update runs in MISO.

Documentation for Run Scanner can be found in its [GitHub repository](https://github.com/miso-lims/runscanner).

## Pinery

Pinery is a REST webservice for serving LIMS data, mainly for consumption by other applications. Pinery is used as an
abstraction layer to prevent other applications from depending on a specific LIMS' schema. This means that you can
write applications to consume Pinery data, and then if you ever want to use a different data source, such as another
LIMS, you just need a Pinery implementation for that data source and all of your existing applications will remain
compatible.

Documentation for Pinery can be found in its [GitHub repository](https://github.com/oicr-gsi/pinery). Documentation for
the MISO implementation of Pinery can be found in
[MISO's GitHub repository](https://github.com/miso-lims/miso-lims/tree/develop/pinery-miso).

