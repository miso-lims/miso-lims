---
layout: page
title: "Developer's Manual"
category: dev
date: 2016-01-12 13:38:53
---

![](/plugins/servlet/confluence/placeholder/macro?definition=e3RvYzpvdXRsaW5lPXRydWV9&locale=en_GB&version=2)

# Introduction

This is the "Really Big Index" of all MISO's major development areas. Some things may be missing here - if so, please [contact us](http://tracker.tgac.ac.uk).

In-depth complete class information is held in the nightly [Javadoc](https://repos.tgac.bbsrc.ac.uk/miso/nightly/javadoc/site/), built from the [github develop branch](https://github.com/TGAC/miso-lims/tree/develop)[.](https://repos.tgac.bbsrc.ac.uk/miso/nightly/javadoc/)

# Core

The core module represents the heart of MISO which models the domain of NGS metadata tracking that underpins the other modules.

## Domain Model

The MISO domain model incorporates those objects which are central to describing the generic core workflow of NGS experiments.

![MISO model interfaces]({{ site.baseurl }}/images/core_model.png)

### Core Behavioral Interfaces

|Interface|Description|Extending interfaces / Implementing classes|
|-------|-----------|--------------|
|[Alertable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Alertable.java)|Defines whether an implementing object is able to have [MisoListener](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/event/listener/MisoListener.java) objects registered to it. These listeners are automatically picked up by the Alerting system and used in event propagation to produce alerts that can be tracked by the system (see [System Level Alerts](#DeveloperManual-SystemLevelAlerts)) or raised to a particular Group or User (see [User Level Alerts](#DeveloperManual-UserLevelAlerts)).|[Pool](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Pool.java), [Project](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Project.java), [Run](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Run.java), [ProjectOverview](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/impl/ProjectOverview.java)
|[Barcodable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Barcodable.java)|Defines whether an implementing object is able to be identified by a barcode string. This interface also defines a label text property which can be used to provide abstraction of a number of member properties into a printable string.|[Dilution](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Dilution.java), [Kit](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Kit.java), [Library](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Library.java), [Plate](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Plate.java), [Pool](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Pool.java), [Sample](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Sample.java), [SequencerPartitionContainer](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/SequencerPartitionContainer.java)|
|[Deletable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Deletable.java)|Defines whether an implementing object is deletable by the system. The isDeletable() method defines a contract for ascertaining whether the object has any dependencies that prevent it from being removed, e.g. child members.|[Dilution](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Dilution.java), [Experiment](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Experiment.java), [Library](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Library.java), [Plate](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Plate.java), [Pool](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Pool.java), [Project](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Project.java), [QC](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/QC.java), [Run](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Run.java), [Sample](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Sample.java), [SequencerReference](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/SequencerReference.java), [Study](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Study.java), [Alert](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/event/Alert.java), [emPCR](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/impl/emPCR.java)|
|[Locatable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Locatable.java)|Defines whether an implementing object is able to be located by a barcode string, e.g. a freezer shelf barcode.|[Kit](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Kit.java), [Library](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Library.java), [Plate](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Plate.java), [Sample](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Sample.java), [SequencerPartitionContainer](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/SequencerPartitionContainer.java)|
|[Nameable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Nameable.java)|Defines whether an implementing object is able to be identified by a unique long and named by a string. This name may or may not be unique depending on the given [MisoNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/MisoNamingScheme.java) applied (see [Naming Schemes](#DeveloperManual-NamingSchemes)). This interface is heavily used in MISO for all persistable objects.|[Barcodable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Barcodable.java), [Experiment](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Experiment.java), [HardwareReference](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/HardwareReference.java), [Plateable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Plateable.java), [Poolable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Poolable.java), [Project](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Project.java), [Run](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Run.java), [Study](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Study.java), [Submission](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Submission.java), [TagBarcode](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/TagBarcode.java)|
|[Plateable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Plateable.java)|Defines whether an implementing object can be placed in a [Plate](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Plate.java).|[Dilution](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Dilution.java), [Library](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Library.java), [Sample](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Sample.java), [_96WellPlate](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/impl/_96WellPlate.java)|
|[Poolable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Poolable.java)|Defines whether an implementing object can be placed in a [Pool](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Pool.java)[.](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Pool.java)|[Dilution](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Dilution.java), [Plate](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Plate.java), [Pool](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Pool.java), [Poolable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Poolable.java) (itself)|
|[Reportable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Reportable.java)|Defines whether an implementing object can be used as input for a given report.|[Project](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Project.java), [Run](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Run.java), [Sample](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Sample.java)|
|Securable|Defines whether an object can be read from or written to, given a User.|[SecurableByProfile](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/security/SecurableByProfile.java)|
|[Submittable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Submittable.java)|Defines whether an implementing object can be aggregated into a [Submission](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Submission.java) object.|[Experiment](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Experiment.java), [Project](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Project.java), [Run](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Run.java), [Sample](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Sample.java), [SequencerPoolPartition](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/SequencerPoolPartition.java), [Study](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Study.java), [Submission](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Submission.java)|
|[Watchable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Watchable.java)|Defines whether an implementing object can be assigned watchers that will receive alerts upon the occurrence of defined events.|[Pool](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Pool.java), [Project](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Project.java)<span>,</span> [Run](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Run.java)|


### Core Model Interfaces

These interfaces represent the objects that store state inherent to the MISO model, and are a superset of the [EBI SRA domain model schema](http://www.ebi.ac.uk/ena/about/sra_format). This means that as object fields are inputted by technicians/auxiliary tools using MISO, the submission schema for the SRA is being populated behind-the-scenes. Decorators are then used to wrap up the synonymous objects so that SRA XMLs can be generated (see [ENA Decorators](#DeveloperManual-ENADecorators)).

![MISO SRA schema model subset](/download/attachments/950284/image2013-5-29%2017%3A12%3A25.png?version=1&modificationDate=1369843955000&api=v2&effects=border-simple,blur-border "MISO Documentation > Developer Manual > image2013-5-29 17:12:25.png (MISO SRA schema model subset)")

### Core Security Interfaces

MISO objects are secured based on a SecurityProfile associated with that object:


```java
public class SecurityProfile implements Serializable {
  ...

  private User owner = null;
  private Collection<User> readUsers = new HashSet<User>();
  private Collection<User> writeUsers = new HashSet<User>();
  private Collection<Group> readGroups = new HashSet<Group>();
  private Collection<Group> writeGroups = new HashSet<Group>();
  private boolean allowAllInternal = true;

  ...
}
```

SecurityProfiles control access based on a single overarching owner, which always has full access (read/write/delete). Users can then be registered with that object's SecurityProfile that enable them to read or write information. Similarly, read and write access can be specified at the Group level. The _allowAllInternal_ flag states that any User with the ROLE_INTERNAL role (see LINK User Roles) is able to read and write to this object, regardless of the Group that the User is a part of.

## Enumerated Types

MISO has two categories of enumerated types: those that are actual Java enums, and those that are database-defined.

### Enums

These concrete enums are intended to provide collections of relatively static instances of descriptive definitions.

_384WellPlatePosition

HealthType

KitType

PlateMaterialType

PlatformType

ProgressType

SubmissionActionType

### Database definition types

Unlike their enum conterparts, these type definitions are instances of database entities, and as such are user-editable from within the MISO interface. The reason for this is because these types follow the enumerations specified in the [Experiment SRA common schema](ftp://ftp.sra.ebi.ac.uk/meta/xsd/sra_1_5/SRA.experiment.xsd), and are more liable to change.

LibrarySelectionType

LibraryStrategyType

LibraryType

QcType

## Object Factories

## Naming Schemes

All [Nameable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Nameable.java) entities in MISO should conform to a Naming Scheme. This ensures consistency of human-readable names across entity space, and allows centralised validation with no requirement of extra code (backend or frontend) on an external developer's part.

Naming schemes are resolved by the widely-used SPI/ServiceProvider strategy (see also [Naming Scheme Services](#DeveloperManual-NamingSchemeServices)), and default implementations of these schemes are named and set in the MISO webapp configuration (see [Web Application Configuration](#DeveloperManual-Configuration)). The global default naming scheme is mapped to "namingScheme", the global sample scheme to "sampleNamingScheme", and the global library scheme to "libraryNamingScheme". Any custom services wanting to replace these beans will need to be mapped to these bean IDs at runtime.

### MisoNamingSchemes

A [MisoNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/MisoNamingScheme.java) is typed by a class T that will be represented and returned by the **namingSchemeFor()** method. They also need to be uniquely named so that they can be resolved at runtime (see also [Naming Scheme Services](#DeveloperManual-NamingSchemeServices)). Fields in the typed class are mapped to validation regular expressions, and exposed via the **validateField(String field, String entityName)** method. Finally, names can be generated by mapping [NameGenerator](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/NameGenerator.java) instances (see below) to the fields from which names need to be generated, in the same fashion as the validation mapping process. A default concrete implementation, [DefaultEntityNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/DefaultEntityNamingScheme.java), is supplied that will generate and validate entity names of the regex below for any instances of the [Nameable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Nameable.java) class type:

```java
...
type = (Class<T>)Class.forName("uk.ac.bbsrc.tgac.miso.core.data.Nameable");
validationMap.put("name", Pattern.compile("([A-Z]{3})([0-9]+)"));
...
```

In this example, the [DefaultEntityNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/DefaultEntityNamingScheme.java) will use reflection to retrieve the contents of the _name_ field via the **getName()** method, as mapped by the "name" key (the "get" prefix is added by the implementation). As such, "FOO123" is a valid name, whereas "BA1" and "BAZ1FOO" are not. If you aren't worried about name collisions for certain fields (which isn't really helpful and certainly isn't recommended), then MISO supplies the [AllowAnythingEntityNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/AllowAnythingEntityNamingScheme.java) implementation which validates against the (.*) regex and generates names based on the object's simple class name and ID.

If access to the persistence store is required when validating/generating names, a dedicated interface is available that require implementations to supply a RequestManager instance, i.e. [RequestManagerAwareNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/RequestManagerAwareNamingScheme.java). This interface extends both the [MisoNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/MisoNamingScheme.java) and [RequestManagerAware](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/RequestManagerAware.java) interfaces. The two default concrete Sample and Library naming schemes supplied my MISO ([DefaultSampleNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/DefaultSampleNamingScheme.java) and [DefaultLibraryNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/DefaultLibraryNamingScheme.java), respectively) use the [RequestManagerAwareNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/RequestManagerAwareNamingScheme.java) interface as they need to check the database for any existing samples/libraries that may have the same alias field value.

### NameGenerators

A [NameGenerator](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/NameGenerator.java) interface describes classes that can generate Strings from object fields, and are registered in [MisoNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/MisoNamingScheme.java)s via the <span style="color: rgb(51,51,51);">**registerCustomNameGenerator()** method</span>. MISO doesn't supply any concrete implementations of a [NameGenerator](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/NameGenerator.java), and will only use a custom mapped generator if registered, but, if not, instead uses a basic set of prefixes of the domain model objects to construct names, e.g. Sample -> "SAM\<id\>", Library -> "LIB\<id\>", etc. The full list of default prefixes is held in the [DefaultMisoEntityPrefix](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/DefaultMisoEntityPrefix.java) enum.

## Managers

### Files

API access to the underlying filesystem is made available through implementors of the [FilesManager](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/manager/FilesManager.java) interface. This interface defines a contract to, based on a properties-supplied base directory (see [Web Application Configuration](#DeveloperManual-Configuration) and [Installation Readme](https://documentation.tgac.ac.uk/pages/viewpage.action?pageId=950282#Installation&AdministrationManual-Settingupmiso.propertiesfile)), generate temporary files, store files and retrieve files from disk, and list files within a given storage directory. The default file storage path is:

```
/storage/miso/files
```

It is very important that all the underlying directories exist and are writable by the user that runs the MISO instance, but at any rate MISO will attempt to check and create these paths if not.

This manager provides a mechanism to standardise file output into specific directories based on Java object types (simple class names, lowercased) and qualifiers. These qualifiers are a simple string which can be kept constant by the implementor for a given field, e.g object entity ID. So, for example, sample delivery forms can be generated and stored under a Project type and qualifier (see LINK Sample Delivery Forms). The code to do this looks something like:


```java
File f = misoFileManager.getNewFile(
           Project.class,
           projectId.toString(),
           "SampleDeliveryForm-" + LimsUtils.getCurrentDateAsString() + ".odt");
```

```

A project with ID 1 and stored in the default file storage directory on the 31st May 2013 will result in the following path structure:

```
/storage/miso/files/project/1/SampleDeliveryForm-20130531.odt
```

MISO also obfuscates the actual filename and path within the web application user interfaces by using the file object's hashcode.

### IssueTrackers

API access to any registered Issue trackers, e.g. JIRA, RT, Redmine, Mantis, is made available through implementors of the [IssueTrackerManager](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/manager/IssueTrackerManager.java) interface. Like a lot of other MISO services, the IssueTrackerManager interface has the @Spi annotation, allowing any custom managers to be automatically resolved at runtime by using the @ServiceProvider annotation on any concrete classes (see [Miso Service Providers](#DeveloperManual-MisoServiceProviders)). The interface itself is very simple, with only three methods to override: **getType()** which represents the underlying issue tracker enum, e.g. "JIRA", **getBaseTrackerUrl()** which represents the REST API base URL of the tracker service, and **getIssue(String issueKey)** which actually does the work of grabbing the issue and representing it as a JSONObject.

Issue tracker managers allow integration with external issue trackers, removing the need to context switch between MISO and said tracker by a user. An example of this feature is in the Project page, where one or more issue IDs can be supplied which will import the issue details from the tracker's API. Currently the only default supported implementation is JIRA, as provided by the [JiraIssueManager](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/java/uk/ac/bbsrc/tgac/miso/webapp/service/integration/jira/JiraIssueManager.java) class.

### Printers

API access to any printing devices, notably barcode printers, is made available through implementors of the [PrintManager](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/manager/PrintManager.java) interface. This interface defines the contract whereby [MisoPrintService](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/MisoPrintService.java)s and [PrintJob](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/PrintJob.java)s can be stored and retrieved, as well as an abstraction of printing content to a print service. Like a lot of other MISO services, the [MisoPrintService](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/MisoPrintService.java) interface has the @Spi annotation, allowing any custom printing services to be automatically resolved at runtime by using the @ServiceProvider annotation on any concrete classes (see [Miso Service Providers](#DeveloperManual-MisoServiceProviders)).

Because of the varied nature of what needs to be printed and to what device that content will be printed, the MISO printing structure is quite complex.

The [MisoPrintService](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/MisoPrintService.java) interface acts as the "glue" that wires every other required printing object together and as such is typed accordingly:

```java
public interface MisoPrintService<T, S, C extends PrintContext<T>> {
  ...
}
```

Various selected methods act on these specified types:

```java
  C getPrintContext();
  void setPrintContext(C pc);

  boolean print(T content) throws IOException;

  void setPrintServiceFor(Class<? extends S> c);
  Class<? extends S> getPrintServiceFor();

  public T getLabelFor(S b);
  void setBarcodableSchema(BarcodableSchema<T, S> barcodableSchema);
  BarcodableSchema getBarcodableSchema();
```

**T** here represents the type that represents the actual label object. This could be a _File_, _String_, or other custom object that the **print(T content)** method takes.

**S** here represents the domain model class type that will be used to generate the label object **T**, using the specified [BarcodableSchema](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/schema/BarcodableSchema.java), e.g. _Sample.class_

A [BarcodableSchema](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/schema/BarcodableSchema.java) represents the way that an object of the domain model class type **S**is converted into the label object **T**:

```java
@Spi
public interface BarcodableSchema<S, T> {
  Class<T> isStateFor();
  public String getRawState(T t);
  public S getPrintableLabel(T t);
  String getName();
  public BarcodeLabelFactory<S, T, BarcodableSchema<S, T>> getBarcodeLabelFactory();
}
```

A [PrintContext](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/context/PrintContext.java) defines the actual printer host (IP address, FQDN, LPR/IPP string, etc) and any access credentials required whereby content is printed to a host via the **print(T content)** method:

```java
@Spi
public interface PrintContext<T> {
  boolean print(T content) throws IOException;
  public String getName();
  public String getDescription();
  public String getHost();
}
```
The **print(T content)** method usually delegates to a [PrintStrategy](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/strategy/PrintStrategy.java), thus allowing different printing mechanisms to be used to the same [PrintContext](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/context/PrintContext.java), e.g. the same printer might accept both FTP and spooler jobs. Like a lot of other MISO services, the [PrintContext](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/context/PrintContext.java) interface has the @Spi annotation, allowing any custom printing contexts to be automatically resolved at runtime by using the @ServiceProvider annotation on any concrete classes (see [Miso Service Providers](#DeveloperManual-MisoServiceProviders)).

The [PrintStrategy](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/strategy/PrintStrategy.java) interface defines a single method, **print(T content, C pc)**, which actually initiates whatever mechanism is required to print the label content **T** to the relevant [PrintContext](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/context/PrintContext.java) **C**:


```java
public interface PrintStrategy<T, C extends PrintContext> {
  public boolean print(T content, C pc) throws IOException;
}
```

Phew! So by means of an example, let's take a MisoPrintService that takes a JSONObject object as content, produces a File that represents barcode label information, and a strategy that knows how to print this file by FTPing it to a printer (this is what actually happens for the Brady IP300/CAB Mach4 printers we have at TGAC, via the [CustomPrintService](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/CustomPrintService.java) implementation):


```java
@ServiceProvider
public class CustomPrintService implements MisoPrintService<File, JSONObject, PrintContext<File>> {
  private BarcodableSchema<File, JSONObject> barcodableSchema;
  ...
}
```

To generate a label File from the JSONObject content, the class calls on the member [BarcodableSchema](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/schema/BarcodableSchema.java):

```java
public File getLabelFor(JSONObject b) {
  return getBarcodableSchema().getPrintableLabel(b);
}
```

The schema in this case is the [BradyCustomStandardTubeBarcodeLabelSchema](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/schema/impl/BradyCustomStandardTubeBarcodeLabelSchema.java) implementation, which is the concrete class that can generate the [Brady printer's JScript format](https://www.google.co.uk/url?sa=t&rct=j&q=&esrc=s&source=web&cd=3&cad=rja&ved=0CD4QFjAC&url=http%3A%2F%2Fdomino.bradycms.com%2Fbrady%2Folrsv1r0.nsf%2F67493912eb200af0862568cb005367ff%2Ff5cdfc510e26b3f7c12571fc004626e2%2F%24FILE%2FProgman61_Brady.pdf&ei=sLCtUaOGHMm0PLnOgcAK&usg=AFQjCNFUNw75_6OiUFVvpqumm4acpZ0CBg&sig2=ZfdstELqsNIcW4Cr3OaSog) from a JSON input string:









```java
@ServiceProvider
public class BradyCustomStandardTubeBarcodeLabelSchema implements BarcodableSchema<File, JSONObject> {
  private BarcodeLabelFactory<File, JSONObject, BarcodableSchema<File, JSONObject>> barcodeLabelFactory = new FileGeneratingBarcodeLabelFactory<JSONObject>();
  ...

  @Override
  public String getRawState(JSONObject jsonObject) {
    StringBuilder sb = new StringBuilder();
    try {
      String barcodeit = jsonObject.getString("barcode");
      String field1 = jsonObject.getString("field1");
      String alias = jsonObject.getString("field2");
      String name = jsonObject.getString("field3");
      if ("yes".equals(barcodeit)) {
        String barcode = new String(Base64.encodeBase64(field1.getBytes("UTF-8")));
        sb.append("m m").append("\n");
        sb.append("J").append("\n");
        sb.append("S l1;0,0,12,15,38").append("\n");
        sb.append("B 3,2,0,DATAMATRIX,0.21;").append(barcode).append("\n");
        sb.append("B 17,1,0,DATAMATRIX+RECT,0.25;").append(barcode).append("\n");
        sb.append("T 29,2,0,5,pt4;[DATE]").append("\n");
      }
      else {
        sb.append("m m").append("\n");
        sb.append("J").append("\n");
        sb.append("S l1;0,0,12,15,38").append("\n");
        sb.append("T 17,5,0,5,pt6;").append(LimsUtils.unicodeify(field1)).append("\n");
        sb.append("T 29,2,0,5,pt4;[DATE]").append("\n");
      }
      //shorten alias to fit on label if too long
      if (alias.length() >= 17) {
        alias = alias.substring(0, 15) + "...";
      }
      sb.append("T 17,8,0,5,pt6;").append(LimsUtils.unicodeify(alias)).append("\n");
      sb.append("T 17,11,0,5,pt6;").append(LimsUtils.unicodeify(name)).append("\n");
      sb.append("A 1").append("\n");
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  ...
}
```

The JSON input content needs to be in the following structure:

```json
{
  "field1":"Foo",
  "field2":"Bar",
  "field3":"Baz",
  "barcodeit":"yes" // yes/no boolean representing whether to convert the field1 into a Base64 unicode string that will be used to generate a DataMatrix barcode image
}
```
The [BradyCustomStandardTubeBarcodeLabelSchema](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/schema/impl/BradyCustomStandardTubeBarcodeLabelSchema.java) class uses a handy reusable [BarcodeLabelFactory](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/factory/BarcodeLabelFactory.java) class, [FileGeneratingBarcodeLabelFactory](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/factory/FileGeneratingBarcodeLabelFactory.java), that knows how to convert the passed in [BarcodableSchema](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/schema/BarcodableSchema.java) into the _File_ label:

```java
@ServiceProvider
public class FileGeneratingBarcodeLabelFactory<T> implements BarcodeLabelFactory<File, T, BarcodableSchema<File, T>> {
  ...
  @Override
  public File getLabel(BarcodableSchema<File, T> s,T b) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      String labelScript = s.getRawState(b);

      File f = misoFileManager.generateTemporaryFile(user.getLoginName() + "_"+b.getClass().getSimpleName().toLowerCase()+"-", ".printjob");
      FileUtils.write(f, labelScript);
      return f;
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  ...
}
```

As you can see, the factory uses the [BarcodableSchema](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/printing/schema/BarcodableSchema.java)'s **getRawState(JSONObject json)** method (see above). The reason for this seemingly back-to-front redundant architecture is that these factories and schemas can be reused without all the encompassing printing structure, decoupling all the implementations from each other, thus giving the flexibility required to support many different printing platforms and label formats.

The above example powers the custom label printing page in the MISO user interface.

### Request Layer

API access to the core persistence layer is made available through implementors of the [RequestManager](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/manager/RequestManager.java) interface. This interface defines a great deal of behaviour that needs to be implemented so to perform all the necessary CRUD operations. As such, concrete classes of this type can be method heavy. We are hoping to rework this in future MISO versions.

MISO comes with a default implementation, [MisoRequestManager](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/manager/MisoRequestManager.java). This is a simple class that calls the underlying [Store](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/store/Store.java) implementations (see [Persistence Interfaces](#DeveloperManual-PersistenceInterfaces)) to enact the CRUD operations. This makes **unsecured** requests to the stores, i.e. anyone can access anything through this implementation. As a result, we also supply a [UserAuthMisoRequestManager](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/manager/UserAuthMisoRequestManager.java) which carries out read/write access checks against a user's credentials at each method call, thus allowing permissions-enabled abstraction over a data store.

### Submissions

API access to the submission workflow is made available through implementors of the [SubmissionManager](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/manager/SubmissionManager.java) interface, which is typed according to the input type of the submission, **I**, the submission endpoint, **O**, and the response object type from the submission endpoint, **R**.

```java
public interface  SubmissionManager<I, O, R> {
  ...
  public void setSubmissionEndPoint(O o);
  public O getSubmissionEndPoint();
  public R submit(I i) throws SubmissionException;
  public Object parseResponse(R response);

  public void setTransferMethod(TransferMethod transferMethod);
  ...
}
```
The [TransferMethod](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/submission/TransferMethod.java) interface defines just that - the way the **submit(I i)** method actually carries out any data file transfers to an [EndPoint](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/submission/EndPoint.java), which simply wraps up a local or remote destination by means of a typed URI:


```java
public interface TransferMethod {
  public UploadReport uploadSequenceData(Set<File> dataFiles, EndPoint endpoint) throws SubmissionException;
}
```

```java
public interface EndPoint<T extends URI> {
  public void setDestination(T destination);
  public T getDestination();
}
```

## Persistence Interfaces

### Stores

CRUD functionality in MISO is defined by classes adhering, at their simplest level, to the [Store](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/store/Store.java) interface:

```java
public interface Store<T> {
  public long save(T t) throws IOException;
  public T get(long id) throws IOException;
  public Collection<T> listAll() throws IOException;
  public int count() throws IOException;
}
```

You'll notice that the top-level interface doesn't support deletion out-of-the-box. This is defined by another interface, [Remover](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/store/Remover.java). The separation is useful so that a developer could provide a read-only Store implementation easily, and the interface is typed to subinterfaces of Deletable, making sure that only certain objects are eligible for deletion.


```java
public interface Remover<T extends Deletable> {
  public boolean remove(T t) throws IOException;
}
```

More interfaces are supplied which wrap up these interfaces, plus more method definitions to add more functionality depending on the object type persisted. The full list of persistence subinterfaces is [on GitHub](https://github.com/TGAC/miso-lims/tree/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/store), and implementations of these can be found in the [Data Access Objects](#DeveloperManual-DataAccessObjects(DAOs)) section.

These interfaces provide a robust API to whatever underlying persistence architecture or engine you choose to implement. MISO comes with a default MySQL schema and implementation, which will also be suitable for other open-source MySQL-like databases, such as [Percona](http://www.percona.com/software/percona-server) or [MariaDB](https://mariadb.org/). We are currently working on implementations for NoSQL databases, such as [MongoDB](http://www.mongodb.org/) and [OrientDB](http://www.orientdb.org/).

### Object-Relational Mapping (ORM)

MISO, like other database-enabled web applications, requires some kind of [mapping](http://en.wikipedia.org/wiki/Object-relational_mapping) between the database schema fields and datatypes to Java objects. There are solutions that aim to help with this potentially tricky problem, such as [Hibernate](http://www.hibernate.org/), but we chose not to use this platform, mainly because of the complexity of the relationships that MISO has to manage, but also the troubleshooting guru level required for Hibernate is, in our opinion, restrictive for most developers when picking up a pre-existing project.

Therefore, we have wired up the ORM layer for MISO manually. This is quite a painful procedure given the size of the domain model, but the bonuses are that it is immediately obvious how the relationships are formed by looking through the domain model hierarchy and applying it to the store implementations.

Another bonus is that we can achieve really fine-grained caching support at the time of row mapping code instructions, rather than post-mapping, where the cost of object retrieval has already been spent thus slowing down the process. See the following [Caching](#DeveloperManual-Caching) section for details.

## Alerting System

The MISO alerting system propagates information about events to users, based on watched entity types, and to the system, used for reporting purposes.

### Interfaces

MISO raises [Alert](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/event/Alert.java)s, based on [Event](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/event/Event.java)s, via a [ResponderService](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/event/ResponderService.java).

### Aspects

MISO uses aspect-oriented programming ([AOP](http://static.springsource.org/spring/docs/current/spring-framework-reference/htmlsingle/#aop)) to allow runtime weaving of triggered code around defined expressions. This means that there doesn't have to be concrete code-level boilerplate to wrap up other code that will be fired, given a specific event. An example of this would be logging a bit of text to a file whenever a given method is run. This **can** be achieved through attaching a concrete member to that method to log some text, but this creates a direct coupling between potentially two disparate code levels, e.g. a domain model and a logging system. Aspects removes this coupling.

### System Level Alerts

System level alerts have a user ID of 0, and are visible only by administrators.

### User Level Alerts

User level alerts are linked to a user ID, and are visible only by that user and administrators.

## Tag Barcodes

## Data Submission

### ENA Decorators

## Utilities

## Resources

# Persistence Layers

## Data Access Objects (DAOs)

Data Access Objects (DAOs) are at the heart of a persistence layer implementation. They effectively are concrete classes of [Store](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/store/Store.java) (sub-)interfaces.

## ORM Pitfalls

With MISO's manually mapped object-relational system, there are a few pitfalls to recognise as a developer.

## Cascading

ORM cascading, where an object has parent, child, or related objects that need to be persisted also, is supported in MISO by the [Persistence](http://www.oracle.com/technetwork/java/javaee/tech/persistence-jsp-140049.html) API [CascadeType](http://docs.oracle.com/javaee/7/api/javax/persistence/CascadeType.html) enum, which has the following types: ALL, PERSIST, MERGE, REMOVE, REFRESH

DAOs have their CascadeType configured via the [db-config.xml configuration file](#DeveloperManual-Databaseconfiguration). These configurations can get pretty tangled so it's best to leave the settings as is for the default MISO configuration otherwise unwanted behaviour may result.

### Child mapping cascading

When saving a parent object, sometimes it's necessary to persist any child objects too. For example, in the [SQLSampleDAO](https://github.com/TGAC/miso-lims/blob/develop/sqlstore/src/main/java/uk/ac/bbsrc/tgac/miso/sqlstore/SQLSampleDAO.java) (and most other relevant [SecurableByProfile](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/security/SecurableByProfile.java) implementors), we'd like to persist the SecurityProfile at the time of [Sample](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Sample.java) persistence:



```java
...

public long save(Sample sample) throws IOException {
  Long securityProfileId = sample.getSecurityProfile().getProfileId();
  if (this.cascadeType != null) {
    securityProfileId = securityProfileDAO.save(sample.getSecurityProfile());
  }

  ...
}

...
```

So, in any case where the CascadeType is set, the SecurityProfile associated with this Sample will also be saved.

### Parental mapping cascading

Likewise, when saving a child object, sometimes it's necessary to persist the parent object too. Using the same [SQLSampleDAO](https://github.com/TGAC/miso-lims/blob/develop/sqlstore/src/main/java/uk/ac/bbsrc/tgac/miso/sqlstore/SQLSampleDAO.java) example as above, we want to ensure the parent [Project](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Project.java) is managed correctly:









```java
...

public long save(Sample sample) throws IOException {
  ...

  if (this.cascadeType != null) {
    Project p = sample.getProject();
    if (this.cascadeType.equals(CascadeType.PERSIST)) {
      if (p!=null) {
        projectDAO.save(p);
      }
    }
    else if (this.cascadeType.equals(CascadeType.REMOVE)) {
      if (p != null) {
        DbUtils.updateCaches(cacheManager, p, Project.class);
      }
    }

    if (!sample.getNotes().isEmpty()) {
      for (Note n : sample.getNotes()) {
        noteDAO.saveSampleNote(sample, n);
      }
    }
    purgeListCache(sample);
  }

  ...
}
```



If the CascadeType is set to PERSIST, we want to call the parent [Project](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Project.java) DAO's **save(Project project)** method, passing in the current Sample's parent Project. If the CascadeType is set to REMOVE, we want to evict the parent Project from it's cache, so that stale information isn't presented to any users.

In any CascadeType scenario, we want to make sure any [Note](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Note.java)s attached to this Sample are persisted. Finally, we want to evict this Sample from the cache that holds the list of all Samples, again to make sure stale information isn't presented to any users.

### Cache eviction

As is apparent in the previous parent/child mapping cascading examples, any relevant caches should be managed so that up-to-date information is available. When saving any objects, state may change, so stale objects need to be evicted from their caches. See the [Caching](#DeveloperManual-Caching) section below.

## Caching

Considering the default SQL layer supplied with MISO, SQL queries can be very slow, especially across multiple tables. As a result, we have implemented an [EhCache](http://ehcache.org/) layer that greatly improves read times from the underlying database by storing frequently used objects in memory, and less frequently used on disk, as per a standard [LRU policy](http://en.wikipedia.org/wiki/Cache_algorithms#Least_Recently_Used). A number of caches exist for the various domain model objects and are configured according to available memory and disk space (see [EhCache Configuration](#DeveloperManual-EhCacheConfiguration)). Furthermore, the [EhCache Annotations library](http://ehcache.org/documentation/2.4/user-guide/spring#spring-25---31-ehcache-annotations-for-spring) is used to simplify the configuration of the persistence and removal policy of objects themselves (see Persisting Objects and below).

### Persisting objects

When saving objects back to the database, any cache entries need to be removed to ensure that old data is flushed.









```java
@TriggersRemove(
        cacheName = {"projectCache", "lazyProjectCache"},
        keyGenerator = @KeyGenerator(
                name = "HashCodeCacheKeyGenerator",
                properties = {
                        @Property(name = "includeMethod", value = "false"),
                        @Property(name = "includeParameterTypes", value = "false")
                }
        )
)
public long save(Project project) throws IOException {
  ...
}
```





### Retrieving objects

When retrieving objects from the data store, the ehcache-annotated **get()** and **listAll()** methods check the specified caches for collision on the supplied object. If the objects exists in the cache, the cached version is returned. If not, the method body executes:









```java
@Cacheable(cacheName = "projectCache",
           keyGenerator = @KeyGenerator(
                   name = "HashCodeCacheKeyGenerator",
                   properties = {
                           @Property(name = "includeMethod", value = "false"),
                           @Property(name = "includeParameterTypes", value = "false")
                   }
           )
)
public Project get(long projectId) throws IOException {
  ...
}

@Cacheable(cacheName="projectListCache",
    keyGenerator = @KeyGenerator(
            name = "HashCodeCacheKeyGenerator",
            properties = {
                    @Property(name="includeMethod", value="false"),
                    @Property(name="includeParameterTypes", value="false")
            }
    )
)
public List<Project> listAll() {
  ...
}
```





### Row mapping

In a typical JDBC ORM scenario, Java would make calls to some form of mapper to convert from the ResultSet fields to a Java domain model object. In MISO, [RowMapper\<T\>](http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/jdbc/core/RowMapper.html) implementations are used to facilitate this, specifically a custom abstract class called [CacheAwareRowMapper\<T\>](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/sqlstore/cache/CacheAwareRowMapper.java):









```java
public abstract class CacheAwareRowMapper<T> implements RowMapper<T> {
  private boolean lazy;

  ...

  public CacheAwareRowMapper(Class<T> clz, boolean lazy) {
    this.lazy = lazy;
    if (lazy) {
      this.cacheName = "lazy"+LimsUtils.capitalise(clz.getSimpleName())+"Cache";
    }
    else {
      this.cacheName = LimsUtils.noddyCamelCaseify(clz.getSimpleName())+"Cache";
    }
  }

  ...

  public Cache lookupCache(CacheManager cacheManager) throws CacheException, UnsupportedOperationException {

  }
}
```





Class construction, can happen in a number of ways, but the most typical is depicted above, i.e. with a Class and boolean signature. Another important method of note is **lookupCache(CacheManager cacheManager)**. This method ensures the cache specified in the constructor is available in a given CacheManager, avoiding NullPointerExceptions.

Three default types of cache exist in MISO, **standard**, **lazy**, and **list**, hence the constructor logic seen above:

*   Standard caches hold the results of full object DAO get() operations, and are named as "\<objectClassSimpleName\>Cache", e.g. _projectCache_
*   Lazy caches hold the results of partial object DAO lazyGet() operations, and are named as "lazy\<objectClassSimpleName\>Cache", e.g. _lazyProjectCache_
*   List caches hold the results of full or partial DAO listAll() operations, and are named as "\<objectClassSimpleName\>ListCache", e.g. _projectListCache_

Let's look at a typical implementation of a [CacheAwareRowMapper\<T\>](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/sqlstore/cache/CacheAwareRowMapper.java), in this instance for [Project](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Project.java) objects, in the [SQLProjectDAO](https://github.com/TGAC/miso-lims/blob/develop/sqlstore/src/main/java/uk/ac/bbsrc/tgac/miso/sqlstore/SQLProjectDAO.java) class:









```java
public class ProjectMapper extends CacheAwareRowMapper<Project> {
  public ProjectMapper() {
    super(Project.class);
  }

  public ProjectMapper(boolean lazy) {
    super(Project.class, lazy);
  }

  @Override
  public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
    long id = rs.getLong("projectId");
    Project project = null;
    try {
      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for Project " + id);
          return (Project)element.getObjectValue();
        }
      }

      // do ORM mapping from ResultSet fields to Project object fields
      ...

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id) ,project));
        log.debug("Cache put for Project " + id);
      }
    }
    catch(net.sf.ehcache.CacheException ce) {
      ce.printStackTrace();
    }
    catch(UnsupportedOperationException uoe) {
      uoe.printStackTrace();
    }
    return project;
  }
}
```





As you can see, if a Project instance (lazy or otherwise) with a matching hashcode key (see the [DBUtils helper class](https://github.com/TGAC/miso-lims/blob/develop/sqlstore/src/main/java/uk/ac/bbsrc/tgac/miso/sqlstore/util/DbUtils.java)) is already in a given cache, then that object is immediately returned and no object creation and mapping takes place. If not, the mapping takes place, and the resulting object is placed into the cache layer.

Each DAO would usually, and does by default, have such a CacheAwareRowMapper for SQL databases. Other engines may well require (or not, as the case may be), different caching strategies.

# Web Application

The main MISO web application is powered by the [Spring framework](http://www.springsource.org/), notably [Spring MVC](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/mvc.html). This allows powerful webapp configuration and tailoring via Spring XML and annotations, making functionality like the REST API a breeze. Here, we will go through the MISO elements that comprise the web application layer.

## Configuration

A great deal of MISO can be configured at the Spring XML level, making it easy for developers to swap out existing MISO implementations for their own, via [Dependency Injection](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/overview.html#overview-dependency-injection). MISO uses the usual web.xml to define properties relevant to the webapp container, and a number of Spring configuration XML files for the core application itself:

|Configuration file|Description|Documentation|
|------------------|-----------|-------------|
|[web.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/web.xml)|Configures the web application with respect to the web container, e.g. Tomcat.|[Core webapp configuration](#DeveloperManual-Corewebappconfiguration)|
|[apicationContext.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/applicationContext.xml)|Configures the central application miso.properties location and pulls in the configuration files below|[Application context configuration](#DeveloperManual-Applicationcontextconfiguration)|
|[miso-servlet.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/miso-servlet.xml)|Defines low-level MISO webapp-centric elements.|[MISO servlet configuration](#DeveloperManual-MISOservletconfiguration)|
|[miso-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/miso-config.xml)|High-level user-space MISO bean configuration|[MISO configuration](#DeveloperManual-MISOconfiguration)|
|[db-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/resources/sql/db-config.xml)|Configures access to the underlying datasource|[Database configuration](#DeveloperManual-Databaseconfiguration)|
|[jdbc-security-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/jdbc-security-config.xml) / [ldap-security-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/ldap-security-config.xml)|Database and LDAP specific configuration, respectively|[Security configuration](#DeveloperManual-Securityconfiguration)|
|[event-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/event-config.xml)|Configures the event subsystem, e.g. aspects for watcher alerting|[Event configuration](#DeveloperManual-Eventconfiguration)|
|[integration-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/integration-config.xml)|Configures elements in the integration layer, e.g. analysis server|[Integration configuration](#DeveloperManual-Integrationconfiguration)|
|[print-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/print-config.xml)|Configures the printing subsystem, e.g. print context resolvers|[Printer configuration](#DeveloperManual-Printerconfiguration)|
|[logging-config.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/webapp/WEB-INF/logging-config.xml)|Configures the logging subsystem, e.g. request layer logging|[Logging configuration](#DeveloperManual-Loggingconfiguration)|
|[ehcache.xml](https://github.com/TGAC/miso-lims/blob/develop/miso-web/src/main/resources/ehcache.xml)|Configures the caching layer|[EhCache configuration](#DeveloperManual-EhCacheconfiguration)|


### Core webapp configuration

The web.xml is the lowest-level configuration element of many web application containers. In the case of Spring, it allows mapping of URLs to DispatcherServlets, and the inclusion of any relevant filters or logging framework configuration property files.

### <span>Application context configuration</span>

The applicationContext.xml is always parsed first in a Spring web application, and could in theory hold all webapp config elements, but we have chosen to separate out the specific configuration component areas into individual XML files for ease of use. As such, this XML file pulls in the other downstream config files (see table above).

### <span><span>MISO servlet configuration</span></span>

This configuration holds low-level MISO configuration elements, e.g.

### <span><span><span>MISO configuration</span></span></span>

<span><span><span>The miso-config.xml file holds higher-level config elements, e.g.</span></span></span>

### <span><span><span>Database configuration</span></span></span>

<span><span><span>This configuration file covers the DAO obejct wiring and CascadeType policy definitions.</span></span></span>

### <span><span><span><span>Security configuration</span></span></span></span>

<span><span><span><span>There are two supplied security configuration templates supplied with MISO, one for LDAP and one fo JDBC. The JDBC configuration is the initial default, and is the simplest mechanism to get started. If you would like more fine-grained access to a directory-style authentication and role assignmet mechanism, then LDAP support is also available (subject to obvious configuration). The LDAP mechanism is a nice addition, because the same login access can be used for multiple tools.</span></span></span></span>

### <span>Event configuration</span>

<span>Aspect configuration surrounding the event model is configured here.</span>

### <span><span>Integration configuration</span></span>

<span><span>This file provides access to the minimal configuration around the analysis and notification server connectivity.</span></span>

### <span><span><span>Printer configuration</span></span></span>

The print-config.xml controls available PrintManager beans and relevant print services and strategies.

### <span><span><span><span>Logging configuration</span></span></span></span>

<span><span><span><span>Overarching logging mechanisms and respective aspect triggers are configured here.</span></span></span></span>

### EhCache configuration

Caches, eviction policies and cache sizes are configured here. There shouldn't be any reason to amend the configuration here in terms of available caches, other than increasing/decreasing cache sizes in memory and disk according to your available JVM memory.

## Contexts

## Controllers

## REST API

### Request Signing

Signing requests requires 3 elements:

*   REST API URL of the service you wish to request (see [REST API]({{ site.baseurl }}{% post_url 2016-01-12-rest-api %}) )
*   Your MISO username
*   Your MISO API key - this can be found by logging in to MISO as normal, clicking "My Account" and the key is in the top box

Producing HMAC keys from these elements for your request is easy:

```
echo -n "<REST-url>?x-url=<REST-url>@x-user=<miso_user_name>" | openssl sha1 -binary -hmac "<your_key_from_miso>" | openssl base64 | tr -d = | tr +/ -_
```

You can then use curl to initiate the request, using the REST API URL, your username, and the signed fragment produced above:

```
curl --request GET 'http://<miso_url>/<REST-url>'--header 'x-user:<miso_user_name>'--header 'x-signature:<hmac_string_from_above>'--header 'x-url:<REST-url>'
```

Putting the two together, here's an example shell script that can grab a list of libraries associated with a project:

```
#!/bin/bash

PROJECTID=$1
USER=$2
KEY=$3

SIGNATURE=`echo -n "/miso/rest/project/$PROJECTID/libraries?x-url=/miso/rest/project/$PROJECTID/libraries@x-user=$USER" | openssl sha1 -binary -hmac "$KEY" | openssl base64 | tr -d = | tr +/ -_`

curl --request GET "http://your.miso.url/miso/rest/project/$PROJECTID/libraries" --header "x-user:$USER" --header "x-signature:$SIGNATURE" --header "x-url:/miso/rest/project/$PROJECTID/libraries"
```





## AJAX Beans

## Services

## Utilities

## Resources

# Sequencer Notification System

## Core

## Transformers

## Utilities

## Configuration

## Consumer Services

# Stats DB

## API

# MISO Service Providers

MISO exposes many of its services via interface APIs that implement the Service Provider Interface framework. Specifically, the Fluxion SPI library is used to make annotating and discovering services easy via the @ServiceProvider and @Spi annotations. These marked-up services are then discovered at runtime by the core Java ServiceLoader class, and in most cases wired up by Spring to relevant classes that require these services. In this way, developers are able to supply their own services that hook into the MISO APIs and are automatically discovered.

## Service Interfaces and Providers

Service interfaces are annotated with the @Spi annotation, which marks them up for automatic discovery (see Service Discovery section below). Services should always have some kind of identifier, exposed via a simple getter, e.g. getName(). This name should be unique, otherwise services will become confused with others when discovered.









```java
@Spi
public interface FooService {
  String getName();
  void doSomething();
}
```





Service implementations are annotated with the @ServiceProvider annotation, which the ServiceLoader class uses to expose the concrete instances of @Spi interfaces.









```java
@ServiceProvider
public class FooServiceImpl {
  String getName() {
    return "FooServiceImpl";
  }

  void doSomething() {
    System.out.println("Something has been done");
  }
}
```





## Service Discovery

Services are discovered via the ServiceLoader class. An example of a class that resolves a given service is below:









```java
public class FooServiceResolver {
  private Map<String, FooService> serviceMap;

  public Collection<FooService> discoverServices() {
    if (serviceMap == null) {
      //on first call of this method, populate a map of service names to services discovered on the classpath
      ServiceLoader<FooService> loader = ServiceLoader.load(FooService.class);
      Iterator<FooService> it = loader.iterator();

      serviceMap = new HashMap<String, FooService>();

      while (it.hasNext()) {
        FooService s = it.next();
        if (!serviceMap.containsKey(s.getName())) {
          serviceMap.put(s.getName(), s);
        }
        else {
          if (serviceMap.get(s.getName()) != s) {
            String msg = "Multiple different FooServices with the same service name " +
            "('" + s.getName() + "') are present on the classpath. Service names must be unique.";            
            throw new ServiceConfigurationError(msg);
          }
        }
      }
    }
    return serviceMap.values();
  }

  public FooService getFooService(String serviceName) {
    for (FooService s : discoverServices()) {
      if (s.getName().equals(serviceName)) {
        return s;
      }
    }
    log.warn("No service called '" + serviceName + "' was available on the classpath");
    return null;
  }
}
```





You would realistically have one such class for each service type interface, and wire these into relevant beans via Spring configuration.

## Implementations

## Naming Scheme Services

MISO has a default built-in service interface called [MisoNamingScheme](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/service/naming/MisoNamingScheme.java) that defines contracts for supplying values to [Nameable](https://github.com/TGAC/miso-lims/blob/develop/core/src/main/java/uk/ac/bbsrc/tgac/miso/core/data/Nameable.java) object fields, e.g. name and alias. There are a number of concrete default implementations of this interface.

## Tag Barcode Services

## Notification Consumer Services

## Barcode Printing Services

## Plate Conversion Services

# Analysis Modules
