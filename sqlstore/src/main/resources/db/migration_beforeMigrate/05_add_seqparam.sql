-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addSequencingParameters//
CREATE PROCEDURE addSequencingParameters(
  iName text,
  iPlatformName varchar(50),
  iPlatformModel varchar(100),
  iReadLength int(11),
  iPaired tinyint(1),
  iChemistry varchar(255)
) BEGIN
  DECLARE errorMessage varchar(300);
  DECLARE platId, createUser bigint(20);
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  
  SELECT platformId INTO platId FROM Platform WHERE name = iPlatformName AND instrumentModel = iPlatformModel;
  IF platId IS NULL THEN
    SET errorMessage = CONCAT('Platform ''', iPlatformModel, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM SequencingParameters WHERE name = iName AND platformId = platId) THEN
    SET createUser = getAdminUserId();
    INSERT INTO SequencingParameters(name, platformId, readLength, paired, createdBy, creationDate, updatedBy, lastUpdated, chemistry)
    VALUES (iName, platId, iReadLength, iPaired, createUser, createTime, createUser, createTime, iChemistry);
  END IF;
END//

DELIMITER ;
-- EndNoTest
