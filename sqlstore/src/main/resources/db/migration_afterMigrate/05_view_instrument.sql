CREATE OR REPLACE VIEW InstrumentPositionStatus AS
SELECT
  inst.instrumentId,
  ipos.positionId,
  COALESCE(ipos.positionId, -1) AS positionKey,
  sub1.runId,
  COALESCE(sub1.runId, -1) AS runKey
FROM Instrument inst
JOIN InstrumentModel im ON im.instrumentModelId = inst.instrumentModelId
LEFT JOIN InstrumentPosition ipos ON ipos.instrumentModelId = im.instrumentModelId
LEFT JOIN (
  SELECT sub2.instrumentId, sub2.positionId, r.runId FROM Run r
  JOIN Run_SequencerPartitionContainer rspc ON rspc.Run_runId = r.runId
  JOIN (
    SELECT inst.instrumentId, ipos.positionId, MAX(r.startDate) AS startDate
    FROM Run r
    JOIN Run_SequencerPartitionContainer rspc ON rspc.Run_runId = r.runId
    JOIN Instrument inst ON inst.instrumentId = r.instrumentId
    LEFT JOIN InstrumentPosition ipos ON ipos.positionId = rspc.positionId
    GROUP BY inst.instrumentId, ipos.positionId
  ) sub2 ON sub2.instrumentId = r.instrumentId
    AND ((sub2.positionId IS NULL AND rspc.positionId IS NULL) OR sub2.positionId = rspc.positionId)
    AND sub2.startDate = r.startDate
) sub1 ON sub1.instrumentId = inst.instrumentId
  AND ((sub1.positionId IS NULL AND ipos.positionId IS NULL) OR sub1.positionId = ipos.positionId)
WHERE im.instrumentType = 'SEQUENCER'
AND inst.dateDecommissioned IS NULL;
