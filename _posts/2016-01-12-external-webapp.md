---
layout: page
title: "Configuring the external webapp"
category: adm
date: 2016-01-12 13:51:46
---

The external MISO web application can give project- and run-level read-only access to collaborators who need to access this data from outside the institute. It is identical to the information that external users see when they log into the root MISO web application, but it could be run on a separate server that links to an internally-hosted MISO LIMS, without exposing that MISO instance beyond the institute's network.

# Hostname change:

Edit _src/main/java/uk/ac/tgac/bbsrc/miso/external/ajax/ExternalSectionControllerHelperService.java_

Change 2 instances of "hostname-here" to your main MISO URL (this can be a MISO instance hosted on another server).

### Build

    $ cd miso-external-web/
    $ mvn clean install

# Deploy

Deploy the _miso-external-web/target/miso-external.war_ file to the webapps/ directory in a Tomcat instance.

# Config

Add the external user to the external access list to both associated Project and Run.

# Use

Login via _http://hostname-here/**miso-external**_

Requests are processed through [REST API]({{ site.baseurl }}{% post_url 2016-01-12-rest-api %}) with JDBC authentication.

REST API endpoints called by the external webapp:

*   /miso/rest/external/projects (list of project names and aliases to which user has access)
*   /miso/rest/external/project/{projectId}
*   project overview, samples, and runs
