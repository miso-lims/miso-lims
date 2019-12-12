-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS LibraryChange//
CREATE TRIGGER LibraryChange BEFORE UPDATE ON Library
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias AND (OLD.alias NOT LIKE 'TEMPORARY%') THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN CONCAT('concentration: ', COALESCE(decimalToString(OLD.concentration), 'n/a'), ' → ', COALESCE(decimalToString(NEW.concentration), 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
        CASE WHEN (NEW.librarySelectionType IS NULL) <> (OLD.librarySelectionType IS NULL) OR NEW.librarySelectionType <> OLD.librarySelectionType THEN CONCAT('selection: ', COALESCE((SELECT name FROM LibrarySelectionType WHERE librarySelectionTypeId = OLD.librarySelectionType), 'n/a'), ' → ', COALESCE((SELECT name FROM LibrarySelectionType WHERE librarySelectionTypeId = NEW.librarySelectionType), 'n/a')) END,
        CASE WHEN (NEW.libraryStrategyType IS NULL) <> (OLD.libraryStrategyType IS NULL) OR NEW.libraryStrategyType <> OLD.libraryStrategyType THEN CONCAT('strategy: ', COALESCE((SELECT name FROM LibraryStrategyType WHERE libraryStrategyTypeId = OLD.libraryStrategyType), 'n/a'), ' → ', COALESCE((SELECT name FROM LibraryStrategyType WHERE libraryStrategyTypeId = NEW.libraryStrategyType), 'n/a')) END,
        CASE WHEN (NEW.libraryType IS NULL) <> (OLD.libraryType IS NULL) OR NEW.libraryType <> OLD.libraryType THEN CONCAT('type: ', COALESCE((SELECT description FROM LibraryType WHERE libraryTypeId = OLD.libraryType), 'n/a'), ' → ', COALESCE((SELECT description FROM LibraryType WHERE libraryTypeId = NEW.libraryType), 'n/a')) END,
        CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN CONCAT('location: ', COALESCE(OLD.locationBarcode, 'n/a'), ' → ', COALESCE(NEW.locationBarcode, 'n/a')) END,
        CASE WHEN NEW.lowQuality <> OLD.lowQuality THEN CONCAT('low quality: ', CASE WHEN OLD.lowQuality THEN 'yes' ELSE 'no' END, ' → ', CASE WHEN NEW.lowQuality THEN 'yes' ELSE 'no' END) END,
        CASE WHEN NEW.paired <> OLD.paired THEN CONCAT('end: ', CASE WHEN OLD.paired THEN 'paired' ELSE 'single' END, ' → ', CASE WHEN NEW.paired THEN 'paired' ELSE 'single' END) END,
        CASE WHEN (NEW.platformType IS NULL) <> (OLD.platformType IS NULL) OR NEW.platformType <> OLD.platformType THEN CONCAT('platform: ', COALESCE(OLD.platformType, 'n/a'), ' → ', COALESCE(NEW.platformType, 'n/a')) END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN CONCAT('QC passed: ', COALESCE(OLD.qcPassed, 'n/a'), ' → ', COALESCE(NEW.qcPassed, 'n/a')) END,
        CASE WHEN NEW.discarded <> OLD.discarded THEN CONCAT('discarded: ', IF(OLD.discarded = 0, 'No', 'Yes'), ' → ', IF(NEW.discarded = 0, 'No', 'Yes')) END,
        CASE WHEN (NEW.dnaSize IS NULL) <> (OLD.dnaSize IS NULL) OR NEW.dnaSize <> OLD.dnaSize THEN CONCAT('size: ', COALESCE(OLD.dnaSize, 'n/a'), ' → ', COALESCE(NEW.dnaSize, 'n/a')) END,
        CASE WHEN NEW.sample_sampleId <> OLD.sample_sampleId THEN CONCAT('parent: ', (SELECT name FROM Sample WHERE sampleId = OLD.sample_sampleId), ' → ', (SELECT name FROM Sample WHERE sampleId = NEW.sample_sampleId)) END,
        CASE WHEN (NEW.kitDescriptorId IS NULL) <> (OLD.kitDescriptorId IS NULL) OR NEW.kitDescriptorId <> OLD.kitDescriptorId THEN CONCAT('kit: ', COALESCE((SELECT name FROM KitDescriptor WHERE kitDescriptorId = OLD.kitDescriptorId), 'n/a'), ' → ', COALESCE((SELECT name FROM KitDescriptor WHERE kitDescriptorId = NEW.kitDescriptorId), 'n/a')) END,
        CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN CONCAT('volume: ', COALESCE(decimalToString(OLD.volume), 'n/a'), ' → ', COALESCE(decimalToString(NEW.volume), 'n/a')) END,
        CASE WHEN (NEW.initialVolume IS NULL) <> (OLD.initialVolume IS NULL) OR NEW.initialVolume <> OLD.initialVolume THEN CONCAT('initial volume: ', COALESCE(decimalToString(OLD.initialVolume), 'n/a'), ' → ', COALESCE(decimalToString(NEW.initialVolume), 'n/a')) END,
        CASE WHEN (NEW.volumeUsed IS NULL) <> (OLD.volumeUsed IS NULL) OR NEW.volumeUsed <> OLD.volumeUsed THEN CONCAT('volume used: ', COALESCE(decimalToString(OLD.volumeUsed), 'n/a'), ' → ', COALESCE(decimalToString(NEW.volumeUsed), 'n/a')) END,
        CASE WHEN (NEW.ngUsed IS NULL) <> (OLD.ngUsed IS NULL) OR NEW.ngUsed <> OLD.ngUsed THEN CONCAT('ng used: ', COALESCE(decimalToString(OLD.ngUsed), 'n/a'), ' → ', COALESCE(decimalToString(NEW.ngUsed), 'n/a')) END,
        CASE WHEN (NEW.concentrationUnits IS NULL) <> (OLD.concentrationUnits IS NULL) OR NEW.concentrationUnits <> OLD.concentrationUnits THEN CONCAT(NEW.name, ' concentration units: ', COALESCE(OLD.concentrationUnits, 'n/a'), ' → ', COALESCE(NEW.concentrationUnits, 'n/a')) END,
        CASE WHEN (NEW.volumeUnits IS NULL) <> (OLD.volumeUnits IS NULL) OR NEW.volumeUnits <> OLD.volumeUnits THEN CONCAT(NEW.name, ' volume units: ', COALESCE(OLD.volumeUnits, 'n/a'), ' → ', COALESCE(NEW.volumeUnits, 'n/a')) END,
        CASE WHEN NEW.umis <> OLD.umis THEN CONCAT('UMIs: ', IF(OLD.umis = 0, 'No', 'Yes'), ' → ', IF(NEW.umis = 0, 'No', 'Yes')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.libraryId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN 'accession' END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias AND (OLD.alias NOT LIKE 'TEMPORARY%') THEN 'alias' END,
        CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN 'concentration' END,
        CASE WHEN NEW.description <> OLD.description THEN 'description' END,
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
        CASE WHEN NEW.librarySelectionType <> OLD.librarySelectionType THEN 'librarySelectionType' END,
        CASE WHEN (NEW.libraryStrategyType IS NULL) <> (OLD.libraryStrategyType IS NULL) OR NEW.libraryStrategyType <> OLD.libraryStrategyType THEN 'libraryStrategyType' END,
        CASE WHEN (NEW.libraryType IS NULL) <> (OLD.libraryType IS NULL) OR NEW.libraryType <> OLD.libraryType THEN 'libraryType' END,
        CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN 'locationBarcode' END,
        CASE WHEN NEW.lowQuality <> OLD.lowQuality THEN 'lowQuality' END,
        CASE WHEN NEW.paired <> OLD.paired THEN 'paired' END,
        CASE WHEN (NEW.platformType IS NULL) <> (OLD.platformType IS NULL) OR NEW.platformType <> OLD.platformType THEN 'platformType' END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN 'qcPassed' END,
        CASE WHEN NEW.sample_sampleId <> OLD.sample_sampleId THEN 'sample_sampleId' END,
        CASE WHEN NEW.discarded <> OLD.discarded THEN 'discarded' END,
        CASE WHEN (NEW.dnaSize IS NULL) <> (OLD.dnaSize IS NULL) OR NEW.dnaSize <> OLD.dnaSize THEN 'dnaSize' END,
        CASE WHEN NEW.sample_sampleId <> OLD.sample_sampleId THEN 'parentSample' END,
        CASE WHEN (NEW.kitDescriptorId IS NULL) <> (OLD.kitDescriptorId IS NULL) OR NEW.kitDescriptorId <> OLD.kitDescriptorId THEN 'kitDescriptorId' END,
        CASE WHEN (NEW.initialVolume IS NULL) <> (OLD.initialVolume IS NULL) OR NEW.initialVolume <> OLD.initialVolume THEN 'initial volume' END,
        CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN 'volume' END,
        CASE WHEN (NEW.volumeUsed IS NULL) <> (OLD.volumeUsed IS NULL) OR NEW.volumeUsed <> OLD.volumeUsed THEN 'volume used' END,
        CASE WHEN (NEW.ngUsed IS NULL) <> (OLD.ngUsed IS NULL) OR NEW.ngUsed <> OLD.ngUsed THEN 'ng used' END,
        CASE WHEN (NEW.concentrationUnits IS NULL) <> (OLD.concentrationUnits IS NULL) OR NEW.concentrationUnits <> OLD.concentrationUnits THEN CONCAT(NEW.name, ' concentrationUnits') END,
        CASE WHEN (NEW.volumeUnits IS NULL) <> (OLD.volumeUnits IS NULL) OR NEW.volumeUnits <> OLD.volumeUnits THEN CONCAT(NEW.name, ' volumeUnits') END,
        CASE WHEN NEW.umis <> OLD.umis THEN 'umis' END
  ), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS DetailedLibraryChange//
CREATE TRIGGER DetailedLibraryChange BEFORE UPDATE ON DetailedLibrary
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
     CASE WHEN NEW.archived <> OLD.archived THEN CONCAT('archived: ', IF(OLD.archived = 0, 'No', 'Yes'), ' → ', IF(NEW.archived = 0, 'No', 'Yes')) END,
     CASE WHEN NEW.libraryDesignCodeId <> OLD.libraryDesignCodeId THEN CONCAT('designCode: ', (SELECT code FROM LibraryDesignCode WHERE libraryDesignCodeId = OLD.libraryDesignCodeId), ' → ', (SELECT code FROM LibraryDesignCode WHERE libraryDesignCodeId = NEW.libraryDesignCodeId)) END,
     CASE WHEN (NEW.libraryDesign IS NULL) <> (OLD.libraryDesign IS NULL) OR NEW.libraryDesign <> OLD.libraryDesign THEN CONCAT('library design: ', COALESCE((SELECT name FROM LibraryDesign WHERE libraryDesignId = OLD.libraryDesign), 'n/a'), ' → ', COALESCE((SELECT name FROM LibraryDesign WHERE libraryDesignId = NEW.libraryDesign), 'n/a')) END,
     CASE WHEN (NEW.groupDescription IS NULL) <> (OLD.groupDescription IS NULL) OR NEW.groupDescription <> OLD.groupDescription THEN CONCAT('group description: ', COALESCE(OLD.groupDescription, 'n/a'), ' → ', COALESCE(NEW.groupDescription, 'n/a')) END,
     CASE WHEN (NEW.groupId IS NULL) <> (OLD.groupId IS NULL) OR NEW.groupId <> OLD.groupId THEN CONCAT('group id: ', COALESCE(OLD.groupId, 'n/a'), ' → ', COALESCE(NEW.groupId, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.libraryId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.archived <> OLD.archived THEN 'archived' END,
        CASE WHEN (NEW.libraryDesign IS NULL) <> (OLD.libraryDesign IS NULL) OR NEW.libraryDesign <> OLD.libraryDesign THEN 'libraryDesign' END,
        CASE WHEN NEW.libraryDesignCodeId <> OLD.libraryDesignCodeId THEN 'libraryDesignCode' END,
        CASE WHEN (NEW.groupId IS NULL) <> (OLD.groupId IS NULL) OR NEW.groupID <> OLD.groupId THEN 'groupId' END,
        CASE WHEN (NEW.groupDescription IS NULL) <> (OLD.groupDescription IS NULL) OR NEW.groupDescription <> OLD.groupDescription THEN 'groupDescription' END
      ), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Library WHERE libraryId = NEW.libraryId;
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
