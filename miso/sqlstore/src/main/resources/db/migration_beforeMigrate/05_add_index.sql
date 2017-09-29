-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addIndexFamily//
CREATE PROCEDURE addIndexFamily(
  iName varchar(255),
  iPlatformType varchar(20),
  iArchived tinyint(1)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM IndexFamily WHERE name = iName)
  THEN
    INSERT INTO IndexFamily(name, platformType, archived)
    VALUES (iName, UPPER(iPlatformType), iArchived);
  END IF;
END//

DROP PROCEDURE IF EXISTS addIndex//
CREATE PROCEDURE addIndex(
  iFamilyName varchar(255),
  iName varchar(10),
  iSequence varchar(20),
  iPosition int(11)
) BEGIN
  DECLARE famId bigint(20);
  DECLARE errorMessage varchar(300);
  SELECT indexFamilyId INTO famId FROM IndexFamily WHERE name = iFamilyName;
  IF famId IS NULL
  THEN
    SET errorMessage = CONCAT('IndexFamily ''', iFamilyName, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  ELSE
    IF NOT EXISTS (SELECT 1 FROM Indices WHERE indexFamilyId = famId AND sequence = iSequence AND position = iPosition)
    THEN
      INSERT INTO Indices(name, sequence, position, indexFamilyId)
      VALUES (iName, iSequence, iPosition, famId);
    END IF;
  END IF;
  
END//
DELIMITER ;
-- EndNoTest
