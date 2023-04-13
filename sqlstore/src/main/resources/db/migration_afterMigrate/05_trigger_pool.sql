-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS PoolChange//
CREATE TRIGGER PoolChange BEFORE UPDATE ON Pool
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext;
  SET log_message = CONCAT_WS(', ',
    makeChangeMessage('alias', OLD.alias, NEW.alias),
    makeChangeMessage('description', OLD.description, NEW.description),
    makeChangeMessage('identification barcode', OLD.identificationBarcode, NEW.identificationBarcode),
    makeChangeMessage('size', OLD.dnaSize, NEW.dnaSize),
    makeChangeMessage('concentration', decimalToString(OLD.concentration), decimalToString(NEW.concentration)),
    makeChangeMessage('concentration units', OLD.concentrationUnits, NEW.concentrationUnits),
    makeChangeMessage('volume', decimalToString(OLD.concentration), decimalToString(NEW.concentration)),
    makeChangeMessage('volume units: ', OLD.volumeUnits, NEW.volumeUnits),
    makeChangeMessage('platform type', OLD.platformType, NEW.platformType),
    makeChangeMessage('discarded', booleanToString(OLD.discarded), booleanToString(NEW.discarded)),
    makeChangeMessage('QC passed', booleanToString(OLD.qcPassed), booleanToString(NEW.qcPassed))
  );
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.poolId,
      COALESCE(CONCAT_WS(',',
        makeChangeColumn('alias', NEW.alias, OLD.alias),
        makeChangeColumn('description', NEW.description, OLD.description),
        makeChangeColumn('identificationBarcode', NEW.identificationBarcode, OLD.identificationBarcode),
        makeChangeColumn('dnaSize', NEW.dnaSize, OLD.dnaSize),
        makeChangeColumn('concentration', NEW.concentration, OLD.concentration),
        makeChangeColumn('concentrationUnits', NEW.concentrationUnits, OLD.concentrationUnits),
        makeChangeColumn('volume', NEW.volume, OLD.volume),
        makeChangeColumn('volumeUnits', NEW.volumeUnits, OLD.volumeUnits),
        makeChangeColumn('platformType', NEW.platformType, OLD.platformType),
        makeChangeColumn('discarded', NEW.discarded, OLD.discarded),
        makeChangeColumn('qcPassed', NEW.qcPassed, OLD.qcPassed)
        ), ''),
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
