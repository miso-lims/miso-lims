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

To use it, install required dependencies:

1. [Install Docker 18.06.0+](https://docs.docker.com/install/)
1. If necessary, [install Docker Compose](https://docs.docker.com/compose/install/)

There are two available modes for MISO to run in: plain sample mode, which was 
originally used by Earlham Institute, and detailed sample mode, used by Ontario
Institute for Cancer Research. 

**Plain sample mode** has a straightforward Sample -> Library -> Dilution -> 
Pool workflow and is sufficient for basic laboratory tracking for sequencing.

Launch the plain sample demo with docker-compose:

```bash
# download the docker-compose file
wget https://raw.githubusercontent.com/miso-lims/miso-lims/master/docker-compose.yml -O docker-compose.yml
wget https://raw.githubusercontent.com/miso-lims/miso-lims/master/docker-compose.override.yml -O docker-compose.override.yml

# set the password for the database
echo "changeme" > ./.miso_db_password
# set required environment variables
export MISO_DB_USER=tgaclims MISO_DB=lims MISO_DB_PASSWORD_FILE=./.miso_db_password MISO_TAG=latest
docker-compose up
```

Navigate to [http://localhost:8090](http://localhost:8090) to login to miso with
the credentials **admin/admin**.

**Detailed sample mode** has all of the features of plain sample mode, plus it
allows users to build a hierarchy of Samples (e.g. Identity -> Tissue -> Slide 
-> gDNA (stock) -> gDNA (aliquot) and also includes alias autogeneration.

Launch the detailed sample demo with docker-compose:

```bash
# download the docker-compose file
wget https://raw.githubusercontent.com/miso-lims/miso-lims/master/docker-compose.yml -O docker-compose.yml
wget https://raw.githubusercontent.com/miso-lims/miso-lims/master/docker-compose.override.yml -O docker-compose.override.yml
wget https://raw.githubusercontent.com/miso-lims/miso-lims/master/docker-compose.detailed.yml -O docker-compose.detailed.yml

# set the password for the database
echo "changeme" > ./.miso_db_password
# set required environment variables
export MISO_DB_USER=tgaclims MISO_DB=lims MISO_DB_PASSWORD_FILE=./.miso_db_password MISO_TAG=latest
docker-compose -f docker-compose.yml -f docker-compose.override.yml -f docker-compose.detailed.yml up
```

Navigate to [http://localhost:8090](http://localhost:8090) to login to miso with
the credentials **admin/admin**.

The docker-compose.override.yml file creates Docker volumes for the MISO DB 
(miso_db) and files uploaded into MISO (miso_files) and these will persist 
across multiple startups until the Docker environment is pruned. As such, it is 
intended as a demonstration and __not a permanent installation__. Please see
[miso-lims-webapp](https://cloud.docker.com/u/misolims/repository/docker/misolims/miso-lims-webapp)
on DockerHub for more information on configuring the containers and the compose files.


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

Installation and configuration details can be found in the [MISO installation guide](docs/_posts/2016-01-11-installation-guide.md).

## Contact and Community

- [MISO Twitter](https://twitter.com/misolims) : for news and updates
- [MISO Gitter](https://gitter.im/TGAC/miso-lims) : for user group and developer questions and discussion
- [Slack Group](https://miso-lims.slack.com/) : for developers
