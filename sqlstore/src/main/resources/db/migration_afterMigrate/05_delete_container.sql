-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS deleteContainer//
CREATE PROCEDURE deleteContainer(
  iContainerId BIGINT(20),
  iContainerBarcode VARCHAR(255)
) BEGIN
  DECLARE errorMessage VARCHAR(300);
  -- rollback if any errors are thrown
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;
  
  START TRANSACTION;
  
  -- check that the container exists
  IF NOT EXISTS (SELECT 1 FROM SequencerPartitionContainer WHERE containerId = iContainerId AND identificationBarcode = iContainerBarcode)
  THEN
    SET errorMessage = CONCAT('Cannot find container with ID ', iContainerId, ' and barcode "', iContainerBarcode, '"');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  
  -- delete any and all associated items
  DELETE FROM SequencerPartitionContainerChangeLog WHERE containerId = iContainerId;
  DELETE FROM SequencerPartitionContainer_Partition WHERE container_containerId = iContainerId;

  -- delete any orphaned partitions
  DELETE FROM _Partition WHERE NOT EXISTS (SELECT * FROM SequencerPartitionContainer_Partition WHERE partitions_partitionId = partitionId);

  DELETE FROM SequencerPartitionContainer WHERE containerId = iContainerId;
  SELECT ROW_COUNT() AS number_deleted;
  
  COMMIT;
END//

DELIMITER ;
-- EndNoTest
