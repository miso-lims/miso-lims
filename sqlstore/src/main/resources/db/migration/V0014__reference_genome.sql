CREATE TABLE `ReferenceGenome` (
  `referenceGenomeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (`referenceGenomeId`));

INSERT INTO `ReferenceGenome` ( `referenceGenomeId`, `alias`) VALUES (0,'Unknown');

ALTER TABLE Project ADD COLUMN `referenceGenomeId` BIGINT (20) NOT NULL DEFAULT 0 after alias;

ALTER TABLE Subproject ADD COLUMN `referenceGenomeId` BIGINT (20) NOT NULL DEFAULT 0 after alias;

ALTER TABLE Project ADD FOREIGN KEY (referenceGenomeId) REFERENCES ReferenceGenome (referenceGenomeId);

ALTER TABLE Subproject ADD FOREIGN KEY (referenceGenomeId) REFERENCES ReferenceGenome (referenceGenomeId);