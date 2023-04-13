DELIMITER //

DROP TRIGGER IF EXISTS StorageLocationInsert//
CREATE TRIGGER StorageLocationInsert AFTER INSERT ON StorageLocation
FOR EACH ROW
  IF NEW.locationUnit = 'FREEZER' THEN
    INSERT INTO StorageLocationChangeLog(locationId, columnsChanged, userId, message, changeTime)
    VALUES (NEW.locationId, '', NEW.lastModifier, 'Freezer created.', NEW.lastModified);
  END IF//

DROP TRIGGER IF EXISTS StorageLocationChange//
CREATE TRIGGER StorageLocationChange BEFORE UPDATE ON StorageLocation
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext;
  IF NEW.locationUnit = 'FREEZER' THEN
    SET log_message = CONCAT_WS(', ',
      makeChangeMessage('alias', OLD.alias, NEW.alias),
      makeChangeMessage('barcode', OLD.identificationBarcode, NEW.identificationBarcode),
      makeChangeMessage('room', (SELECT alias FROM StorageLocation WHERE locationId = OLD.parentLocationId), (SELECT alias FROM StorageLocation WHERE locationId = NEW.parentLocationId)),
      makeChangeMessage('probe ID', OLD.probeId, NEW.probeId),
      makeChangeMessage('retired', booleanToString(OLD.retired), booleanToString(NEW.retired)),
      makeChangeMessage('label', (SELECT label FROM StorageLabel WHERE labelId = OLD.labelId), (SELECT label FROM StorageLabel WHERE labelId = NEW.labelId))
    );
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO StorageLocationChangeLog(locationId, columnsChanged, userId, message, changeTime) VALUES (
        NEW.locationId,
        COALESCE(CONCAT_WS(',',
          makeChangeColumn('alias', OLD.alias, NEW.alias),
          makeChangeColumn('identificationBarcode', OLD.identificationBarcode, NEW.identificationBarcode),
          makeChangeColumn('parentLocationId', OLD.parentLocationId, NEW.parentLocationId),
          makeChangeColumn('probeId', OLD.probeId, NEW.probeId),
          makeChangeColumn('retired', OLD.retired, NEW.retired),
          makeChangeColumn('labelId', OLD.labelId, NEW.labelId)
        ), ''),
        NEW.lastModifier,
        log_message,
        NEW.lastModified);
    END IF;
  END IF;
END//
  
DELIMITER ;
