-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS BoxChange//
CREATE TRIGGER BoxChange BEFORE UPDATE ON Box
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    makeChangeMessage('alias', OLD.alias, NEW.alias),
    makeChangeMessage('barcode', OLD.identificationBarcode, NEW.identificationBarcode),
    makeChangeMessage('location', OLD.locationBarcode, NEW.locationBarcode),
    makeChangeMessage('description', OLD.description, NEW.description),
    makeChangeMessage('use', (SELECT alias FROM BoxUse WHERE boxUseId = OLD.boxUseId), (SELECT alias FROM BoxUse WHERE boxUseId = NEW.boxUseId))
  );
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message, changeTime) VALUES (
      New.boxId,
      COALESCE(CONCAT_WS(',',
        makeChangeColumn('alias', NEW.alias, OLD.alias),
        makeChangeColumn('identificationBarcode', NEW.identificationBarcode, OLD.identificationBarcode),
        makeChangeColumn('locationBarcode', NEW.locationBarcode, OLD.locationBarcode),
        makeChangeColumn('description', NEW.description, OLD.description),
        makeChangeColumn('boxUseId', NEW.boxUseId, OLD.boxUseId)
      ), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified
    );
  END IF;
  END//

DROP TRIGGER IF EXISTS BoxInsert//
CREATE TRIGGER BoxInsert AFTER INSERT ON Box
FOR EACH ROW
  INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.boxId,
    '',
    NEW.lastModifier,
    'Box created.',
    NEW.lastModified
  )//

DELIMITER ;
-- EndNoTest
