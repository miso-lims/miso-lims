[![Build Status](https://travis-ci.org/TGAC/miso-lims.svg?branch=develop)](https://travis-ci.org/TGAC/miso-lims) [![Quality Gate](https://sonarqube.com/api/badges/gate?key=uk.ac.bbsrc.tgac.miso:miso:sonarqube)](https://sonarqube.com/dashboard?id=uk.ac.bbsrc.tgac.miso%3Amiso%3Asonarqube) [![DOI](https://zenodo.org/badge/4726428.svg)](https://zenodo.org/badge/latestdoi/4726428)


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

### Virtual Box

An older version of MISO is available in our our virtual machine image with
everything preinstalled. A VirtualBox instance is available with a
self-contained MISO installation and MySQL database server, fully configured and
can be used out-of-the-box.

To use it:

1.  Download VirtualBox software from:
[https://www.virtualbox.org/](https://www.virtualbox.org/) 
1.  Download the MISO OVA:
[https://repos.tgac.ac.uk/miso/latest/MISO_Image.ova](https://repos.tgac.ac.uk/miso/latest/MISO_Image.ova)
1.  Import this OVA file into VirtualBox via _File_ → _Import Appliance_. You
may have to select _Bridged Adaptor_ instead of _NAT_ in the network settings.
1.  Start the instance. Starting the appliance will result in an Ubuntu virtual
machine complete with all MISO’s prerequisites pre-installed. The login for the
virtual image is `miso` / `misoadmin`.
1.  Start MISO server by using `./misoStart.sh` and then open the browser and go http://localhost:8080/
1.  Log in to MISO using `admin` / `admin`. Only some simple configuration for
certain elements is required, such as _Sequencer References_, Printers and any
additional users and groups.

## User Tutorial

There is a [tutorial available](https://oicr-gsi.github.io/miso-docs-oicr/plain-index)
for introducing new users to MISO's functionality. Some of the resources (MISO URL,
ways of contacting the MISO administrators) are specific to Ontario Institute for
Cancer Research, but this can be changed by forking and configuring the
[tutorial repository](https://github.com/oicr-gsi/miso-docs-oicr).

## Running an Instance of MISO 

To run your own MISO instance in the long term, download the 
[latest release](https://github.com/TGAC/miso-lims/releases/latest).

Installation and configuration details can be found in the [MISO installation guide](docs/_posts/2016-01-11-installation-guide.md).

## Contact and Community

- [MISO Twitter](https://twitter.com/misolims)
- [Slack Group](https://miso-lims.slack.com/)
