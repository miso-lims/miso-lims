-- sequencing_container_models

DROP TABLE IF EXISTS SequencingContainerModel_Platform;
DROP TABLE IF EXISTS SequencingContainerModel;

CREATE TABLE SequencingContainerModel (
  sequencingContainerModelId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(255) NOT NULL,
  identificationBarcode varchar(255),
  partitionCount int NOT NULL,
  platformType varchar(255) NOT NULL,
  fallback tinyint NOT NULL DEFAULT 0,
  archived tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (sequencingContainerModelId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SequencingContainerModel_Platform (
  sequencingContainerModelId bigint NOT NULL,
  platformId bigint NOT NULL,
  PRIMARY KEY (sequencingContainerModelId, platformId),
  CONSTRAINT fk_SequencingContainerModel_Platform_model FOREIGN KEY (sequencingContainerModelId) REFERENCES SequencingContainerModel (sequencingContainerModelId),
  CONSTRAINT fk_SequencingContainerModel_Platform_platform FOREIGN KEY (platformId) REFERENCES Platform (platformId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- StartNoTest
DROP PROCEDURE IF EXISTS tempMakeModels;

DELIMITER //
CREATE PROCEDURE tempMakeModels() BEGIN
  INSERT INTO SequencingContainerModel (alias, partitionCount, platformType, fallback)
  SELECT DISTINCT CONCAT('Unknown ', ps.partitionSize, '-',
    CASE
      WHEN p.name = 'PACBIO' THEN 'SMRT-Cell'
      ELSE 'Lane'
    END,
    ' ',
    CASE
      WHEN p.name = 'PACBIO' THEN 'PacBio 8Pac'
      WHEN p.name = 'LS454' THEN 'LS454 Plate'
      WHEN p.name = 'SOLID' THEN 'Solid Slide'
      WHEN p.name = 'IONTORRENT' THEN 'Ion Torrent Chip'
      WHEN p.name = 'OXFORDNANOPORE' THEN 'Oxford Nanopore Flow Cell'
      WHEN p.name = 'ILLUMINA' THEN 'Illumina Flow Cell'
      ELSE CONCAT(p.name, ' Container')
    END),
    ps.partitionSize,
    p.name,
    1
  FROM PlatformSizes ps
  JOIN Platform p ON p.platformId = ps.platform_platformId;
  
  INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
  SELECT m.sequencingContainerModelId, ps.platform_platformId
  FROM PlatformSizes ps
  JOIN Platform p ON p.platformId = ps.platform_platformId
  JOIN SequencingContainerModel m ON m.partitionCount = ps.partitionSize AND m.platformType = p.name;
END// 
DELIMITER ;

CALL tempMakeModels();
DROP PROCEDURE tempMakeModels;
-- EndNoTest

ALTER TABLE SequencerPartitionContainer ADD COLUMN sequencingContainerModelId bigint;

-- StartNoTest
UPDATE SequencerPartitionContainer spc
JOIN (
  SELECT container_containerId, COUNT(*) AS partitionCount
  FROM SequencerPartitionContainer_Partition
  GROUP BY container_containerId
) AS pc ON pc.container_containerId = spc.containerId
SET spc.sequencingContainerModelId = (
  SELECT m.sequencingContainerModelId FROM SequencingContainerModel m
  JOIN SequencingContainerModel_Platform mp ON mp.sequencingContainerModelId = m.sequencingContainerModelId
  WHERE mp.platformId = spc.platform
  AND m.partitionCount = pc.partitionCount
);
-- EndNoTest

ALTER TABLE SequencerPartitionContainer MODIFY COLUMN sequencingContainerModelId bigint NOT NULL;
ALTER TABLE SequencerPartitionContainer ADD CONSTRAINT fk_SequencerPartitionContainer_model
  FOREIGN KEY (sequencingContainerModelId) REFERENCES SequencingContainerModel (sequencingContainerModelId);

ALTER TABLE SequencerPartitionContainer DROP COLUMN platform;
DROP TABLE PlatformSizes;

-- StartNoTest
INSERT INTO SequencingContainerModel(alias, identificationBarcode, partitionCount, platformType) VALUES
('S2 Flow Cell', '20015845', 2, 'ILLUMINA'),
('S4 Flow Cell', '20015843', 4, 'ILLUMINA'),
('HiSeq PE Flow Cell v4', '15049346', 8, 'ILLUMINA'),
('HiSeq SR Flow Cell v4', '15052255', 8, 'ILLUMINA'),
('HiSeq PE Flow Cell v3', '15022186', 8, 'ILLUMINA'),
('HiSeq SR Flow Cell v3', NULL, 8, 'ILLUMINA'),
('HiSeq Rapid PE Flow Cell v2', '15053059', 2, 'ILLUMINA'),
('HiSeq Rapid SR Flow Cell v2', '15053060', 2, 'ILLUMINA'),
('HiSeq Rapid PE Flow Cell', '15034173', 2, 'ILLUMINA'),
('HiSeq Rapid SR Flow Cell', '15034244', 2, 'ILLUMINA'),
('PE MiSeq Flow Cell', '15028382', 1, 'ILLUMINA'),
('PE-Micro MiSeq Flow Cell', '15035218', 1, 'ILLUMINA'),
('PE-Nano MiSeq Flow Cell', '15035217', 1, 'ILLUMINA'),
('High Output Flow Cell Cartridge V2', '15065973', 1, 'ILLUMINA'),
('Mid Output Flow Cell Cartridge V2', '15065974', 1, 'ILLUMINA');

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'S2 Flow Cell' AND p.instrumentModel = 'Illumina NovaSeq 6000';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'S4 Flow Cell' AND p.instrumentModel = 'Illumina NovaSeq 6000';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'HiSeq PE Flow Cell v4' AND p.instrumentModel = 'Illumina HiSeq 2500';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'HiSeq SR Flow Cell v4' AND p.instrumentModel = 'Illumina HiSeq 2500';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'HiSeq PE Flow Cell v3' AND p.instrumentModel = 'Illumina HiSeq 2500';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'HiSeq SR Flow Cell v3' AND p.instrumentModel = 'Illumina HiSeq 2500';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'HiSeq Rapid PE Flow Cell v2' AND p.instrumentModel = 'Illumina HiSeq 2500';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'HiSeq Rapid SR Flow Cell v2' AND p.instrumentModel = 'Illumina HiSeq 2500';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'HiSeq Rapid PE Flow Cell' AND p.instrumentModel = 'Illumina HiSeq 2500';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'HiSeq Rapid SR Flow Cell' AND p.instrumentModel = 'Illumina HiSeq 2500';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'PE MiSeq Flow Cell' AND p.instrumentModel = 'Illumina MiSeq';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'PE-Micro MiSeq Flow Cell' AND p.instrumentModel = 'Illumina MiSeq';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'PE-Nano MiSeq Flow Cell' AND p.instrumentModel = 'Illumina MiSeq';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'High Output Flow Cell Cartridge V2' AND p.instrumentModel LIKE '%NextSeq 500';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'Mid Output Flow Cell Cartridge V2' AND p.instrumentModel LIKE '%NextSeq 500';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'High Output Flow Cell Cartridge V2' AND p.instrumentModel LIKE '%NextSeq 550';

INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
SELECT m.sequencingContainerModelId, p.platformId FROM SequencingContainerModel m JOIN Platform p
WHERE m.alias = 'Mid Output Flow Cell Cartridge V2' AND p.instrumentModel LIKE '%NextSeq 550';
-- EndNoTest


