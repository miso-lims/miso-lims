-- MySQL dump 10.13  Distrib 5.1.62, for debian-linux-gnu (i486)
--
-- Host: localhost    Database: conan
-- ------------------------------------------------------
-- Server version	5.1.62-0ubuntu0.10.04.1

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
-- Table structure for table `CONAN_PARAMETERS`
--

DROP TABLE IF EXISTS `CONAN_PARAMETERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CONAN_PARAMETERS` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PARAMETER_NAME` varchar(200) NOT NULL,
  `PARAMETER_VALUE` text NOT NULL,
  `TASK_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CONAN_PROCESSES`
--

DROP TABLE IF EXISTS `CONAN_PROCESSES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CONAN_PROCESSES` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(200) NOT NULL,
  `START_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `END_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `USER_ID` bigint(20) NOT NULL,
  `TASK_ID` bigint(20) NOT NULL,
  `EXIT_CODE` int(6) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CONAN_TASKS`
--

DROP TABLE IF EXISTS `CONAN_TASKS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CONAN_TASKS` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(200) DEFAULT NULL,
  `START_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `END_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `USER_ID` bigint(20) NOT NULL,
  `PIPELINE_NAME` varchar(200) NOT NULL,
  `PRIORITY` varchar(200) DEFAULT NULL,
  `FIRST_PROCESS_INDEX` bigint(20) NOT NULL,
  `STATE` varchar(200) DEFAULT NULL,
  `STATUS_MESSAGE` text,
  `CURRENT_EXECUTED_INDEX` bigint(20) NOT NULL,
  `CREATION_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CONAN_USERS`
--

DROP TABLE IF EXISTS `CONAN_USERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CONAN_USERS` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `FIRST_NAME` varchar(200) DEFAULT NULL,
  `LAST_NAME` varchar(200) NOT NULL,
  `EMAIL` varchar(200) NOT NULL,
  `RESTAPIKEY` varchar(200) DEFAULT NULL,
  `USER_NAME` varchar(200) NOT NULL,
  `PERMISSIONS` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-06-01 14:10:16
