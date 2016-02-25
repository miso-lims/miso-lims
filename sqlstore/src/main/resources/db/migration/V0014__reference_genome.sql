CREATE TABLE `ReferenceGenome` (
  `referenceGenomeId` INT NOT NULL,
  `alias` VARCHAR(45) NULL,
  PRIMARY KEY (`referenceGenomeId`));

INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('1', 'Human hg19 random');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('2', 'Human hg19');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('3', 'Human hg18 random');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('4', 'Human hg18');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('5', 'Mouse mm9');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('6', 'Rat rn4');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('7', 'E. coli DH10B');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('8', 'phiX174');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('9', 'C. elegans ce6');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('10', 'C. pygerythrus');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('11', 'Vaccinia JX-594 (OICR)');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('12', 'P. falciparum');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('13', 'R. sphaeroides');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('14', 'S. aureus');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('15', 'de novo assemby');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES ('16', 'See comments');

ALTER TABLE Project ADD COLUMN `referenceGenomeId` BIGINT (20) DEFAULT -1 after alias;

ALTER TABLE Subproject ADD COLUMN `referenceGenomeId` BIGINT (20) DEFAULT -1 after alias;