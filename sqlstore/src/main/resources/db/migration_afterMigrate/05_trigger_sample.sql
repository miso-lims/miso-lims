-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS SampleChange//
CREATE TRIGGER SampleChange BEFORE UPDATE ON Sample
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    makeChangeMessage('accession', OLD.accession, NEW.accession),
    CASE WHEN OLD.alias NOT LIKE 'TEMPORARY%' THEN makeChangeMessage('alias', OLD.alias, NEW.alias) END,
    makeChangeMessage('description', OLD.description, NEW.description),
    makeChangeMessage('barcode', OLD.identificationBarcode, NEW.identificationBarcode),
    makeChangeMessage('location', OLD.locationBarcode, NEW.locationBarcode),
    makeChangeMessage('project', (SELECT name FROM Project WHERE projectId = OLD.project_projectId), (SELECT name FROM Project WHERE projectId = NEW.project_projectId)),
    makeChangeMessage('type', OLD.sampleType, NEW.sampleType),
    makeChangeMessage('scientific name', (SELECT alias FROM ScientificName WHERE scientificNameId = OLD.scientificNameId), (SELECT alias FROM ScientificName WHERE scientificNameId = NEW.scientificNameId)),
    makeChangeMessage('taxon', OLD.taxonIdentifier, NEW.taxonIdentifier),
    makeChangeMessage('discarded', booleanToString(OLD.discarded), booleanToString(NEW.discarded)),
    makeChangeMessage('concentration', decimalToString(OLD.concentration), decimalToString(NEW.concentration)),
    makeChangeMessage('initial volume', decimalToString(OLD.initialVolume), decimalToString(NEW.initialVolume)),
    makeChangeMessage('volume', decimalToString(OLD.volume), decimalToString(NEW.volume)),
    makeChangeMessage('concentration units', OLD.concentrationUnits, NEW.concentrationUnits),
    makeChangeMessage('volume units', OLD.volumeUnits, NEW.volumeUnits),
    makeChangeMessage('requisition ID', OLD.requisitionId, NEW.requisitionId),
    makeChangeMessage('sequencing control type', (SELECT alias FROM SequencingControlType WHERE sequencingControlTypeId = OLD.sequencingControlTypeId), (SELECT alias FROM SequencingControlType WHERE sequencingControlTypeId = NEW.sequencingControlTypeId)),
    makeChangeMessage('archived', booleanToString(OLD.archived), booleanToString(NEW.archived)),
    makeChangeMessage('group description', OLD.groupDescription, NEW.groupDescription),
    makeChangeMessage('group id', OLD.groupId, NEW.groupId),
    makeChangeMessage('parent', (SELECT name FROM Sample WHERE sampleId = OLD.parentId), (SELECT name FROM Sample WHERE sampleId = NEW.parentId)),
    makeChangeMessage('QC Status', (SELECT description FROM DetailedQcStatus WHERE detailedQcStatusId = OLD.detailedQcStatusId), (SELECT description FROM DetailedQcStatus WHERE detailedQcStatusId = NEW.detailedQcStatusId)),
    makeChangeMessage('QC Status Note', OLD.detailedQcStatusNote, NEW.detailedQcStatusNote),
    makeChangeMessage('class', (SELECT alias FROM SampleClass WHERE sampleClassId = OLD.sampleClassId), (SELECT alias FROM SampleClass WHERE sampleClassId = NEW.sampleClassId)),
    makeChangeMessage('subproject', (SELECT alias FROM Subproject WHERE subprojectId = OLD.subprojectId), (SELECT alias FROM Subproject WHERE subprojectId = NEW.subprojectId)),
    makeChangeMessage('volume used', decimalToString(OLD.volumeUsed), decimalToString(NEW.volumeUsed)),
    makeChangeMessage('ng used', decimalToString(OLD.ngUsed), decimalToString(NEW.ngUsed)),
    makeChangeMessage('creation date', OLD.creationDate, NEW.creationDate),
    makeChangeMessage('purpose', (SELECT alias FROM SamplePurpose WHERE samplePurposeId = OLD.samplePurposeId), (SELECT alias FROM SamplePurpose WHERE samplePurposeId = NEW.samplePurposeId)),
    makeChangeMessage('slides', OLD.slides, NEW.slides),
    makeChangeMessage('discards', OLD.discards, NEW.discards),
    makeChangeMessage('thickness', OLD.thickness, NEW.thickness),
    makeChangeMessage('stain', (SELECT name FROM Stain WHERE stainId = OLD.stain), (SELECT name FROM Stain WHERE stainId = NEW.stain)),
    makeChangeMessage('percent tumour', OLD.percentTumour, NEW.percentTumour),
    makeChangeMessage('percent necrosis', OLD.percentNecrosis, NEW.percentNecrosis),
    makeChangeMessage('marked area size', OLD.markedAreaSize, NEW.markedAreaSize),
    makeChangeMessage('marked area percent tumour', OLD.markedAreaPercentTumour, NEW.markedAreaPercentTumour),
    makeChangeMessage('slides', OLD.slidesConsumed, NEW.slidesConsumed),
    makeChangeMessage('type', (SELECT name FROM TissuePieceType WHERE tissuePieceTypeId = OLD.tissuePieceType), (SELECT name FROM TissuePieceType WHERE tissuePieceTypeId = NEW.tissuePieceType)),
    makeChangeMessage('reference slide', (SELECT name FROM Sample WHERE sampleId = OLD.referenceSlideId), (SELECT name FROM Sample WHERE sampleId = NEW.referenceSlideId)),
    makeChangeMessage('STR status', OLD.strStatus, NEW.strStatus),
    makeChangeMessage('secondary identifier', OLD.secondaryIdentifier, NEW.secondaryIdentifier),
    makeChangeMessage('lab', (SELECT alias FROM Lab WHERE labId = OLD.labId), (SELECT alias FROM Lab WHERE labId = NEW.labId)),
    makeChangeMessage('passage number', OLD.passageNumber, NEW.passageNumber),
    makeChangeMessage('times received', OLD.timesReceived, NEW.timesReceived),
    makeChangeMessage('tube number', OLD.tubeNumber, NEW.tubeNumber),
    makeChangeMessage('region', OLD.region, NEW.region),
    makeChangeMessage('material', (SELECT alias FROM TissueMaterial WHERE tissueMaterialId = OLD.tissueMaterialId), (SELECT alias FROM TissueMaterial WHERE tissueMaterialId = NEW.tissueMaterialId)),
    makeChangeMessage('origin', (SELECT alias FROM TissueOrigin WHERE tissueOriginId = OLD.tissueOriginId), (SELECT alias FROM TissueOrigin WHERE tissueOriginId = NEW.tissueOriginId)),
    makeChangeMessage('type', (SELECT alias FROM TissueType WHERE tissueTypeId = OLD.tissueTypeId), (SELECT alias FROM TissueType WHERE tissueTypeId = NEW.tissueTypeId)),
    makeChangeMessage('donor sex', OLD.donorSex, NEW.donorSex),
    makeChangeMessage('consent level', OLD.consentLevel, NEW.consentLevel),
    makeChangeMessage('external name', OLD.externalName, NEW.externalName),
    makeChangeMessage('initial cell concentration', decimalToString(OLD.initialCellConcentration), decimalToString(NEW.initialCellConcentration)),
    makeChangeMessage('digestion', OLD.digestion, NEW.digestion),
    makeChangeMessage('target cell recovery', decimalToString(OLD.targetCellRecovery), decimalToString(NEW.targetCellRecovery)),
    makeChangeMessage('cell viability', decimalToString(OLD.cellViability), decimalToString(NEW.cellViability)),
    makeChangeMessage('loading cell concentration', decimalToString(OLD.loadingCellConcentration), decimalToString(NEW.loadingCellConcentration)),
    makeChangeMessage('input into library', decimalToString(OLD.inputIntoLibrary), decimalToString(NEW.inputIntoLibrary)),
    makeChangeMessage('SOP', (SELECT CONCAT(alias, ' (', version, ')') FROM Sop WHERE sopId = OLD.sopId), (SELECT CONCAT(alias, ' (', version, ')') FROM Sop WHERE sopId = NEW.sopId))
  );
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.sampleId,
      COALESCE(CONCAT_WS(',',
        makeChangeColumn('accession', OLD.accession, NEW.accession),
        CASE WHEN OLD.alias NOT LIKE 'TEMPORARY%' THEN makeChangeColumn('alias', OLD.alias, NEW.alias) END,
        makeChangeColumn('description', OLD.description, NEW.description),
        makeChangeColumn('identificationBarcode', OLD.identificationBarcode, NEW.identificationBarcode),
        makeChangeColumn('locationBarcode', OLD.locationBarcode, NEW.locationBarcode),
        makeChangeColumn('project_projectId', OLD.project_projectId, NEW.project_projectId),
        makeChangeColumn('sampleType', OLD.sampleType, NEW.sampleType),
        makeChangeColumn('scientificNameId', OLD.scientificNameId, NEW.scientificNameId),
        makeChangeColumn('taxonIdentifier', OLD.taxonIdentifier, NEW.taxonIdentifier),
        makeChangeColumn('discarded', OLD.discarded, NEW.discarded),
        makeChangeColumn('concentration', OLD.concentration, NEW.concentration),
        makeChangeColumn('initialVolume', OLD.initialVolume, NEW.initialVolume),
        makeChangeColumn('volume', OLD.volume, NEW.volume),
        makeChangeColumn('concentrationUnits', OLD.concentrationUnits, NEW.concentrationUnits),
        makeChangeColumn('volumeUnits', OLD.volumeUnits, NEW.volumeUnits),
        makeChangeColumn('requisitionId', OLD.requisitionId, NEW.requisitionId),
        makeChangeColumn('sequencingControlTypeId', OLD.sequencingControlTypeId, NEW.sequencingControlTypeId),
        makeChangeColumn('archived', OLD.archived, NEW.archived),
        makeChangeColumn('groupDescription', OLD.groupDescription, NEW.groupDescription),
        makeChangeColumn('groupId', OLD.groupId, NEW.groupId),
        makeChangeColumn('parentId', OLD.parentId, NEW.parentId),
        makeChangeColumn('detailedQcStatusId', OLD.detailedQcStatusId, NEW.detailedQcStatusId),
        makeChangeColumn('detailedQcStatusNote', OLD.detailedQcStatusNote, NEW.detailedQcStatusNote),
        makeChangeColumn('sampleClassId', OLD.sampleClassId, NEW.sampleClassId),
        makeChangeColumn('subprojectId', OLD.subprojectId, NEW.subprojectId),
        makeChangeColumn('volumeUsed', OLD.volumeUsed, NEW.volumeUsed),
        makeChangeColumn('ngUsed', OLD.ngUsed, NEW.ngUsed),
        makeChangeColumn('creationDate', OLD.creationDate, NEW.creationDate),
        makeChangeColumn('samplePurposeId', OLD.samplePurposeId, NEW.samplePurposeId),
        makeChangeColumn('slides', OLD.slides, NEW.slides),
        makeChangeColumn('discards', OLD.discards, NEW.discards),
        makeChangeColumn('thickness', OLD.thickness, NEW.thickness),
        makeChangeColumn('stain', OLD.stain, NEW.stain),
        makeChangeColumn('percentTumour', OLD.percentTumour, NEW.percentTumour),
        makeChangeColumn('percentNecrosis', OLD.percentNecrosis, NEW.percentNecrosis),
        makeChangeColumn('markedAreaSize', OLD.markedAreaSize, NEW.markedAreaSize),
        makeChangeColumn('markedAreaPercentTumour', OLD.markedAreaPercentTumour, NEW.markedAreaPercentTumour),
        makeChangeColumn('slidesConsumed', OLD.slidesConsumed, NEW.slidesConsumed),
        makeChangeColumn('tissuePieceType', OLD.tissuePieceType, NEW.tissuePieceType),
        makeChangeColumn('referenceSlideId', OLD.referenceSlideId, NEW.referenceSlideId),
        makeChangeColumn('strStatus', OLD.strStatus, NEW.strStatus),
        makeChangeColumn('secondaryIdentifier', OLD.secondaryIdentifier, NEW.secondaryIdentifier),
        makeChangeColumn('labId', OLD.labId, NEW.labId),
        makeChangeColumn('passageNumber', OLD.passageNumber, NEW.passageNumber),
        makeChangeColumn('region', OLD.region, NEW.region),
        makeChangeColumn('timesReceived', OLD.timesReceived, NEW.timesReceived),
        makeChangeColumn('tubeNumber', OLD.tubeNumber, NEW.tubeNumber),
        makeChangeColumn('tissueMaterialId', OLD.tissueMaterialId, NEW.tissueMaterialId),
        makeChangeColumn('tissueOriginId', OLD.tissueOriginId, NEW.tissueOriginId),
        makeChangeColumn('tissueTypeId', OLD.tissueTypeId, NEW.tissueTypeId),
        makeChangeColumn('donorSex', OLD.donorSex, NEW.donorSex),
        makeChangeColumn('consentLevel', OLD.consentLevel, NEW.consentLevel),
        makeChangeColumn('externalName', OLD.externalName, NEW.externalName),
        makeChangeColumn('initialCellConcentration', OLD.initialCellConcentration, NEW.initialCellConcentration),
        makeChangeColumn('digestion', OLD.digestion, NEW.digestion),
        makeChangeColumn('targetCellRecovery', OLD.targetCellRecovery, NEW.targetCellRecovery),
        makeChangeColumn('cellViability', OLD.cellViability, NEW.cellViability),
        makeChangeColumn('loadingCellConcentration', OLD.loadingCellConcentration, NEW.loadingCellConcentration),
        makeChangeColumn('inputIntoLibrary', OLD.inputIntoLibrary, NEW.inputIntoLibrary),
        makeChangeColumn('sopId', OLD.sopId, NEW.sopId)
  ), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified
      );
  END IF;
  IF (NEW.parentId IS NULL) <> (OLD.parentId IS NULL) OR NEW.parentId <> OLD.parentId THEN
    CALL updateSampleHierarchy(NEW.sampleId);
  END IF;
  END//

DROP TRIGGER IF EXISTS SampleInsert//
CREATE TRIGGER SampleInsert AFTER INSERT ON Sample
FOR EACH ROW
BEGIN
  INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.sampleId,
    '',
    NEW.lastModifier,
    'Sample created.',
    NEW.lastModified);
  IF (NEW.discriminator <> 'Sample') THEN
    CALL updateSampleHierarchy(NEW.sampleId);
  END IF;
END//

DELIMITER ;
-- EndNoTest
