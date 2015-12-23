CREATE TRIGGER SampleChange BEFORE UPDATE ON Sample
FOR EACH ROW
  INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
    NEW.sampleId,
    COALESCE(CONCAT_WS(',',
      CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN 'accession' END,
      CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
      CASE WHEN NEW.description <> OLD.description THEN 'description' END,
      CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
      CASE WHEN NEW.locationBarcode <> OLD.locationBarcode THEN 'locationBarcode' END,
      CASE WHEN NEW.name <> OLD.name THEN 'name' END,
      CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN 'project_projectId' END,
      CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN 'qcPassed' END,
      CASE WHEN (NEW.receivedDate IS NULL) <> (OLD.receivedDate IS NULL) OR NEW.receivedDate <> OLD.receivedDate THEN 'receivedDate' END,
      CASE WHEN NEW.sampleType <> OLD.sampleType THEN 'sampleType' END,
      CASE WHEN NEW.scientificName <> OLD.scientificName THEN 'scientificName' END,
      CASE WHEN (NEW.taxonIdentifier IS NULL) <> (OLD.taxonIdentifier IS NULL) OR NEW.taxonIdentifier <> OLD.taxonIdentifier THEN 'taxonIdentifier' END,
      CASE WHEN NEW.emptied <> OLD.emptied THEN 'emptied' END,
      CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN 'volume' END
), ''),
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' has changed: ',
      CONCAT_WS(', ',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
        CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN CONCAT('location: ', COALESCE(OLD.locationBarcode, 'n/a'), ' → ', COALESCE(NEW.locationBarcode, 'n/a')) END,
        CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN CONCAT('project: ', (SELECT name FROM Project WHERE projectId = OLD.project_projectId), ' → ', (SELECT name FROM Project WHERE projectId = NEW.project_projectId)) END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN CONCAT('qcPassed: ', COALESCE(OLD.qcPassed, 'n/a'), ' → ', COALESCE(NEW.qcPassed, 'n/a')) END,
        CASE WHEN (NEW.receivedDate IS NULL) <> (OLD.receivedDate IS NULL) OR NEW.receivedDate <> OLD.receivedDate THEN CONCAT('received: ', COALESCE(OLD.receivedDate, 'n/a'), ' → ', COALESCE(NEW.receivedDate, 'n/a')) END,
        CASE WHEN NEW.sampleType <> OLD.sampleType THEN CONCAT('type: ', OLD.sampleType, ' → ', NEW.sampleType) END,
        CASE WHEN NEW.scientificName <> OLD.scientificName THEN CONCAT('scientific name: ', OLD.scientificName, ' → ', NEW.scientificName) END,
        CASE WHEN (NEW.taxonIdentifier IS NULL) <> (OLD.taxonIdentifier IS NULL) OR NEW.taxonIdentifier <> OLD.taxonIdentifier THEN CONCAT('taxon: ', COALESCE(OLD.taxonIdentifier, 'n/a'), ' → ', COALESCE(NEW.taxonIdentifier, 'n/a')) END,
        CASE WHEN NEW.emptied <> OLD.emptied THEN CONCAT('emptied: ', OLD.emptied, ' → ', NEW.emptied) END,
        CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN CONCAT('volume: ', COALESCE(OLD.volume, 'n/a'), ' → ', COALESCE(NEW.volume, 'n/a')) END)));

CREATE TRIGGER BeforeInsertSample BEFORE INSERT ON Sample
  FOR EACH ROW
  SET NEW.boxPositionId = nextval('box_position_seq');

CREATE TRIGGER SampleInsert AFTER INSERT ON Sample
FOR EACH ROW
  INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
    NEW.sampleId,
    '',
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' created sample.'));

CREATE TRIGGER PlateChange BEFORE UPDATE ON Plate
FOR EACH ROW
  INSERT INTO PlateChangeLog(plateId, columnsChanged, userId, message) VALUES (
    NEW.plateId,
    COALESCE(CONCAT_WS(',',
      CASE WHEN NEW.plateMaterialType <> OLD.plateMaterialType THEN 'plateMaterialType' END,
      CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN 'locationBarcode' END,
      CASE WHEN NEW.size <> OLD.size THEN 'size' END,
      CASE WHEN (NEW.tagBarcodeId IS NULL) <> (OLD.tagBarcodeId IS NULL) OR NEW.tagBarcodeId <> OLD.tagBarcodeId THEN 'tagBarcodeId' END,
      CASE WHEN NEW.description <> OLD.description THEN 'description' END), ''),
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' has changed: ',
      CONCAT_WS(', ',
        CASE WHEN NEW.plateMaterialType <> OLD.plateMaterialType THEN CONCAT('plate material: ', OLD.plateMaterialType, ' → ', NEW.plateMaterialType) END,
        CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN CONCAT('location barcode: ', COALESCE(OLD.locationBarcode, 'n/a'), ' → ', COALESCE(NEW.locationBarcode, 'n/a')) END,
        CASE WHEN NEW.size <> OLD.size THEN CONCAT('size: ', OLD.size, ' → ', NEW.size) END,
        CASE WHEN (NEW.tagBarcodeId IS NULL) <> (OLD.tagBarcodeId IS NULL) OR NEW.tagBarcodeId <> OLD.tagBarcodeId THEN CONCAT('tag barcode: ', COALESCE(OLD.tagBarcodeId, 'n/a'), ' → ', COALESCE(NEW.tagBarcodeId, 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END)));

CREATE TRIGGER PlateInsert AFTER INSERT ON Plate
FOR EACH ROW
  INSERT INTO PlateChangeLog(sampleId, columnsChanged, userId, message) VALUES (
    NEW.plateId,
    '',
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' created plate.'));

CREATE TRIGGER RunChange BEFORE UPDATE ON Run
FOR EACH ROW
  INSERT INTO RunChangeLog(runId, columnsChanged, userId, message) VALUES (
    NEW.runId,
    COALESCE(CONCAT_WS(',',
      CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN 'accession' END,
      CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
      CASE WHEN (NEW.cycles IS NULL) <> (OLD.cycles IS NULL) OR NEW.cycles <> OLD.cycles THEN 'cycles' END,
      CASE WHEN NEW.description <> OLD.description THEN 'description' END,
      CASE WHEN (NEW.filePath IS NULL) <> (OLD.filePath IS NULL) OR NEW.filePath <> OLD.filePath THEN 'filePath' END,
      CASE WHEN NEW.name <> OLD.name THEN 'name' END,
      CASE WHEN NEW.pairedend <> OLD.pairedend THEN 'pairedend' END,
      CASE WHEN (NEW.platformRunId IS NULL) <> (OLD.platformRunId IS NULL) OR NEW.platformRunId <> OLD.platformRunId THEN 'platformRunId' END,
      CASE WHEN (NEW.sequencerReference_sequencerReferenceId IS NULL) <> (OLD.sequencerReference_sequencerReferenceId IS NULL) OR NEW.sequencerReference_sequencerReferenceId <> OLD.sequencerReference_sequencerReferenceId THEN 'sequencerReference_sequencerReferenceId' END,
      CASE WHEN (NEW.status_statusId IS NULL) <> (OLD.status_statusId IS NULL) OR NEW.status_statusId <> OLD.status_statusId THEN 'status_statusId' END), ''),
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' has changed: ',
      CONCAT_WS(', ',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN (NEW.cycles IS NULL) <> (OLD.cycles IS NULL) OR NEW.cycles <> OLD.cycles THEN CONCAT('cycles: ', COALESCE(OLD.cycles, 'n/a'), ' → ', COALESCE(NEW.cycles, 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN (NEW.filePath IS NULL) <> (OLD.filePath IS NULL) OR NEW.filePath <> OLD.filePath THEN CONCAT('file path: ', COALESCE(OLD.filePath, 'n/a'), ' → ', COALESCE(NEW.filePath, 'n/a')) END,
        CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN CONCAT('ends: ', CASE WHEN OLD.pairedEnd THEN 'paired' ELSE 'single' END, ' → ', CASE WHEN NEW.pairedEnd THEN 'paired' ELSE 'single' END) END,
        CASE WHEN (NEW.platformRunId IS NULL) <> (OLD.platformRunId IS NULL) OR NEW.platformRunId <> OLD.platformRunId THEN CONCAT('platform-run: ', COALESCE(OLD.platformRunId, 'n/a'), ' → ', COALESCE(NEW.platformRunId, 'n/a')) END,
        CASE WHEN NEW.platformType <> OLD.platformType THEN CONCAT('platform-type: ', OLD.platformType, ' → ', NEW.platformType) END,
        CASE WHEN (NEW.sequencerReference_sequencerReferenceId IS NULL) <> (OLD.sequencerReference_sequencerReferenceId IS NULL) OR NEW.sequencerReference_sequencerReferenceId <> OLD.sequencerReference_sequencerReferenceId THEN CONCAT('sequencer: ', COALESCE((SELECT name FROM SequencerReference WHERE referenceId = OLD.sequencerReference_sequencerReferenceId), 'n/a'), ' → ', COALESCE((SELECT name FROM SequencerReference WHERE referenceId = NEW.sequencerReference_sequencerReferenceId), 'n/a')) END,
        CASE WHEN (NEW.status_statusId IS NULL) <> (OLD.status_statusId IS NULL) OR NEW.status_statusId <> OLD.status_statusId THEN 'status' END)));

CREATE TRIGGER RunInsert AFTER INSERT ON Run
FOR EACH ROW
  INSERT INTO RunChangeLog(runId, columnsChanged, userId, message) VALUES (
    NEW.runId,
    '',
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' created run.'));

CREATE TRIGGER PoolChange BEFORE UPDATE ON Pool
FOR EACH ROW
  INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
    NEW.poolId,
    COALESCE(CONCAT_WS(',',
      CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
      CASE WHEN NEW.concentration <> OLD.concentration THEN 'concentration' END,
      CASE WHEN (NEW.experiment_experimentId IS NULL) <> (OLD.experiment_experimentId IS NULL) OR NEW.experiment_experimentId <> OLD.experiment_experimentId THEN 'experiment_experimentId' END,
      CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
      CASE WHEN NEW.name <> OLD.name THEN 'name' END,
      CASE WHEN NEW.platformType <> OLD.platformType THEN 'platformType' END,
      CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN 'qcPassed' END,
      CASE WHEN NEW.ready <> OLD.ready THEN 'ready' END), ''),
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' has changed: ',
      CONCAT_WS(', ',
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN NEW.concentration <> OLD.concentration THEN CONCAT('concentration: ', OLD.concentration, ' → ', NEW.concentration) END,
        CASE WHEN (NEW.experiment_experimentId IS NULL) <> (OLD.experiment_experimentId IS NULL) OR NEW.experiment_experimentId <> OLD.experiment_experimentId THEN CONCAT('experiment: ', COALESCE((SELECT name FROM Experiment WHERE experimentId = OLD.experiment_experimentId), 'n/a'), ' → ', COALESCE((SELECT name FROM Experiment WHERE experimentId = NEW.experiment_experimentId), 'n/a')) END,
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification-barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
        CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.platformType <> OLD.platformType THEN CONCAT('platform-type: ', OLD.platformType, ' → ', NEW.platformType) END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN CONCAT('QC passed: ', COALESCE(OLD.qcPassed, 'n/a'), ' → ', COALESCE(NEW.qcPassed, 'n/a')) END,
        CASE WHEN NEW.ready <> OLD.ready THEN CONCAT('ready: ', OLD.ready, ' → ', NEW.ready) END)));

CREATE TRIGGER PoolInsert AFTER INSERT ON Pool
FOR EACH ROW
  INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
    NEW.poolId,
    '',
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' created pool.'));

CREATE TRIGGER ExperimentChange BEFORE UPDATE ON Experiment
FOR EACH ROW
  INSERT INTO ExperimentChangeLog(experimentId, columnsChanged, userId, message) VALUES (
    NEW.experimentId,
    COALESCE(CONCAT_WS(',',
      CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN 'accession' END,
      CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
      CASE WHEN NEW.description <> OLD.description THEN 'description' END,
      CASE WHEN NEW.name <> OLD.name THEN 'name' END,
      CASE WHEN NEW.platform_platformId <> OLD.platform_platformId THEN 'platform_platformId' END,
      CASE WHEN (NEW.study_studyId IS NULL) <> (OLD.study_studyId IS NULL) OR NEW.study_studyId <> OLD.study_studyId THEN 'study_studyId' END,
      CASE WHEN NEW.title <> OLD.title THEN 'title' END), ''),
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' has changed: ',
      CONCAT_WS(', ',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.platform_platformId <> OLD.platform_platformId THEN CONCAT('platform: ', (SELECT name FROM Platform WHERE platformId = OLD.platform_platformId), ' → ', (SELECT name FROM Platform WHERE platformId = NEW.platform_platformId)) END,
        CASE WHEN (NEW.study_studyId IS NULL) <> (OLD.study_studyId IS NULL) OR NEW.study_studyId <> OLD.study_studyId THEN CONCAT('study: ', COALESCE((SELECT name FROM Study WHERE studyId = OLD.study_studyId), 'n/a'), ' → ', COALESCE((SELECT name FROM Study WHERE studyId = NEW.study_studyId), 'n/a')) END,
        CASE WHEN NEW.title <> OLD.title THEN CONCAT('title: ', OLD.title, ' → ', NEW.title) END)));

CREATE TRIGGER ExperimentInsert AFTER INSERT ON Experiment
FOR EACH ROW
  INSERT INTO ExperimentChangeLog(experimentId, columnsChanged, userId, message) VALUES (
    NEW.experimentId,
    '',
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' created experiment.'));

CREATE TRIGGER LibraryChange BEFORE UPDATE ON Library
FOR EACH ROW
  INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message) VALUES (
    NEW.libraryId,
    COALESCE(CONCAT_WS(',',
      CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN 'accession' END,
      CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
      CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN 'concentration' END,
      CASE WHEN NEW.description <> OLD.description THEN 'description' END,
      CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
      CASE WHEN NEW.librarySelectionType <> OLD.librarySelectionType THEN 'librarySelectionType' END,
      CASE WHEN (NEW.libraryStrategyType IS NULL) <> (OLD.libraryStrategyType IS NULL) OR NEW.libraryStrategyType <> OLD.libraryStrategyType THEN 'libraryStrategyType' END,
      CASE WHEN (NEW.libraryType IS NULL) <> (OLD.libraryType IS NULL) OR NEW.libraryType <> OLD.libraryType THEN 'libraryType' END,
      CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN 'locationBarcode' END,
      CASE WHEN NEW.name <> OLD.name THEN 'name' END,
      CASE WHEN NEW.paired <> OLD.paired THEN 'paired' END,
      CASE WHEN (NEW.platformName IS NULL) <> (OLD.platformName IS NULL) OR NEW.platformName <> OLD.platformName THEN 'platformName' END,
      CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN 'qcPassed' END,
      CASE WHEN NEW.sample_sampleId <> OLD.sample_sampleId THEN 'sample_sampleId' END,
      CASE WHEN NEW.emptied <> OLD.emptied THEN 'emptied' END,
      CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN 'volume' END
), ''),
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' has changed: ',
      CONCAT_WS(', ',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN CONCAT('concentration: ', COALESCE(OLD.concentration, 'n/a'), ' → ', COALESCE(NEW.concentration, 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
        CASE WHEN (NEW.librarySelectionType IS NULL) <> (OLD.librarySelectionType IS NULL) OR NEW.librarySelectionType <> OLD.librarySelectionType THEN CONCAT('slection: ', COALESCE((SELECT name FROM LibrarySelectionType WHERE librarySelectionTypeId = OLD.librarySelectionType), 'n/a'), ' → ', COALESCE((SELECT name FROM LibrarySelectionType WHERE librarySelectionTypeId = NEW.librarySelectionType), 'n/a')) END,
        CASE WHEN (NEW.libraryStrategyType IS NULL) <> (OLD.libraryStrategyType IS NULL) OR NEW.libraryStrategyType <> OLD.libraryStrategyType THEN CONCAT('strategy: ', COALESCE((SELECT name FROM LibraryStrategyType WHERE libraryStrategyTypeId = OLD.libraryStrategyType), 'n/a'), ' → ', COALESCE((SELECT name FROM LibraryStrategyType WHERE libraryStrategyTypeId = NEW.libraryStrategyType), 'n/a')) END,
        CASE WHEN (NEW.libraryType IS NULL) <> (OLD.libraryType IS NULL) OR NEW.libraryType <> OLD.libraryType THEN CONCAT('type: ', COALESCE((SELECT description FROM LibraryType WHERE libraryTypeId = OLD.libraryType), 'n/a'), ' → ', COALESCE((SELECT description FROM LibraryType WHERE libraryTypeId = NEW.libraryType), 'n/a')) END,
        CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN CONCAT('location barcode: ', COALESCE(OLD.locationBarcode, 'n/a'), ' → ', COALESCE(NEW.locationBarcode, 'n/a')) END,
        CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.paired <> OLD.paired THEN CONCAT('end: ', CASE WHEN OLD.paired THEN 'paired' ELSE 'singled' END, ' → ', CASE WHEN NEW.paired THEN 'paired' ELSE 'single' END) END,
        CASE WHEN (NEW.platformName IS NULL) <> (OLD.platformName IS NULL) OR NEW.platformName <> OLD.platformName THEN CONCAT('platform: ', COALESCE(OLD.platformName, 'n/a'), ' → ', COALESCE(NEW.platformName, 'n/a')) END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN CONCAT('QC passed: ', COALESCE(OLD.qcPassed, 'n/a'), ' → ', COALESCE(NEW.qcPassed, 'n/a')) END,
        CASE WHEN NEW.emptied <> OLD.emptied THEN CONCAT('emptied: ', OLD.emptied, ' → ', NEW.emptied) END,
        CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN CONCAT('volume: ', COALESCE(OLD.volume, 'n/a'), ' → ', COALESCE(NEW.volume, 'n/a')) END)));

CREATE TRIGGER BeforeInsertLibrary BEFORE INSERT ON Library
  FOR EACH ROW
  SET NEW.boxPositionId = nextval('box_position_seq');


CREATE TRIGGER LibraryInsert AFTER INSERT ON Library
FOR EACH ROW
  INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message) VALUES (
    NEW.libraryId,
    '',
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' created library.'));

CREATE TRIGGER StudyChange BEFORE UPDATE ON Study
FOR EACH ROW
  INSERT INTO StudyChangeLog(studyId, columnsChanged, userId, message) VALUES (
    NEW.studyId,
    COALESCE(CONCAT_WS(',',
      CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN 'accession' END,
      CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
      CASE WHEN NEW.description <> OLD.description THEN 'description' END,
      CASE WHEN NEW.name <> OLD.name THEN 'name' END,
      CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN 'project_projectId' END,
      CASE WHEN NEW.studyType <> OLD.studyType THEN 'studyType' END), ''),
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' has changed: ',
      CONCAT_WS(', ',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN CONCAT('project: ', COALESCE((SELECT name FROM Project WHERE projectId = OLD.project_projectId), 'n/a'), ' → ', COALESCE((SELECT name FROM Project WHERE projectId = NEW.project_projectId), 'n/a')) END,
        CASE WHEN NEW.studyType <> OLD.studyType THEN CONCAT('type: ', COALESCE((SELECT name FROM StudyType WHERE typeId = OLD.studyType), 'n/a'), ' → ', COALESCE((SELECT name FROM StudyType WHERE typeId = NEW.studyType), 'n/a')) END)));

CREATE TRIGGER StudyInsert AFTER INSERT ON Study
FOR EACH ROW
  INSERT INTO StudyChangeLog(studyId, columnsChanged, userId, message) VALUES (
    NEW.studyId,
    '',
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' created study.'));


CREATE TRIGGER SequencerPartitionContainerChange BEFORE UPDATE ON SequencerPartitionContainer
FOR EACH ROW
  INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message) VALUES (
    NEW.containerId,
    COALESCE(CONCAT_WS(',',
      CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
      CASE WHEN NEW.locationBarcode <> OLD.locationBarcode THEN 'locationBarcode' END,
      CASE WHEN (NEW.platform IS NULL) <> (OLD.platform IS NULL) OR NEW.platform <> OLD.platform THEN 'platform' END,
      CASE WHEN (NEW.validationBarcode IS NULL) <> (OLD.validationBarcode IS NULL) OR NEW.validationBarcode <> OLD.validationBarcode THEN 'validationBarcode' END), ''),
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' has changed: ',
      CONCAT_WS(', ',
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
        CASE WHEN NEW.locationBarcode <> OLD.locationBarcode THEN CONCAT('location barcode: ', COALESCE(OLD.locationBarcode, 'n/a'), ' → ', COALESCE(NEW.locationBarcode, 'n/a')) END,
        CASE WHEN (NEW.platform IS NULL) <> (OLD.platform IS NULL) OR NEW.platform <> OLD.platform THEN CONCAT('platform: ', COALESCE(OLD.platform, 'n/a'), ' → ', COALESCE(NEW.platform, 'n/a')) END,
        CASE WHEN (NEW.validationBarcode IS NULL) <> (OLD.validationBarcode IS NULL) OR NEW.validationBarcode <> OLD.validationBarcode THEN CONCAT('validation barcode: ', COALESCE(OLD.validationBarcode, 'n/a'), ' → ', COALESCE(NEW.validationBarcode, 'n/a')) END)));

CREATE TRIGGER SequencerPartitionContainerInsert AFTER INSERT ON SequencerPartitionContainer
FOR EACH ROW
  INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message) VALUES (
    NEW.containerId,
    '',
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' created container.'));

DELIMITER //
CREATE TRIGGER BoxChange BEFORE UPDATE ON Box
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500);
  SET log_message = CONCAT_WS(', ',
    CASE WHEN NEW.alias <> OLD.alias THEN CONCAT('alias: ', OLD.alias, ' → ', NEW.alias) END,
    CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
    CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN CONCAT('location: ', COALESCE(OLD.locationBarcode, 'n/a'), ' → ', COALESCE(NEW.locationBarcode, 'n/a')) END,
    CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN CONCAT('description: ', COALESCE(OLD.description, 'n/a'), ' → ', COALESCE(NEW.description, 'n/a')) END
  );
  IF log_message = '' THEN
    INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message) VALUES (
      New.boxId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.alias <> OLD.alias THEN 'alias' END,
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
        CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN 'locationBarcode' END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN 'description' END), ''),
      NEW.lastModifier,
      CONCAT(
        (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
        ' has changed: ',
        log_message
      )
    );
  END IF;
  END//
DELIMITER ;

CREATE TRIGGER BoxInsert AFTER INSERT ON Box
FOR EACH ROW
  INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message) VALUES (
    NEW.boxId,
    '',
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' created box.')
  );
