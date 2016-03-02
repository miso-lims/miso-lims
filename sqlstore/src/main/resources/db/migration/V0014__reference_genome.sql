CREATE TABLE `ReferenceGenome` (
  `referenceGenomeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (`referenceGenomeId`));

INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'Human hg19 random');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'Human hg19');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'Human hg18 random');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'Human hg18');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'Mouse mm9');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'Rat rn4');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'E. coli DH10B');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'phiX174');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'C. elegans ce6');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'C. pygerythrus');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'Vaccinia JX-594 (OICR)');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'P. falciparum');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'R. sphaeroides');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'S. aureus');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'de novo assemby');
INSERT INTO `ReferenceGenome` (`alias`) VALUES ( 'See comments');

ALTER TABLE Project ADD COLUMN `referenceGenomeId` BIGINT (20) NOT NULL after alias;

ALTER TABLE Subproject ADD COLUMN `referenceGenomeId` BIGINT (20) NOT NULL after alias;

ALTER TABLE Project ADD FOREIGN KEY (referenceGenomeId) REFERENCES ReferenceGenome (referenceGenomeId);

ALTER TABLE Subproject ADD FOREIGN KEY (referenceGenomeId) REFERENCES ReferenceGenome (referenceGenomeId);