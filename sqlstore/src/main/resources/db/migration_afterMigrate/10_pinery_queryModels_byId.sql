--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryModelById//
CREATE PROCEDURE queryModelById(
  iModelId BIGINT(20)
) BEGIN
  PREPARE stmt FROM 'SELECT p.platformId
    , p.instrumentModel
    FROM Platform as p
    WHERE p.platformId = ?';
  SET @platformId = iModelId;
  EXECUTE stmt USING @platformId;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest