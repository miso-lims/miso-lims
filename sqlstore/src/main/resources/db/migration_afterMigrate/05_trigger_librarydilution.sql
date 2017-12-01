-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS LibraryDilutionChange//
CREATE TRIGGER LibraryDilutionChange BEFORE UPDATE ON LibraryDilution
FOR EACH ROW
  BEGIN
    DECLARE log_message varchar(500) CHARACTER SET utf8;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN NEW.concentration <> OLD.concentration THEN CONCAT(NEW.name, ' concentration: ', OLD.concentration, ' → ', NEW.concentration) END,
      CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT(NEW.name, ' barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
      CASE WHEN NEW.library_libraryId <> OLD.library_libraryId THEN CONCAT('parent: ', (SELECT name FROM Library WHERE libraryId = OLD.library_libraryId), ' → ', (SELECT name FROM Library WHERE libraryId = NEW.library_libraryId)) END,
      CASE WHEN (NEW.targetedSequencingId IS NULL) <> (OLD.targetedSequencingId IS NULL) OR NEW.targetedSequencingId <> OLD.targetedSequencingId THEN CONCAT(NEW.name, ' targeted sequencing: ', COALESCE((SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = OLD.targetedSequencingId), 'n/a'), ' → ', COALESCE((SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = NEW.targetedSequencingId), 'n/a')) END);
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.library_libraryId,
        COALESCE(CONCAT_WS(',',
          CASE WHEN NEW.concentration <> OLD.concentration THEN CONCAT(NEW.name, ' concentration') END,
          CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT(NEW.name, ' identificationBarcode') END,
          CASE WHEN NEW.library_libraryId <> OLD.library_libraryId THEN CONCAT(NEW.name, ' parent') END,
          CASE WHEN (NEW.targetedSequencingId IS NULL) <> (OLD.targetedSequencingId IS NULL) OR NEW.targetedSequencingId <> OLD.targetedSequencingId THEN CONCAT(NEW.name, ' targetedSequencingId') END
        ), ''),
        NEW.lastModifier,
        log_message,
        NEW.lastUpdated
      );
    END IF;
  END//

DROP TRIGGER IF EXISTS LibraryDilutionInsert//
CREATE TRIGGER LibraryDilutionInsert AFTER INSERT ON LibraryDilution
FOR EACH ROW
  INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.library_libraryId,
    '',
    NEW.lastModifier,
    CONCAT('Library dilution LDI', NEW.dilutionId, ' created.'),
    NEW.lastUpdated)//

DELIMITER ;
-- EndNoTest
