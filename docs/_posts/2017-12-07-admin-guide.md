---
layout: page
title: "Administrator's Manual"
category: adm
date: 2016-01-11 13:51:46
order: 2
---

This guide container instructions to facilitate day-to-day running MISO at your institution. See [Building and Deploying](installation-guide) if you need to set up your instance.


# Adding Value-Type Data

MISO has several categories of value-type data which the users interact with but cannot add or edit.
There are some stored procedures which may help in adding these values to the database. If any of these are
out of date, please <a href="https://github.com/TGAC/miso-lims/issues">create an issue</a>, and check the
source code for the exact methods of <a href="https://github.com/TGAC/miso-lims/tree/master/sqlstore/src/main/resources/db/migration_beforeMigrate">adding</a> and <a href="https://github.com/TGAC/miso-lims/tree/master/sqlstore/src/main/resources/db/migration_afterMigrate">removing</a> data from the database.

## Indices (also known as Barcodes, Molecular IDs, Tag Barcodes)

An `Index` has a name, a sequence, a position (1 or 2) and is a member of an `Index Family`. An `Index Family` has a name and is associated with a sequencing platform, and can be archived to hide its members when creating new libraries.
```
addIndexFamily(name, platformType, archived)
addIndex(familyName, name, sequence, position)
```

For example, to add a set of Illumina indices named "Custom Indices A," with "Index 1" ACACACAC and "Index 2" GTGTGTGT:
```
CALL addIndexFamily('Custom Indices A', 'ILLUMINA', 0);
CALL addIndex('Custom Indices A', 'Index 1', 'ACACACAC', 1);
CALL addIndex('Custom Indices A', 'Index 2', 'GTGTGTGT', 1);
```
Note that the sequencing platform must be entered in all caps.
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_beforeMigrate/05_add_index.sql">Source</a>


## Boxes

A `Box` is described by a set `size` and `use`. MISO can be set up to integrate with a VisionMate scanner. Box sizes that can be read by the scanner are said to be scannable. Note that since `size` and `use` are required fields on a box, no boxes can be saved until at least one `size` and `use` are added to the database.
```
addBoxSize(numRows, numColumns, scannable);
addBoxUse(name);
```

For example, to add a box for DNA that has 10 rows and 10 columns and cannot be read by a box scanner, you would first need to add the following values to the database:
```
CALL addBoxUse('DNA');
CALL addBoxSize(10, 10, 0);
```
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_beforeMigrate/05_add_box_parameters.sql">Source</a>

## Library Type

Each library is described by a library type. This is a somewhat nebulous term, and not a controlled vocabulary term (in contrast to library selection and library strategy, which are [controlled vocabulary terms from the SRA](https://github.com/enasequence/schema/blob/master/src/main/resources/uk/ac/ebi/ena/sra/schema/SRA.experiment.xsd). Note that archived library types will not be available for creating new libraries.

```
addLibraryType(name, platform, archived);
```

For example, to add a whole genome PacBio library type:
```
CALL addLibraryType('Whole Genome', 'PACBIO', 0);
```
Note that the sequencing platform must be entered in all caps.
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_beforeMigrate/05_add_library_parameters.sql">Source</a>


## Instrument Models

Each instrument model has a platform, a model name, a description, and a maximum number of containers that can be associated with a single run.

```
addInstrumentModel(platform, modelName, description, maxContainers);
```

For example, to add a MinION sequencer:
```
CALL addInstrumentModel('OXFORDNANOPORE', 'MinION', '1-channel portable nanopore', 1);
```
Note that the sequencing platform must be entered in all caps.
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_beforeMigrate/05_add_platform.sql">Source</a>


## Instruments

Each instrument has a platform, an instrument model, a serial number, an IP address, a date the instrument was put into service (commissionedDate), an optional date the instrument was taken out of service (decommissionedDate), and an optional reference to the sequencer that this instrument was upgraded to.
Instruments can also be added to MISO by admin users.

```
addInstrument(name, platform, instrumentModel, serialNumber, ipAddress, commissionedDate, decommissionedDate, upgradedInstrumentName);
```

For instance, to add an Illumina HiSeq 2000 which was later upgraded to a HiSeq2500:
```
CALL addInstrument('h501', 'ILLUMINA', 'Illumina HiSeq 2000', '12345', 'localhost', '2015-01-07', '2016-05-19', 'SN501');
```
Note that the platform must be in all caps, and the instrumentModel must be an exact match for a value in `Platform.instrumentModel`.
Note also that if adding an instrument which references an upgraded instrument, the upgraded instrument must already exist in MISO. If the instrument to be added has not been upgraded, set upgradedInstrumentName to `NULL`.
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_beforeMigrate/05_add_instrument.sql">Source</a>


## Sequencing Parameters

Runs have sequencing parameters, and pool Orders are requested with a given set of sequencing parameters. MISO determines whether a pool Order has been completed by looking at the run(s) associated with the pool and seeing if their sequencing parameters match the requested sequencing parameters. While it is possible to add all possible sequencing parameter combinations to this table, it may be easier to add the parameters used most often and describing all infrequently-used parameters with the 'Custom' label.

```
addSequencingParameters(name, platform, instrumentModel, readLength, paired, chemistryVersion);
```

For instance, to add sequencing parameters for a paired-end run with a read length of 151 bases on the MiSeq:
```
CALL addSequencingParameters('2x151', 'ILLUMINA', 'Illumina MiSeq', 151, 1, 'V2');
```
Note that the sequencing platform must be in all caps, and the instrumentModel must be an exact match for a value in `Platform.instrumentModel`.
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_beforeMigrate/05_add_seqparam.sql">Source</a>


## QC Types

A QC is associated with a specific QC type, which has a name, description, target (sample, library, pool, lane), units of measurement, and an indicator for the post-decimal precision associated with the specific QC instrument.

```
addQcType(name, description, target, units, precision);
```

For example, to add Qubit as a QC Type for samples:
```
CALL addQcType('Qubit', 'Quantitation of DNA, RNA, and protein, manufactured by Invitrogen', 'Sample', 'ng/&#181;l', 2);
```
Note that special characters should be HTML-encoded. If your lab does Qubit for libraries as well, a separate QcType for target 'Library' would have to be added.
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_beforeMigrate/05_add_qc.sql">Source</a>


## Kits

A `KitDescriptor` has a name, version, manufacturer, part number, kit type (Library, Multiplexing, Sequencing), sequencing platform, and description. Note that while the MISO database does have a `Kit` table, it is not accessible from the user interface at this time. There are plans to add a kit tracking feature to MISO in the future.
```
addKitDescriptor(name, version, partNumber, kitType, platform, description);
```

For example, to add a Nextera library prep kit:
```
CALL addKitDescriptor('Nextera DNA Exome', 1, 1, 'LIBRARY', 'ILLUMINA', 'Previously known as the TruSeq Rapid Exome Library Prep Kit');
```
Note that the kit type and sequencing platform must be entered in all caps.
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_beforeMigrate/05_add_kit.sql">Source</a>


## Targeted Sequencing

A Targeted Sequencing panel has a name, description, and name of the library kit with which it is to be used. Targeted Sequencing values can be added to Library Dilutions to assist with analysis.

```
addTargetedSequencing(alias, description, kitName, archived);
```

For example, to add a targeted sequencing panel for a library kit of the same name:
```
CALL addTargetedSequencing('Agilent SureSelectXT MethylSeq', 'Agilent SureSelectXT panel for methyl seq', 'Agilent SureSelectXT MethylSeq', 0);
```
Note: the library kit must already exist in MISO.
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_beforeMigrate/05_add_tarseq.sql">Source</a>


# Removing Lab-entered data

Lab members are now able to delete samples, libraries, dilutions, and pools. Generally, they are only able 
to delete items they have created, but some exceptions apply. An item can only be deleted if it has no
realted downstream-processing items; for example, a library with associated dilutions and pools can only 
be deleted after the pools and then the dilutions are deleted.

Deleting an item deletes all of the associated entities (QCs, sequencing parameters, position in box or sequencing container, changelogs, etc.). Deletions are final and cannot be undone.

As an administrator, the following items can be deleted via SQL stored procedures:

## Runs

```
CALL deleteRun(runId, runAlias);
```
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_afterMigrate/05_delete_run.sql">Source</a>


## Sequencing Containers

```
CALL deleteContainer(containerId, containerBarcode);
```
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_afterMigrate/05_delete_container.sql">Source</a>


## Pools

```
CALL deletePool(poolId, poolAlias);
```
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_afterMigrate/05_delete_pool.sql">Source</a>


## Dilutions

```
CALL deleteDilution(dilutionId, libraryId, userName);
```
Note: deleting a dilution adds a changelog entry to the library.
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_afterMigrate/05_delete_librarydilution.sql">Source</a>


## Libraries

```
CALL deleteLibrary(libraryId, libraryAlias);
```
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_afterMigrate/05_delete_library.sql">Source</a>


## Samples

```
CALL deleteSample(sampleId, sampleAlias);
```
<a href="https://github.com/TGAC/miso-lims/blob/master/sqlstore/src/main/resources/db/migration_afterMigrate/05_delete_sample.sql">Source</a>


# Upgrading to the latest version.

To upgrade MISO to the latest version, follow the upgrading instructions in [Building and Deploying](installation-guide#upgrading).
