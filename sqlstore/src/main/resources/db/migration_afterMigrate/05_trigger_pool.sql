-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS BeforeInsertPool//

DROP TRIGGER IF EXISTS PoolChange//
CREATE TRIGGER PoolChange BEFORE UPDATE ON Pool
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN CONCAT('description: ', COALESCE(OLD.description, 'n/a'), ' → ', COALESCE(NEW.description, 'n/a')) END,
        CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN CONCAT('concentration: ', COALESCE(OLD.concentration, 'n/a'), ' → ', COALESCE(NEW.concentration, 'n/a')) END, 
        CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN CONCAT('volume: ', COALESCE(OLD.volume, 'n/a'), ' → ', COALESCE(NEW.volume, 'n/a')) END, 
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification-barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
        CASE WHEN NEW.platformType <> OLD.platformType THEN CONCAT('platform-type: ', OLD.platformType, ' → ', NEW.platformType) END,
        CASE WHEN NEW.discarded <> OLD.discarded THEN CONCAT('discarded: ', OLD.discarded, ' → ', NEW.discarded) END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN CONCAT('QC passed: ', COALESCE(OLD.qcPassed, 'n/a'), ' → ', COALESCE(NEW.qcPassed, 'n/a')) END,
        CASE WHEN (NEW.concentrationUnits IS NULL) <> (OLD.concentrationUnits IS NULL) OR NEW.concentrationUnits <> OLD.concentrationUnits THEN CONCAT(NEW.name, ' concentration units: ', COALESCE(OLD.concentrationUnits, 'n/a'), ' → ', COALESCE(NEW.concentrationUnits, 'n/a')) END,
        CASE WHEN (NEW.volumeUnits IS NULL) <> (OLD.volumeUnits IS NULL) OR NEW.volumeUnits <> OLD.volumeUnits THEN CONCAT(NEW.name, ' volume units: ', COALESCE(OLD.volumeUnits, 'n/a'), ' → ', COALESCE(NEW.volumeUnits, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.poolId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN 'description' END,
        CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN 'concentration' END,
        CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN 'volume' END,
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
        CASE WHEN NEW.platformType <> OLD.platformType THEN 'platformType' END,
        CASE WHEN NEW.discarded <> OLD.discarded THEN 'discarded' END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN 'qcPassed' END,
        CASE WHEN (NEW.concentrationUnits IS NULL) <> (OLD.concentrationUnits IS NULL) OR NEW.concentrationUnits <> OLD.concentrationUnits THEN CONCAT(NEW.name, ' concentrationUnits') END,
        CASE WHEN (NEW.volumeUnits IS NULL) <> (OLD.volumeUnits IS NULL) OR NEW.volumeUnits <> OLD.volumeUnits THEN CONCAT(NEW.name, ' volumeUnits') END), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified);
  END IF;
  END//

DROP TRIGGER IF EXISTS PoolInsert//
CREATE TRIGGER PoolInsert AFTER INSERT ON Pool
FOR EACH ROW
  INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.poolId,
    '',
    NEW.lastModifier,
    'Pool created.',
    NEW.lastModified)//

DELIMITER ;
-- EndNoTest
