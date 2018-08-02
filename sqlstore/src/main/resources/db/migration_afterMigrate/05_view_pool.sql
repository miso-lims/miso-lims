CREATE OR REPLACE VIEW PoolableElementView
AS SELECT
    d.dilutionId,
    d.name AS dilutionName,
    d.concentration AS dilutionConcentration,
    d.concentrationUnits AS dilutionConcentrationUnits,
    d.volume AS dilutionVolume,
    d.volumeUnits AS dilutionVolumeUnits,
    d.ngUsed AS dilutionNgUsed,
    d.volumeUsed AS dilutionVolumeUsed,
    d.identificationBarcode AS dilutionBarcode,
    d.lastUpdated AS lastModified,
    d.creationDate AS created,
    createUser.fullName AS creatorName,
    d.targetedSequencingId AS targetedSequencingId,
    modUser.loginName AS lastModifierName,
    d.preMigrationId AS preMigrationId,
    l.libraryId AS libraryId,
    l.name AS libraryName,
    l.alias AS libraryAlias,
    l.description AS libraryDescription,
    l.identificationBarcode AS libraryBarcode,
    l.lowQuality AS lowQualityLibrary,
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
    box.alias AS boxAlias,
    box.name AS boxName,
    box.identificationBarcode AS boxIdentificationBarcode,
    box.locationBarcode AS boxLocationBarcode
  FROM LibraryDilution d
    LEFT JOIN User modUser ON modUser.userId = d.lastModifier
    JOIN User createUser ON createUser.userId = d.creator
    JOIN Library l ON l.libraryId = d.library_libraryId
    JOIN Sample s ON s.sampleId = l.sample_sampleId
    JOIN Project p ON p.projectId = s.project_projectId
    LEFT JOIN LibrarySelectionType sel ON sel.librarySelectionTypeId = l.librarySelectionType
    LEFT JOIN LibraryStrategyType strat ON strat.libraryStrategyTypeId = l.libraryStrategyType
    LEFT JOIN DilutionBoxPosition dbp ON dbp.dilutionId = d.dilutionId
    LEFT JOIN Box box ON box.boxId = dbp.boxId;
