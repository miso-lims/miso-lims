CREATE OR REPLACE VIEW ActivePlatformTypes AS
  SELECT DISTINCT im.platform AS platform
  FROM Instrument inst
  JOIN InstrumentModel im ON im.instrumentModelId = inst.instrumentModelId
  WHERE im.instrumentType = 'SEQUENCER'
  AND inst.dateDecommissioned IS NULL

  UNION SELECT DISTINCT platformType FROM Pool

  UNION SELECT DISTINCT platformType FROM Library;
