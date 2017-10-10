--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryUserById//
CREATE PROCEDURE queryUserById(
  iUserId BIGINT(20)
) BEGIN
  PREPARE stmt FROM 'SELECT u.userId, u.fullname, u.email, u.active FROM User AS u WHERE u.userId = ?';
  SET @userId = iUserId;
  EXECUTE stmt USING @userId;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest