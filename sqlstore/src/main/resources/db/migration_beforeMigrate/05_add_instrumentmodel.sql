-- If you change these method names or signatures, update the docs in `docs/_posts/2017-12-07-admin-guide.md`
-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addInstrumentModel//
CREATE PROCEDURE addInstrumentModel(
  iPlatform varchar(50),
  iAlias varchar(100),
  iDescription varchar(255),
  iNumContainers tinyint(4),
  iInstrumentType varchar(50)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM InstrumentModel WHERE platform = iPlatform and alias = iAlias) THEN
    INSERT INTO InstrumentModel(platform, alias, description, numContainers, instrumentType)
    VALUES (iPlatform, iAlias, iDescription, iNumContainers, iInstrumentType);
  END IF;
END//

DELIMITER ;
-- EndNoTest
