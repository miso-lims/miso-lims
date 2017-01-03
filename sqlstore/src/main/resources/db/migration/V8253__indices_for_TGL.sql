-- StartNoTest
DELIMITER //
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

CALL addIndex('TruSeq HT', 'D707', 'CTGAAGCT', 1);
CALL addIndex('TruSeq HT', 'D708', 'TAATGCGC', 1);
CALL addIndex('TruSeq HT', 'D709', 'CGGCTATG', 1);
CALL addIndex('TruSeq HT', 'D710', 'TCCGCGAA', 1);
CALL addIndex('TruSeq HT', 'D711', 'TCTCGCGC', 1);
CALL addIndex('TruSeq HT', 'D712', 'AGCGATAG', 1);

CALL addIndex('TruSeq HT', 'D507', 'CAGGACGT', 2);
CALL addIndex('TruSeq HT', 'D508', 'GTACTGAC', 2);
DROP PROCEDURE IF EXISTS addIndex;
-- EndNoTest