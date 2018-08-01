-- If you change these method names or signatures, update the docs in `docs/_posts/2017-12-07-admin-guide.md`
-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addIndexFamily//
CREATE PROCEDURE addIndexFamily(
  iName varchar(255),
  iPlatformType varchar(20),
  iArchived tinyint(1),
  iUniqueDualIndex tinyint(1)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM IndexFamily WHERE name = iName)
  THEN
    INSERT INTO IndexFamily(name, platformType, archived, uniqueDualIndex)
    VALUES (iName, UPPER(iPlatformType), iArchived, iUniqueDualIndex);
  END IF;
END//

DROP PROCEDURE IF EXISTS addIndex//
CREATE PROCEDURE addIndex(
  iFamilyName varchar(255),
  iName varchar(10),
  iSequence varchar(24),
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
    INSERT INTO Indices(name, sequence, position, indexFamilyId)
    VALUES (iName, iSequence, iPosition, famId);
  END IF;
  
END//
DELIMITER ;
-- EndNoTest
