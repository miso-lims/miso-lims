CREATE TABLE `LibraryQC2` (
  `qcId` bigint(20) NOT NULL AUTO_INCREMENT,
  `library_libraryId` bigint(20) NOT NULL,
  `creator` bigint(20) NOT NULL,
  `date` date NOT NULL,
  `type` bigint(20) NOT NULL,
  `results` double NOT NULL,
  PRIMARY KEY (`qcId`),
  CONSTRAINT `FK_library_qc_library` FOREIGN KEY (`library_libraryId`) REFERENCES `Library` (`libraryId`),
  CONSTRAINT `FK_library_qc_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_library_qc_type` FOREIGN KEY (`type`) REFERENCES `QCType` (`qcTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleQC2` (
  `qcId` bigint(20) NOT NULL AUTO_INCREMENT,
  `sample_sampleId` bigint(20) NOT NULL,
  `creator` bigint(20) NOT NULL,
  `date` date NOT NULL,
  `type` bigint(20) NOT NULL,
  `results` double NOT NULL,
  PRIMARY KEY (`qcId`),
  CONSTRAINT `FK_sample_qc_sample` FOREIGN KEY (`sample_sampleId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `FK_sample_qc_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_sample_qc_type` FOREIGN KEY (`type`) REFERENCES `QCType` (`qcTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `PoolQC2` (
  `qcId` bigint(20) NOT NULL AUTO_INCREMENT,
  `pool_poolId` bigint(20) NOT NULL,
  `creator` bigint(20) NOT NULL,
  `date` date NOT NULL,
  `type` bigint(20) NOT NULL,
  `results` double NOT NULL,
  PRIMARY KEY (`qcId`),
  CONSTRAINT `FK_pool_qc_pool` FOREIGN KEY (`pool_poolId`) REFERENCES `Pool` (`poolId`),
  CONSTRAINT `FK_pool_qc_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_pool_qc_type` FOREIGN KEY (`type`) REFERENCES `QCType` (`qcTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO LibraryQC2(`qcId`, `library_libraryId`, `creator`, `date`, `type`, `results`)
	SELECT qcId, library_libraryId, (SELECT userId FROM `User` WHERE fullName = qcCreator), qcDate, qcMethod, results FROM LibraryQC;
INSERT INTO SampleQC2(`qcId`, `sample_sampleId`, `creator`, `date`, `type`, `results`)
	SELECT qcId, sample_sampleId, (SELECT userId FROM `User` WHERE fullName = qcCreator), qcDate, qcMethod, results FROM SampleQC;
INSERT INTO PoolQC2(`qcId`, `pool_poolId`, `creator`, `date`, `type`, `results`)
	SELECT qcId, pool_poolId, (SELECT userId FROM `User` WHERE fullName = qcCreator), qcDate, qcMethod, results FROM PoolQC;

DROP TABLE LibraryQC;
DROP TABLE SampleQC;
DROP TABLE PoolQC;

ALTER TABLE LibraryQC2 RENAME TO LibraryQC;
ALTER TABLE SampleQC2 RENAME TO SampleQC;
ALTER TABLE PoolQC2 RENAME TO PoolQC;

UPDATE QCType SET precisionAfterDecimal = 2 WHERE precisionAfterDecimal = 0;
