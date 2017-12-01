-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS deleteSample//
CREATE PROCEDURE deleteSample(
  iSampleId BIGINT(20),
  iSampleAlias VARCHAR(255)
) BEGIN
  DECLARE errorMessage varchar(300);
  -- rollback if any errors are thrown
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  START TRANSACTION;

  -- check that the sample exists
  IF NOT EXISTS (SELECT 1 FROM Sample WHERE sampleId = iSampleId AND alias = iSampleAlias)
  THEN
    SET errorMessage = CONCAT('Sample with ID ', iSampleId, ' and alias ', iSampleAlias, ' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- confirm that the sample has no sample children
  IF EXISTS (SELECT * FROM DetailedSample WHERE parentId = iSampleId)
  THEN
    SET errorMessage = CONCAT('Cannot delete sample with ID ', iSampleId, ' due to child samples.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- confirm that the sample has no library children
  IF EXISTS (SELECT * FROM Library WHERE sample_sampleId = iSampleId)
  THEN
    SET errorMessage = CONCAT('Cannot delete sample with ID ', iSampleId, ' due to related libraries.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- delete related SampleQCs and notes
  DELETE FROM SampleQC WHERE sample_sampleId = iSampleId;
  DELETE FROM Note WHERE noteId IN (SELECT notes_noteId FROM Sample_Note WHERE sample_sampleId = iSampleId);
  DELETE FROM Sample_Note WHERE sample_sampleId = iSampleId;

  -- delete from sample class/category tables
  DELETE FROM SampleAliquot WHERE sampleId = iSampleId;
  DELETE FROM SampleStock WHERE sampleId = iSampleId;
  DELETE FROM SampleTissueProcessing WHERE sampleId = iSampleId;
  DELETE FROM SampleSlide WHERE sampleId = iSampleId;
  DELETE FROM SampleLCMTube WHERE sampleId = iSampleId;
  DELETE FROM SampleTissue WHERE sampleId = iSampleId;
  DELETE FROM `Identity` WHERE sampleId = iSampleId;
  DELETE FROM SampleChangeLog WHERE sampleId = iSampleId;
  DELETE FROM DetailedSample WHERE sampleId = iSampleId;

  -- delete from Sample table
  DELETE FROM Sample WHERE sampleId = iSampleId;
  SELECT ROW_COUNT() AS number_deleted;

  COMMIT;
END//

DELIMITER ;
-- EndNoTest
