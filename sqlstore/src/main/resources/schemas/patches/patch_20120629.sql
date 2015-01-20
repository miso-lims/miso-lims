USE lims;

ALTER TABLE `lims`.`TagBarcodes` ADD COLUMN `strategyName` VARCHAR(100) NOT NULL;

ALTER TABLE `lims`.`QCType` CHANGE `units` `units` VARCHAR(20) NOT NULL;
INSERT INTO `lims`.`QCType` VALUES(7, "qPCR", "Quantitative PCR", "Library", "mol/&#181;l");