-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS PartitionQCInsert//
CREATE TRIGGER PartitionQCInsert AFTER INSERT ON Run_Partition_QC
FOR EACH ROW
  INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message) VALUES (
    (SELECT container_containerId FROM SequencerPartitionContainer_Partition WHERE partitions_partitionId = NEW.partitionId),
    'QC',
    (SELECT lastModifier FROM SequencerPartitionContainer JOIN SequencerPartitionContainer_Partition ON SequencerPartitionContainer.containerId = SequencerPartitionContainer_Partition.container_containerId WHERE partitions_partitionId = NEW.partitionId),
    CONCAT('QC for ', (SELECT partitionNumber FROM _Partition WHERE partitionId = NEW.partitionId), ': N/A → ', (SELECT description FROM PartitionQCType WHERE partitionQcTypeId = NEW.partitionQcTypeId))
  )//

DROP TRIGGER IF EXISTS PartitionQCUpdate//
CREATE TRIGGER PartitionQCUpdate BEFORE UPDATE ON Run_Partition_QC
FOR EACH ROW
  BEGIN
      IF NEW.partitionQcTypeId <> OLD.partitionQcTypeId  THEN
        INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message) VALUES (
          (SELECT container_containerId FROM SequencerPartitionContainer_Partition WHERE partitions_partitionId = NEW.partitionId),
          'QC',
          (SELECT lastModifier FROM SequencerPartitionContainer JOIN SequencerPartitionContainer_Partition ON SequencerPartitionContainer.containerId = SequencerPartitionContainer_Partition.container_containerId WHERE partitions_partitionId = NEW.partitionId),
          CONCAT('QC for ', (SELECT partitionNumber FROM _Partition WHERE partitionId = NEW.partitionId), ': ', (SELECT description FROM PartitionQCType WHERE partitionQcTypeId = OLD.partitionQcTypeId), ' → ', (SELECT description FROM PartitionQCType WHERE partitionQcTypeId = NEW.partitionQcTypeId)));
      END IF;
  END//
DELIMITER ;
-- EndNoTest
