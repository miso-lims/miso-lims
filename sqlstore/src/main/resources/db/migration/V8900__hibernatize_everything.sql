UPDATE KitDescriptor SET kitType = UPPER(kitType), platformType = UPPER(platformType);

UPDATE Platform SET name = UPPER(name);

ALTER TABLE RunQC_Partition ADD COLUMN partition_partitionId BIGINT(20);
-- changes here
UPDATE RunQC_Partition rqp SET partition_partitionId = (
  SELECT p.partitionId FROM `_Partition` p 
  JOIN SequencerPartitionContainer_Partition spcp ON spcp.partitions_partitionId = p.partitionId
  WHERE spcp.container_containerId = rqp.containers_containerId
);
ALTER TABLE RunQC_Partition CHANGE COLUMN partition_partitionId partition_partitionId BIGINT(20) NOT NULL;
ALTER TABLE RunQC_Partition ADD FOREIGN KEY (partition_partitionId) REFERENCES `_Partition` (partitionId);
ALTER TABLE RunQC_Partition ADD FOREIGN KEY (runQc_runQcId) REFERENCES `RunQC` (qcId);
ALTER TABLE RunQC_Partition DROP PRIMARY KEY;
ALTER TABLE RunQC_Partition ADD PRIMARY KEY(`runQc_runQcId`, `partitionId`);
ALTER TABLE RunQC_Partition DROP COLUMN partitionNumber;
ALTER TABLE RunQC_Partition DROP COLUMN containers_containerId;
