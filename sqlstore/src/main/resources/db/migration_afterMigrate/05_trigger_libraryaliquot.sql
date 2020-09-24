-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS LibraryAliquotChange//
CREATE TRIGGER LibraryAliquotChange BEFORE UPDATE ON LibraryAliquot
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext CHARACTER SET utf8;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN OLD.alias NOT LIKE 'TEMPORARY%' THEN makeChangeMessage('alias', OLD.alias, NEW.alias) END,
      makeChangeMessage('barcode', OLD.identificationBarcode, NEW.identificationBarcode),
      makeChangeMessage('parent', (SELECT name FROM Library WHERE libraryId = OLD.libraryId), (SELECT name FROM Library WHERE libraryId = NEW.libraryId)),
      makeChangeMessage('targeted sequencing', (SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = OLD.targetedSequencingId), (SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = NEW.targetedSequencingId)),
      makeChangeMessage('concentration', decimalToString(OLD.concentration), decimalToString(NEW.concentration)),
      makeChangeMessage('concentration units', OLD.concentrationUnits, NEW.concentrationUnits),
      makeChangeMessage('volume', decimalToString(OLD.volume), decimalToString(NEW.volume)),
      makeChangeMessage('volume units', OLD.volumeUnits, NEW.volumeUnits),
      makeChangeMessage('creation date', OLD.creationDate, NEW.creationDate),
      makeChangeMessage('ng used', decimalToString(OLD.ngUsed), decimalToString(NEW.ngUsed)),
      makeChangeMessage('volume used', decimalToString(OLD.volumeUsed), decimalToString(NEW.volumeUsed)),
      makeChangeMessage('design code', (SELECT code FROM LibraryDesignCode WHERE libraryDesignCodeId = OLD.libraryDesignCodeId), (SELECT code FROM LibraryDesignCode WHERE libraryDesignCodeId = NEW.libraryDesignCodeId)),
      makeChangeMessage('discarded', booleanToString(OLD.discarded), booleanToString(NEW.discarded)),
      makeChangeMessage('size', OLD.dnaSize, NEW.dnaSize),
      makeChangeMessage('QC Status', (SELECT description FROM DetailedQcStatus WHERE detailedQcStatusId = OLD.detailedQcStatusId), (SELECT description FROM DetailedQcStatus WHERE detailedQcStatusId = NEW.detailedQcStatusId)),
      makeChangeMessage('QC Status Note', OLD.detailedQcStatusNote, NEW.detailedQcStatusNote),
      makeChangeMessage('group id', OLD.groupId, NEW.groupId),
      makeChangeMessage('group description', OLD.groupDescription, NEW.groupDescription)
    );
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO LibraryAliquotChangeLog(aliquotId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.aliquotId,
        COALESCE(CONCAT_WS(',',
          CASE WHEN OLD.alias NOT LIKE 'TEMPORARY%' THEN makeChangeColumn('alias', OLD.alias, NEW.alias) END,
          makeChangeColumn('identificationBarcode', OLD.identificationBarcode, NEW.identificationBarcode),
          makeChangeColumn('libraryId', OLD.libraryId, NEW.libraryId),
          makeChangeColumn('targetedSequencingId', OLD.targetedSequencingId, NEW.targetedSequencingId),
          makeChangeColumn('concentration', OLD.concentration, NEW.concentration),
          makeChangeColumn('concentrationUnits', OLD.concentrationUnits, NEW.concentrationUnits),
          makeChangeColumn('volume', OLD.volume, NEW.volume),
          makeChangeColumn('volumeUnits', OLD.volumeUnits, NEW.volumeUnits),
          makeChangeColumn('creationDate', OLD.creationDate, NEW.creationDate),
          makeChangeColumn('ngUsed', OLD.ngUsed, NEW.ngUsed),
          makeChangeColumn('volumeUsed', OLD.volumeUsed, NEW.volumeUsed),
          makeChangeColumn('libraryDesignCodeId', OLD.libraryDesignCodeId, NEW.libraryDesignCodeId),
          makeChangeColumn('discarded', OLD.discarded, NEW.discarded),
          makeChangeColumn('dnaSize', OLD.dnaSize, NEW.dnaSize),
          makeChangeColumn('detailedQcStatusId', OLD.detailedQcStatusId, NEW.detailedQcStatusId),
          makeChangeColumn('detailedQcStatusNote', OLD.detailedQcStatusNote, NEW.detailedQcStatusNote),
          makeChangeColumn('groupId', OLD.groupId, NEW.groupId),
          makeChangeColumn('groupDescription', OLD.groupDescription, NEW.groupDescription)
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
    'Library aliquot created.',
    NEW.lastUpdated)//

DELIMITER ;
-- EndNoTest
