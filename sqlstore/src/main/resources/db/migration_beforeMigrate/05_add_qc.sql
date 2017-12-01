-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addQcType//
CREATE PROCEDURE addQcType(
  iName varchar(255),
  iDescription varchar(255),
  iQcTarget varchar(50),
  iUnits varchar(20),
  iPrecisionAfterDecimal int(11)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM QCType WHERE name = iName AND qcTarget = iQcTarget) THEN
    INSERT INTO QCType(name, description, qcTarget, units, precisionAfterDecimal, archived)
    VALUES (iName, iDescription, iQcTarget, iUnits, iPrecisionAfterDecimal, 0);
  END IF;
END//

DELIMITER ;
-- EndNoTest
