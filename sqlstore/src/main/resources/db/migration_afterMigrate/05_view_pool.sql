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
    createUser.loginName AS creatorName,
    createUser.fullName AS creatorFullName,
    d.targetedSequencingId AS targetedSequencingId,
    modUser.loginName AS lastModifierName,
    d.preMigrationId AS preMigrationId,
    l.libraryId AS libraryId,
    l.name AS libraryName,
    l.alias AS libraryAlias,
    l.description AS libraryDescription,
    l.identificationBarcode AS libraryBarcode,
    l.lowQuality AS libraryLowQuality,
    l.platformType AS platformType,
    l.paired AS libraryPaired,
    l.qcPassed AS libraryQcPassed,
    sel.name AS librarySelectionType,
    strat.name AS libraryStrategyType,
    s.sampleId AS sampleId,
    s.name AS sampleName,
    s.alias AS sampleAlias,
    s.description AS sampleDescription,
    s.accession AS sampleAccession,
    s.sampleType AS sampleType,
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
    box.locationBarcode AS boxLocationBarcode
  FROM LibraryAliquot d
    LEFT JOIN User modUser ON modUser.userId = d.lastModifier
    JOIN User createUser ON createUser.userId = d.creator
    JOIN Library l ON l.libraryId = d.libraryId
    JOIN Sample s ON s.sampleId = l.sample_sampleId
    JOIN Project p ON p.projectId = s.project_projectId
    LEFT JOIN DetailedSample ds ON ds.sampleId = s.sampleId
    LEFT JOIN Subproject sub ON sub.subprojectId = ds.subprojectId
    LEFT JOIN LibrarySelectionType sel ON sel.librarySelectionTypeId = l.librarySelectionType
    LEFT JOIN LibraryStrategyType strat ON strat.libraryStrategyTypeId = l.libraryStrategyType
    LEFT JOIN LibraryAliquotBoxPosition dbp ON dbp.aliquotId = d.aliquotId
    LEFT JOIN Box box ON box.boxId = dbp.boxId;

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
  p.lastModifier,
  p.lastModified,
  p.concentration,
  p.concentrationUnits,
  p.discarded,
  p.distributed,
  b.boxId,
  b.name AS boxName,
  b.alias AS boxAlias,
  b.locationBarcode AS boxLocationBarcode,
  bp.position AS boxPosition
FROM Pool p
LEFT JOIN BoxPosition bp ON bp.targetType = 'POOL' AND bp.targetId = p.poolId
LEFT JOIN Box b ON b.boxId = bp.boxId;

CREATE OR REPLACE VIEW ListPoolView_Element AS
SELECT
  link.poolId,
  lib.libraryId,
  la.aliquotId,
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
LEFT JOIN DetailedSample ds1 ON ds1.sampleId = sam.sampleId
LEFT JOIN Subproject sp ON sp.subprojectId = ds1.subprojectId
LEFT JOIN DetailedSample ds2 ON ds2.sampleId = ds1.parentId
LEFT JOIN DetailedSample ds3 ON ds3.sampleId = ds2.parentId
LEFT JOIN DetailedSample ds4 ON ds4.sampleId = ds3.parentId
LEFT JOIN DetailedSample ds5 ON ds5.sampleId = ds4.parentId
LEFT JOIN DetailedSample ds6 ON ds6.sampleId = ds5.parentId
LEFT JOIN DetailedSample ds7 ON ds7.sampleId = ds6.parentId
LEFT JOIN DetailedSample ds8 ON ds8.sampleId = ds7.parentId
LEFT JOIN DetailedSample ds9 ON ds9.sampleId = ds8.parentId
LEFT JOIN DetailedSample ds10 ON ds10.sampleId = ds9.parentId
LEFT JOIN DetailedSample ds11 ON ds11.sampleId = ds10.parentId
LEFT JOIN DetailedSample ds12 ON ds12.sampleId = ds11.parentId
LEFT JOIN DetailedSample ds13 ON ds13.sampleId = ds12.parentId
LEFT JOIN DetailedSample ds14 ON ds14.sampleId = ds13.parentId
LEFT JOIN DetailedSample ds15 ON ds15.sampleId = ds14.parentId
LEFT JOIN Identity ident ON ident.sampleId = COALESCE(
  ds15.sampleId, ds14.sampleId, ds13.sampleId, ds12.sampleId, ds11.sampleId,
  ds10.sampleId, ds9.sampleId, ds8.sampleId, ds7.sampleId, ds6.sampleId,
  ds5.sampleId, ds4.sampleId, ds3.sampleId, ds2.sampleId, ds1.sampleId
);
