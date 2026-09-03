DELIMITER //

DROP PROCEDURE IF EXISTS enforce_mysql_version//
CREATE PROCEDURE enforce_mysql_version()
BEGIN
  IF VERSION() NOT LIKE '9.7%' THEN
    SET @message = CONCAT('Error: MySQL version 9.7 is required. ', VERSION(), ' detected.');
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = @message;
  END IF;
END//

DELIMITER ;

CALL enforce_mysql_version();

ALTER DATABASE CHARACTER SET utf8mb4 COLLATE 'utf8mb4_0900_ai_ci';
-- Recursion used in updateSampleHierarchy procedure
SET GLOBAL max_sp_recursion_depth=255;
SET max_sp_recursion_depth=255;
-- Disable "Trigger does not exist" and other note-level warnings
SET sql_notes = 0;