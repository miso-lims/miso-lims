-- StartNoTest
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
  DECLARE log_message longtext CHARACTER SET utf8;
  IF NEW.locationUnit = 'FREEZER' THEN
    SET log_message = CONCAT_WS(', ',
          CASE WHEN NEW.alias <> OLD.alias THEN CONCAT('alias: ', OLD.alias, ' → ', NEW.alias) END,
          CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
          CASE WHEN NEW.parentLocationId <> OLD.parentLocationId THEN CONCAT('room: ', (SELECT alias FROM StorageLocation WHERE locationId = OLD.parentLocationId), ' → ', (SELECT alias FROM StorageLocation WHERE locationId = NEW.parentLocationId)) END,
          CASE WHEN (NEW.probeId IS NULL) <> (OLD.probeId IS NULL) OR NEW.probeId <> OLD.probeId THEN CONCAT('probe ID: ', COALESCE(OLD.probeId, 'n/a'),  ' → ', COALESCE(NEW.probeId, 'n/a')) END);
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO StorageLocationChangeLog(locationId, columnsChanged, userId, message, changeTime) VALUES (
        NEW.locationId,
        COALESCE(CONCAT_WS(',',
          CASE WHEN NEW.alias <> OLD.alias THEN 'alias' END,
          CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
          CASE WHEN NEW.parentLocationId <> OLD.parentLocationId THEN 'parentLocationId' END,
          CASE WHEN (NEW.probeId IS NULL) <> (OLD.probeId IS NULL) OR NEW.probeId <> OLD.probeId THEN 'probeId' END), ''),
        NEW.lastModifier,
        log_message,
        NEW.lastModified);
    END IF;
  END IF;
END//
  
DELIMITER ;
-- EndNoTest
