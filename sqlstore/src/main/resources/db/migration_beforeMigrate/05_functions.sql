DELIMITER //

DROP FUNCTION IF EXISTS getAdminUserId//
CREATE FUNCTION getAdminUserId() RETURNS bigint
READS SQL DATA
BEGIN
  DECLARE adminId bigint;
  SELECT userId INTO adminId FROM User WHERE loginName = 'admin';
    IF adminId IS NULL
    THEN
      SIGNAL SQLSTATE '45000' SET message_text = '''admin'' user not found.';
    ELSE
      RETURN adminId;
    END IF;
END//

DROP FUNCTION IF EXISTS decimalToString//
CREATE FUNCTION decimalToString(original DECIMAL(20,10)) RETURNS CHAR(21) DETERMINISTIC
BEGIN
  RETURN CAST(original AS CHAR(21))+0;
END//

DROP FUNCTION IF EXISTS booleanToString//
CREATE FUNCTION booleanToString(original BOOLEAN) RETURNS CHAR(21) DETERMINISTIC
BEGIN
  RETURN CASE original
    WHEN TRUE THEN 'yes'
    WHEN FALSE THEN 'no'
    ELSE 'n/a'
  END;
END//

DROP FUNCTION IF EXISTS qcPassedToString//
CREATE FUNCTION qcPassedToString(original BOOLEAN) RETURNS CHAR(21) DETERMINISTIC NO SQL
BEGIN
  RETURN CASE original
    WHEN TRUE THEN 'Ready'
    WHEN FALSE THEN 'Failed'
    ELSE 'Not Ready'
  END;
END//

DROP FUNCTION IF EXISTS dataReviewToString//
CREATE FUNCTION dataReviewToString(original BOOLEAN) RETURNS CHAR(21) DETERMINISTIC NO SQL
BEGIN
  RETURN CASE original
    WHEN TRUE THEN 'Pass'
    WHEN FALSE THEN 'Fail'
    ELSE 'n/a'
  END;
END//

DROP FUNCTION IF EXISTS boxSizeToString//
CREATE FUNCTION boxSizeToString(id bigint) RETURNS varchar(50)
  NOT DETERMINISTIC READS SQL DATA
BEGIN
  DECLARE label varchar(50);
  SELECT CONCAT(boxSizeRows, '×', boxSizeColumns, ' ', LOWER(boxType)) INTO label
  FROM BoxSize WHERE boxSizeId = id;
  RETURN label;
END//

DROP FUNCTION IF EXISTS isChanged//
CREATE FUNCTION isChanged(val1 varchar(255), val2 varchar(255)) RETURNS BOOLEAN
DETERMINISTIC NO SQL
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
CREATE FUNCTION makeChangeMessage(fieldName varchar(255), beforeVal varchar(255), afterVal varchar(255))
  RETURNS longtext DETERMINISTIC NO SQL
BEGIN
  IF isChanged(beforeVal, afterVal) THEN
    RETURN CONCAT(fieldName, ': ', COALESCE(beforeVal, 'n/a'), ' → ', COALESCE(afterVal, 'n/a'));
  ELSE
    RETURN NULL;
  END IF;
END//

DROP FUNCTION IF EXISTS makeChangeColumn//
CREATE FUNCTION makeChangeColumn(fieldName varchar(255), beforeVal varchar(255), afterVal varchar(255))
  RETURNS varchar(255) DETERMINISTIC NO SQL
BEGIN
  IF isChanged(beforeVal, afterVal) THEN
    RETURN fieldName;
  ELSE
    RETURN NULL;
  END IF;
END//

DELIMITER ;
