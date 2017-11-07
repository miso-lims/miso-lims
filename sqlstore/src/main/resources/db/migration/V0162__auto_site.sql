-- svrs_for_TGL
-- StartNoTest
--StartNoTest
DELIMITER //
DROP PROCEDURE IF EXISTS insert_svr_if_not_exists//
CREATE PROCEDURE insert_svr_if_not_exists(svrParentId BIGINT(20), svrChildId BIGINT(20))
BEGIN
  SET @time = NOW();
  SELECT userId INTO @user FROM User WHERE loginName = 'admin';

  IF NOT EXISTS (SELECT 1 FROM SampleValidRelationship WHERE parentId = svrParentId AND childId = svrChildId) THEN
    INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
      VALUES (svrParentId, svrChildId, @user, @time, @user, @time, 0);
  END IF;
END //
DELIMITER ;

SELECT sampleClassId INTO @curls FROM SampleClass WHERE alias = 'Curls';
SELECT sampleClassId INTO @cvSlide FROM SampleClass WHERE alias = 'CV Slide';
SELECT sampleClassId INTO @dnaStock FROM SampleClass WHERE alias = 'gDNA (stock)';
SELECT sampleClassId INTO @rnaStock FROM SampleClass WHERE alias = 'whole RNA (stock)';

CALL insert_svr_if_not_exists(@curls, @dnaStock);
CALL insert_svr_if_not_exists(@curls, @rnaStock);
CALL insert_svr_if_not_exists(@cvSlide, @dnaStock);
CALL insert_svr_if_not_exists(@cvSlide, @rnaStock);

DROP PROCEDURE IF EXISTS insert_svr_if_not_exists;
--EndNoTest
-- EndNoTest

-- indices_for_TGL
-- StartNoTest
-- StartNoTest
DELIMITER //
DROP PROCEDURE IF EXISTS addIndexTemp//
CREATE PROCEDURE addIndexTemp(
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

CALL addIndexTemp('TruSeq HT', 'D707', 'CTGAAGCT', 1);
CALL addIndexTemp('TruSeq HT', 'D708', 'TAATGCGC', 1);
CALL addIndexTemp('TruSeq HT', 'D709', 'CGGCTATG', 1);
CALL addIndexTemp('TruSeq HT', 'D710', 'TCCGCGAA', 1);
CALL addIndexTemp('TruSeq HT', 'D711', 'TCTCGCGC', 1);
CALL addIndexTemp('TruSeq HT', 'D712', 'AGCGATAG', 1);

CALL addIndex('TruSeq HT', 'D507', 'CAGGACGT', 2);
CALL addIndex('TruSeq HT', 'D508', 'GTACTGAC', 2);
DROP PROCEDURE IF EXISTS addIndexTemp;
-- EndNoTest
-- EndNoTest

