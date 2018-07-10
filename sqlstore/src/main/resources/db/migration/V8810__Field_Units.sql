ALTER TABLE `Library` ADD COLUMN `concentrationUnits` varchar(20) DEFAULT 'nM' NOT NULL;
ALTER TABLE `Library` ADD COLUMN `volumeUnits` varchar(20) DEFAULT '&#181;l' NOT NULL;

ALTER TABLE `Sample` ADD COLUMN `concentrationUnits` varchar(20) DEFAULT 'ng/&#181;L' NOT NULL;

ALTER TABLE `Sample` ADD COLUMN `volumeUnits` varchar(20) DEFAULT '&#181;l' NOT NULL;

ALTER TABLE `Pool` ADD COLUMN `concentrationUnits` varchar(20) DEFAULT 'nM' NOT NULL;
ALTER TABLE `Pool` ADD COLUMN `volumeUnits` varchar(20) DEFAULT '&#181;l' NOT NULL;

ALTER TABLE `Sample` ADD COLUMN `concentration` double DEFAULT NULL;

UPDATE `Sample` SET `concentration` = (SELECT `concentration` from `DetailedSample` WHERE `sampleId` = Sample.sampleId); 
