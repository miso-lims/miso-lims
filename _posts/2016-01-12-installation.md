---
layout: page
title: "Installing MISO"
category: start
date: 2016-01-12 15:56:13
order: 1 
---

## Docker

The simplest way to get MISO up and running quickly is to use [Docker](https://www.docker.com/). Images of the most recent MISO releases are available on Docker Hub in [misolims/miso-lims](https://hub.docker.com/r/misolims/miso-lims/). The Docker container is intended as a demonstration and not a permanent installation.

To use it:

1. [Install Docker 1.9.1+](https://www.docker.com/products/docker) 
1. ```docker pull misolims/miso-lims``` 
1. ```docker run -p 8090:8080 -d -t misolims/miso-lims```

Navigate to [http://localhost:8090](http://localhost:8090) to login to miso with the credentials **admin/admin**.

## Virtual Box

An older version of MISO is available in our our virtual machine image with everything preinstalled. A VirtualBox instance is available with a self-contained MISO installation and MySQL database server, fully configured and can be used out-of-the-box.

To use it:

1. Download VirtualBox software from: https://www.virtualbox.org/
1. Download the MISO OVA: https://repos.tgac.ac.uk/miso/latest/MISO_Image.ova
1. Import this OVA file into VirtualBox via File → Import Appliance. You may have to select Bridged Adaptor instead of NAT in the network settings.
1. Start the instance. Starting the appliance will result in an Ubuntu virtual machine complete with all MISO’s prerequisites pre-installed. The login for the virtual image is miso / misoadmin.
1. Start MISO server by using ./misoStart.sh and then open the browser and go http://localhost:8080/
1. Log in to MISO using admin / admin. Only some simple configuration for certain elements is required, such as Sequencer References, Printers and any additional users and groups.

## Installing from source

To run your own MISO instance in the long term, you will need to maintain your own fork of the MISO codebase with your configuration changes. For each release, you will need to merge the main changes into your local repository and then deploy a new version.

For deployment, you will need a mySQL database server, a server to scan the sequencer output directories (the notification server), and a Tomcat application server. These can be on the same machine. It is advisable to have a separate build environment.

At each upgrade, there are two steps: migrating the database and deploying a new application. Each version of the application contains all the migrations needed to upgrade any old database to match the current version. However, there is no rollback procedure, so a full database backup should be taken before migration.

Installation and configuration details can be found in the [MISO admin guide]({{ site.baseurl }}/adm/admin-manual).

