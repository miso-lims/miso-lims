-- If you change these method names or signatures, update the docs in `docs/_posts/2017-12-07-admin-guide.md`
-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addSequencingParameters//
CREATE PROCEDURE addSequencingParameters(
  iName text,
  iPlatformName varchar(50),
  iInstrumentModel varchar(100),
  iReadLength int(11),
  iReadLength2 int(11),
  iChemistry varchar(255)
) BEGIN
  DECLARE errorMessage varchar(300);
  DECLARE instModelId, createUser bigint(20);
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  
  SELECT instrumentModelId INTO instModelId FROM InstrumentModel WHERE platform = iPlatformName AND alias = iInstrumentModel;
  IF instModelId IS NULL THEN
    SET errorMessage = CONCAT('Instrument Model ''', iInstrumentModel, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM SequencingParameters WHERE name = iName AND instrumentModelId = instModelId) THEN
    SET createUser = getAdminUserId();
    INSERT INTO SequencingParameters(name, instrumentModelId, readLength, readLength2, createdBy, creationDate, updatedBy, lastUpdated, chemistry)
    VALUES (iName, instModelId, iReadLength, iReadLength2, createUser, createTime, createUser, createTime, iChemistry);
  END IF;
END//

DELIMITER ;
-- EndNoTest
