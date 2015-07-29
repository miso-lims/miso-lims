
#ALTER TABLE `Pool` ADD COLUMN `qcPassed` VARCHAR(5) DEFAULT NULL;

#CREATE TABLE `PoolQC` (
#  `qcId` bigint(20) NOT NULL auto_increment,
#  `pool_poolId` bigint(20) NOT NULL,
#  `qcUserName` varchar(255) NOT NULL,
#  `qcDate` date NOT NULL,
#  `qcMethod` bigint(20) default NULL,
#  `results` double default NULL,
#  PRIMARY KEY  (`qcId`)
#) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

ALTER TABLE `Library` CHANGE `qcPassed` `qcPassed` VARCHAR(5) DEFAULT NULL;
UPDATE `Library` SET qcPassed="false" WHERE qcPassed="0";
UPDATE `Library` SET qcPassed="true" WHERE qcPassed="1";
