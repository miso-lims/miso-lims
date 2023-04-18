DELIMITER //

DROP TRIGGER IF EXISTS ArrayRunInsert//
CREATE TRIGGER ArrayRunInsert AFTER INSERT ON ArrayRun
FOR EACH ROW
  INSERT INTO ArrayRunChangeLog(arrayRunId, columnsChanged, userId, message, changeTime)
  VALUES (NEW.arrayRunId, '', NEW.lastModifier, 'Array run created.', NEW.lastModified)//

DROP TRIGGER IF EXISTS ArrayRunChange//
CREATE TRIGGER ArrayRunChange BEFORE UPDATE ON ArrayRun
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext;
  SET log_message = CONCAT_WS(', ',
    CASE WHEN NEW.alias <> OLD.alias THEN
      CONCAT('alias: ', OLD.alias, ' → ', NEW.alias)
    END,
    CASE WHEN NEW.instrumentId <> OLD.instrumentId THEN CONCAT(
      'instrument: ', (SELECT name FROM Instrument WHERE instrumentId = OLD.instrumentId),
      ' → ', (SELECT name FROM Instrument WHERE instrumentId = NEW.instrumentId)
    ) END,
    CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN CONCAT(
      'description: ', COALESCE(OLD.description, 'n/a'), ' → ', COALESCE(NEW.description, 'n/a')
    ) END,
    CASE WHEN (NEW.filePath IS NULL) <> (OLD.filePath IS NULL) OR NEW.filePath <> OLD.filePath THEN CONCAT(
      'file path: ', COALESCE(OLD.filePath, 'n/a'), ' → ', COALESCE(NEW.filePath, 'n/a')
    ) END,
    CASE WHEN (NEW.arrayId IS NULL) <> (OLD.arrayId IS NULL) OR NEW.arrayId <> OLD.arrayId THEN CONCAT(
      'array: ', COALESCE((SELECT CONCAT(alias, ' (ID: ', arrayId, ')') FROM Array WHERE arrayId = OLD.arrayId), 'n/a'),
      ' → ', COALESCE((SELECT CONCAT(alias, ' (ID: ', arrayId, ')') FROM Array WHERE arrayId = NEW.arrayId), 'n/a')
    ) END,
    CASE WHEN NEW.health <> OLD.health THEN CONCAT(
      'health: ', OLD.health, ' → ', NEW.health
    ) END,
    CASE WHEN (NEW.startDate IS NULL) <> (OLD.startDate IS NULL) OR NEW.startDate <> OLD.startDate THEN CONCAT(
      'startDate: ', COALESCE(OLD.startDate, 'n/a'), ' → ', COALESCE(NEW.startDate, 'n/a')
    ) END,
    CASE WHEN (NEW.completionDate IS NULL) <> (OLD.completionDate IS NULL) OR NEW.completionDate <> OLD.completionDate THEN CONCAT(
      'completion: ', COALESCE(OLD.completionDate, 'n/a'), ' → ', COALESCE(NEW.completionDate, 'n/a')
    ) END
  );
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO ArrayRunChangeLog(arrayRunId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.arrayRunId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.alias <> OLD.alias THEN 'alias' END,
        CASE WHEN NEW.instrumentId <> OLD.instrumentId THEN 'instrument' END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN 'description' END,
        CASE WHEN (NEW.filePath IS NULL) <> (OLD.filePath IS NULL) OR NEW.filePath <> OLD.filePath THEN 'filePath' END,
        CASE WHEN (NEW.arrayId IS NULL) <> (OLD.arrayId IS NULL) OR NEW.arrayId <> OLD.arrayId THEN 'arrayId' END,
        CASE WHEN NEW.health <> OLD.health THEN 'health' END,
        CASE WHEN (NEW.startDate IS NULL) <> (OLD.startDate IS NULL) OR NEW.startDate <> OLD.startDate THEN 'startDate' END,
        CASE WHEN (NEW.completionDate IS NULL) <> (OLD.completionDate IS NULL) OR NEW.completionDate <> OLD.completionDate THEN 'completionDate' END), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified);
  END IF;
END//

DELIMITER ;
