-- MySQL dump 10.13  Distrib 8.0.36, for Linux (x86_64)
--
-- Host: localhost    Database: lims
-- ------------------------------------------------------
-- Server version	8.0.42-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Temporary view structure for view `ActivePlatformTypes`
--


CREATE DATABASE lims;

USE lims;

DROP TABLE IF EXISTS `ActivePlatformTypes`;
/*!50001 DROP VIEW IF EXISTS `ActivePlatformTypes`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `ActivePlatformTypes` AS SELECT 
 1 AS `platform`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `ApiKey`
--

DROP TABLE IF EXISTS `ApiKey`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ApiKey` (
  `keyId` bigint NOT NULL AUTO_INCREMENT,
  `userId` bigint NOT NULL,
  `apiKey` varchar(50) NOT NULL,
  `apiSecret` varchar(255) NOT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`keyId`),
  UNIQUE KEY `uk_apikey_key` (`apiKey`),
  KEY `fk_apikey_user` (`userId`),
  KEY `fk_apikey_creator` (`creator`),
  CONSTRAINT `fk_apikey_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_apikey_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Array`
--

DROP TABLE IF EXISTS `Array`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Array` (
  `arrayId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `arrayModelId` bigint NOT NULL,
  `serialNumber` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModifier` bigint NOT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`arrayId`),
  UNIQUE KEY `uk_array_alias` (`alias`),
  UNIQUE KEY `uk_array_serialNumber` (`serialNumber`),
  KEY `fk_array_model` (`arrayModelId`),
  KEY `fk_array_creator` (`creator`),
  KEY `fk_array_modifier` (`lastModifier`),
  CONSTRAINT `fk_array_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_array_model` FOREIGN KEY (`arrayModelId`) REFERENCES `ArrayModel` (`arrayModelId`),
  CONSTRAINT `fk_array_modifier` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ArrayChangeLog`
--

DROP TABLE IF EXISTS `ArrayChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ArrayChangeLog` (
  `arrayChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `arrayId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`arrayChangeLogId`),
  KEY `fk_arrayChangeLog_array` (`arrayId`),
  KEY `fk_arrayChangeLog_user` (`userId`),
  CONSTRAINT `fk_arrayChangeLog_array` FOREIGN KEY (`arrayId`) REFERENCES `Array` (`arrayId`) ON DELETE CASCADE,
  CONSTRAINT `fk_arrayChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=217 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ArrayModel`
--

DROP TABLE IF EXISTS `ArrayModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ArrayModel` (
  `arrayModelId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `arrayModelRows` tinyint unsigned DEFAULT NULL,
  `arrayModelColumns` tinyint unsigned DEFAULT NULL,
  PRIMARY KEY (`arrayModelId`),
  UNIQUE KEY `uk_arrayModel_alias` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ArrayPosition`
--

DROP TABLE IF EXISTS `ArrayPosition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ArrayPosition` (
  `arrayId` bigint NOT NULL,
  `position` varchar(6) NOT NULL,
  `sampleId` bigint NOT NULL,
  PRIMARY KEY (`arrayId`,`position`),
  KEY `fk_arrayPosition_sample` (`sampleId`),
  CONSTRAINT `fk_arrayPosition_array` FOREIGN KEY (`arrayId`) REFERENCES `Array` (`arrayId`),
  CONSTRAINT `fk_arrayPosition_sample` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ArrayRun`
--

DROP TABLE IF EXISTS `ArrayRun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ArrayRun` (
  `arrayRunId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `instrumentId` bigint NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  `arrayId` bigint DEFAULT NULL,
  `health` varchar(50) NOT NULL,
  `startDate` date DEFAULT NULL,
  `completionDate` date DEFAULT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModifier` bigint NOT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`arrayRunId`),
  UNIQUE KEY `uk_arrayRun_alias` (`alias`),
  KEY `fk_arrayRun_instrument` (`instrumentId`),
  KEY `fk_arrayRun_array` (`arrayId`),
  KEY `fk_arrayRun_creator` (`creator`),
  KEY `fk_arrayRun_modifier` (`lastModifier`),
  KEY `startDate_ArrayRun` (`startDate`),
  CONSTRAINT `fk_arrayRun_array` FOREIGN KEY (`arrayId`) REFERENCES `Array` (`arrayId`),
  CONSTRAINT `fk_arrayRun_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_arrayRun_instrument` FOREIGN KEY (`instrumentId`) REFERENCES `Instrument` (`instrumentId`),
  CONSTRAINT `fk_arrayRun_modifier` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ArrayRunChangeLog`
--

DROP TABLE IF EXISTS `ArrayRunChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ArrayRunChangeLog` (
  `arrayRunChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `arrayRunId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`arrayRunChangeLogId`),
  KEY `fk_arrayRunChangeLog_arrayRun` (`arrayRunId`),
  KEY `fk_arrayRunChangeLog_user` (`userId`),
  CONSTRAINT `fk_arrayRunChangeLog_arrayRun` FOREIGN KEY (`arrayRunId`) REFERENCES `ArrayRun` (`arrayRunId`) ON DELETE CASCADE,
  CONSTRAINT `fk_arrayRunChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ArrayRun_Attachment`
--

DROP TABLE IF EXISTS `ArrayRun_Attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ArrayRun_Attachment` (
  `arrayRunId` bigint NOT NULL,
  `attachmentId` bigint NOT NULL,
  PRIMARY KEY (`arrayRunId`,`attachmentId`),
  KEY `fk_arrayrun_attachment` (`attachmentId`),
  CONSTRAINT `fk_arrayrun_attachment` FOREIGN KEY (`attachmentId`) REFERENCES `Attachment` (`attachmentId`),
  CONSTRAINT `fk_attachment_arrayrun` FOREIGN KEY (`arrayRunId`) REFERENCES `ArrayRun` (`arrayRunId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Assay`
--

DROP TABLE IF EXISTS `Assay`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Assay` (
  `assayId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(50) NOT NULL,
  `version` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  `caseTargetDays` smallint DEFAULT NULL,
  `receiptTargetDays` smallint DEFAULT NULL,
  `extractionTargetDays` smallint DEFAULT NULL,
  `libraryPreparationTargetDays` smallint DEFAULT NULL,
  `libraryQualificationTargetDays` smallint DEFAULT NULL,
  `fullDepthSequencingTargetDays` smallint DEFAULT NULL,
  `analysisReviewTargetDays` smallint DEFAULT NULL,
  `releaseApprovalTargetDays` smallint DEFAULT NULL,
  `releaseTargetDays` smallint DEFAULT NULL,
  PRIMARY KEY (`assayId`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AssayTest`
--

DROP TABLE IF EXISTS `AssayTest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AssayTest` (
  `testId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(50) NOT NULL,
  `tissueTypeId` bigint DEFAULT NULL,
  `negateTissueType` tinyint(1) NOT NULL DEFAULT '0',
  `extractionClassId` bigint DEFAULT NULL,
  `libraryDesignCodeId` bigint DEFAULT NULL,
  `libraryQualificationMethod` varchar(25) NOT NULL,
  `libraryQualificationDesignCodeId` bigint DEFAULT NULL,
  `repeatPerTimepoint` tinyint(1) NOT NULL DEFAULT '0',
  `permittedSamples` varchar(20) NOT NULL DEFAULT 'ALL',
  `tissueOriginId` bigint DEFAULT NULL,
  `negateTissueOrigin` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`testId`),
  UNIQUE KEY `uk_assayTest_alias` (`alias`),
  KEY `fk_assayTest_tissueType` (`tissueTypeId`),
  KEY `fk_assayTest_libraryDesignCode` (`libraryDesignCodeId`),
  KEY `fk_assayTest_qualificationDesignCode` (`libraryQualificationDesignCodeId`),
  KEY `fk_assayTest_tissueOrigin` (`tissueOriginId`),
  CONSTRAINT `fk_assayTest_libraryDesignCode` FOREIGN KEY (`libraryDesignCodeId`) REFERENCES `LibraryDesignCode` (`libraryDesignCodeId`),
  CONSTRAINT `fk_assayTest_qualificationDesignCode` FOREIGN KEY (`libraryQualificationDesignCodeId`) REFERENCES `LibraryDesignCode` (`libraryDesignCodeId`),
  CONSTRAINT `fk_assayTest_tissueOrigin` FOREIGN KEY (`tissueOriginId`) REFERENCES `TissueOrigin` (`tissueOriginId`),
  CONSTRAINT `fk_assayTest_tissueType` FOREIGN KEY (`tissueTypeId`) REFERENCES `TissueType` (`tissueTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Assay_AssayTest`
--

DROP TABLE IF EXISTS `Assay_AssayTest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Assay_AssayTest` (
  `assayId` bigint NOT NULL,
  `testId` bigint NOT NULL,
  PRIMARY KEY (`assayId`,`testId`),
  KEY `fk_assay_assayTest` (`testId`),
  CONSTRAINT `fk_assay_assayTest` FOREIGN KEY (`testId`) REFERENCES `AssayTest` (`testId`),
  CONSTRAINT `fk_assayTest_assay` FOREIGN KEY (`assayId`) REFERENCES `Assay` (`assayId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Assay_Metric`
--

DROP TABLE IF EXISTS `Assay_Metric`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Assay_Metric` (
  `assayId` bigint NOT NULL,
  `metricId` bigint NOT NULL,
  `minimumThreshold` decimal(13,3) DEFAULT NULL,
  `maximumThreshold` decimal(13,3) DEFAULT NULL,
  PRIMARY KEY (`assayId`,`metricId`),
  KEY `fk_assay_metric` (`metricId`),
  CONSTRAINT `fk_assay_metric` FOREIGN KEY (`metricId`) REFERENCES `Metric` (`metricId`),
  CONSTRAINT `fk_metric_assay` FOREIGN KEY (`assayId`) REFERENCES `Assay` (`assayId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Attachment`
--

DROP TABLE IF EXISTS `Attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Attachment` (
  `attachmentId` bigint NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) NOT NULL,
  `path` varchar(4096) NOT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `categoryId` bigint DEFAULT NULL,
  PRIMARY KEY (`attachmentId`),
  KEY `fk_attachment_creator` (`creator`),
  KEY `fk_attachment_category` (`categoryId`),
  CONSTRAINT `fk_attachment_category` FOREIGN KEY (`categoryId`) REFERENCES `AttachmentCategory` (`categoryId`),
  CONSTRAINT `fk_attachment_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=12559 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AttachmentCategory`
--

DROP TABLE IF EXISTS `AttachmentCategory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AttachmentCategory` (
  `categoryId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  PRIMARY KEY (`categoryId`),
  UNIQUE KEY `uk_attachmentCategory_alias` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `AttachmentUsage`
--

DROP TABLE IF EXISTS `AttachmentUsage`;
/*!50001 DROP VIEW IF EXISTS `AttachmentUsage`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `AttachmentUsage` AS SELECT 
 1 AS `attachmentId`,
 1 AS `usage`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `BarcodableView`
--

DROP TABLE IF EXISTS `BarcodableView`;
/*!50001 DROP VIEW IF EXISTS `BarcodableView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `BarcodableView` AS SELECT 
 1 AS `targetId`,
 1 AS `identificationBarcode`,
 1 AS `name`,
 1 AS `alias`,
 1 AS `targetType`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `Box`
--

DROP TABLE IF EXISTS `Box`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Box` (
  `boxId` bigint NOT NULL AUTO_INCREMENT,
  `boxSizeId` bigint NOT NULL,
  `boxUseId` bigint NOT NULL,
  `name` varchar(255) NOT NULL,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `lastModifier` bigint NOT NULL DEFAULT '1',
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `locationId` bigint DEFAULT NULL,
  PRIMARY KEY (`boxId`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `alias` (`alias`),
  UNIQUE KEY `identificationBarcode` (`identificationBarcode`),
  KEY `fk_box_boxSize` (`boxSizeId`),
  KEY `fk_box_boxUse` (`boxUseId`),
  KEY `fk_box_lastModifier_user` (`lastModifier`),
  KEY `fk_box_creator` (`creator`),
  KEY `fk_box_location` (`locationId`),
  KEY `name_Box` (`name`),
  CONSTRAINT `fk_box_boxSize` FOREIGN KEY (`boxSizeId`) REFERENCES `BoxSize` (`boxSizeId`),
  CONSTRAINT `fk_box_boxUse` FOREIGN KEY (`boxUseId`) REFERENCES `BoxUse` (`boxUseId`),
  CONSTRAINT `fk_box_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_box_lastModifier_user` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_box_location` FOREIGN KEY (`locationId`) REFERENCES `StorageLocation` (`locationId`)
) ENGINE=InnoDB AUTO_INCREMENT=8643 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BoxChangeLog`
--

DROP TABLE IF EXISTS `BoxChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BoxChangeLog` (
  `boxChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `boxId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`boxChangeLogId`),
  KEY `fk_boxChangeLog_box` (`boxId`),
  KEY `fk_boxChangeLog_user` (`userId`),
  KEY `BoxChangeLogDerivedInfo` (`boxId`,`changeTime`),
  CONSTRAINT `fk_boxChangeLog_box` FOREIGN KEY (`boxId`) REFERENCES `Box` (`boxId`) ON DELETE CASCADE,
  CONSTRAINT `fk_boxChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=796217 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BoxPosition`
--

DROP TABLE IF EXISTS `BoxPosition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BoxPosition` (
  `boxId` bigint NOT NULL,
  `targetId` bigint NOT NULL,
  `targetType` varchar(50) NOT NULL,
  `position` varchar(3) NOT NULL,
  PRIMARY KEY (`boxId`,`targetId`,`targetType`),
  UNIQUE KEY `box_unique_item` (`targetId`,`targetType`),
  UNIQUE KEY `box_single_occupancy` (`boxId`,`position`),
  CONSTRAINT `boxcontents_box_boxId` FOREIGN KEY (`boxId`) REFERENCES `Box` (`boxId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BoxSize`
--

DROP TABLE IF EXISTS `BoxSize`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BoxSize` (
  `boxSizeId` bigint NOT NULL AUTO_INCREMENT,
  `boxSizeRows` bigint NOT NULL,
  `boxSizeColumns` bigint NOT NULL,
  `scannable` tinyint(1) NOT NULL DEFAULT '0',
  `boxType` varchar(20) NOT NULL DEFAULT 'STORAGE',
  PRIMARY KEY (`boxSizeId`),
  UNIQUE KEY `boxSizeRows` (`boxSizeRows`,`boxSizeColumns`,`scannable`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BoxUse`
--

DROP TABLE IF EXISTS `BoxUse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BoxUse` (
  `boxUseId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  PRIMARY KEY (`boxUseId`),
  UNIQUE KEY `alias` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Contact`
--

DROP TABLE IF EXISTS `Contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Contact` (
  `contactId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  PRIMARY KEY (`contactId`),
  UNIQUE KEY `uk_contact_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=303 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ContactRole`
--

DROP TABLE IF EXISTS `ContactRole`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ContactRole` (
  `contactRoleId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`contactRoleId`),
  UNIQUE KEY `UQ_contactRole_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ContainerQC`
--

DROP TABLE IF EXISTS `ContainerQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ContainerQC` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `containerId` bigint NOT NULL,
  `creator` bigint NOT NULL,
  `date` date NOT NULL,
  `type` bigint DEFAULT NULL,
  `results` decimal(16,10) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL,
  `instrumentId` bigint DEFAULT NULL,
  `kitLot` varchar(50) DEFAULT NULL,
  `kitDescriptorId` bigint DEFAULT NULL,
  PRIMARY KEY (`qcId`),
  KEY `FK_ContainerQC_Container` (`containerId`),
  KEY `FK_ContainerQC_Creator` (`creator`),
  KEY `fk_containerQc_instrument` (`instrumentId`),
  KEY `fk_containerQc_kit` (`kitDescriptorId`),
  CONSTRAINT `FK_ContainerQC_Container` FOREIGN KEY (`containerId`) REFERENCES `SequencerPartitionContainer` (`containerId`),
  CONSTRAINT `FK_ContainerQC_Creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_containerQc_instrument` FOREIGN KEY (`instrumentId`) REFERENCES `Instrument` (`instrumentId`),
  CONSTRAINT `fk_containerQc_kit` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ContainerQcControl`
--

DROP TABLE IF EXISTS `ContainerQcControl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ContainerQcControl` (
  `qcControlId` bigint NOT NULL AUTO_INCREMENT,
  `qcId` bigint NOT NULL,
  `controlId` bigint NOT NULL,
  `lot` varchar(50) NOT NULL,
  `qcPassed` tinyint(1) NOT NULL,
  PRIMARY KEY (`qcControlId`),
  KEY `fk_containerQcControl_qc` (`qcId`),
  KEY `fk_containerQcControl_control` (`controlId`),
  CONSTRAINT `fk_containerQcControl_control` FOREIGN KEY (`controlId`) REFERENCES `QcControl` (`controlId`),
  CONSTRAINT `fk_containerQcControl_qc` FOREIGN KEY (`qcId`) REFERENCES `ContainerQC` (`qcId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Deletion`
--

DROP TABLE IF EXISTS `Deletion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Deletion` (
  `deletionId` bigint NOT NULL AUTO_INCREMENT,
  `targetType` varchar(50) NOT NULL,
  `targetId` bigint NOT NULL,
  `description` varchar(255) NOT NULL,
  `userId` bigint NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`deletionId`),
  KEY `fk_deletion_user` (`userId`),
  CONSTRAINT `fk_deletion_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=31301 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Deliverable`
--

DROP TABLE IF EXISTS `Deliverable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Deliverable` (
  `deliverableId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `analysisReviewRequired` tinyint(1) NOT NULL DEFAULT '1',
  `categoryId` bigint NOT NULL,
  PRIMARY KEY (`deliverableId`),
  UNIQUE KEY `UQ_deliverable_name` (`name`),
  KEY `fk_deliverable_category` (`categoryId`),
  CONSTRAINT `fk_deliverable_category` FOREIGN KEY (`categoryId`) REFERENCES `DeliverableCategory` (`categoryId`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DeliverableCategory`
--

DROP TABLE IF EXISTS `DeliverableCategory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DeliverableCategory` (
  `categoryId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`categoryId`),
  UNIQUE KEY `uk_deliverableCategory_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DetailedLibraryTemplate`
--

DROP TABLE IF EXISTS `DetailedLibraryTemplate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DetailedLibraryTemplate` (
  `libraryTemplateId` bigint NOT NULL,
  `libraryDesignId` bigint DEFAULT NULL,
  `libraryDesignCodeId` bigint DEFAULT NULL,
  PRIMARY KEY (`libraryTemplateId`),
  KEY `fk_detailedLibraryTemplate_design` (`libraryDesignId`),
  KEY `fk_detailedLibraryTemplate_designCode` (`libraryDesignCodeId`),
  CONSTRAINT `fk_detailedLibraryTemplate_design` FOREIGN KEY (`libraryDesignId`) REFERENCES `LibraryDesign` (`libraryDesignId`),
  CONSTRAINT `fk_detailedLibraryTemplate_designCode` FOREIGN KEY (`libraryDesignCodeId`) REFERENCES `LibraryDesignCode` (`libraryDesignCodeId`),
  CONSTRAINT `fk_detailedLibraryTemplate_libraryTemplate` FOREIGN KEY (`libraryTemplateId`) REFERENCES `LibraryTemplate` (`libraryTemplateId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DetailedQcStatus`
--

DROP TABLE IF EXISTS `DetailedQcStatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DetailedQcStatus` (
  `detailedQcStatusId` bigint NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  `noteRequired` bit(1) NOT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`detailedQcStatusId`),
  UNIQUE KEY `uk_detailedQcStatus_description` (`description`),
  KEY `FK82obpt4ig4g20eycits1ss1am` (`createdBy`),
  KEY `FK8xn9wkmnf09k06en6m91g5ks3` (`updatedBy`),
  CONSTRAINT `FK82obpt4ig4g20eycits1ss1am` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK8xn9wkmnf09k06en6m91g5ks3` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Experiment`
--

DROP TABLE IF EXISTS `Experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Experiment` (
  `experimentId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `accession` varchar(30) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `study_studyId` bigint DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `instrumentModelId` bigint NOT NULL,
  `lastModifier` bigint NOT NULL DEFAULT '1',
  `library_libraryId` bigint DEFAULT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`experimentId`),
  UNIQUE KEY `name` (`name`),
  KEY `experiment_user_userid_fkey` (`lastModifier`),
  KEY `experiment_library_libraryId_fkey` (`library_libraryId`),
  KEY `fk_experiment_creator` (`creator`),
  KEY `fk_experiment_instrumentModel` (`instrumentModelId`),
  KEY `fk_experiment_study` (`study_studyId`),
  CONSTRAINT `experiment_library_libraryId_fkey` FOREIGN KEY (`library_libraryId`) REFERENCES `Library` (`libraryId`),
  CONSTRAINT `fk_experiment_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_experiment_instrumentModel` FOREIGN KEY (`instrumentModelId`) REFERENCES `InstrumentModel` (`instrumentModelId`),
  CONSTRAINT `fk_experiment_lastModifier` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_experiment_study` FOREIGN KEY (`study_studyId`) REFERENCES `Study` (`studyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ExperimentChangeLog`
--

DROP TABLE IF EXISTS `ExperimentChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ExperimentChangeLog` (
  `experimentChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `experimentId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`experimentChangeLogId`),
  KEY `fk_experimentChangeLog_experiment` (`experimentId`),
  KEY `fk_experimentChangeLog_user` (`userId`),
  KEY `ExperimentChangeLogDerivedInfo` (`experimentId`,`changeTime`),
  CONSTRAINT `fk_experimentChangeLog_experiment` FOREIGN KEY (`experimentId`) REFERENCES `Experiment` (`experimentId`) ON DELETE CASCADE,
  CONSTRAINT `fk_experimentChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Experiment_Kit`
--

DROP TABLE IF EXISTS `Experiment_Kit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Experiment_Kit` (
  `experiments_experimentId` bigint NOT NULL,
  `kits_kitId` bigint NOT NULL,
  PRIMARY KEY (`experiments_experimentId`,`kits_kitId`),
  KEY `fk_experiment_kit` (`kits_kitId`),
  CONSTRAINT `fk_experiment_kit` FOREIGN KEY (`kits_kitId`) REFERENCES `Kit` (`kitId`),
  CONSTRAINT `fk_experiment_kit_experiment` FOREIGN KEY (`experiments_experimentId`) REFERENCES `Experiment` (`experimentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Experiment_Run_Partition`
--

DROP TABLE IF EXISTS `Experiment_Run_Partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Experiment_Run_Partition` (
  `experiment_experimentId` bigint NOT NULL,
  `run_runId` bigint NOT NULL,
  `partition_partitionId` bigint NOT NULL,
  PRIMARY KEY (`experiment_experimentId`,`run_runId`,`partition_partitionId`),
  KEY `experiment_run_partition_runId_fkey` (`run_runId`),
  KEY `experiment_run_partition_partitionId_fkey` (`partition_partitionId`),
  CONSTRAINT `experiment_run_partition_experimentId_fkey` FOREIGN KEY (`experiment_experimentId`) REFERENCES `Experiment` (`experimentId`),
  CONSTRAINT `experiment_run_partition_partitionId_fkey` FOREIGN KEY (`partition_partitionId`) REFERENCES `_Partition` (`partitionId`),
  CONSTRAINT `experiment_run_partition_runId_fkey` FOREIGN KEY (`run_runId`) REFERENCES `Run` (`runId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Instrument`
--

DROP TABLE IF EXISTS `Instrument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Instrument` (
  `instrumentId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `instrumentModelId` bigint NOT NULL,
  `serialNumber` varchar(30) DEFAULT NULL,
  `dateCommissioned` date DEFAULT NULL,
  `dateDecommissioned` date DEFAULT NULL,
  `upgradedInstrumentId` bigint DEFAULT NULL,
  `defaultPurposeId` bigint DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `workstationId` bigint DEFAULT NULL,
  PRIMARY KEY (`instrumentId`),
  UNIQUE KEY `uk_sequencer_name` (`name`),
  UNIQUE KEY `upgraded_SR_UK` (`upgradedInstrumentId`),
  UNIQUE KEY `uk_instrument_identificationBarcode` (`identificationBarcode`),
  KEY `fk_sequencerReference_platform` (`instrumentModelId`),
  KEY `name_Instrument` (`name`),
  KEY `instrument_defaultPurpose` (`defaultPurposeId`),
  KEY `fk_instrument_workstation` (`workstationId`),
  CONSTRAINT `fk_instrument_instrumentModel` FOREIGN KEY (`instrumentModelId`) REFERENCES `InstrumentModel` (`instrumentModelId`),
  CONSTRAINT `fk_instrument_upgradedInstrument` FOREIGN KEY (`upgradedInstrumentId`) REFERENCES `Instrument` (`instrumentId`),
  CONSTRAINT `fk_instrument_workstation` FOREIGN KEY (`workstationId`) REFERENCES `Workstation` (`workstationId`),
  CONSTRAINT `instrument_defaultPurpose` FOREIGN KEY (`defaultPurposeId`) REFERENCES `RunPurpose` (`purposeId`)
) ENGINE=InnoDB AUTO_INCREMENT=638 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `InstrumentModel`
--

DROP TABLE IF EXISTS `InstrumentModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `InstrumentModel` (
  `instrumentModelId` bigint NOT NULL AUTO_INCREMENT,
  `platform` varchar(50) NOT NULL,
  `alias` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `numContainers` tinyint NOT NULL,
  `instrumentType` varchar(50) NOT NULL,
  `dataManglingPolicy` varchar(50) DEFAULT 'NONE',
  PRIMARY KEY (`instrumentModelId`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `InstrumentPosition`
--

DROP TABLE IF EXISTS `InstrumentPosition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `InstrumentPosition` (
  `positionId` bigint NOT NULL AUTO_INCREMENT,
  `instrumentModelId` bigint NOT NULL,
  `alias` varchar(10) NOT NULL,
  PRIMARY KEY (`positionId`),
  KEY `fk_instrumentPosition_instrumentModel` (`instrumentModelId`),
  CONSTRAINT `fk_instrumentPosition_instrumentModel` FOREIGN KEY (`instrumentModelId`) REFERENCES `InstrumentModel` (`instrumentModelId`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `InstrumentStatusPositionRunPoolView`
--

DROP TABLE IF EXISTS `InstrumentStatusPositionRunPoolView`;
/*!50001 DROP VIEW IF EXISTS `InstrumentStatusPositionRunPoolView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `InstrumentStatusPositionRunPoolView` AS SELECT 
 1 AS `runId`,
 1 AS `positionId`,
 1 AS `partitionId`,
 1 AS `poolId`,
 1 AS `name`,
 1 AS `alias`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `InstrumentStatusPositionRunView`
--

DROP TABLE IF EXISTS `InstrumentStatusPositionRunView`;
/*!50001 DROP VIEW IF EXISTS `InstrumentStatusPositionRunView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `InstrumentStatusPositionRunView` AS SELECT 
 1 AS `runId`,
 1 AS `name`,
 1 AS `alias`,
 1 AS `instrumentId`,
 1 AS `health`,
 1 AS `startDate`,
 1 AS `completionDate`,
 1 AS `lastModified`,
 1 AS `positionId`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `InstrumentStatusPositionView`
--

DROP TABLE IF EXISTS `InstrumentStatusPositionView`;
/*!50001 DROP VIEW IF EXISTS `InstrumentStatusPositionView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `InstrumentStatusPositionView` AS SELECT 
 1 AS `instrumentId`,
 1 AS `positionId`,
 1 AS `alias`,
 1 AS `outOfServiceTime`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `InstrumentStatusView`
--

DROP TABLE IF EXISTS `InstrumentStatusView`;
/*!50001 DROP VIEW IF EXISTS `InstrumentStatusView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `InstrumentStatusView` AS SELECT 
 1 AS `instrumentId`,
 1 AS `name`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `Instrument_ServiceRecord`
--

DROP TABLE IF EXISTS `Instrument_ServiceRecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Instrument_ServiceRecord` (
  `recordId` bigint NOT NULL,
  `instrumentId` bigint NOT NULL,
  PRIMARY KEY (`recordId`,`instrumentId`),
  KEY `fk_instrumentServiceRecord_instrument` (`instrumentId`),
  CONSTRAINT `fk_instrumentServiceRecord_instrument` FOREIGN KEY (`instrumentId`) REFERENCES `Instrument` (`instrumentId`),
  CONSTRAINT `fk_instrumentServiceRecord_ServiceRecord` FOREIGN KEY (`recordId`) REFERENCES `ServiceRecord` (`recordId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Kit`
--

DROP TABLE IF EXISTS `Kit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Kit` (
  `kitId` bigint NOT NULL AUTO_INCREMENT,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `lotNumber` varchar(30) NOT NULL,
  `kitDate` date NOT NULL,
  `kitDescriptorId` bigint NOT NULL,
  PRIMARY KEY (`kitId`),
  KEY `kit_kitDescriptor_fkey` (`kitDescriptorId`),
  CONSTRAINT `kit_kitDescriptor_fkey` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `KitDescriptor`
--

DROP TABLE IF EXISTS `KitDescriptor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `KitDescriptor` (
  `kitDescriptorId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `version` int DEFAULT NULL,
  `manufacturer` varchar(100) NOT NULL,
  `partNumber` varchar(50) NOT NULL,
  `stockLevel` int NOT NULL DEFAULT '0',
  `kitType` varchar(30) NOT NULL,
  `platformType` varchar(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `lastModifier` bigint NOT NULL DEFAULT '1',
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`kitDescriptorId`),
  UNIQUE KEY `uk_kitDescriptor_name` (`name`),
  KEY `kitdescriptor_user_userid_fkey` (`lastModifier`),
  KEY `fk_kitDescriptor_creator` (`creator`),
  KEY `name_KitDescriptor` (`name`),
  CONSTRAINT `fk_kitDescriptor_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `kitdescriptor_user_userid_fkey` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=280 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `KitDescriptorChangeLog`
--

DROP TABLE IF EXISTS `KitDescriptorChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `KitDescriptorChangeLog` (
  `kitDescriptorChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `kitDescriptorId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`kitDescriptorChangeLogId`),
  KEY `fk_kitDescriptorChangeLog_kitDescriptor` (`kitDescriptorId`),
  KEY `fk_kitDescriptorChangeLog_user` (`userId`),
  KEY `KitDescriptorChangeLogDerivedInfo` (`kitDescriptorId`,`changeTime`),
  CONSTRAINT `fk_kitDescriptorChangeLog_kitDescriptor` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`) ON DELETE CASCADE,
  CONSTRAINT `fk_kitDescriptorChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=280 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Kit_Note`
--

DROP TABLE IF EXISTS `Kit_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Kit_Note` (
  `kit_kitId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`kit_kitId`,`notes_noteId`),
  KEY `KitNote_Note_FK` (`notes_noteId`),
  CONSTRAINT `KitNote_Kit_FK` FOREIGN KEY (`kit_kitId`) REFERENCES `Kit` (`kitId`),
  CONSTRAINT `KitNote_Note_FK` FOREIGN KEY (`notes_noteId`) REFERENCES `Note` (`noteId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Lab`
--

DROP TABLE IF EXISTS `Lab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Lab` (
  `labId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `excludeFromPinery` tinyint(1) NOT NULL DEFAULT '0',
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`labId`),
  UNIQUE KEY `uk_lab_alias` (`alias`),
  KEY `lab_createUser_fkey` (`createdBy`),
  KEY `lab_updateUser_fkey` (`updatedBy`),
  CONSTRAINT `lab_createUser_fkey` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `lab_updateUser_fkey` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Library`
--

DROP TABLE IF EXISTS `Library`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Library` (
  `libraryId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `accession` varchar(30) DEFAULT NULL,
  `sample_sampleId` bigint NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `libraryType` bigint DEFAULT NULL,
  `concentration` decimal(14,10) DEFAULT NULL,
  `creationDate` date DEFAULT NULL,
  `platformType` varchar(255) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `paired` tinyint(1) NOT NULL DEFAULT '0',
  `librarySelectionType` bigint DEFAULT NULL,
  `libraryStrategyType` bigint DEFAULT NULL,
  `lastModifier` bigint NOT NULL DEFAULT '1',
  `discarded` tinyint(1) NOT NULL DEFAULT '0',
  `volume` decimal(16,10) DEFAULT NULL,
  `lowQuality` tinyint(1) NOT NULL DEFAULT '0',
  `dnaSize` bigint DEFAULT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `kitDescriptorId` bigint DEFAULT NULL,
  `concentrationUnits` varchar(30) DEFAULT NULL,
  `volumeUnits` varchar(30) DEFAULT NULL,
  `spikeInId` bigint DEFAULT NULL,
  `spikeInDilutionFactor` varchar(50) DEFAULT NULL,
  `spikeInVolume` decimal(14,10) DEFAULT NULL,
  `initialVolume` decimal(16,10) DEFAULT NULL,
  `volumeUsed` decimal(16,10) DEFAULT NULL,
  `ngUsed` decimal(14,10) DEFAULT NULL,
  `umis` tinyint(1) NOT NULL DEFAULT '0',
  `thermalCyclerId` bigint DEFAULT NULL,
  `workstationId` bigint DEFAULT NULL,
  `kitLot` varchar(255) DEFAULT NULL,
  `discriminator` varchar(50) NOT NULL DEFAULT 'Library',
  `archived` bit(1) DEFAULT NULL,
  `libraryDesign` bigint DEFAULT NULL,
  `nonStandardAlias` tinyint(1) DEFAULT NULL,
  `preMigrationId` bigint DEFAULT NULL,
  `libraryDesignCodeId` bigint DEFAULT NULL,
  `groupId` varchar(100) DEFAULT NULL,
  `groupDescription` varchar(255) DEFAULT NULL,
  `sopId` bigint DEFAULT NULL,
  `detailedQcStatusId` bigint DEFAULT NULL,
  `detailedQcStatusNote` varchar(500) DEFAULT NULL,
  `qcUser` bigint DEFAULT NULL,
  `qcDate` date DEFAULT NULL,
  `index1Id` bigint DEFAULT NULL,
  `index2Id` bigint DEFAULT NULL,
  `requisitionId` bigint DEFAULT NULL,
  PRIMARY KEY (`libraryId`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `identificationBarcode` (`identificationBarcode`),
  UNIQUE KEY `uk_library_preMigrationId` (`preMigrationId`),
  KEY `Library_sampleId_libraryId` (`sample_sampleId`,`libraryId`),
  KEY `fk_library_libraryType` (`libraryType`),
  KEY `fk_library_librarySelectionType` (`librarySelectionType`),
  KEY `fk_library_libraryStrategyType` (`libraryStrategyType`),
  KEY `fk_library_lastModifier_user` (`lastModifier`),
  KEY `fk_library_creator` (`creator`),
  KEY `library_kitDescriptor_fkey` (`kitDescriptorId`),
  KEY `lastModified_Library` (`lastModified`),
  KEY `fk_library_spikeIn` (`spikeInId`),
  KEY `fk_library_thermalCycler` (`thermalCyclerId`),
  KEY `fk_library_workstation` (`workstationId`),
  KEY `fk_library_libraryDesignCode` (`libraryDesignCodeId`),
  KEY `fk_library_libraryDesign` (`libraryDesign`),
  KEY `fk_library_sop` (`sopId`),
  KEY `fk_library_detailedQcStatus` (`detailedQcStatusId`),
  KEY `fk_library_qcUser` (`qcUser`),
  KEY `fk_library_index1` (`index1Id`),
  KEY `fk_library_index2` (`index2Id`),
  KEY `fk_library_requisition` (`requisitionId`),
  CONSTRAINT `fk_library_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_library_detailedQcStatus` FOREIGN KEY (`detailedQcStatusId`) REFERENCES `DetailedQcStatus` (`detailedQcStatusId`),
  CONSTRAINT `fk_library_index1` FOREIGN KEY (`index1Id`) REFERENCES `LibraryIndex` (`indexId`),
  CONSTRAINT `fk_library_index2` FOREIGN KEY (`index2Id`) REFERENCES `LibraryIndex` (`indexId`),
  CONSTRAINT `fk_library_lastModifier_user` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_library_libraryDesign` FOREIGN KEY (`libraryDesign`) REFERENCES `LibraryDesign` (`libraryDesignId`),
  CONSTRAINT `fk_library_libraryDesignCode` FOREIGN KEY (`libraryDesignCodeId`) REFERENCES `LibraryDesignCode` (`libraryDesignCodeId`),
  CONSTRAINT `fk_library_librarySelectionType` FOREIGN KEY (`librarySelectionType`) REFERENCES `LibrarySelectionType` (`librarySelectionTypeId`),
  CONSTRAINT `fk_library_libraryStrategyType` FOREIGN KEY (`libraryStrategyType`) REFERENCES `LibraryStrategyType` (`libraryStrategyTypeId`),
  CONSTRAINT `fk_library_libraryType` FOREIGN KEY (`libraryType`) REFERENCES `LibraryType` (`libraryTypeId`),
  CONSTRAINT `fk_library_qcUser` FOREIGN KEY (`qcUser`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_library_requisition` FOREIGN KEY (`requisitionId`) REFERENCES `Requisition` (`requisitionId`),
  CONSTRAINT `fk_library_sample` FOREIGN KEY (`sample_sampleId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `fk_library_sop` FOREIGN KEY (`sopId`) REFERENCES `Sop` (`sopId`),
  CONSTRAINT `fk_library_spikeIn` FOREIGN KEY (`spikeInId`) REFERENCES `LibrarySpikeIn` (`spikeInId`),
  CONSTRAINT `fk_library_thermalCycler` FOREIGN KEY (`thermalCyclerId`) REFERENCES `Instrument` (`instrumentId`),
  CONSTRAINT `fk_library_workstation` FOREIGN KEY (`workstationId`) REFERENCES `Workstation` (`workstationId`),
  CONSTRAINT `library_kitDescriptor_fkey` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`)
) ENGINE=InnoDB AUTO_INCREMENT=155755 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryAliquot`
--

DROP TABLE IF EXISTS `LibraryAliquot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryAliquot` (
  `aliquotId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `libraryId` bigint NOT NULL,
  `preMigrationId` bigint DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `volumeUsed` decimal(16,10) DEFAULT NULL,
  `volume` decimal(16,10) DEFAULT NULL,
  `volumeUnits` varchar(30) DEFAULT NULL,
  `discarded` tinyint(1) NOT NULL DEFAULT '0',
  `concentration` decimal(14,10) DEFAULT NULL,
  `concentrationUnits` varchar(30) DEFAULT NULL,
  `targetedSequencingId` bigint DEFAULT NULL,
  `ngUsed` decimal(14,10) DEFAULT NULL,
  `creationDate` date NOT NULL,
  `creator` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModifier` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `alias` varchar(100) NOT NULL,
  `dnaSize` bigint DEFAULT NULL,
  `parentAliquotId` bigint DEFAULT NULL,
  `discriminator` varchar(50) NOT NULL DEFAULT 'LibraryAliquot',
  `nonStandardAlias` tinyint(1) DEFAULT NULL,
  `libraryDesignCodeId` bigint DEFAULT NULL,
  `groupId` varchar(100) DEFAULT NULL,
  `groupDescription` varchar(255) DEFAULT NULL,
  `detailedQcStatusId` bigint DEFAULT NULL,
  `detailedQcStatusNote` varchar(500) DEFAULT NULL,
  `qcUser` bigint DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `qcDate` date DEFAULT NULL,
  `kitDescriptorId` bigint DEFAULT NULL,
  `kitLot` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`aliquotId`),
  UNIQUE KEY `uk_libraryAliquot_name` (`name`),
  UNIQUE KEY `uk_libraryAliquot_preMigrationId` (`preMigrationId`),
  UNIQUE KEY `uk_libraryAliquot_identificationBarcode` (`identificationBarcode`),
  KEY `fk_libraryAliquot_targetedSequencing` (`targetedSequencingId`),
  KEY `fk_libraryAliquot_creator` (`creator`),
  KEY `fk_libraryAliquot_lastModifier_user` (`lastModifier`),
  KEY `fk_libraryAliquot_library` (`libraryId`),
  KEY `fk_libraryAliquot_parentAliquot` (`parentAliquotId`),
  KEY `fk_libraryAliquot_libraryDesignCode` (`libraryDesignCodeId`),
  KEY `fk_libraryAliquot_detailedQcStatus` (`detailedQcStatusId`),
  KEY `fk_libraryAliquot_qcUser` (`qcUser`),
  KEY `fk_libraryAliquot_kitDescriptor` (`kitDescriptorId`),
  CONSTRAINT `fk_libraryAliquot_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_libraryAliquot_detailedQcStatus` FOREIGN KEY (`detailedQcStatusId`) REFERENCES `DetailedQcStatus` (`detailedQcStatusId`),
  CONSTRAINT `fk_libraryAliquot_kitDescriptor` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`),
  CONSTRAINT `fk_libraryAliquot_lastModifier_user` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_libraryAliquot_library` FOREIGN KEY (`libraryId`) REFERENCES `Library` (`libraryId`),
  CONSTRAINT `fk_libraryAliquot_libraryDesignCode` FOREIGN KEY (`libraryDesignCodeId`) REFERENCES `LibraryDesignCode` (`libraryDesignCodeId`),
  CONSTRAINT `fk_libraryAliquot_parentAliquot` FOREIGN KEY (`parentAliquotId`) REFERENCES `LibraryAliquot` (`aliquotId`),
  CONSTRAINT `fk_libraryAliquot_qcUser` FOREIGN KEY (`qcUser`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_libraryAliquot_targetedSequencing` FOREIGN KEY (`targetedSequencingId`) REFERENCES `TargetedSequencing` (`targetedSequencingId`)
) ENGINE=InnoDB AUTO_INCREMENT=128247 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `LibraryAliquotBoxPosition`
--

DROP TABLE IF EXISTS `LibraryAliquotBoxPosition`;
/*!50001 DROP VIEW IF EXISTS `LibraryAliquotBoxPosition`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `LibraryAliquotBoxPosition` AS SELECT 
 1 AS `aliquotId`,
 1 AS `boxId`,
 1 AS `position`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `LibraryAliquotChangeLog`
--

DROP TABLE IF EXISTS `LibraryAliquotChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryAliquotChangeLog` (
  `aliquotChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `aliquotId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`aliquotChangeLogId`),
  KEY `fk_libraryAliquotChangeLog_user` (`userId`),
  KEY `fk_libraryAliquotChangeLog_libraryAliquot` (`aliquotId`),
  CONSTRAINT `fk_libraryAliquotChangeLog_libraryAliquot` FOREIGN KEY (`aliquotId`) REFERENCES `LibraryAliquot` (`aliquotId`) ON DELETE CASCADE,
  CONSTRAINT `fk_libraryAliquotChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=362115 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `LibraryAliquotDistributionView`
--

DROP TABLE IF EXISTS `LibraryAliquotDistributionView`;
/*!50001 DROP VIEW IF EXISTS `LibraryAliquotDistributionView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `LibraryAliquotDistributionView` AS SELECT 
 1 AS `aliquotId`,
 1 AS `distributed`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `LibraryBoxPosition`
--

DROP TABLE IF EXISTS `LibraryBoxPosition`;
/*!50001 DROP VIEW IF EXISTS `LibraryBoxPosition`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `LibraryBoxPosition` AS SELECT 
 1 AS `libraryId`,
 1 AS `boxId`,
 1 AS `position`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `LibraryChangeLog`
--

DROP TABLE IF EXISTS `LibraryChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryChangeLog` (
  `libraryChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `libraryId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`libraryChangeLogId`),
  KEY `fk_libraryChangeLog_library` (`libraryId`),
  KEY `fk_libraryChangeLog_user` (`userId`),
  KEY `LibraryChangeLogDerivedInfo` (`libraryId`,`changeTime`),
  CONSTRAINT `fk_libraryChangeLog_library` FOREIGN KEY (`libraryId`) REFERENCES `Library` (`libraryId`) ON DELETE CASCADE,
  CONSTRAINT `fk_libraryChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=822979 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryDesign`
--

DROP TABLE IF EXISTS `LibraryDesign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryDesign` (
  `libraryDesignId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `sampleClassId` bigint NOT NULL,
  `librarySelectionType` bigint NOT NULL,
  `libraryStrategyType` bigint NOT NULL,
  `libraryDesignCodeId` bigint NOT NULL,
  PRIMARY KEY (`libraryDesignId`),
  UNIQUE KEY `uk_libraryDesign_name_sampleClass` (`name`,`sampleClassId`),
  KEY `FK_lpr_sampleClassId` (`sampleClassId`),
  KEY `LibraryDesign_librarySelectionType_fkey` (`librarySelectionType`),
  KEY `LibraryDesign_libraryStrategyType_fkey` (`libraryStrategyType`),
  KEY `FK_ld_libraryDesignCode_libraryDesignCodeId` (`libraryDesignCodeId`),
  CONSTRAINT `FK_ld_libraryDesignCode_libraryDesignCodeId` FOREIGN KEY (`libraryDesignCodeId`) REFERENCES `LibraryDesignCode` (`libraryDesignCodeId`),
  CONSTRAINT `FK_lpr_sampleClassId` FOREIGN KEY (`sampleClassId`) REFERENCES `SampleClass` (`sampleClassId`),
  CONSTRAINT `LibraryDesign_librarySelectionType_fkey` FOREIGN KEY (`librarySelectionType`) REFERENCES `LibrarySelectionType` (`librarySelectionTypeId`),
  CONSTRAINT `LibraryDesign_libraryStrategyType_fkey` FOREIGN KEY (`libraryStrategyType`) REFERENCES `LibraryStrategyType` (`libraryStrategyTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryDesignCode`
--

DROP TABLE IF EXISTS `LibraryDesignCode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryDesignCode` (
  `libraryDesignCodeId` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(2) NOT NULL,
  `description` varchar(255) NOT NULL,
  `targetedSequencingRequired` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`libraryDesignCodeId`),
  UNIQUE KEY `libraryDesignCode_unique` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `LibraryDistributionView`
--

DROP TABLE IF EXISTS `LibraryDistributionView`;
/*!50001 DROP VIEW IF EXISTS `LibraryDistributionView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `LibraryDistributionView` AS SELECT 
 1 AS `libraryId`,
 1 AS `distributed`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `LibraryIndex`
--

DROP TABLE IF EXISTS `LibraryIndex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryIndex` (
  `indexId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(24) DEFAULT NULL,
  `sequence` varchar(120) DEFAULT NULL,
  `position` int DEFAULT '1',
  `indexFamilyId` bigint NOT NULL,
  `realSequences` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`indexId`),
  KEY `Indices_ibfk_1` (`indexFamilyId`),
  CONSTRAINT `LibraryIndex_ibfk_1` FOREIGN KEY (`indexFamilyId`) REFERENCES `LibraryIndexFamily` (`indexFamilyId`)
) ENGINE=InnoDB AUTO_INCREMENT=13318 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryIndexFamily`
--

DROP TABLE IF EXISTS `LibraryIndexFamily`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryIndexFamily` (
  `indexFamilyId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `platformType` varchar(20) NOT NULL,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  `fakeSequence` tinyint(1) NOT NULL DEFAULT '0',
  `uniqueDualIndex` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`indexFamilyId`),
  UNIQUE KEY `UK_tbs_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryQC`
--

DROP TABLE IF EXISTS `LibraryQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryQC` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `library_libraryId` bigint NOT NULL,
  `creator` bigint NOT NULL,
  `date` date NOT NULL,
  `type` bigint NOT NULL,
  `results` decimal(16,10) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL,
  `instrumentId` bigint DEFAULT NULL,
  `kitLot` varchar(50) DEFAULT NULL,
  `kitDescriptorId` bigint DEFAULT NULL,
  PRIMARY KEY (`qcId`),
  KEY `FK_library_qc_library` (`library_libraryId`),
  KEY `FK_library_qc_creator` (`creator`),
  KEY `FK_library_qc_type` (`type`),
  KEY `fk_libraryQc_instrument` (`instrumentId`),
  KEY `fk_libraryQc_kit` (`kitDescriptorId`),
  CONSTRAINT `FK_library_qc_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_library_qc_library` FOREIGN KEY (`library_libraryId`) REFERENCES `Library` (`libraryId`),
  CONSTRAINT `FK_library_qc_type` FOREIGN KEY (`type`) REFERENCES `QCType` (`qcTypeId`),
  CONSTRAINT `fk_libraryQc_instrument` FOREIGN KEY (`instrumentId`) REFERENCES `Instrument` (`instrumentId`),
  CONSTRAINT `fk_libraryQc_kit` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`)
) ENGINE=InnoDB AUTO_INCREMENT=23130 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryQcControl`
--

DROP TABLE IF EXISTS `LibraryQcControl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryQcControl` (
  `qcControlId` bigint NOT NULL AUTO_INCREMENT,
  `qcId` bigint NOT NULL,
  `controlId` bigint NOT NULL,
  `lot` varchar(50) NOT NULL,
  `qcPassed` tinyint(1) NOT NULL,
  PRIMARY KEY (`qcControlId`),
  KEY `fk_libraryQcControl_qc` (`qcId`),
  KEY `fk_libraryQcControl_control` (`controlId`),
  CONSTRAINT `fk_libraryQcControl_control` FOREIGN KEY (`controlId`) REFERENCES `QcControl` (`controlId`),
  CONSTRAINT `fk_libraryQcControl_qc` FOREIGN KEY (`qcId`) REFERENCES `LibraryQC` (`qcId`)
) ENGINE=InnoDB AUTO_INCREMENT=40670 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `LibraryReceiptView`
--

DROP TABLE IF EXISTS `LibraryReceiptView`;
/*!50001 DROP VIEW IF EXISTS `LibraryReceiptView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `LibraryReceiptView` AS SELECT 
 1 AS `libraryId`,
 1 AS `transferId`,
 1 AS `transferTime`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `LibrarySelectionType`
--

DROP TABLE IF EXISTS `LibrarySelectionType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibrarySelectionType` (
  `librarySelectionTypeId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`librarySelectionTypeId`),
  UNIQUE KEY `uk_librarySelectionType_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibrarySpikeIn`
--

DROP TABLE IF EXISTS `LibrarySpikeIn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibrarySpikeIn` (
  `spikeInId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  PRIMARY KEY (`spikeInId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryStrategyType`
--

DROP TABLE IF EXISTS `LibraryStrategyType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryStrategyType` (
  `libraryStrategyTypeId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`libraryStrategyTypeId`),
  UNIQUE KEY `uk_libraryStrategyType_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryTemplate`
--

DROP TABLE IF EXISTS `LibraryTemplate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryTemplate` (
  `libraryTemplateId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `defaultVolume` decimal(14,10) DEFAULT NULL,
  `platformType` varchar(255) DEFAULT NULL,
  `libraryTypeId` bigint DEFAULT NULL,
  `librarySelectionTypeId` bigint DEFAULT NULL,
  `libraryStrategyTypeId` bigint DEFAULT NULL,
  `kitDescriptorId` bigint DEFAULT NULL,
  `indexFamilyId` bigint DEFAULT NULL,
  `volumeUnits` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`libraryTemplateId`),
  UNIQUE KEY `uk_libraryTemplate_alias` (`alias`),
  KEY `fk_libraryTemplate_libraryType` (`libraryTypeId`),
  KEY `fk_libraryTemplate_selection` (`librarySelectionTypeId`),
  KEY `fk_libraryTemplate_strategy` (`libraryStrategyTypeId`),
  KEY `fk_libraryTemplate_kitDescriptor` (`kitDescriptorId`),
  KEY `fk_libraryTemplate_indexFamily` (`indexFamilyId`),
  CONSTRAINT `fk_libraryTemplate_indexFamily` FOREIGN KEY (`indexFamilyId`) REFERENCES `LibraryIndexFamily` (`indexFamilyId`),
  CONSTRAINT `fk_libraryTemplate_kitDescriptor` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`),
  CONSTRAINT `fk_libraryTemplate_libraryType` FOREIGN KEY (`libraryTypeId`) REFERENCES `LibraryType` (`libraryTypeId`),
  CONSTRAINT `fk_libraryTemplate_selection` FOREIGN KEY (`librarySelectionTypeId`) REFERENCES `LibrarySelectionType` (`librarySelectionTypeId`),
  CONSTRAINT `fk_libraryTemplate_strategy` FOREIGN KEY (`libraryStrategyTypeId`) REFERENCES `LibraryStrategyType` (`libraryStrategyTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryTemplate_Index1`
--

DROP TABLE IF EXISTS `LibraryTemplate_Index1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryTemplate_Index1` (
  `libraryTemplateId` bigint NOT NULL,
  `position` varchar(3) NOT NULL,
  `indexId` bigint NOT NULL,
  PRIMARY KEY (`libraryTemplateId`,`position`),
  KEY `fk_libraryTemplateIndex1_index` (`indexId`),
  CONSTRAINT `fk_libraryTemplateIndex1_index` FOREIGN KEY (`indexId`) REFERENCES `LibraryIndex` (`indexId`),
  CONSTRAINT `fk_libraryTemplateIndex1_libraryTemplate` FOREIGN KEY (`libraryTemplateId`) REFERENCES `LibraryTemplate` (`libraryTemplateId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryTemplate_Index2`
--

DROP TABLE IF EXISTS `LibraryTemplate_Index2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryTemplate_Index2` (
  `libraryTemplateId` bigint NOT NULL,
  `position` varchar(3) NOT NULL,
  `indexId` bigint NOT NULL,
  PRIMARY KEY (`libraryTemplateId`,`position`),
  KEY `fk_libraryTemplateIndex2_index` (`indexId`),
  CONSTRAINT `fk_libraryTemplateIndex2_index` FOREIGN KEY (`indexId`) REFERENCES `LibraryIndex` (`indexId`),
  CONSTRAINT `fk_libraryTemplateIndex2_libraryTemplate` FOREIGN KEY (`libraryTemplateId`) REFERENCES `LibraryTemplate` (`libraryTemplateId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryTemplate_Project`
--

DROP TABLE IF EXISTS `LibraryTemplate_Project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryTemplate_Project` (
  `libraryTemplateId` bigint NOT NULL,
  `projectId` bigint NOT NULL,
  PRIMARY KEY (`libraryTemplateId`,`projectId`),
  KEY `projectId` (`projectId`),
  CONSTRAINT `LibraryTemplate_Project_fk_1` FOREIGN KEY (`libraryTemplateId`) REFERENCES `LibraryTemplate` (`libraryTemplateId`),
  CONSTRAINT `LibraryTemplate_Project_fk_2` FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LibraryType`
--

DROP TABLE IF EXISTS `LibraryType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LibraryType` (
  `libraryTypeId` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `platformType` varchar(50) NOT NULL,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  `abbreviation` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`libraryTypeId`),
  UNIQUE KEY `uk_libraryType_byPlatform` (`platformType`,`description`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Library_Attachment`
--

DROP TABLE IF EXISTS `Library_Attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Library_Attachment` (
  `libraryId` bigint NOT NULL,
  `attachmentId` bigint NOT NULL,
  PRIMARY KEY (`libraryId`,`attachmentId`),
  KEY `fk_library_attachment` (`attachmentId`),
  CONSTRAINT `fk_attachment_library` FOREIGN KEY (`libraryId`) REFERENCES `Library` (`libraryId`),
  CONSTRAINT `fk_library_attachment` FOREIGN KEY (`attachmentId`) REFERENCES `Attachment` (`attachmentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Library_Note`
--

DROP TABLE IF EXISTS `Library_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Library_Note` (
  `library_libraryId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`library_libraryId`,`notes_noteId`),
  KEY `LibraryNote_Note_FK` (`notes_noteId`),
  CONSTRAINT `LibraryNote_Library_FK` FOREIGN KEY (`library_libraryId`) REFERENCES `Library` (`libraryId`),
  CONSTRAINT `LibraryNote_Note_FK` FOREIGN KEY (`notes_noteId`) REFERENCES `Note` (`noteId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `ListPoolView`
--

DROP TABLE IF EXISTS `ListPoolView`;
/*!50001 DROP VIEW IF EXISTS `ListPoolView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `ListPoolView` AS SELECT 
 1 AS `poolId`,
 1 AS `name`,
 1 AS `alias`,
 1 AS `identificationBarcode`,
 1 AS `description`,
 1 AS `platformType`,
 1 AS `creator`,
 1 AS `created`,
 1 AS `creationDate`,
 1 AS `lastModifier`,
 1 AS `lastModified`,
 1 AS `concentration`,
 1 AS `concentrationUnits`,
 1 AS `dnaSize`,
 1 AS `discarded`,
 1 AS `distributed`,
 1 AS `boxId`,
 1 AS `boxName`,
 1 AS `boxAlias`,
 1 AS `boxLocationBarcode`,
 1 AS `boxPosition`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `ListPoolView_Element`
--

DROP TABLE IF EXISTS `ListPoolView_Element`;
/*!50001 DROP VIEW IF EXISTS `ListPoolView_Element`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `ListPoolView_Element` AS SELECT 
 1 AS `aliquotId`,
 1 AS `name`,
 1 AS `alias`,
 1 AS `dnaSize`,
 1 AS `libraryId`,
 1 AS `lowQuality`,
 1 AS `index1Id`,
 1 AS `index2Id`,
 1 AS `projectId`,
 1 AS `subprojectAlias`,
 1 AS `subprojectPriority`,
 1 AS `consentLevel`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `ListWorksetView`
--

DROP TABLE IF EXISTS `ListWorksetView`;
/*!50001 DROP VIEW IF EXISTS `ListWorksetView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `ListWorksetView` AS SELECT 
 1 AS `worksetId`,
 1 AS `alias`,
 1 AS `itemCount`,
 1 AS `description`,
 1 AS `category`,
 1 AS `stage`,
 1 AS `creator`,
 1 AS `created`,
 1 AS `lastModifier`,
 1 AS `lastModified`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `Metric`
--

DROP TABLE IF EXISTS `Metric`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Metric` (
  `metricId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(100) NOT NULL,
  `category` varchar(50) NOT NULL,
  `thresholdType` varchar(20) NOT NULL,
  `units` varchar(20) DEFAULT NULL,
  `subcategoryId` bigint DEFAULT NULL,
  `sortPriority` tinyint unsigned DEFAULT NULL,
  `nucleicAcidType` varchar(10) DEFAULT NULL,
  `tissueMaterialId` bigint DEFAULT NULL,
  `tissueTypeId` bigint DEFAULT NULL,
  `negateTissueType` tinyint(1) NOT NULL DEFAULT '0',
  `tissueOriginId` bigint DEFAULT NULL,
  `containerModelId` bigint DEFAULT NULL,
  `readLength` int DEFAULT NULL,
  `readLength2` int DEFAULT NULL,
  PRIMARY KEY (`metricId`),
  KEY `fk_metric_subcategory` (`subcategoryId`),
  KEY `fk_assayMetric_tissueMaterial` (`tissueMaterialId`),
  KEY `fk_assayMetric_tissueType` (`tissueTypeId`),
  KEY `fk_assayMetric_tissueOrigin` (`tissueOriginId`),
  KEY `fk_assayMetric_containerModel` (`containerModelId`),
  CONSTRAINT `fk_assayMetric_containerModel` FOREIGN KEY (`containerModelId`) REFERENCES `SequencingContainerModel` (`sequencingContainerModelId`),
  CONSTRAINT `fk_assayMetric_tissueMaterial` FOREIGN KEY (`tissueMaterialId`) REFERENCES `TissueMaterial` (`tissueMaterialId`),
  CONSTRAINT `fk_assayMetric_tissueOrigin` FOREIGN KEY (`tissueOriginId`) REFERENCES `TissueOrigin` (`tissueOriginId`),
  CONSTRAINT `fk_assayMetric_tissueType` FOREIGN KEY (`tissueTypeId`) REFERENCES `TissueType` (`tissueTypeId`),
  CONSTRAINT `fk_metric_subcategory` FOREIGN KEY (`subcategoryId`) REFERENCES `MetricSubcategory` (`subcategoryId`)
) ENGINE=InnoDB AUTO_INCREMENT=242 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MetricSubcategory`
--

DROP TABLE IF EXISTS `MetricSubcategory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MetricSubcategory` (
  `subcategoryId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(100) NOT NULL,
  `category` varchar(50) NOT NULL,
  `libraryDesignCodeId` bigint DEFAULT NULL,
  `sortPriority` tinyint unsigned DEFAULT NULL,
  PRIMARY KEY (`subcategoryId`),
  UNIQUE KEY `uk_metricSubcategory_alias_category` (`alias`,`category`),
  KEY `fk_metricSubcategory_libraryDesignCode` (`libraryDesignCodeId`),
  CONSTRAINT `fk_metricSubcategory_libraryDesignCode` FOREIGN KEY (`libraryDesignCodeId`) REFERENCES `LibraryDesignCode` (`libraryDesignCodeId`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Note`
--

DROP TABLE IF EXISTS `Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Note` (
  `noteId` bigint NOT NULL AUTO_INCREMENT,
  `creationDate` date NOT NULL,
  `internalOnly` tinyint(1) NOT NULL DEFAULT '0',
  `text` mediumtext,
  `owner_userId` bigint DEFAULT NULL,
  PRIMARY KEY (`noteId`),
  KEY `FK2524124140968C` (`owner_userId`)
) ENGINE=InnoDB AUTO_INCREMENT=3195 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `OxfordNanoporeContainer`
--

DROP TABLE IF EXISTS `OxfordNanoporeContainer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `OxfordNanoporeContainer` (
  `containerId` bigint NOT NULL AUTO_INCREMENT,
  `poreVersionId` bigint DEFAULT NULL,
  `receivedDate` date DEFAULT NULL,
  `returnedDate` date DEFAULT NULL,
  PRIMARY KEY (`containerId`),
  KEY `FK_OxfordNanoporeContainer_PoreVersion` (`poreVersionId`),
  CONSTRAINT `FK_OxfordNanoporeContainer_Container` FOREIGN KEY (`containerId`) REFERENCES `SequencerPartitionContainer` (`containerId`),
  CONSTRAINT `FK_OxfordNanoporeContainer_PoreVersion` FOREIGN KEY (`poreVersionId`) REFERENCES `PoreVersion` (`poreVersionId`)
) ENGINE=InnoDB AUTO_INCREMENT=5669 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PartitionQCType`
--

DROP TABLE IF EXISTS `PartitionQCType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PartitionQCType` (
  `partitionQcTypeId` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `noteRequired` tinyint(1) DEFAULT '0',
  `orderFulfilled` tinyint(1) NOT NULL DEFAULT '1',
  `analysisSkipped` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`partitionQcTypeId`),
  UNIQUE KEY `uk_partitionqctype_description` (`description`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `PendingTransferGroupView`
--

DROP TABLE IF EXISTS `PendingTransferGroupView`;
/*!50001 DROP VIEW IF EXISTS `PendingTransferGroupView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `PendingTransferGroupView` AS SELECT 
 1 AS `groupId`,
 1 AS `transfers`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `Pipeline`
--

DROP TABLE IF EXISTS `Pipeline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pipeline` (
  `pipelineId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(50) NOT NULL,
  PRIMARY KEY (`pipelineId`),
  UNIQUE KEY `uk_pipeline_alias` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pool`
--

DROP TABLE IF EXISTS `Pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pool` (
  `poolId` bigint NOT NULL AUTO_INCREMENT,
  `concentration` decimal(14,10) DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `creationDate` date NOT NULL,
  `platformType` varchar(50) NOT NULL,
  `alias` varchar(255) NOT NULL,
  `lastModifier` bigint NOT NULL DEFAULT '1',
  `discarded` tinyint(1) NOT NULL DEFAULT '0',
  `volume` decimal(16,10) DEFAULT NULL,
  `qcPassed` tinyint(1) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `concentrationUnits` varchar(30) DEFAULT NULL,
  `volumeUnits` varchar(30) DEFAULT NULL,
  `dnaSize` bigint DEFAULT NULL,
  PRIMARY KEY (`poolId`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `uk_pool_alias` (`alias`),
  UNIQUE KEY `identificationBarcode` (`identificationBarcode`),
  KEY `fk_pool_lastModifier_user` (`lastModifier`),
  KEY `fk_pool_creator` (`creator`),
  KEY `lastModified_Pool` (`lastModified`),
  CONSTRAINT `fk_pool_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_pool_lastModifier_user` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=18693 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `PoolBoxPosition`
--

DROP TABLE IF EXISTS `PoolBoxPosition`;
/*!50001 DROP VIEW IF EXISTS `PoolBoxPosition`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `PoolBoxPosition` AS SELECT 
 1 AS `poolId`,
 1 AS `boxId`,
 1 AS `position`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `PoolChangeLog`
--

DROP TABLE IF EXISTS `PoolChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PoolChangeLog` (
  `poolChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `poolId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`poolChangeLogId`),
  KEY `fk_poolChangeLog_pool` (`poolId`),
  KEY `fk_poolChangeLog_user` (`userId`),
  KEY `PoolChangeLogDerivedInfo` (`poolId`,`changeTime`),
  CONSTRAINT `fk_poolChangeLog_pool` FOREIGN KEY (`poolId`) REFERENCES `Pool` (`poolId`) ON DELETE CASCADE,
  CONSTRAINT `fk_poolChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=49095 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `PoolDistributionView`
--

DROP TABLE IF EXISTS `PoolDistributionView`;
/*!50001 DROP VIEW IF EXISTS `PoolDistributionView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `PoolDistributionView` AS SELECT 
 1 AS `poolId`,
 1 AS `distributed`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `PoolOrder`
--

DROP TABLE IF EXISTS `PoolOrder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PoolOrder` (
  `poolOrderId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `purposeId` bigint NOT NULL,
  `parametersId` bigint DEFAULT NULL,
  `partitions` int DEFAULT NULL,
  `draft` tinyint(1) NOT NULL DEFAULT '0',
  `poolId` bigint DEFAULT NULL,
  `sequencingOrderId` bigint DEFAULT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sequencingContainerModelId` bigint DEFAULT NULL,
  PRIMARY KEY (`poolOrderId`),
  KEY `fk_poolOrder_sequencingParameters` (`parametersId`),
  KEY `fk_poolOrder_creator` (`createdBy`),
  KEY `fk_poolOrder_updater` (`updatedBy`),
  KEY `fk_poolOrder_pool` (`poolId`),
  KEY `fk_poolOrder_sequencingOrder` (`sequencingOrderId`),
  KEY `fk_poolOrder_purpose` (`purposeId`),
  KEY `fk_poolOrder_sequencingContainerModel` (`sequencingContainerModelId`),
  CONSTRAINT `fk_poolOrder_creator` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_poolOrder_pool` FOREIGN KEY (`poolId`) REFERENCES `Pool` (`poolId`),
  CONSTRAINT `fk_poolOrder_purpose` FOREIGN KEY (`purposeId`) REFERENCES `RunPurpose` (`purposeId`),
  CONSTRAINT `fk_poolOrder_sequencingContainerModel` FOREIGN KEY (`sequencingContainerModelId`) REFERENCES `SequencingContainerModel` (`sequencingContainerModelId`),
  CONSTRAINT `fk_poolOrder_sequencingOrder` FOREIGN KEY (`sequencingOrderId`) REFERENCES `SequencingOrder` (`sequencingOrderId`),
  CONSTRAINT `fk_poolOrder_sequencingParameters` FOREIGN KEY (`parametersId`) REFERENCES `SequencingParameters` (`parametersId`),
  CONSTRAINT `fk_poolOrder_updater` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PoolOrderChangeLog`
--

DROP TABLE IF EXISTS `PoolOrderChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PoolOrderChangeLog` (
  `poolOrderChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `poolOrderId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`poolOrderChangeLogId`),
  KEY `fk_poolOrderChangeLog_user` (`userId`),
  KEY `fk_poolOrderChangeLog_poolOrder` (`poolOrderId`),
  CONSTRAINT `fk_poolOrderChangeLog_poolOrder` FOREIGN KEY (`poolOrderId`) REFERENCES `PoolOrder` (`poolOrderId`) ON DELETE CASCADE,
  CONSTRAINT `fk_poolOrderChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PoolOrder_LibraryAliquot`
--

DROP TABLE IF EXISTS `PoolOrder_LibraryAliquot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PoolOrder_LibraryAliquot` (
  `poolOrderId` bigint NOT NULL,
  `aliquotId` bigint NOT NULL,
  `proportion` smallint unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`poolOrderId`,`aliquotId`),
  KEY `fk_poolOrder_libraryAliquot` (`aliquotId`),
  CONSTRAINT `fk_libraryAliquot_poolOrder` FOREIGN KEY (`poolOrderId`) REFERENCES `PoolOrder` (`poolOrderId`),
  CONSTRAINT `fk_poolOrder_libraryAliquot` FOREIGN KEY (`aliquotId`) REFERENCES `LibraryAliquot` (`aliquotId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PoolQC`
--

DROP TABLE IF EXISTS `PoolQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PoolQC` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `pool_poolId` bigint NOT NULL,
  `creator` bigint NOT NULL,
  `date` date NOT NULL,
  `type` bigint NOT NULL,
  `results` decimal(16,10) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL,
  `instrumentId` bigint DEFAULT NULL,
  `kitLot` varchar(50) DEFAULT NULL,
  `kitDescriptorId` bigint DEFAULT NULL,
  PRIMARY KEY (`qcId`),
  KEY `FK_pool_qc_pool` (`pool_poolId`),
  KEY `FK_pool_qc_creator` (`creator`),
  KEY `FK_pool_qc_type` (`type`),
  KEY `fk_poolQc_instrument` (`instrumentId`),
  KEY `fk_poolQc_kit` (`kitDescriptorId`),
  CONSTRAINT `FK_pool_qc_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_pool_qc_pool` FOREIGN KEY (`pool_poolId`) REFERENCES `Pool` (`poolId`),
  CONSTRAINT `FK_pool_qc_type` FOREIGN KEY (`type`) REFERENCES `QCType` (`qcTypeId`),
  CONSTRAINT `fk_poolQc_instrument` FOREIGN KEY (`instrumentId`) REFERENCES `Instrument` (`instrumentId`),
  CONSTRAINT `fk_poolQc_kit` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`)
) ENGINE=InnoDB AUTO_INCREMENT=1195 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PoolQcControl`
--

DROP TABLE IF EXISTS `PoolQcControl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PoolQcControl` (
  `qcControlId` bigint NOT NULL AUTO_INCREMENT,
  `qcId` bigint NOT NULL,
  `controlId` bigint NOT NULL,
  `lot` varchar(50) NOT NULL,
  `qcPassed` tinyint(1) NOT NULL,
  PRIMARY KEY (`qcControlId`),
  KEY `fk_poolQcControl_qc` (`qcId`),
  KEY `fk_poolQcControl_control` (`controlId`),
  CONSTRAINT `fk_poolQcControl_control` FOREIGN KEY (`controlId`) REFERENCES `QcControl` (`controlId`),
  CONSTRAINT `fk_poolQcControl_qc` FOREIGN KEY (`qcId`) REFERENCES `PoolQC` (`qcId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pool_Attachment`
--

DROP TABLE IF EXISTS `Pool_Attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pool_Attachment` (
  `poolId` bigint NOT NULL,
  `attachmentId` bigint NOT NULL,
  PRIMARY KEY (`poolId`,`attachmentId`),
  KEY `fk_pool_attachment` (`attachmentId`),
  CONSTRAINT `fk_attachment_pool` FOREIGN KEY (`poolId`) REFERENCES `Pool` (`poolId`),
  CONSTRAINT `fk_pool_attachment` FOREIGN KEY (`attachmentId`) REFERENCES `Attachment` (`attachmentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pool_LibraryAliquot`
--

DROP TABLE IF EXISTS `Pool_LibraryAliquot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pool_LibraryAliquot` (
  `poolId` bigint NOT NULL,
  `aliquotId` bigint NOT NULL,
  `proportion` smallint unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`poolId`,`aliquotId`),
  KEY `fk_pool_libraryAliquot` (`aliquotId`),
  CONSTRAINT `fk_libraryAliquot_pool` FOREIGN KEY (`poolId`) REFERENCES `Pool` (`poolId`),
  CONSTRAINT `fk_pool_libraryAliquot` FOREIGN KEY (`aliquotId`) REFERENCES `LibraryAliquot` (`aliquotId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pool_Note`
--

DROP TABLE IF EXISTS `Pool_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pool_Note` (
  `pool_poolId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`pool_poolId`,`notes_noteId`),
  KEY `PoolNote_Note_FK` (`notes_noteId`),
  CONSTRAINT `PoolNote_Note_FK` FOREIGN KEY (`notes_noteId`) REFERENCES `Note` (`noteId`),
  CONSTRAINT `PoolNote_Pool_FK` FOREIGN KEY (`pool_poolId`) REFERENCES `Pool` (`poolId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PoreVersion`
--

DROP TABLE IF EXISTS `PoreVersion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PoreVersion` (
  `poreVersionId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(100) NOT NULL,
  PRIMARY KEY (`poreVersionId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Printer`
--

DROP TABLE IF EXISTS `Printer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Printer` (
  `printerId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `driver` varchar(20) NOT NULL,
  `backend` varchar(20) NOT NULL,
  `configuration` varchar(1024) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `layout` varchar(2048) NOT NULL,
  `height` double NOT NULL DEFAULT '0',
  `width` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`printerId`),
  UNIQUE KEY `printer_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Project`
--

DROP TABLE IF EXISTS `Project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Project` (
  `projectId` bigint NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `referenceGenomeId` bigint NOT NULL DEFAULT '1',
  `targetedSequencingId` bigint DEFAULT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `code` varchar(255) DEFAULT NULL,
  `creator` bigint NOT NULL,
  `lastModifier` bigint NOT NULL,
  `secondaryNaming` tinyint(1) NOT NULL DEFAULT '0',
  `rebNumber` varchar(50) DEFAULT NULL,
  `rebExpiry` date DEFAULT NULL,
  `pipelineId` bigint NOT NULL,
  `samplesExpected` int DEFAULT NULL,
  `additionalDetails` text,
  PRIMARY KEY (`projectId`),
  UNIQUE KEY `project_alias_UK` (`title`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `uk_project_shortname` (`code`),
  KEY `referenceGenomeId` (`referenceGenomeId`),
  KEY `fk_Project_TargetedSequencing` (`targetedSequencingId`),
  KEY `fk_project_pipeline` (`pipelineId`),
  KEY `fk_project_creator` (`creator`),
  KEY `fk_project_modifier` (`lastModifier`),
  CONSTRAINT `fk_project_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_project_modifier` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_project_pipeline` FOREIGN KEY (`pipelineId`) REFERENCES `Pipeline` (`pipelineId`),
  CONSTRAINT `fk_Project_TargetedSequencing` FOREIGN KEY (`targetedSequencingId`) REFERENCES `TargetedSequencing` (`targetedSequencingId`),
  CONSTRAINT `Project_ibfk_1` FOREIGN KEY (`referenceGenomeId`) REFERENCES `ReferenceGenome` (`referenceGenomeId`)
) ENGINE=InnoDB AUTO_INCREMENT=723 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProjectChangeLog`
--

DROP TABLE IF EXISTS `ProjectChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ProjectChangeLog` (
  `projectChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `projectId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`projectChangeLogId`),
  KEY `fk_projectChangeLog_user` (`userId`),
  KEY `fk_projectChangeLog_project` (`projectId`),
  CONSTRAINT `fk_projectChangeLog_project` FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`) ON DELETE CASCADE,
  CONSTRAINT `fk_projectChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1499 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Project_Assay`
--

DROP TABLE IF EXISTS `Project_Assay`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Project_Assay` (
  `projectId` bigint NOT NULL,
  `assayId` bigint NOT NULL,
  PRIMARY KEY (`projectId`,`assayId`),
  KEY `fk_projectAssay_assay` (`assayId`),
  CONSTRAINT `fk_projectAssay_assay` FOREIGN KEY (`assayId`) REFERENCES `Assay` (`assayId`),
  CONSTRAINT `fk_projectAssay_project` FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Project_Attachment`
--

DROP TABLE IF EXISTS `Project_Attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Project_Attachment` (
  `projectId` bigint NOT NULL,
  `attachmentId` bigint NOT NULL,
  PRIMARY KEY (`projectId`,`attachmentId`),
  KEY `fk_project_attachment` (`attachmentId`),
  CONSTRAINT `fk_attachment_project` FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`),
  CONSTRAINT `fk_project_attachment` FOREIGN KEY (`attachmentId`) REFERENCES `Attachment` (`attachmentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Project_Contact`
--

DROP TABLE IF EXISTS `Project_Contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Project_Contact` (
  `projectId` bigint NOT NULL,
  `contactId` bigint NOT NULL,
  `contactRoleId` bigint NOT NULL,
  PRIMARY KEY (`projectId`,`contactId`,`contactRoleId`),
  KEY `fk_project_contact_contact` (`contactId`),
  KEY `fk_project_contact_contactRole` (`contactRoleId`),
  CONSTRAINT `fk_project_contact_contact` FOREIGN KEY (`contactId`) REFERENCES `Contact` (`contactId`),
  CONSTRAINT `fk_project_contact_contactRole` FOREIGN KEY (`contactRoleId`) REFERENCES `ContactRole` (`contactRoleId`),
  CONSTRAINT `fk_project_contact_project` FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Project_Deliverable`
--

DROP TABLE IF EXISTS `Project_Deliverable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Project_Deliverable` (
  `projectId` bigint NOT NULL,
  `deliverableId` bigint NOT NULL,
  PRIMARY KEY (`projectId`,`deliverableId`),
  KEY `fk_project_deliverable` (`deliverableId`),
  CONSTRAINT `fk_deliverable_project` FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`),
  CONSTRAINT `fk_project_deliverable` FOREIGN KEY (`deliverableId`) REFERENCES `Deliverable` (`deliverableId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QCType`
--

DROP TABLE IF EXISTS `QCType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QCType` (
  `qcTypeId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `qcTarget` varchar(50) NOT NULL,
  `units` varchar(20) DEFAULT NULL,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  `precisionAfterDecimal` int NOT NULL DEFAULT '0',
  `correspondingField` varchar(50) NOT NULL DEFAULT 'NONE',
  `autoUpdateField` tinyint(1) NOT NULL DEFAULT '0',
  `instrumentModelId` bigint DEFAULT NULL,
  PRIMARY KEY (`qcTypeId`),
  UNIQUE KEY `uk_qcType` (`name`,`qcTarget`),
  KEY `fk_qcType_instrumentModel` (`instrumentModelId`),
  CONSTRAINT `fk_qcType_instrumentModel` FOREIGN KEY (`instrumentModelId`) REFERENCES `InstrumentModel` (`instrumentModelId`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QCType_KitDescriptor`
--

DROP TABLE IF EXISTS `QCType_KitDescriptor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QCType_KitDescriptor` (
  `qcTypeId` bigint NOT NULL,
  `kitDescriptorId` bigint NOT NULL,
  PRIMARY KEY (`qcTypeId`,`kitDescriptorId`),
  KEY `fk_qcType_kitDescriptor` (`kitDescriptorId`),
  CONSTRAINT `fk_kitDescriptor_qcType` FOREIGN KEY (`qcTypeId`) REFERENCES `QCType` (`qcTypeId`),
  CONSTRAINT `fk_qcType_kitDescriptor` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QcControl`
--

DROP TABLE IF EXISTS `QcControl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QcControl` (
  `controlId` bigint NOT NULL AUTO_INCREMENT,
  `qcTypeId` bigint NOT NULL,
  `alias` varchar(100) NOT NULL,
  PRIMARY KEY (`controlId`),
  KEY `fk_qcControl_qcType` (`qcTypeId`),
  CONSTRAINT `fk_qcControl_qcType` FOREIGN KEY (`qcTypeId`) REFERENCES `QCType` (`qcTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ReferenceGenome`
--

DROP TABLE IF EXISTS `ReferenceGenome`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ReferenceGenome` (
  `referenceGenomeId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `defaultScientificNameId` bigint DEFAULT NULL,
  PRIMARY KEY (`referenceGenomeId`),
  UNIQUE KEY `alias` (`alias`),
  KEY `fk_referenceGenome_defaultScientificName` (`defaultScientificNameId`),
  CONSTRAINT `fk_referenceGenome_defaultScientificName` FOREIGN KEY (`defaultScientificNameId`) REFERENCES `ScientificName` (`scientificNameId`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Requisition`
--

DROP TABLE IF EXISTS `Requisition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Requisition` (
  `requisitionId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(150) NOT NULL,
  `stopped` tinyint(1) NOT NULL DEFAULT '0',
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModifier` bigint NOT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `stopReason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`requisitionId`),
  UNIQUE KEY `uk_requisition_alias` (`alias`),
  KEY `fk_requisition_creator` (`creator`),
  KEY `fk_requisition_modifier` (`lastModifier`),
  CONSTRAINT `fk_requisition_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_requisition_modifier` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=3360 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RequisitionChangeLog`
--

DROP TABLE IF EXISTS `RequisitionChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RequisitionChangeLog` (
  `requisitionChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `requisitionId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`requisitionChangeLogId`),
  KEY `fk_requisitionChangeLog_requisition` (`requisitionId`),
  KEY `fk_requisitionChangeLog_user` (`userId`),
  CONSTRAINT `fk_requisitionChangeLog_requisition` FOREIGN KEY (`requisitionId`) REFERENCES `Requisition` (`requisitionId`) ON DELETE CASCADE,
  CONSTRAINT `fk_requisitionChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=32832 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RequisitionPause`
--

DROP TABLE IF EXISTS `RequisitionPause`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RequisitionPause` (
  `pauseId` bigint NOT NULL AUTO_INCREMENT,
  `requisitionId` bigint NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pauseId`),
  KEY `fk_requisitionPause_requisition` (`requisitionId`),
  CONSTRAINT `fk_requisitionPause_requisition` FOREIGN KEY (`requisitionId`) REFERENCES `Requisition` (`requisitionId`)
) ENGINE=InnoDB AUTO_INCREMENT=238 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RequisitionQc`
--

DROP TABLE IF EXISTS `RequisitionQc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RequisitionQc` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `requisitionId` bigint NOT NULL,
  `creator` bigint NOT NULL,
  `date` date NOT NULL,
  `type` bigint NOT NULL,
  `results` decimal(16,10) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL,
  `instrumentId` bigint DEFAULT NULL,
  `kitLot` varchar(50) DEFAULT NULL,
  `kitDescriptorId` bigint DEFAULT NULL,
  PRIMARY KEY (`qcId`),
  KEY `fk_requisitionQc_requisition` (`requisitionId`),
  KEY `fk_requisitionQc_creator` (`creator`),
  KEY `fk_requisitionQc_type` (`type`),
  KEY `fk_requisitionQc_kit` (`kitDescriptorId`),
  CONSTRAINT `fk_requisitionQc_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_requisitionQc_kit` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`),
  CONSTRAINT `fk_requisitionQc_requisition` FOREIGN KEY (`requisitionId`) REFERENCES `Requisition` (`requisitionId`),
  CONSTRAINT `fk_requisitionQc_type` FOREIGN KEY (`type`) REFERENCES `QCType` (`qcTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=1430 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RequisitionQcControl`
--

DROP TABLE IF EXISTS `RequisitionQcControl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RequisitionQcControl` (
  `qcControlId` bigint NOT NULL AUTO_INCREMENT,
  `qcId` bigint NOT NULL,
  `controlId` bigint NOT NULL,
  `lot` varchar(50) NOT NULL,
  `qcPassed` tinyint(1) NOT NULL,
  PRIMARY KEY (`qcControlId`),
  KEY `fk_requisitionQcControl_qc` (`qcId`),
  KEY `fk_requisitionQcControl_control` (`controlId`),
  CONSTRAINT `fk_requisitionQcControl_control` FOREIGN KEY (`controlId`) REFERENCES `QcControl` (`controlId`),
  CONSTRAINT `fk_requisitionQcControl_qc` FOREIGN KEY (`qcId`) REFERENCES `RequisitionQc` (`qcId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Requisition_Assay`
--

DROP TABLE IF EXISTS `Requisition_Assay`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Requisition_Assay` (
  `requisitionId` bigint NOT NULL,
  `assayId` bigint NOT NULL,
  PRIMARY KEY (`requisitionId`,`assayId`),
  KEY `fk_requisitionAssay_assay` (`assayId`),
  CONSTRAINT `fk_requisitionAssay_assay` FOREIGN KEY (`assayId`) REFERENCES `Assay` (`assayId`),
  CONSTRAINT `fk_requisitionAssay_requisition` FOREIGN KEY (`requisitionId`) REFERENCES `Requisition` (`requisitionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Requisition_Attachment`
--

DROP TABLE IF EXISTS `Requisition_Attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Requisition_Attachment` (
  `requisitionId` bigint NOT NULL,
  `attachmentId` bigint NOT NULL,
  PRIMARY KEY (`requisitionId`,`attachmentId`),
  KEY `fk_requisition_attachment` (`attachmentId`),
  CONSTRAINT `fk_attachment_requisition` FOREIGN KEY (`requisitionId`) REFERENCES `Requisition` (`requisitionId`),
  CONSTRAINT `fk_requisition_attachment` FOREIGN KEY (`attachmentId`) REFERENCES `Attachment` (`attachmentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Requisition_Note`
--

DROP TABLE IF EXISTS `Requisition_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Requisition_Note` (
  `requisitionId` bigint NOT NULL,
  `noteId` bigint NOT NULL,
  PRIMARY KEY (`requisitionId`,`noteId`),
  KEY `fk_requisition_note` (`noteId`),
  CONSTRAINT `fk_note_requisition` FOREIGN KEY (`requisitionId`) REFERENCES `Requisition` (`requisitionId`),
  CONSTRAINT `fk_requisition_note` FOREIGN KEY (`noteId`) REFERENCES `Note` (`noteId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Requisition_SupplementalLibrary`
--

DROP TABLE IF EXISTS `Requisition_SupplementalLibrary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Requisition_SupplementalLibrary` (
  `requisitionId` bigint NOT NULL,
  `libraryId` bigint NOT NULL,
  PRIMARY KEY (`requisitionId`,`libraryId`),
  KEY `fk_requisition_supplementalLibrary` (`libraryId`),
  CONSTRAINT `Requisition_SupplementalLibrary_ibfk_1` FOREIGN KEY (`requisitionId`) REFERENCES `Requisition` (`requisitionId`),
  CONSTRAINT `Requisition_SupplementalLibrary_ibfk_2` FOREIGN KEY (`libraryId`) REFERENCES `Library` (`libraryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Requisition_SupplementalSample`
--

DROP TABLE IF EXISTS `Requisition_SupplementalSample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Requisition_SupplementalSample` (
  `requisitionId` bigint NOT NULL,
  `sampleId` bigint NOT NULL,
  PRIMARY KEY (`requisitionId`,`sampleId`),
  KEY `fk_requisition_supplementalSample` (`sampleId`),
  CONSTRAINT `fk_requisition_supplementalSample` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `fk_supplementalSample_requisition` FOREIGN KEY (`requisitionId`) REFERENCES `Requisition` (`requisitionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Run`
--

DROP TABLE IF EXISTS `Run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Run` (
  `runId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `accession` varchar(50) DEFAULT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  `alias` varchar(255) NOT NULL,
  `instrumentId` bigint NOT NULL,
  `lastModifier` bigint NOT NULL DEFAULT '1',
  `sequencingParameters_parametersId` bigint DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `completionDate` date DEFAULT NULL,
  `health` varchar(50) NOT NULL,
  `metrics` longtext,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sequencingKitId` bigint DEFAULT NULL,
  `dataReview` tinyint(1) DEFAULT NULL,
  `dataReviewerId` bigint DEFAULT NULL,
  `sequencingKitLot` varchar(100) DEFAULT NULL,
  `sopId` bigint DEFAULT NULL,
  `dataManglingPolicy` varchar(50) DEFAULT NULL,
  `qcPassed` tinyint(1) DEFAULT NULL,
  `qcUser` bigint DEFAULT NULL,
  `qcDate` date DEFAULT NULL,
  `dataReviewDate` date DEFAULT NULL,
  PRIMARY KEY (`runId`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `uk_run_alias` (`alias`),
  KEY `fk_run_sequencerReference` (`instrumentId`),
  KEY `fk_run_lastModifier_user` (`lastModifier`),
  KEY `fk_run_sequencingParameters` (`sequencingParameters_parametersId`),
  KEY `fk_run_creator` (`creator`),
  KEY `startDate_Run` (`startDate`),
  KEY `run_alias_index` (`alias`),
  KEY `fk_run_sequencingKit` (`sequencingKitId`),
  KEY `fk_run_approver` (`dataReviewerId`),
  KEY `fk_run_sop` (`sopId`),
  KEY `fk_run_qcUser` (`qcUser`),
  CONSTRAINT `fk_run_approver` FOREIGN KEY (`dataReviewerId`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_run_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_run_instrument` FOREIGN KEY (`instrumentId`) REFERENCES `Instrument` (`instrumentId`),
  CONSTRAINT `fk_run_lastModifier_user` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_run_qcUser` FOREIGN KEY (`qcUser`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_run_sequencingKit` FOREIGN KEY (`sequencingKitId`) REFERENCES `KitDescriptor` (`kitDescriptorId`),
  CONSTRAINT `fk_run_sequencingParameters` FOREIGN KEY (`sequencingParameters_parametersId`) REFERENCES `SequencingParameters` (`parametersId`),
  CONSTRAINT `fk_run_sop` FOREIGN KEY (`sopId`) REFERENCES `Sop` (`sopId`)
) ENGINE=InnoDB AUTO_INCREMENT=8120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunChangeLog`
--

DROP TABLE IF EXISTS `RunChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunChangeLog` (
  `runChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `runId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`runChangeLogId`),
  KEY `fk_runChangeLog_run` (`runId`),
  KEY `fk_runChangeLog_user` (`userId`),
  KEY `RunChangeLogDerivedInfo` (`runId`,`changeTime`),
  CONSTRAINT `fk_runChangeLog_run` FOREIGN KEY (`runId`) REFERENCES `Run` (`runId`) ON DELETE CASCADE,
  CONSTRAINT `fk_runChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=238895 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunIllumina`
--

DROP TABLE IF EXISTS `RunIllumina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunIllumina` (
  `runId` bigint NOT NULL,
  `callCycle` int DEFAULT NULL,
  `imgCycle` int DEFAULT NULL,
  `numCycles` int DEFAULT NULL,
  `scoreCycle` int DEFAULT NULL,
  `pairedEnd` tinyint(1) NOT NULL DEFAULT '1',
  `runBasesMask` varchar(100) DEFAULT NULL,
  `workflowType` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`runId`),
  CONSTRAINT `runillumina_run_runid` FOREIGN KEY (`runId`) REFERENCES `Run` (`runId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunIonTorrent`
--

DROP TABLE IF EXISTS `RunIonTorrent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunIonTorrent` (
  `runId` bigint NOT NULL,
  PRIMARY KEY (`runId`),
  CONSTRAINT `runiontorrent_run_runid` FOREIGN KEY (`runId`) REFERENCES `Run` (`runId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunLS454`
--

DROP TABLE IF EXISTS `RunLS454`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunLS454` (
  `runId` bigint NOT NULL,
  `cycles` int DEFAULT NULL,
  `pairedEnd` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`runId`),
  CONSTRAINT `runls454_run_runid` FOREIGN KEY (`runId`) REFERENCES `Run` (`runId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunLibraryQcStatus`
--

DROP TABLE IF EXISTS `RunLibraryQcStatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunLibraryQcStatus` (
  `statusId` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(50) NOT NULL,
  `qcPassed` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`statusId`),
  UNIQUE KEY `uk_runLibraryQcStatus_description` (`description`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunOxfordNanopore`
--

DROP TABLE IF EXISTS `RunOxfordNanopore`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunOxfordNanopore` (
  `runId` bigint NOT NULL AUTO_INCREMENT,
  `minKnowVersion` varchar(100) DEFAULT NULL,
  `protocolVersion` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`runId`),
  CONSTRAINT `FK_OxfordNanopore_Run` FOREIGN KEY (`runId`) REFERENCES `Run` (`runId`)
) ENGINE=InnoDB AUTO_INCREMENT=6833 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunPacBio`
--

DROP TABLE IF EXISTS `RunPacBio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunPacBio` (
  `runId` bigint NOT NULL,
  PRIMARY KEY (`runId`),
  CONSTRAINT `runpacbio_run_runid` FOREIGN KEY (`runId`) REFERENCES `Run` (`runId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunPurpose`
--

DROP TABLE IF EXISTS `RunPurpose`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunPurpose` (
  `purposeId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(50) NOT NULL,
  PRIMARY KEY (`purposeId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunSolid`
--

DROP TABLE IF EXISTS `RunSolid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunSolid` (
  `runId` bigint NOT NULL,
  `pairedEnd` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`runId`),
  CONSTRAINT `runsolid_run_runid` FOREIGN KEY (`runId`) REFERENCES `Run` (`runId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunUltima`
--

DROP TABLE IF EXISTS `RunUltima`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RunUltima` (
  `runId` bigint NOT NULL,
  PRIMARY KEY (`runId`),
  CONSTRAINT `fk_runultima_run` FOREIGN KEY (`runId`) REFERENCES `Run` (`runId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Run_Attachment`
--

DROP TABLE IF EXISTS `Run_Attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Run_Attachment` (
  `runId` bigint NOT NULL,
  `attachmentId` bigint NOT NULL,
  PRIMARY KEY (`runId`,`attachmentId`),
  KEY `fk_run_attachment` (`attachmentId`),
  CONSTRAINT `fk_attachment_run` FOREIGN KEY (`runId`) REFERENCES `Run` (`runId`),
  CONSTRAINT `fk_run_attachment` FOREIGN KEY (`attachmentId`) REFERENCES `Attachment` (`attachmentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Run_Note`
--

DROP TABLE IF EXISTS `Run_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Run_Note` (
  `run_runId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`run_runId`,`notes_noteId`),
  KEY `RunNote_Note_FK` (`notes_noteId`),
  CONSTRAINT `RunNote_Note_FK` FOREIGN KEY (`notes_noteId`) REFERENCES `Note` (`noteId`),
  CONSTRAINT `RunNote_Run_FK` FOREIGN KEY (`run_runId`) REFERENCES `Run` (`runId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Run_Partition`
--

DROP TABLE IF EXISTS `Run_Partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Run_Partition` (
  `runId` bigint NOT NULL,
  `partitionId` bigint NOT NULL,
  `partitionQcTypeId` bigint DEFAULT NULL,
  `notes` varchar(1024) DEFAULT NULL,
  `purposeId` bigint NOT NULL,
  `lastModifier` bigint NOT NULL,
  PRIMARY KEY (`runId`,`partitionId`),
  KEY `fk_rpq_partition_partitionId` (`partitionId`),
  KEY `fk_rpq_partitiontypeqc_partitiontypeqc` (`partitionQcTypeId`),
  KEY `runPartition_purpose` (`purposeId`),
  KEY `runPartition_lastModifier` (`lastModifier`),
  CONSTRAINT `fk_rpq_partition_partitionId` FOREIGN KEY (`partitionId`) REFERENCES `_Partition` (`partitionId`),
  CONSTRAINT `fk_rpq_partitiontypeqc_partitiontypeqc` FOREIGN KEY (`partitionQcTypeId`) REFERENCES `PartitionQCType` (`partitionQcTypeId`),
  CONSTRAINT `fk_rpq_run_runId` FOREIGN KEY (`runId`) REFERENCES `Run` (`runId`),
  CONSTRAINT `runPartition_lastModifier` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `runPartition_purpose` FOREIGN KEY (`purposeId`) REFERENCES `RunPurpose` (`purposeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Run_Partition_LibraryAliquot`
--

DROP TABLE IF EXISTS `Run_Partition_LibraryAliquot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Run_Partition_LibraryAliquot` (
  `runId` bigint NOT NULL,
  `partitionId` bigint NOT NULL,
  `aliquotId` bigint NOT NULL,
  `purposeId` bigint DEFAULT NULL,
  `lastModifier` bigint NOT NULL,
  `qcNote` varchar(255) DEFAULT NULL,
  `qcUser` bigint DEFAULT NULL,
  `qcDate` date DEFAULT NULL,
  `statusId` bigint DEFAULT NULL,
  `dataReview` tinyint(1) DEFAULT NULL,
  `dataReviewerId` bigint DEFAULT NULL,
  `dataReviewDate` date DEFAULT NULL,
  PRIMARY KEY (`runId`,`partitionId`,`aliquotId`),
  KEY `runAliquot_partition` (`partitionId`),
  KEY `runAliquot_aliquot` (`aliquotId`),
  KEY `runAliquot_purpose` (`purposeId`),
  KEY `runAliquot_lastModifier` (`lastModifier`),
  KEY `fk_runPartitionLibraryAliquot_qcUser` (`qcUser`),
  KEY `fk_runAliquot_status` (`statusId`),
  KEY `fk_run_aliquot_dataReviewer` (`dataReviewerId`),
  CONSTRAINT `fk_run_aliquot_dataReviewer` FOREIGN KEY (`dataReviewerId`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_runAliquot_status` FOREIGN KEY (`statusId`) REFERENCES `RunLibraryQcStatus` (`statusId`),
  CONSTRAINT `fk_runPartitionLibraryAliquot_qcUser` FOREIGN KEY (`qcUser`) REFERENCES `User` (`userId`),
  CONSTRAINT `runAliquot_aliquot` FOREIGN KEY (`aliquotId`) REFERENCES `LibraryAliquot` (`aliquotId`),
  CONSTRAINT `runAliquot_lastModifier` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `runAliquot_partition` FOREIGN KEY (`partitionId`) REFERENCES `_Partition` (`partitionId`),
  CONSTRAINT `runAliquot_purpose` FOREIGN KEY (`purposeId`) REFERENCES `RunPurpose` (`purposeId`),
  CONSTRAINT `runAliquot_run` FOREIGN KEY (`runId`) REFERENCES `Run` (`runId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Run_SequencerPartitionContainer`
--

DROP TABLE IF EXISTS `Run_SequencerPartitionContainer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Run_SequencerPartitionContainer` (
  `Run_runId` bigint NOT NULL,
  `containers_containerId` bigint NOT NULL,
  `positionId` bigint DEFAULT NULL,
  `sequencingParametersId` bigint DEFAULT NULL,
  PRIMARY KEY (`Run_runId`,`containers_containerId`),
  UNIQUE KEY `uk_run_position` (`Run_runId`,`positionId`),
  KEY `containers_containerId` (`containers_containerId`),
  KEY `fk_run_container_position` (`positionId`),
  KEY `fk_Run_SequencerPartitionContainer_sequencingParameters` (`sequencingParametersId`),
  CONSTRAINT `fk_run_container_position` FOREIGN KEY (`positionId`) REFERENCES `InstrumentPosition` (`positionId`),
  CONSTRAINT `fk_Run_SequencerPartitionContainer_sequencingParameters` FOREIGN KEY (`sequencingParametersId`) REFERENCES `SequencingParameters` (`parametersId`),
  CONSTRAINT `Run_SequencerPartitionContainer_ibfk_1` FOREIGN KEY (`Run_runId`) REFERENCES `Run` (`runId`),
  CONSTRAINT `Run_SequencerPartitionContainer_ibfk_2` FOREIGN KEY (`containers_containerId`) REFERENCES `SequencerPartitionContainer` (`containerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Sample`
--

DROP TABLE IF EXISTS `Sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Sample` (
  `sampleId` bigint NOT NULL AUTO_INCREMENT,
  `accession` varchar(50) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `sampleType` varchar(50) NOT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `project_projectId` bigint NOT NULL,
  `taxonIdentifier` varchar(255) DEFAULT NULL,
  `lastModifier` bigint NOT NULL DEFAULT '1',
  `discarded` tinyint(1) NOT NULL DEFAULT '0',
  `volume` decimal(16,10) DEFAULT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `concentrationUnits` varchar(30) DEFAULT NULL,
  `volumeUnits` varchar(30) DEFAULT NULL,
  `concentration` decimal(14,10) DEFAULT NULL,
  `initialVolume` decimal(16,10) DEFAULT NULL,
  `sequencingControlTypeId` bigint DEFAULT NULL,
  `discriminator` varchar(50) NOT NULL DEFAULT 'Sample',
  `sampleClassId` bigint DEFAULT NULL,
  `detailedQcStatusId` bigint DEFAULT NULL,
  `subprojectId` bigint DEFAULT NULL,
  `archived` bit(1) DEFAULT NULL,
  `parentId` bigint DEFAULT NULL,
  `siblingNumber` int DEFAULT NULL,
  `groupId` varchar(100) DEFAULT NULL,
  `groupDescription` varchar(255) DEFAULT NULL,
  `isSynthetic` tinyint(1) DEFAULT NULL,
  `nonStandardAlias` tinyint(1) DEFAULT NULL,
  `preMigrationId` bigint DEFAULT NULL,
  `detailedQcStatusNote` varchar(500) DEFAULT NULL,
  `creationDate` date DEFAULT NULL,
  `volumeUsed` decimal(16,10) DEFAULT NULL,
  `ngUsed` decimal(14,10) DEFAULT NULL,
  `externalName` varchar(255) DEFAULT NULL,
  `donorSex` varchar(50) DEFAULT NULL,
  `consentLevel` varchar(50) DEFAULT NULL,
  `tissueOriginId` bigint DEFAULT NULL,
  `tissueTypeId` bigint DEFAULT NULL,
  `secondaryIdentifier` varchar(255) DEFAULT NULL,
  `labId` bigint DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `passageNumber` int DEFAULT NULL,
  `tubeNumber` int DEFAULT NULL,
  `timesReceived` int DEFAULT NULL,
  `tissueMaterialId` bigint DEFAULT NULL,
  `initialSlides` int DEFAULT NULL,
  `thickness` int DEFAULT NULL,
  `stain` bigint DEFAULT NULL,
  `slides` int DEFAULT NULL,
  `percentTumour` decimal(11,8) DEFAULT NULL,
  `percentNecrosis` decimal(11,8) DEFAULT NULL,
  `markedAreaSize` decimal(11,8) DEFAULT NULL,
  `markedAreaPercentTumour` decimal(11,8) DEFAULT NULL,
  `slidesConsumed` int DEFAULT NULL,
  `tissuePieceType` bigint DEFAULT NULL,
  `referenceSlideId` bigint DEFAULT NULL,
  `initialCellConcentration` decimal(14,10) DEFAULT NULL,
  `digestion` varchar(255) DEFAULT NULL,
  `strStatus` varchar(50) DEFAULT NULL,
  `dnaseTreated` tinyint(1) DEFAULT NULL,
  `targetCellRecovery` int DEFAULT NULL,
  `cellViability` decimal(14,10) DEFAULT NULL,
  `loadingCellConcentration` decimal(14,10) DEFAULT NULL,
  `samplePurposeId` bigint DEFAULT NULL,
  `inputIntoLibrary` decimal(14,10) DEFAULT NULL,
  `scientificNameId` bigint NOT NULL,
  `sopId` bigint DEFAULT NULL,
  `qcUser` bigint DEFAULT NULL,
  `timepoint` varchar(50) DEFAULT NULL,
  `qcDate` date DEFAULT NULL,
  `requisitionId` bigint DEFAULT NULL,
  `indexId` bigint DEFAULT NULL,
  PRIMARY KEY (`sampleId`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `identificationBarcode` (`identificationBarcode`),
  UNIQUE KEY `uk_sample_preMigrationId` (`preMigrationId`),
  KEY `Sample_project_projectId_sampleId` (`project_projectId`,`sampleId`),
  KEY `fk_sample_lastModifier_user` (`lastModifier`),
  KEY `fk_sample_creator` (`creator`),
  KEY `lastModified_Sample` (`lastModified`),
  KEY `fk_sample_sequencingControlType` (`sequencingControlTypeId`),
  KEY `fk_sample_detailedQcStatus` (`detailedQcStatusId`),
  KEY `fk_sample_sampleClass` (`sampleClassId`),
  KEY `fk_sample_subproject` (`subprojectId`),
  KEY `fk_sample_parent` (`parentId`),
  KEY `fk_sample_lab` (`labId`),
  KEY `fk_sample_tissueOrigin` (`tissueOriginId`),
  KEY `fk_sample_tissueType` (`tissueTypeId`),
  KEY `fk_sample_tissueMaterial` (`tissueMaterialId`),
  KEY `fk_sample_stain` (`stain`),
  KEY `fk_sample_referenceSlide` (`referenceSlideId`),
  KEY `fk_sample_tissuePieceType` (`tissuePieceType`),
  KEY `fk_sample_samplePurpose` (`samplePurposeId`),
  KEY `fk_sample_scientificName` (`scientificNameId`),
  KEY `fk_sample_sop` (`sopId`),
  KEY `fk_sample_qcUser` (`qcUser`),
  KEY `fk_sample_requisition` (`requisitionId`),
  KEY `fk_sample_sampleIndex` (`indexId`),
  CONSTRAINT `fk_sample_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_sample_detailedQcStatus` FOREIGN KEY (`detailedQcStatusId`) REFERENCES `DetailedQcStatus` (`detailedQcStatusId`),
  CONSTRAINT `fk_sample_lab` FOREIGN KEY (`labId`) REFERENCES `Lab` (`labId`),
  CONSTRAINT `fk_sample_lastModifier_user` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_sample_parent` FOREIGN KEY (`parentId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `fk_sample_project` FOREIGN KEY (`project_projectId`) REFERENCES `Project` (`projectId`),
  CONSTRAINT `fk_sample_qcUser` FOREIGN KEY (`qcUser`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_sample_referenceSlide` FOREIGN KEY (`referenceSlideId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `fk_sample_requisition` FOREIGN KEY (`requisitionId`) REFERENCES `Requisition` (`requisitionId`),
  CONSTRAINT `fk_sample_sampleClass` FOREIGN KEY (`sampleClassId`) REFERENCES `SampleClass` (`sampleClassId`),
  CONSTRAINT `fk_sample_sampleIndex` FOREIGN KEY (`indexId`) REFERENCES `SampleIndex` (`indexId`),
  CONSTRAINT `fk_sample_samplePurpose` FOREIGN KEY (`samplePurposeId`) REFERENCES `SamplePurpose` (`samplePurposeId`),
  CONSTRAINT `fk_sample_scientificName` FOREIGN KEY (`scientificNameId`) REFERENCES `ScientificName` (`scientificNameId`),
  CONSTRAINT `fk_sample_sequencingControlType` FOREIGN KEY (`sequencingControlTypeId`) REFERENCES `SequencingControlType` (`sequencingControlTypeId`),
  CONSTRAINT `fk_sample_sop` FOREIGN KEY (`sopId`) REFERENCES `Sop` (`sopId`),
  CONSTRAINT `fk_sample_stain` FOREIGN KEY (`stain`) REFERENCES `Stain` (`stainId`),
  CONSTRAINT `fk_sample_subproject` FOREIGN KEY (`subprojectId`) REFERENCES `Subproject` (`subprojectId`),
  CONSTRAINT `fk_sample_tissueMaterial` FOREIGN KEY (`tissueMaterialId`) REFERENCES `TissueMaterial` (`tissueMaterialId`),
  CONSTRAINT `fk_sample_tissueOrigin` FOREIGN KEY (`tissueOriginId`) REFERENCES `TissueOrigin` (`tissueOriginId`),
  CONSTRAINT `fk_sample_tissuePieceType` FOREIGN KEY (`tissuePieceType`) REFERENCES `TissuePieceType` (`tissuePieceTypeId`),
  CONSTRAINT `fk_sample_tissueType` FOREIGN KEY (`tissueTypeId`) REFERENCES `TissueType` (`tissueTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=651127 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `SampleBoxPosition`
--

DROP TABLE IF EXISTS `SampleBoxPosition`;
/*!50001 DROP VIEW IF EXISTS `SampleBoxPosition`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `SampleBoxPosition` AS SELECT 
 1 AS `sampleId`,
 1 AS `boxId`,
 1 AS `position`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `SampleChangeLog`
--

DROP TABLE IF EXISTS `SampleChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleChangeLog` (
  `sampleChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `sampleId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sampleChangeLogId`),
  KEY `fk_sampleChangeLog_sample` (`sampleId`),
  KEY `fk_sampleChangeLog_user` (`userId`),
  KEY `SampleChangeLogDerivedInfo` (`sampleId`,`changeTime`),
  CONSTRAINT `fk_sampleChangeLog_sample` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`) ON DELETE CASCADE,
  CONSTRAINT `fk_sampleChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1399479 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SampleClass`
--

DROP TABLE IF EXISTS `SampleClass`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleClass` (
  `sampleClassId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `sampleCategory` varchar(255) NOT NULL,
  `suffix` varchar(5) DEFAULT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  `directCreationAllowed` tinyint(1) DEFAULT '1',
  `sampleSubcategory` varchar(50) DEFAULT NULL,
  `v2NamingCode` varchar(2) DEFAULT NULL,
  `defaultSampleTypeId` bigint DEFAULT NULL,
  PRIMARY KEY (`sampleClassId`),
  UNIQUE KEY `uk_sampleClass_alias` (`alias`),
  KEY `FK30w1j1qqj9qoxgpopa0uq9jeh` (`createdBy`),
  KEY `FKoyjjomji7fvjrlgevecxqv12o` (`updatedBy`),
  KEY `fk_sampleClass_defaultSampleType` (`defaultSampleTypeId`),
  CONSTRAINT `FK30w1j1qqj9qoxgpopa0uq9jeh` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_sampleClass_defaultSampleType` FOREIGN KEY (`defaultSampleTypeId`) REFERENCES `SampleType` (`typeId`),
  CONSTRAINT `FKoyjjomji7fvjrlgevecxqv12o` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `SampleDistributionView`
--

DROP TABLE IF EXISTS `SampleDistributionView`;
/*!50001 DROP VIEW IF EXISTS `SampleDistributionView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `SampleDistributionView` AS SELECT 
 1 AS `sampleId`,
 1 AS `distributed`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `SampleHierarchy`
--

DROP TABLE IF EXISTS `SampleHierarchy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleHierarchy` (
  `sampleId` bigint NOT NULL,
  `identityId` bigint DEFAULT NULL,
  `tissueId` bigint DEFAULT NULL,
  PRIMARY KEY (`sampleId`),
  KEY `fk_sampleHierarchy_identity` (`identityId`),
  KEY `fk_sampleHierarchy_tissue` (`tissueId`),
  CONSTRAINT `fk_sampleHierarchy_identity` FOREIGN KEY (`identityId`) REFERENCES `Sample` (`sampleId`) ON DELETE CASCADE,
  CONSTRAINT `fk_sampleHierarchy_sample` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`) ON DELETE CASCADE,
  CONSTRAINT `fk_sampleHierarchy_tissue` FOREIGN KEY (`tissueId`) REFERENCES `Sample` (`sampleId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SampleIndex`
--

DROP TABLE IF EXISTS `SampleIndex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleIndex` (
  `indexId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(24) NOT NULL,
  `indexFamilyId` bigint NOT NULL,
  PRIMARY KEY (`indexId`),
  UNIQUE KEY `uk_sampleIndex_family_name` (`indexFamilyId`,`name`),
  CONSTRAINT `fk_sampleIndex_sampleIndexFamily` FOREIGN KEY (`indexFamilyId`) REFERENCES `SampleIndexFamily` (`indexFamilyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SampleIndexFamily`
--

DROP TABLE IF EXISTS `SampleIndexFamily`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleIndexFamily` (
  `indexFamilyId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`indexFamilyId`),
  UNIQUE KEY `uk_sampleIndexFamily_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SampleNumberPerProject`
--

DROP TABLE IF EXISTS `SampleNumberPerProject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleNumberPerProject` (
  `sampleNumberPerProjectId` bigint NOT NULL AUTO_INCREMENT,
  `projectId` bigint NOT NULL,
  `highestSampleNumber` int NOT NULL,
  `padding` int NOT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sampleNumberPerProjectId`),
  UNIQUE KEY `UK_dw1vcaxddbxopw3imu0rxm1ww` (`projectId`),
  KEY `FKjxikp47dpisx3tr3vkxuknfeh` (`createdBy`),
  KEY `FKlgd3qd6d25aawdl1ldqvc1vxf` (`updatedBy`),
  CONSTRAINT `FKjxikp47dpisx3tr3vkxuknfeh` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKlgd3qd6d25aawdl1ldqvc1vxf` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKpbhtha4po9so0lup7x3sxge5p` FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`)
) ENGINE=InnoDB AUTO_INCREMENT=949 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SamplePurpose`
--

DROP TABLE IF EXISTS `SamplePurpose`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SamplePurpose` (
  `samplePurposeId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`samplePurposeId`),
  UNIQUE KEY `UK_t1fmado2v5jf9troedycvnxfv` (`alias`),
  KEY `FKcgjgyju8kvxgi1uaceewhtmbt` (`createdBy`),
  KEY `FKf50vooqtktimgba328whal3o0` (`updatedBy`),
  CONSTRAINT `FKcgjgyju8kvxgi1uaceewhtmbt` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKf50vooqtktimgba328whal3o0` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SampleQC`
--

DROP TABLE IF EXISTS `SampleQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleQC` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `sample_sampleId` bigint NOT NULL,
  `creator` bigint NOT NULL,
  `date` date NOT NULL,
  `type` bigint NOT NULL,
  `results` decimal(16,10) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL,
  `instrumentId` bigint DEFAULT NULL,
  `kitLot` varchar(50) DEFAULT NULL,
  `kitDescriptorId` bigint DEFAULT NULL,
  PRIMARY KEY (`qcId`),
  KEY `FK_sample_qc_sample` (`sample_sampleId`),
  KEY `FK_sample_qc_creator` (`creator`),
  KEY `FK_sample_qc_type` (`type`),
  KEY `fk_sampleQc_instrument` (`instrumentId`),
  KEY `fk_sampleQc_kit` (`kitDescriptorId`),
  CONSTRAINT `FK_sample_qc_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_sample_qc_sample` FOREIGN KEY (`sample_sampleId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `FK_sample_qc_type` FOREIGN KEY (`type`) REFERENCES `QCType` (`qcTypeId`),
  CONSTRAINT `fk_sampleQc_instrument` FOREIGN KEY (`instrumentId`) REFERENCES `Instrument` (`instrumentId`),
  CONSTRAINT `fk_sampleQc_kit` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`)
) ENGINE=InnoDB AUTO_INCREMENT=13616 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SampleQcControl`
--

DROP TABLE IF EXISTS `SampleQcControl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleQcControl` (
  `qcControlId` bigint NOT NULL AUTO_INCREMENT,
  `qcId` bigint NOT NULL,
  `controlId` bigint NOT NULL,
  `lot` varchar(50) NOT NULL,
  `qcPassed` tinyint(1) NOT NULL,
  PRIMARY KEY (`qcControlId`),
  KEY `fk_sampleQcControl_qc` (`qcId`),
  KEY `fk_sampleQcControl_control` (`controlId`),
  CONSTRAINT `fk_sampleQcControl_control` FOREIGN KEY (`controlId`) REFERENCES `QcControl` (`controlId`),
  CONSTRAINT `fk_sampleQcControl_qc` FOREIGN KEY (`qcId`) REFERENCES `SampleQC` (`qcId`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `SampleReceiptView`
--

DROP TABLE IF EXISTS `SampleReceiptView`;
/*!50001 DROP VIEW IF EXISTS `SampleReceiptView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `SampleReceiptView` AS SELECT 
 1 AS `sampleId`,
 1 AS `transferId`,
 1 AS `transferTime`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `SampleType`
--

DROP TABLE IF EXISTS `SampleType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleType` (
  `typeId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`typeId`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SampleValidRelationship`
--

DROP TABLE IF EXISTS `SampleValidRelationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SampleValidRelationship` (
  `sampleValidRelationshipId` bigint NOT NULL AUTO_INCREMENT,
  `parentId` bigint NOT NULL,
  `childId` bigint NOT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`sampleValidRelationshipId`),
  UNIQUE KEY `UK6h6c3shh0sluresucsxf5ixb7` (`parentId`,`childId`),
  KEY `FKk7dtvey4xjbrt9qdwkjl00wlb` (`childId`),
  KEY `FKfk3wsykea5rk3svf1n702eti0` (`createdBy`),
  KEY `FKb9uqsxsfb2fxnl8jjo8p5ifer` (`updatedBy`),
  CONSTRAINT `FK9tn7y9gmki3ygroc0fd3288vm` FOREIGN KEY (`parentId`) REFERENCES `SampleClass` (`sampleClassId`),
  CONSTRAINT `FKb9uqsxsfb2fxnl8jjo8p5ifer` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKfk3wsykea5rk3svf1n702eti0` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKk7dtvey4xjbrt9qdwkjl00wlb` FOREIGN KEY (`childId`) REFERENCES `SampleClass` (`sampleClassId`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Sample_Attachment`
--

DROP TABLE IF EXISTS `Sample_Attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Sample_Attachment` (
  `sampleId` bigint NOT NULL,
  `attachmentId` bigint NOT NULL,
  PRIMARY KEY (`sampleId`,`attachmentId`),
  KEY `fk_sample_attachment` (`attachmentId`),
  CONSTRAINT `fk_attachment_sample` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `fk_sample_attachment` FOREIGN KEY (`attachmentId`) REFERENCES `Attachment` (`attachmentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Sample_Note`
--

DROP TABLE IF EXISTS `Sample_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Sample_Note` (
  `sample_sampleId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`sample_sampleId`,`notes_noteId`),
  KEY `SampleNote_Note_FK` (`notes_noteId`),
  CONSTRAINT `SampleNote_Note_FK` FOREIGN KEY (`notes_noteId`) REFERENCES `Note` (`noteId`),
  CONSTRAINT `SampleNote_Sample_FK` FOREIGN KEY (`sample_sampleId`) REFERENCES `Sample` (`sampleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ScientificName`
--

DROP TABLE IF EXISTS `ScientificName`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ScientificName` (
  `scientificNameId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(100) NOT NULL,
  PRIMARY KEY (`scientificNameId`),
  UNIQUE KEY `uk_scientificName_alias` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SequencerPartitionContainer`
--

DROP TABLE IF EXISTS `SequencerPartitionContainer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SequencerPartitionContainer` (
  `containerId` bigint NOT NULL AUTO_INCREMENT,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `lastModifier` bigint NOT NULL DEFAULT '1',
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `clusteringKit` bigint DEFAULT NULL,
  `multiplexingKit` bigint DEFAULT NULL,
  `sequencingContainerModelId` bigint NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `clusteringKitLot` varchar(100) DEFAULT NULL,
  `multiplexingKitLot` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`containerId`),
  UNIQUE KEY `uk_container_identificationBarcode` (`identificationBarcode`),
  KEY `sequencerpartitioncontainer_user_userid_fkey` (`lastModifier`),
  KEY `fk_container_creator` (`creator`),
  KEY `container_clusteringKit_kitDescriptor_fkey` (`clusteringKit`),
  KEY `container_multiplexingKit_kitDescriptor_fkey` (`multiplexingKit`),
  KEY `fk_SequencerPartitionContainer_model` (`sequencingContainerModelId`),
  KEY `lastModified_Container` (`lastModified`),
  CONSTRAINT `container_clusteringKit_kitDescriptor_fkey` FOREIGN KEY (`clusteringKit`) REFERENCES `KitDescriptor` (`kitDescriptorId`),
  CONSTRAINT `container_multiplexingKit_kitDescriptor_fkey` FOREIGN KEY (`multiplexingKit`) REFERENCES `KitDescriptor` (`kitDescriptorId`),
  CONSTRAINT `fk_container_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_SequencerPartitionContainer_model` FOREIGN KEY (`sequencingContainerModelId`) REFERENCES `SequencingContainerModel` (`sequencingContainerModelId`)
) ENGINE=InnoDB AUTO_INCREMENT=6957 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SequencerPartitionContainerChangeLog`
--

DROP TABLE IF EXISTS `SequencerPartitionContainerChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SequencerPartitionContainerChangeLog` (
  `containerChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `containerId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`containerChangeLogId`),
  KEY `fk_containerChangeLog_box` (`containerId`),
  KEY `fk_containerChangeLog_user` (`userId`),
  KEY `SequencerPartitionContainerChangeLogDerivedInfo` (`containerId`,`changeTime`),
  CONSTRAINT `fk_containerChangeLog_container` FOREIGN KEY (`containerId`) REFERENCES `SequencerPartitionContainer` (`containerId`) ON DELETE CASCADE,
  CONSTRAINT `fk_containerChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=53601 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SequencingContainerModel`
--

DROP TABLE IF EXISTS `SequencingContainerModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SequencingContainerModel` (
  `sequencingContainerModelId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `partitionCount` int NOT NULL,
  `platformType` varchar(255) NOT NULL,
  `fallback` tinyint(1) NOT NULL DEFAULT '0',
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`sequencingContainerModelId`),
  UNIQUE KEY `uk_sequencingContainerModel_platform_alias` (`platformType`,`alias`),
  UNIQUE KEY `uk_sequencingContainerModel_platform_barcode` (`platformType`,`identificationBarcode`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SequencingContainerModel_InstrumentModel`
--

DROP TABLE IF EXISTS `SequencingContainerModel_InstrumentModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SequencingContainerModel_InstrumentModel` (
  `sequencingContainerModelId` bigint NOT NULL,
  `instrumentModelId` bigint NOT NULL,
  PRIMARY KEY (`sequencingContainerModelId`,`instrumentModelId`),
  KEY `fk_SequencingContainerModel_Platform_platform` (`instrumentModelId`),
  CONSTRAINT `fk_sequencingContainerModel_instrumentModel` FOREIGN KEY (`instrumentModelId`) REFERENCES `InstrumentModel` (`instrumentModelId`),
  CONSTRAINT `fk_SequencingContainerModel_Platform_model` FOREIGN KEY (`sequencingContainerModelId`) REFERENCES `SequencingContainerModel` (`sequencingContainerModelId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SequencingControlType`
--

DROP TABLE IF EXISTS `SequencingControlType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SequencingControlType` (
  `sequencingControlTypeId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(50) NOT NULL,
  PRIMARY KEY (`sequencingControlTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SequencingOrder`
--

DROP TABLE IF EXISTS `SequencingOrder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SequencingOrder` (
  `sequencingOrderId` bigint NOT NULL AUTO_INCREMENT,
  `poolId` bigint NOT NULL,
  `partitions` int NOT NULL,
  `parametersId` bigint DEFAULT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL,
  `purposeId` bigint NOT NULL,
  `sequencingContainerModelId` bigint DEFAULT NULL,
  PRIMARY KEY (`sequencingOrderId`),
  KEY `fk_sequencingOrder_creator` (`createdBy`),
  KEY `fk_sequencingOrder_parameters` (`parametersId`),
  KEY `fk_sequencingOrder_pool` (`poolId`),
  KEY `fk_sequencingOrder_updater` (`updatedBy`),
  KEY `fk_sequencingOrder_purpose` (`purposeId`),
  KEY `fk_sequencingOrder_sequencingContainerModel` (`sequencingContainerModelId`),
  CONSTRAINT `fk_sequencingOrder_creator` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_sequencingOrder_parameters` FOREIGN KEY (`parametersId`) REFERENCES `SequencingParameters` (`parametersId`),
  CONSTRAINT `fk_sequencingOrder_pool` FOREIGN KEY (`poolId`) REFERENCES `Pool` (`poolId`),
  CONSTRAINT `fk_sequencingOrder_purpose` FOREIGN KEY (`purposeId`) REFERENCES `RunPurpose` (`purposeId`),
  CONSTRAINT `fk_sequencingOrder_sequencingContainerModel` FOREIGN KEY (`sequencingContainerModelId`) REFERENCES `SequencingContainerModel` (`sequencingContainerModelId`),
  CONSTRAINT `fk_sequencingOrder_updater` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1905 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `SequencingOrderFulfillmentView`
--

DROP TABLE IF EXISTS `SequencingOrderFulfillmentView`;
/*!50001 DROP VIEW IF EXISTS `SequencingOrderFulfillmentView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `SequencingOrderFulfillmentView` AS SELECT 
 1 AS `orderSummaryId`,
 1 AS `fulfilled`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `SequencingOrderLoadedPartitionView`
--

DROP TABLE IF EXISTS `SequencingOrderLoadedPartitionView`;
/*!50001 DROP VIEW IF EXISTS `SequencingOrderLoadedPartitionView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `SequencingOrderLoadedPartitionView` AS SELECT 
 1 AS `poolId`,
 1 AS `sequencingContainerModelId`,
 1 AS `loaded`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `SequencingOrderNoContainerModelFulfillmentView`
--

DROP TABLE IF EXISTS `SequencingOrderNoContainerModelFulfillmentView`;
/*!50001 DROP VIEW IF EXISTS `SequencingOrderNoContainerModelFulfillmentView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `SequencingOrderNoContainerModelFulfillmentView` AS SELECT 
 1 AS `orderSummaryId`,
 1 AS `fulfilled`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `SequencingOrderPartitionView`
--

DROP TABLE IF EXISTS `SequencingOrderPartitionView`;
/*!50001 DROP VIEW IF EXISTS `SequencingOrderPartitionView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `SequencingOrderPartitionView` AS SELECT 
 1 AS `partitionId`,
 1 AS `orderSummaryId`,
 1 AS `noContainerModelId`,
 1 AS `health`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `SequencingOrderSummaryView`
--

DROP TABLE IF EXISTS `SequencingOrderSummaryView`;
/*!50001 DROP VIEW IF EXISTS `SequencingOrderSummaryView`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `SequencingOrderSummaryView` AS SELECT 
 1 AS `orderSummaryId`,
 1 AS `poolId`,
 1 AS `sequencingContainerModelId`,
 1 AS `parametersId`,
 1 AS `requested`,
 1 AS `loaded`,
 1 AS `description`,
 1 AS `purpose`,
 1 AS `lastUpdated`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `SequencingParameters`
--

DROP TABLE IF EXISTS `SequencingParameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SequencingParameters` (
  `parametersId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `instrumentModelId` bigint NOT NULL,
  `readLength` int NOT NULL DEFAULT '0',
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `chemistry` varchar(255) DEFAULT 'UNKNOWN',
  `runType` varchar(255) DEFAULT NULL,
  `readLength2` int NOT NULL,
  `movieTime` int unsigned DEFAULT NULL,
  `flows` smallint unsigned DEFAULT NULL,
  PRIMARY KEY (`parametersId`),
  UNIQUE KEY `uk_sequencingParameters_name_model` (`name`,`instrumentModelId`),
  KEY `sequencingParameters_createUser_fkey` (`createdBy`),
  KEY `sequencingParameters_updateUser_fkey` (`updatedBy`),
  KEY `parameter_platformId_fkey` (`instrumentModelId`),
  CONSTRAINT `fk_sequencingParameters_instrumentModel` FOREIGN KEY (`instrumentModelId`) REFERENCES `InstrumentModel` (`instrumentModelId`),
  CONSTRAINT `sequencingParameters_createUser_fkey` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `sequencingParameters_updateUser_fkey` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=187 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ServiceRecord`
--

DROP TABLE IF EXISTS `ServiceRecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ServiceRecord` (
  `recordId` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `details` mediumtext,
  `servicedBy` varchar(30) DEFAULT NULL,
  `referenceNumber` varchar(30) DEFAULT NULL,
  `serviceDate` date NOT NULL,
  `startTime` timestamp NULL DEFAULT NULL,
  `endTime` timestamp NULL DEFAULT NULL,
  `outOfService` tinyint(1) NOT NULL DEFAULT '1',
  `positionId` bigint DEFAULT NULL,
  PRIMARY KEY (`recordId`),
  KEY `fk_serviceRecord_position` (`positionId`),
  CONSTRAINT `fk_serviceRecord_position` FOREIGN KEY (`positionId`) REFERENCES `InstrumentPosition` (`positionId`)
) ENGINE=InnoDB AUTO_INCREMENT=2107 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ServiceRecord_Attachment`
--

DROP TABLE IF EXISTS `ServiceRecord_Attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ServiceRecord_Attachment` (
  `recordId` bigint NOT NULL,
  `attachmentId` bigint NOT NULL,
  PRIMARY KEY (`recordId`,`attachmentId`),
  KEY `fk_serviceRecord_attachment` (`attachmentId`),
  CONSTRAINT `fk_attachment_serviceRecord` FOREIGN KEY (`recordId`) REFERENCES `ServiceRecord` (`recordId`),
  CONSTRAINT `fk_serviceRecord_attachment` FOREIGN KEY (`attachmentId`) REFERENCES `Attachment` (`attachmentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Sop`
--

DROP TABLE IF EXISTS `Sop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Sop` (
  `sopId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(200) NOT NULL,
  `version` varchar(50) NOT NULL,
  `category` varchar(20) NOT NULL,
  `url` varchar(255) NOT NULL,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`sopId`),
  UNIQUE KEY `uk_sop_version` (`category`,`alias`,`version`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Stain`
--

DROP TABLE IF EXISTS `Stain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Stain` (
  `stainId` bigint NOT NULL AUTO_INCREMENT,
  `stainCategoryId` bigint DEFAULT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`stainId`),
  UNIQUE KEY `stain_name` (`name`),
  KEY `stain_staincategory` (`stainCategoryId`),
  CONSTRAINT `stain_staincategory` FOREIGN KEY (`stainCategoryId`) REFERENCES `StainCategory` (`stainCategoryId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StainCategory`
--

DROP TABLE IF EXISTS `StainCategory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StainCategory` (
  `stainCategoryId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`stainCategoryId`),
  UNIQUE KEY `staincategory_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StepBox`
--

DROP TABLE IF EXISTS `StepBox`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StepBox` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  `boxId` bigint NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  KEY `fk_StepBox_Box` (`boxId`),
  CONSTRAINT `fk_StepBox_Box` FOREIGN KEY (`boxId`) REFERENCES `Box` (`boxId`),
  CONSTRAINT `fk_StepBox_Step` FOREIGN KEY (`workflowProgressId`, `stepNumber`) REFERENCES `WorkflowProgressStep` (`workflowProgressId`, `stepNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StepBoxPosition`
--

DROP TABLE IF EXISTS `StepBoxPosition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StepBoxPosition` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  `input` varchar(20) NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  CONSTRAINT `fk_StepBoxPosition_step` FOREIGN KEY (`workflowProgressId`, `stepNumber`) REFERENCES `WorkflowProgressStep` (`workflowProgressId`, `stepNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StepInteger`
--

DROP TABLE IF EXISTS `StepInteger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StepInteger` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  `input` int NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  CONSTRAINT `StepInteger_ibfk_1` FOREIGN KEY (`workflowProgressId`, `stepNumber`) REFERENCES `WorkflowProgressStep` (`workflowProgressId`, `stepNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StepPool`
--

DROP TABLE IF EXISTS `StepPool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StepPool` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  `poolId` bigint NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  KEY `poolId` (`poolId`),
  CONSTRAINT `StepPool_ibfk_1` FOREIGN KEY (`workflowProgressId`, `stepNumber`) REFERENCES `WorkflowProgressStep` (`workflowProgressId`, `stepNumber`),
  CONSTRAINT `StepPool_ibfk_2` FOREIGN KEY (`poolId`) REFERENCES `Pool` (`poolId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StepPositiveDouble`
--

DROP TABLE IF EXISTS `StepPositiveDouble`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StepPositiveDouble` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  `input` float NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  CONSTRAINT `fk_StepPositiveDouble_step` FOREIGN KEY (`workflowProgressId`, `stepNumber`) REFERENCES `WorkflowProgressStep` (`workflowProgressId`, `stepNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StepPositiveInteger`
--

DROP TABLE IF EXISTS `StepPositiveInteger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StepPositiveInteger` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  `input` int unsigned NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  CONSTRAINT `fk_StepPositiveInteger_step` FOREIGN KEY (`workflowProgressId`, `stepNumber`) REFERENCES `WorkflowProgressStep` (`workflowProgressId`, `stepNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StepSample`
--

DROP TABLE IF EXISTS `StepSample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StepSample` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  `sampleId` bigint NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  KEY `sampleId` (`sampleId`),
  CONSTRAINT `StepSample_ibfk_1` FOREIGN KEY (`workflowProgressId`, `stepNumber`) REFERENCES `WorkflowProgressStep` (`workflowProgressId`, `stepNumber`),
  CONSTRAINT `StepSample_ibfk_2` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StepSequencerPartitionContainer`
--

DROP TABLE IF EXISTS `StepSequencerPartitionContainer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StepSequencerPartitionContainer` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  `containerId` bigint NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  KEY `fk_StepSequencerPartitionContainer_container` (`containerId`),
  CONSTRAINT `fk_StepSequencerPartitionContainer_container` FOREIGN KEY (`containerId`) REFERENCES `SequencerPartitionContainer` (`containerId`),
  CONSTRAINT `fk_StepSequencerPartitionContainer_step` FOREIGN KEY (`workflowProgressId`, `stepNumber`) REFERENCES `WorkflowProgressStep` (`workflowProgressId`, `stepNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StepSequencingContainerModel`
--

DROP TABLE IF EXISTS `StepSequencingContainerModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StepSequencingContainerModel` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  `sequencingContainerModelId` bigint NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  KEY `fk_StepSequencingContainerModel_model` (`sequencingContainerModelId`),
  CONSTRAINT `fk_StepSequencingContainerModel_model` FOREIGN KEY (`sequencingContainerModelId`) REFERENCES `SequencingContainerModel` (`sequencingContainerModelId`),
  CONSTRAINT `fk_StepSequencingContainerModel_step` FOREIGN KEY (`workflowProgressId`, `stepNumber`) REFERENCES `WorkflowProgressStep` (`workflowProgressId`, `stepNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StepSkip`
--

DROP TABLE IF EXISTS `StepSkip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StepSkip` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  CONSTRAINT `fk_StepSkip_step` FOREIGN KEY (`workflowProgressId`, `stepNumber`) REFERENCES `WorkflowProgressStep` (`workflowProgressId`, `stepNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StepString`
--

DROP TABLE IF EXISTS `StepString`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StepString` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  `input` varchar(20) NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  CONSTRAINT `fk_StepString_step` FOREIGN KEY (`workflowProgressId`, `stepNumber`) REFERENCES `WorkflowProgressStep` (`workflowProgressId`, `stepNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StorageLabel`
--

DROP TABLE IF EXISTS `StorageLabel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StorageLabel` (
  `labelId` bigint NOT NULL AUTO_INCREMENT,
  `label` varchar(100) NOT NULL,
  PRIMARY KEY (`labelId`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StorageLocation`
--

DROP TABLE IF EXISTS `StorageLocation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StorageLocation` (
  `locationId` bigint NOT NULL AUTO_INCREMENT,
  `parentLocationId` bigint DEFAULT NULL,
  `locationUnit` varchar(50) NOT NULL,
  `alias` varchar(255) NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModifier` bigint NOT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `probeId` varchar(50) DEFAULT NULL,
  `mapId` bigint DEFAULT NULL,
  `mapAnchor` varchar(100) DEFAULT NULL,
  `labelId` bigint DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`locationId`),
  UNIQUE KEY `uk_storagelocation_barcode` (`identificationBarcode`),
  UNIQUE KEY `uk_storagelocation_child` (`parentLocationId`,`locationUnit`,`alias`),
  UNIQUE KEY `storageLocation_probeId_uk` (`probeId`),
  KEY `name_StorageLocation` (`alias`),
  KEY `fk_storagelocation_creator` (`creator`),
  KEY `fk_storagelocation_modifier` (`lastModifier`),
  KEY `fk_storageLocation_map` (`mapId`),
  KEY `fk_storageLocation_label` (`labelId`),
  CONSTRAINT `fk_location_parent` FOREIGN KEY (`parentLocationId`) REFERENCES `StorageLocation` (`locationId`),
  CONSTRAINT `fk_storagelocation_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_storageLocation_label` FOREIGN KEY (`labelId`) REFERENCES `StorageLabel` (`labelId`),
  CONSTRAINT `fk_storageLocation_map` FOREIGN KEY (`mapId`) REFERENCES `StorageLocationMap` (`mapId`),
  CONSTRAINT `fk_storagelocation_modifier` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=2630 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StorageLocationChangeLog`
--

DROP TABLE IF EXISTS `StorageLocationChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StorageLocationChangeLog` (
  `storageLocationChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `locationId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`storageLocationChangeLogId`),
  KEY `fk_storageLocationChangeLog_storageLocation` (`locationId`),
  KEY `fk_storageLocationChangeLog_user` (`userId`),
  CONSTRAINT `fk_storageLocationChangeLog_storageLocation` FOREIGN KEY (`locationId`) REFERENCES `StorageLocation` (`locationId`) ON DELETE CASCADE,
  CONSTRAINT `fk_storageLocationChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=8834 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StorageLocationMap`
--

DROP TABLE IF EXISTS `StorageLocationMap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StorageLocationMap` (
  `mapId` bigint NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`mapId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StorageLocation_ServiceRecord`
--

DROP TABLE IF EXISTS `StorageLocation_ServiceRecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StorageLocation_ServiceRecord` (
  `recordId` bigint NOT NULL,
  `locationId` bigint NOT NULL,
  PRIMARY KEY (`recordId`,`locationId`),
  KEY `fk_storageLocationServiceRecord_storageLocation` (`locationId`),
  CONSTRAINT `fk_storageLocationServiceRecord_serviceRecord` FOREIGN KEY (`recordId`) REFERENCES `ServiceRecord` (`recordId`),
  CONSTRAINT `fk_storageLocationServiceRecord_storageLocation` FOREIGN KEY (`locationId`) REFERENCES `StorageLocation` (`locationId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Study`
--

DROP TABLE IF EXISTS `Study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Study` (
  `studyId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `accession` varchar(30) DEFAULT NULL,
  `project_projectId` bigint NOT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `lastModifier` bigint NOT NULL DEFAULT '1',
  `studyTypeId` bigint NOT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`studyId`),
  UNIQUE KEY `name` (`name`),
  KEY `study_studyTypeId` (`studyTypeId`),
  KEY `fk_study_project` (`project_projectId`),
  KEY `fk_study_lastModifier_user` (`lastModifier`),
  KEY `fk_study_creator` (`creator`),
  CONSTRAINT `fk_study_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_study_lastModifier_user` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_study_project` FOREIGN KEY (`project_projectId`) REFERENCES `Project` (`projectId`),
  CONSTRAINT `study_studyTypeId` FOREIGN KEY (`studyTypeId`) REFERENCES `StudyType` (`typeId`)
) ENGINE=InnoDB AUTO_INCREMENT=162 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudyChangeLog`
--

DROP TABLE IF EXISTS `StudyChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StudyChangeLog` (
  `studyChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `studyId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`studyChangeLogId`),
  KEY `fk_studyChangeLog_study` (`studyId`),
  KEY `fk_studyChangeLog_user` (`userId`),
  KEY `StudyChangeLogDerivedInfo` (`studyId`,`changeTime`),
  CONSTRAINT `fk_studyChangeLog_study` FOREIGN KEY (`studyId`) REFERENCES `Study` (`studyId`) ON DELETE CASCADE,
  CONSTRAINT `fk_studyChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudyType`
--

DROP TABLE IF EXISTS `StudyType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `StudyType` (
  `typeId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`typeId`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Submission`
--

DROP TABLE IF EXISTS `Submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Submission` (
  `submissionId` bigint NOT NULL AUTO_INCREMENT,
  `creationDate` date NOT NULL,
  `submittedDate` date DEFAULT NULL,
  `verified` tinyint(1) NOT NULL DEFAULT '0',
  `description` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `accession` varchar(50) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `completed` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`submissionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Submission_Experiment`
--

DROP TABLE IF EXISTS `Submission_Experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Submission_Experiment` (
  `submission_submissionId` bigint NOT NULL,
  `experiments_experimentId` bigint NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`experiments_experimentId`),
  KEY `fk_submission_experiment` (`experiments_experimentId`),
  CONSTRAINT `fk_submission_experiment` FOREIGN KEY (`experiments_experimentId`) REFERENCES `Experiment` (`experimentId`),
  CONSTRAINT `fk_submission_experiment_submission` FOREIGN KEY (`submission_submissionId`) REFERENCES `Submission` (`submissionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Subproject`
--

DROP TABLE IF EXISTS `Subproject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Subproject` (
  `subprojectId` bigint NOT NULL AUTO_INCREMENT,
  `projectId` bigint NOT NULL,
  `alias` varchar(255) NOT NULL,
  `referenceGenomeId` bigint NOT NULL DEFAULT '1',
  `description` varchar(255) DEFAULT NULL,
  `priority` tinyint(1) NOT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`subprojectId`),
  UNIQUE KEY `uk_subproject_project_alias` (`projectId`,`alias`),
  KEY `FK5mudbpqu96ccsmoldngfn7ulx` (`createdBy`),
  KEY `FKhb5p2460x4v7hd29wia24nnbu` (`projectId`),
  KEY `FKl477a3ed1xwaqx5k9hqu8naqi` (`updatedBy`),
  KEY `referenceGenomeId` (`referenceGenomeId`),
  CONSTRAINT `FK5mudbpqu96ccsmoldngfn7ulx` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKhb5p2460x4v7hd29wia24nnbu` FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`),
  CONSTRAINT `FKl477a3ed1xwaqx5k9hqu8naqi` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `Subproject_ibfk_1` FOREIGN KEY (`referenceGenomeId`) REFERENCES `ReferenceGenome` (`referenceGenomeId`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TargetedSequencing`
--

DROP TABLE IF EXISTS `TargetedSequencing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TargetedSequencing` (
  `targetedSequencingId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `archived` bit(1) NOT NULL DEFAULT b'0',
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`targetedSequencingId`),
  UNIQUE KEY `UK_TargetedResequencing_a_kdi2` (`alias`),
  KEY `FK_TargetedResequencing_cb2` (`createdBy`),
  KEY `FK_TargetedResequencing_ub2` (`updatedBy`),
  CONSTRAINT `FK_TargetedResequencing_cb2` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_TargetedResequencing_ub2` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TargetedSequencing_KitDescriptor`
--

DROP TABLE IF EXISTS `TargetedSequencing_KitDescriptor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TargetedSequencing_KitDescriptor` (
  `targetedSequencingId` bigint NOT NULL,
  `kitDescriptorId` bigint NOT NULL,
  PRIMARY KEY (`targetedSequencingId`,`kitDescriptorId`),
  KEY `TK_KitDescriptor_FK` (`kitDescriptorId`),
  CONSTRAINT `TK_KitDescriptor_FK` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`),
  CONSTRAINT `TK_TargetedSequencing_FK` FOREIGN KEY (`targetedSequencingId`) REFERENCES `TargetedSequencing` (`targetedSequencingId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TissueMaterial`
--

DROP TABLE IF EXISTS `TissueMaterial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TissueMaterial` (
  `tissueMaterialId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tissueMaterialId`),
  UNIQUE KEY `UK_6pr0m7xvv7g5ajmmv93mqvdg7` (`alias`),
  KEY `FKtrwn1w8po9spxnkex9rpgsn64` (`createdBy`),
  KEY `FK69r5v1ppgjw6jth6saekcmv96` (`updatedBy`),
  CONSTRAINT `FK69r5v1ppgjw6jth6saekcmv96` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKtrwn1w8po9spxnkex9rpgsn64` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TissueOrigin`
--

DROP TABLE IF EXISTS `TissueOrigin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TissueOrigin` (
  `tissueOriginId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tissueOriginId`),
  UNIQUE KEY `UK_m3j5fpd9m5hpofmdggxxmxtde` (`alias`),
  KEY `FK8gy70defmu4xsbhiubahuwto9` (`createdBy`),
  KEY `FKjdbxm47tiwma7ge045wjgvjdi` (`updatedBy`),
  CONSTRAINT `FK8gy70defmu4xsbhiubahuwto9` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKjdbxm47tiwma7ge045wjgvjdi` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TissuePieceType`
--

DROP TABLE IF EXISTS `TissuePieceType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TissuePieceType` (
  `tissuePieceTypeId` bigint NOT NULL AUTO_INCREMENT,
  `abbreviation` varchar(500) NOT NULL,
  `name` varchar(500) NOT NULL,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  `v2NamingCode` varchar(2) NOT NULL DEFAULT 'TP',
  PRIMARY KEY (`tissuePieceTypeId`),
  UNIQUE KEY `uk_tissuePieceType_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TissueType`
--

DROP TABLE IF EXISTS `TissueType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TissueType` (
  `tissueTypeId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `createdBy` bigint NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sampleTypeName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`tissueTypeId`),
  UNIQUE KEY `UK_5kvipym1ykutjwljtmigu043a` (`alias`),
  KEY `FKsnq8m3yj353mujw9c0iqrsjma` (`createdBy`),
  KEY `FK47m56tfdlpjqwgg79txgdt141` (`updatedBy`),
  CONSTRAINT `FK47m56tfdlpjqwgg79txgdt141` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKsnq8m3yj353mujw9c0iqrsjma` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Transfer`
--

DROP TABLE IF EXISTS `Transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Transfer` (
  `transferId` bigint NOT NULL AUTO_INCREMENT,
  `transferTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `senderLabId` bigint DEFAULT NULL,
  `senderGroupId` bigint DEFAULT NULL,
  `recipient` varchar(255) DEFAULT NULL,
  `recipientGroupId` bigint DEFAULT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModifier` bigint NOT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `transferRequestName` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`transferId`),
  KEY `fk_transfer_senderLab` (`senderLabId`),
  KEY `fk_transfer_senderGroup` (`senderGroupId`),
  KEY `fk_transfer_recipientGroup` (`recipientGroupId`),
  KEY `fk_transfer_creator` (`creator`),
  KEY `fk_transfer_modifier` (`lastModifier`),
  CONSTRAINT `fk_transfer_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_transfer_modifier` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_transfer_recipientGroup` FOREIGN KEY (`recipientGroupId`) REFERENCES `_Group` (`groupId`),
  CONSTRAINT `fk_transfer_senderGroup` FOREIGN KEY (`senderGroupId`) REFERENCES `_Group` (`groupId`),
  CONSTRAINT `fk_transfer_senderLab` FOREIGN KEY (`senderLabId`) REFERENCES `Lab` (`labId`)
) ENGINE=InnoDB AUTO_INCREMENT=11200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TransferChangeLog`
--

DROP TABLE IF EXISTS `TransferChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TransferChangeLog` (
  `transferChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `transferId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transferChangeLogId`),
  KEY `fk_transferChangeLog_user` (`userId`),
  KEY `fk_transferChangeLog_transfer` (`transferId`),
  CONSTRAINT `fk_transferChangeLog_transfer` FOREIGN KEY (`transferId`) REFERENCES `Transfer` (`transferId`) ON DELETE CASCADE,
  CONSTRAINT `fk_transferChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=63061 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TransferNotification`
--

DROP TABLE IF EXISTS `TransferNotification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TransferNotification` (
  `notificationId` bigint NOT NULL AUTO_INCREMENT,
  `transferId` bigint NOT NULL,
  `recipientName` varchar(255) NOT NULL,
  `recipientEmail` varchar(255) NOT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sentTime` timestamp NULL DEFAULT NULL,
  `sendSuccess` tinyint(1) DEFAULT NULL,
  `failureSentTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`notificationId`),
  KEY `fk_transferNotification_transfer` (`transferId`),
  KEY `fk_transferNotification_creator` (`creator`),
  CONSTRAINT `fk_transferNotification_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_transferNotification_transfer` FOREIGN KEY (`transferId`) REFERENCES `Transfer` (`transferId`)
) ENGINE=InnoDB AUTO_INCREMENT=6009 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Transfer_Library`
--

DROP TABLE IF EXISTS `Transfer_Library`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Transfer_Library` (
  `transferId` bigint NOT NULL,
  `libraryId` bigint NOT NULL,
  `received` tinyint(1) DEFAULT NULL,
  `qcPassed` tinyint(1) DEFAULT NULL,
  `qcNote` varchar(255) DEFAULT NULL,
  `distributedVolume` decimal(16,10) DEFAULT NULL,
  `distributedBoxAlias` varchar(255) DEFAULT NULL,
  `distributedBoxPosition` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`transferId`,`libraryId`),
  KEY `fk_transfer_library` (`libraryId`),
  CONSTRAINT `fk_library_transfer` FOREIGN KEY (`transferId`) REFERENCES `Transfer` (`transferId`),
  CONSTRAINT `fk_transfer_library` FOREIGN KEY (`libraryId`) REFERENCES `Library` (`libraryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Transfer_LibraryAliquot`
--

DROP TABLE IF EXISTS `Transfer_LibraryAliquot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Transfer_LibraryAliquot` (
  `transferId` bigint NOT NULL,
  `aliquotId` bigint NOT NULL,
  `received` tinyint(1) DEFAULT NULL,
  `qcPassed` tinyint(1) DEFAULT NULL,
  `qcNote` varchar(255) DEFAULT NULL,
  `distributedVolume` decimal(16,10) DEFAULT NULL,
  `distributedBoxAlias` varchar(255) DEFAULT NULL,
  `distributedBoxPosition` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`transferId`,`aliquotId`),
  KEY `fk_transfer_libraryAliquot` (`aliquotId`),
  CONSTRAINT `fk_libraryAliquot_transfer` FOREIGN KEY (`transferId`) REFERENCES `Transfer` (`transferId`),
  CONSTRAINT `fk_transfer_libraryAliquot` FOREIGN KEY (`aliquotId`) REFERENCES `LibraryAliquot` (`aliquotId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Transfer_Pool`
--

DROP TABLE IF EXISTS `Transfer_Pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Transfer_Pool` (
  `transferId` bigint NOT NULL,
  `poolId` bigint NOT NULL,
  `received` tinyint(1) DEFAULT NULL,
  `qcPassed` tinyint(1) DEFAULT NULL,
  `qcNote` varchar(255) DEFAULT NULL,
  `distributedVolume` decimal(16,10) DEFAULT NULL,
  `distributedBoxAlias` varchar(255) DEFAULT NULL,
  `distributedBoxPosition` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`transferId`,`poolId`),
  KEY `fk_transfer_pool` (`poolId`),
  CONSTRAINT `fk_pool_transfer` FOREIGN KEY (`transferId`) REFERENCES `Transfer` (`transferId`),
  CONSTRAINT `fk_transfer_pool` FOREIGN KEY (`poolId`) REFERENCES `Pool` (`poolId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Transfer_Sample`
--

DROP TABLE IF EXISTS `Transfer_Sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Transfer_Sample` (
  `transferId` bigint NOT NULL,
  `sampleId` bigint NOT NULL,
  `received` tinyint(1) DEFAULT NULL,
  `qcPassed` tinyint(1) DEFAULT NULL,
  `qcNote` varchar(255) DEFAULT NULL,
  `distributedVolume` decimal(16,10) DEFAULT NULL,
  `distributedBoxAlias` varchar(255) DEFAULT NULL,
  `distributedBoxPosition` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`transferId`,`sampleId`),
  KEY `fk_transfer_sample` (`sampleId`),
  CONSTRAINT `fk_sample_transfer` FOREIGN KEY (`transferId`) REFERENCES `Transfer` (`transferId`),
  CONSTRAINT `fk_transfer_sample` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User` (
  `userId` bigint NOT NULL AUTO_INCREMENT,
  `active` tinyint(1) NOT NULL DEFAULT '0',
  `admin` tinyint(1) NOT NULL DEFAULT '0',
  `fullName` varchar(255) NOT NULL,
  `internal` tinyint(1) NOT NULL DEFAULT '0',
  `loginName` varchar(255) NOT NULL,
  `roles` blob,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `uk_user_loginname` (`loginName`),
  KEY `User_loginName` (`loginName`)
) ENGINE=InnoDB AUTO_INCREMENT=212 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `User_FavouriteWorkflows`
--

DROP TABLE IF EXISTS `User_FavouriteWorkflows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User_FavouriteWorkflows` (
  `userId` bigint DEFAULT NULL,
  `favouriteWorkflow` varchar(20) DEFAULT NULL,
  KEY `fk_user_favouriteworkflow_user` (`userId`),
  CONSTRAINT `fk_user_favouriteworkflow_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `User_Group`
--

DROP TABLE IF EXISTS `User_Group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User_Group` (
  `users_userId` bigint NOT NULL,
  `groups_groupId` bigint NOT NULL,
  UNIQUE KEY `uk_user_group` (`users_userId`,`groups_groupId`),
  KEY `FKE7B7ED0B94349B7F` (`groups_groupId`),
  KEY `FKE7B7ED0B749D8197` (`users_userId`),
  CONSTRAINT `fk_user_group_group` FOREIGN KEY (`groups_groupId`) REFERENCES `_Group` (`groupId`),
  CONSTRAINT `fk_user_group_user` FOREIGN KEY (`users_userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorkflowProgress`
--

DROP TABLE IF EXISTS `WorkflowProgress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkflowProgress` (
  `workflowProgressId` bigint NOT NULL AUTO_INCREMENT,
  `workflowName` varchar(255) NOT NULL,
  `userId` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`workflowProgressId`),
  KEY `userId` (`userId`),
  CONSTRAINT `WorkflowProgress_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorkflowProgressStep`
--

DROP TABLE IF EXISTS `WorkflowProgressStep`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorkflowProgressStep` (
  `workflowProgressId` bigint NOT NULL,
  `stepNumber` bigint NOT NULL,
  PRIMARY KEY (`workflowProgressId`,`stepNumber`),
  CONSTRAINT `WorkflowProgressStep_ibfk_1` FOREIGN KEY (`workflowProgressId`) REFERENCES `WorkflowProgress` (`workflowProgressId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Workset`
--

DROP TABLE IF EXISTS `Workset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Workset` (
  `worksetId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator` bigint NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModifier` bigint NOT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `categoryId` bigint DEFAULT NULL,
  `stageId` bigint DEFAULT NULL,
  PRIMARY KEY (`worksetId`),
  UNIQUE KEY `uk_workset_alias` (`alias`),
  KEY `fk_workset_creator` (`creator`),
  KEY `fk_workset_modifier` (`lastModifier`),
  KEY `fk_workset_category` (`categoryId`),
  KEY `fk_workset_stage` (`stageId`),
  CONSTRAINT `fk_workset_category` FOREIGN KEY (`categoryId`) REFERENCES `WorksetCategory` (`categoryId`),
  CONSTRAINT `fk_workset_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_workset_modifier` FOREIGN KEY (`lastModifier`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_workset_stage` FOREIGN KEY (`stageId`) REFERENCES `WorksetStage` (`stageId`)
) ENGINE=InnoDB AUTO_INCREMENT=4281 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorksetCategory`
--

DROP TABLE IF EXISTS `WorksetCategory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorksetCategory` (
  `categoryId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`categoryId`),
  UNIQUE KEY `uk_worksetCategory_alias` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorksetChangeLog`
--

DROP TABLE IF EXISTS `WorksetChangeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorksetChangeLog` (
  `worksetChangeLogId` bigint NOT NULL AUTO_INCREMENT,
  `worksetId` bigint NOT NULL,
  `columnsChanged` varchar(500) NOT NULL,
  `userId` bigint NOT NULL,
  `message` longtext NOT NULL,
  `changeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`worksetChangeLogId`),
  KEY `fk_worksetChangeLog_user` (`userId`),
  KEY `fk_worksetChangeLog_workset` (`worksetId`),
  CONSTRAINT `fk_worksetChangeLog_user` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`),
  CONSTRAINT `fk_worksetChangeLog_workset` FOREIGN KEY (`worksetId`) REFERENCES `Workset` (`worksetId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30817 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WorksetStage`
--

DROP TABLE IF EXISTS `WorksetStage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `WorksetStage` (
  `stageId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`stageId`),
  UNIQUE KEY `uk_worksetStage_alias` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Workset_Library`
--

DROP TABLE IF EXISTS `Workset_Library`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Workset_Library` (
  `worksetId` bigint NOT NULL,
  `libraryId` bigint NOT NULL,
  `addedTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`worksetId`,`libraryId`),
  KEY `fk_workset_library` (`libraryId`),
  CONSTRAINT `fk_library_workset` FOREIGN KEY (`worksetId`) REFERENCES `Workset` (`worksetId`),
  CONSTRAINT `fk_workset_library` FOREIGN KEY (`libraryId`) REFERENCES `Library` (`libraryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Workset_LibraryAliquot`
--

DROP TABLE IF EXISTS `Workset_LibraryAliquot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Workset_LibraryAliquot` (
  `worksetId` bigint NOT NULL,
  `aliquotId` bigint NOT NULL,
  `addedTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`worksetId`,`aliquotId`),
  KEY `fk_workset_libraryAliquot` (`aliquotId`),
  CONSTRAINT `fk_libraryAliquot_workset` FOREIGN KEY (`worksetId`) REFERENCES `Workset` (`worksetId`),
  CONSTRAINT `fk_workset_libraryAliquot` FOREIGN KEY (`aliquotId`) REFERENCES `LibraryAliquot` (`aliquotId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Workset_Note`
--

DROP TABLE IF EXISTS `Workset_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Workset_Note` (
  `worksetId` bigint NOT NULL,
  `noteId` bigint NOT NULL,
  PRIMARY KEY (`worksetId`,`noteId`),
  KEY `fk_worksetNote_note` (`noteId`),
  CONSTRAINT `fk_worksetNote_note` FOREIGN KEY (`noteId`) REFERENCES `Note` (`noteId`),
  CONSTRAINT `fk_wotksetNote_workset` FOREIGN KEY (`worksetId`) REFERENCES `Workset` (`worksetId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Workset_Sample`
--

DROP TABLE IF EXISTS `Workset_Sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Workset_Sample` (
  `worksetId` bigint NOT NULL,
  `sampleId` bigint NOT NULL,
  `addedTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`worksetId`,`sampleId`),
  KEY `fk_workset_sample` (`sampleId`),
  CONSTRAINT `fk_sample_workset` FOREIGN KEY (`worksetId`) REFERENCES `Workset` (`worksetId`),
  CONSTRAINT `fk_workset_sample` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Workstation`
--

DROP TABLE IF EXISTS `Workstation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Workstation` (
  `workstationId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`workstationId`),
  UNIQUE KEY `uk_workstation_alias` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `_Group`
--

DROP TABLE IF EXISTS `_Group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `_Group` (
  `groupId` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `builtIn` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`groupId`),
  UNIQUE KEY `uk_group_name` (`name`),
  KEY `Group_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `_Partition`
--

DROP TABLE IF EXISTS `_Partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `_Partition` (
  `partitionId` bigint NOT NULL AUTO_INCREMENT,
  `partitionNumber` tinyint NOT NULL,
  `pool_poolId` bigint DEFAULT NULL,
  `loadingConcentration` decimal(14,10) DEFAULT NULL,
  `loadingConcentrationUnits` varchar(30) DEFAULT NULL,
  `containerId` bigint NOT NULL,
  PRIMARY KEY (`partitionId`),
  KEY `pool_poolId` (`pool_poolId`),
  KEY `fk_partition_container` (`containerId`),
  CONSTRAINT `_Partition_ibfk_1` FOREIGN KEY (`pool_poolId`) REFERENCES `Pool` (`poolId`),
  CONSTRAINT `fk_partition_container` FOREIGN KEY (`containerId`) REFERENCES `SequencerPartitionContainer` (`containerId`)
) ENGINE=InnoDB AUTO_INCREMENT=27166 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`version`),
  KEY `schema_version_ir_idx` (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `persistent_logins`
--

DROP TABLE IF EXISTS `persistent_logins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `ActivePlatformTypes`
--

/*!50001 DROP VIEW IF EXISTS `ActivePlatformTypes`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `ActivePlatformTypes` AS select distinct `im`.`platform` AS `platform` from (`Instrument` `inst` join `InstrumentModel` `im` on((`im`.`instrumentModelId` = `inst`.`instrumentModelId`))) where ((`im`.`instrumentType` = 'SEQUENCER') and (`inst`.`dateDecommissioned` is null)) union select distinct `Pool`.`platformType` AS `platformType` from `Pool` union select distinct `Library`.`platformType` AS `platformType` from `Library` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `AttachmentUsage`
--

/*!50001 DROP VIEW IF EXISTS `AttachmentUsage`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `AttachmentUsage` AS select `sub`.`attachmentId` AS `attachmentId`,((((`sub`.`libraries` + `sub`.`pools`) + `sub`.`projects`) + `sub`.`samples`) + `sub`.`serviceRecords`) AS `usage` from (select `a`.`attachmentId` AS `attachmentId`,count(`la`.`libraryId`) AS `libraries`,count(`pa`.`poolId`) AS `pools`,count(`pra`.`projectId`) AS `projects`,count(`sa`.`sampleId`) AS `samples`,count(`sra`.`recordId`) AS `serviceRecords` from (((((`Attachment` `a` left join `Library_Attachment` `la` on((`la`.`attachmentId` = `a`.`attachmentId`))) left join `Pool_Attachment` `pa` on((`pa`.`attachmentId` = `a`.`attachmentId`))) left join `Project_Attachment` `pra` on((`pra`.`attachmentId` = `a`.`attachmentId`))) left join `Sample_Attachment` `sa` on((`sa`.`attachmentId` = `a`.`attachmentId`))) left join `ServiceRecord_Attachment` `sra` on((`sra`.`attachmentId` = `a`.`attachmentId`))) group by `a`.`attachmentId`) `sub` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `BarcodableView`
--

/*!50001 DROP VIEW IF EXISTS `BarcodableView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `BarcodableView` AS select `LibraryAliquot`.`aliquotId` AS `targetId`,`LibraryAliquot`.`identificationBarcode` AS `identificationBarcode`,`LibraryAliquot`.`name` AS `name`,`LibraryAliquot`.`alias` AS `alias`,'LIBRARY_ALIQUOT' AS `targetType` from `LibraryAliquot` union all select `Pool`.`poolId` AS `poolId`,`Pool`.`identificationBarcode` AS `identificationBarcode`,`Pool`.`name` AS `name`,`Pool`.`alias` AS `alias`,'POOL' AS `targetType` from `Pool` union all select `Sample`.`sampleId` AS `sampleId`,`Sample`.`identificationBarcode` AS `identificationBarcode`,`Sample`.`name` AS `name`,`Sample`.`alias` AS `alias`,'SAMPLE' AS `targetType` from `Sample` union all select `Library`.`libraryId` AS `libraryId`,`Library`.`identificationBarcode` AS `identificationBarcode`,`Library`.`name` AS `name`,`Library`.`alias` AS `alias`,'LIBRARY' AS `targetType` from `Library` union all select `Box`.`boxId` AS `boxId`,`Box`.`identificationBarcode` AS `identificationBarcode`,`Box`.`name` AS `name`,`Box`.`alias` AS `alias`,'BOX' AS `targetType` from `Box` union all select `SequencerPartitionContainer`.`containerId` AS `containerId`,`SequencerPartitionContainer`.`identificationBarcode` AS `identificationBarcode`,NULL AS `name`,NULL AS `alias`,'CONTAINER' AS `targetType` from `SequencerPartitionContainer` union all select `SequencingContainerModel`.`sequencingContainerModelId` AS `sequencingContainerModelId`,`SequencingContainerModel`.`identificationBarcode` AS `identificationBarcode`,NULL AS `name`,`SequencingContainerModel`.`alias` AS `alias`,'CONTAINER_MODEL' AS `targetType` from `SequencingContainerModel` union all select `Workstation`.`workstationId` AS `workstationId`,`Workstation`.`identificationBarcode` AS `identificationBarcode`,NULL AS `name`,`Workstation`.`alias` AS `alias`,'WORKSTATION' AS `targetType` from `Workstation` union all select `Instrument`.`instrumentId` AS `instrumentId`,`Instrument`.`identificationBarcode` AS `identificationBarcode`,`Instrument`.`name` AS `name`,NULL AS `alias`,'INSTRUMENT' AS `targetType` from `Instrument` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `InstrumentStatusPositionRunPoolView`
--

/*!50001 DROP VIEW IF EXISTS `InstrumentStatusPositionRunPoolView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `InstrumentStatusPositionRunPoolView` AS select `rspc`.`Run_runId` AS `runId`,coalesce(`rspc`.`positionId`,-(1)) AS `positionId`,`part`.`partitionId` AS `partitionId`,`pool`.`poolId` AS `poolId`,`pool`.`name` AS `name`,`pool`.`alias` AS `alias` from ((`Run_SequencerPartitionContainer` `rspc` join `_Partition` `part` on((`part`.`containerId` = `rspc`.`containers_containerId`))) join `Pool` `pool` on((`pool`.`poolId` = `part`.`pool_poolId`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `InstrumentStatusPositionRunView`
--

/*!50001 DROP VIEW IF EXISTS `InstrumentStatusPositionRunView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `InstrumentStatusPositionRunView` AS select `r`.`runId` AS `runId`,`r`.`name` AS `name`,`r`.`alias` AS `alias`,`r`.`instrumentId` AS `instrumentId`,`r`.`health` AS `health`,`r`.`startDate` AS `startDate`,`r`.`completionDate` AS `completionDate`,`r`.`lastModified` AS `lastModified`,coalesce(`rspc`.`positionId`,-(1)) AS `positionId` from (`Run` `r` left join `Run_SequencerPartitionContainer` `rspc` on((`rspc`.`Run_runId` = `r`.`runId`))) order by coalesce(`r`.`completionDate`,`r`.`startDate`) desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `InstrumentStatusPositionView`
--

/*!50001 DROP VIEW IF EXISTS `InstrumentStatusPositionView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `InstrumentStatusPositionView` AS select `inst`.`instrumentId` AS `instrumentId`,coalesce(`ipos`.`positionId`,-(1)) AS `positionId`,`ipos`.`alias` AS `alias`,`sr`.`outOfServiceTime` AS `outOfServiceTime` from ((`Instrument` `inst` left join `InstrumentPosition` `ipos` on((`ipos`.`instrumentModelId` = `inst`.`instrumentModelId`))) left join (select `instrec`.`instrumentId` AS `instrumentId`,`rec`.`positionId` AS `positionId`,min(`rec`.`startTime`) AS `outOfServiceTime` from (`ServiceRecord` `rec` join `Instrument_ServiceRecord` `instrec` on((`instrec`.`recordId` = `rec`.`recordId`))) where ((`rec`.`outOfService` = true) and (`rec`.`startTime` is not null) and (`rec`.`endTime` is null)) group by `instrec`.`instrumentId`,`rec`.`positionId`) `sr` on(((`sr`.`instrumentId` = `inst`.`instrumentId`) and ((`sr`.`positionId` is null) or (`sr`.`positionId` = `ipos`.`positionId`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `InstrumentStatusView`
--

/*!50001 DROP VIEW IF EXISTS `InstrumentStatusView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `InstrumentStatusView` AS select `inst`.`instrumentId` AS `instrumentId`,`inst`.`name` AS `name` from (`Instrument` `inst` join `InstrumentModel` `im` on((`im`.`instrumentModelId` = `inst`.`instrumentModelId`))) where ((`inst`.`dateDecommissioned` is null) and (`im`.`instrumentType` = 'SEQUENCER')) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `LibraryAliquotBoxPosition`
--

/*!50001 DROP VIEW IF EXISTS `LibraryAliquotBoxPosition`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `LibraryAliquotBoxPosition` AS select `BoxPosition`.`targetId` AS `aliquotId`,`BoxPosition`.`boxId` AS `boxId`,`BoxPosition`.`position` AS `position` from `BoxPosition` where (`BoxPosition`.`targetType` = 'LIBRARY_ALIQUOT') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `LibraryAliquotDistributionView`
--

/*!50001 DROP VIEW IF EXISTS `LibraryAliquotDistributionView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `LibraryAliquotDistributionView` AS select `la`.`aliquotId` AS `aliquotId`,if((sum(if((`t`.`recipient` is not null),1,0)) > 0),true,false) AS `distributed` from ((`LibraryAliquot` `la` left join `Transfer_LibraryAliquot` `tla` on((`tla`.`aliquotId` = `la`.`aliquotId`))) left join `Transfer` `t` on((`t`.`transferId` = `tla`.`transferId`))) group by `la`.`aliquotId` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `LibraryBoxPosition`
--

/*!50001 DROP VIEW IF EXISTS `LibraryBoxPosition`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `LibraryBoxPosition` AS select `BoxPosition`.`targetId` AS `libraryId`,`BoxPosition`.`boxId` AS `boxId`,`BoxPosition`.`position` AS `position` from `BoxPosition` where (`BoxPosition`.`targetType` = 'LIBRARY') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `LibraryDistributionView`
--

/*!50001 DROP VIEW IF EXISTS `LibraryDistributionView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `LibraryDistributionView` AS select `l`.`libraryId` AS `libraryId`,if((sum(if((`t`.`recipient` is not null),1,0)) > 0),true,false) AS `distributed` from ((`Library` `l` left join `Transfer_Library` `tl` on((`tl`.`libraryId` = `l`.`libraryId`))) left join `Transfer` `t` on((`t`.`transferId` = `tl`.`transferId`))) group by `l`.`libraryId` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `LibraryReceiptView`
--

/*!50001 DROP VIEW IF EXISTS `LibraryReceiptView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `LibraryReceiptView` AS select `xferl`.`libraryId` AS `libraryId`,`xfer`.`transferId` AS `transferId`,`xfer`.`transferTime` AS `transferTime` from (`Transfer_Library` `xferl` join `Transfer` `xfer` on((`xfer`.`transferId` = `xferl`.`transferId`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `ListPoolView`
--

/*!50001 DROP VIEW IF EXISTS `ListPoolView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `ListPoolView` AS select `p`.`poolId` AS `poolId`,`p`.`name` AS `name`,`p`.`alias` AS `alias`,`p`.`identificationBarcode` AS `identificationBarcode`,`p`.`description` AS `description`,`p`.`platformType` AS `platformType`,`p`.`creator` AS `creator`,`p`.`created` AS `created`,`p`.`creationDate` AS `creationDate`,`p`.`lastModifier` AS `lastModifier`,`p`.`lastModified` AS `lastModified`,`p`.`concentration` AS `concentration`,`p`.`concentrationUnits` AS `concentrationUnits`,`p`.`dnaSize` AS `dnaSize`,`p`.`discarded` AS `discarded`,`dist`.`distributed` AS `distributed`,`b`.`boxId` AS `boxId`,`b`.`name` AS `boxName`,`b`.`alias` AS `boxAlias`,`b`.`locationBarcode` AS `boxLocationBarcode`,`bp`.`position` AS `boxPosition` from (((`Pool` `p` left join `BoxPosition` `bp` on(((`bp`.`targetType` = 'POOL') and (`bp`.`targetId` = `p`.`poolId`)))) left join `Box` `b` on((`b`.`boxId` = `bp`.`boxId`))) join `PoolDistributionView` `dist` on((`dist`.`poolId` = `p`.`poolId`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `ListPoolView_Element`
--

/*!50001 DROP VIEW IF EXISTS `ListPoolView_Element`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `ListPoolView_Element` AS select `la`.`aliquotId` AS `aliquotId`,`la`.`name` AS `name`,`la`.`alias` AS `alias`,`la`.`dnaSize` AS `dnaSize`,`lib`.`libraryId` AS `libraryId`,`lib`.`lowQuality` AS `lowQuality`,`lib`.`index1Id` AS `index1Id`,`lib`.`index2Id` AS `index2Id`,`sam`.`project_projectId` AS `projectId`,`sp`.`alias` AS `subprojectAlias`,`sp`.`priority` AS `subprojectPriority`,`ident`.`consentLevel` AS `consentLevel` from (((((`LibraryAliquot` `la` join `Library` `lib` on((`lib`.`libraryId` = `la`.`libraryId`))) join `Sample` `sam` on((`sam`.`sampleId` = `lib`.`sample_sampleId`))) left join `Subproject` `sp` on((`sp`.`subprojectId` = `sam`.`subprojectId`))) left join `SampleHierarchy` `sh` on((`sh`.`sampleId` = `sam`.`sampleId`))) left join `Sample` `ident` on((`ident`.`sampleId` = `sh`.`identityId`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `ListWorksetView`
--

/*!50001 DROP VIEW IF EXISTS `ListWorksetView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `ListWorksetView` AS select `w`.`worksetId` AS `worksetId`,`w`.`alias` AS `alias`,((coalesce(`sam`.`count`,0) + coalesce(`lib`.`count`,0)) + coalesce(`ali`.`count`,0)) AS `itemCount`,`w`.`description` AS `description`,`cat`.`alias` AS `category`,`stage`.`alias` AS `stage`,`w`.`creator` AS `creator`,`w`.`created` AS `created`,`w`.`lastModifier` AS `lastModifier`,`w`.`lastModified` AS `lastModified` from (((((`Workset` `w` left join `WorksetCategory` `cat` on((`cat`.`categoryId` = `w`.`categoryId`))) left join `WorksetStage` `stage` on((`stage`.`stageId` = `w`.`stageId`))) left join (select `Workset_Sample`.`worksetId` AS `worksetId`,count(`Workset_Sample`.`sampleId`) AS `count` from `Workset_Sample` group by `Workset_Sample`.`worksetId`) `sam` on((`sam`.`worksetId` = `w`.`worksetId`))) left join (select `Workset_Library`.`worksetId` AS `worksetId`,count(`Workset_Library`.`libraryId`) AS `count` from `Workset_Library` group by `Workset_Library`.`worksetId`) `lib` on((`lib`.`worksetId` = `w`.`worksetId`))) left join (select `Workset_LibraryAliquot`.`worksetId` AS `worksetId`,count(`Workset_LibraryAliquot`.`aliquotId`) AS `count` from `Workset_LibraryAliquot` group by `Workset_LibraryAliquot`.`worksetId`) `ali` on((`ali`.`worksetId` = `w`.`worksetId`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `PendingTransferGroupView`
--

/*!50001 DROP VIEW IF EXISTS `PendingTransferGroupView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `PendingTransferGroupView` AS select `t`.`recipientGroupId` AS `groupId`,count(`t`.`transferId`) AS `transfers` from `Transfer` `t` where ((`t`.`recipientGroupId` is not null) and (exists(select 1 from `Transfer_Sample` `ts` where ((`ts`.`transferId` = `t`.`transferId`) and ((`ts`.`received` is null) or (`ts`.`qcPassed` is null)))) or exists(select 1 from `Transfer_Library` `tl` where ((`tl`.`transferId` = `t`.`transferId`) and ((`tl`.`received` is null) or (`tl`.`qcPassed` is null)))) or exists(select 1 from `Transfer_LibraryAliquot` `tla` where ((`tla`.`transferId` = `t`.`transferId`) and ((`tla`.`received` is null) or (`tla`.`qcPassed` is null)))) or exists(select 1 from `Transfer_Pool` `tp` where ((`tp`.`transferId` = `t`.`transferId`) and ((`tp`.`received` is null) or (`tp`.`qcPassed` is null)))))) group by `t`.`recipientGroupId` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `PoolBoxPosition`
--

/*!50001 DROP VIEW IF EXISTS `PoolBoxPosition`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `PoolBoxPosition` AS select `BoxPosition`.`targetId` AS `poolId`,`BoxPosition`.`boxId` AS `boxId`,`BoxPosition`.`position` AS `position` from `BoxPosition` where (`BoxPosition`.`targetType` = 'POOL') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `PoolDistributionView`
--

/*!50001 DROP VIEW IF EXISTS `PoolDistributionView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `PoolDistributionView` AS select `p`.`poolId` AS `poolId`,if((sum(if((`t`.`recipient` is not null),1,0)) > 0),true,false) AS `distributed` from ((`Pool` `p` left join `Transfer_Pool` `tp` on((`tp`.`poolId` = `p`.`poolId`))) left join `Transfer` `t` on((`t`.`transferId` = `tp`.`transferId`))) group by `p`.`poolId` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `SampleBoxPosition`
--

/*!50001 DROP VIEW IF EXISTS `SampleBoxPosition`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `SampleBoxPosition` AS select `BoxPosition`.`targetId` AS `sampleId`,`BoxPosition`.`boxId` AS `boxId`,`BoxPosition`.`position` AS `position` from `BoxPosition` where (`BoxPosition`.`targetType` = 'SAMPLE') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `SampleDistributionView`
--

/*!50001 DROP VIEW IF EXISTS `SampleDistributionView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `SampleDistributionView` AS select `s`.`sampleId` AS `sampleId`,if((sum(if((`t`.`recipient` is not null),1,0)) > 0),true,false) AS `distributed` from ((`Sample` `s` left join `Transfer_Sample` `ts` on((`ts`.`sampleId` = `s`.`sampleId`))) left join `Transfer` `t` on((`t`.`transferId` = `ts`.`transferId`))) group by `s`.`sampleId` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `SampleReceiptView`
--

/*!50001 DROP VIEW IF EXISTS `SampleReceiptView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `SampleReceiptView` AS select `xfers`.`sampleId` AS `sampleId`,`xfer`.`transferId` AS `transferId`,`xfer`.`transferTime` AS `transferTime` from (`Transfer_Sample` `xfers` join `Transfer` `xfer` on((`xfer`.`transferId` = `xfers`.`transferId`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `SequencingOrderFulfillmentView`
--

/*!50001 DROP VIEW IF EXISTS `SequencingOrderFulfillmentView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `SequencingOrderFulfillmentView` AS select concat(`part`.`pool_poolId`,'_',coalesce(`spc`.`sequencingContainerModelId`,0),'_',coalesce(`run`.`sequencingParameters_parametersId`,0)) AS `orderSummaryId`,count(`part`.`partitionId`) AS `fulfilled` from (((((`_Partition` `part` join `SequencerPartitionContainer` `spc` on((`spc`.`containerId` = `part`.`containerId`))) left join `Run_SequencerPartitionContainer` `rspc` on((`rspc`.`containers_containerId` = `spc`.`containerId`))) left join `Run` `run` on((`run`.`runId` = `rspc`.`Run_runId`))) left join `Run_Partition` `rpqc` on(((`rpqc`.`runId` = `run`.`runId`) and (`rpqc`.`partitionId` = `part`.`partitionId`)))) left join `PartitionQCType` `qct` on((`qct`.`partitionQcTypeId` = `rpqc`.`partitionQcTypeId`))) where ((`part`.`pool_poolId` is not null) and ((`qct`.`orderFulfilled` is null) or (`qct`.`orderFulfilled` = true)) and ((`run`.`health` is null) or ((`run`.`health` <> 'Failed') and (`run`.`health` <> 'Unknown')))) group by `part`.`pool_poolId`,`spc`.`sequencingContainerModelId`,`run`.`sequencingParameters_parametersId` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `SequencingOrderLoadedPartitionView`
--

/*!50001 DROP VIEW IF EXISTS `SequencingOrderLoadedPartitionView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `SequencingOrderLoadedPartitionView` AS select `part`.`pool_poolId` AS `poolId`,`spc`.`sequencingContainerModelId` AS `sequencingContainerModelId`,count(0) AS `loaded` from ((`_Partition` `part` join `SequencerPartitionContainer` `spc` on((`spc`.`containerId` = `part`.`containerId`))) left join `Run_SequencerPartitionContainer` `rspc` on((`rspc`.`containers_containerId` = `spc`.`containerId`))) where ((`part`.`pool_poolId` is not null) and (`rspc`.`Run_runId` is null)) group by `part`.`pool_poolId`,`spc`.`sequencingContainerModelId` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `SequencingOrderNoContainerModelFulfillmentView`
--

/*!50001 DROP VIEW IF EXISTS `SequencingOrderNoContainerModelFulfillmentView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `SequencingOrderNoContainerModelFulfillmentView` AS select concat(`part`.`pool_poolId`,'_0_',coalesce(`run`.`sequencingParameters_parametersId`,0)) AS `orderSummaryId`,count(`part`.`partitionId`) AS `fulfilled` from (((((`_Partition` `part` join `SequencerPartitionContainer` `spc` on((`spc`.`containerId` = `part`.`containerId`))) left join `Run_SequencerPartitionContainer` `rspc` on((`rspc`.`containers_containerId` = `spc`.`containerId`))) left join `Run` `run` on((`run`.`runId` = `rspc`.`Run_runId`))) left join `Run_Partition` `rpqc` on(((`rpqc`.`runId` = `run`.`runId`) and (`rpqc`.`partitionId` = `part`.`partitionId`)))) left join `PartitionQCType` `qct` on((`qct`.`partitionQcTypeId` = `rpqc`.`partitionQcTypeId`))) where ((`part`.`pool_poolId` is not null) and ((`qct`.`orderFulfilled` is null) or (`qct`.`orderFulfilled` = true)) and ((`run`.`health` is null) or ((`run`.`health` <> 'Failed') and (`run`.`health` <> 'Unknown')))) group by `part`.`pool_poolId`,`run`.`sequencingParameters_parametersId` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `SequencingOrderPartitionView`
--

/*!50001 DROP VIEW IF EXISTS `SequencingOrderPartitionView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `SequencingOrderPartitionView` AS select `part`.`partitionId` AS `partitionId`,concat(`part`.`pool_poolId`,'_',`spc`.`sequencingContainerModelId`,'_',`run`.`sequencingParameters_parametersId`) AS `orderSummaryId`,concat(`part`.`pool_poolId`,'_0_',`run`.`sequencingParameters_parametersId`) AS `noContainerModelId`,`run`.`health` AS `health` from (((((`_Partition` `part` join `SequencerPartitionContainer` `spc` on((`spc`.`containerId` = `part`.`containerId`))) join `Run_SequencerPartitionContainer` `rspc` on((`rspc`.`containers_containerId` = `spc`.`containerId`))) join `Run` `run` on((`run`.`runId` = `rspc`.`Run_runId`))) left join `Run_Partition` `rpqc` on(((`rpqc`.`runId` = `run`.`runId`) and (`rpqc`.`partitionId` = `part`.`partitionId`)))) left join `PartitionQCType` `qct` on((`qct`.`partitionQcTypeId` = `rpqc`.`partitionQcTypeId`))) where ((`part`.`pool_poolId` is not null) and ((`qct`.`orderFulfilled` is null) or (`qct`.`orderFulfilled` = true))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `SequencingOrderSummaryView`
--

/*!50001 DROP VIEW IF EXISTS `SequencingOrderSummaryView`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`pchopralims`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `SequencingOrderSummaryView` AS select concat(`o`.`poolId`,'_',coalesce(`o`.`sequencingContainerModelId`,0),'_',`o`.`parametersId`) AS `orderSummaryId`,`o`.`poolId` AS `poolId`,`o`.`sequencingContainerModelId` AS `sequencingContainerModelId`,`o`.`parametersId` AS `parametersId`,sum(`o`.`partitions`) AS `requested`,coalesce(`loaded`.`loaded`,0) AS `loaded`,group_concat(`o`.`description` separator '; ') AS `description`,group_concat(distinct `RunPurpose`.`alias` separator '; ') AS `purpose`,max(`o`.`lastUpdated`) AS `lastUpdated` from ((`SequencingOrder` `o` join `RunPurpose` on((`RunPurpose`.`purposeId` = `o`.`purposeId`))) left join `SequencingOrderLoadedPartitionView` `loaded` on(((`loaded`.`poolId` = `o`.`poolId`) and ((`loaded`.`sequencingContainerModelId` = `o`.`sequencingContainerModelId`) or ((`loaded`.`sequencingContainerModelId` is null) and (`o`.`sequencingContainerModelId` is null)))))) group by `o`.`poolId`,`o`.`sequencingContainerModelId`,`o`.`parametersId`,`loaded`.`loaded` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-15 16:33:54
