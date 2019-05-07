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

CREATE OR REPLACE VIEW DilutionBoxPosition
AS SELECT d.dilutionId, bp.boxId, bp.position
FROM LibraryDilution d
JOIN BoxPosition bp
  ON bp.targetType = 'DILUTION'
  AND bp.targetId = d.dilutionId;

CREATE OR REPLACE VIEW PoolBoxPosition
AS SELECT p.poolId, bp.boxId, bp.position
FROM Pool p
JOIN BoxPosition bp
  ON bp.targetType = 'POOL'
  AND bp.targetId = p.poolId;

CREATE OR REPLACE VIEW SampleBoxableView AS
  SELECT s.sampleId AS targetId, 'SAMPLE' AS targetType, s.name, s.alias, s.identificationBarcode, s.locationBarcode,
    s.volume, s.discarded, s.distributed, ds.preMigrationId, ds.sampleClassId, bp.boxId AS boxId, bp.position AS boxPosition,
    b.name AS boxName, b.alias AS boxAlias, b.locationBarcode AS boxLocationBarcode
  FROM Sample s
  LEFT JOIN DetailedSample ds ON ds.sampleId = s.sampleId
  LEFT JOIN BoxPosition bp ON bp.targetId = s.sampleId AND bp.targetType = 'SAMPLE'
  LEFT JOIN Box b ON b.boxId = bp.boxId;

CREATE OR REPLACE VIEW LibraryBoxableView AS
  SELECT l.libraryId AS targetId, 'LIBRARY' AS targetType, l.name, l.alias, l.identificationBarcode, l.locationBarcode,
    l.volume, l.discarded, l.distributed, dl.preMigrationId, NULL AS sampleClassId, bp.boxId, bp.position AS boxPosition,
    b.name AS boxName, b.alias AS boxAlias, b.locationBarcode AS boxLocationBarcode
  FROM Library l
  LEFT JOIN DetailedLibrary dl ON dl.libraryId = l.libraryId
  LEFT JOIN BoxPosition bp ON bp.targetId = l.libraryId AND bp.targetType = 'LIBRARY'
  LEFT JOIN Box b ON b.boxId = bp.boxId;

CREATE OR REPLACE VIEW DilutionBoxableView AS
  SELECT dilutionId AS targetId, 'DILUTION' AS targetType, LibraryDilution.name, LibraryDilution.name AS alias,
    LibraryDilution.identificationBarcode, NULL AS locationBarcode, volume, discarded, distributed, preMigrationId,
    NULL AS sampleClassId, bp.boxId, bp.position AS boxPosition, b.name AS boxName, b.alias AS boxAlias,
    b.locationBarcode AS boxLocationBarcode
  FROM LibraryDilution
  LEFT JOIN BoxPosition bp ON bp.targetId = dilutionId AND bp.targetType = 'DILUTION'
  LEFT JOIN Box b ON b.boxId = bp.boxId;

CREATE OR REPLACE VIEW PoolBoxableView AS
  SELECT poolId AS targetId, 'POOL' AS targetType, Pool.name, Pool.alias, Pool.identificationBarcode,
    NULL AS locationBarcode, volume, discarded, distributed, NULL AS preMigrationId, NULL AS sampleClassId, bp.boxId,
    bp.position AS boxPosition, b.name AS boxName, b.alias AS boxAlias, b.locationBarcode AS boxLocationBarcode
  FROM Pool
  LEFT JOIN BoxPosition bp ON bp.targetId = poolId AND bp.targetType = 'POOL'
  LEFT JOIN Box b ON b.boxId = bp.boxId;
