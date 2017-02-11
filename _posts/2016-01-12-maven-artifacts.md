---
layout: developers
title: "Maven artifacts"
category: dev
date: 2016-01-12 13:47:32
---



# Maven configuration

You can use the following configuration to grab any MISO artifacts from our TGAC repository:

```
<repository>
  <id>tgac-repo</id>
  <name>TGAC Maven Repository</name>
  <url>https://repos.tgac.ac.uk/maven/repo</url>
</repository>
 
<repository>
  <id>tgac-snapshots-repo</id>
  <name>TGAC Maven Snapshots Repository</name>
  <url>https://repos.tgac.ac.uk/maven/miso/snapshots</url>
</repository>
 
<repository>
  <id>tgac-releases-repo</id>
  <name>TGAC Maven Releases Repository</name>
  <url>https://repos.tgac.ac.uk/maven/miso/releases</url>
</repository>
```


# Example

To grab the 0.1.9 release MISO core module artifact, include the repository declaration above and then the following dependency:

```
<dependency>
  <groupId>uk.ac.bbsrc.tgac.miso</groupId>
  <artifactId>core</artifactId>
  <version>0.1.9</version>
</dependency>
```
