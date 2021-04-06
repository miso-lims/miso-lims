CREATE OR REPLACE VIEW SampleBoxPosition
AS SELECT targetId AS sampleId, boxId, position
FROM BoxPosition
WHERE targetType = 'SAMPLE';

CREATE OR REPLACE VIEW LibraryBoxPosition
AS SELECT targetId AS libraryId, boxId, position
FROM BoxPosition
WHERE targetType = 'LIBRARY';

CREATE OR REPLACE VIEW LibraryAliquotBoxPosition
AS SELECT targetId AS aliquotId, boxId, position
FROM BoxPosition
WHERE targetType = 'LIBRARY_ALIQUOT';

CREATE OR REPLACE VIEW PoolBoxPosition
AS SELECT targetId AS poolId, boxId, position
FROM BoxPosition
WHERE targetType = 'POOL';


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
