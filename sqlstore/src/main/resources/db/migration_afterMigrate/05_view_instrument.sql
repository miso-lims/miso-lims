CREATE OR REPLACE VIEW InstrumentStatusView AS
SELECT inst.instrumentId, inst.name
FROM Instrument inst
JOIN InstrumentModel im ON im.instrumentModelId = inst.instrumentModelId
WHERE inst.dateDecommissioned IS NULL
AND im.instrumentType = 'SEQUENCER';

CREATE OR REPLACE VIEW InstrumentStatusPositionView AS
SELECT inst.instrumentId, COALESCE(ipos.positionId, -1) AS positionId, ipos.alias, sr.outOfServiceTime
FROM Instrument inst
LEFT JOIN InstrumentPosition ipos ON ipos.instrumentModelId = inst.instrumentModelId
LEFT JOIN (
  SELECT instrumentId, positionId, MIN(startTime) AS outOfServiceTime
  FROM ServiceRecord rec INNER JOIN Instrument_ServiceRecord instrec ON instrec.recordId = rec.recordId
  WHERE outOfService = TRUE AND startTime IS NOT NULL AND endTime IS NULL
  GROUP BY instrumentId, positionId
) sr ON sr.instrumentId = inst.instrumentId AND (sr.positionId IS NULL OR sr.positionId = ipos.positionId);

CREATE OR REPLACE VIEW InstrumentStatusPositionRunView AS
SELECT r.runId, r.name, r.alias, r.instrumentId, r.health, r.startDate, r.completionDate, r.lastModified,
  COALESCE(rspc.positionId, -1) AS positionId
FROM Run r
LEFT JOIN Run_SequencerPartitionContainer rspc ON rspc.Run_runId = r.runId
ORDER BY COALESCE(r.completionDate, r.startDate) DESC;

CREATE OR REPLACE VIEW InstrumentStatusPositionRunPoolView AS
SELECT rspc.Run_runId AS runId, COALESCE(rspc.positionId, -1) AS positionId, part.partitionId, pool.poolId, pool.name, pool.alias
FROM Run_SequencerPartitionContainer rspc
JOIN _Partition part ON part.containerId = rspc.containers_containerId
JOIN Pool pool ON pool.poolId = part.pool_poolId;
