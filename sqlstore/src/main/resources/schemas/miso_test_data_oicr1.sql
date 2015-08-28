-- MySQL dump 10.13  Distrib 5.6.19, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: lims
-- ------------------------------------------------------
-- Server version	5.6.19-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
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
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Alert` (
  `alertId` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `text` text NOT NULL,
  `userId` bigint(20) NOT NULL,
  `date` date NOT NULL,
  `isRead` bit(1) NOT NULL DEFAULT b'0',
  `level` varchar(8) NOT NULL DEFAULT 'INFO',
  PRIMARY KEY (`alertId`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Alert`
--

LOCK TABLES `Alert` WRITE;
/*!40000 ALTER TABLE `Alert` DISABLE KEYS */;
INSERT INTO `Alert` VALUES (1,'Pool Pool 1(IPO1)','The following Pool is ready to run: IPO1 (Pool IPO1 ready to run). Please view Pool 1 in MISO for more information',1,'2015-08-27','','INFO'),(2,'Pool Pool 2(IPO2)','The following Pool is ready to run: IPO2 (Pool IPO2 ready to run). Please view Pool 2 in MISO for more information',1,'2015-08-27','','INFO'),(3,'Pool Pool 3(IPO3)','The following Pool is ready to run: IPO3 (Pool IPO3 ready to run). Please view Pool 3 in MISO for more information',1,'2015-08-27','','INFO'),(4,'Pool Pool 4(IPO4)','The following Pool is ready to run: IPO4 (Pool IPO4 ready to run). Please view Pool 4 in MISO for more information',1,'2015-08-27','','INFO'),(5,'Pool Pool 5(IPO5)','The following Pool is ready to run: IPO5 (Pool IPO5 ready to run). Please view Pool 5 in MISO for more information',1,'2015-08-27','','INFO'),(6,'Pool Pool 6(IPO6)','The following Pool is ready to run: IPO6 (Pool IPO6 ready to run). Please view Pool 6 in MISO for more information',1,'2015-08-27','','INFO'),(7,'Pool Pool 7(IPO7)','The following Pool is ready to run: IPO7 (Pool IPO7 ready to run). Please view Pool 7 in MISO for more information',1,'2015-08-27','','INFO'),(8,'Pool Pool 8(IPO8)','The following Pool is ready to run: IPO8 (Pool IPO8 ready to run). Please view Pool 8 in MISO for more information',1,'2015-08-27','','INFO'),(9,'Pool Pool 9(IPO9)','The following Pool is ready to run: IPO9 (Pool IPO9 ready to run). Please view Pool 9 in MISO for more information',1,'2015-08-27','','INFO'),(10,'Pool Pool 10(IPO10)','The following Pool is ready to run: IPO10 (Pool IPO10 ready to run). Please view Pool 10 in MISO for more information',1,'2015-08-27','','INFO');
/*!40000 ALTER TABLE `Alert` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Chamber`
--

DROP TABLE IF EXISTS `Chamber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Chamber` (
  `chamberId` bigint(20) NOT NULL AUTO_INCREMENT,
  `chamberNumber` tinyint(4) NOT NULL,
  `pool_poolId` bigint(20) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`chamberId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Chamber`
--

LOCK TABLES `Chamber` WRITE;
/*!40000 ALTER TABLE `Chamber` DISABLE KEYS */;
/*!40000 ALTER TABLE `Chamber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DATABASECHANGELOG`
--

DROP TABLE IF EXISTS `DATABASECHANGELOG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DATABASECHANGELOG`
--

LOCK TABLES `DATABASECHANGELOG` WRITE;
/*!40000 ALTER TABLE `DATABASECHANGELOG` DISABLE KEYS */;
/*!40000 ALTER TABLE `DATABASECHANGELOG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DATABASECHANGELOGLOCK`
--

DROP TABLE IF EXISTS `DATABASECHANGELOGLOCK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DATABASECHANGELOGLOCK` (
  `ID` int(11) NOT NULL,
  `LOCKED` tinyint(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DATABASECHANGELOGLOCK`
--

LOCK TABLES `DATABASECHANGELOGLOCK` WRITE;
/*!40000 ALTER TABLE `DATABASECHANGELOGLOCK` DISABLE KEYS */;
/*!40000 ALTER TABLE `DATABASECHANGELOGLOCK` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `EntityGroup`
--

DROP TABLE IF EXISTS `EntityGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `EntityGroup` (
  `entityGroupId` bigint(20) NOT NULL AUTO_INCREMENT,
  `parentId` bigint(20) NOT NULL,
  `parentType` varchar(255) NOT NULL,
  PRIMARY KEY (`entityGroupId`,`parentId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `EntityGroup`
--

LOCK TABLES `EntityGroup` WRITE;
/*!40000 ALTER TABLE `EntityGroup` DISABLE KEYS */;
/*!40000 ALTER TABLE `EntityGroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `EntityGroup_Elements`
--

DROP TABLE IF EXISTS `EntityGroup_Elements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `EntityGroup_Elements` (
  `entityGroup_entityGroupId` bigint(20) NOT NULL,
  `entityId` bigint(20) NOT NULL,
  `entityType` varchar(255) NOT NULL,
  PRIMARY KEY (`entityGroup_entityGroupId`,`entityId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `EntityGroup_Elements`
--

LOCK TABLES `EntityGroup_Elements` WRITE;
/*!40000 ALTER TABLE `EntityGroup_Elements` DISABLE KEYS */;
/*!40000 ALTER TABLE `EntityGroup_Elements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Experiment`
--

DROP TABLE IF EXISTS `Experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Experiment`
--

LOCK TABLES `Experiment` WRITE;
/*!40000 ALTER TABLE `Experiment` DISABLE KEYS */;
INSERT INTO `Experiment` VALUES (1,'EXP1','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_1',26),(2,'EXP2','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_2',26),(3,'EXP3','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_3',26),(4,'EXP4','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_4',26),(5,'EXP5','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_5',26),(6,'EXP6','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_6',26),(7,'EXP7','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_7',26),(8,'EXP8','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_8',26),(9,'EXP9','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_9',26),(10,'EXP10','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_10',26),(11,'EXP11','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_11',26),(12,'EXP12','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_12',26),(13,'EXP13','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_13',26),(14,'EXP14','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_14',26),(15,'EXP15','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_15',26),(16,'EXP16','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_16',26),(17,'EXP17','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_17',26),(18,'EXP18','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_18',26),(19,'EXP19','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_19',26),(20,'EXP20','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_20',26),(21,'EXP21','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_21',26),(22,'EXP22','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_22',26),(23,'EXP23','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_23',26),(24,'EXP24','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_24',26),(25,'EXP25','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_25',26),(26,'EXP26','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_26',26),(27,'EXP27','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_27',26),(28,'EXP28','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_28',26),(29,'EXP29','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_29',26),(30,'EXP30','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_30',26),(31,'EXP31','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_31',26),(32,'EXP32','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_32',26);
/*!40000 ALTER TABLE `Experiment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Experiment_Kit`
--

DROP TABLE IF EXISTS `Experiment_Kit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Experiment_Kit` (
  `experiments_experimentId` bigint(20) NOT NULL,
  `kits_kitId` bigint(20) NOT NULL,
  PRIMARY KEY (`experiments_experimentId`,`kits_kitId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Experiment_Kit`
--

LOCK TABLES `Experiment_Kit` WRITE;
/*!40000 ALTER TABLE `Experiment_Kit` DISABLE KEYS */;
/*!40000 ALTER TABLE `Experiment_Kit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Experiment_Run`
--

DROP TABLE IF EXISTS `Experiment_Run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Experiment_Run` (
  `Experiment_experimentId` bigint(20) NOT NULL,
  `runs_runId` bigint(20) NOT NULL,
  PRIMARY KEY (`Experiment_experimentId`,`runs_runId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Experiment_Run`
--

LOCK TABLES `Experiment_Run` WRITE;
/*!40000 ALTER TABLE `Experiment_Run` DISABLE KEYS */;
/*!40000 ALTER TABLE `Experiment_Run` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Flowcell`
--

DROP TABLE IF EXISTS `Flowcell`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Flowcell`
--

LOCK TABLES `Flowcell` WRITE;
/*!40000 ALTER TABLE `Flowcell` DISABLE KEYS */;
/*!40000 ALTER TABLE `Flowcell` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Flowcell_Chamber`
--

DROP TABLE IF EXISTS `Flowcell_Chamber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Flowcell_Chamber` (
  `Flowcell_flowcellId` bigint(20) NOT NULL,
  `chambers_chamberId` bigint(20) NOT NULL,
  PRIMARY KEY (`Flowcell_flowcellId`,`chambers_chamberId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Flowcell_Chamber`
--

LOCK TABLES `Flowcell_Chamber` WRITE;
/*!40000 ALTER TABLE `Flowcell_Chamber` DISABLE KEYS */;
/*!40000 ALTER TABLE `Flowcell_Chamber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Flowcell_Lane`
--

DROP TABLE IF EXISTS `Flowcell_Lane`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Flowcell_Lane` (
  `Flowcell_flowcellId` bigint(20) NOT NULL,
  `lanes_laneId` bigint(20) NOT NULL,
  PRIMARY KEY (`Flowcell_flowcellId`,`lanes_laneId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Flowcell_Lane`
--

LOCK TABLES `Flowcell_Lane` WRITE;
/*!40000 ALTER TABLE `Flowcell_Lane` DISABLE KEYS */;
/*!40000 ALTER TABLE `Flowcell_Lane` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Kit`
--

DROP TABLE IF EXISTS `Kit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Kit` (
  `kitId` bigint(20) NOT NULL AUTO_INCREMENT,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `lotNumber` varchar(30) NOT NULL,
  `kitDate` date NOT NULL,
  `kitDescriptorId` bigint(20) NOT NULL,
  PRIMARY KEY (`kitId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Kit`
--

LOCK TABLES `Kit` WRITE;
/*!40000 ALTER TABLE `Kit` DISABLE KEYS */;
/*!40000 ALTER TABLE `Kit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `KitDescriptor`
--

DROP TABLE IF EXISTS `KitDescriptor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `KitDescriptor` (
  `kitDescriptorId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `version` int(3) DEFAULT NULL,
  `manufacturer` varchar(100) NOT NULL,
  `partNumber` varchar(50) NOT NULL,
  `stockLevel` int(10) NOT NULL DEFAULT '0',
  `kitType` varchar(30) NOT NULL,
  `platformType` varchar(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`kitDescriptorId`)
) ENGINE=MyISAM AUTO_INCREMENT=169 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `KitDescriptor`
--

LOCK TABLES `KitDescriptor` WRITE;
/*!40000 ALTER TABLE `KitDescriptor` DISABLE KEYS */;
INSERT INTO `KitDescriptor` VALUES (168,'TruSeq_Stranded_TotalRNA',0,'TruSeq','1',0,'Library','Illumina','Illumina Directional Whole Transcriptome Library'),(167,'TruSeq_Stranded_mRNA',0,'TruSeq','1',0,'Library','Illumina','Illumina Directional mRNA Library'),(166,'TruSeq RNA Access',0,'TruSeq','RS-301-2001',0,'Library','Illumina','RS-301-2001'),(165,'TruSeq Amplicon - Cancer Panel',0,'TruSeq','1',0,'Library','Illumina','Associated with Illumina PE libraries. Not compatible with the HiSeq - only MiSeq http://support.illumina.com/sequencing/sequencing_kits/truseq_amplicon_cancer_panel.ilmn'),(164,'Thunderbolt Cancer Panel',0,'Thunderbolt','1',0,'Library','Illumina','n/a'),(163,'Nextera DNA',0,'Nextera','1',0,'Library','Illumina','n/a'),(162,'KAPA Hyper Prep',0,'KAPA','1',0,'Library','Illumina','KK8504 With KAPA Library Amplification Primer Mix (10X) 96 reactions'),(160,'GA_TruSeq_SmRNA',0,'GA','1',0,'Library','Illumina','n/a'),(161,'KAPA BC OnBead',0,'KAPA','1',0,'Library','Illumina','n/a'),(159,'GA_PE',0,'GA','1',0,'Library','Illumina','n/a'),(158,'AmpliSeq-KAPA Hyper Prep V1',1,'AmpliSeq','1',0,'Library','Illumina','Testing'),(157,'AmpliSeq-Illumina V1',1,'AmpliSeq','1',0,'Library','Illumina','AmpliSeq-Illumina Modified Protocol Version 1'),(156,'AmpliSeq-Illumina Exome V1',1,'AmpliSeq','1',0,'Library','Illumina','n/a'),(155,'Agilent SureSelect XT2',0,'Agilent','1',0,'Library','Illumina','n/a'),(154,'Agilent SureSelect XT',0,'Agilent','1',0,'Library','Illumina','n/a'),(153,'Multiprimer Re-hybridization Plate',NULL,'Illumina','GD-305-1001',3,'Sequencing','Illumina','3 Expired'),(152,'HiSeq Rehyb V4',NULL,'Illumina','GD-403-4001',4,'Sequencing','Illumina','4 Expired'),(151,'SR Cluster Kit',NULL,'Illumina','GD-401-4001',1,'Sequencing','Illumina','(Cluster Kit): 1-Expired & (SR Flowcells): 2'),(150,'MiSeq Reagent Kit 600 Cycle (V3)',NULL,'Illumina','MS-102-3003',1,'Sequencing','Illumina','(Box 1 of 2): 1--Expired & (Box 2 of 2): 1'),(149,'Dual Index Seq. Primer Single End',NULL,'Illumina','FC-121-1003',2,'Sequencing','Illumina','All Expired'),(148,'Dual Index Seq. Primer Paired End',NULL,'Illumina','PE-121-1003',14,'Sequencing','Illumina','n/a'),(147,'MiSeq Reagent Kit 500 cycle',NULL,'Illumina','MS-102-2003',1,'Sequencing','Illumina','(Box 1 of 2): 1--Expired & (Box 2 of 2): 11'),(146,'MiSeq Reagent Kit 50 Cycle',NULL,'Illumina','MS-102-2001',0,'Sequencing','Illumina','(Box 1 of 2): 0 & (Box 2 of 2): 9'),(145,'Rapid Duo cBot Sample Plate Box',NULL,'Illumina','n/a',2,'Clustering','Illumina','n/a'),(144,'Rapid V2 SBS Reagent Kit 500 cycle',NULL,'Illumina','FC-402-4023',1,'Sequencing','Illumina','(Box 1 of 2): 1 & (Box 2 of 2): 1'),(143,'SBS Reagent Kit 200 Cycle',NULL,'Illumina','FC-401-3001',5,'Sequencing','Illumina','(Box 1 of 2): 9 & (Box 2 of 2): 5. Box 1 - 5 expired. Box 2 - 2 expired'),(142,'SBS Rapid Run Kits - 50 Cycle',NULL,'Illumina','FC-402-4002',1,'Sequencing','Illumina','Expired'),(141,'SBS Rapid Run Kits - 200 Cycle',NULL,'Illumina','FC-402-4001',7,'Sequencing','Illumina','Expired'),(140,'V3 PE Cluster Kit',NULL,'Illumina','PE-401-3001',7,'Clustering','Illumina','(Box 1 of 2): 10 & (Box 2 of 2): 7 & (V3 Flowcells): 14 (Truseq Multiplex Seq Primer): 13'),(139,'Rapid PE Cluster Kit',NULL,'Illumina','GD-402-4001',4,'Clustering','Illumina','Expired. (Cluster Kit): 4 & (Rapid Flowcells): 6'),(138,'SR Rapid Cluster Kit',NULL,'Illumina','GD-401-3001',1,'Clustering','Illumina','Expired (Cluster Kit): 1 & (SR Rapid Flowcells): 1'),(137,'TruSeq RapidRun Duo cBot',NULL,'Illumina','CT-402-4001',1,'Clustering','Illumina','Expired'),(136,'HiSeq Rapid Duo cBot Sample Plate Box',NULL,'Illumina','n/a',2,'Clustering','Illumina','n/a'),(135,'Rapid V2 SBS Reagent Kit 50 Cycle',NULL,'Illumina','FC-402-4022',2,'Sequencing','Illumina','(Box 1 of 2): 2 & (Box 2 of 2): 2'),(134,'Rapid V2 SBS Reagent Kit 200 Cycle',NULL,'Illumina','FC-402-4021',1,'Sequencing','Illumina','(Box 1 of 2): 1 & (Box 2 of 2): 1'),(133,'Rapid V2 PE Cluster Kit',NULL,'Illumina','PE-402-4002',2,'Clustering','Illumina','(Cluster Kits): 2 & (Rapid V2 Flowcells): 2'),(132,'SBS Reagent Kit 250 Cycle',NULL,'Illumina','FC-401-4003',4,'Sequencing','Illumina','(Box 1 of 2): 4 & (Box 2 of 2): 4'),(130,'V4 PE Cluster Kit',NULL,'Illumina','PE-401-4001',4,'Sequencing','Illumina','(Box 1 of 2): 4 & (Box 2 of 2): 4 & (V4 Flowcells): 4'),(131,'MiSeq Reagent Kit 300 Cycle',NULL,'Illumina','MS-102-2002',4,'Sequencing','Illumina','(Box 1 of 2): 4 & (Box 2 of 2): 7');
/*!40000 ALTER TABLE `KitDescriptor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Kit_Note`
--

DROP TABLE IF EXISTS `Kit_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Kit_Note` (
  `kit_kitId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`kit_kitId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Kit_Note`
--

LOCK TABLES `Kit_Note` WRITE;
/*!40000 ALTER TABLE `Kit_Note` DISABLE KEYS */;
/*!40000 ALTER TABLE `Kit_Note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Lane`
--

DROP TABLE IF EXISTS `Lane`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Lane` (
  `laneId` bigint(20) NOT NULL AUTO_INCREMENT,
  `laneNumber` tinyint(4) NOT NULL,
  `pool_poolId` bigint(20) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`laneId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Lane`
--

LOCK TABLES `Lane` WRITE;
/*!40000 ALTER TABLE `Lane` DISABLE KEYS */;
/*!40000 ALTER TABLE `Lane` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Library`
--

DROP TABLE IF EXISTS `Library`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Library`
--

LOCK TABLES `Library` WRITE;
/*!40000 ALTER TABLE `Library` DISABLE KEYS */;
INSERT INTO `Library` VALUES (1,'LIB1','Inherited from TEST_0001',NULL,1,1,'LIB1::TEST_0001_Bn_P_PE_300_WG','LIBRARY_INBOX_A01',3,0,'2015-08-27','Illumina','TEST_0001_Bn_P_PE_300_WG','',1,1,'true'),(2,'LIB2','Inherited from TEST_0001',NULL,1,2,'LIB2::TEST_0001_Bn_R_PE_300_WG','LIBRARY_INBOX_A02',3,0,'2015-08-27','Illumina','TEST_0001_Bn_R_PE_300_WG','',1,1,'true'),(3,'LIB3','Inherited from TEST_0002',NULL,1,3,'LIB3::TEST_0002_Bn_P_PE_300_WG','LIBRARY_INBOX_A03',3,0,'2015-08-27','Illumina','TEST_0002_Bn_P_PE_300_WG','',1,1,'true'),(4,'LIB4','Inherited from TEST_0002',NULL,1,4,'LIB4::TEST_0002_Bn_R_PE_300_WG','LIBRARY_INBOX_A04',3,0,'2015-08-27','Illumina','TEST_0002_Bn_R_PE_300_WG','',1,1,'true'),(5,'LIB5','Inherited from TEST_0003',NULL,1,5,'LIB5::TEST_0003_Bn_P_PE_300_WG','LIBRARY_INBOX_A05',3,0,'2015-08-27','Illumina','TEST_0003_Bn_P_PE_300_WG','',1,1,'true'),(6,'LIB6','Inherited from TEST_0003',NULL,1,6,'LIB6::TEST_0003_Bn_R_PE_300_WG','LIBRARY_INBOX_A06',3,0,'2015-08-27','Illumina','TEST_0003_Bn_R_PE_300_WG','',1,1,'true'),(7,'LIB7','Inherited from TEST_0004',NULL,1,7,'LIB7::TEST_0004_Bn_P_PE_300_WG','LIBRARY_INBOX_A07',3,0,'2015-08-27','Illumina','TEST_0004_Bn_P_PE_300_WG','',1,1,'true'),(8,'LIB8','Inherited from TEST_0004',NULL,1,8,'LIB8::TEST_0004_Bn_R_PE_300_WG','LIBRARY_INBOX_A08',3,0,'2015-08-27','Illumina','TEST_0004_Bn_R_PE_300_WG','',1,1,'true'),(9,'LIB9','Inherited from TEST_0005',NULL,1,9,'LIB9::TEST_0005_Bn_P_PE_300_WG','LIBRARY_INBOX_A09',3,0,'2015-08-27','Illumina','TEST_0005_Bn_P_PE_300_WG','',1,1,'true'),(10,'LIB10','Inherited from TEST_0005',NULL,1,10,'LIB10::TEST_0005_Bn_R_PE_300_WG','LIBRARY_INBOX_A10',3,0,'2015-08-27','Illumina','TEST_0005_Bn_R_PE_300_WG','',1,1,'true'),(11,'LIB11','Inherited from TEST_0006',NULL,1,11,'LIB11::TEST_0006_Bn_P_PE_300_WG','LIBRARY_INBOX_B01',3,0,'2015-08-27','Illumina','TEST_0006_Bn_P_PE_300_WG','',1,1,'true'),(12,'LIB12','Inherited from TEST_0006',NULL,1,12,'LIB12::TEST_0006_Bn_R_PE_300_WG','LIBRARY_INBOX_B02',3,0,'2015-08-27','Illumina','TEST_0006_Bn_R_PE_300_WG','',1,1,'true'),(13,'LIB13','Inherited from TEST_0007',NULL,1,13,'LIB13::TEST_0007_Bn_P_PE_300_WG','LIBRARY_INBOX_B03',3,0,'2015-08-27','Illumina','TEST_0007_Bn_P_PE_300_WG','',1,1,'true'),(14,'LIB14','Inherited from TEST_0007',NULL,1,14,'LIB14::TEST_0007_Bn_R_PE_300_WG','LIBRARY_INBOX_B04',3,0,'2015-08-27','Illumina','TEST_0007_Bn_R_PE_300_WG','',1,1,'true');
/*!40000 ALTER TABLE `Library` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LibraryDilution`
--

DROP TABLE IF EXISTS `LibraryDilution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LibraryDilution`
--

LOCK TABLES `LibraryDilution` WRITE;
/*!40000 ALTER TABLE `LibraryDilution` DISABLE KEYS */;
INSERT INTO `LibraryDilution` VALUES (1,2,1,'LDI1::TEST_0001_Bn_P_PE_300_WG','2015-08-27','admin','LDI1',1),(2,2,2,'LDI2::TEST_0001_Bn_R_PE_300_WG','2015-08-27','admin','LDI2',1),(3,2,3,'LDI3::TEST_0002_Bn_P_PE_300_WG','2015-08-27','admin','LDI3',1),(4,2,4,'LDI4::TEST_0002_Bn_R_PE_300_WG','2015-08-27','admin','LDI4',1),(5,2,5,'LDI5::TEST_0003_Bn_P_PE_300_WG','2015-08-27','admin','LDI5',1),(6,2,6,'LDI6::TEST_0003_Bn_R_PE_300_WG','2015-08-27','admin','LDI6',1),(7,2,7,'LDI7::TEST_0004_Bn_P_PE_300_WG','2015-08-27','admin','LDI7',1),(8,2,8,'LDI8::TEST_0004_Bn_R_PE_300_WG','2015-08-27','admin','LDI8',1),(9,2,9,'LDI9::TEST_0005_Bn_P_PE_300_WG','2015-08-27','admin','LDI9',1),(10,2,10,'LDI10::TEST_0005_Bn_R_PE_300_WG','2015-08-27','admin','LDI10',1),(11,2,11,'LDI11::TEST_0006_Bn_P_PE_300_WG','2015-08-27','admin','LDI11',1),(12,2,12,'LDI12::TEST_0006_Bn_R_PE_300_WG','2015-08-27','admin','LDI12',1),(13,2,13,'LDI13::TEST_0007_Bn_P_PE_300_WG','2015-08-27','admin','LDI13',1),(14,2,14,'LDI14::TEST_0007_Bn_R_PE_300_WG','2015-08-27','admin','LDI14',1);
/*!40000 ALTER TABLE `LibraryDilution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LibraryQC`
--

DROP TABLE IF EXISTS `LibraryQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `LibraryQC` (
  `qcId` bigint(20) NOT NULL AUTO_INCREMENT,
  `library_libraryId` bigint(20) NOT NULL,
  `qcUserName` varchar(255) NOT NULL,
  `qcDate` date NOT NULL,
  `qcMethod` bigint(20) DEFAULT NULL,
  `results` double DEFAULT NULL,
  `insertSize` int(11) NOT NULL,
  PRIMARY KEY (`qcId`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LibraryQC`
--

LOCK TABLES `LibraryQC` WRITE;
/*!40000 ALTER TABLE `LibraryQC` DISABLE KEYS */;
INSERT INTO `LibraryQC` VALUES (1,1,'admin','2015-08-27',4,3,300),(2,2,'admin','2015-08-27',4,3,300),(3,3,'admin','2015-08-27',4,3,300),(4,4,'admin','2015-08-27',4,3,300),(5,5,'admin','2015-08-27',4,3,300),(6,6,'admin','2015-08-27',4,3,300),(7,7,'admin','2015-08-27',4,3,300),(8,8,'admin','2015-08-27',4,3,300),(9,9,'admin','2015-08-27',4,3,300),(10,10,'admin','2015-08-27',4,3,300),(11,11,'admin','2015-08-27',4,3,300),(12,12,'admin','2015-08-27',4,3,300),(13,13,'admin','2015-08-27',4,3,300),(14,14,'admin','2015-08-27',4,3,300);
/*!40000 ALTER TABLE `LibraryQC` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LibrarySelectionType`
--

DROP TABLE IF EXISTS `LibrarySelectionType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `LibrarySelectionType` (
  `librarySelectionTypeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`librarySelectionTypeId`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LibrarySelectionType`
--

LOCK TABLES `LibrarySelectionType` WRITE;
/*!40000 ALTER TABLE `LibrarySelectionType` DISABLE KEYS */;
INSERT INTO `LibrarySelectionType` VALUES (1,'PCR','Source material was selected by designed primers'),(2,'other','Other library enrichment, screening, or selection process'),(3,'Reduced Representation','Reproducible genomic subsets, often generated by restriction fragment size selection, containing a manageable number of loci to facilitate re-sampling'),(4,'Hybrid Selection','Selection by hybridization in array or solution'),(5,'ChIP','Chromatin Immunoprecipitation'),(6,'cDNA','Complementary DNA'),(7,'size fractionation','Physical selection of size appropriate targets'),(8,'Restriction Digest','DNA fractionation using restriction enzymes');
/*!40000 ALTER TABLE `LibrarySelectionType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LibraryStrategyType`
--

DROP TABLE IF EXISTS `LibraryStrategyType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `LibraryStrategyType` (
  `libraryStrategyTypeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`libraryStrategyTypeId`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LibraryStrategyType`
--

LOCK TABLES `LibraryStrategyType` WRITE;
/*!40000 ALTER TABLE `LibraryStrategyType` DISABLE KEYS */;
INSERT INTO `LibraryStrategyType` VALUES (1,'WGS','Whole genome shotgun'),(2,'AMPLICON','Sequencing of overlapping or distinct PCR or RT-PCR products'),(3,'ChIP-Seq','Direct sequencing of chromatin immunoprecipitates'),(4,'Bisulfite-Seq','Sequencing\n following treatment of DNA with Bisulfite-Seq bisulfite to convert \ncytosine residues to uracil depending on methylation status'),(5,'OTHER','Library strategy not listed'),(6,'MeDIP-Seq','Metylated DNA Immunoprecipitation Sequencing strategy'),(7,'RNA-Seq','Random sequencing of whole transcriptome'),(8,'WXS','Random sequencing of exonic regions selected from the genome');
/*!40000 ALTER TABLE `LibraryStrategyType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LibraryType`
--

DROP TABLE IF EXISTS `LibraryType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `LibraryType` (
  `libraryTypeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `platformType` varchar(50) NOT NULL,
  PRIMARY KEY (`libraryTypeId`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LibraryType`
--

LOCK TABLES `LibraryType` WRITE;
/*!40000 ALTER TABLE `LibraryType` DISABLE KEYS */;
INSERT INTO `LibraryType` VALUES (1,'mRNA Seq','Illumina'),(2,'Mate Pair','Illumina'),(3,'Paired End','Illumina'),(4,'Small RNA','Illumina'),(5,'Single End','Illumina'),(6,'cDNA','PacBio'),(7,'Amplicon','PacBio'),(8,'2kb Shotgun','PacBio'),(9,'10kb Shotgun','PacBio'),(10,'20kb Shotgun','PacBio'),(11,'Plasmid','PacBio'),(12,'BAC','PacBio');
/*!40000 ALTER TABLE `LibraryType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Library_Note`
--

DROP TABLE IF EXISTS `Library_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Library_Note` (
  `library_libraryId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`library_libraryId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Library_Note`
--

LOCK TABLES `Library_Note` WRITE;
/*!40000 ALTER TABLE `Library_Note` DISABLE KEYS */;
/*!40000 ALTER TABLE `Library_Note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Library_TagBarcode`
--

DROP TABLE IF EXISTS `Library_TagBarcode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Library_TagBarcode` (
  `library_libraryId` bigint(20) NOT NULL,
  `barcode_barcodeId` bigint(20) NOT NULL,
  PRIMARY KEY (`library_libraryId`,`barcode_barcodeId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Library_TagBarcode`
--

LOCK TABLES `Library_TagBarcode` WRITE;
/*!40000 ALTER TABLE `Library_TagBarcode` DISABLE KEYS */;
INSERT INTO `Library_TagBarcode` VALUES (1,12),(2,11),(3,10),(4,9),(5,8),(6,7),(7,6),(8,5),(9,4),(10,3),(11,2),(12,1),(13,24),(14,23);
/*!40000 ALTER TABLE `Library_TagBarcode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Note`
--

DROP TABLE IF EXISTS `Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Note` (
  `noteId` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` date NOT NULL,
  `internalOnly` bit(1) NOT NULL DEFAULT b'1',
  `text` text,
  `owner_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`noteId`),
  KEY `FK2524124140968C` (`owner_userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Note`
--

LOCK TABLES `Note` WRITE;
/*!40000 ALTER TABLE `Note` DISABLE KEYS */;
/*!40000 ALTER TABLE `Note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Plate`
--

DROP TABLE IF EXISTS `Plate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Plate`
--

LOCK TABLES `Plate` WRITE;
/*!40000 ALTER TABLE `Plate` DISABLE KEYS */;
/*!40000 ALTER TABLE `Plate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Plate_Elements`
--

DROP TABLE IF EXISTS `Plate_Elements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Plate_Elements` (
  `plate_plateId` bigint(20) NOT NULL,
  `elementType` varchar(255) NOT NULL,
  `elementPosition` int(11) NOT NULL,
  `elementId` bigint(20) NOT NULL,
  PRIMARY KEY (`plate_plateId`,`elementId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Plate_Elements`
--

LOCK TABLES `Plate_Elements` WRITE;
/*!40000 ALTER TABLE `Plate_Elements` DISABLE KEYS */;
/*!40000 ALTER TABLE `Plate_Elements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Plate_Library`
--

DROP TABLE IF EXISTS `Plate_Library`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Plate_Library` (
  `plate_plateId` bigint(20) NOT NULL,
  `library_libraryId` bigint(20) NOT NULL,
  PRIMARY KEY (`plate_plateId`,`library_libraryId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Plate_Library`
--

LOCK TABLES `Plate_Library` WRITE;
/*!40000 ALTER TABLE `Plate_Library` DISABLE KEYS */;
/*!40000 ALTER TABLE `Plate_Library` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Platform`
--

DROP TABLE IF EXISTS `Platform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Platform` (
  `platformId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `instrumentModel` varchar(100) NOT NULL,
  `description` varchar(255) NOT NULL,
  `numContainers` tinyint(4) NOT NULL,
  PRIMARY KEY (`platformId`)
) ENGINE=MyISAM AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Platform`
--

LOCK TABLES `Platform` WRITE;
/*!40000 ALTER TABLE `Platform` DISABLE KEYS */;
INSERT INTO `Platform` VALUES (6,'Illumina','unspecified','4-channel flowgram',1),(7,'Illumina','Illumina Genome Analyzer II','4-channel flowgram',1),(8,'Illumina','Illumina Genome Analyzer','4-channel flowgram',1),(9,'Illumina','Solexa 1G Genome Analyzer','4-channel flowgram',1),(16,'Illumina','Illumina HiSeq 2000','4-channel flowgram',1),(19,'Illumina','Illumina Genome Analyzer IIx','4-channel flowgram',1),(20,'PacBio','PacBio RS','',1),(24,'Illumina','Illumina MiSeq','',1),(25,'Illumina','Illumina HiSeq 1000','',1),(26,'Illumina','Illumina HiSeq 2500','4-channel flowgram',1);
/*!40000 ALTER TABLE `Platform` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Pool`
--

DROP TABLE IF EXISTS `Pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Pool`
--

LOCK TABLES `Pool` WRITE;
/*!40000 ALTER TABLE `Pool` DISABLE KEYS */;
INSERT INTO `Pool` VALUES (1,2,'IPO1::Illumina','IPO1','2015-08-27',2,NULL,'Illumina',0,'Pool 1',NULL),(2,2,'IPO2::Illumina','IPO2','2015-08-27',3,NULL,'Illumina',0,'Pool 2',NULL),(3,2,'IPO3::Illumina','IPO3','2015-08-27',4,NULL,'Illumina',0,'Pool 3',NULL),(4,2,'IPO4::Illumina','IPO4','2015-08-27',5,NULL,'Illumina',0,'Pool 4',NULL),(5,2,'IPO5::Illumina','IPO5','2015-08-27',6,NULL,'Illumina',0,'Pool 5',NULL),(6,2,'IPO6::Illumina','IPO6','2015-08-27',7,NULL,'Illumina',0,'Pool 6',NULL),(7,2,'IPO7::Illumina','IPO7','2015-08-27',8,NULL,'Illumina',0,'Pool 7',NULL),(8,2,'IPO8::Illumina','IPO8','2015-08-27',9,NULL,'Illumina',0,'Pool 8',NULL),(9,2,'IPO9::Illumina','IPO9','2015-08-27',10,NULL,'Illumina',0,'Pool 9',NULL),(10,2,'IPO10::Illumina','IPO10','2015-08-27',11,NULL,'Illumina',0,'Pool 10',NULL);
/*!40000 ALTER TABLE `Pool` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PoolQC`
--

DROP TABLE IF EXISTS `PoolQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PoolQC` (
  `qcId` bigint(20) NOT NULL AUTO_INCREMENT,
  `pool_poolId` bigint(20) NOT NULL,
  `qcUserName` varchar(255) NOT NULL,
  `qcDate` date NOT NULL,
  `qcMethod` bigint(20) DEFAULT NULL,
  `results` double DEFAULT NULL,
  PRIMARY KEY (`qcId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PoolQC`
--

LOCK TABLES `PoolQC` WRITE;
/*!40000 ALTER TABLE `PoolQC` DISABLE KEYS */;
/*!40000 ALTER TABLE `PoolQC` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Pool_Elements`
--

DROP TABLE IF EXISTS `Pool_Elements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pool_Elements` (
  `pool_poolId` bigint(20) NOT NULL,
  `elementType` varchar(255) NOT NULL,
  `elementId` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_poolId`,`elementId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Pool_Elements`
--

LOCK TABLES `Pool_Elements` WRITE;
/*!40000 ALTER TABLE `Pool_Elements` DISABLE KEYS */;
INSERT INTO `Pool_Elements` VALUES (1,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',2),(2,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',6),(2,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',5),(2,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',4),(3,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',7),(3,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',8),(3,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',9),(3,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',10),(3,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',11),(3,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',12),(3,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',13),(4,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',1),(5,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',2),(6,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',3),(6,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',4),(6,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',5),(6,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',6),(7,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',7),(7,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',8),(8,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',8),(8,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',9),(9,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',11),(9,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',12),(10,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',13),(10,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',14),(3,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',14),(1,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',1),(2,'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution',3);
/*!40000 ALTER TABLE `Pool_Elements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Pool_Experiment`
--

DROP TABLE IF EXISTS `Pool_Experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pool_Experiment` (
  `pool_poolId` bigint(20) NOT NULL,
  `experiments_experimentId` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_poolId`,`experiments_experimentId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Pool_Experiment`
--

LOCK TABLES `Pool_Experiment` WRITE;
/*!40000 ALTER TABLE `Pool_Experiment` DISABLE KEYS */;
INSERT INTO `Pool_Experiment` VALUES (1,1),(2,2),(3,3),(3,4),(4,5),(4,6),(4,7),(4,8),(5,9),(5,10),(5,11),(5,12),(6,13),(6,14),(6,15),(6,16),(7,17),(7,18),(7,19),(7,20),(8,21),(8,22),(8,23),(8,24),(9,29),(9,30),(9,31),(9,32),(10,25),(10,26),(10,27),(10,28);
/*!40000 ALTER TABLE `Pool_Experiment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Pool_LibraryDilution`
--

DROP TABLE IF EXISTS `Pool_LibraryDilution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pool_LibraryDilution` (
  `pool_poolId` bigint(20) NOT NULL,
  `dilutions_dilutionId` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_poolId`,`dilutions_dilutionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Pool_LibraryDilution`
--

LOCK TABLES `Pool_LibraryDilution` WRITE;
/*!40000 ALTER TABLE `Pool_LibraryDilution` DISABLE KEYS */;
/*!40000 ALTER TABLE `Pool_LibraryDilution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Pool_emPCRDilution`
--

DROP TABLE IF EXISTS `Pool_emPCRDilution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pool_emPCRDilution` (
  `pool_poolId` bigint(20) NOT NULL,
  `dilutions_dilutionId` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_poolId`,`dilutions_dilutionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Pool_emPCRDilution`
--

LOCK TABLES `Pool_emPCRDilution` WRITE;
/*!40000 ALTER TABLE `Pool_emPCRDilution` DISABLE KEYS */;
/*!40000 ALTER TABLE `Pool_emPCRDilution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PrintJob`
--

DROP TABLE IF EXISTS `PrintJob`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PrintJob` (
  `jobId` bigint(20) NOT NULL AUTO_INCREMENT,
  `printServiceName` varchar(100) NOT NULL,
  `printDate` date NOT NULL,
  `jobCreator_userId` bigint(20) NOT NULL,
  `printedElements` blob NOT NULL,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`jobId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PrintJob`
--

LOCK TABLES `PrintJob` WRITE;
/*!40000 ALTER TABLE `PrintJob` DISABLE KEYS */;
/*!40000 ALTER TABLE `PrintJob` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PrintService`
--

DROP TABLE IF EXISTS `PrintService`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PrintService`
--

LOCK TABLES `PrintService` WRITE;
/*!40000 ALTER TABLE `PrintService` DISABLE KEYS */;
/*!40000 ALTER TABLE `PrintService` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Project`
--

DROP TABLE IF EXISTS `Project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Project`
--

LOCK TABLES `Project` WRITE;
/*!40000 ALTER TABLE `Project` DISABLE KEYS */;
INSERT INTO `Project` VALUES (1,'2015-08-27 15:40:15','Test project','PRO1',1,'Active','TEST','2015-08-27 19:40:40');
/*!40000 ALTER TABLE `Project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ProjectOverview`
--

DROP TABLE IF EXISTS `ProjectOverview`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ProjectOverview`
--

LOCK TABLES `ProjectOverview` WRITE;
/*!40000 ALTER TABLE `ProjectOverview` DISABLE KEYS */;
/*!40000 ALTER TABLE `ProjectOverview` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ProjectOverview_Note`
--

DROP TABLE IF EXISTS `ProjectOverview_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProjectOverview_Note` (
  `overview_overviewId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`overview_overviewId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ProjectOverview_Note`
--

LOCK TABLES `ProjectOverview_Note` WRITE;
/*!40000 ALTER TABLE `ProjectOverview_Note` DISABLE KEYS */;
/*!40000 ALTER TABLE `ProjectOverview_Note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Project_Issues`
--

DROP TABLE IF EXISTS `Project_Issues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Project_Issues` (
  `project_projectId` bigint(20) NOT NULL,
  `issueKey` varchar(255) NOT NULL,
  PRIMARY KEY (`project_projectId`,`issueKey`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Project_Issues`
--

LOCK TABLES `Project_Issues` WRITE;
/*!40000 ALTER TABLE `Project_Issues` DISABLE KEYS */;
/*!40000 ALTER TABLE `Project_Issues` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Project_Note`
--

DROP TABLE IF EXISTS `Project_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Project_Note` (
  `project_projectId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`project_projectId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Project_Note`
--

LOCK TABLES `Project_Note` WRITE;
/*!40000 ALTER TABLE `Project_Note` DISABLE KEYS */;
/*!40000 ALTER TABLE `Project_Note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Project_ProjectOverview`
--

DROP TABLE IF EXISTS `Project_ProjectOverview`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Project_ProjectOverview` (
  `project_projectId` bigint(20) NOT NULL,
  `overviews_overviewId` bigint(20) NOT NULL,
  PRIMARY KEY (`project_projectId`,`overviews_overviewId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Project_ProjectOverview`
--

LOCK TABLES `Project_ProjectOverview` WRITE;
/*!40000 ALTER TABLE `Project_ProjectOverview` DISABLE KEYS */;
/*!40000 ALTER TABLE `Project_ProjectOverview` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Project_Request`
--

DROP TABLE IF EXISTS `Project_Request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Project_Request` (
  `Project_projectId` bigint(20) NOT NULL,
  `requests_requestId` bigint(20) NOT NULL,
  UNIQUE KEY `requests_requestId` (`requests_requestId`),
  KEY `FKDA6E0B2925FFBF98` (`Project_projectId`),
  KEY `FKDA6E0B29B36A83EF` (`requests_requestId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Project_Request`
--

LOCK TABLES `Project_Request` WRITE;
/*!40000 ALTER TABLE `Project_Request` DISABLE KEYS */;
/*!40000 ALTER TABLE `Project_Request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Project_Study`
--

DROP TABLE IF EXISTS `Project_Study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Project_Study` (
  `Project_projectId` bigint(20) NOT NULL,
  `studies_studyId` bigint(20) NOT NULL,
  KEY `studyId` (`studies_studyId`) USING BTREE,
  KEY `projectId` (`Project_projectId`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Project_Study`
--

LOCK TABLES `Project_Study` WRITE;
/*!40000 ALTER TABLE `Project_Study` DISABLE KEYS */;
INSERT INTO `Project_Study` VALUES (1,1);
/*!40000 ALTER TABLE `Project_Study` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QCType`
--

DROP TABLE IF EXISTS `QCType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QCType` (
  `qcTypeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `qcTarget` varchar(50) NOT NULL,
  `units` varchar(20) NOT NULL,
  PRIMARY KEY (`qcTypeId`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QCType`
--

LOCK TABLES `QCType` WRITE;
/*!40000 ALTER TABLE `QCType` DISABLE KEYS */;
INSERT INTO `QCType` VALUES (2,'Bioanalyzer','Chip-based capillary electrophoresis machine to analyse RNA, DNA, and protein, manufactured by Agilent','Library','nM'),(1,'QuBit','Quantitation of DNA, RNA and protein, manufacturered by Invitrogen','Sample','ng/&#181;l'),(3,'Bioanalyser','Chip-based capillary electrophoresis machine to analyse RNA, DNA, and protein, manufactured by Agilent','Sample','ng/&#181;l'),(4,'QuBit','Quantitation of DNA, RNA and protein, manufacturered by Invitrogen','Library','ng/&#181;l'),(6,'SeqInfo QC','Post-run completion run QC step, undertaken by the SeqInfo team, as part of the primary analysis stage.','Run',''),(5,'SeqOps QC','Post-run completion run QC step, undertaken by the SeqOps team, to move a run through to the primary analysis stage.','Run',''),(7,'qPCR','Quantitative PCR','Library','mol/&#181;l'),(8,'qPCR','Quantitative PCR','Pool','mol/&#181;l');
/*!40000 ALTER TABLE `QCType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Request`
--

DROP TABLE IF EXISTS `Request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Request`
--

LOCK TABLES `Request` WRITE;
/*!40000 ALTER TABLE `Request` DISABLE KEYS */;
/*!40000 ALTER TABLE `Request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Request_Note`
--

DROP TABLE IF EXISTS `Request_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Request_Note` (
  `Request_requestId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  UNIQUE KEY `notes_noteId` (`notes_noteId`),
  KEY `FK57687FE2A7DC4D2C` (`notes_noteId`),
  KEY `FK57687FE2E8B554FA` (`Request_requestId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Request_Note`
--

LOCK TABLES `Request_Note` WRITE;
/*!40000 ALTER TABLE `Request_Note` DISABLE KEYS */;
/*!40000 ALTER TABLE `Request_Note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Run`
--

DROP TABLE IF EXISTS `Run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Run`
--

LOCK TABLES `Run` WRITE;
/*!40000 ALTER TABLE `Run` DISABLE KEYS */;
INSERT INTO `Run` VALUES (1,'RUN1','BC0JHTACXX',NULL,0,1,202,'/.mounts/labs/prod/archive/h1179/120323_h1179_0070_BC0JHTACXX',12,'Illumina',1,'120323_h1179_0070_BC0JHTACXX',1),(2,'RUN2','AD0VJ9ACXX',NULL,2,1,202,'/.mounts/labs/prod/archive/h1179/120404_h1179_0072_AD0VJ9ACXX',13,'Illumina',2,'120404_h1179_0072_AD0VJ9ACXX',1),(3,'RUN3','BC075RACXX',NULL,3,1,209,'/.mounts/labs/prod/archive/h1179/120412_h1179_0073_BC075RACXX',14,'Illumina',3,'120412_h1179_0073_BC075RACXX',1),(4,'RUN4','AC0KY7ACXX',NULL,8,1,209,'/.mounts/labs/prod/archive/h1179/120314_h1179_0068_AC0KY7ACXX',15,'Illumina',4,'120314_h1179_0068_AC0KY7ACXX',1);
/*!40000 ALTER TABLE `Run` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RunQC`
--

DROP TABLE IF EXISTS `RunQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RunQC`
--

LOCK TABLES `RunQC` WRITE;
/*!40000 ALTER TABLE `RunQC` DISABLE KEYS */;
/*!40000 ALTER TABLE `RunQC` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RunQC_Partition`
--

DROP TABLE IF EXISTS `RunQC_Partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RunQC_Partition` (
  `runQc_runQcId` bigint(20) NOT NULL,
  `containers_containerId` bigint(20) NOT NULL DEFAULT '0',
  `partitionNumber` tinyint(2) NOT NULL,
  PRIMARY KEY (`runQc_runQcId`,`containers_containerId`,`partitionNumber`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RunQC_Partition`
--

LOCK TABLES `RunQC_Partition` WRITE;
/*!40000 ALTER TABLE `RunQC_Partition` DISABLE KEYS */;
/*!40000 ALTER TABLE `RunQC_Partition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Run_Flowcell`
--

DROP TABLE IF EXISTS `Run_Flowcell`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Run_Flowcell` (
  `Run_runId` bigint(20) NOT NULL,
  `flowcells_flowcellId` bigint(20) NOT NULL,
  PRIMARY KEY (`Run_runId`,`flowcells_flowcellId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Run_Flowcell`
--

LOCK TABLES `Run_Flowcell` WRITE;
/*!40000 ALTER TABLE `Run_Flowcell` DISABLE KEYS */;
/*!40000 ALTER TABLE `Run_Flowcell` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Run_Note`
--

DROP TABLE IF EXISTS `Run_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Run_Note` (
  `run_runId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`run_runId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Run_Note`
--

LOCK TABLES `Run_Note` WRITE;
/*!40000 ALTER TABLE `Run_Note` DISABLE KEYS */;
/*!40000 ALTER TABLE `Run_Note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Run_SequencerPartitionContainer`
--

DROP TABLE IF EXISTS `Run_SequencerPartitionContainer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Run_SequencerPartitionContainer` (
  `Run_runId` bigint(20) NOT NULL,
  `containers_containerId` bigint(20) NOT NULL,
  PRIMARY KEY (`Run_runId`,`containers_containerId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Run_SequencerPartitionContainer`
--

LOCK TABLES `Run_SequencerPartitionContainer` WRITE;
/*!40000 ALTER TABLE `Run_SequencerPartitionContainer` DISABLE KEYS */;
INSERT INTO `Run_SequencerPartitionContainer` VALUES (1,1),(2,2),(3,3),(4,4);
/*!40000 ALTER TABLE `Run_SequencerPartitionContainer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Sample`
--

DROP TABLE IF EXISTS `Sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Sample`
--

LOCK TABLES `Sample` WRITE;
/*!40000 ALTER TABLE `Sample` DISABLE KEYS */;
INSERT INTO `Sample` VALUES (1,NULL,'SAM1','Inherited from TEST_0001',1,'SAM1::TEST_0001_Bn_P_nn_1-1_D_1','Freezer1_1','GENOMIC','2015-01-27','true','TEST_0001_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL),(2,NULL,'SAM2','Inherited from TEST_0001',1,'SAM2::TEST_0001_Bn_R_nn_1-1_D_1','Freezer1_2','GENOMIC','2015-01-27','true','TEST_0001_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL),(3,NULL,'SAM3','Inherited from TEST_0002',1,'SAM3::TEST_0002_Bn_P_nn_1-1_D_1','Freezer1_3','GENOMIC','2015-01-27','true','TEST_0002_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL),(4,NULL,'SAM4','Inherited from TEST_0002',1,'SAM4::TEST_0002_Bn_R_nn_1-1_D_1','Freezer1_4','GENOMIC','2015-01-27','true','TEST_0002_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL),(5,NULL,'SAM5','Inherited from TEST_0003',1,'SAM5::TEST_0003_Bn_P_nn_1-1_D_1','Freezer1_5','GENOMIC','2015-01-27','true','TEST_0003_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL),(6,NULL,'SAM6','Inherited from TEST_0003',1,'SAM6::TEST_0003_Bn_R_nn_1-1_D_1','Freezer1_6','GENOMIC','2015-01-27','true','TEST_0003_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL),(7,NULL,'SAM7','Inherited from TEST_0004',1,'SAM7::TEST_0004_Bn_P_nn_1-1_D_1','Freezer1_7','GENOMIC','2015-01-27','true','TEST_0004_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL),(8,NULL,'SAM8','Inherited from TEST_0004',1,'SAM8::TEST_0004_Bn_R_nn_1-1_D_1','Freezer1_8','GENOMIC','2015-01-27','true','TEST_0004_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL),(9,NULL,'SAM9','Inherited from TEST_0005',1,'SAM9::TEST_0005_Bn_P_nn_1-1_D_1','Freezer1_9','GENOMIC','2015-01-27','true','TEST_0005_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL),(10,NULL,'SAM10','Inherited from TEST_0005',1,'SAM10::TEST_0005_Bn_R_nn_1-1_D_1','Freezer1_10','GENOMIC','2015-01-27','true','TEST_0005_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL),(11,NULL,'SAM11','Inherited from TEST_0006',1,'SAM11::TEST_0006_Bn_P_nn_1-1_D_1','Freezer1_11','GENOMIC','2015-01-27','true','TEST_0006_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL),(12,NULL,'SAM12','Inherited from TEST_0006',1,'SAM12::TEST_0006_Bn_R_nn_1-1_D_1','Freezer1_12','GENOMIC','2015-01-27','true','TEST_0006_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL),(13,NULL,'SAM13','Inherited from TEST_0007',1,'SAM13::TEST_0007_Bn_P_nn_1-1_D_1','Freezer1_13','GENOMIC','2015-01-27','true','TEST_0007_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL),(14,NULL,'SAM14','Inherited from TEST_0007',1,'SAM14::TEST_0007_Bn_R_nn_1-1_D_1','Freezer1_14','GENOMIC','2015-01-27','true','TEST_0007_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL);
/*!40000 ALTER TABLE `Sample` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SampleQC`
--

DROP TABLE IF EXISTS `SampleQC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SampleQC` (
  `qcId` bigint(20) NOT NULL AUTO_INCREMENT,
  `sample_sampleId` bigint(20) NOT NULL,
  `qcUserName` varchar(255) NOT NULL,
  `qcDate` date NOT NULL,
  `qcMethod` bigint(20) DEFAULT NULL,
  `results` double DEFAULT NULL,
  PRIMARY KEY (`qcId`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SampleQC`
--

LOCK TABLES `SampleQC` WRITE;
/*!40000 ALTER TABLE `SampleQC` DISABLE KEYS */;
INSERT INTO `SampleQC` VALUES (1,1,'admin','2015-08-27',1,5),(2,2,'admin','2015-08-27',1,5),(3,3,'admin','2015-08-27',1,5),(4,4,'admin','2015-08-27',1,5),(5,5,'admin','2015-08-27',1,5),(6,6,'admin','2015-08-27',1,5),(7,7,'admin','2015-08-27',1,5),(8,8,'admin','2015-08-27',1,5),(9,9,'admin','2015-08-27',1,5),(10,10,'admin','2015-08-27',1,5),(11,11,'admin','2015-08-27',1,5),(12,12,'admin','2015-08-27',1,5),(13,13,'admin','2015-08-27',1,5),(14,14,'admin','2015-08-27',1,5);
/*!40000 ALTER TABLE `SampleQC` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SampleType`
--

DROP TABLE IF EXISTS `SampleType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SampleType` (
  `typeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`typeId`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SampleType`
--

LOCK TABLES `SampleType` WRITE;
/*!40000 ALTER TABLE `SampleType` DISABLE KEYS */;
INSERT INTO `SampleType` VALUES (12,'OTHER'),(11,'TRANSCRIPTOMIC'),(10,'SYNTHETIC'),(9,'GENOMIC');
/*!40000 ALTER TABLE `SampleType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Sample_Note`
--

DROP TABLE IF EXISTS `Sample_Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Sample_Note` (
  `sample_sampleId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`sample_sampleId`,`notes_noteId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Sample_Note`
--

LOCK TABLES `Sample_Note` WRITE;
/*!40000 ALTER TABLE `Sample_Note` DISABLE KEYS */;
/*!40000 ALTER TABLE `Sample_Note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SecurityProfile`
--

DROP TABLE IF EXISTS `SecurityProfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SecurityProfile` (
  `profileId` bigint(20) NOT NULL AUTO_INCREMENT,
  `allowAllInternal` bit(1) NOT NULL,
  `owner_userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`profileId`),
  KEY `FK18AEBA294140968C` (`owner_userId`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SecurityProfile`
--

LOCK TABLES `SecurityProfile` WRITE;
/*!40000 ALTER TABLE `SecurityProfile` DISABLE KEYS */;
INSERT INTO `SecurityProfile` VALUES (1,'',1),(2,'',1),(3,'',1),(4,'',1),(5,'',1),(6,'',1),(7,'',1),(8,'',1),(9,'',1),(10,'',1),(11,'',1),(12,'',NULL),(13,'',NULL),(14,'',NULL),(15,'',NULL);
/*!40000 ALTER TABLE `SecurityProfile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SecurityProfile_ReadGroup`
--

DROP TABLE IF EXISTS `SecurityProfile_ReadGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SecurityProfile_ReadGroup` (
  `SecurityProfile_profileId` bigint(20) NOT NULL,
  `readGroup_groupId` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SecurityProfile_ReadGroup`
--

LOCK TABLES `SecurityProfile_ReadGroup` WRITE;
/*!40000 ALTER TABLE `SecurityProfile_ReadGroup` DISABLE KEYS */;
/*!40000 ALTER TABLE `SecurityProfile_ReadGroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SecurityProfile_ReadUser`
--

DROP TABLE IF EXISTS `SecurityProfile_ReadUser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SecurityProfile_ReadUser` (
  `SecurityProfile_profileId` bigint(20) NOT NULL,
  `readUser_userId` bigint(20) NOT NULL,
  KEY `FKD4CF504160F9CBA8` (`SecurityProfile_profileId`),
  KEY `FKD4CF504125267E4D` (`readUser_userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SecurityProfile_ReadUser`
--

LOCK TABLES `SecurityProfile_ReadUser` WRITE;
/*!40000 ALTER TABLE `SecurityProfile_ReadUser` DISABLE KEYS */;
/*!40000 ALTER TABLE `SecurityProfile_ReadUser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SecurityProfile_WriteGroup`
--

DROP TABLE IF EXISTS `SecurityProfile_WriteGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SecurityProfile_WriteGroup` (
  `SecurityProfile_profileId` bigint(20) NOT NULL,
  `writeGroup_groupId` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SecurityProfile_WriteGroup`
--

LOCK TABLES `SecurityProfile_WriteGroup` WRITE;
/*!40000 ALTER TABLE `SecurityProfile_WriteGroup` DISABLE KEYS */;
/*!40000 ALTER TABLE `SecurityProfile_WriteGroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SecurityProfile_WriteUser`
--

DROP TABLE IF EXISTS `SecurityProfile_WriteUser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SecurityProfile_WriteUser` (
  `SecurityProfile_profileId` bigint(20) NOT NULL,
  `writeUser_userId` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SecurityProfile_WriteUser`
--

LOCK TABLES `SecurityProfile_WriteUser` WRITE;
/*!40000 ALTER TABLE `SecurityProfile_WriteUser` DISABLE KEYS */;
/*!40000 ALTER TABLE `SecurityProfile_WriteUser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SequencerPartitionContainer`
--

DROP TABLE IF EXISTS `SequencerPartitionContainer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SequencerPartitionContainer` (
  `containerId` bigint(20) NOT NULL AUTO_INCREMENT,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `platform` bigint(20) DEFAULT NULL,
  `validationBarcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`containerId`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SequencerPartitionContainer`
--

LOCK TABLES `SequencerPartitionContainer` WRITE;
/*!40000 ALTER TABLE `SequencerPartitionContainer` DISABLE KEYS */;
INSERT INTO `SequencerPartitionContainer` VALUES (1,12,'C0JHTACXX','',26,''),(2,13,'D0VJ9ACXX','',26,''),(3,14,'C075RACXX','',26,''),(4,15,'C0KY7ACXX','',26,'');
/*!40000 ALTER TABLE `SequencerPartitionContainer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SequencerPartitionContainer_Partition`
--

DROP TABLE IF EXISTS `SequencerPartitionContainer_Partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SequencerPartitionContainer_Partition` (
  `container_containerId` bigint(20) NOT NULL,
  `partitions_partitionId` bigint(20) NOT NULL,
  PRIMARY KEY (`container_containerId`,`partitions_partitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SequencerPartitionContainer_Partition`
--

LOCK TABLES `SequencerPartitionContainer_Partition` WRITE;
/*!40000 ALTER TABLE `SequencerPartitionContainer_Partition` DISABLE KEYS */;
INSERT INTO `SequencerPartitionContainer_Partition` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(2,9),(2,10),(2,11),(2,12),(2,13),(2,14),(2,15),(2,16),(3,17),(3,18),(3,19),(3,20),(3,21),(3,22),(3,23),(3,24),(4,25),(4,26),(4,27),(4,28),(4,29),(4,30),(4,31),(4,32);
/*!40000 ALTER TABLE `SequencerPartitionContainer_Partition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SequencerReference`
--

DROP TABLE IF EXISTS `SequencerReference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SequencerReference` (
  `referenceId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `ipAddress` blob NOT NULL,
  `platformId` bigint(20) NOT NULL,
  `available` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`referenceId`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SequencerReference`
--

LOCK TABLES `SequencerReference` WRITE;
/*!40000 ALTER TABLE `SequencerReference` DISABLE KEYS */;
INSERT INTO `SequencerReference` VALUES (1,'h1179','\n\0\0Ï',26,'');
/*!40000 ALTER TABLE `SequencerReference` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `State_Key`
--

DROP TABLE IF EXISTS `State_Key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `State_Key` (
  `id` bigint(20) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `State_Key`
--

LOCK TABLES `State_Key` WRITE;
/*!40000 ALTER TABLE `State_Key` DISABLE KEYS */;
/*!40000 ALTER TABLE `State_Key` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `State_Value`
--

DROP TABLE IF EXISTS `State_Value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `State_Value` (
  `id` bigint(20) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `State_Value`
--

LOCK TABLES `State_Value` WRITE;
/*!40000 ALTER TABLE `State_Value` DISABLE KEYS */;
/*!40000 ALTER TABLE `State_Value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Status`
--

DROP TABLE IF EXISTS `Status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Status`
--

LOCK TABLES `Status` WRITE;
/*!40000 ALTER TABLE `Status` DISABLE KEYS */;
INSERT INTO `Status` VALUES (1,'Completed','2012-03-31','2012-03-23','SN7001179','2015-08-28 18:32:29','120323_h1179_0070_BC0JHTACXX','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!--Illumina RTA Status Report-->\n<Status>\n  <Software>Illumina RTA 1.12.4.2</Software>\n  <RunName>120323_h1179_0070_BC0JHTACXX</RunName>\n  <InstrumentName>H1179</InstrumentName>\n  <RunStarted>Tuesday, March 27, 2012 5:22 PM</RunStarted>\n  <NumCycles>202</NumCycles>\n  <ImgCycle>202</ImgCycle>\n  <ScoreCycle>202</ScoreCycle>\n  <CallCycle>202</CallCycle>\n  <InputDir>E:\\Illumina\\HiSeqTemp\\120323_h1179_0070_BC0JHTACXX</InputDir>\n  <OutputDir>\\\\storage4.stg.oicr.on.ca\\bas005\\archive\\h1179\\120323_h1179_0070_BC0JHTACXX</OutputDir>\n  <Configuration>\n    <CopyAllFiles>true</CopyAllFiles>\n    <CopyImages>False</CopyImages>\n    <DeleteImages>True</DeleteImages>\n    <RunInfoExists>True</RunInfoExists>\n    <IsPairedEndRun>True</IsPairedEndRun>\n    <NumberOfReads>2</NumberOfReads>\n    <NumberOfLanes>8</NumberOfLanes>\n    <TilesPerLane>48</TilesPerLane>\n    <ControlLane>8</ControlLane>\n  </Configuration>\n</Status>\n'),(2,'Unknown','2012-04-04','2012-04-04','SN7001179','2015-08-28 18:32:31','120404_h1179_0072_AD0VJ9ACXX','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!--Illumina RTA Status Report-->\n<Status>\n  <Software>Illumina RTA 1.12.4.2</Software>\n  <RunName>120404_h1179_0072_AD0VJ9ACXX</RunName>\n  <InstrumentName>H1179</InstrumentName>\n  <RunStarted>Sunday, April 08, 2012 10:55 AM</RunStarted>\n  <NumCycles>202</NumCycles>\n  <ImgCycle>101</ImgCycle>\n  <ScoreCycle>101</ScoreCycle>\n  <CallCycle>101</CallCycle>\n  <InputDir>D:\\Illumina\\HiSeqTemp\\120404_h1179_0072_AD0VJ9ACXX</InputDir>\n  <OutputDir>\\\\storage4.stg.oicr.on.ca\\bas005\\archive\\h1179\\120404_h1179_0072_AD0VJ9ACXX</OutputDir>\n  <Configuration>\n    <CopyAllFiles>true</CopyAllFiles>\n    <CopyImages>False</CopyImages>\n    <DeleteImages>True</DeleteImages>\n    <RunInfoExists>True</RunInfoExists>\n    <IsPairedEndRun>True</IsPairedEndRun>\n    <NumberOfReads>2</NumberOfReads>\n    <NumberOfLanes>8</NumberOfLanes>\n    <TilesPerLane>48</TilesPerLane>\n    <ControlLane>0</ControlLane>\n  </Configuration>\n</Status>\n'),(3,'Completed','2012-04-20','2012-04-12','SN7001179','2015-08-28 18:32:35','120412_h1179_0073_BC075RACXX','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!--Illumina RTA Status Report-->\n<Status>\n  <Software>Illumina RTA 1.12.4.2</Software>\n  <RunName>120412_h1179_0073_BC075RACXX</RunName>\n  <InstrumentName>H1179</InstrumentName>\n  <RunStarted>Tuesday, April 17, 2012 5:36 PM</RunStarted>\n  <NumCycles>209</NumCycles>\n  <ImgCycle>209</ImgCycle>\n  <ScoreCycle>209</ScoreCycle>\n  <CallCycle>209</CallCycle>\n  <InputDir>E:\\Illumina\\HiSeqTemp\\120412_h1179_0073_BC075RACXX</InputDir>\n  <OutputDir>\\\\storage4.stg.oicr.on.ca\\bas005\\archive\\h1179\\120412_h1179_0073_BC075RACXX</OutputDir>\n  <Configuration>\n    <CopyAllFiles>true</CopyAllFiles>\n    <CopyImages>False</CopyImages>\n    <DeleteImages>True</DeleteImages>\n    <RunInfoExists>True</RunInfoExists>\n    <IsPairedEndRun>True</IsPairedEndRun>\n    <NumberOfReads>3</NumberOfReads>\n    <NumberOfLanes>8</NumberOfLanes>\n    <TilesPerLane>48</TilesPerLane>\n    <ControlLane>0</ControlLane>\n  </Configuration>\n</Status>\n'),(4,'Completed','2012-03-23','2012-03-14','SN7001179','2015-08-28 18:32:35','120314_h1179_0068_AC0KY7ACXX','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!--Illumina RTA Status Report-->\n<Status>\n  <Software>Illumina RTA 1.12.4.2</Software>\n  <RunName>120314_h1179_0068_AC0KY7ACXX</RunName>\n  <InstrumentName>H1179</InstrumentName>\n  <RunStarted>Sunday, March 18, 2012 3:37 PM</RunStarted>\n  <NumCycles>209</NumCycles>\n  <ImgCycle>209</ImgCycle>\n  <ScoreCycle>209</ScoreCycle>\n  <CallCycle>209</CallCycle>\n  <InputDir>D:\\Illumina\\HiSeqTemp\\120314_h1179_0068_AC0KY7ACXX</InputDir>\n  <OutputDir>\\\\storage4.stg.oicr.on.ca\\bas005\\archive\\h1179\\120314_h1179_0068_AC0KY7ACXX</OutputDir>\n  <Configuration>\n    <CopyAllFiles>true</CopyAllFiles>\n    <CopyImages>False</CopyImages>\n    <DeleteImages>True</DeleteImages>\n    <RunInfoExists>True</RunInfoExists>\n    <IsPairedEndRun>True</IsPairedEndRun>\n    <NumberOfReads>3</NumberOfReads>\n    <NumberOfLanes>8</NumberOfLanes>\n    <TilesPerLane>48</TilesPerLane>\n    <ControlLane>0</ControlLane>\n  </Configuration>\n</Status>\n');
/*!40000 ALTER TABLE `Status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Study`
--

DROP TABLE IF EXISTS `Study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Study`
--

LOCK TABLES `Study` WRITE;
/*!40000 ALTER TABLE `Study` DISABLE KEYS */;
INSERT INTO `Study` VALUES (1,'STU1','Test study',NULL,1,1,'Other','Test Study');
/*!40000 ALTER TABLE `Study` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `StudyType`
--

DROP TABLE IF EXISTS `StudyType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudyType` (
  `typeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`typeId`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `StudyType`
--

LOCK TABLES `StudyType` WRITE;
/*!40000 ALTER TABLE `StudyType` DISABLE KEYS */;
INSERT INTO `StudyType` VALUES (1,'Other'),(12,'RNASeq'),(11,'Population Genomics'),(10,'Cancer Genomics'),(9,'Gene Regulation Study'),(8,'Forensic or Paleo-genomics'),(7,'Synthetic Genomics'),(6,'Epigenetics'),(5,'Resequencing'),(4,'Transcriptome Analysis'),(3,'Metagenomics'),(2,'Whole Genome Sequencing');
/*!40000 ALTER TABLE `StudyType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Study_Experiment`
--

DROP TABLE IF EXISTS `Study_Experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Study_Experiment` (
  `Study_studyId` bigint(20) NOT NULL,
  `experiments_experimentId` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Study_Experiment`
--

LOCK TABLES `Study_Experiment` WRITE;
/*!40000 ALTER TABLE `Study_Experiment` DISABLE KEYS */;
/*!40000 ALTER TABLE `Study_Experiment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Submission`
--

DROP TABLE IF EXISTS `Submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Submission`
--

LOCK TABLES `Submission` WRITE;
/*!40000 ALTER TABLE `Submission` DISABLE KEYS */;
/*!40000 ALTER TABLE `Submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Submission_Chamber`
--

DROP TABLE IF EXISTS `Submission_Chamber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Submission_Chamber` (
  `submission_submissionId` bigint(20) NOT NULL,
  `chambers_chamberId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`chambers_chamberId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Submission_Chamber`
--

LOCK TABLES `Submission_Chamber` WRITE;
/*!40000 ALTER TABLE `Submission_Chamber` DISABLE KEYS */;
/*!40000 ALTER TABLE `Submission_Chamber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Submission_Experiment`
--

DROP TABLE IF EXISTS `Submission_Experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Submission_Experiment` (
  `submission_submissionId` bigint(20) NOT NULL,
  `experiments_experimentId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`experiments_experimentId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Submission_Experiment`
--

LOCK TABLES `Submission_Experiment` WRITE;
/*!40000 ALTER TABLE `Submission_Experiment` DISABLE KEYS */;
/*!40000 ALTER TABLE `Submission_Experiment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Submission_Lane`
--

DROP TABLE IF EXISTS `Submission_Lane`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Submission_Lane` (
  `submission_submissionId` bigint(20) NOT NULL,
  `lanes_laneId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`lanes_laneId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Submission_Lane`
--

LOCK TABLES `Submission_Lane` WRITE;
/*!40000 ALTER TABLE `Submission_Lane` DISABLE KEYS */;
/*!40000 ALTER TABLE `Submission_Lane` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Submission_Partition`
--

DROP TABLE IF EXISTS `Submission_Partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Submission_Partition` (
  `submission_submissionId` bigint(20) NOT NULL,
  `partitions_partitionId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`partitions_partitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Submission_Partition`
--

LOCK TABLES `Submission_Partition` WRITE;
/*!40000 ALTER TABLE `Submission_Partition` DISABLE KEYS */;
/*!40000 ALTER TABLE `Submission_Partition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Submission_Partition_Dilution`
--

DROP TABLE IF EXISTS `Submission_Partition_Dilution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Submission_Partition_Dilution` (
  `submission_submissionId` bigint(20) NOT NULL,
  `partition_partitionId` bigint(20) NOT NULL,
  `dilution_dilutionId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`partition_partitionId`,`dilution_dilutionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Submission_Partition_Dilution`
--

LOCK TABLES `Submission_Partition_Dilution` WRITE;
/*!40000 ALTER TABLE `Submission_Partition_Dilution` DISABLE KEYS */;
/*!40000 ALTER TABLE `Submission_Partition_Dilution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Submission_Sample`
--

DROP TABLE IF EXISTS `Submission_Sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Submission_Sample` (
  `submission_submissionId` bigint(20) NOT NULL,
  `samples_sampleId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`samples_sampleId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Submission_Sample`
--

LOCK TABLES `Submission_Sample` WRITE;
/*!40000 ALTER TABLE `Submission_Sample` DISABLE KEYS */;
/*!40000 ALTER TABLE `Submission_Sample` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Submission_Study`
--

DROP TABLE IF EXISTS `Submission_Study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Submission_Study` (
  `submission_submissionId` bigint(20) NOT NULL,
  `studies_studyId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`studies_studyId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Submission_Study`
--

LOCK TABLES `Submission_Study` WRITE;
/*!40000 ALTER TABLE `Submission_Study` DISABLE KEYS */;
/*!40000 ALTER TABLE `Submission_Study` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TagBarcodes`
--

DROP TABLE IF EXISTS `TagBarcodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TagBarcodes` (
  `tagId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `sequence` varchar(20) NOT NULL,
  `platformName` varchar(20) NOT NULL,
  `strategyName` varchar(100) NOT NULL,
  PRIMARY KEY (`tagId`)
) ENGINE=MyISAM AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TagBarcodes`
--

LOCK TABLES `TagBarcodes` WRITE;
/*!40000 ALTER TABLE `TagBarcodes` DISABLE KEYS */;
INSERT INTO `TagBarcodes` VALUES (1,'Index 12','CTTGTA','Illumina','TruSeq Single Index'),(2,'Index 11','GGCTAC','Illumina','TruSeq Single Index'),(3,'Index 10','TAGCTT','Illumina','TruSeq Single Index'),(4,'Index 9','GATCAG','Illumina','TruSeq Single Index'),(5,'Index 8','ACTTGA','Illumina','TruSeq Single Index'),(6,'Index 7','CAGATC','Illumina','TruSeq Single Index'),(7,'Index 6','GCCAAT','Illumina','TruSeq Single Index'),(8,'Index 5','ACAGTG','Illumina','TruSeq Single Index'),(9,'Index 4','TGACCA','Illumina','TruSeq Single Index'),(10,'Index 3','TTAGGC','Illumina','TruSeq Single Index'),(11,'Index 2','CGATGT','Illumina','TruSeq Single Index'),(12,'Index 1','ATCACG','Illumina','TruSeq Single Index'),(13,'Index 24','GGTAGC','Illumina','TruSeq Single Index'),(14,'Index 23','GAGTGG','Illumina','TruSeq Single Index'),(15,'Index 22','CGTACG','Illumina','TruSeq Single Index'),(16,'Index 21','GTTTCG','Illumina','TruSeq Single Index'),(17,'Index 20','GTGGCC','Illumina','TruSeq Single Index'),(18,'Index 19','GTGAAA','Illumina','TruSeq Single Index'),(19,'Index 18','GTCCGC','Illumina','TruSeq Single Index'),(20,'Index 17','GTAGAG','Illumina','TruSeq Single Index'),(21,'Index 16','CCGTCC','Illumina','TruSeq Single Index'),(22,'Index 15','ATGTCA','Illumina','TruSeq Single Index'),(23,'Index 14','AGTTCC','Illumina','TruSeq Single Index'),(24,'Index 13','AGTCAA','Illumina','TruSeq Single Index'),(25,'Index 48','TCGGCA','Illumina','TruSeq Single Index'),(26,'Index 47','TCGAAG','Illumina','TruSeq Single Index'),(27,'Index 46','TCCCGA','Illumina','TruSeq Single Index'),(28,'Index 45','TCATTC','Illumina','TruSeq Single Index'),(29,'Index 44','TATAAT','Illumina','TruSeq Single Index'),(30,'Index 43','TACAGC','Illumina','TruSeq Single Index'),(31,'Index 42','TAATCG','Illumina','TruSeq Single Index'),(32,'Index 41','GACGAC','Illumina','TruSeq Single Index'),(33,'Index 40','CTCAGA','Illumina','TruSeq Single Index'),(34,'Index 39','CTATAC','Illumina','TruSeq Single Index'),(35,'Index 38','CTAGCT','Illumina','TruSeq Single Index'),(36,'Index 37','CGGAAT','Illumina','TruSeq Single Index'),(37,'Index 36','CCAACA','Illumina','TruSeq Single Index'),(38,'Index 35','CATTTT','Illumina','TruSeq Single Index'),(39,'Index 34','CATGGC','Illumina','TruSeq Single Index'),(40,'Index 33','CAGGCG','Illumina','TruSeq Single Index'),(41,'Index 32','CACTCA','Illumina','TruSeq Single Index'),(42,'Index 31','CACGAT','Illumina','TruSeq Single Index'),(43,'Index 30','CACCGG','Illumina','TruSeq Single Index'),(44,'Index 29','CAACTA','Illumina','TruSeq Single Index'),(45,'Index 28','CAAAAG','Illumina','TruSeq Single Index'),(46,'Index 27','ATTCCT','Illumina','TruSeq Single Index'),(47,'Index 26','ATGAGC','Illumina','TruSeq Single Index'),(48,'Index 25','ACTGAT','Illumina','TruSeq Single Index');
/*!40000 ALTER TABLE `TagBarcodes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
/*!40000 ALTER TABLE `User` DISABLE KEYS */;
INSERT INTO `User` VALUES (1,'','','\0','admin','','admin','ROLE_ADMIN,ROLE_INTERNAL','d033e22ae348aeb5660fc2140aec35850c4da997','admin@admin'),(2,'','\0','\0','test user','','test','ROLE_INTERNAL','a94a8fe5ccb19ba61c4c0873d391e987982fbbd3','test@test');
/*!40000 ALTER TABLE `User` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User_Group`
--

DROP TABLE IF EXISTS `User_Group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `User_Group` (
  `users_userId` bigint(20) NOT NULL,
  `groups_groupId` bigint(20) NOT NULL,
  KEY `FKE7B7ED0B94349B7F` (`groups_groupId`),
  KEY `FKE7B7ED0B749D8197` (`users_userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User_Group`
--

LOCK TABLES `User_Group` WRITE;
/*!40000 ALTER TABLE `User_Group` DISABLE KEYS */;
/*!40000 ALTER TABLE `User_Group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Watcher`
--

DROP TABLE IF EXISTS `Watcher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Watcher` (
  `entityName` varchar(12) NOT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`entityName`,`userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Watcher`
--

LOCK TABLES `Watcher` WRITE;
/*!40000 ALTER TABLE `Watcher` DISABLE KEYS */;
INSERT INTO `Watcher` VALUES ('IPO1',1),('IPO10',1),('IPO2',1),('IPO3',1),('IPO4',1),('IPO5',1),('IPO6',1),('IPO7',1),('IPO8',1),('IPO9',1);
/*!40000 ALTER TABLE `Watcher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Workflow`
--

DROP TABLE IF EXISTS `Workflow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Workflow` (
  `workflowId` bigint(20) NOT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `completion_date` date DEFAULT NULL,
  `workflowDefinition_definitionId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`workflowId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Workflow`
--

LOCK TABLES `Workflow` WRITE;
/*!40000 ALTER TABLE `Workflow` DISABLE KEYS */;
/*!40000 ALTER TABLE `Workflow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorkflowDefinition`
--

DROP TABLE IF EXISTS `WorkflowDefinition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WorkflowDefinition` (
  `workflowDefinitionId` bigint(20) NOT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `creation_date` date DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`workflowDefinitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorkflowDefinition`
--

LOCK TABLES `WorkflowDefinition` WRITE;
/*!40000 ALTER TABLE `WorkflowDefinition` DISABLE KEYS */;
/*!40000 ALTER TABLE `WorkflowDefinition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorkflowDefinition_State`
--

DROP TABLE IF EXISTS `WorkflowDefinition_State`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WorkflowDefinition_State` (
  `workflowDefinitionId` bigint(20) NOT NULL,
  `state_key` varchar(45) DEFAULT NULL,
  `required` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`workflowDefinitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorkflowDefinition_State`
--

LOCK TABLES `WorkflowDefinition_State` WRITE;
/*!40000 ALTER TABLE `WorkflowDefinition_State` DISABLE KEYS */;
/*!40000 ALTER TABLE `WorkflowDefinition_State` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorkflowDefinition_WorkflowProcessDefinition`
--

DROP TABLE IF EXISTS `WorkflowDefinition_WorkflowProcessDefinition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WorkflowDefinition_WorkflowProcessDefinition` (
  `workflowDefinitionId` bigint(20) NOT NULL,
  `workflowProcessDefinitionId` bigint(20) NOT NULL,
  PRIMARY KEY (`workflowDefinitionId`,`workflowProcessDefinitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorkflowDefinition_WorkflowProcessDefinition`
--

LOCK TABLES `WorkflowDefinition_WorkflowProcessDefinition` WRITE;
/*!40000 ALTER TABLE `WorkflowDefinition_WorkflowProcessDefinition` DISABLE KEYS */;
/*!40000 ALTER TABLE `WorkflowDefinition_WorkflowProcessDefinition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorkflowProcess`
--

DROP TABLE IF EXISTS `WorkflowProcess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WorkflowProcess` (
  `processId` bigint(20) NOT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `completion_date` date DEFAULT NULL,
  `workflowProcessDefinition_definitionId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`processId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorkflowProcess`
--

LOCK TABLES `WorkflowProcess` WRITE;
/*!40000 ALTER TABLE `WorkflowProcess` DISABLE KEYS */;
/*!40000 ALTER TABLE `WorkflowProcess` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorkflowProcessDefinition`
--

DROP TABLE IF EXISTS `WorkflowProcessDefinition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WorkflowProcessDefinition` (
  `workflowProcessDefinitionId` bigint(20) NOT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `creation_date` date DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `order` tinyint(4) DEFAULT NULL,
  `inputType` text,
  `outputType` text,
  PRIMARY KEY (`workflowProcessDefinitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorkflowProcessDefinition`
--

LOCK TABLES `WorkflowProcessDefinition` WRITE;
/*!40000 ALTER TABLE `WorkflowProcessDefinition` DISABLE KEYS */;
/*!40000 ALTER TABLE `WorkflowProcessDefinition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorkflowProcessDefinition_State`
--

DROP TABLE IF EXISTS `WorkflowProcessDefinition_State`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WorkflowProcessDefinition_State` (
  `workflowProcessDefinitionId` bigint(20) NOT NULL,
  `state_key` varchar(45) DEFAULT NULL,
  `required` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`workflowProcessDefinitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorkflowProcessDefinition_State`
--

LOCK TABLES `WorkflowProcessDefinition_State` WRITE;
/*!40000 ALTER TABLE `WorkflowProcessDefinition_State` DISABLE KEYS */;
/*!40000 ALTER TABLE `WorkflowProcessDefinition_State` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorkflowProcess_State`
--

DROP TABLE IF EXISTS `WorkflowProcess_State`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WorkflowProcess_State` (
  `processId` bigint(20) NOT NULL,
  `state_key_id` bigint(20) NOT NULL,
  `state_value_id` bigint(20) NOT NULL,
  PRIMARY KEY (`processId`,`state_value_id`,`state_key_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorkflowProcess_State`
--

LOCK TABLES `WorkflowProcess_State` WRITE;
/*!40000 ALTER TABLE `WorkflowProcess_State` DISABLE KEYS */;
/*!40000 ALTER TABLE `WorkflowProcess_State` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Workflow_State`
--

DROP TABLE IF EXISTS `Workflow_State`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Workflow_State` (
  `workflowId` bigint(20) NOT NULL,
  `state_key_id` bigint(20) NOT NULL,
  `state_value_id` bigint(20) NOT NULL,
  PRIMARY KEY (`workflowId`,`state_key_id`,`state_value_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Workflow_State`
--

LOCK TABLES `Workflow_State` WRITE;
/*!40000 ALTER TABLE `Workflow_State` DISABLE KEYS */;
/*!40000 ALTER TABLE `Workflow_State` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Workflow_WorkflowProcess`
--

DROP TABLE IF EXISTS `Workflow_WorkflowProcess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Workflow_WorkflowProcess` (
  `workflowId` bigint(20) NOT NULL,
  `processId` bigint(20) NOT NULL,
  PRIMARY KEY (`workflowId`,`processId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Workflow_WorkflowProcess`
--

LOCK TABLES `Workflow_WorkflowProcess` WRITE;
/*!40000 ALTER TABLE `Workflow_WorkflowProcess` DISABLE KEYS */;
/*!40000 ALTER TABLE `Workflow_WorkflowProcess` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `_Group`
--

DROP TABLE IF EXISTS `_Group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `_Group` (
  `groupId` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`groupId`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `_Group`
--

LOCK TABLES `_Group` WRITE;
/*!40000 ALTER TABLE `_Group` DISABLE KEYS */;
INSERT INTO `_Group` VALUES (2,'Watches for all events on all projects and related overviews','ProjectWatchers'),(1,'Watches for all events on all runs','RunWatchers'),(3,'Watches for all events on all pools','PoolWatchers');
/*!40000 ALTER TABLE `_Group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `_Partition`
--

DROP TABLE IF EXISTS `_Partition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `_Partition` (
  `partitionId` bigint(20) NOT NULL AUTO_INCREMENT,
  `partitionNumber` tinyint(4) NOT NULL,
  `pool_poolId` bigint(20) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`partitionId`)
) ENGINE=MyISAM AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `_Partition`
--

LOCK TABLES `_Partition` WRITE;
/*!40000 ALTER TABLE `_Partition` DISABLE KEYS */;
INSERT INTO `_Partition` VALUES (1,1,7,12),(2,2,7,12),(3,3,7,12),(4,4,7,12),(5,5,8,12),(6,6,8,12),(7,7,8,12),(8,8,8,12),(9,1,5,13),(10,2,5,13),(11,3,5,13),(12,4,5,13),(13,5,6,13),(14,6,6,13),(15,7,6,13),(16,8,6,13),(17,1,9,14),(18,2,9,14),(19,3,9,14),(20,4,9,14),(21,5,10,14),(22,6,10,14),(23,7,10,14),(24,8,10,14),(25,1,1,15),(26,2,2,15),(27,3,3,15),(28,4,3,15),(29,5,4,15),(30,6,4,15),(31,7,4,15),(32,8,4,15);
/*!40000 ALTER TABLE `_Partition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emPCR`
--

DROP TABLE IF EXISTS `emPCR`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emPCR`
--

LOCK TABLES `emPCR` WRITE;
/*!40000 ALTER TABLE `emPCR` DISABLE KEYS */;
/*!40000 ALTER TABLE `emPCR` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emPCRDilution`
--

DROP TABLE IF EXISTS `emPCRDilution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emPCRDilution`
--

LOCK TABLES `emPCRDilution` WRITE;
/*!40000 ALTER TABLE `emPCRDilution` DISABLE KEYS */;
/*!40000 ALTER TABLE `emPCRDilution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `persistent_logins`
--

DROP TABLE IF EXISTS `persistent_logins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persistent_logins`
--

LOCK TABLES `persistent_logins` WRITE;
/*!40000 ALTER TABLE `persistent_logins` DISABLE KEYS */;
/*!40000 ALTER TABLE `persistent_logins` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-08-28 16:01:54
