DROP TABLE IF EXISTS SequencingContainerModel_Platform;
DROP TABLE IF EXISTS SequencingContainerModel;

CREATE TABLE SequencingContainerModel (
  sequencingContainerModelId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(255) NOT NULL,
  identificationBarcode varchar(255),
  partitionCount int NOT NULL,
  platformType varchar(255) NOT NULL,
  fallback tinyint(1) NOT NULL DEFAULT 0,
  archived tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (sequencingContainerModelId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SequencingContainerModel_Platform (
  sequencingContainerModelId bigint(20) NOT NULL,
  platformId bigint(20) NOT NULL,
  PRIMARY KEY (sequencingContainerModelId, platformId),
  CONSTRAINT fk_SequencingContainerModel_Platform_model FOREIGN KEY (sequencingContainerModelId) REFERENCES SequencingContainerModel (sequencingContainerModelId),
  CONSTRAINT fk_SequencingContainerModel_Platform_platform FOREIGN KEY (platformId) REFERENCES Platform (platformId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- StartNoTest
DROP PROCEDURE IF EXISTS tempMakeModels;

DELIMITER //
CREATE PROCEDURE tempMakeModels() BEGIN
  INSERT INTO SequencingContainerModel (alias, partitionCount, platformType, fallback)
  SELECT DISTINCT CONCAT('Generic ', ps.partitionSize, '-',
    CASE
      WHEN p.name = 'PACBIO' THEN 'SMRT Cell'
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

ALTER TABLE SequencerPartitionContainer ADD COLUMN sequencingContainerModelId bigint(20);

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

ALTER TABLE SequencerPartitionContainer MODIFY COLUMN sequencingContainerModelId bigint(20) NOT NULL;
ALTER TABLE SequencerPartitionContainer ADD CONSTRAINT fk_SequencerPartitionContainer_model
  FOREIGN KEY (sequencingContainerModelId) REFERENCES SequencingContainerModel (sequencingContainerModelId);

ALTER TABLE SequencerPartitionContainer DROP COLUMN platform;
DROP TABLE PlatformSizes;
