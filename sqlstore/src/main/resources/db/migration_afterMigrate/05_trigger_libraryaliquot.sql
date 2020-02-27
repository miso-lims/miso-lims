-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS LibraryAliquotChange//
CREATE TRIGGER LibraryAliquotChange BEFORE UPDATE ON LibraryAliquot
FOR EACH ROW
  BEGIN
    DECLARE log_message varchar(500) CHARACTER SET utf8;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN NEW.alias <> OLD.alias THEN CONCAT('alias: ', OLD.alias, ' → ', NEW.alias) END,
      CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN CONCAT('concentration: ', COALESCE(decimalToString(OLD.concentration), 'n/a'), ' → ', COALESCE(decimalToString(NEW.concentration), 'n/a')) END,
      CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
      CASE WHEN NEW.libraryId <> OLD.libraryId THEN CONCAT('parent: ', (SELECT name FROM Library WHERE libraryId = OLD.libraryId), ' → ', (SELECT name FROM Library WHERE libraryId = NEW.libraryId)) END,
      CASE WHEN (NEW.targetedSequencingId IS NULL) <> (OLD.targetedSequencingId IS NULL) OR NEW.targetedSequencingId <> OLD.targetedSequencingId THEN CONCAT('targeted sequencing: ', COALESCE((SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = OLD.targetedSequencingId), 'n/a'), ' → ', COALESCE((SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = NEW.targetedSequencingId), 'n/a')) END,
      CASE WHEN (NEW.concentrationUnits IS NULL) <> (OLD.concentrationUnits IS NULL) OR NEW.concentrationUnits <> OLD.concentrationUnits THEN CONCAT('concentration units: ', COALESCE(OLD.concentrationUnits, 'n/a'), ' → ', COALESCE(NEW.concentrationUnits, 'n/a')) END,
      CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN CONCAT('volume: ', COALESCE(decimalToString(OLD.volume), 'n/a'), ' → ', COALESCE(decimalToString(NEW.volume), 'n/a')) END,
      CASE WHEN (NEW.volumeUnits IS NULL) <> (OLD.volumeUnits IS NULL) OR NEW.volumeUnits <> OLD.volumeUnits THEN CONCAT('volume units: ', COALESCE(OLD.volumeUnits, 'n/a'), ' → ', COALESCE(NEW.volumeUnits, 'n/a')) END,
      CASE WHEN (NEW.creationDate IS NULL) <> (OLD.creationDate IS NULL) OR NEW.creationDate <> OLD.creationDate THEN CONCAT('creation date: ', COALESCE(OLD.creationDate, 'n/a'), ' → ', COALESCE(NEW.creationDate, 'n/a')) END,
      CASE WHEN (NEW.ngUsed IS NULL) <> (OLD.ngUsed IS NULL) OR NEW.ngUsed <> OLD.ngUsed THEN CONCAT('ng used: ', COALESCE(decimalToString(OLD.ngUsed), 'n/a'), ' → ', COALESCE(decimalToString(NEW.ngUsed), 'n/a')) END,
      CASE WHEN (NEW.volumeUsed IS NULL) <> (OLD.volumeUsed IS NULL) OR NEW.volumeUsed <> OLD.volumeUsed THEN CONCAT('volume used: ', COALESCE(decimalToString(OLD.volumeUsed), 'n/a'), ' → ', COALESCE(decimalToString(NEW.volumeUsed), 'n/a')) END);
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO LibraryAliquotChangeLog(aliquotId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.aliquotId,
        COALESCE(CONCAT_WS(',',
          CASE WHEN NEW.alias <> OLD.alias THEN 'alias' END,
          CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN 'concentration' END,
          CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
          CASE WHEN NEW.libraryId <> OLD.libraryId THEN 'parent' END,
          CASE WHEN (NEW.targetedSequencingId IS NULL) <> (OLD.targetedSequencingId IS NULL) OR NEW.targetedSequencingId <> OLD.targetedSequencingId THEN 'targetedSequencingId' END,
          CASE WHEN (NEW.concentrationUnits IS NULL) <> (OLD.concentrationUnits IS NULL) OR NEW.concentrationUnits <> OLD.concentrationUnits THEN 'concentrationUnits' END,
          CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN 'volume' END,
          CASE WHEN (NEW.volumeUnits IS NULL) <> (OLD.volumeUnits IS NULL) OR NEW.volumeUnits <> OLD.volumeUnits THEN 'volumeUnits' END,
          CASE WHEN (NEW.creationDate IS NULL) <> (OLD.creationDate IS NULL) OR NEW.creationDate <> OLD.creationDate THEN 'creationDate' END,
          CASE WHEN (NEW.ngUsed IS NULL) <> (OLD.ngUsed IS NULL) OR NEW.ngUsed <> OLD.ngUsed THEN 'ngUsed' END,
          CASE WHEN (NEW.volumeUsed IS NULL) <> (OLD.volumeUsed IS NULL) OR NEW.volumeUsed <> OLD.volumeUsed THEN 'volumeUsed' END
        ), ''),
        NEW.lastModifier,
        log_message,
        NEW.lastUpdated
      );
    END IF;
  END//

DROP TRIGGER IF EXISTS LibraryAliquotInsert//
CREATE TRIGGER LibraryAliquotInsert AFTER INSERT ON LibraryAliquot
FOR EACH ROW
  INSERT INTO LibraryAliquotChangeLog(aliquotId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.aliquotId,
    '',
    NEW.lastModifier,
    CONCAT('Library aliquot created.'),
    NEW.lastUpdated)//

DELIMITER ;
-- EndNoTest
