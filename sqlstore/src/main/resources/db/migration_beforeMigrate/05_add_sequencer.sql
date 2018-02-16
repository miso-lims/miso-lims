-- If you change these method names or signatures, update the docs in `docs/_posts/2017-12-07-admin-guide.md`
-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addInstrument//
CREATE PROCEDURE addInstrument(
  iName varchar(30),
  iPlatformName varchar(50),
  iPlatformModel varchar(100),
  iSerialNumber varchar(30),
  iIpAddress varchar(50),
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
    
    INSERT INTO Instrument(name, ip, platformId, serialNumber, dateCommissioned, dateDecommissioned,
    upgradedInstrumentId)
    VALUES (iName, toIpAddressBlob(iIpAddress), platId, iSerialNumber, iDateCommissioned, iDateDecommissioned, upgradedId);
  END IF;
END//

DROP FUNCTION IF EXISTS toHexString//
CREATE FUNCTION toHexString(iNumber varchar(3)) RETURNS varchar(2)
BEGIN
  RETURN LPAD(HEX(CAST(iNumber AS DECIMAL)), 2, '0');
END// 

DROP FUNCTION IF EXISTS toIpAddressBlob//
CREATE FUNCTION toIpAddressBlob(iAddress varchar(30)) RETURNS blob
BEGIN
  DECLARE errorMessage varchar(300);
  DECLARE pos, len int;
  DECLARE hexString varchar(8);
  IF NOT (iAddress REGEXP '^[[:digit:]]{1,3}\\.[[:digit:]]{1,3}\\.[[:digit:]]{1,3}\\.[[:digit:]]{1,3}$') THEN
    SET errorMessage = CONCAT('Invalid IP address: ''', iAddress, '''');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  SET pos = 1;
  SET len = LOCATE('.', iAddress, pos) - 1;
  SET hexString = toHexString(SUBSTRING(iAddress, pos, len));
  SET pos = pos + len + 1;
  SET len = LOCATE('.', iAddress, pos) - pos;
  SET hexString = CONCAT(hexString, toHexString(SUBSTRING(iAddress, pos, len)));
  SET pos = pos + len + 1;
  SET len = LOCATE('.', iAddress, pos) - pos;
  SET hexString = CONCAT(hexString, toHexString(SUBSTRING(iAddress, pos, len)));
  SET pos = pos + len + 1;
  SET hexString = CONCAT(hexString, toHexString(SUBSTRING(iAddress, pos)));
  RETURN UNHEX(hexString);
END//

DELIMITER ;
-- EndNoTest
