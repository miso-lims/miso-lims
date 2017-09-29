-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addSamplePurpose//
CREATE PROCEDURE addSamplePurpose(
  iAlias varchar(255)
) BEGIN
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  DECLARE createUser bigint(20);
  
  IF NOT EXISTS (SELECT 1 FROM SamplePurpose WHERE alias = iAlias) THEN
    SET createUser = getAdminUserId();
    INSERT INTO SamplePurpose(alias, createdBy, creationDate, updatedBy, lastUpdated)
    VALUES (iAlias, createUser, createTime, createUser, createTime);
  END IF;
END//
DELIMITER ;
-- EndNoTest
