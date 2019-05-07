[![Build Status](https://travis-ci.org/TGAC/miso-lims.svg?branch=develop)](https://travis-ci.org/TGAC/miso-lims) [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=uk.ac.bbsrc.tgac.miso:miso&metric=alert_status)](https://sonarcloud.io/dashboard?id=uk.ac.bbsrc.tgac.miso:miso) [![DOI](https://zenodo.org/badge/4726428.svg)](https://zenodo.org/badge/latestdoi/4726428) [![Gitter](https://badges.gitter.im/TGAC/miso-lims.svg)](https://gitter.im/TGAC/miso-lims?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)


# MISO: An open source LIMS for small-to-large scale sequencing centres

&copy; 2017. [Earlham Institute](http://earlham.ac.uk/), Norwich, UK, [Ontario Institute for Cancer Research](http://oicr.on.ca), Toronto, Canada

> MISO project contacts: [Robert Davey](robert.davey@earlham.ac.uk), [Morgan Taschuk](morgan.taschuk@oicr.on.ca)
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


## Trying MISO

### Docker

The simplest way to get MISO up and running quickly is to use
[Docker](https://www.docker.com/) compose. Images of the most recent MISO releases are
available on Docker Hub in
the [misolims](https://hub.docker.com/r/misolims/) organisation.

#### Prerequisites

Install required dependencies:

1. [Install Docker 18.06.0+](https://docs.docker.com/install/)
1. If necessary, [install Docker Compose](https://docs.docker.com/compose/install/)


Download and extract the `.docker` directory from Github into `miso-lims-compose`.

```
wget https://github.com/miso-lims/miso-lims/archive/master.zip
unzip master.zip 'miso-lims-master/.docker/*'
mv miso-lims-master/.docker miso-lims-compose
rm -r master.zip miso-lims-master/
```

You are now ready to run MISO.

#### Quick Start

To bring up a demo environment, install the pre-requisites above and run the
following commands.

**Plain sample mode** has a straightforward Sample -> Library -> Dilution ->
Pool workflow and is sufficient for basic laboratory tracking for sequencing.

Launch the plain sample demo with docker-compose:
``` bash
cd miso-lims-compose
export MISO_DB_USER=tgaclims && export MISO_DB=lims && export MISO_DB_PASSWORD_FILE=./.miso_db_password && MISO_TAG=latest
echo "changeme" > ./.miso_db_password
docker-compose -f demo.plain.yml up
```

**Detailed sample mode** has all of the features of plain sample mode, plus it
allows users to build a hierarchy of Samples (e.g. Identity -> Tissue -> Slide
-> gDNA (stock) -> gDNA (aliquot) and also includes alias autogeneration.

Launch the detailed sample demo with docker-compose:

```bash
cd miso-lims-compose
export MISO_DB_USER=tgaclims && export MISO_DB=lims && export MISO_DB_PASSWORD_FILE=./.miso_db_password && MISO_TAG=latest
echo "changeme" > ./.miso_db_password
docker-compose -f demo.detailed.yml up
```

For both environments, navigate to [http://localhost/miso](http://localhost/miso)
and use the credentials **admin**/**admin**.

Once you are finished with the container, make sure to run
`docker-compose -f <compose.yml> down`, where `<compose.yml>` is either
`demo.plain.yml` or `demo.detailed.yml`. This will clean up the instances and
networks and release their resources to the host operating system.


These compose files are intended as a demonstration and __not a permanent installation__.

Please see the [Docker Compose Guide](http://miso-lims.github.io/miso-lims/adm/compose-installation-guide) for more information on configuring the containers and the compose files.


## User Tutorial

There are tutorials available to introduce and train new users to MISO's functionality.  
* [Plain sample tutorials](https://miso-lims.github.io/walkthroughs/plain-index)
* [Detailed sample tutorials](https://miso-lims.github.io/walkthroughs)

Some of the resources (MISO URL,
ways of contacting the MISO administrators) can be changed by forking and configuring
the [tutorial repository](https://github.com/miso-lims/walkthroughs) to suit your
lab's specific needs.

## Running an Instance of MISO

To run your own MISO instance in the long term, download the
[latest release](https://github.com/miso-lims/miso-lims/releases/latest).

Installation and configuration details can be found in the [MISO installation guide](http://miso-lims.github.io/miso-lims/adm/installation-guide.html).

## Contact and Community

- [MISO Twitter](https://twitter.com/misolims) : for news and updates
- [MISO Gitter](https://gitter.im/TGAC/miso-lims) : for user group and developer questions and discussion
- [Slack Group](https://miso-lims.slack.com/) : for developers
