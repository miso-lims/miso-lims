--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryInstrumentById//
CREATE PROCEDURE queryInstrumentById(
  iInstrumentId BIGINT(20)
) BEGIN
  PREPARE stmt FROM 'SELECT sr.referenceId
    , sr.name
    , sr.platformId
    FROM SequencerReference AS sr
    WHERE sr.referenceId = ?';
  SET @referenceId = iInstrumentId;
  EXECUTE stmt USING @referenceId;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest
