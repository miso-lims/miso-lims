-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addPlatform//
CREATE PROCEDURE addPlatform(
  iName varchar(50),
  iInstrumentModel varchar(100),
  iDescription varchar(255),
  iNumContainers tinyint(4)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM Platform WHERE name = iName and instrumentModel = iInstrumentModel) THEN
    INSERT INTO Platform(name, instrumentModel, description, numContainers)
    VALUES (iName, iInstrumentModel, iDescription, iNumContainers);
  END IF;
END//

DELIMITER ;
-- EndNoTest
