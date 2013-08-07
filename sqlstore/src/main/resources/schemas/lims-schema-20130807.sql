# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.6.12)
# Database: lims
# Generation Time: 2013-08-07 10:58:40 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table _Group
# ------------------------------------------------------------

CREATE TABLE `_Group` (
  `groupId` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`groupId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Alert
# ------------------------------------------------------------

CREATE TABLE `Alert` (
  `alertId` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `text` text NOT NULL,
  `userId` bigint(20) NOT NULL,
  `date` date NOT NULL,
  `isRead` bit(1) NOT NULL DEFAULT b'0',
  `level` varchar(8) NOT NULL DEFAULT 'INFO',
  PRIMARY KEY (`alertId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Chamber
# ------------------------------------------------------------

CREATE TABLE `Chamber` (
  `chamberId` bigint(20) NOT NULL AUTO_INCREMENT,
  `chamberNumber` tinyint(4) NOT NULL,
  `pool_poolId` bigint(20) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`chamberId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table DATABASECHANGELOG
# ------------------------------------------------------------

CREATE TABLE `DATABASECHANGELOG` (
  `ID` varchar(63) NOT NULL,
  `AUTHOR` varchar(63) NOT NULL,
  `FILENAME` varchar(200) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`,`AUTHOR`,`FILENAME`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table DATABASECHANGELOGLOCK
# ------------------------------------------------------------

CREATE TABLE `DATABASECHANGELOGLOCK` (
  `ID` int(11) NOT NULL,
  `LOCKED` tinyint(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table emPCR
# ------------------------------------------------------------

CREATE TABLE `emPCR` (
  `pcrId` bigint(20) NOT NULL AUTO_INCREMENT,
  `concentration` double NOT NULL,
  `dilution_dilutionId` bigint(20) NOT NULL,
  `creationDate` date NOT NULL,
  `pcrUserName` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`pcrId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table emPCRDilution
# ------------------------------------------------------------

CREATE TABLE `emPCRDilution` (
  `dilutionId` bigint(20) NOT NULL AUTO_INCREMENT,
  `concentration` double NOT NULL,
  `emPCR_pcrId` bigint(20) NOT NULL,
  `identificationBarcode` varchar(13) DEFAULT NULL,
  `creationDate` date NOT NULL,
  `dilutionUserName` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`dilutionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table Experiment
# ------------------------------------------------------------

CREATE TABLE `Experiment` (
  `experimentId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `accession` varchar(30) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `study_studyId` bigint(20) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `platform_platformId` bigint(20) NOT NULL,
  PRIMARY KEY (`experimentId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table Experiment_Kit
# ------------------------------------------------------------

CREATE TABLE `Experiment_Kit` (
  `experiments_experimentId` bigint(20) NOT NULL,
  `kits_kitId` bigint(20) NOT NULL,
  PRIMARY KEY (`experiments_experimentId`,`kits_kitId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Experiment_Run
# ------------------------------------------------------------

CREATE TABLE `Experiment_Run` (
  `Experiment_experimentId` bigint(20) NOT NULL,
  `runs_runId` bigint(20) NOT NULL,
  PRIMARY KEY (`Experiment_experimentId`,`runs_runId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Flowcell
# ------------------------------------------------------------

CREATE TABLE `Flowcell` (
  `flowcellId` bigint(20) NOT NULL AUTO_INCREMENT,
  `reservoirType` varchar(10) NOT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `paired` bit(1) NOT NULL DEFAULT b'0',
  `platformType` varchar(50) DEFAULT NULL,
  `validationBarcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`flowcellId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Flowcell_Chamber
# ------------------------------------------------------------

CREATE TABLE `Flowcell_Chamber` (
  `Flowcell_flowcellId` bigint(20) NOT NULL,
  `chambers_chamberId` bigint(20) NOT NULL,
  PRIMARY KEY (`Flowcell_flowcellId`,`chambers_chamberId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Flowcell_Lane
# ------------------------------------------------------------

CREATE TABLE `Flowcell_Lane` (
  `Flowcell_flowcellId` bigint(20) NOT NULL,
  `lanes_laneId` bigint(20) NOT NULL,
  PRIMARY KEY (`Flowcell_flowcellId`,`lanes_laneId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Kit
# ------------------------------------------------------------

CREATE TABLE `Kit` (
  `kitId` bigint(20) NOT NULL AUTO_INCREMENT,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `lotNumber` varchar(30) NOT NULL,
  `kitDate` date NOT NULL,
  `kitDescriptorId` bigint(20) NOT NULL,
  PRIMARY KEY (`kitId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Kit_Note
# ------------------------------------------------------------

CREATE TABLE `Kit_Note` (
  `kit_kitId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`kit_kitId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table KitDescriptor
# ------------------------------------------------------------

CREATE TABLE `KitDescriptor` (
  `kitDescriptorId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `version` int(3) DEFAULT NULL,
  `manufacturer` varchar(100) NOT NULL,
  `partNumber` varchar(50) NOT NULL,
  `stockLevel` int(10) NOT NULL DEFAULT '0',
  `kitType` varchar(30) NOT NULL,
  `platformType` varchar(20) NOT NULL,
  PRIMARY KEY (`kitDescriptorId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Lane
# ------------------------------------------------------------

CREATE TABLE `Lane` (
  `laneId` bigint(20) NOT NULL AUTO_INCREMENT,
  `laneNumber` tinyint(4) NOT NULL,
  `pool_poolId` bigint(20) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`laneId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table Library
# ------------------------------------------------------------

CREATE TABLE `Library` (
  `libraryId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `accession` varchar(30) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `sample_sampleId` bigint(20) NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `libraryType` bigint(20) DEFAULT NULL,
  `concentration` double DEFAULT NULL,
  `creationDate` date NOT NULL,
  `platformName` varchar(255) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `paired` bit(1) NOT NULL DEFAULT b'0',
  `librarySelectionType` bigint(20) DEFAULT NULL,
  `libraryStrategyType` bigint(20) DEFAULT NULL,
  `qcPassed` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`libraryId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Library_Note
# ------------------------------------------------------------

CREATE TABLE `Library_Note` (
  `library_libraryId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`library_libraryId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Library_TagBarcode
# ------------------------------------------------------------

CREATE TABLE `Library_TagBarcode` (
  `library_libraryId` bigint(20) NOT NULL,
  `barcode_barcodeId` bigint(20) NOT NULL,
  PRIMARY KEY (`library_libraryId`,`barcode_barcodeId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table LibraryDilution
# ------------------------------------------------------------

CREATE TABLE `LibraryDilution` (
  `dilutionId` bigint(20) NOT NULL AUTO_INCREMENT,
  `concentration` double NOT NULL,
  `library_libraryId` bigint(20) NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `creationDate` date NOT NULL,
  `dilutionUserName` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`dilutionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table LibraryQC
# ------------------------------------------------------------

CREATE TABLE `LibraryQC` (
  `qcId` bigint(20) NOT NULL AUTO_INCREMENT,
  `library_libraryId` bigint(20) NOT NULL,
  `qcUserName` varchar(255) NOT NULL,
  `qcDate` date NOT NULL,
  `qcMethod` bigint(20) DEFAULT NULL,
  `results` double DEFAULT NULL,
  `insertSize` int(11) NOT NULL,
  PRIMARY KEY (`qcId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table LibrarySelectionType
# ------------------------------------------------------------

CREATE TABLE `LibrarySelectionType` (
  `librarySelectionTypeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`librarySelectionTypeId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table LibraryStrategyType
# ------------------------------------------------------------

CREATE TABLE `LibraryStrategyType` (
  `libraryStrategyTypeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`libraryStrategyTypeId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table LibraryType
# ------------------------------------------------------------

CREATE TABLE `LibraryType` (
  `libraryTypeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `platformType` varchar(50) NOT NULL,
  PRIMARY KEY (`libraryTypeId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Note
# ------------------------------------------------------------

CREATE TABLE `Note` (
  `noteId` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` date NOT NULL,
  `internalOnly` bit(1) NOT NULL DEFAULT b'1',
  `text` varchar(255) NOT NULL,
  `owner_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`noteId`),
  KEY `FK2524124140968C` (`owner_userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Partition
# ------------------------------------------------------------

CREATE TABLE `Partition` (
  `partitionId` bigint(20) NOT NULL AUTO_INCREMENT,
  `partitionNumber` tinyint(4) NOT NULL,
  `pool_poolId` bigint(20) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`partitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table persistent_logins
# ------------------------------------------------------------

CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Plate
# ------------------------------------------------------------

CREATE TABLE `Plate` (
  `plateId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `creationDate` date NOT NULL,
  `plateMaterialType` varchar(20) NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `size` int(11) NOT NULL DEFAULT '96',
  `tagBarcodeId` bigint(20) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`plateId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Plate_Elements
# ------------------------------------------------------------

CREATE TABLE `Plate_Elements` (
  `plate_plateId` bigint(20) NOT NULL,
  `elementType` varchar(255) NOT NULL,
  `elementPosition` int(11) NOT NULL,
  `elementId` bigint(20) NOT NULL,
  PRIMARY KEY (`plate_plateId`,`elementId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Plate_Library
# ------------------------------------------------------------

CREATE TABLE `Plate_Library` (
  `plate_plateId` bigint(20) NOT NULL,
  `library_libraryId` bigint(20) NOT NULL,
  PRIMARY KEY (`plate_plateId`,`library_libraryId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Platform
# ------------------------------------------------------------

CREATE TABLE `Platform` (
  `platformId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `instrumentModel` varchar(100) NOT NULL,
  `description` varchar(255) NOT NULL,
  `numContainers` tinyint(4) NOT NULL,
  PRIMARY KEY (`platformId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Pool
# ------------------------------------------------------------

CREATE TABLE `Pool` (
  `poolId` bigint(20) NOT NULL AUTO_INCREMENT,
  `concentration` double NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `creationDate` date NOT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `experiment_experimentId` bigint(20) DEFAULT NULL,
  `platformType` varchar(50) NOT NULL,
  `ready` tinyint(1) NOT NULL DEFAULT '0',
  `alias` varchar(50) DEFAULT NULL,
  `qcPassed` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`poolId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table Pool_Elements
# ------------------------------------------------------------

CREATE TABLE `Pool_Elements` (
  `pool_poolId` bigint(20) NOT NULL,
  `elementType` varchar(255) NOT NULL,
  `elementId` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_poolId`,`elementId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Pool_emPCRDilution
# ------------------------------------------------------------

CREATE TABLE `Pool_emPCRDilution` (
  `pool_poolId` bigint(20) NOT NULL,
  `dilutions_dilutionId` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_poolId`,`dilutions_dilutionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Pool_Experiment
# ------------------------------------------------------------

CREATE TABLE `Pool_Experiment` (
  `pool_poolId` bigint(20) NOT NULL,
  `experiments_experimentId` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_poolId`,`experiments_experimentId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Pool_LibraryDilution
# ------------------------------------------------------------

CREATE TABLE `Pool_LibraryDilution` (
  `pool_poolId` bigint(20) NOT NULL,
  `dilutions_dilutionId` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_poolId`,`dilutions_dilutionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table PoolQC
# ------------------------------------------------------------

CREATE TABLE `PoolQC` (
  `qcId` bigint(20) NOT NULL AUTO_INCREMENT,
  `pool_poolId` bigint(20) NOT NULL,
  `qcUserName` varchar(255) NOT NULL,
  `qcDate` date NOT NULL,
  `qcMethod` bigint(20) DEFAULT NULL,
  `results` double DEFAULT NULL,
  PRIMARY KEY (`qcId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table PrintJob
# ------------------------------------------------------------

CREATE TABLE `PrintJob` (
  `jobId` bigint(20) NOT NULL AUTO_INCREMENT,
  `printServiceName` varchar(100) NOT NULL,
  `printDate` date NOT NULL,
  `jobCreator_userId` bigint(20) NOT NULL,
  `printedElements` blob NOT NULL,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`jobId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table PrintService
# ------------------------------------------------------------

CREATE TABLE `PrintService` (
  `serviceId` bigint(20) NOT NULL AUTO_INCREMENT,
  `serviceName` varchar(100) NOT NULL,
  `contextName` varchar(100) NOT NULL,
  `contextFields` text,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `printServiceFor` varchar(255) NOT NULL,
  `printSchema` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`serviceId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Project
# ------------------------------------------------------------

CREATE TABLE `Project` (
  `projectId` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `progress` varchar(20) NOT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`projectId`),
  KEY `FK50C8E2F960F9CBA8` (`securityProfile_profileId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Project_Issues
# ------------------------------------------------------------

CREATE TABLE `Project_Issues` (
  `project_projectId` bigint(20) NOT NULL,
  `issueKey` varchar(255) NOT NULL,
  PRIMARY KEY (`project_projectId`,`issueKey`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Project_Note
# ------------------------------------------------------------

CREATE TABLE `Project_Note` (
  `project_projectId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`project_projectId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Project_ProjectOverview
# ------------------------------------------------------------

CREATE TABLE `Project_ProjectOverview` (
  `project_projectId` bigint(20) NOT NULL,
  `overviews_overviewId` bigint(20) NOT NULL,
  PRIMARY KEY (`project_projectId`,`overviews_overviewId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;



# Dump of table Project_Request
# ------------------------------------------------------------

CREATE TABLE `Project_Request` (
  `Project_projectId` bigint(20) NOT NULL,
  `requests_requestId` bigint(20) NOT NULL,
  UNIQUE KEY `requests_requestId` (`requests_requestId`),
  KEY `FKDA6E0B2925FFBF98` (`Project_projectId`),
  KEY `FKDA6E0B29B36A83EF` (`requests_requestId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Project_Study
# ------------------------------------------------------------

CREATE TABLE `Project_Study` (
  `Project_projectId` bigint(20) NOT NULL,
  `studies_studyId` bigint(20) NOT NULL,
  KEY `studyId` (`studies_studyId`) USING BTREE,
  KEY `projectId` (`Project_projectId`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table ProjectOverview
# ------------------------------------------------------------

CREATE TABLE `ProjectOverview` (
  `overviewId` bigint(20) NOT NULL AUTO_INCREMENT,
  `principalInvestigator` varchar(255) NOT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `numProposedSamples` int(10) DEFAULT NULL,
  `locked` bit(1) NOT NULL DEFAULT b'0',
  `allSampleQcPassed` bit(1) DEFAULT b'0',
  `libraryPreparationComplete` bit(1) DEFAULT b'0',
  `allLibraryQcPassed` bit(1) DEFAULT b'0',
  `allPoolsConstructed` bit(1) DEFAULT b'0',
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `allRunsCompleted` bit(1) DEFAULT b'0',
  `primaryAnalysisCompleted` bit(1) DEFAULT b'0',
  PRIMARY KEY (`overviewId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Information about a project proposal';



# Dump of table ProjectOverview_Note
# ------------------------------------------------------------

CREATE TABLE `ProjectOverview_Note` (
  `overview_overviewId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`overview_overviewId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table QCType
# ------------------------------------------------------------

CREATE TABLE `QCType` (
  `qcTypeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `qcTarget` varchar(50) NOT NULL,
  `units` varchar(20) NOT NULL,
  PRIMARY KEY (`qcTypeId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Request
# ------------------------------------------------------------

CREATE TABLE `Request` (
  `requestId` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `executionCount` int(11) NOT NULL,
  `lastExecutionDate` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `protocolUniqueIdentifier` varchar(255) DEFAULT NULL,
  `project_projectId` bigint(20) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`requestId`),
  KEY `FKA4878A6F60F9CBA8` (`securityProfile_profileId`),
  KEY `FKA4878A6F25FFBF98` (`project_projectId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Request_Note
# ------------------------------------------------------------

CREATE TABLE `Request_Note` (
  `Request_requestId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  UNIQUE KEY `notes_noteId` (`notes_noteId`),
  KEY `FK57687FE2A7DC4D2C` (`notes_noteId`),
  KEY `FK57687FE2E8B554FA` (`Request_requestId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Run
# ------------------------------------------------------------

CREATE TABLE `Run` (
  `runId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `accession` varchar(50) DEFAULT NULL,
  `platformRunId` int(11) DEFAULT NULL,
  `pairedEnd` tinyint(1) NOT NULL DEFAULT '0',
  `cycles` smallint(6) DEFAULT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `platformType` varchar(50) NOT NULL,
  `status_statusId` bigint(20) DEFAULT NULL,
  `alias` varchar(255) DEFAULT NULL,
  `sequencerReference_sequencerReferenceId` bigint(20) NOT NULL,
  PRIMARY KEY (`runId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table Run_Flowcell
# ------------------------------------------------------------

CREATE TABLE `Run_Flowcell` (
  `Run_runId` bigint(20) NOT NULL,
  `flowcells_flowcellId` bigint(20) NOT NULL,
  PRIMARY KEY (`Run_runId`,`flowcells_flowcellId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Run_Note
# ------------------------------------------------------------

CREATE TABLE `Run_Note` (
  `run_runId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`run_runId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Run_SequencerPartitionContainer
# ------------------------------------------------------------

CREATE TABLE `Run_SequencerPartitionContainer` (
  `Run_runId` bigint(20) NOT NULL,
  `containers_containerId` bigint(20) NOT NULL,
  PRIMARY KEY (`Run_runId`,`containers_containerId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table RunQC
# ------------------------------------------------------------

CREATE TABLE `RunQC` (
  `qcId` bigint(20) NOT NULL AUTO_INCREMENT,
  `run_runId` bigint(20) NOT NULL,
  `qcUserName` varchar(255) NOT NULL,
  `qcDate` date NOT NULL,
  `qcMethod` bigint(20) NOT NULL,
  `information` text,
  `doNotProcess` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`qcId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table RunQC_Partition
# ------------------------------------------------------------

CREATE TABLE `RunQC_Partition` (
  `runQc_runQcId` bigint(20) NOT NULL,
  `containers_containerId` bigint(20) NOT NULL DEFAULT '0',
  `partitionNumber` tinyint(2) NOT NULL,
  PRIMARY KEY (`runQc_runQcId`,`containers_containerId`,`partitionNumber`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Sample
# ------------------------------------------------------------

CREATE TABLE `Sample` (
  `sampleId` bigint(20) NOT NULL AUTO_INCREMENT,
  `accession` varchar(50) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `sampleType` varchar(50) NOT NULL,
  `receivedDate` date DEFAULT NULL,
  `qcPassed` varchar(5) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `project_projectId` bigint(20) NOT NULL,
  `scientificName` varchar(255) NOT NULL,
  `taxonIdentifier` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sampleId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table Sample_Note
# ------------------------------------------------------------

CREATE TABLE `Sample_Note` (
  `sample_sampleId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`sample_sampleId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table SampleQC
# ------------------------------------------------------------

CREATE TABLE `SampleQC` (
  `qcId` bigint(20) NOT NULL AUTO_INCREMENT,
  `sample_sampleId` bigint(20) NOT NULL,
  `qcUserName` varchar(255) NOT NULL,
  `qcDate` date NOT NULL,
  `qcMethod` bigint(20) DEFAULT NULL,
  `results` double DEFAULT NULL,
  PRIMARY KEY (`qcId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table SampleType
# ------------------------------------------------------------

CREATE TABLE `SampleType` (
  `typeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`typeId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table SecurityProfile
# ------------------------------------------------------------

CREATE TABLE `SecurityProfile` (
  `profileId` bigint(20) NOT NULL AUTO_INCREMENT,
  `allowAllInternal` bit(1) NOT NULL,
  `owner_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`profileId`),
  KEY `FK18AEBA294140968C` (`owner_userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table SecurityProfile_ReadGroup
# ------------------------------------------------------------

CREATE TABLE `SecurityProfile_ReadGroup` (
  `SecurityProfile_profileId` bigint(20) NOT NULL,
  `readGroup_groupId` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table SecurityProfile_ReadUser
# ------------------------------------------------------------

CREATE TABLE `SecurityProfile_ReadUser` (
  `SecurityProfile_profileId` bigint(20) NOT NULL,
  `readUser_userId` bigint(20) NOT NULL,
  KEY `FKD4CF504160F9CBA8` (`SecurityProfile_profileId`),
  KEY `FKD4CF504125267E4D` (`readUser_userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table SecurityProfile_WriteGroup
# ------------------------------------------------------------

CREATE TABLE `SecurityProfile_WriteGroup` (
  `SecurityProfile_profileId` bigint(20) NOT NULL,
  `writeGroup_groupId` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table SecurityProfile_WriteUser
# ------------------------------------------------------------

CREATE TABLE `SecurityProfile_WriteUser` (
  `SecurityProfile_profileId` bigint(20) NOT NULL,
  `writeUser_userId` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table SequencerPartitionContainer
# ------------------------------------------------------------

CREATE TABLE `SequencerPartitionContainer` (
  `containerId` bigint(20) NOT NULL AUTO_INCREMENT,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `platformType` varchar(50) DEFAULT NULL,
  `validationBarcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`containerId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table SequencerPartitionContainer_Partition
# ------------------------------------------------------------

CREATE TABLE `SequencerPartitionContainer_Partition` (
  `container_containerId` bigint(20) NOT NULL,
  `partitions_partitionId` bigint(20) NOT NULL,
  PRIMARY KEY (`container_containerId`,`partitions_partitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table SequencerReference
# ------------------------------------------------------------

CREATE TABLE `SequencerReference` (
  `referenceId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `ipAddress` blob NOT NULL,
  `platformId` bigint(20) NOT NULL,
  `available` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`referenceId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Status
# ------------------------------------------------------------

CREATE TABLE `Status` (
  `statusId` bigint(20) NOT NULL AUTO_INCREMENT,
  `health` varchar(50) NOT NULL DEFAULT 'Unknown',
  `completionDate` date DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `instrumentName` varchar(255) NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `runName` varchar(255) NOT NULL,
  `xml` longblob,
  PRIMARY KEY (`statusId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table Study
# ------------------------------------------------------------

CREATE TABLE `Study` (
  `studyId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `accession` varchar(30) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `project_projectId` bigint(20) NOT NULL,
  `studyType` varchar(255) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`studyId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



# Dump of table Study_Experiment
# ------------------------------------------------------------

CREATE TABLE `Study_Experiment` (
  `Study_studyId` bigint(20) NOT NULL,
  `experiments_experimentId` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table StudyType
# ------------------------------------------------------------

CREATE TABLE `StudyType` (
  `typeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`typeId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Submission
# ------------------------------------------------------------

CREATE TABLE `Submission` (
  `submissionId` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` date NOT NULL,
  `submittedDate` date DEFAULT NULL,
  `verified` bit(1) DEFAULT b'0',
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `accession` varchar(50) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `completed` bit(1) DEFAULT b'0',
  PRIMARY KEY (`submissionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Submission_Chamber
# ------------------------------------------------------------

CREATE TABLE `Submission_Chamber` (
  `submission_submissionId` bigint(20) NOT NULL,
  `chambers_chamberId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`chambers_chamberId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Submission_Experiment
# ------------------------------------------------------------

CREATE TABLE `Submission_Experiment` (
  `submission_submissionId` bigint(20) NOT NULL,
  `experiments_experimentId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`experiments_experimentId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Submission_Lane
# ------------------------------------------------------------

CREATE TABLE `Submission_Lane` (
  `submission_submissionId` bigint(20) NOT NULL,
  `lanes_laneId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`lanes_laneId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Submission_Partition
# ------------------------------------------------------------

CREATE TABLE `Submission_Partition` (
  `submission_submissionId` bigint(20) NOT NULL,
  `partitions_partitionId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`partitions_partitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Submission_Partition_Dilution
# ------------------------------------------------------------

CREATE TABLE `Submission_Partition_Dilution` (
  `submission_submissionId` bigint(20) NOT NULL,
  `partition_partitionId` bigint(20) NOT NULL,
  `dilution_dilutionId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`partition_partitionId`,`dilution_dilutionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Submission_Sample
# ------------------------------------------------------------

CREATE TABLE `Submission_Sample` (
  `submission_submissionId` bigint(20) NOT NULL,
  `samples_sampleId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`samples_sampleId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Submission_Study
# ------------------------------------------------------------

CREATE TABLE `Submission_Study` (
  `submission_submissionId` bigint(20) NOT NULL,
  `studies_studyId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`studies_studyId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table TagBarcodes
# ------------------------------------------------------------

CREATE TABLE `TagBarcodes` (
  `tagId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `sequence` varchar(20) NOT NULL,
  `platformName` varchar(20) NOT NULL,
  `strategyName` varchar(100) NOT NULL,
  PRIMARY KEY (`tagId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table User
# ------------------------------------------------------------

CREATE TABLE `User` (
  `userId` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `admin` bit(1) NOT NULL,
  `external` bit(1) NOT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `internal` bit(1) NOT NULL,
  `loginName` varchar(255) DEFAULT NULL,
  `roles` blob,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table User_Group
# ------------------------------------------------------------

CREATE TABLE `User_Group` (
  `users_userId` bigint(20) NOT NULL,
  `groups_groupId` bigint(20) NOT NULL,
  KEY `FKE7B7ED0B94349B7F` (`groups_groupId`),
  KEY `FKE7B7ED0B749D8197` (`users_userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table Watcher
# ------------------------------------------------------------

CREATE TABLE `Watcher` (
  `entityName` varchar(12) NOT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`entityName`,`userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
