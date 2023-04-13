CREATE TABLE `ReferenceGenome` (
  `referenceGenomeId` bigint NOT NULL AUTO_INCREMENT,
  `alias` VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (`referenceGenomeId`));

INSERT INTO `ReferenceGenome` ( `referenceGenomeId`, `alias`) VALUES (1,'Unknown');

ALTER TABLE Project ADD COLUMN `referenceGenomeId` BIGINT NOT NULL DEFAULT 1 after alias;

ALTER TABLE Subproject ADD COLUMN `referenceGenomeId` BIGINT NOT NULL DEFAULT 1 after alias;

ALTER TABLE Project ADD FOREIGN KEY (referenceGenomeId) REFERENCES ReferenceGenome (referenceGenomeId);

ALTER TABLE Subproject ADD FOREIGN KEY (referenceGenomeId) REFERENCES ReferenceGenome (referenceGenomeId);
