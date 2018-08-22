-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS SampleChange//
CREATE TRIGGER SampleChange BEFORE UPDATE ON Sample
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
    CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias AND (OLD.alias NOT LIKE 'TEMPORARY%') THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
    CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
    CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
    CASE WHEN (NEW.locationBarcode IS NULL) <> (OLD.locationBarcode IS NULL) OR NEW.locationBarcode <> OLD.locationBarcode THEN CONCAT('location: ', COALESCE(OLD.locationBarcode, 'n/a'), ' → ', COALESCE(NEW.locationBarcode, 'n/a')) END,
    CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN CONCAT('project: ', (SELECT name FROM Project WHERE projectId = OLD.project_projectId), ' → ', (SELECT name FROM Project WHERE projectId = NEW.project_projectId)) END,
    CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN CONCAT('qcPassed: ', COALESCE(OLD.qcPassed, 'n/a'), ' → ', COALESCE(NEW.qcPassed, 'n/a')) END,
    CASE WHEN (NEW.receivedDate IS NULL) <> (OLD.receivedDate IS NULL) OR NEW.receivedDate <> OLD.receivedDate THEN CONCAT('received: ', COALESCE(OLD.receivedDate, 'n/a'), ' → ', COALESCE(NEW.receivedDate, 'n/a')) END,
    CASE WHEN NEW.sampleType <> OLD.sampleType THEN CONCAT('type: ', OLD.sampleType, ' → ', NEW.sampleType) END,
    CASE WHEN NEW.scientificName <> OLD.scientificName THEN CONCAT('scientific name: ', OLD.scientificName, ' → ', NEW.scientificName) END,
    CASE WHEN (NEW.taxonIdentifier IS NULL) <> (OLD.taxonIdentifier IS NULL) OR NEW.taxonIdentifier <> OLD.taxonIdentifier THEN CONCAT('taxon: ', COALESCE(OLD.taxonIdentifier, 'n/a'), ' → ', COALESCE(NEW.taxonIdentifier, 'n/a')) END,
    CASE WHEN NEW.discarded <> OLD.discarded THEN CONCAT('discarded: ', OLD.discarded, ' → ', NEW.discarded) END,
    CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN CONCAT('volume: ', COALESCE(OLD.volume, 'n/a'), ' → ', COALESCE(NEW.volume, 'n/a')) END,
    CASE WHEN (NEW.concentrationUnits IS NULL) <> (OLD.concentrationUnits IS NULL) OR NEW.concentrationUnits <> OLD.concentrationUnits THEN CONCAT(NEW.name, ' concentration units: ', COALESCE(OLD.concentrationUnits, 'n/a'), ' → ', COALESCE(NEW.concentrationUnits, 'n/a')) END,
    CASE WHEN (NEW.volumeUnits IS NULL) <> (OLD.volumeUnits IS NULL) OR NEW.volumeUnits <> OLD.volumeUnits THEN CONCAT(NEW.name, ' volume units: ', COALESCE(OLD.volumeUnits, 'n/a'), ' → ', COALESCE(NEW.volumeUnits, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN 'accession' END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias AND (OLD.alias NOT LIKE 'TEMPORARY%') THEN 'alias' END,
        CASE WHEN NEW.description <> OLD.description THEN 'description' END,
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'barcode' END,
        CASE WHEN NEW.locationBarcode <> OLD.locationBarcode THEN 'locationBarcode' END,
        CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN 'project_projectId' END,
        CASE WHEN (NEW.qcPassed IS NULL) <> (OLD.qcPassed IS NULL) OR NEW.qcPassed <> OLD.qcPassed THEN 'qcPassed' END,
        CASE WHEN (NEW.receivedDate IS NULL) <> (OLD.receivedDate IS NULL) OR NEW.receivedDate <> OLD.receivedDate THEN 'receivedDate' END,
        CASE WHEN NEW.sampleType <> OLD.sampleType THEN 'sampleType' END,
        CASE WHEN NEW.scientificName <> OLD.scientificName THEN 'scientificName' END,
        CASE WHEN (NEW.taxonIdentifier IS NULL) <> (OLD.taxonIdentifier IS NULL) OR NEW.taxonIdentifier <> OLD.taxonIdentifier THEN 'taxonIdentifier' END,
        CASE WHEN NEW.discarded <> OLD.discarded THEN 'discarded' END,
        CASE WHEN (NEW.volume IS NULL) <> (OLD.volume IS NULL) OR NEW.volume <> OLD.volume THEN 'volume' END,
        CASE WHEN (NEW.concentrationUnits IS NULL) <> (OLD.concentrationUnits IS NULL) OR NEW.concentrationUnits <> OLD.concentrationUnits THEN CONCAT(NEW.name, ' concentrationUnits') END,
        CASE WHEN (NEW.volumeUnits IS NULL) <> (OLD.volumeUnits IS NULL) OR NEW.volumeUnits <> OLD.volumeUnits THEN CONCAT(NEW.name, ' volumeUnits') END
  ), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified
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
     CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN CONCAT('concentration: ', COALESCE(OLD.concentration, 'n/a'), ' → ', COALESCE(NEW.concentration, 'n/a')) END, 
     CASE WHEN (NEW.subprojectId IS NULL) <> (OLD.subprojectId IS NULL) OR NEW.subprojectId <> OLD.subprojectId THEN CONCAT('subproject: ', COALESCE((SELECT alias FROM Subproject WHERE subprojectId = OLD.subprojectId), 'n/a'), ' → ', COALESCE((SELECT alias FROM Subproject WHERE subprojectId = NEW.subprojectId), 'n/a')) END,
    CASE WHEN (NEW.creationDate IS NULL) <> (OLD.creationDate IS NULL) OR NEW.creationDate <> OLD.creationDate THEN CONCAT('creationDate: ', COALESCE(OLD.creationDate, 'n/a'), ' → ', COALESCE(NEW.creationDate, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime) 
    SELECT
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.archived <> OLD.archived THEN 'archived' END,
        CASE WHEN (NEW.groupDescription IS NULL) <> (OLD.groupDescription IS NULL) OR NEW.groupDescription <> OLD.groupDescription THEN 'groupDescription' END,
        CASE WHEN (NEW.groupId IS NULL) <> (OLD.groupId IS NULL) OR NEW.groupId <> OLD.groupId THEN 'groupId' END,
        CASE WHEN (NEW.parentId IS NULL) <> (OLD.parentId IS NULL) OR NEW.parentId <> OLD.parentId THEN 'parentId' END,
        CASE WHEN (NEW.detailedQcStatusId IS NULL) <> (OLD.detailedQcStatusId IS NULL) OR NEW.detailedQcStatusId <> OLD.detailedQcStatusId THEN 'detailedQcStatusId' END,
        CASE WHEN (NEW.detailedQcStatusNote IS NULL) <> (OLD.detailedQcStatusNote IS NULL) OR NEW.detailedQcStatusNote <> OLD.detailedQcStatusNote THEN 'detailedQcStatusNote' END,
        CASE WHEN NEW.sampleClassId <> OLD.sampleClassId THEN 'sampleClassId' END,
        CASE WHEN (NEW.concentration IS NULL) <> (OLD.concentration IS NULL) OR NEW.concentration <> OLD.concentration THEN 'concentration' END,
        CASE WHEN (NEW.subprojectId IS NULL) <> (OLD.subprojectId IS NULL) OR NEW.subprojectId <> OLD.subprojectId THEN 'subprojectId' END,
        CASE WHEN (NEW.creationDate IS NULL) <> (OLD.creationDate IS NULL) OR NEW.creationDate <> OLD.creationDate THEN 'creationDate' END
      ), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Sample WHERE sampleId = NEW.sampleId;
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
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.samplePurposeId IS NULL) <> (OLD.samplePurposeId IS NULL) OR NEW.samplePurposeId <> OLD.samplePurposeId THEN 'samplePurposeId' END
      ), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Sample WHERE sampleId = NEW.sampleId;
  END IF;
  END//

DROP TRIGGER IF EXISTS SampleCVSlideChange//
DROP TRIGGER IF EXISTS SampleSlideChange//
CREATE TRIGGER SampleSlideChange BEFORE UPDATE ON SampleSlide
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
     CASE WHEN NEW.slides <> OLD.slides THEN CONCAT('slides: ', OLD.slides, ' → ', NEW.slides) END,
     CASE WHEN (NEW.discards IS NULL) <> (OLD.discards IS NULL) OR NEW.discards <> OLD.discards THEN CONCAT('discards: ', COALESCE(OLD.discards, 'n/a'), ' → ', COALESCE(NEW.discards, 'n/a')) END,
     CASE WHEN (NEW.thickness IS NULL) <> (OLD.thickness IS NULL) OR NEW.thickness <> OLD.thickness THEN CONCAT('thickness: ', COALESCE(OLD.thickness, 'n/a'), ' → ', COALESCE(NEW.thickness, 'n/a')) END,
     CASE WHEN (NEW.stain IS NULL) <> (OLD.stain IS NULL) OR NEW.stain <> OLD.stain THEN CONCAT('stain: ', COALESCE((SELECT name FROM Stain WHERE stainId = OLD.stain), 'none'), ' → ', COALESCE((SELECT name FROM Stain WHERE stainId = NEW.stain), 'none')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.slides <> OLD.slides THEN 'slides' END,
        CASE WHEN (NEW.discards IS NULL) <> (OLD.discards IS NULL) OR NEW.discards <> OLD.discards THEN 'discards' END,
        CASE WHEN (NEW.thickness IS NULL) <> (OLD.thickness IS NULL) OR NEW.thickness <> OLD.thickness THEN 'thickness' END,
        CASE WHEN (NEW.stain IS NULL) <> (OLD.stain IS NULL) OR NEW.stain <> OLD.stain THEN 'stain' END
      ), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Sample WHERE sampleId = NEW.sampleId;
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
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.slidesConsumed <> OLD.slidesConsumed THEN 'slides' END
      ), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Sample WHERE sampleId = NEW.sampleId;
  END IF;
  END//

DROP TRIGGER IF EXISTS SampleStockChange//
CREATE TRIGGER SampleStockChange BEFORE UPDATE ON SampleStock
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    CASE WHEN NEW.strStatus <> OLD.strStatus THEN CONCAT('STR status: ', OLD.strStatus, ' → ', NEW.strStatus) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
         CASE WHEN NEW.strStatus <> OLD.strStatus THEN 'strStatus' END
      ), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Sample WHERE sampleId = NEW.sampleId;
  END IF;
  END//

DROP TRIGGER IF EXISTS SampleTissueChange//
CREATE TRIGGER SampleTissueChange BEFORE UPDATE ON SampleTissue
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    CASE WHEN (NEW.secondaryIdentifier IS NULL) <> (OLD.secondaryIdentifier IS NULL) OR NEW.secondaryIdentifier <> OLD.secondaryIdentifier THEN CONCAT('secondary identifier: ', COALESCE(OLD.secondaryIdentifier, 'n/a'), ' → ', COALESCE(NEW.secondaryIdentifier, 'n/a')) END,
    CASE WHEN (NEW.labId IS NULL) <> (OLD.labId IS NULL) OR NEW.labId <> OLD.labId THEN CONCAT('lab: ', COALESCE((SELECT alias FROM Lab WHERE labId = OLD.labId), 'n/a'), ' → ', COALESCE((SELECT alias FROM Lab WHERE labId = NEW.labId), 'n/a')) END,
    CASE WHEN (NEW.passageNumber IS NULL) <> (OLD.passageNumber IS NULL) OR NEW.passageNumber <> OLD.passageNumber OR (NEW.timesReceived IS NULL) <> (OLD.timesReceived IS NULL) OR NEW.timesReceived <> OLD.timesReceived OR (NEW.tubeNumber IS NULL) <> (OLD.tubeNumber IS NULL) OR NEW.tubeNumber <> OLD.tubeNumber THEN CONCAT('passage: ', COALESCE(OLD.passageNumber, 'n/a'), '-', COALESCE(OLD.timesReceived, 'n/a'), '-', COALESCE(OLD.tubeNumber, 'n/a'), ' → ', COALESCE(NEW.passageNumber, 'n/a'), '-', COALESCE(NEW.timesReceived, 'n/a'), '-', COALESCE(NEW.tubeNumber, 'n/a')) END,
    CASE WHEN (NEW.region IS NULL) <> (OLD.region IS NULL) OR NEW.region <> OLD.region THEN CONCAT('region: ', COALESCE(OLD.region, 'n/a'), ' → ', COALESCE(NEW.region, 'n/a')) END,
    CASE WHEN (NEW.tissueMaterialId IS NULL) <> (OLD.tissueMaterialId IS NULL) OR NEW.tissueMaterialId <> OLD.tissueMaterialId THEN CONCAT('material: ', COALESCE((SELECT alias FROM TissueMaterial WHERE tissueMaterialId = OLD.tissueMaterialId), 'n/a'), ' → ', COALESCE((SELECT alias FROM TissueMaterial WHERE tissueMaterialId = NEW.tissueMaterialId), 'n/a')) END,
    CASE WHEN (NEW.tissueOriginId IS NULL) <> (OLD.tissueOriginId IS NULL) OR NEW.tissueOriginId <> OLD.tissueOriginId THEN CONCAT('origin: ', COALESCE((SELECT alias FROM TissueOrigin WHERE tissueOriginId = OLD.tissueOriginId), 'n/a'), ' → ', COALESCE((SELECT alias FROM TissueOrigin WHERE tissueOriginId = NEW.tissueOriginId), 'n/a')) END,
    CASE WHEN (NEW.tissueTypeId IS NULL) <> (OLD.tissueTypeId IS NULL) OR NEW.tissueTypeId <> OLD.tissueTypeId THEN CONCAT('type: ', COALESCE((SELECT alias FROM TissueType WHERE tissueTypeId = OLD.tissueTypeId), 'n/a'), ' → ', COALESCE((SELECT alias FROM TissueType WHERE tissueTypeId = NEW.tissueTypeId), 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
         CASE WHEN (NEW.secondaryIdentifier IS NULL) <> (OLD.secondaryIdentifier IS NULL) OR NEW.secondaryIdentifier <> OLD.secondaryIdentifier THEN 'secondaryIdentifier' END,
         CASE WHEN (NEW.labId IS NULL) <> (OLD.labId IS NULL) OR NEW.labId <> OLD.labId THEN 'labId' END,
         CASE WHEN (NEW.passageNumber IS NULL) <> (OLD.passageNumber IS NULL) OR NEW.passageNumber <> OLD.passageNumber THEN 'passageNumber' END,
         CASE WHEN (NEW.region IS NULL) <> (OLD.region IS NULL) OR NEW.region <> OLD.region THEN 'region' END,
         CASE WHEN (NEW.timesReceived IS NULL) <> (OLD.timesReceived IS NULL) OR NEW.timesReceived <> OLD.timesReceived THEN 'timesReceived' END,
         CASE WHEN (NEW.tissueMaterialId IS NULL) <> (OLD.tissueMaterialId IS NULL) OR NEW.tissueMaterialId <> OLD.tissueMaterialId THEN 'tissueMaterialId' END,
         CASE WHEN (NEW.tissueOriginId IS NULL) <> (OLD.tissueOriginId IS NULL) OR NEW.tissueOriginId <> OLD.tissueOriginId THEN 'tissueOriginId' END,
         CASE WHEN (NEW.tissueTypeId IS NULL) <> (OLD.tissueTypeId IS NULL) OR NEW.tissueTypeId <> OLD.tissueTypeId THEN 'tissueTypeId' END,
         CASE WHEN (NEW.tubeNumber IS NULL) <> (OLD.tubeNumber IS NULL) OR NEW.tubeNumber <> OLD.tubeNumber THEN 'tubeNumber' END
      ), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Sample WHERE sampleId = NEW.sampleId;
  END IF;
  END//

DROP TRIGGER IF EXISTS IdentityChange//
CREATE TRIGGER IdentityChange BEFORE UPDATE ON Identity
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    CASE WHEN NEW.donorSex <> OLD.donorSex THEN CONCAT('donor sex: ', OLD.donorSex, ' → ', NEW.donorSex) END,
    CASE WHEN NEW.consentLevel <> OLD.consentLevel THEN CONCAT('consent level: ', OLD.consentLevel, ' → ', NEW.consentLevel) END,
    CASE WHEN NEW.externalName <> OLD.externalName THEN CONCAT('externalName: ', OLD.externalName, ' → ', NEW.externalName) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.donorSex <> OLD.donorSex THEN 'donorSex' END,
        CASE WHEN NEW.consentLevel <> OLD.consentLevel THEN 'consentLevel' END,
        CASE WHEN NEW.externalName <> OLD.externalName THEN 'externalName' END
      ), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Sample WHERE sampleId = NEW.sampleId;
  END IF;
  END//

DROP TRIGGER IF EXISTS BeforeInsertSample//

DROP TRIGGER IF EXISTS SampleInsert//
CREATE TRIGGER SampleInsert AFTER INSERT ON Sample
FOR EACH ROW
  INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.sampleId,
    '',
    NEW.lastModifier,
    'Sample created.',
    NEW.lastModified)//
    
DELIMITER ;
-- EndNoTest
