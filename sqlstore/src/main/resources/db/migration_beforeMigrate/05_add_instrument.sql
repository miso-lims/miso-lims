-- If you change these method names or signatures, update the docs in `docs/_posts/2017-12-07-admin-guide.md`
-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addInstrument//
CREATE PROCEDURE addInstrument(
  iName varchar(30),
  iPlatformName varchar(50),
  iPlatformModel varchar(100),
  iSerialNumber varchar(30),
  iDateCommissioned date,
  iDateDecommissioned date,
  iUpgradedInstrumentName varchar(30)
) BEGIN
  DECLARE platId, upgradedId bigint(20);
  DECLARE errorMessage varchar(300);
  IF NOT EXISTS (SELECT 1 FROM Instrument WHERE name = iName) THEN
    SELECT platformId INTO platId FROM Platform WHERE name = iPlatformName AND instrumentModel = iPlatformModel;
    IF platId IS NULL THEN
      SET errorMessage = CONCAT('Platform ''', iPlatformModel, ''' not found.');
      SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
    END IF;
    IF iUpgradedInstrumentName IS NOT NULL THEN
      SELECT instrumentId INTO upgradedId FROM Instrument WHERE name = iUpgradedInstrumentName;
      IF upgradedId IS NULL THEN
        SET errorMessage = CONCAT('Upgraded instrument ''', iUpgradedInstrumentName, ''' not found.');
        SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
      END IF;
    ELSE
      SET upgradedId = NULL;
    END IF;
    
    INSERT INTO Instrument(name, platformId, serialNumber, dateCommissioned, dateDecommissioned,
    upgradedInstrumentId)
    VALUES (iName, platId, iSerialNumber, iDateCommissioned, iDateDecommissioned, upgradedId);
  END IF;
END//

DELIMITER ;
-- EndNoTest
