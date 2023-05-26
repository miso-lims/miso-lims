-- order_container_model
ALTER TABLE SequencingOrder ADD COLUMN sequencingContainerModelId bigint;
ALTER TABLE SequencingOrder ADD CONSTRAINT fk_sequencingOrder_sequencingContainerModel
FOREIGN KEY (sequencingContainerModelId) REFERENCES SequencingContainerModel(sequencingContainerModelId);

ALTER TABLE PoolOrder ADD COLUMN sequencingContainerModelId bigint;
ALTER TABLE PoolOrder ADD CONSTRAINT fk_poolOrder_sequencingContainerModel
FOREIGN KEY (sequencingContainerModelId) REFERENCES SequencingContainerModel(sequencingContainerModelId);

DROP VIEW IF EXISTS CompletedPartitions;

ALTER TABLE _Partition ADD COLUMN containerId bigint;
UPDATE _Partition SET containerId = (
  SELECT container_containerId FROM SequencerPartitionContainer_Partition
  WHERE partitions_partitionId = partitionId
);
ALTER TABLE _Partition MODIFY COLUMN containerId bigint NOT NULL;
ALTER TABLE _Partition ADD CONSTRAINT fk_partition_container FOREIGN KEY (containerId) REFERENCES SequencerPartitionContainer(containerId);

DROP TABLE SequencerPartitionContainer_Partition;

DROP VIEW IF EXISTS RunPartitionsByHealth;
DROP VIEW IF EXISTS DesiredPartitions;
DROP VIEW IF EXISTS SequencingOrderCompletion_Backing;
DROP VIEW IF EXISTS SequencingOrderCompletion;
DROP VIEW IF EXISTS SequencingOrderCompletion_Items;

DELETE FROM SequencingOrder WHERE partitions < 1;

