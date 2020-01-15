-- Disable "trigger doesn't exist" warnings
SET sql_notes = 0;
DROP TRIGGER IF EXISTS PartitionQCInsert;
DROP TRIGGER IF EXISTS PartitionQCUpdate;
SET sql_notes = 1;

RENAME TABLE OrderPurpose TO RunPurpose;

ALTER TABLE Instrument ADD COLUMN defaultPurposeId bigint(20);
ALTER TABLE Instrument ADD CONSTRAINT instrument_defaultPurpose FOREIGN KEY (defaultPurposeId) REFERENCES RunPurpose (purposeId);
UPDATE Instrument inst
JOIN InstrumentModel im ON im.instrumentModelId = inst.instrumentModelId
SET defaultPurposeId = (SELECT purposeId FROM RunPurpose WHERE alias = 'Production')
WHERE im.instrumentType = 'SEQUENCER';

RENAME TABLE Run_Partition_QC TO Run_Partition;

ALTER TABLE Run_Partition MODIFY COLUMN partitionQcTypeId bigint(20);
ALTER TABLE Run_Partition ADD COLUMN purposeId bigint(20);
ALTER TABLE Run_Partition ADD CONSTRAINT runPartition_purpose FOREIGN KEY (purposeId) REFERENCES RunPurpose (purposeId);
ALTER TABLE Run_Partition MODIFY COLUMN purposeId bigint(20) NOT NULL;
ALTER TABLE Run_Partition ADD COLUMN lastModifier bigint(20);
UPDATE Run_Partition SET lastModifier = (SELECT userId FROM User WHERE loginName = 'admin');
ALTER TABLE Run_Partition MODIFY COLUMN lastModifier bigint(20) NOT NULL;
ALTER TABLE Run_Partition ADD CONSTRAINT runPartition_lastModifier FOREIGN KEY (lastModifier) REFERENCES User (userId);

INSERT INTO Run_Partition (runId, partitionId)
SELECT rspc.Run_runId, spcp.partitions_partitionId
FROM Run_SequencerPartitionContainer rspc
JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = rspc.containers_containerId
WHERE NOT EXISTS (
  SELECT 1 FROM Run_Partition
  WHERE runId = rspc.Run_runId AND partitionId = spcp.partitions_partitionId
);

UPDATE Run_Partition
SET purposeId = (SELECT purposeId FROM RunPurpose WHERE alias = 'Production');

CREATE TABLE Run_Partition_LibraryAliquot (
  runId bigint(20) NOT NULL,
  partitionId bigint(20) NOT NULL,
  aliquotId bigint(20) NOT NULL,
  purposeId bigint(20),
  lastModifier bigint(20) NOT NULL,
  PRIMARY KEY (runId, partitionId, aliquotId),
  CONSTRAINT runAliquot_run FOREIGN KEY (runId) REFERENCES Run (runId),
  CONSTRAINT runAliquot_partition FOREIGN KEY (partitionId) REFERENCES _Partition (partitionId),
  CONSTRAINT runAliquot_aliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId),
  CONSTRAINT runAliquot_purpose FOREIGN KEY (purposeId) REFERENCES RunPurpose (purposeId),
  CONSTRAINT runAliquot_lastModifier FOREIGN KEY (lastModifier) REFERENCES User (userId)
) Engine=InnoDB DEFAULT CHARSET=utf8;
