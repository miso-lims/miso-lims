-- MySQL dump 10.13  Distrib 5.5.44, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: lims
-- ------------------------------------------------------
-- Server version	5.5.44-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Alert`
--

DROP TABLE IF EXISTS `Alert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Alert` (
  `alertId` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `text` text NOT NULL,
  `userId` bigint NOT NULL,
  `date` date NOT NULL,
  `isRead` bit NOT NULL DEFAULT b'0',
  `level` varchar(8) NOT NULL DEFAULT 'INFO',
  PRIMARY KEY (`alertId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Chamber`
--

DROP TABLE IF EXISTS `Chamber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Chamber` (
  `chamberId` bigint NOT NULL AUTO_INCREMENT,
  `chamberNumber` tinyint NOT NULL,
  `pool_poolId` bigint DEFAULT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  PRIMARY KEY (`chamberId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `EntityGroup`
--

DROP TABLE IF EXISTS `EntityGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `EntityGroup` (
  `entityGroupId` bigint NOT NULL AUTO_INCREMENT,
  `parentId` bigint NOT NULL,
  `parentType` varchar(255) NOT NULL,
  PRIMARY KEY (`entityGroupId`,`parentId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `EntityGroup_Elements`
--

DROP TABLE IF EXISTS `EntityGroup_Elements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `EntityGroup_Elements` (
  `entityGroup_entityGroupId` bigint NOT NULL,
  `entityId` bigint NOT NULL,
  `entityType` varchar(255) NOT NULL,
  PRIMARY KEY (`entityGroup_entityGroupId`,`entityId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Experiment`
--

DROP TABLE IF EXISTS `Experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Experiment` (
  `experimentId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `accession` varchar(30) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  `study_studyId` bigint DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `platform_platformId` bigint NOT NULL,
  PRIMARY KEY (`experimentId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Experiment_Kit`
--

DROP TABLE IF EXISTS `Experiment_Kit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Experiment_Kit` (
  `experiments_experimentId` bigint NOT NULL,
  `kits_kitId` bigint NOT NULL,
  PRIMARY KEY (`experiments_experimentId`,`kits_kitId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Experiment_Run`
--

DROP TABLE IF EXISTS `Experiment_Run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Experiment_Run` (
  `Experiment_experimentId` bigint NOT NULL,
  `runs_runId` bigint NOT NULL,
  PRIMARY KEY (`Experiment_experimentId`,`runs_runId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Flowcell`
--

DROP TABLE IF EXISTS `Flowcell`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Flowcell` (
  `flowcellId` bigint NOT NULL AUTO_INCREMENT,
  `reservoirType` varchar(10) NOT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `paired` bit NOT NULL DEFAULT b'0',
  `platformType` varchar(50) DEFAULT NULL,
  `validationBarcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`flowcellId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Flowcell_Chamber`
--

DROP TABLE IF EXISTS `Flowcell_Chamber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Flowcell_Chamber` (
  `Flowcell_flowcellId` bigint NOT NULL,
  `chambers_chamberId` bigint NOT NULL,
  PRIMARY KEY (`Flowcell_flowcellId`,`chambers_chamberId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Flowcell_Lane`
--

DROP TABLE IF EXISTS `Flowcell_Lane`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Flowcell_Lane` (
  `Flowcell_flowcellId` bigint NOT NULL,
  `lanes_laneId` bigint NOT NULL,
  PRIMARY KEY (`Flowcell_flowcellId`,`lanes_laneId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Kit`
--

DROP TABLE IF EXISTS `Kit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Kit` (
  `kitId` bigint NOT NULL AUTO_INCREMENT,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `lotNumber` varchar(30) NOT NULL,
  `kitDate` date NOT NULL,
  `kitDescriptorId` bigint NOT NULL,
  PRIMARY KEY (`kitId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `KitDescriptor`
--

DROP TABLE IF EXISTS `KitDescriptor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `KitDescriptor` (
  `kitDescriptorId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `version` int DEFAULT NULL,
  `manufacturer` varchar(100) NOT NULL,
  `partNumber` varchar(50) NOT NULL,
  `stockLevel` int NOT NULL DEFAULT '0',
  `kitType` varchar(30) NOT NULL,
  `platformType` varchar(20) NOT NULL,
  PRIMARY KEY (`kitDescriptorId`)
) ENGINE=InnoDB   DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Kit_Note`
--

DROP TABLE IF EXISTS `Kit_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Kit_Note` (
  `kit_kitId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`kit_kitId`,`notes_noteId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Lane`
--

DROP TABLE IF EXISTS `Lane`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Lane` (
  `laneId` bigint NOT NULL AUTO_INCREMENT,
  `laneNumber` tinyint NOT NULL,
  `pool_poolId` bigint DEFAULT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  PRIMARY KEY (`laneId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Library`
--

DROP TABLE IF EXISTS `Library`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Library` (
  `libraryId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `accession` varchar(30) DEFAULT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  `sample_sampleId` bigint NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `libraryType` bigint DEFAULT NULL,
  `concentration` double DEFAULT NULL,
  `creationDate` date NOT NULL,
  `platformName` varchar(255) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `paired` bit NOT NULL DEFAULT b'0',
  `librarySelectionType` bigint DEFAULT NULL,
  `libraryStrategyType` bigint DEFAULT NULL,
  `qcPassed` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`libraryId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryDilution`
--

DROP TABLE IF EXISTS `LibraryDilution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryDilution` (
  `dilutionId` bigint NOT NULL AUTO_INCREMENT,
  `concentration` double NOT NULL,
  `library_libraryId` bigint NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `creationDate` date NOT NULL,
  `dilutionUserName` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  PRIMARY KEY (`dilutionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryQC`
--

DROP TABLE IF EXISTS `LibraryQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryQC` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `library_libraryId` bigint NOT NULL,
  `qcUserName` varchar(255) NOT NULL,
  `qcDate` date NOT NULL,
  `qcMethod` bigint DEFAULT NULL,
  `results` double DEFAULT NULL,
  `insertSize` int NOT NULL,
  PRIMARY KEY (`qcId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibrarySelectionType`
--

DROP TABLE IF EXISTS `LibrarySelectionType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibrarySelectionType` (
  `librarySelectionTypeId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`librarySelectionTypeId`)
) ENGINE=InnoDB   DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryStrategyType`
--

DROP TABLE IF EXISTS `LibraryStrategyType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryStrategyType` (
  `libraryStrategyTypeId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`libraryStrategyTypeId`)
) ENGINE=InnoDB   DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryType`
--

DROP TABLE IF EXISTS `LibraryType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryType` (
  `libraryTypeId` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `platformType` varchar(50) NOT NULL,
  PRIMARY KEY (`libraryTypeId`)
) ENGINE=InnoDB   DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Library_Note`
--

DROP TABLE IF EXISTS `Library_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Library_Note` (
  `library_libraryId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`library_libraryId`,`notes_noteId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Library_TagBarcode`
--

DROP TABLE IF EXISTS `Library_TagBarcode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Library_TagBarcode` (
  `library_libraryId` bigint NOT NULL,
  `barcode_barcodeId` bigint NOT NULL,
  PRIMARY KEY (`library_libraryId`,`barcode_barcodeId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Note`
--

DROP TABLE IF EXISTS `Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Note` (
  `noteId` bigint NOT NULL AUTO_INCREMENT,
  `creationDate` date NOT NULL,
  `internalOnly` bit NOT NULL DEFAULT b'1',
  `text` text,
  `owner_userId` bigint DEFAULT NULL,
  PRIMARY KEY (`noteId`),
  KEY `FK2524124140968C` (`owner_userId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Plate`
--

DROP TABLE IF EXISTS `Plate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Plate` (
  `plateId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `creationDate` date NOT NULL,
  `plateMaterialType` varchar(20) NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `size` int NOT NULL DEFAULT '96',
  `tagBarcodeId` bigint DEFAULT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`plateId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Plate_Elements`
--

DROP TABLE IF EXISTS `Plate_Elements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Plate_Elements` (
  `plate_plateId` bigint NOT NULL,
  `elementType` varchar(255) NOT NULL,
  `elementPosition` int NOT NULL,
  `elementId` bigint NOT NULL,
  PRIMARY KEY (`plate_plateId`,`elementId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Plate_Library`
--

DROP TABLE IF EXISTS `Plate_Library`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Plate_Library` (
  `plate_plateId` bigint NOT NULL,
  `library_libraryId` bigint NOT NULL,
  PRIMARY KEY (`plate_plateId`,`library_libraryId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Platform`
--

DROP TABLE IF EXISTS `Platform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Platform` (
  `platformId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `instrumentModel` varchar(100) NOT NULL,
  `description` varchar(255) NOT NULL,
  `numContainers` tinyint NOT NULL,
  PRIMARY KEY (`platformId`)
) ENGINE=InnoDB   DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pool`
--

DROP TABLE IF EXISTS `Pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pool` (
  `poolId` bigint NOT NULL AUTO_INCREMENT,
  `concentration` double NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `creationDate` date NOT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  `experiment_experimentId` bigint DEFAULT NULL,
  `platformType` varchar(50) NOT NULL,
  `ready` tinyint NOT NULL DEFAULT '0',
  `alias` varchar(50) DEFAULT NULL,
  `qcPassed` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`poolId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PoolQC`
--

DROP TABLE IF EXISTS `PoolQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `PoolQC` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `pool_poolId` bigint NOT NULL,
  `qcUserName` varchar(255) NOT NULL,
  `qcDate` date NOT NULL,
  `qcMethod` bigint DEFAULT NULL,
  `results` double DEFAULT NULL,
  PRIMARY KEY (`qcId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pool_Elements`
--

DROP TABLE IF EXISTS `Pool_Elements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pool_Elements` (
  `pool_poolId` bigint NOT NULL,
  `elementType` varchar(255) NOT NULL,
  `elementId` bigint NOT NULL,
  PRIMARY KEY (`pool_poolId`,`elementId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pool_Experiment`
--

DROP TABLE IF EXISTS `Pool_Experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pool_Experiment` (
  `pool_poolId` bigint NOT NULL,
  `experiments_experimentId` bigint NOT NULL,
  PRIMARY KEY (`pool_poolId`,`experiments_experimentId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pool_LibraryDilution`
--

DROP TABLE IF EXISTS `Pool_LibraryDilution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pool_LibraryDilution` (
  `pool_poolId` bigint NOT NULL,
  `dilutions_dilutionId` bigint NOT NULL,
  PRIMARY KEY (`pool_poolId`,`dilutions_dilutionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pool_emPCRDilution`
--

DROP TABLE IF EXISTS `Pool_emPCRDilution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pool_emPCRDilution` (
  `pool_poolId` bigint NOT NULL,
  `dilutions_dilutionId` bigint NOT NULL,
  PRIMARY KEY (`pool_poolId`,`dilutions_dilutionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PrintJob`
--

DROP TABLE IF EXISTS `PrintJob`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `PrintJob` (
  `jobId` bigint NOT NULL AUTO_INCREMENT,
  `printServiceName` varchar(100) NOT NULL,
  `printDate` date NOT NULL,
  `jobCreator_userId` bigint NOT NULL,
  `printedElements` blob NOT NULL,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`jobId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PrintService`
--

DROP TABLE IF EXISTS `PrintService`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `PrintService` (
  `serviceId` bigint NOT NULL AUTO_INCREMENT,
  `serviceName` varchar(100) NOT NULL,
  `contextName` varchar(100) NOT NULL,
  `contextFields` text,
  `enabled` bit NOT NULL DEFAULT b'1',
  `printServiceFor` varchar(255) NOT NULL,
  `printSchema` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`serviceId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Project`
--

DROP TABLE IF EXISTS `Project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Project` (
  `projectId` bigint NOT NULL AUTO_INCREMENT,
  `creationDate` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  `progress` varchar(20) NOT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`projectId`),
  KEY `FK50C8E2F960F9CBA8` (`securityProfile_profileId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProjectOverview`
--

DROP TABLE IF EXISTS `ProjectOverview`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `ProjectOverview` (
  `overviewId` bigint NOT NULL AUTO_INCREMENT,
  `principalInvestigator` varchar(255) NOT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `numProposedSamples` int DEFAULT NULL,
  `locked` bit NOT NULL DEFAULT b'0',
  `allSampleQcPassed` bit DEFAULT b'0',
  `libraryPreparationComplete` bit DEFAULT b'0',
  `allLibraryQcPassed` bit DEFAULT b'0',
  `allPoolsConstructed` bit DEFAULT b'0',
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `allRunsCompleted` bit DEFAULT b'0',
  `primaryAnalysisCompleted` bit DEFAULT b'0',
  PRIMARY KEY (`overviewId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProjectOverview_Note`
--

DROP TABLE IF EXISTS `ProjectOverview_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `ProjectOverview_Note` (
  `overview_overviewId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`overview_overviewId`,`notes_noteId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Project_Issues`
--

DROP TABLE IF EXISTS `Project_Issues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Project_Issues` (
  `project_projectId` bigint NOT NULL,
  `issueKey` varchar(255) NOT NULL,
  PRIMARY KEY (`project_projectId`,`issueKey`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Project_Note`
--

DROP TABLE IF EXISTS `Project_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Project_Note` (
  `project_projectId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`project_projectId`,`notes_noteId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Project_ProjectOverview`
--

DROP TABLE IF EXISTS `Project_ProjectOverview`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Project_ProjectOverview` (
  `project_projectId` bigint NOT NULL,
  `overviews_overviewId` bigint NOT NULL,
  PRIMARY KEY (`project_projectId`,`overviews_overviewId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPRESSED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Project_Request`
--

DROP TABLE IF EXISTS `Project_Request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Project_Request` (
  `Project_projectId` bigint NOT NULL,
  `requests_requestId` bigint NOT NULL,
  UNIQUE KEY `requests_requestId` (`requests_requestId`),
  KEY `FKDA6E0B2925FFBF98` (`Project_projectId`),
  KEY `FKDA6E0B29B36A83EF` (`requests_requestId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Project_Study`
--

DROP TABLE IF EXISTS `Project_Study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Project_Study` (
  `Project_projectId` bigint NOT NULL,
  `studies_studyId` bigint NOT NULL,
  KEY `studyId` (`studies_studyId`) USING BTREE,
  KEY `projectId` (`Project_projectId`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QCType`
--

DROP TABLE IF EXISTS `QCType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `QCType` (
  `qcTypeId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `qcTarget` varchar(50) NOT NULL,
  `units` varchar(20) NOT NULL,
  PRIMARY KEY (`qcTypeId`)
) ENGINE=InnoDB   DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Request`
--

DROP TABLE IF EXISTS `Request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Request` (
  `requestId` bigint NOT NULL AUTO_INCREMENT,
  `creationDate` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `executionCount` int NOT NULL,
  `lastExecutionDate` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `protocolUniqueIdentifier` varchar(255) DEFAULT NULL,
  `project_projectId` bigint DEFAULT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  PRIMARY KEY (`requestId`),
  KEY `FKA4878A6F60F9CBA8` (`securityProfile_profileId`),
  KEY `FKA4878A6F25FFBF98` (`project_projectId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Request_Note`
--

DROP TABLE IF EXISTS `Request_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Request_Note` (
  `Request_requestId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  UNIQUE KEY `notes_noteId` (`notes_noteId`),
  KEY `FK57687FE2A7DC4D2C` (`notes_noteId`),
  KEY `FK57687FE2E8B554FA` (`Request_requestId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Run`
--

DROP TABLE IF EXISTS `Run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Run` (
  `runId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `accession` varchar(50) DEFAULT NULL,
  `platformRunId` int DEFAULT NULL,
  `pairedEnd` tinyint NOT NULL DEFAULT '0',
  `cycles` smallint DEFAULT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  `platformType` varchar(50) NOT NULL,
  `status_statusId` bigint DEFAULT NULL,
  `alias` varchar(255) DEFAULT NULL,
  `sequencerReference_sequencerReferenceId` bigint NOT NULL,
  PRIMARY KEY (`runId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunQC`
--

DROP TABLE IF EXISTS `RunQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunQC` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `run_runId` bigint NOT NULL,
  `qcUserName` varchar(255) NOT NULL,
  `qcDate` date NOT NULL,
  `qcMethod` bigint NOT NULL,
  `information` text,
  `doNotProcess` bit NOT NULL DEFAULT b'0',
  PRIMARY KEY (`qcId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunQC_Partition`
--

DROP TABLE IF EXISTS `RunQC_Partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunQC_Partition` (
  `runQc_runQcId` bigint NOT NULL,
  `containers_containerId` bigint NOT NULL DEFAULT '0',
  `partitionNumber` tinyint NOT NULL,
  PRIMARY KEY (`runQc_runQcId`,`containers_containerId`,`partitionNumber`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Run_Flowcell`
--

DROP TABLE IF EXISTS `Run_Flowcell`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Run_Flowcell` (
  `Run_runId` bigint NOT NULL,
  `flowcells_flowcellId` bigint NOT NULL,
  PRIMARY KEY (`Run_runId`,`flowcells_flowcellId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Run_Note`
--

DROP TABLE IF EXISTS `Run_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Run_Note` (
  `run_runId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`run_runId`,`notes_noteId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Run_SequencerPartitionContainer`
--

DROP TABLE IF EXISTS `Run_SequencerPartitionContainer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Run_SequencerPartitionContainer` (
  `Run_runId` bigint NOT NULL,
  `containers_containerId` bigint NOT NULL,
  PRIMARY KEY (`Run_runId`,`containers_containerId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Sample`
--

DROP TABLE IF EXISTS `Sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Sample` (
  `sampleId` bigint NOT NULL AUTO_INCREMENT,
  `accession` varchar(50) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `sampleType` varchar(50) NOT NULL,
  `receivedDate` date DEFAULT NULL,
  `qcPassed` varchar(5) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `project_projectId` bigint NOT NULL,
  `scientificName` varchar(255) NOT NULL,
  `taxonIdentifier` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sampleId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SampleQC`
--

DROP TABLE IF EXISTS `SampleQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleQC` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `sample_sampleId` bigint NOT NULL,
  `qcUserName` varchar(255) NOT NULL,
  `qcDate` date NOT NULL,
  `qcMethod` bigint DEFAULT NULL,
  `results` double DEFAULT NULL,
  PRIMARY KEY (`qcId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SampleType`
--

DROP TABLE IF EXISTS `SampleType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleType` (
  `typeId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`typeId`)
) ENGINE=InnoDB   DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Sample_Note`
--

DROP TABLE IF EXISTS `Sample_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Sample_Note` (
  `sample_sampleId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`sample_sampleId`,`notes_noteId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SecurityProfile`
--

DROP TABLE IF EXISTS `SecurityProfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `SecurityProfile` (
  `profileId` bigint NOT NULL AUTO_INCREMENT,
  `allowAllInternal` bit NOT NULL,
  `owner_userId` bigint DEFAULT NULL,
  PRIMARY KEY (`profileId`),
  KEY `FK18AEBA294140968C` (`owner_userId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SecurityProfile_ReadGroup`
--

DROP TABLE IF EXISTS `SecurityProfile_ReadGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `SecurityProfile_ReadGroup` (
  `SecurityProfile_profileId` bigint NOT NULL,
  `readGroup_groupId` bigint NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SecurityProfile_ReadUser`
--

DROP TABLE IF EXISTS `SecurityProfile_ReadUser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `SecurityProfile_ReadUser` (
  `SecurityProfile_profileId` bigint NOT NULL,
  `readUser_userId` bigint NOT NULL,
  KEY `FKD4CF504160F9CBA8` (`SecurityProfile_profileId`),
  KEY `FKD4CF504125267E4D` (`readUser_userId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SecurityProfile_WriteGroup`
--

DROP TABLE IF EXISTS `SecurityProfile_WriteGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `SecurityProfile_WriteGroup` (
  `SecurityProfile_profileId` bigint NOT NULL,
  `writeGroup_groupId` bigint NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SecurityProfile_WriteUser`
--

DROP TABLE IF EXISTS `SecurityProfile_WriteUser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `SecurityProfile_WriteUser` (
  `SecurityProfile_profileId` bigint NOT NULL,
  `writeUser_userId` bigint NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SequencerPartitionContainer`
--

DROP TABLE IF EXISTS `SequencerPartitionContainer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `SequencerPartitionContainer` (
  `containerId` bigint NOT NULL AUTO_INCREMENT,
  `securityProfile_profileId` bigint DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `platform` bigint DEFAULT NULL,
  `validationBarcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`containerId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SequencerPartitionContainer_Partition`
--

DROP TABLE IF EXISTS `SequencerPartitionContainer_Partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `SequencerPartitionContainer_Partition` (
  `container_containerId` bigint NOT NULL,
  `partitions_partitionId` bigint NOT NULL,
  PRIMARY KEY (`container_containerId`,`partitions_partitionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SequencerReference`
--

DROP TABLE IF EXISTS `SequencerReference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `SequencerReference` (
  `referenceId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `ipAddress` blob NOT NULL,
  `platformId` bigint NOT NULL,
  `available` bit NOT NULL DEFAULT b'0',
  PRIMARY KEY (`referenceId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `State_Key`
--

DROP TABLE IF EXISTS `State_Key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `State_Key` (
  `id` bigint NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `State_Value`
--

DROP TABLE IF EXISTS `State_Value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `State_Value` (
  `id` bigint NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Status`
--

DROP TABLE IF EXISTS `Status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Status` (
  `statusId` bigint NOT NULL AUTO_INCREMENT,
  `health` varchar(50) NOT NULL DEFAULT 'Unknown',
  `completionDate` date DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `instrumentName` varchar(255) NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `runName` varchar(255) NOT NULL,
  `xml` longblob,
  PRIMARY KEY (`statusId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Study`
--

DROP TABLE IF EXISTS `Study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Study` (
  `studyId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `accession` varchar(30) DEFAULT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  `project_projectId` bigint NOT NULL,
  `studyType` varchar(255) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`studyId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudyType`
--

DROP TABLE IF EXISTS `StudyType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `StudyType` (
  `typeId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`typeId`)
) ENGINE=InnoDB   DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Study_Experiment`
--

DROP TABLE IF EXISTS `Study_Experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Study_Experiment` (
  `Study_studyId` bigint NOT NULL,
  `experiments_experimentId` bigint NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Submission`
--

DROP TABLE IF EXISTS `Submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Submission` (
  `submissionId` bigint NOT NULL AUTO_INCREMENT,
  `creationDate` date NOT NULL,
  `submittedDate` date DEFAULT NULL,
  `verified` bit DEFAULT b'0',
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `accession` varchar(50) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `completed` bit DEFAULT b'0',
  PRIMARY KEY (`submissionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Submission_Chamber`
--

DROP TABLE IF EXISTS `Submission_Chamber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Submission_Chamber` (
  `submission_submissionId` bigint NOT NULL,
  `chambers_chamberId` bigint NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`chambers_chamberId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Submission_Experiment`
--

DROP TABLE IF EXISTS `Submission_Experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Submission_Experiment` (
  `submission_submissionId` bigint NOT NULL,
  `experiments_experimentId` bigint NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`experiments_experimentId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Submission_Lane`
--

DROP TABLE IF EXISTS `Submission_Lane`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Submission_Lane` (
  `submission_submissionId` bigint NOT NULL,
  `lanes_laneId` bigint NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`lanes_laneId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Submission_Partition`
--

DROP TABLE IF EXISTS `Submission_Partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Submission_Partition` (
  `submission_submissionId` bigint NOT NULL,
  `partitions_partitionId` bigint NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`partitions_partitionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Submission_Partition_Dilution`
--

DROP TABLE IF EXISTS `Submission_Partition_Dilution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Submission_Partition_Dilution` (
  `submission_submissionId` bigint NOT NULL,
  `partition_partitionId` bigint NOT NULL,
  `dilution_dilutionId` bigint NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`partition_partitionId`,`dilution_dilutionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Submission_Sample`
--

DROP TABLE IF EXISTS `Submission_Sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Submission_Sample` (
  `submission_submissionId` bigint NOT NULL,
  `samples_sampleId` bigint NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`samples_sampleId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Submission_Study`
--

DROP TABLE IF EXISTS `Submission_Study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Submission_Study` (
  `submission_submissionId` bigint NOT NULL,
  `studies_studyId` bigint NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`studies_studyId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TagBarcodes`
--

DROP TABLE IF EXISTS `TagBarcodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `TagBarcodes` (
  `tagId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `sequence` varchar(20) NOT NULL,
  `platformName` varchar(20) NOT NULL,
  `strategyName` varchar(100) NOT NULL,
  PRIMARY KEY (`tagId`)
) ENGINE=InnoDB   DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `User` (
  `userId` bigint NOT NULL AUTO_INCREMENT,
  `active` bit NOT NULL,
  `admin` bit NOT NULL,
  `external` bit NOT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `internal` bit NOT NULL,
  `loginName` varchar(255) DEFAULT NULL,
  `roles` blob,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB   DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `User_Group`
--

DROP TABLE IF EXISTS `User_Group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `User_Group` (
  `users_userId` bigint NOT NULL,
  `groups_groupId` bigint NOT NULL,
  KEY `FKE7B7ED0B94349B7F` (`groups_groupId`),
  KEY `FKE7B7ED0B749D8197` (`users_userId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Watcher`
--

DROP TABLE IF EXISTS `Watcher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Watcher` (
  `entityName` varchar(12) NOT NULL,
  `userId` bigint NOT NULL,
  PRIMARY KEY (`entityName`,`userId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Workflow`
--

DROP TABLE IF EXISTS `Workflow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Workflow` (
  `workflowId` bigint NOT NULL,
  `userId` bigint DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `completion_date` date DEFAULT NULL,
  `workflowDefinition_definitionId` bigint DEFAULT NULL,
  PRIMARY KEY (`workflowId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorkflowDefinition`
--

DROP TABLE IF EXISTS `WorkflowDefinition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkflowDefinition` (
  `workflowDefinitionId` bigint NOT NULL,
  `userId` bigint DEFAULT NULL,
  `creation_date` date DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`workflowDefinitionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorkflowDefinition_State`
--

DROP TABLE IF EXISTS `WorkflowDefinition_State`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkflowDefinition_State` (
  `workflowDefinitionId` bigint NOT NULL,
  `state_key` varchar(45) DEFAULT NULL,
  `required` tinyint DEFAULT NULL,
  PRIMARY KEY (`workflowDefinitionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorkflowDefinition_WorkflowProcessDefinition`
--

DROP TABLE IF EXISTS `WorkflowDefinition_WorkflowProcessDefinition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkflowDefinition_WorkflowProcessDefinition` (
  `workflowDefinitionId` bigint NOT NULL,
  `workflowProcessDefinitionId` bigint NOT NULL,
  PRIMARY KEY (`workflowDefinitionId`,`workflowProcessDefinitionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorkflowProcess`
--

DROP TABLE IF EXISTS `WorkflowProcess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkflowProcess` (
  `processId` bigint NOT NULL,
  `userId` bigint DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `completion_date` date DEFAULT NULL,
  `workflowProcessDefinition_definitionId` bigint DEFAULT NULL,
  PRIMARY KEY (`processId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorkflowProcessDefinition`
--

DROP TABLE IF EXISTS `WorkflowProcessDefinition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkflowProcessDefinition` (
  `workflowProcessDefinitionId` bigint NOT NULL,
  `userId` bigint DEFAULT NULL,
  `creation_date` date DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `order` tinyint DEFAULT NULL,
  `inputType` text,
  `outputType` text,
  PRIMARY KEY (`workflowProcessDefinitionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorkflowProcessDefinition_State`
--

DROP TABLE IF EXISTS `WorkflowProcessDefinition_State`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkflowProcessDefinition_State` (
  `workflowProcessDefinitionId` bigint NOT NULL,
  `state_key` varchar(45) DEFAULT NULL,
  `required` tinyint DEFAULT NULL,
  PRIMARY KEY (`workflowProcessDefinitionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorkflowProcess_State`
--

DROP TABLE IF EXISTS `WorkflowProcess_State`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkflowProcess_State` (
  `processId` bigint NOT NULL,
  `state_key_id` bigint NOT NULL,
  `state_value_id` bigint NOT NULL,
  PRIMARY KEY (`processId`,`state_value_id`,`state_key_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Workflow_State`
--

DROP TABLE IF EXISTS `Workflow_State`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Workflow_State` (
  `workflowId` bigint NOT NULL,
  `state_key_id` bigint NOT NULL,
  `state_value_id` bigint NOT NULL,
  PRIMARY KEY (`workflowId`,`state_key_id`,`state_value_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Workflow_WorkflowProcess`
--

DROP TABLE IF EXISTS `Workflow_WorkflowProcess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Workflow_WorkflowProcess` (
  `workflowId` bigint NOT NULL,
  `processId` bigint NOT NULL,
  PRIMARY KEY (`workflowId`,`processId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `_Group`
--

DROP TABLE IF EXISTS `_Group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `_Group` (
  `groupId` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`groupId`)
) ENGINE=InnoDB   DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `_Partition`
--

DROP TABLE IF EXISTS `_Partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `_Partition` (
  `partitionId` bigint NOT NULL AUTO_INCREMENT,
  `partitionNumber` tinyint NOT NULL,
  `pool_poolId` bigint DEFAULT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  PRIMARY KEY (`partitionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emPCR`
--

DROP TABLE IF EXISTS `emPCR`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `emPCR` (
  `pcrId` bigint NOT NULL AUTO_INCREMENT,
  `concentration` double NOT NULL,
  `dilution_dilutionId` bigint NOT NULL,
  `creationDate` date NOT NULL,
  `pcrUserName` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  PRIMARY KEY (`pcrId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emPCRDilution`
--

DROP TABLE IF EXISTS `emPCRDilution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `emPCRDilution` (
  `dilutionId` bigint NOT NULL AUTO_INCREMENT,
  `concentration` double NOT NULL,
  `emPCR_pcrId` bigint NOT NULL,
  `identificationBarcode` varchar(13) DEFAULT NULL,
  `creationDate` date NOT NULL,
  `dilutionUserName` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `securityProfile_profileId` bigint DEFAULT NULL,
  PRIMARY KEY (`dilutionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `persistent_logins`
--

DROP TABLE IF EXISTS `persistent_logins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-09-28 15:15:27
