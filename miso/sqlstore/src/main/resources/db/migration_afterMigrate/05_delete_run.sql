-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS deleteRun//
CREATE PROCEDURE deleteRun(
  iRunId BIGINT(20),
  iRunAlias VARCHAR(255)
) BEGIN
  DECLARE errorMessage VARCHAR(300);
  -- rollback if any errors are thrown
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;
  
  START TRANSACTION;
  
  -- check that the run exists
  IF NOT EXISTS (SELECT 1 FROM Run WHERE runId = iRunId AND alias = iRunAlias)
  THEN
    SET errorMessage = CONCAT('Cannot find run with ID ', iRunId, ' and alias "', iRunAlias, '"');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  
  SET @runId = iRunId;
  
  -- delete any and all associated items
  DELETE Note, Run_Note FROM Run_Note
  JOIN Note 
  WHERE Note.noteId = Run_Note.notes_noteId AND Run_Note.run_runId = @runId;
  
  DELETE FROM RunChangeLog WHERE runId = @runId;
  DELETE FROM Run_SequencerPartitionContainer WHERE Run_runId = @runId;
  
  -- delete from platform-specific tables
  DELETE FROM RunIllumina WHERE runId = @runId;
  DELETE FROM RunIonTorrent WHERE runId = @runId;
  DELETE FROM RunLS454 WHERE runId = @runId;
  DELETE FROM RunPacBio WHERE runId = @runId;
  DELETE FROM RunSolid WHERE runId = @runId;
  
  DELETE FROM Run WHERE runId = @runId;
  SELECT ROW_COUNT() AS number_deleted;
  
  COMMIT;
END//

DELIMITER ;
-- EndNoTest
