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
[Docker](https://www.docker.com/). Images of the most recent MISO releases are
available on Docker Hub in
[misolims/miso-lims](https://hub.docker.com/r/misolims/miso-lims/). The Docker
container is intended as a demonstration and not a permanent installation. Please 
note that shutting down the Docker container deletes all MISO data, and 
restarting the Docker container starts up with a fresh MISO. If you wish to 
store your data longer-term when testing out MISO, please follow the instructions 
for _Running an Instance of MISO_ below.

To use it:

1. [Install Docker 1.9.1+](https://www.docker.com/products/docker) 
1. ```docker pull misolims/miso-lims``` 
1. ```docker run -p 8090:8080 -d -t misolims/miso-lims```

Navigate to [http://localhost:8090](http://localhost:8090) to login to miso with
the credentials **admin/admin**.

Please add a sequencing _Instrument_ before you attempt to create libraries, as 
libraries can only be created for platforms with active (non-retired) instruments.

## User Tutorial

There is a [tutorial available](https://oicr-gsi.github.io/miso-docs-oicr/plain-index)
for introducing new users to MISO's functionality. Some of the resources (MISO URL,
ways of contacting the MISO administrators) can be changed by forking and configuring
the [tutorial repository](https://github.com/oicr-gsi/miso-docs-oicr) to suit your
lab's specific needs.

## Running an Instance of MISO 

To run your own MISO instance in the long term, download the 
[latest release](https://github.com/TGAC/miso-lims/releases/latest).

Installation and configuration details can be found in the [MISO installation guide](docs/_posts/2016-01-11-installation-guide.md).

## Contact and Community

- [MISO Twitter](https://twitter.com/misolims) : for news and updates
- [MISO Gitter](https://gitter.im/TGAC/miso-lims) : for user group and developer questions and discussion
- [Slack Group](https://miso-lims.slack.com/) : for developers
