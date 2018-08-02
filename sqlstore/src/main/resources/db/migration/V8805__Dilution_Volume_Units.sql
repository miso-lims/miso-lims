ALTER TABLE `Sample` MODIFY COLUMN `concentrationUnits` varchar(30) DEFAULT NULL;
ALTER TABLE `Sample` MODIFY COLUMN `volumeUnits` varchar(30) DEFAULT NULL;

ALTER TABLE `Library` MODIFY COLUMN `concentrationUnits` varchar(30) DEFAULT NULL;
ALTER TABLE `Library` MODIFY COLUMN `volumeUnits` varchar(30) DEFAULT NULL;

ALTER TABLE `LibraryDilution` ADD COLUMN `volumeUnits` varchar(30) DEFAULT NULL;
ALTER TABLE `LibraryDilution` ADD COLUMN `concentrationUnits2` varchar(30) DEFAULT NULL;
UPDATE `LibraryDilution` SET `concentrationUnits2` = `concentrationUnits`;
ALTER TABLE `LibraryDilution` DROP COLUMN `concentrationUnits`;
ALTER TABLE `LibraryDilution` CHANGE COLUMN `concentrationUnits2` `concentrationUnits` varchar(30) DEFAULT NULL;

ALTER TABLE `Pool` MODIFY COLUMN `concentrationUnits` varchar(30) DEFAULT NULL;
ALTER TABLE `Pool` MODIFY COLUMN `volumeUnits` varchar(30) DEFAULT NULL;

-- StartNoTest

UPDATE `Sample` SET `volumeUnits` = 'MICROLITRE' WHERE `volumeUnits` IS NOT NULL;
UPDATE `Library` SET `volumeUnits` = 'MICROLITRE' WHERE `volumeUnits` IS NOT NULL;
UPDATE `LibraryDilution` SET `volumeUnits` = 'MICROLITRE';
UPDATE `Pool` SET `volumeUnits` = 'MICROLITRE' WHERE `volumeUnits` IS NOT NULL;

UPDATE `Sample` SET `concentrationUnits` = IF(`concentrationUnits` LIKE 'ng/&#181;L','NANOGRAMS_PER_MICROLITRE', IF(`concentrationUnits` LIKE 'nM', 'NANOMOLAR', NULL));
UPDATE `Library` SET `concentrationUnits` = IF(`concentrationUnits` LIKE 'ng/&#181;L','NANOGRAMS_PER_MICROLITRE', IF(`concentrationUnits` LIKE 'nM', 'NANOMOLAR', NULL));
UPDATE `LibraryDilution` SET `concentrationUnits` = IF(`concentrationUnits` LIKE 'ng/&#181;L','NANOGRAMS_PER_MICROLITRE', IF(`concentrationUnits` LIKE 'nM', 'NANOMOLAR', NULL));
UPDATE `Pool` SET `concentrationUnits` = IF(`concentrationUnits` LIKE 'ng/&#181;L','NANOGRAMS_PER_MICROLITRE', IF(`concentrationUnits` LIKE 'nM', 'NANOMOLAR', NULL));

-- EndNoTest


