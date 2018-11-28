CREATE OR REPLACE VIEW ActivePlatformTypes AS
  SELECT DISTINCT plat.name AS platform
  FROM Instrument inst
  JOIN Platform plat ON plat.platformId = inst.platformId
  WHERE plat.instrumentType = 'SEQUENCER'
  AND inst.dateDecommissioned IS NULL

  UNION SELECT DISTINCT platformType FROM Pool

  UNION SELECT DISTINCT platformType FROM Library;
