CREATE TABLE PlatformSizes (
  platform_platformId bigint(20) NOT NULL,
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
  partitionQcTypeId bigint(20) NOT NULL AUTO_INCREMENT,
  description varchar(255) NOT NULL,
  noteRequired boolean DEFAULT false,
  PRIMARY KEY (partitionQcTypeId),
  UNIQUE KEY uk_partitionqctype_description (description)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

CREATE TABLE Run_Partition_QC (
  runId bigint(20) NOT NULL,
  partitionId bigint(20) NOT NULL,
  partitionQcTypeId bigint(20) NOT NULL,
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
