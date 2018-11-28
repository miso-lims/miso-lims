CREATE OR REPLACE VIEW InstrumentPositionStatus AS
SELECT
  inst.instrumentId,
  ppos.positionId,
  COALESCE(ppos.positionId, -1) AS positionKey,
  sub1.runId,
  COALESCE(sub1.runId, -1) AS runKey
FROM Instrument inst
JOIN Platform plat ON plat.platformId = inst.platformId
LEFT JOIN PlatformPosition ppos ON ppos.platformId = plat.platformId
LEFT JOIN (
  SELECT sub2.instrumentId, sub2.positionId, r.runId FROM Run r
  JOIN Run_SequencerPartitionContainer rspc ON rspc.Run_runId = r.runId
  JOIN (
    SELECT inst.instrumentId, ppos.positionId, MAX(r.startDate) AS startDate
    FROM Run r
    JOIN Run_SequencerPartitionContainer rspc ON rspc.Run_runId = r.runId
    JOIN Instrument inst ON inst.instrumentId = r.instrumentId
    LEFT JOIN PlatformPosition ppos ON ppos.positionId = rspc.positionId
    GROUP BY inst.instrumentId, ppos.positionId
  ) sub2 ON sub2.instrumentId = r.instrumentId
    AND ((sub2.positionId IS NULL AND rspc.positionId IS NULL) OR sub2.positionId = rspc.positionId)
    AND sub2.startDate = r.startDate
) sub1 ON sub1.instrumentId = inst.instrumentId
  AND ((sub1.positionId IS NULL AND ppos.positionId IS NULL) OR sub1.positionId = ppos.positionId)
WHERE plat.instrumentType = 'SEQUENCER'
AND inst.dateDecommissioned IS NULL;
