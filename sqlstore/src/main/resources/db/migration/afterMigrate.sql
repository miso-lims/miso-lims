-- StartNoTest
DELIMITER //

DROP FUNCTION IF EXISTS `nextval`//

DROP TRIGGER IF EXISTS SampleChange//
CREATE TRIGGER SampleChange BEFORE UPDATE ON Sample
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
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
    CASE WHEN NEW.discarded <> OLD.discarded THEN CONCAT('discarded: ', OLD.discarded, ' → ', NEW.discarded) END,
    CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN CONCAT('volume: ', COALESCE(OLD.volume, 'n/a'), ' → ', COALESCE(NEW.volume, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
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
          CASE WHEN NEW.discarded <> OLD.discarded THEN 'discarded' END,
        CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN 'volume' END
  ), ''),
      NEW.lastModifier,
      log_message
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS SampleAdditionalInfoChange//
DROP TRIGGER IF EXISTS DetailedSampleChange//
CREATE TRIGGER DetailedSampleChange BEFORE UPDATE ON DetailedSample
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
     CASE WHEN NEW.archived <> OLD.archived THEN CONCAT('archived: ', OLD.archived, ' → ', NEW.archived) END,
     CASE WHEN (NEW.groupDescription IS NULL) <> (OLD.groupDescription IS NULL) OR NEW.groupDescription <> OLD.groupDescription THEN CONCAT('group description: ', COALESCE(OLD.groupDescription, 'n/a'), ' → ', COALESCE(NEW.groupDescription, 'n/a')) END,
     CASE WHEN (NEW.groupId IS NULL) <> (OLD.groupId IS NULL) OR NEW.groupId <> OLD.groupId THEN CONCAT('group id: ', COALESCE(OLD.groupId, 'n/a'), ' → ', COALESCE(NEW.groupId, 'n/a')) END,
     CASE WHEN (NEW.parentId IS NULL) <> (OLD.parentId IS NULL) OR NEW.parentId <> OLD.parentId THEN CONCAT('parent: ', (SELECT name FROM Sample WHERE sampleId = OLD.parentId), ' → ', (SELECT name FROM Sample WHERE sampleId = NEW.parentId)) END,
     CASE WHEN (NEW.detailedQcStatusId IS NULL) <> (OLD.detailedQcStatusId IS NULL) OR NEW.detailedQcStatusId <> OLD.detailedQcStatusId THEN CONCAT('QC Status: ', COALESCE((SELECT description FROM DetailedQcStatus WHERE detailedQcStatusId = OLD.detailedQcStatusId), 'n/a'), ' → ', COALESCE((SELECT description FROM DetailedQcStatus WHERE detailedQcStatusId = NEW.detailedQcStatusId), 'n/a')) END,
     CASE WHEN (NEW.detailedQcStatusNote IS NULL) <> (OLD.detailedQcStatusNote IS NULL) OR NEW.detailedQcStatusNote <> OLD.detailedQcStatusNote THEN CONCAT('QC Status Note: ', COALESCE(OLD.detailedQcStatusNote, 'n/a'), ' → ', COALESCE(NEW.detailedQcStatusNote, 'n/a')) END,
     CASE WHEN NEW.sampleClassId <> OLD.sampleClassId THEN CONCAT('class: ', (SELECT alias FROM SampleClass WHERE sampleClassId = OLD.sampleClassId), ' → ', (SELECT alias FROM SampleClass WHERE sampleClassId = NEW.sampleClassId)) END,
     CASE WHEN (NEW.siblingNumber IS NULL) <> (OLD.siblingNumber IS NULL) OR NEW.siblingNumber <> OLD.siblingNumber THEN CONCAT('sibling: ', COALESCE(OLD.siblingNumber, 'n/a'), ' → ', COALESCE(NEW.siblingNumber, 'n/a')) END,
     CASE WHEN (NEW.subprojectId IS NULL) <> (OLD.subprojectId IS NULL) OR NEW.subprojectId <> OLD.subprojectId THEN CONCAT('subproject: ', COALESCE((SELECT alias FROM Subproject WHERE subprojectId = OLD.subprojectId), 'n/a'), ' → ', COALESCE((SELECT alias FROM Subproject WHERE subprojectId = NEW.subprojectId), 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.archived <> OLD.archived THEN 'archived' END,
        CASE WHEN (NEW.groupDescription IS NULL) <> (OLD.groupDescription IS NULL) OR NEW.groupDescription <> OLD.groupDescription THEN 'groupDescription' END,
        CASE WHEN (NEW.groupId IS NULL) <> (OLD.groupId IS NULL) OR NEW.groupId <> OLD.groupId THEN 'groupId' END,
        CASE WHEN (NEW.parentId IS NULL) <> (OLD.parentId IS NULL) OR NEW.parentId <> OLD.parentId THEN 'parentId' END,
        CASE WHEN (NEW.detailedQcStatusId IS NULL) <> (OLD.detailedQcStatusId IS NULL) OR NEW.detailedQcStatusId <> OLD.detailedQcStatusId THEN 'detailedQcStatusId' END,
        CASE WHEN (NEW.detailedQcStatusNote IS NULL) <> (OLD.detailedQcStatusNote IS NULL) OR NEW.detailedQcStatusNote <> OLD.detailedQcStatusNote THEN 'detailedQcStatusNote' END,
        CASE WHEN NEW.sampleClassId <> OLD.sampleClassId THEN 'sampleClassId' END,
        CASE WHEN (NEW.siblingNumber IS NULL) <> (OLD.siblingNumber IS NULL) OR NEW.siblingNumber <> OLD.siblingNumber THEN 'siblingNumber' END,
        CASE WHEN (NEW.subprojectId IS NULL) <> (OLD.subprojectId IS NULL) OR NEW.subprojectId <> OLD.subprojectId THEN 'subprojectId' END
      ), ''),
      (SELECT lastModifier FROM Sample WHERE sampleId = NEW.sampleId),
      log_message
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS SampleAliquotChange//
CREATE TRIGGER SampleAliquotChange BEFORE UPDATE ON SampleAliquot
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
     CASE WHEN (NEW.samplePurposeId IS NULL) <> (OLD.samplePurposeId IS NULL) OR NEW.samplePurposeId <> OLD.samplePurposeId THEN CONCAT('purpose: ', COALESCE((SELECT alias FROM SamplePurpose WHERE samplePurposeId = OLD.samplePurposeId), 'n/a'), ' → ', COALESCE((SELECT alias FROM SamplePurpose WHERE samplePurposeId = NEW.samplePurposeId), 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.samplePurposeId IS NULL) <> (OLD.samplePurposeId IS NULL) OR NEW.samplePurposeId <> OLD.samplePurposeId THEN 'samplePurposeId' END
      ), ''),
      (SELECT lastModifier FROM Sample WHERE sampleId = NEW.sampleId),
      log_message
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS SampleCVSlideChange//
CREATE TRIGGER SampleCVSlideChange BEFORE UPDATE ON SampleCVSlide
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
     CASE WHEN NEW.slides <> OLD.slides THEN CONCAT('slides: ', OLD.slides, ' → ', NEW.slides) END,
     CASE WHEN (NEW.discards IS NULL) <> (OLD.discards IS NULL) OR NEW.discards <> OLD.discards THEN CONCAT('discards: ', COALESCE(OLD.discards, 'n/a'), ' → ', COALESCE(NEW.discards, 'n/a')) END,
     CASE WHEN (NEW.thickness IS NULL) <> (OLD.thickness IS NULL) OR NEW.thickness <> OLD.thickness THEN CONCAT('thickness: ', COALESCE(OLD.thickness, 'n/a'), ' → ', COALESCE(NEW.thickness, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.slides <> OLD.slides THEN 'slides' END,
        CASE WHEN (NEW.discards IS NULL) <> (OLD.discards IS NULL) OR NEW.discards <> OLD.discards THEN 'discards' END,
        CASE WHEN (NEW.thickness IS NULL) <> (OLD.thickness IS NULL) OR NEW.thickness <> OLD.thickness THEN 'thickness' END
      ), ''),
      (SELECT lastModifier FROM Sample WHERE sampleId = NEW.sampleId),
      log_message
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS SampleLCMTubeChange//
CREATE TRIGGER SampleLCMTubeChange BEFORE UPDATE ON SampleLCMTube
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
     CASE WHEN NEW.slidesConsumed <> OLD.slidesConsumed THEN CONCAT('slides: ', OLD.slidesConsumed, ' → ', NEW.slidesConsumed) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.slidesConsumed <> OLD.slidesConsumed THEN 'slides' END
      ), ''),
      (SELECT lastModifier FROM Sample WHERE sampleId = NEW.sampleId),
      log_message
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS SampleStockChange//
CREATE TRIGGER SampleStockChange BEFORE UPDATE ON SampleStock
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN CONCAT('concentration: ', COALESCE(OLD.concentration, 'n/a'), ' → ', COALESCE(NEW.concentration, 'n/a')) END,
    CASE WHEN NEW.strStatus <> OLD.strStatus THEN CONCAT('STR status: ', OLD.strStatus, ' → ', NEW.strStatus) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
         CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN 'concentration' END,
         CASE WHEN NEW.strStatus <> OLD.strStatus THEN 'strStatus' END
      ), ''),
      (SELECT lastModifier FROM Sample WHERE sampleId = NEW.sampleId),
      log_message
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS SampleTissueChange//
CREATE TRIGGER SampleTissueChange BEFORE UPDATE ON SampleTissue
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    CASE WHEN (NEW.externalInstituteIdentifier IS NULL) <> (OLD.externalInstituteIdentifier IS NULL) OR NEW.externalInstituteIdentifier <> OLD.externalInstituteIdentifier THEN CONCAT('external identifier: ', COALESCE(OLD.externalInstituteIdentifier, 'n/a'), ' → ', COALESCE(NEW.externalInstituteIdentifier, 'n/a')) END,
    CASE WHEN (NEW.labId IS NULL) <> (OLD.labId IS NULL) OR NEW.labId <> OLD.labId THEN CONCAT('lab: ', COALESCE((SELECT alias FROM Lab WHERE labId = OLD.labId), 'n/a'), ' → ', COALESCE((SELECT alias FROM Lab WHERE labId = NEW.labId), 'n/a')) END,
    CASE WHEN (NEW.passageNumber IS NULL) <> (OLD.passageNumber IS NULL) OR NEW.passageNumber <> OLD.passageNumber OR (NEW.timesReceived IS NULL) <> (OLD.timesReceived IS NULL) OR NEW.timesReceived <> OLD.timesReceived OR (NEW.tubeNumber IS NULL) <> (OLD.tubeNumber IS NULL) OR NEW.tubeNumber <> OLD.tubeNumber THEN CONCAT('passage: ', COALESCE(OLD.passageNumber, 'n/a'), '-', COALESCE(OLD.timesReceived, 'n/a'), '-', COALESCE(OLD.tubeNumber, 'n/a'), ' → ', COALESCE(NEW.passageNumber, 'n/a'), '-', COALESCE(NEW.timesReceived, 'n/a'), '-', COALESCE(NEW.tubeNumber, 'n/a')) END,
    CASE WHEN (NEW.region IS NULL) <> (OLD.region IS NULL) OR NEW.region <> OLD.region THEN CONCAT('region: ', COALESCE(OLD.region, 'n/a'), ' → ', COALESCE(NEW.region, 'n/a')) END,
    CASE WHEN (NEW.tissueMaterialId IS NULL) <> (OLD.tissueMaterialId IS NULL) OR NEW.tissueMaterialId <> OLD.tissueMaterialId THEN CONCAT('material: ', COALESCE((SELECT alias FROM TissueMaterial WHERE tissueMaterialId = OLD.tissueMaterialId), 'n/a'), ' → ', COALESCE((SELECT alias FROM TissueMaterial WHERE tissueMaterialId = NEW.tissueMaterialId), 'n/a')) END,
    CASE WHEN (NEW.tissueOriginId IS NULL) <> (OLD.tissueOriginId IS NULL) OR NEW.tissueOriginId <> OLD.tissueOriginId THEN CONCAT('origin: ', COALESCE((SELECT alias FROM TissueOrigin WHERE tissueOriginId = OLD.tissueOriginId), 'n/a'), ' → ', COALESCE((SELECT alias FROM TissueOrigin WHERE tissueOriginId = NEW.tissueOriginId), 'n/a')) END,
    CASE WHEN (NEW.tissueTypeId IS NULL) <> (OLD.tissueTypeId IS NULL) OR NEW.tissueTypeId <> OLD.tissueTypeId THEN CONCAT('type: ', COALESCE((SELECT alias FROM TissueType WHERE tissueTypeId = OLD.tissueTypeId), 'n/a'), ' → ', COALESCE((SELECT alias FROM TissueType WHERE tissueTypeId = NEW.tissueTypeId), 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
         CASE WHEN (NEW.externalInstituteIdentifier IS NULL) <> (OLD.externalInstituteIdentifier IS NULL) OR NEW.externalInstituteIdentifier <> OLD.externalInstituteIdentifier THEN 'externalInstituteIdentifier' END,
         CASE WHEN (NEW.labId IS NULL) <> (OLD.labId IS NULL) OR NEW.labId <> OLD.labId THEN 'labId' END,
         CASE WHEN (NEW.passageNumber IS NULL) <> (OLD.passageNumber IS NULL) OR NEW.passageNumber <> OLD.passageNumber THEN 'passageNumber' END,
         CASE WHEN (NEW.region IS NULL) <> (OLD.region IS NULL) OR NEW.region <> OLD.region THEN 'region' END,
         CASE WHEN (NEW.timesReceived IS NULL) <> (OLD.timesReceived IS NULL) OR NEW.timesReceived <> OLD.timesReceived THEN 'timesReceived' END,
         CASE WHEN (NEW.tissueMaterialId IS NULL) <> (OLD.tissueMaterialId IS NULL) OR NEW.tissueMaterialId <> OLD.tissueMaterialId THEN 'tissueMaterialId' END,
         CASE WHEN (NEW.tissueOriginId IS NULL) <> (OLD.tissueOriginId IS NULL) OR NEW.tissueOriginId <> OLD.tissueOriginId THEN 'tissueOriginId' END,
         CASE WHEN (NEW.tissueTypeId IS NULL) <> (OLD.tissueTypeId IS NULL) OR NEW.tissueTypeId <> OLD.tissueTypeId THEN 'tissueTypeId' END,
         CASE WHEN (NEW.tubeNumber IS NULL) <> (OLD.tubeNumber IS NULL) OR NEW.tubeNumber <> OLD.tubeNumber THEN 'tubeNumber' END
      ), ''),
      (SELECT lastModifier FROM Sample WHERE sampleId = NEW.sampleId),
      log_message
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS IdentityChange//
CREATE TRIGGER IdentityChange BEFORE UPDATE ON Identity
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    CASE WHEN NEW.donorSex <> OLD.donorSex THEN CONCAT('donor sex: ', OLD.donorSex, ' → ', NEW.donorSex) END,
    CASE WHEN NEW.externalName <> OLD.externalName THEN CONCAT('externalName: ', OLD.externalName, ' → ', NEW.externalName) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.donorSex <> OLD.donorSex THEN 'donorSex' END,
        CASE WHEN NEW.externalName <> OLD.externalName THEN 'externalName' END
      ), ''),
      (SELECT lastModifier FROM Sample WHERE sampleId = NEW.sampleId),
      log_message
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS BeforeInsertSample//

DROP TRIGGER IF EXISTS SampleInsert//
CREATE TRIGGER SampleInsert AFTER INSERT ON Sample
FOR EACH ROW
  INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
    NEW.sampleId,
    '',
    NEW.lastModifier,
    'Sample created.')//

DROP TRIGGER IF EXISTS PlateChange//
DROP TRIGGER IF EXISTS PlateInsert//

DROP TRIGGER IF EXISTS RunChange//
CREATE TRIGGER RunChange BEFORE UPDATE ON Run
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN (NEW.completionDate IS NULL) <> (OLD.completionDate IS NULL) OR NEW.completionDate <> OLD.completionDate THEN CONCAT('completion: ', COALESCE(OLD.completionDate, 'n/a'), ' → ', COALESCE(NEW.completionDate, 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN (NEW.filePath IS NULL) <> (OLD.filePath IS NULL) OR NEW.filePath <> OLD.filePath THEN CONCAT('file path: ', COALESCE(OLD.filePath, 'n/a'), ' → ', COALESCE(NEW.filePath, 'n/a')) END,
        CASE WHEN NEW.health <> OLD.health THEN CONCAT('health: ', COALESCE(OLD.health, 'n/a'), ' → ', COALESCE(NEW.health, 'n/a')) END,
        CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN CONCAT('ends: ', CASE WHEN OLD.pairedEnd THEN 'paired' ELSE 'single' END, ' → ', CASE WHEN NEW.pairedEnd THEN 'paired' ELSE 'single' END,
        CASE WHEN (NEW.startDate IS NULL) <> (OLD.startDate IS NULL) OR NEW.startDate <> OLD.startDate THEN CONCAT('startDate: ', COALESCE(OLD.startDate, 'n/a'), ' → ', COALESCE(NEW.startDate, 'n/a')) END) END,
        CASE WHEN (NEW.sequencerReference_sequencerReferenceId IS NULL) <> (OLD.sequencerReference_sequencerReferenceId IS NULL) OR NEW.sequencerReference_sequencerReferenceId <> OLD.sequencerReference_sequencerReferenceId THEN CONCAT('sequencer: ', COALESCE((SELECT name FROM SequencerReference WHERE referenceId = OLD.sequencerReference_sequencerReferenceId), 'n/a'), ' → ', COALESCE((SELECT name FROM SequencerReference WHERE referenceId = NEW.sequencerReference_sequencerReferenceId), 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message) VALUES (
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN 'accession' END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
        CASE WHEN (NEW.completionDate IS NULL) <> (OLD.completionDate IS NULL) OR NEW.completionDate <> OLD.completionDate THEN 'completionDate' END,
        CASE WHEN NEW.description <> OLD.description THEN 'description' END,
        CASE WHEN (NEW.filePath IS NULL) <> (OLD.filePath IS NULL) OR NEW.filePath <> OLD.filePath THEN 'filePath' END,
        CASE WHEN (NEW.health IS NULL) <> (OLD.health IS NULL) OR NEW.health <> OLD.health THEN 'health' END,
        CASE WHEN (NEW.metrics IS NULL) <> (OLD.metrics IS NULL) OR NEW.metrics <> OLD.metrics THEN 'metrics' END,
        CASE WHEN NEW.name <> OLD.name THEN 'name' END,
        CASE WHEN NEW.pairedend <> OLD.pairedend THEN 'pairedend' END,
        CASE WHEN (NEW.startDate IS NULL) <> (OLD.startDate IS NULL) OR NEW.startDate <> OLD.startDate THEN 'startDate' END,
        CASE WHEN (NEW.sequencerReference_sequencerReferenceId IS NULL) <> (OLD.sequencerReference_sequencerReferenceId IS NULL) OR NEW.sequencerReference_sequencerReferenceId <> OLD.sequencerReference_sequencerReferenceId THEN 'sequencerReference_sequencerReferenceId' END), ''),
      NEW.lastModifier,
      log_message);
  END IF;
  END//

DROP TRIGGER IF EXISTS RunChangeLS454//
CREATE TRIGGER RunChangeLS454 BEFORE UPDATE ON RunLS454
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.cycles IS NULL) <> (OLD.cycles IS NULL) OR NEW.cycles <> OLD.cycles THEN CONCAT('cycles: ', COALESCE(OLD.cycles, 'n/a'), ' → ', COALESCE(NEW.cycles, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message) VALUES (
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.cycles IS NULL) <> (OLD.cycles IS NULL) OR NEW.cycles <> OLD.cycles THEN 'cycles' END), ''),
      (SELECT lastModifier FROM Run WHERE Run.runId = NEW.runId),
      log_message);
  END IF;
  END//

DROP TRIGGER IF EXISTS RunChangePacBio//
CREATE TRIGGER RunChangePacBio BEFORE UPDATE ON RunPacBio
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.movieDuration IS NULL) <> (OLD.movieDuration IS NULL) OR NEW.movieDuration <> OLD.movieDuration THEN CONCAT('movie duration: ', COALESCE(OLD.movieDuration, 'n/a'), ' → ', COALESCE(NEW.movieDuration, 'n/a')) END,
        CASE WHEN (NEW.wellName IS NULL) <> (OLD.wellName IS NULL) OR NEW.wellName <> OLD.wellName THEN CONCAT('well: ', COALESCE(OLD.wellName, 'n/a'), ' → ', COALESCE(NEW.wellName, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message) VALUES (
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.movieDuration IS NULL) <> (OLD.movieDuration IS NULL) OR NEW.movieDuration <> OLD.movieDuration THEN 'movieDuration' END,
        CASE WHEN (NEW.wellName IS NULL) <> (OLD.wellName IS NULL) OR NEW.wellName <> OLD.wellName THEN 'wellName' END), ''),
      (SELECT lastModifier FROM Run WHERE Run.runId = NEW.runId),
      log_message);
  END IF;
  END//

DROP TRIGGER IF EXISTS RunChangeIllumina//
CREATE TRIGGER RunChangeIllumina BEFORE UPDATE ON RunIllumina
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.callCycle IS NULL) <> (OLD.callCycle IS NULL) OR NEW.callCycle <> OLD.callCycle THEN CONCAT('call cycles: ', COALESCE(OLD.callCycle, 'n/a'), ' → ', COALESCE(NEW.callCycle, 'n/a')) END,
        CASE WHEN (NEW.imgCycle IS NULL) <> (OLD.imgCycle IS NULL) OR NEW.imgCycle <> OLD.imgCycle THEN CONCAT('image cycles: ', COALESCE(OLD.imgCycle, 'n/a'), ' → ', COALESCE(NEW.imgCycle, 'n/a')) END,
        CASE WHEN (NEW.numCycles IS NULL) <> (OLD.numCycles IS NULL) OR NEW.numCycles <> OLD.numCycles THEN CONCAT('number of cycles: ', COALESCE(OLD.numCycles, 'n/a'), ' → ', COALESCE(NEW.numCycles, 'n/a')) END,
        CASE WHEN (NEW.scoreCycle IS NULL) <> (OLD.scoreCycle IS NULL) OR NEW.scoreCycle <> OLD.scoreCycle THEN CONCAT('scoring cycles: ', COALESCE(OLD.scoreCycle, 'n/a'), ' → ', COALESCE(NEW.scoreCycle, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message) VALUES (
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.callCycle IS NULL) <> (OLD.callCycle IS NULL) OR NEW.callCycle <> OLD.callCycle THEN 'callCycle' END,
        CASE WHEN (NEW.imgCycle IS NULL) <> (OLD.imgCycle IS NULL) OR NEW.imgCycle <> OLD.imgCycle THEN 'imgCycle' END,
        CASE WHEN (NEW.numCycles IS NULL) <> (OLD.numCycles IS NULL) OR NEW.numCycles <> OLD.numCycles THEN 'numCycles' END,
        CASE WHEN (NEW.scoreCycle IS NULL) <> (OLD.scoreCycle IS NULL) OR NEW.scoreCycle <> OLD.scoreCycle THEN 'scoreCycle' END), ''),
      (SELECT lastModifier FROM Run WHERE Run.runId = NEW.runId),
      log_message);
  END IF;
  END//

DROP TRIGGER IF EXISTS RunInsert//
CREATE TRIGGER RunInsert AFTER INSERT ON Run
FOR EACH ROW
  INSERT INTO RunChangeLog(runId, columnsChanged, userId, message) VALUES (
    NEW.runId,
    '',
    NEW.lastModifier,
    'Run created.')//
    
DROP TRIGGER IF EXISTS StatusChange//

DROP TRIGGER IF EXISTS BeforeInsertPool//

DROP TRIGGER IF EXISTS PoolChange//
CREATE TRIGGER PoolChange BEFORE UPDATE ON Pool
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN NEW.concentration <> OLD.concentration THEN CONCAT('concentration: ', OLD.concentration, ' → ', NEW.concentration) END,
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification-barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
        CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.platformType <> OLD.platformType THEN CONCAT('platform-type: ', OLD.platformType, ' → ', NEW.platformType) END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN CONCAT('QC passed: ', COALESCE(OLD.qcPassed, 'n/a'), ' → ', COALESCE(NEW.qcPassed, 'n/a')) END,
        CASE WHEN NEW.ready <> OLD.ready THEN CONCAT('ready: ', OLD.ready, ' → ', NEW.ready) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
      NEW.poolId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
        CASE WHEN NEW.concentration <> OLD.concentration THEN 'concentration' END,
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
        CASE WHEN NEW.name <> OLD.name THEN 'name' END,
        CASE WHEN NEW.platformType <> OLD.platformType THEN 'platformType' END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN 'qcPassed' END,
        CASE WHEN NEW.ready <> OLD.ready THEN 'ready' END), ''),
      NEW.lastModifier,
      log_message);
  END IF;
  END//

DROP TRIGGER IF EXISTS PoolInsert//
CREATE TRIGGER PoolInsert AFTER INSERT ON Pool
FOR EACH ROW
  INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
    NEW.poolId,
    '',
    NEW.lastModifier,
    'Pool created.')//

DROP TRIGGER IF EXISTS PoolOrderInsert//
CREATE TRIGGER PoolOrderInsert AFTER INSERT ON PoolOrder
FOR EACH ROW
  INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
    NEW.poolId,
    '',
    NEW.updatedBy,
    CONCAT(
      'New order: ',
      NEW.partitions,
      ' of ',
      COALESCE((SELECT CONCAT(Platform.instrumentModel, ' ', SequencingParameters.name) FROM SequencingParameters JOIN Platform ON SequencingParameters.platformId = Platform.platformId WHERE SequencingParameters.parametersId = NEW.parametersId), 'n/a')))//

DROP TRIGGER IF EXISTS PoolOrderDelete//
CREATE TRIGGER PoolOrderDelete AFTER DELETE ON PoolOrder
FOR EACH ROW
  INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
    OLD.poolId,
    '',
    OLD.updatedBy,
    CONCAT(
      'Removed order: ',
      OLD.partitions,
      ' of ',
      COALESCE((SELECT CONCAT(Platform.instrumentModel, ' ', SequencingParameters.name) FROM SequencingParameters JOIN Platform ON SequencingParameters.platformId = Platform.platformId WHERE SequencingParameters.parametersId = OLD.parametersId), 'n/a')))//

DROP TRIGGER IF EXISTS PoolOrderChange//
CREATE TRIGGER PoolOrderChange BEFORE UPDATE ON PoolOrder
FOR EACH ROW
  BEGIN
  IF NEW.partitions <> OLD.partitions OR (NEW.parametersId IS NULL) <> (OLD.parametersId IS NULL) OR NEW.parametersId <> OLD.parametersId THEN
    INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
      NEW.poolId,
      '',
      NEW.updatedBy,
      CONCAT(
        'Changed order: ',
        OLD.partitions,
        ' of ',
        COALESCE((SELECT CONCAT(Platform.instrumentModel, ' ', SequencingParameters.name) FROM SequencingParameters JOIN Platform ON SequencingParameters.platformId = Platform.platformId WHERE SequencingParameters.parametersId = OLD.parametersId), 'n/a'),
        ' → ',
        NEW.partitions,
        ' of ',
        COALESCE((SELECT CONCAT(Platform.instrumentModel, ' ', SequencingParameters.name) FROM SequencingParameters JOIN Platform ON SequencingParameters.platformId = Platform.platformId WHERE SequencingParameters.parametersId = NEW.parametersId), 'n/a')));
  END IF;
  END//

DROP TRIGGER IF EXISTS ExperimentChange//
CREATE TRIGGER ExperimentChange BEFORE UPDATE ON Experiment
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.platform_platformId <> OLD.platform_platformId THEN CONCAT('platform: ', (SELECT name FROM Platform WHERE platformId = OLD.platform_platformId), ' → ', (SELECT name FROM Platform WHERE platformId = NEW.platform_platformId)) END,
        CASE WHEN (NEW.study_studyId IS NULL) <> (OLD.study_studyId IS NULL) OR NEW.study_studyId <> OLD.study_studyId THEN CONCAT('study: ', COALESCE((SELECT name FROM Study WHERE studyId = OLD.study_studyId), 'n/a'), ' → ', COALESCE((SELECT name FROM Study WHERE studyId = NEW.study_studyId), 'n/a')) END,
        CASE WHEN NEW.title <> OLD.title THEN CONCAT('title: ', OLD.title, ' → ', NEW.title) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
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
      log_message);
  END IF;
  END//

DROP TRIGGER IF EXISTS ExperimentInsert//
CREATE TRIGGER ExperimentInsert AFTER INSERT ON Experiment
FOR EACH ROW
  INSERT INTO ExperimentChangeLog(experimentId, columnsChanged, userId, message) VALUES (
    NEW.experimentId,
    '',
    NEW.lastModifier,
    'Experiment created.')//

DROP TRIGGER IF EXISTS LibraryChange//
CREATE TRIGGER LibraryChange BEFORE UPDATE ON Library
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
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
        CASE WHEN (NEW.platformType IS NULL) <> (OLD.platformType IS NULL) OR NEW.platformType <> OLD.platformType THEN CONCAT('platform: ', COALESCE(OLD.platformType, 'n/a'), ' → ', COALESCE(NEW.platformType, 'n/a')) END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN CONCAT('QC passed: ', COALESCE(OLD.qcPassed, 'n/a'), ' → ', COALESCE(NEW.qcPassed, 'n/a')) END,
        CASE WHEN NEW.discarded <> OLD.discarded THEN CONCAT('discarded: ', OLD.discarded, ' → ', NEW.discarded) END,
        CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN CONCAT('volume: ', COALESCE(OLD.volume, 'n/a'), ' → ', COALESCE(NEW.volume, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
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
        CASE WHEN (NEW.platformType IS NULL) <> (OLD.platformType IS NULL) OR NEW.platformType <> OLD.platformType THEN 'platformType' END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN 'qcPassed' END,
        CASE WHEN NEW.sample_sampleId <> OLD.sample_sampleId THEN 'sample_sampleId' END,
        CASE WHEN NEW.discarded <> OLD.discarded THEN 'discarded' END,
        CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN 'volume' END
  ), ''),
      NEW.lastModifier,
      log_message
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS LibraryAdditionalInfoChange//
DROP TRIGGER IF EXISTS DetailedLibraryChange//
CREATE TRIGGER DetailedLibraryChange BEFORE UPDATE ON DetailedLibrary
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
     CASE WHEN NEW.archived <> OLD.archived THEN CONCAT('archived: ', OLD.archived, ' → ', NEW.archived) END,
     CASE WHEN (NEW.kitDescriptorId IS NULL) <> (OLD.kitDescriptorId IS NULL) OR NEW.kitDescriptorId <> OLD.kitDescriptorId THEN CONCAT('kit: ', COALESCE((SELECT name FROM KitDescriptor WHERE kitDescriptorId = OLD.kitDescriptorId), 'n/a'), ' → ', COALESCE((SELECT name FROM KitDescriptor WHERE kitDescriptorId = NEW.kitDescriptorId), 'n/a')) END,
     CASE WHEN NEW.libraryDesignCodeId <> OLD.libraryDesignCodeId THEN CONCAT('designCode: ', (SELECT code FROM LibraryDesignCode WHERE libraryDesignCodeId = OLD.libraryDesignCodeId), ' → ', (SELECT code FROM LibraryDesignCode WHERE libraryDesignCodeId = NEW.libraryDesignCodeId)) END,
     CASE WHEN (NEW.libraryDesign IS NULL) <> (OLD.libraryDesign IS NULL) OR NEW.libraryDesign <> OLD.libraryDesign THEN CONCAT('library design: ', COALESCE((SELECT name FROM LibraryDesign WHERE libraryDesignId = OLD.libraryDesign), 'n/a'), ' → ', COALESCE((SELECT name FROM LibraryDesign WHERE libraryDesignId = NEW.libraryDesign), 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message) VALUES (
      NEW.libraryId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.archived <> OLD.archived THEN 'archived' END,
        CASE WHEN (NEW.kitDescriptorId IS NULL) <> (OLD.kitDescriptorId IS NULL) OR NEW.kitDescriptorId <> OLD.kitDescriptorId THEN 'kitDescriptorId' END,
        CASE WHEN (NEW.libraryDesign IS NULL) <> (OLD.libraryDesign IS NULL) OR NEW.libraryDesign <> OLD.libraryDesign THEN 'libraryDesign' END
      ), ''),
      (SELECT lastModifier FROM Library WHERE libraryId = NEW.libraryId),
      log_message
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS LibraryDilutionChange//
CREATE TRIGGER LibraryDilutionChange BEFORE UPDATE ON LibraryDilution
FOR EACH ROW
  BEGIN
    DECLARE log_message varchar(500) CHARACTER SET utf8;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN NEW.concentration <> OLD.concentration THEN CONCAT(NEW.name, ' concentration: ', OLD.concentration, ' → ', NEW.concentration) END,
      CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT(NEW.name, ' barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
      CASE WHEN (NEW.targetedSequencingId IS NULL) <> (OLD.targetedSequencingId IS NULL) OR NEW.targetedSequencingId <> OLD.targetedSequencingId THEN CONCAT(NEW.name, ' targeted sequencing: ', COALESCE((SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = OLD.targetedSequencingId), 'n/a'), ' → ', COALESCE((SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = NEW.targetedSequencingId), 'n/a')) END);
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message) VALUES (
      NEW.library_libraryId,
        COALESCE(CONCAT_WS(',',
          CASE WHEN NEW.concentration <> OLD.concentration THEN CONCAT(NEW.name, ' concentration') END,
          CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT(NEW.name, ' identificationBarcode') END,
          CASE WHEN (NEW.targetedSequencingId IS NULL) <> (OLD.targetedSequencingId IS NULL) OR NEW.targetedSequencingId <> OLD.targetedSequencingId THEN CONCAT(NEW.name, ' targetedSequencingId') END
        ), ''),
        (SELECT lastModifier FROM Library WHERE libraryId = NEW.library_libraryId),
        log_message
      );
    END IF;
  END//

DROP TRIGGER IF EXISTS LibraryDilutionInsert//
CREATE TRIGGER LibraryDilutionInsert AFTER INSERT ON LibraryDilution
FOR EACH ROW
  INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message) VALUES (
    NEW.library_libraryId,
    '',
    (SELECT lastModifier FROM Library WHERE libraryId = NEW.library_libraryId),
    CONCAT('Library dilution ', NEW.name, ' created.'))//

DROP TRIGGER IF EXISTS BeforeInsertLibrary//

DROP TRIGGER IF EXISTS LibraryInsert//
CREATE TRIGGER LibraryInsert AFTER INSERT ON Library
FOR EACH ROW
  INSERT INTO LibraryChangeLog(libraryId, columnsChanged, userId, message) VALUES (
    NEW.libraryId,
    '',
    NEW.lastModifier,
    'Library created.')//

DROP TRIGGER IF EXISTS StudyChange//
CREATE TRIGGER StudyChange BEFORE UPDATE ON Study
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN CONCAT('project: ', COALESCE((SELECT name FROM Project WHERE projectId = OLD.project_projectId), 'n/a'), ' → ', COALESCE((SELECT name FROM Project WHERE projectId = NEW.project_projectId), 'n/a')) END,
        CASE WHEN NEW.studyTypeId <> OLD.studyTypeId THEN CONCAT('type: ', COALESCE((SELECT name FROM StudyType WHERE typeId = OLD.studyTypeId), 'n/a'), ' → ', COALESCE((SELECT name FROM StudyType WHERE typeId = NEW.studyTypeId), 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO StudyChangeLog(studyId, columnsChanged, userId, message) VALUES (
      NEW.studyId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN 'accession' END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
        CASE WHEN NEW.description <> OLD.description THEN 'description' END,
        CASE WHEN NEW.name <> OLD.name THEN 'name' END,
        CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN 'project_projectId' END,
        CASE WHEN NEW.studyTypeId <> OLD.studyTypeId THEN 'studyTypeId' END), ''),
      NEW.lastModifier,
      log_message
      );
  END IF;
  END//

DROP TRIGGER IF EXISTS StudyInsert//
CREATE TRIGGER StudyInsert AFTER INSERT ON Study
FOR EACH ROW
  INSERT INTO StudyChangeLog(studyId, columnsChanged, userId, message) VALUES (
    NEW.studyId,
    '',
    NEW.lastModifier,
    'Study created.')//


DROP TRIGGER IF EXISTS SequencerPartitionContainerChange//
CREATE TRIGGER SequencerPartitionContainerChange BEFORE UPDATE ON SequencerPartitionContainer
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
        CASE WHEN NEW.locationBarcode <> OLD.locationBarcode THEN CONCAT('location barcode: ', COALESCE(OLD.locationBarcode, 'n/a'), ' → ', COALESCE(NEW.locationBarcode, 'n/a')) END,
        CASE WHEN (NEW.platform IS NULL) <> (OLD.platform IS NULL) OR NEW.platform <> OLD.platform THEN CONCAT('platform: ', COALESCE(OLD.platform, 'n/a'), ' → ', COALESCE(NEW.platform, 'n/a')) END,
        CASE WHEN (NEW.validationBarcode IS NULL) <> (OLD.validationBarcode IS NULL) OR NEW.validationBarcode <> OLD.validationBarcode THEN CONCAT('validation barcode: ', COALESCE(OLD.validationBarcode, 'n/a'), ' → ', COALESCE(NEW.validationBarcode, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message) VALUES (
      NEW.containerId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
        CASE WHEN NEW.locationBarcode <> OLD.locationBarcode THEN 'locationBarcode' END,
        CASE WHEN (NEW.platform IS NULL) <> (OLD.platform IS NULL) OR NEW.platform <> OLD.platform THEN 'platform' END,
        CASE WHEN (NEW.validationBarcode IS NULL) <> (OLD.validationBarcode IS NULL) OR NEW.validationBarcode <> OLD.validationBarcode THEN 'validationBarcode' END), ''),
      NEW.lastModifier,
      log_message
    );
  END IF;
  END//

DROP TRIGGER IF EXISTS SequencerPartitionContainerInsert//
CREATE TRIGGER SequencerPartitionContainerInsert AFTER INSERT ON SequencerPartitionContainer
FOR EACH ROW
  INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message) VALUES (
    NEW.containerId,
    '',
    NEW.lastModifier,
    'Container created.')//

DROP TRIGGER IF EXISTS PartitionChange//
CREATE TRIGGER PartitionChange BEFORE UPDATE ON _Partition
FOR EACH ROW
  BEGIN
    DECLARE log_message varchar(500) CHARACTER SET utf8;
    SET log_message = CONCAT_WS(', ',
    CASE WHEN (NEW.pool_poolId IS NULL) <> (OLD.pool_poolId IS NULL) OR NEW.pool_poolId <> OLD.pool_poolId THEN CONCAT('pool changed in partition ', OLD.partitionNumber, ': ', COALESCE((SELECT name FROM Pool WHERE poolId = OLD.pool_poolId), 'n/a'), ' → ', COALESCE((SELECT name FROM Pool WHERE poolId = NEW.pool_poolId), 'n/a')) END);
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message) VALUES (
        (SELECT spcp.container_containerId FROM SequencerPartitionContainer_Partition spcp
         WHERE spcp.partitions_partitionId = OLD.partitionId),
         COALESCE(CONCAT_WS(', ',
           CASE WHEN (NEW.pool_poolId IS NULL) <> (OLD.pool_poolId IS NULL) OR NEW.pool_poolId <> OLD.pool_poolId THEN 'pool' END), ''),
         (SELECT spc.lastModifier FROM SequencerPartitionContainer spc
           JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = spc.containerId
         WHERE spcp.partitions_partitionId = OLD.partitionId),
         log_message
      );
    END IF;
  END //

DROP TRIGGER IF EXISTS BoxChange//
CREATE TRIGGER BoxChange BEFORE UPDATE ON Box
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    CASE WHEN NEW.alias <> OLD.alias THEN CONCAT('alias: ', OLD.alias, ' → ', NEW.alias) END,
    CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
    CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN CONCAT('location: ', COALESCE(OLD.locationBarcode, 'n/a'), ' → ', COALESCE(NEW.locationBarcode, 'n/a')) END,
    CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN CONCAT('description: ', COALESCE(OLD.description, 'n/a'), ' → ', COALESCE(NEW.description, 'n/a')) END
  );
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message) VALUES (
      New.boxId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.alias <> OLD.alias THEN 'alias' END,
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
        CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN 'locationBarcode' END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN 'description' END), ''),
      NEW.lastModifier,
      log_message
    );
  END IF;
  END//

DROP TRIGGER IF EXISTS BoxInsert//
CREATE TRIGGER BoxInsert AFTER INSERT ON Box
FOR EACH ROW
  INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message) VALUES (
    NEW.boxId,
    '',
    NEW.lastModifier,
    'Box created.'
  )//

DROP TRIGGER IF EXISTS KitDescriptorChange//
CREATE TRIGGER KitDescriptorChange BEFORE UPDATE ON KitDescriptor
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
    CASE WHEN (NEW.version IS NULL) <> (OLD.version IS NULL) OR NEW.version <> OLD.version THEN CONCAT('version: ', COALESCE(OLD.version, 'n/a'), ' → ', COALESCE(NEW.version, 'n/a')) END,
    CASE WHEN NEW.manufacturer <> OLD.manufacturer THEN CONCAT('manufacturer: ', OLD.manufacturer, ' → ', NEW.manufacturer) END,
    CASE WHEN NEW.partNumber <> OLD.partNumber THEN CONCAT('part number: ', OLD.partNumber, ' → ', NEW.partNumber) END,
    CASE WHEN NEW.stockLevel <> OLD.stockLevel THEN CONCAT('stock: ', OLD.stockLevel, ' → ', NEW.stockLevel) END,
    CASE WHEN NEW.kitType <> OLD.kitType THEN CONCAT('type: ', OLD.kitType, ' → ', NEW.kitType) END,
    CASE WHEN NEW.platformType <> OLD.platformType THEN CONCAT('platform: ', OLD.platformType, ' → ', NEW.platformType) END,
    CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN CONCAT('description: ', COALESCE(OLD.description, 'n/a'), ' → ', COALESCE(NEW.description, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO KitDescriptorChangeLog(kitDescriptorId, columnsChanged, userId, message) VALUES (
      NEW.kitDescriptorId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.name <> OLD.name THEN 'name' END,
        CASE WHEN (NEW.version IS NULL) <> (OLD.version IS NULL) OR NEW.version <> OLD.version THEN 'version' END,
        CASE WHEN NEW.manufacturer <> OLD.manufacturer THEN 'manufacturer' END,
        CASE WHEN NEW.partNumber <> OLD.partNumber THEN 'partNumber' END,
        CASE WHEN NEW.stockLevel <> OLD.stockLevel THEN 'stockLevel' END,
        CASE WHEN NEW.kitType <> OLD.kitType THEN 'kitType' END,
        CASE WHEN NEW.platformType <> OLD.platformType THEN 'platformType' END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN 'description' END), ''),
      NEW.lastModifier,
      log_message
    );
  END IF;
  END//

DROP TRIGGER IF EXISTS KitDescriptorInsert//
CREATE TRIGGER KitDescriptorInsert AFTER INSERT ON KitDescriptor
FOR EACH ROW
  INSERT INTO KitDescriptorChangeLog(kitDescriptorId, columnsChanged, userId, message) VALUES (
    NEW.kitDescriptorId,
    '',
    NEW.lastModifier,
    'Kit descriptor created.')//
    
DROP PROCEDURE IF EXISTS deleteSample//
CREATE PROCEDURE deleteSample(
  iSampleId BIGINT(20),
  iSampleAlias VARCHAR(255)
) BEGIN
  DECLARE errorMessage varchar(300);
  -- rollback if any errors are thrown
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  START TRANSACTION;

  -- check that the sample exists
  IF NOT EXISTS (SELECT 1 FROM Sample WHERE sampleId = iSampleId AND alias = iSampleAlias)
  THEN
    SET errorMessage = CONCAT('Sample with ID ', iSampleId, ' and alias ', iSampleAlias, ' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- confirm that the sample has no sample children
  IF EXISTS (SELECT * FROM DetailedSample WHERE parentId = iSampleId)
  THEN
    SET errorMessage = CONCAT('Cannot delete sample with ID ', iSampleId, ' due to child samples.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- confirm that the sample has no library children
  IF EXISTS (SELECT * FROM Library WHERE sample_sampleId = iSampleId)
  THEN
    SET errorMessage = CONCAT('Cannot delete sample with ID ', iSampleId, ' due to related libraries.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- delete related SampleQCs and notes
  DELETE FROM SampleQC WHERE sample_sampleId = iSampleId;
  DELETE FROM Note WHERE noteId IN (SELECT notes_noteId FROM Sample_Note WHERE sample_sampleId = iSampleId);
  DELETE FROM Sample_Note WHERE sample_sampleId = iSampleId;

  -- delete from sample class/category tables
  DELETE FROM SampleAliquot WHERE sampleId = iSampleId;
  DELETE FROM SampleStock WHERE sampleId = iSampleId;
  DELETE FROM SampleTissueProcessing WHERE sampleId = iSampleId;
  DELETE FROM SampleCVSlide WHERE sampleId = iSampleId;
  DELETE FROM SampleLCMTube WHERE sampleId = iSampleId;
  DELETE FROM SampleTissue WHERE sampleId = iSampleId;
  DELETE FROM `Identity` WHERE sampleId = iSampleId;
  DELETE FROM SampleChangeLog WHERE sampleId = iSampleId;
  DELETE FROM DetailedSample WHERE sampleId = iSampleId;

  -- delete from Sample table
  DELETE FROM Sample WHERE sampleId = iSampleId;
  SELECT ROW_COUNT() AS number_deleted;

  COMMIT;
END//

DROP PROCEDURE IF EXISTS deleteLibrary//
CREATE PROCEDURE deleteLibrary(
  iLibraryId BIGINT(20),
  iLibraryAlias VARCHAR(255)
) BEGIN
  DECLARE errorMessage varchar(300);
  -- rollback if any errors are thrown
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  START TRANSACTION;

  -- check that the library exists
  IF NOT EXISTS (SELECT 1 FROM Library WHERE libraryId = iLibraryId AND alias = iLibraryAlias)
  THEN
    SET errorMessage = CONCAT('Library with ID ', iLibraryId, ' and alias ', iLibraryAlias, ' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- confirm that the library has no dilution children
  IF EXISTS (SELECT * FROM LibraryDilution WHERE library_libraryId = iLibraryId)
  THEN
    SET errorMessage = CONCAT('Cannot delete library with ID ', iLibraryId, ' due to child dilutions.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- delete related LibraryQCs, notes, and indices
  DELETE FROM LibraryQC WHERE library_libraryId = iLibraryId;
  DELETE FROM Note WHERE noteId IN (SELECT notes_noteId FROM Library_Note WHERE library_libraryId = iLibraryId);
  DELETE FROM Library_Note WHERE library_libraryId = iLibraryId;
  DELETE FROM Library_Index WHERE library_libraryId = iLibraryId;

  -- delete from libraryAdditionalInfo
  DELETE FROM LibraryAdditionalInfo WHERE libraryId = iLibraryId;
  DELETE FROM LibraryChangeLog WHERE libraryId = iLibraryId;

  -- delete from Library table
  DELETE FROM Library WHERE libraryId = iLibraryId;
  SELECT ROW_COUNT() AS number_deleted;

  COMMIT;
END//

DELIMITER ;
-- EndNoTest

DROP VIEW IF EXISTS CompletedPartitions;
CREATE OR REPLACE VIEW RunPartitionsByHealth AS
  SELECT pool_poolId AS poolId, sequencingParameters_parametersId as parametersId, COUNT(*) AS num_partitions, health AS health, (SELECT MAX(changeTime) FROM RunChangeLog WHERE RunChangeLog.runId = Run.runId) as lastUpdated
    FROM Run JOIN Run_SequencerPartitionContainer ON Run.runId = Run_SequencerPartitionContainer.Run_runId
     JOIN SequencerPartitionContainer_Partition ON Run_SequencerPartitionContainer.containers_containerId = SequencerPartitionContainer_Partition.container_containerId
     JOIN _Partition ON SequencerPartitionContainer_Partition.partitions_partitionId = _Partition.partitionId
    WHERE sequencingParameters_parametersId IS NOT NULL AND pool_poolId IS NOT NULL
    GROUP BY pool_poolId, sequencingParameters_parametersId, health;

CREATE OR REPLACE VIEW DesiredPartitions AS
  SELECT poolId, parametersId, SUM(partitions) AS num_partitions, MAX(lastUpdated) as lastUpdated
    FROM PoolOrder
    GROUP BY poolId, parametersId;

CREATE OR REPLACE VIEW OrderCompletion AS
  (SELECT poolId, parametersId, num_partitions, health, lastUpdated FROM RunPartitionsByHealth)
  UNION
  (SELECT poolId, parametersId, num_partitions, 'Requested' AS health, lastUpdated FROM DesiredPartitions);

CREATE OR REPLACE VIEW SampleDerivedInfo AS
  SELECT sampleId, MAX(changeTime) as lastModified FROM SampleChangeLog GROUP BY sampleId;
  
CREATE OR REPLACE VIEW RunDerivedInfo AS
  SELECT runId, MAX(changeTime) as lastModified FROM RunChangeLog GROUP BY runId;

CREATE OR REPLACE VIEW ContainerDerivedInfo AS
  SELECT containerId, MAX(changeTime) as lastModified FROM SequencerPartitionContainerChangeLog GROUP BY containerId;

CREATE OR REPLACE VIEW PoolDerivedInfo AS
  SELECT poolId, MAX(changeTime) as lastModified FROM PoolChangeLog GROUP BY poolId;

CREATE OR REPLACE VIEW LibraryDerivedInfo AS
  SELECT libraryId, MAX(changeTime) AS lastModified FROM LibraryChangeLog GROUP BY libraryId;
  
CREATE OR REPLACE VIEW BoxDerivedInfo AS
  SELECT boxId, MAX(changeTime) AS lastModified FROM BoxChangeLog GROUP BY boxId;

