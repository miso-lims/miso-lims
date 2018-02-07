-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS PartitionQCInsert//
CREATE TRIGGER PartitionQCInsert
  AFTER INSERT
  ON Run_Partition_QC
  FOR EACH ROW
  BEGIN
    SELECT
      container_containerId,
      lastModifier,
      partitionNumber
    INTO @containerId, @userId, @partitionNumber
    FROM SequencerPartitionContainer
      JOIN SequencerPartitionContainer_Partition
        ON SequencerPartitionContainer.containerId = SequencerPartitionContainer_Partition.container_containerId
      JOIN _Partition
        ON SequencerPartitionContainer_Partition.partitions_partitionId = _Partition.partitionId
    WHERE partitions_partitionId = NEW.partitionId;

    SET @runId = NEW.runId;
    SET @message = CONCAT('QC for ',
                          @partitionNumber,
                          ': N/A → ',
                          (SELECT description
                           FROM PartitionQCType
                           WHERE partitionQcTypeId = NEW.partitionQcTypeId));

    INSERT INTO SequencerPartitionContainerChangeLog (containerId, columnsChanged, userId, message)
    VALUES (@containerId, 'QC', @userId, @message);

    INSERT INTO RunChangeLog (runId, columnsChanged, userId, message)
    VALUES (@runId, 'QC', @userId, @message);
  END //

DROP TRIGGER IF EXISTS PartitionQCUpdate//
CREATE TRIGGER PartitionQCUpdate
  BEFORE UPDATE
  ON Run_Partition_QC
  FOR EACH ROW
  BEGIN
    SELECT
      container_containerId,
      lastModifier,
      partitionNumber
    INTO @containerId, @userId, @partitionNumber
    FROM SequencerPartitionContainer
      JOIN SequencerPartitionContainer_Partition
        ON SequencerPartitionContainer.containerId = SequencerPartitionContainer_Partition.container_containerId
      JOIN _Partition
        ON SequencerPartitionContainer_Partition.partitions_partitionId = _Partition.partitionId
    WHERE partitions_partitionId = NEW.partitionId;

    SET @runId = NEW.runId;
    SET @message = CONCAT('QC for ',
                          @partitionNumber,
                          ': ',
                          (SELECT description
                           FROM PartitionQCType
                           WHERE partitionQcTypeId = OLD.partitionQcTypeId),
                          ' → ',
                          (SELECT description
                           FROM PartitionQCType
                           WHERE partitionQcTypeId = NEW.partitionQcTypeId));

    IF NEW.partitionQcTypeId <> OLD.partitionQcTypeId
    THEN
      INSERT INTO SequencerPartitionContainerChangeLog (containerId, columnsChanged, userId, message)
      VALUES (@containerId, 'QC', @userId, @message);

      INSERT INTO RunChangeLog (runId, columnsChanged, userId, message)
      VALUES (@runId, 'QC', @userId, @message);
    END IF;
  END//
DELIMITER ;
-- EndNoTest
