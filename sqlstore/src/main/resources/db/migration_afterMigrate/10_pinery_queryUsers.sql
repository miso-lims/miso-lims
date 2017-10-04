--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryAllUsers//
CREATE PROCEDURE queryAllUsers() BEGIN
  PREPARE stmt FROM 'SELECT u.userId, u.fullname, u.email, u.active FROM User AS u';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest