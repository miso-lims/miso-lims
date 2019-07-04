CREATE OR REPLACE VIEW PoolableElementView
AS SELECT
    d.aliquotId,
    d.name AS aliquotName,
    d.alias AS aliquotAlias,
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
    l.dnaSize AS libraryDnaSize,
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
