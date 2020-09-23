CREATE OR REPLACE VIEW PoolableElementView
AS SELECT
    d.aliquotId,
    d.name AS aliquotName,
    d.alias AS aliquotAlias,
    l.dnaSize AS aliquotDnaSize,
    d.concentration AS aliquotConcentration,
    d.concentrationUnits AS aliquotConcentrationUnits,
    d.volume AS aliquotVolume,
    d.volumeUnits AS aliquotVolumeUnits,
    d.ngUsed AS aliquotNgUsed,
    d.volumeUsed AS aliquotVolumeUsed,
    d.identificationBarcode AS aliquotBarcode,
    d.lastUpdated AS lastModified,
    d.creationDate AS created,
    d.creator AS creator,
    d.targetedSequencingId AS targetedSequencingId,
    d.lastModifier AS lastModifier,
    d.preMigrationId AS preMigrationId,
    d.discarded AS discarded,
    d.detailedQcStatusId AS detailedQcStatusId,
    d.detailedQcStatusNote AS detailedQcStatusNote,
    dist.distributed AS distributed,
    l.libraryId AS libraryId,
    l.name AS libraryName,
    l.alias AS libraryAlias,
    l.description AS libraryDescription,
    l.identificationBarcode AS libraryBarcode,
    l.lowQuality AS libraryLowQuality,
    l.platformType AS platformType,
    l.paired AS libraryPaired,
    sel.name AS librarySelectionType,
    strat.name AS libraryStrategyType,
    s.sampleId AS sampleId,
    s.name AS sampleName,
    s.alias AS sampleAlias,
    s.description AS sampleDescription,
    s.accession AS sampleAccession,
    s.sampleType AS sampleType,
    s.sequencingControlTypeId AS sampleSequencingControlTypeId,
    p.projectId AS projectId,
    p.name AS projectName,
    p.shortName AS projectShortName,
    p.alias AS projectAlias,
    sub.subprojectId AS subprojectId,
    sub.alias AS subprojectAlias,
    sub.priority AS subprojectPriority,
    box.alias AS boxAlias,
    box.name AS boxName,
    box.identificationBarcode AS boxIdentificationBarcode,
    box.locationBarcode AS boxLocationBarcode,
    dbp.position AS boxPosition,
    d.libraryDesignCodeId AS libraryDesignCodeId
  FROM LibraryAliquot d
    JOIN Library l ON l.libraryId = d.libraryId
    JOIN Sample s ON s.sampleId = l.sample_sampleId
    JOIN Project p ON p.projectId = s.project_projectId
    LEFT JOIN Subproject sub ON sub.subprojectId = s.subprojectId
    LEFT JOIN LibrarySelectionType sel ON sel.librarySelectionTypeId = l.librarySelectionType
    LEFT JOIN LibraryStrategyType strat ON strat.libraryStrategyTypeId = l.libraryStrategyType
    LEFT JOIN LibraryAliquotBoxPosition dbp ON dbp.aliquotId = d.aliquotId
    LEFT JOIN Box box ON box.boxId = dbp.boxId
    -- Note: LibraryAliquotDistributionView is created in 05_view_box.sql
    JOIN LibraryAliquotDistributionView dist ON dist.aliquotId = d.aliquotId;

CREATE OR REPLACE VIEW ListPoolView AS
SELECT
  p.poolId,
  p.name,
  p.alias,
  p.identificationBarcode,
  p.description,
  p.platformType,
  p.creator,
  p.created,
  p.creationDate,
  p.lastModifier,
  p.lastModified,
  p.concentration,
  p.concentrationUnits,
  p.discarded,
  dist.distributed,
  b.boxId,
  b.name AS boxName,
  b.alias AS boxAlias,
  b.locationBarcode AS boxLocationBarcode,
  bp.position AS boxPosition
FROM Pool p
LEFT JOIN BoxPosition bp ON bp.targetType = 'POOL' AND bp.targetId = p.poolId
LEFT JOIN Box b ON b.boxId = bp.boxId
JOIN PoolDistributionView dist ON dist.poolId = p.poolId;

CREATE OR REPLACE VIEW ListPoolView_Element AS
SELECT
  link.poolId,
  lib.libraryId,
  la.aliquotId,
  la.name,
  la.alias,
  la.dnaSize,
  lib.lowQuality,
  sam.project_projectId AS projectId,
  sp.alias AS subprojectAlias,
  sp.priority AS subprojectPriority,
  ident.consentLevel
FROM Pool_LibraryAliquot link
JOIN LibraryAliquot la ON la.aliquotId = link.aliquotId
JOIN Library lib ON lib.libraryId = la.libraryId
JOIN Sample sam ON sam.sampleId = lib.sample_sampleId
LEFT JOIN Subproject sp ON sp.subprojectId = sam.subprojectId
LEFT JOIN SampleHierarchy sh ON sh.sampleId = sam.sampleId
LEFT JOIN Sample ident ON ident.sampleId = sh.identityId;
