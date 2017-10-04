--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryAllInstruments//
CREATE PROCEDURE queryAllInstruments() BEGIN
  PREPARE stmt FROM 'SELECT sr.referenceId
    , sr.name
    , sr.platformId
    FROM SequencerReference AS sr';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest