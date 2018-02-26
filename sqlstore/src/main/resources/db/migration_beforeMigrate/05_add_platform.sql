-- If you change these method names or signatures, update the docs in `docs/_posts/2017-12-07-admin-guide.md`
-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addPlatform//
DROP PROCEDURE IF EXISTS addInstrumentModel//
CREATE PROCEDURE addInstrumentModel(
  iName varchar(50),
  iInstrumentModel varchar(100),
  iDescription varchar(255),
  iNumContainers tinyint(4),
  iPartitionSize int(11),
  iInstrumentType varchar(50)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM Platform WHERE name = iName and instrumentModel = iInstrumentModel) THEN
    INSERT INTO Platform(name, instrumentModel, description, numContainers, instrumentType)
    VALUES (iName, iInstrumentModel, iDescription, iNumContainers, iInstrumentType);
    
    INSERT INTO PlatformSizes(platform_platformId, partitionSize)
    VALUES (LAST_INSERT_ID(), iPartitionSize);
  END IF;
END//

DELIMITER ;
-- EndNoTest
