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
    INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message) VALUES (
      NEW.containerId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
        CASE WHEN (NEW.platform IS NULL) <> (OLD.platform IS NULL) OR NEW.platform <> OLD.platform THEN 'platform' END), ''),
      NEW.lastModifier,
      log_message
    );
  END IF;
  END//

DROP TRIGGER IF EXISTS SequencerPartitionContainerInsert//
CREATE TRIGGER SequencerPartitionContainerInsert AFTER INSERT ON SequencerPartitionContainer
FOR EACH ROW
  INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message) VALUES (
    NEW.containerId,
    '',
    NEW.lastModifier,
    'Container created.')//

DROP TRIGGER IF EXISTS PartitionChange//
CREATE TRIGGER PartitionChange BEFORE UPDATE ON _Partition
FOR EACH ROW
  BEGIN
    DECLARE log_message varchar(500) CHARACTER SET utf8;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN (NEW.pool_poolId IS NULL) <> (OLD.pool_poolId IS NULL) OR NEW.pool_poolId <> OLD.pool_poolId THEN CONCAT('pool changed in partition ', OLD.partitionNumber, ': ', COALESCE((SELECT name FROM Pool WHERE poolId = OLD.pool_poolId), 'n/a'), ' → ', COALESCE((SELECT name FROM Pool WHERE poolId = NEW.pool_poolId), 'n/a')) END);
    
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message) VALUES (
        (SELECT spcp.container_containerId FROM SequencerPartitionContainer_Partition spcp
         WHERE spcp.partitions_partitionId = OLD.partitionId),
         COALESCE(CONCAT_WS(', ',
           CASE WHEN (NEW.pool_poolId IS NULL) <> (OLD.pool_poolId IS NULL) OR NEW.pool_poolId <> OLD.pool_poolId THEN 'pool' END), ''),
         (SELECT spc.lastModifier FROM SequencerPartitionContainer spc
           JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = spc.containerId
         WHERE spcp.partitions_partitionId = OLD.partitionId),
         log_message
      );
    END IF;

    IF (NEW.pool_poolId IS NOT NULL) AND ((OLD.pool_poolId IS NULL) OR ((OLD.pool_poolId IS NOT NULL) AND (OLD.pool_poolId <> NEW.pool_poolId))) THEN
      INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
        NEW.pool_poolId,
        'container',
        (SELECT spc.lastModifier FROM SequencerPartitionContainer spc
           JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = spc.containerId
         WHERE spcp.partitions_partitionId = OLD.partitionId),
         CONCAT('Added to container ', COALESCE((SELECT spc.identificationBarcode FROM SequencerPartitionContainer spc JOIN SequencerPartitionContainer_Partition sp ON spc.containerId = sp.container_containerId WHERE sp.partitions_partitionId = NEW.partitionId), 'unknown'))
      );
    END IF;
    
    IF (OLD.pool_poolId IS NOT NULL) AND ((NEW.pool_poolId IS NULL) OR ((NEW.pool_poolId IS NOT NULL) AND (OLD.pool_poolId <> NEW.pool_poolId)))  THEN
      INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
        OLD.pool_poolId,
        'container',
        (SELECT spc.lastModifier FROM SequencerPartitionContainer spc
           JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = spc.containerId
         WHERE spcp.partitions_partitionId = OLD.partitionId),
         CONCAT('Removed from container ', COALESCE((SELECT spc.identificationBarcode FROM SequencerPartitionContainer spc JOIN SequencerPartitionContainer_Partition sp ON spc.containerId = sp.container_containerId WHERE sp.partitions_partitionId = NEW.partitionId), 'unknown'))
      );
    END IF;
  END //
DELIMITER ;
-- EndNoTest
