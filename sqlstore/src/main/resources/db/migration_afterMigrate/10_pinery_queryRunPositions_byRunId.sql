--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryRunPositionsByRunId//
CREATE PROCEDURE queryRunPositionsByRunId(
  iRunId BIGINT(20)
) BEGIN
  PREPARE stmt FROM 'SELECT p.partitionId
    , p.partitionNumber
    , r_spc.Run_runId
    FROM _Partition AS p
    JOIN SequencerPartitionContainer_Partition AS spc_p ON spc_p.partitions_partitionId = p.partitionId
    JOIN Run_SequencerPartitionContainer AS r_spc ON r_spc.containers_containerId = spc_p.container_containerId
    WHERE r_spc.Run_runId = ?';
  SET @runId = iRunId;
  EXECUTE stmt USING @runId;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest