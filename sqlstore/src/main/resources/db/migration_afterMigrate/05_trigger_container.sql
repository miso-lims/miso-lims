-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS SequencerPartitionContainerChange//
CREATE TRIGGER SequencerPartitionContainerChange BEFORE UPDATE ON SequencerPartitionContainer
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
        CASE WHEN (NEW.platform IS NULL) <> (OLD.platform IS NULL) OR NEW.platform <> OLD.platform THEN CONCAT('platform: ', COALESCE(OLD.platform, 'n/a'), ' → ', COALESCE(NEW.platform, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.containerId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
        CASE WHEN (NEW.platform IS NULL) <> (OLD.platform IS NULL) OR NEW.platform <> OLD.platform THEN 'platform' END), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified
    );
  END IF;
  END//

DROP TRIGGER IF EXISTS SequencerPartitionContainerInsert//
CREATE TRIGGER SequencerPartitionContainerInsert AFTER INSERT ON SequencerPartitionContainer
FOR EACH ROW
  INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.containerId,
    '',
    NEW.lastModifier,
    'Container created.',
    NEW.lastModified)//

DROP TRIGGER IF EXISTS PartitionChange//
CREATE TRIGGER PartitionChange BEFORE UPDATE ON _Partition
FOR EACH ROW
  BEGIN
    DECLARE log_message varchar(500) CHARACTER SET utf8;
    DECLARE last_modifier bigint(20);
    DECLARE last_modified TIMESTAMP;
    DECLARE old_pool_name, new_pool_name, new_container_serial varchar(255) CHARACTER SET utf8;
    
    SET old_pool_name = COALESCE((SELECT name FROM Pool WHERE poolId = OLD.pool_poolId), 'n/a');
    SET new_pool_name = COALESCE((SELECT name FROM Pool WHERE poolId = NEW.pool_poolId), 'n/a');
    SET log_message = CONCAT_WS(', ',
      CASE WHEN (NEW.pool_poolId IS NULL) <> (OLD.pool_poolId IS NULL) OR NEW.pool_poolId <> OLD.pool_poolId
        THEN CONCAT('pool changed in partition ', OLD.partitionNumber, ': ', old_pool_name, ' → ', new_pool_name)
      END
    );
    SET new_container_serial = COALESCE(
      (SELECT spc.identificationBarcode FROM SequencerPartitionContainer spc
        JOIN SequencerPartitionContainer_Partition sp ON spc.containerId = sp.container_containerId
        WHERE sp.partitions_partitionId = NEW.partitionId),
      'unknown');
    SELECT spc.lastModifier INTO last_modifier FROM SequencerPartitionContainer spc
      JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = spc.containerId
      WHERE spcp.partitions_partitionId = OLD.partitionId;
    SELECT spc.lastModified INTO last_modified FROM SequencerPartitionContainer spc
      JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = spc.containerId
      WHERE spcp.partitions_partitionId = OLD.partitionId; 
    
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message, changeTime) VALUES (
        (SELECT spcp.container_containerId FROM SequencerPartitionContainer_Partition spcp
         WHERE spcp.partitions_partitionId = OLD.partitionId),
         'pool',
         last_modifier,
         log_message,
         last_modified
      );
      
      INSERT INTO RunChangeLog(runId, columnsChanged, userId, message, changeTime)
        SELECT rspc.run_runId,
          'pool',
          last_modifier,
          CONCAT('pool changed in partition ', OLD.partitionNumber, ' of container ', spc.identificationBarcode, ': ', old_pool_name, ' → ', new_pool_name),
          last_modified
        FROM Run_SequencerPartitionContainer rspc
        JOIN SequencerPartitionContainer spc ON spc.containerId = rspc.containers_containerId
        JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = spc.containerId
        WHERE spcp.partitions_partitionId = OLD.partitionId;
    END IF;

    IF (NEW.pool_poolId IS NOT NULL) AND ((OLD.pool_poolId IS NULL) OR ((OLD.pool_poolId IS NOT NULL) AND (OLD.pool_poolId <> NEW.pool_poolId))) THEN
      INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message, changeTime) VALUES (
        NEW.pool_poolId,
        'container',
        last_modifier,
        CONCAT('Added to container ', new_container_serial),
        last_modified
      );
    END IF;
    
    IF (OLD.pool_poolId IS NOT NULL) AND ((NEW.pool_poolId IS NULL) OR ((NEW.pool_poolId IS NOT NULL) AND (OLD.pool_poolId <> NEW.pool_poolId)))  THEN
      INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message, changeTime) VALUES (
        OLD.pool_poolId,
        'container',
        last_modifier,
        CONCAT('Removed from container ', new_container_serial),
        last_modified
      );
    END IF;
  END //
DELIMITER ;
-- EndNoTest
