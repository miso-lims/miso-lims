--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryAllRunPositions//
CREATE PROCEDURE queryAllRunPositions() BEGIN
  PREPARE stmt FROM 'SELECT p.partitionId
    , p.partitionNumber
    , r_spc.Run_runId
    FROM _Partition AS p
    JOIN SequencerPartitionContainer_Partition AS spc_p ON spc_p.partitions_partitionId = p.partitionId
    JOIN Run_SequencerPartitionContainer AS r_spc ON r_spc.containers_containerId = spc_p.container_containerId';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest