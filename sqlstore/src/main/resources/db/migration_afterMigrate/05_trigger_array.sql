-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS ArrayInsert//
CREATE TRIGGER ArrayInsert AFTER INSERT ON Array
FOR EACH ROW
  INSERT INTO ArrayChangeLog(arrayId, columnsChanged, userId, message, changeTime)
  VALUES (NEW.arrayId, '', NEW.lastModifier, 'Array created.', NEW.lastModified)//

DROP TRIGGER IF EXISTS ArrayChange//
CREATE TRIGGER ArrayChange BEFORE UPDATE ON Array
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN NEW.alias <> OLD.alias THEN CONCAT('alias: ', OLD.alias, ' → ', NEW.alias) END,
        CASE WHEN (NEW.serialNumber IS NULL) <> (OLD.serialNumber IS NULL) OR NEW.serialNumber <> OLD.serialNumber THEN CONCAT('serial number: ', COALESCE(OLD.serialNumber, 'n/a'), ' → ', COALESCE(NEW.serialNumber, 'n/a')) END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN NEW.arrayModelId <> OLD.arrayModelId THEN CONCAT('model: ', (SELECT alias FROM ArrayModel WHERE arrayModelId = OLD.arrayModelId), ' → ', (SELECT alias FROM ArrayModel WHERE arrayModelId = NEW.arrayModelId)) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO ArrayChangeLog(arrayId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.arrayId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.alias <> OLD.alias THEN 'alias' END,
        CASE WHEN (NEW.serialNumber IS NULL) <> (OLD.serialNumber IS NULL) OR NEW.serialNumber <> OLD.serialNumber THEN 'serialNumber' END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN 'description' END,
        CASE WHEN NEW.arrayModelId <> OLD.arrayModelId THEN 'arrayModelId' END), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified);
  END IF;
END//

DROP TRIGGER IF EXISTS ArrayPositionInsert//
CREATE TRIGGER ArrayPositionInsert AFTER INSERT ON ArrayPosition
FOR EACH ROW
  INSERT INTO ArrayChangeLog(arrayId, columnsChanged, userId, message, changeTime)
  SELECT NEW.arrayId, '', a.lastModifier, CONCAT('SAM', NEW.sampleId, ' added to ', NEW.position), a.lastModified
  FROM Array a WHERE a.arrayId = NEW.arrayId//

DELIMITER ;
-- EndNoTest
