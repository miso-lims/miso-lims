-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS RunPartitionUpdate//
CREATE TRIGGER RunPartitionUpdate BEFORE UPDATE ON Run_Partition
FOR EACH ROW
  BEGIN
	DECLARE log_message varchar(500) CHARACTER SET utf8;
	DECLARE log_columns varchar(500) CHARACTER SET utf8;
    SELECT
      identificationBarcode,
      container_containerId,
      partitionNumber
    INTO @container, @containerId, @partitionNumber
    FROM SequencerPartitionContainer
      JOIN SequencerPartitionContainer_Partition
        ON SequencerPartitionContainer.containerId = SequencerPartitionContainer_Partition.container_containerId
      JOIN _Partition
        ON SequencerPartitionContainer_Partition.partitions_partitionId = _Partition.partitionId
    WHERE partitions_partitionId = NEW.partitionId;
    
    SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.partitionQcTypeId IS NULL) <> (OLD.partitionQcTypeId IS NULL) OR NEW.partitionQcTypeId <> OLD.partitionQcTypeId THEN CONCAT('QC for ', @container, '-', @partitionNumber, ': ', COALESCE((SELECT description FROM PartitionQCType WHERE partitionQcTypeId = OLD.partitionQcTypeId), 'n/a'), ' → ', COALESCE((SELECT description FROM PartitionQCType WHERE partitionQcTypeId = NEW.partitionQcTypeId), 'n/a')) END,
        CASE WHEN NEW.purposeId <> OLD.purposeId THEN CONCAT('Purpose for ', @container, '-', @partitionNumber, ': ', (SELECT alias FROM RunPurpose WHERE purposeId = OLD.purposeId), ' → ', (SELECT alias FROM RunPurpose WHERE purposeId = NEW.purposeId)) END);
    
    IF log_message IS NOT NULL AND log_message <> '' THEN
      SET log_columns = CONCAT_WS(', ',
        CASE WHEN (NEW.partitionQcTypeId IS NULL) <> (OLD.partitionQcTypeId IS NULL) OR NEW.partitionQcTypeId <> OLD.partitionQcTypeId THEN 'partition QC' END,
        CASE WHEN NEW.purposeId <> OLD.purposeId THEN 'partition purpose' END);

      INSERT INTO RunChangeLog (runId, columnsChanged, userId, message)
      VALUES (NEW.runId, log_columns, NEW.lastModifier, log_message);
    END IF;
  END//

DROP TRIGGER IF EXISTS RunPartitionLibraryAliquotInsert//
CREATE TRIGGER RunPartitionLibraryAliquotInsert AFTER INSERT ON Run_Partition_LibraryAliquot
FOR EACH ROW
  BEGIN
	SELECT identificationBarcode, partitionNumber
	INTO @container, @partitionNumber
	FROM SequencerPartitionContainer
      JOIN SequencerPartitionContainer_Partition
        ON SequencerPartitionContainer.containerId = SequencerPartitionContainer_Partition.container_containerId
      JOIN _Partition
        ON SequencerPartitionContainer_Partition.partitions_partitionId = _Partition.partitionId
    WHERE partitions_partitionId = NEW.partitionId;
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message) VALUES (
      NEW.runId,
      'aliquot purpose',
      NEW.lastModifier,
      CONCAT('Purpose for ', @container, '-', @partitionNumber, '-', (SELECT alias FROM LibraryAliquot WHERE aliquotId = NEW.aliquotId), ': n/a → ', (SELECT alias FROM RunPurpose WHERE purposeId = NEW.purposeId))
    );
  END//

DROP TRIGGER IF EXISTS RunPartitionLibraryAliquotUpdate//
CREATE TRIGGER RunPartitionLibraryAliquotUpdate BEFORE UPDATE ON Run_Partition_LibraryAliquot
FOR EACH ROW
  BEGIN
	DECLARE log_message varchar(500) CHARACTER SET utf8;
	DECLARE log_columns varchar(500) CHARACTER SET utf8;
    SELECT
      identificationBarcode,
      container_containerId,
      partitionNumber
    INTO @container, @containerId, @partitionNumber
    FROM SequencerPartitionContainer
      JOIN SequencerPartitionContainer_Partition
        ON SequencerPartitionContainer.containerId = SequencerPartitionContainer_Partition.container_containerId
      JOIN _Partition
        ON SequencerPartitionContainer_Partition.partitions_partitionId = _Partition.partitionId
    WHERE partitions_partitionId = NEW.partitionId;
    
    SET log_message = CONCAT_WS(', ',
        CASE WHEN NEW.purposeId <> OLD.purposeId THEN CONCAT('Purpose for ', @container, '-', @partitionNumber, '-', (SELECT alias FROM LibraryAliquot WHERE aliquotId = NEW.aliquotId), ': ', (SELECT alias FROM RunPurpose WHERE purposeId = OLD.purposeId), ' → ', (SELECT alias FROM RunPurpose WHERE purposeId = NEW.purposeId)) END);
    
    IF log_message IS NOT NULL AND log_message <> '' THEN
      SET log_columns = CONCAT_WS(', ',
        CASE WHEN NEW.purposeId <> OLD.purposeId THEN 'aliquot purpose' END);

      INSERT INTO RunChangeLog (runId, columnsChanged, userId, message)
      VALUES (NEW.runId, log_columns, NEW.lastModifier, log_message);
    END IF;
  END//

DELIMITER ;
-- EndNoTest
