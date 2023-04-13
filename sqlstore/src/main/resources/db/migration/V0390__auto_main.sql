-- oxford_nanopore

DROP TABLE IF EXISTS ContainerQC;
DROP TABLE IF EXISTS OxfordNanoporeContainer;
DROP TABLE IF EXISTS FlowCellVersion;
DROP TABLE IF EXISTS PoreVersion;
DROP TABLE IF EXISTS RunOxfordNanopore;

CREATE TABLE RunOxfordNanopore(
  runId bigint NOT NULL AUTO_INCREMENT,
  minKnowVersion varchar(100),
  protocolVersion varchar(100),
  PRIMARY KEY (runId),
  CONSTRAINT FK_OxfordNanopore_Run FOREIGN KEY (runId) REFERENCES Run (runId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE FlowCellVersion(
  flowCellVersionId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(100) NOT NULL,
  PRIMARY KEY (flowCellVersionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE PoreVersion(
  poreVersionId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(100) NOT NULL,
  PRIMARY KEY (poreVersionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE OxfordNanoporeContainer (
  containerId bigint NOT NULL AUTO_INCREMENT,
  flowCellVersionId bigint,
  poreVersionId bigint,
  receivedDate DATE NOT NULL,
  returnedDate DATE NULL,
  PRIMARY KEY(containerId),
  CONSTRAINT FK_OxfordNanoporeContainer_Container FOREIGN KEY (containerId) REFERENCES SequencerPartitionContainer (containerId),
  CONSTRAINT FK_OxfordNanoporeContainer_FlowCellVersion FOREIGN KEY (flowCellVersionId) REFERENCES FlowCellVersion (flowCellVersionId),
  CONSTRAINT FK_OxfordNanoporeContainer_PoreVersion FOREIGN KEY (poreVersionId) REFERENCES PoreVersion (poreVersionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ContainerQC (
  qcId bigint NOT NULL AUTO_INCREMENT,
  containerId bigint NOT NULL,
  creator bigint NOT NULL,
  `date` date NOT NULL,
  `type` bigint DEFAULT NULL,
  results double DEFAULT NULL,
  PRIMARY KEY (qcId),
  CONSTRAINT FK_ContainerQC_Container FOREIGN KEY (containerId) REFERENCES SequencerPartitionContainer (containerId),
  CONSTRAINT FK_ContainerQC_Creator FOREIGN KEY (creator) REFERENCES User (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE Indices MODIFY COLUMN sequence VARCHAR(24);

-- StartNoTest
INSERT INTO QCType(name, description, qcTarget, units, precisionAfterDecimal)
VALUES ('Pore Count', 'Number of pores', 'Container', 'pores', 0);

INSERT INTO Platform(name, instrumentModel, description, numContainers)
SELECT 'OXFORDNANOPORE', 'PromethION', 'Nanopore', 48 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Platform WHERE instrumentModel = 'PromethION');

INSERT INTO Platform(name, instrumentModel, description, numContainers)
SELECT 'OXFORDNANOPORE', 'MinION', 'Nanopore', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Platform WHERE instrumentModel = 'MinION');

INSERT INTO PlatformSizes(platform_platformId, partitionSize)
SELECT platformId, 1 FROM Platform
WHERE name = 'OXFORDNANOPORE'
AND NOT EXISTS (SELECT 1 FROM PlatformSizes WHERE platform_platformId = platformId AND partitionSize = 1);

INSERT INTO FlowCellVersion(alias) VALUES
('FLO-MIN104'),
('FLO-MIN105'),
('FLO-MIN106'),
('FLO-MIN107');

INSERT INTO PoreVersion(alias) VALUES
('R9'),
('Custom R9'),
('R9.4');

SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
INSERT INTO KitDescriptor(name, version, manufacturer, partNumber, kitType, platformType, description, lastModifier) VALUES
('Ligation Sequencing Kit 1D', '1', 'Oxford Nanopore', 'SQK-LSK108', 'LIBRARY', 'OXFORDNANOPORE', 'n/a', @admin),
('1D^2 Sequencing Kit', '1', 'Oxford Nanopore', 'SQK-LSK308', 'LIBRARY', 'OXFORDNANOPORE', 'n/a', @admin),
('Direct RNA Sequencing Kit', '1', 'Oxford Nanopore', 'SQK-RNA001', 'LIBRARY', 'OXFORDNANOPORE', 'n/a', @admin),
('Rapid Sequencing Kit', '1', 'Oxford Nanopore', 'SQK-RAD002', 'LIBRARY', 'OXFORDNANOPORE', 'n/a', @admin);

SET @now = CURRENT_TIMESTAMP();
SELECT platformId INTO @promethionPlatformId FROM Platform WHERE instrumentModel = 'PromethION';
INSERT INTO SequencingParameters(name, platformId, readLength, paired, createdBy, creationDate, updatedBy, lastUpdated, chemistry) VALUES
('Configuration Test Cell', @promethionPlatformId, 0, 0, @admin, @now, @admin, @now, 'UNKNOWN'),
('Platform Quality Control', @promethionPlatformId, 0, 0, @admin, @now, @admin, @now, 'UNKNOWN'),
('Sequencing Run', @promethionPlatformId, 0, 0, @admin, @now, @admin, @now, 'UNKNOWN'),
('Control Experiment', @promethionPlatformId, 0, 0, @admin, @now, @admin, @now, 'UNKNOWN');

SELECT platformId INTO @minionPlatformId FROM Platform WHERE instrumentModel = 'MinION';
INSERT INTO SequencingParameters(name, platformId, readLength, paired, createdBy, creationDate, updatedBy, lastUpdated, chemistry) VALUES
('Configuration Test Cell', @minionPlatformId, 0, 0, @admin, @now, @admin, @now, 'UNKNOWN'),
('Platform Quality Control', @minionPlatformId, 0, 0, @admin, @now, @admin, @now, 'UNKNOWN'),
('Sequencing Run', @minionPlatformId, 0, 0, @admin, @now, @admin, @now, 'UNKNOWN'),
('Control Experiment', @minionPlatformId, 0, 0, @admin, @now, @admin, @now, 'UNKNOWN');

INSERT INTO LibraryType(description, platformType, abbreviation) VALUES
('1D Genomic DNA by ligation', 'OXFORDNANOPORE', 'LIG'),
('1D^2 sequencing of genomic DNA', 'OXFORDNANOPORE', '1D2'),
('Direct RNA sequencing', 'OXFORDNANOPORE', 'RNA'),
('1D Low input genomic DNA with PCR', 'OXFORDNANOPORE', 'LOW'),
('Rapid Sequencing', 'OXFORDNANOPORE', 'RPD'),
('Direct cDNA Sequencing', 'OXFORDNANOPORE', 'CDNA');

INSERT INTO OxfordNanoporeContainer(containerId, receivedDate)
SELECT
  spc.containerId,
  spc.created
FROM SequencerPartitionContainer spc
JOIN Platform p ON p.platformId = spc.platform
WHERE p.name = 'OXFORDNANOPORE';
-- EndNoTest


-- dnase

-- StartNoTest
SELECT qcTypeId INTO @dnaseQc FROM QCType WHERE name = 'DNAse Treated';

UPDATE SampleStock ss
JOIN DetailedSample ds ON ds.sampleId = ss.sampleId
JOIN SampleClass sc ON sc.sampleClassId = ds.sampleClassId
JOIN SampleQC sqc ON sqc.sample_sampleId = ss.sampleId
SET dnaseTreated = 1
WHERE sqc.type = @dnaseQc
AND sqc.results > 0
AND sc.dnaseTreatable = 1;

DELETE FROM SampleQC
WHERE `type` = @dnaseQc
AND sample_sampleId IN (
  SELECT ss.sampleId FROM SampleStock ss
  JOIN DetailedSample ds ON ds.sampleId = ss.sampleId
  JOIN SampleClass sc ON sc.sampleClassId = ds.sampleClassId
  WHERE sc.dnaseTreatable = 1
);
-- EndNoTest

-- DNAse treated should only be set on dnaseTreatable Stocks. These cases have been fixed automatically.
-- Next line will fail and require manual fixing for other (unexpected) cases.
DELETE FROM QCType WHERE name = 'DNAse Treated';


