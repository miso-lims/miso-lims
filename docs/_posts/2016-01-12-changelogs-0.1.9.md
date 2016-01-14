---
layout: page
title: "Version 0.1.9"
category: post
date: 2016-01-12 13:32:52
---


##0.1.9

Project level changes

* Upped Spring version from 3.0.5 to 3.1.3
* Upped other Spring module versions

Model changes

* All model elements now have long primitive IDs
* Deprecated all specific getXId() methods for simpler getId()
* Pools can now have a PoolQC
* Pools now hold Poolable elements, rather than simply Dilutions
* Cacheable implementation objects are now Serializable so they can overflow to disk caches
* Expanded submission properties
* Improved Jackson annotations

Alerting system improvements

* No longer keeps clones of all objects, only recent ones that have actually raised alerts - improves heap usage
* EmailAlerterService fixed for to/from addressing

REST API improvements

* JSON cycles reduced with JsonIgnore and custom de/serialisation filters
* Proper REST call signing via keys

Naming scheme improvements

Plate support (beta)

* 96 and 384 well plates
* Spreadsheet-based importing (beta)
* Defined sheet to carry out bulk importing of libraries from given samples into pools and plates (if required)

Notification system

* Improved Illumina and PacBio consumers
* Added message splitting capability to all transformers for efficiency

Caches are now individually flushable, with stats

Frontend changes

* Element listing pages now use paginated datatables
* Highcharts Javascript library for better plotting in reports UI





Created by Xingdong Bian on 07 Aug, 2013
