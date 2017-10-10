--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryAllModels//
CREATE PROCEDURE queryAllModels() BEGIN
  PREPARE stmt FROM 'SELECT p.platformId
    , p.instrumentModel
    FROM Platform as p';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest