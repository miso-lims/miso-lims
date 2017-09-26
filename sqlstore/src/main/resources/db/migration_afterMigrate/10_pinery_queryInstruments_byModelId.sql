--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryInstrumentsByModelId//
CREATE PROCEDURE queryInstrumentsByModelId(
  iPlatformId BIGINT(20)
) BEGIN
  PREPARE stmt FROM 'SELECT sr.referenceId
    , sr.name
    , sr.platformId
    FROM SequencerReference AS sr
    WHERE sr.platformId = ?';
  SET @platformId = iPlatformId;
  EXECUTE stmt USING @platformId;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest