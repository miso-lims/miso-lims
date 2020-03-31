CREATE OR REPLACE VIEW SampleBoxPosition
AS SELECT s.sampleId, bp.boxId, bp.position
FROM Sample s
JOIN BoxPosition bp
  ON bp.targetType = 'SAMPLE'
  AND bp.targetId = s.sampleId;

CREATE OR REPLACE VIEW LibraryBoxPosition
AS SELECT l.libraryId, bp.boxId, bp.position
FROM Library l
JOIN BoxPosition bp
  ON bp.targetType = 'LIBRARY'
  AND bp.targetId = l.libraryId;

CREATE OR REPLACE VIEW LibraryAliquotBoxPosition
AS SELECT d.aliquotId, bp.boxId, bp.position
FROM LibraryAliquot d
JOIN BoxPosition bp
  ON bp.targetType = 'LIBRARY_ALIQUOT'
  AND bp.targetId = d.aliquotId;

CREATE OR REPLACE VIEW PoolBoxPosition
AS SELECT p.poolId, bp.boxId, bp.position
FROM Pool p
JOIN BoxPosition bp
  ON bp.targetType = 'POOL'
  AND bp.targetId = p.poolId;

CREATE OR REPLACE VIEW SampleDistributionView AS
  SELECT s.sampleId, IF(SUM(IF(t.recipient IS NOT NULL, 1, 0)) > 0, TRUE, FALSE) as distributed
  FROM Sample s
  LEFT JOIN Transfer_Sample ts ON ts.sampleId = s.sampleId
  LEFT JOIN Transfer t ON t.transferId = ts.transferId
  GROUP BY s.sampleId;

CREATE OR REPLACE VIEW LibraryDistributionView AS
  SELECT l.libraryId, IF(SUM(IF(t.recipient IS NOT NULL, 1, 0)) > 0, TRUE, FALSE) as distributed
  FROM Library l
  LEFT JOIN Transfer_Library tl ON tl.libraryId = l.libraryId
  LEFT JOIN Transfer t ON t.transferId = tl.transferId
  GROUP BY l.libraryId;

CREATE OR REPLACE VIEW LibraryAliquotDistributionView AS
  SELECT la.aliquotId, IF(SUM(IF(t.recipient IS NOT NULL, 1, 0)) > 0, TRUE, FALSE) as distributed
  FROM LibraryAliquot la
  LEFT JOIN Transfer_LibraryAliquot tla ON tla.aliquotId = la.aliquotId
  LEFT JOIN Transfer t ON t.transferId = tla.transferId
  GROUP BY la.aliquotId;

CREATE OR REPLACE VIEW PoolDistributionView AS
  SELECT p.poolId, IF(SUM(IF(t.recipient IS NOT NULL, 1, 0)) > 0, TRUE, FALSE) as distributed
  FROM Pool p
  LEFT JOIN Transfer_Pool tp ON tp.poolId = p.poolId
  LEFT JOIN Transfer t ON t.transferId = tp.transferId
  GROUP BY p.poolId;

CREATE OR REPLACE VIEW SampleBoxableView AS
  SELECT s.sampleId AS targetId, 'SAMPLE' AS targetType, s.name, s.alias, s.identificationBarcode, s.locationBarcode,
    s.volume, s.discarded, dist.distributed, s.preMigrationId, s.sampleClassId, bp.boxId AS boxId, bp.position AS boxPosition,
    b.name AS boxName, b.alias AS boxAlias, b.locationBarcode AS boxLocationBarcode
  FROM Sample s
  LEFT JOIN BoxPosition bp ON bp.targetId = s.sampleId AND bp.targetType = 'SAMPLE'
  LEFT JOIN Box b ON b.boxId = bp.boxId
  JOIN SampleDistributionView dist ON dist.sampleId = s.sampleId;

CREATE OR REPLACE VIEW LibraryBoxableView AS
  SELECT l.libraryId AS targetId, 'LIBRARY' AS targetType, l.name, l.alias, l.identificationBarcode, l.locationBarcode,
    l.volume, l.discarded, dist.distributed, l.preMigrationId, NULL AS sampleClassId, bp.boxId, bp.position AS boxPosition,
    b.name AS boxName, b.alias AS boxAlias, b.locationBarcode AS boxLocationBarcode
  FROM Library l
  LEFT JOIN BoxPosition bp ON bp.targetId = l.libraryId AND bp.targetType = 'LIBRARY'
  LEFT JOIN Box b ON b.boxId = bp.boxId
  JOIN LibraryDistributionView dist ON dist.libraryId = l.libraryId;

CREATE OR REPLACE VIEW LibraryAliquotBoxableView AS
  SELECT la.aliquotId AS targetId, 'LIBRARY_ALIQUOT' AS targetType, la.name, la.alias, la.identificationBarcode,
    NULL AS locationBarcode, la.volume, la.discarded, dist.distributed, la.preMigrationId, NULL AS sampleClassId, bp.boxId,
    bp.position AS boxPosition, b.name AS boxName, b.alias AS boxAlias, b.locationBarcode AS boxLocationBarcode
  FROM LibraryAliquot la
  LEFT JOIN BoxPosition bp ON bp.targetId = la.aliquotId AND bp.targetType = 'LIBRARY_ALIQUOT'
  LEFT JOIN Box b ON b.boxId = bp.boxId
  JOIN LibraryAliquotDistributionView dist ON dist.aliquotId = la.aliquotId;

CREATE OR REPLACE VIEW PoolBoxableView AS
  SELECT p.poolId AS targetId, 'POOL' AS targetType, p.name, p.alias, p.identificationBarcode,
    NULL AS locationBarcode, p.volume, p.discarded, dist.distributed, NULL AS preMigrationId, NULL AS sampleClassId, bp.boxId,
    bp.position AS boxPosition, b.name AS boxName, b.alias AS boxAlias, b.locationBarcode AS boxLocationBarcode
  FROM Pool p
  LEFT JOIN BoxPosition bp ON bp.targetId = p.poolId AND bp.targetType = 'POOL'
  LEFT JOIN Box b ON b.boxId = bp.boxId
  JOIN PoolDistributionView dist ON dist.poolId = p.poolId;
