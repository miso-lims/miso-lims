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
  p.dnaSize,
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
  la.aliquotId,
  la.name,
  la.alias,
  la.dnaSize,
  lib.libraryId,
  lib.lowQuality,
  lib.index1Id,
  lib.index2Id,
  sam.project_projectId AS projectId,
  sp.alias AS subprojectAlias,
  sp.priority AS subprojectPriority,
  ident.consentLevel
FROM LibraryAliquot la
JOIN Library lib ON lib.libraryId = la.libraryId
JOIN Sample sam ON sam.sampleId = lib.sample_sampleId
LEFT JOIN Subproject sp ON sp.subprojectId = sam.subprojectId
LEFT JOIN SampleHierarchy sh ON sh.sampleId = sam.sampleId
LEFT JOIN Sample ident ON ident.sampleId = sh.identityId;
