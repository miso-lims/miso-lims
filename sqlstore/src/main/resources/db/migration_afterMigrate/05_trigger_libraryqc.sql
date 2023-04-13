-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS LibraryQCInsert//
CREATE TRIGGER LibraryQCInsert AFTER INSERT ON LibraryQC
FOR EACH ROW
  INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.library_libraryId,
      'qc',
      lastModifier,
      CONCAT('QC added: ', (SELECT name FROM QCType WHERE qcTypeId = NEW.type)),
      lastModified
    FROM Library WHERE libraryId = NEW.library_libraryId;
//

DROP TRIGGER IF EXISTS LibraryQcUpdate//
CREATE TRIGGER LibraryQcUpdate BEFORE UPDATE ON LibraryQC
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN NEW.results <> OLD.results 
        THEN CONCAT('Updated ', (SELECT name FROM QCType WHERE qcTypeId = NEW.type), ' QC: ', OLD.results, ' → ', NEW.results, (SELECT units FROM QCType WHERE qcTypeId = NEW.type)) END);
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message, changeTime) 
      SELECT
        NEW.library_libraryId,
        'QC',
        lastModifier,
        log_message,
        lastModified
      FROM Library WHERE libraryId = NEW.library_libraryId;
    END IF;
  END//

DELIMITER;
-- EndNoTest