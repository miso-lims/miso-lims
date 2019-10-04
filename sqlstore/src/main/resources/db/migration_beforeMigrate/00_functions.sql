-- StartNoTest
DELIMITER //

DROP FUNCTION IF EXISTS getAdminUserId//
CREATE FUNCTION getAdminUserId() RETURNS bigint(20)
BEGIN
  DECLARE adminId bigint(20);
  SELECT userId INTO adminId FROM User WHERE loginName = 'admin';
    IF adminId IS NULL
    THEN
      SIGNAL SQLSTATE '45000' SET message_text = '''admin'' user not found.';
    ELSE
      RETURN adminId;
    END IF;
END//

DROP FUNCTION IF EXISTS decimalToString//
CREATE FUNCTION decimalToString(original DECIMAL(20,10)) RETURNS CHAR(21)
BEGIN
  RETURN CAST(original AS CHAR(21))+0;
END//

DELIMITER ;
-- EndNoTest