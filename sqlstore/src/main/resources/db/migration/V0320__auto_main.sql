-- ext_inst_id_to_secondary_id

ALTER TABLE SampleTissue CHANGE COLUMN externalInstituteIdentifier secondaryIdentifier VARCHAR(255) DEFAULT NULL;


-- qcs

CREATE TABLE `LibraryQC2` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `library_libraryId` bigint NOT NULL,
  `creator` bigint NOT NULL,
  `date` date NOT NULL,
  `type` bigint NOT NULL,
  `results` double NOT NULL,
  PRIMARY KEY (`qcId`),
  CONSTRAINT `FK_library_qc_library` FOREIGN KEY (`library_libraryId`) REFERENCES `Library` (`libraryId`),
  CONSTRAINT `FK_library_qc_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_library_qc_type` FOREIGN KEY (`type`) REFERENCES `QCType` (`qcTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleQC2` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `sample_sampleId` bigint NOT NULL,
  `creator` bigint NOT NULL,
  `date` date NOT NULL,
  `type` bigint NOT NULL,
  `results` double NOT NULL,
  PRIMARY KEY (`qcId`),
  CONSTRAINT `FK_sample_qc_sample` FOREIGN KEY (`sample_sampleId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `FK_sample_qc_creator` FOREIGN KEY (`creator`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_sample_qc_type` FOREIGN KEY (`type`) REFERENCES `QCType` (`qcTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `PoolQC2` (
  `qcId` bigint NOT NULL AUTO_INCREMENT,
  `pool_poolId` bigint NOT NULL,
  `creator` bigint NOT NULL,
  `date` date NOT NULL,
  `type` bigint NOT NULL,
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


-- printer_rename

UPDATE Printer SET
  backend = 'BRADY_FTP',
  configuration = JSON_OBJECT('host', JSON_EXTRACT(configuration, '$.host'),
  'pin', JSON_EXTRACT(configuration, '$.password'))
WHERE backend = 'FTP';

UPDATE Printer SET driver = 'BRADY_BPT_635_488' WHERE backend = 'BRADY_M80';
UPDATE Printer SET driver = 'BRADY_THT_181_492_3' WHERE backend = 'BRADY_STANDARD';


-- paritions_for_platform

CREATE TABLE PlatformSizes (
  platform_platformId bigint NOT NULL,
  partitionSize int NOT NULL,
  PRIMARY KEY (platform_platformId, partitionSize),
  CONSTRAINT fk_platform_size_platform FOREIGN KEY (platform_platformId) REFERENCES Platform (platformId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO PlatformSizes(platform_platformId, partitionSize)
  SELECT DISTINCT platform, COUNT(*) AS c FROM SequencerPartitionContainer JOIN SequencerPartitionContainer_Partition ON containerId = container_containerId GROUP BY containerId
  UNION SELECT platformId, 1 FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%MiSeq%'
  UNION SELECT platformId, 2 FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%HiSeq%'
  UNION SELECT platformId, 8 FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%HiSeq%'
  UNION SELECT platformId, 4 FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%NextSeq%'
  UNION SELECT platformId, 4 FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%Genome Analyzer%'
  UNION SELECT platformId, 1 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 2 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 3 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 4 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 5 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 6 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 7 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 8 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 9 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 10 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 11 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 12 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 13 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 14 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 15 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 16 FROM Platform WHERE name = 'PACBIO'
  UNION SELECT platformId, 1 FROM Platform WHERE name = 'LS454'
  UNION SELECT platformId, 2 FROM Platform WHERE name = 'LS454'
  UNION SELECT platformId, 4 FROM Platform WHERE name = 'LS454'
  UNION SELECT platformId, 8 FROM Platform WHERE name = 'LS454'
  UNION SELECT platformId, 16 FROM Platform WHERE name = 'LS454'
  UNION SELECT platformId, 6 FROM Platform WHERE name = 'SOLID' AND instrumentModel = 'AB SOLiD 5500xl'
  UNION SELECT platformId, 1 FROM Platform WHERE name = 'SOLID' AND instrumentModel <> 'AB SOLiD 5500xl'
  UNION SELECT platformId, 2 FROM Platform WHERE name = 'SOLID' AND instrumentModel <> 'AB SOLiD 5500xl'
  UNION SELECT platformId, 4 FROM Platform WHERE name = 'SOLID' AND instrumentModel <> 'AB SOLiD 5500xl'
  UNION SELECT platformId, 8 FROM Platform WHERE name = 'SOLID' AND instrumentModel <> 'AB SOLiD 5500xl'
  UNION SELECT platformId, 16 FROM Platform WHERE name = 'SOLID' AND instrumentModel <> 'AB SOLiD 5500xl'
;

CREATE TABLE PartitionQCType (
  partitionQcTypeId bigint NOT NULL AUTO_INCREMENT,
  description varchar(255) NOT NULL,
  noteRequired boolean DEFAULT false,
  PRIMARY KEY (partitionQcTypeId),
  UNIQUE KEY uk_partitionqctype_description (description)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

CREATE TABLE Run_Partition_QC (
  runId bigint NOT NULL,
  partitionId bigint NOT NULL,
  partitionQcTypeId bigint NOT NULL,
  notes varchar(1024),
  PRIMARY KEY(runId, partitionId),
  CONSTRAINT fk_rpq_run_runId FOREIGN KEY (runId) REFERENCES Run (runId),
  CONSTRAINT fk_rpq_partition_partitionId FOREIGN KEY (partitionId) REFERENCES _Partition (partitionId),
  CONSTRAINT fk_rpq_partitiontypeqc_partitiontypeqc FOREIGN KEY (partitionQcTypeId) REFERENCES PartitionQCType (partitionQcTypeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO PartitionQCType(description, noteRequired) VALUES
  ('OK', false),
  ('OK\'d by collaborator', false),
  ('Failed: Instrument problem', false),
  ('Failed: Library preparation problem', false),
  ('Failed: Analysis problem', false),
  ('Failed: Other problem', true);

INSERT INTO PartitionQCType(description, noteRequired)
	SELECT CONCAT('Failed: ', name), true FROM QCType WHERE qcTarget = 'Run';

INSERT INTO Run_Partition_QC(runId, partitionId, partitionQcTypeId, notes)
  SELECT RunQC.run_runId, RunQC_Partition.partition_partitionId, partitionQcTypeId, information
  FROM RunQC
    JOIN RunQC_Partition ON RunQC.qcId = RunQC_Partition.runQc_runQcId
    JOIN QCType ON RunQC.qcMethod = QCType.qcTypeId
    JOIN PartitionQCType ON PartitionQCType.description = CONCAT('Failed :', QCType.name);

DROP TABLE RunQC_Partition;
DROP TABLE RunQC;
DELETE FROM QCType WHERE qcTarget = 'Run';


