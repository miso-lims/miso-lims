--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryRunById//
CREATE PROCEDURE queryRunById(
  iRunId BIGINT(20)
) BEGIN
  PREPARE stmt FROM 'SELECT DISTINCT r.alias, r.sequencerReference_sequencerReferenceId AS instrumentId
    , r.runId
    , r.filePath
    , r.health
    , r.startDate
    , r.completionDate
    , spc.identificationBarcode
    , createLog.userId
    , createLog.changeTime
    , updateLog.userId
    , updateLog.changeTime
    , sp.paired paired
    , sp.readLength read_length
    FROM Run AS r
    LEFT JOIN SequencingParameters AS sp ON sp.parametersId = r.sequencingParameters_parametersId
    LEFT JOIN Run_SequencerPartitionContainer AS rscp ON rscp.Run_runId = r.runId
    LEFT JOIN SequencerPartitionContainer AS spc ON spc.containerId = rscp.containers_containerId
    LEFT JOIN RunChangeLog AS createLog ON createLog.runId = r.runId
    LEFT JOIN RunChangeLog AS rcl1 ON rcl1.runId = createLog.runId AND createLog.changeTime > rcl1.changeTime
    LEFT JOIN RunChangeLog AS updateLog ON updateLog.runId = r.runId
    LEFT JOIN RunChangeLog AS rcl2 ON rcl2.runId = updateLog.runId AND updateLog.changeTime < rcl2.changeTime
    WHERE rcl1.runId IS NULL
      AND rcl2.runId IS NULL
      AND r.runId = ?';
  SET @runId = iRunId;
  EXECUTE stmt USING @runId;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest