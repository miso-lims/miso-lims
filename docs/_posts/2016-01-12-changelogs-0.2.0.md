---
layout: page
title: "Version 0.2.0"
category: post
date: 2016-01-12 13:32:52
---


## 0.2.0

Build

* Added onejar-maven-plugin pluginRepository declaration to the analysis-server pom. Should build now
* Ensured MisoPropertyExporter sets the System miso.baseDirectory property to use in single config directory path setting.
* Implemented changes required to support a single miso.baseDirectory configuration that is then used in relevant other configurations, e.g. ehcache diskstore path, logging directories, etc
* Enforced the UTF-8 build encoding property for all compilation profiles. This should hopefully fix some of the regex compilation wierdness with non-English alphabets (thanks Sven!)

MySQL Fix

* Fixed a conflict problem of the table name "Partition" due to the latest MySQL v5.6 introduced it as a preserved variable

User Management

* Fixed a problem with request mapping for handling post request for creating user groups

JavaDoc

* Added javadoc stylesheet for Java 7 compatibility. Fix pom.xml to include this.
* Added javadoc plugin to build section, not just reporting.

Notification

* Rapid-run mode HiSeq notification was not picking up correct run status thanks to log file modifications. Thanks Illumina.
* Fix to stop PacBio run save duplication
* Notification system can now track certain failed Illumina runs, i.e. when RTA is terminated. HiSeq 2500 rapid mode lane count now supported (2 instead of 8 lanes).
* Added HiSeq 2500 support

Barcode priting

* Fix for printer admin page so that Add Printer Service button is visible when no printers are already available.
* Added check when clicking Add Printer Service multiple times

Frontend

* Listing tables are now in datatable format. Deprecating tablesorter eventually.

* MISO-6 Added Popup error when adding pool to run when no study exists

Caching

* Fixed intermittent NPE issue when retrieving disk cached objects and calling equals (getId() would be null). Changed domain objects ID field to be long primitive instead of wrapper.

Analysis

* Analysis page now tracks failed jobs separately to pending tasks.

REST API

* JIRA REST API integration upped to v2

* MISO-9: Fixes to the REST API so that sample and library types don't recurse around each other resulting in very long redundant JSON output

Logging

* Changes to logging so that debug logs will rotate into logs/miso_debug.log

Plate

* Added 96 well sample exporting functionality

