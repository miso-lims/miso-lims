-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS LibraryChange//
CREATE TRIGGER LibraryChange BEFORE UPDATE ON Library
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    makeChangeMessage('accession', OLD.accession, NEW.accession),
    CASE WHEN OLD.alias NOT LIKE 'TEMPORARY%' THEN makeChangeMessage('alias', OLD.alias, NEW.alias) END,
    makeChangeMessage('concentration', decimalToString(OLD.concentration), decimalToString(NEW.concentration)),
    makeChangeMessage('description', OLD.description, NEW.description),
    makeChangeMessage('identification barcode', OLD.identificationBarcode, NEW.identificationBarcode),
    makeChangeMessage('selection', (SELECT name FROM LibrarySelectionType WHERE librarySelectionTypeId = OLD.librarySelectionType), (SELECT name FROM LibrarySelectionType WHERE librarySelectionTypeId = NEW.librarySelectionType)),
    makeChangeMessage('strategy', (SELECT name FROM LibraryStrategyType WHERE libraryStrategyTypeId = OLD.libraryStrategyType), (SELECT name FROM LibraryStrategyType WHERE libraryStrategyTypeId = NEW.libraryStrategyType)),
    makeChangeMessage('type', (SELECT description FROM LibraryType WHERE libraryTypeId = OLD.libraryType), (SELECT description FROM LibraryType WHERE libraryTypeId = NEW.libraryType)),
    makeChangeMessage('location', OLD.locationBarcode, NEW.locationBarcode),
    makeChangeMessage('low quality', booleanToString(OLD.lowQuality), booleanToString(NEW.lowQuality)),
    makeChangeMessage('paired end', booleanToString(OLD.paired), booleanToString(NEW.paired)),
    makeChangeMessage('platform', OLD.platformType, NEW.platformType),
    makeChangeMessage('QC Status', (SELECT description FROM DetailedQcStatus WHERE detailedQcStatusId = OLD.detailedQcStatusId), (SELECT description FROM DetailedQcStatus WHERE detailedQcStatusId = NEW.detailedQcStatusId)),
    makeChangeMessage('QC Status Note', OLD.detailedQcStatusNote, NEW.detailedQcStatusNote),
    makeChangeMessage('discarded', booleanToString(OLD.discarded), booleanToString(NEW.discarded)),
    makeChangeMessage('size', OLD.dnaSize, NEW.dnaSize),
    makeChangeMessage('parent', (SELECT name FROM Sample WHERE sampleId = OLD.sample_sampleId), (SELECT name FROM Sample WHERE sampleId = NEW.sample_sampleId)),
    makeChangeMessage('kit', (SELECT name FROM KitDescriptor WHERE kitDescriptorId = OLD.kitDescriptorId), (SELECT name FROM KitDescriptor WHERE kitDescriptorId = NEW.kitDescriptorId)),
    makeChangeMessage('volume', decimalToString(OLD.volume), decimalToString(NEW.volume)),
    makeChangeMessage('initial volume', decimalToString(OLD.initialVolume), decimalToString(NEW.initialVolume)),
    makeChangeMessage('volume used', decimalToString(OLD.volumeUsed), decimalToString(NEW.volumeUsed)),
    makeChangeMessage('ng used', decimalToString(OLD.ngUsed), decimalToString(NEW.ngUsed)),
    makeChangeMessage('concentration units', OLD.concentrationUnits, NEW.concentrationUnits),
    makeChangeMessage('volume units: ', OLD.volumeUnits, NEW.volumeUnits),
    makeChangeMessage('UMIs', booleanToString(OLD.umis), booleanToString(NEW.umis)),
    makeChangeMessage('thermal cycler', (SELECT name FROM Instrument WHERE instrumentId = OLD.thermalCyclerId), (SELECT name FROM Instrument WHERE instrumentId = NEW.thermalCyclerId)),
    makeChangeMessage('kit', (SELECT alias FROM Workstation WHERE workstationId = OLD.workstationId), (SELECT alias FROM Workstation WHERE workstationId = NEW.workstationId)),
    makeChangeMessage('archived', booleanToString(OLD.archived), booleanToString(NEW.archived)),
    makeChangeMessage('design code', (SELECT code FROM LibraryDesignCode WHERE libraryDesignCodeId = OLD.libraryDesignCodeId), (SELECT code FROM LibraryDesignCode WHERE libraryDesignCodeId = NEW.libraryDesignCodeId)),
    makeChangeMessage('design', (SELECT name FROM LibraryDesign WHERE libraryDesignId = OLD.libraryDesign), (SELECT name FROM LibraryDesign WHERE libraryDesignId = NEW.libraryDesign)),
    makeChangeMessage('group description', OLD.groupDescription, NEW.groupDescription),
    makeChangeMessage('group id', OLD.groupId, NEW.groupId),
    makeChangeMessage('SOP', (SELECT CONCAT(alias, ' (', version, ')') FROM Sop WHERE sopId = OLD.sopId), (SELECT CONCAT(alias, ' (', version, ')') FROM Sop WHERE sopId = NEW.sopId))
  );
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.libraryId,
      COALESCE(CONCAT_WS(',',
        makeChangeColumn('accession', NEW.accession, OLD.accession),
        CASE WHEN OLD.alias NOT LIKE 'TEMPORARY%' THEN makeChangeColumn('alias', OLD.alias, NEW.alias) END,
        makeChangeColumn('concentration', OLD.concentration, NEW.concentration),
        makeChangeColumn('description', OLD.description, NEW.description),
        makeChangeColumn('identificationBarcode', OLD.identificationBarcode, NEW.identificationBarcode),
        makeChangeColumn('librarySelectionType', OLD.librarySelectionType, NEW.librarySelectionType),
        makeChangeColumn('libraryStrategyType', OLD.libraryStrategyType, NEW.libraryStrategyType),
        makeChangeColumn('libraryType', OLD.libraryType, NEW.libraryType),
        makeChangeColumn('locationBarcode', OLD.locationBarcode, NEW.locationBarcode),
        makeChangeColumn('lowQuality', OLD.lowQuality, NEW.lowQuality),
        makeChangeColumn('paired', OLD.paired, NEW.paired),
        makeChangeColumn('platformType', OLD.platformType, NEW.platformType),
        makeChangeColumn('detailedQcStatusId', OLD.detailedQcStatusId, NEW.detailedQcStatusId),
        makeChangeColumn('detailedQcStatusNote', OLD.detailedQcStatusNote, NEW.detailedQcStatusNote),
        makeChangeColumn('sample_sampleId', OLD.sample_sampleId, NEW.sample_sampleId),
        makeChangeColumn('discarded', OLD.discarded, NEW.discarded),
        makeChangeColumn('dnaSize', OLD.dnaSize, NEW.dnaSize),
        makeChangeColumn('kitDescriptorId', OLD.kitDescriptorId, NEW.kitDescriptorId),
        makeChangeColumn('initialVolume', OLD.initialVolume, NEW.initialVolume),
        makeChangeColumn('volume', OLD.volume, NEW.volume),
        makeChangeColumn('volumeUsed', OLD.volumeUsed, NEW.volumeUsed),
        makeChangeColumn('ngUsed', OLD.ngUsed, NEW.ngUsed),
        makeChangeColumn('concentrationUnits', OLD.concentrationUnits, NEW.concentrationUnits),
        makeChangeColumn('volumeUnits', OLD.volumeUnits, NEW.volumeUnits),
        makeChangeColumn('umis', OLD.umis, NEW.umis),
        makeChangeColumn('thermalCyclerId', OLD.thermalCyclerId, NEW.thermalCyclerId),
        makeChangeColumn('workstationId', OLD.workstationId, NEW.workstationId),
        makeChangeColumn('archived', OLD.archived, NEW.archived),
        makeChangeColumn('libraryDesign', OLD.libraryDesign, NEW.libraryDesign),
        makeChangeColumn('libraryDesignCodeId', OLD.libraryDesignCodeId, NEW.libraryDesignCodeId),
        makeChangeColumn('groupId', OLD.groupId, NEW.groupId),
        makeChangeColumn('groupDescription', OLD.groupDescription, NEW.groupDescription),
        makeChangeColumn('sopId', OLD.sopId, NEW.sopId)
  ), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS LibraryInsert//
CREATE TRIGGER LibraryInsert AFTER INSERT ON Library
FOR EACH ROW
  INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.libraryId,
    '',
    NEW.lastModifier,
    'Library created.',
    NEW.lastModified)//

DELIMITER ;
-- EndNoTest
