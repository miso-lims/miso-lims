-- StartNoTest
-- Disable "Function does not exist" warnings
SET sql_notes = 0;
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

DROP FUNCTION IF EXISTS booleanToString//
CREATE FUNCTION booleanToString(original BOOLEAN) RETURNS CHAR(21)
BEGIN
  RETURN CASE original
    WHEN TRUE THEN 'yes'
    WHEN FALSE THEN 'no'
    ELSE 'n/a'
  END;
END//

DROP FUNCTION IF EXISTS isChanged//
CREATE FUNCTION isChanged(val1 varchar(255), val2 varchar(255)) RETURNS BOOLEAN
BEGIN
  IF (val1 IS NULL) <> (val2 IS NULL) THEN
    RETURN TRUE;
  ELSEIF val1 IS NULL AND val2 IS NULL THEN
    RETURN FALSE;
  ELSE
    RETURN val1 <> val2;
  END IF;
END//

DROP FUNCTION IF EXISTS makeChangeMessage//
CREATE FUNCTION makeChangeMessage(fieldName varchar(255), beforeVal varchar(255), afterVal varchar(255)) RETURNS longtext
BEGIN
  IF isChanged(beforeVal, afterVal) THEN
    RETURN CONCAT(fieldName, ': ', COALESCE(beforeVal, 'n/a'), ' → ', COALESCE(afterVal, 'n/a'));
  ELSE
    RETURN NULL;
  END IF;
END//

DROP FUNCTION IF EXISTS makeChangeColumn//
CREATE FUNCTION makeChangeColumn(fieldName varchar(255), beforeVal varchar(255), afterVal varchar(255)) RETURNS varchar(255)
BEGIN
  IF isChanged(beforeVal, afterVal) THEN
    RETURN fieldName;
  ELSE
    RETURN NULL;
  END IF;
END//

DELIMITER ;
SET sql_notes = 1;
-- EndNoTest
