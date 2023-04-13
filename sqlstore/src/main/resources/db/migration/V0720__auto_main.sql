-- sequencer_positions

DROP VIEW IF EXISTS InstrumentStats;

DROP TABLE IF EXISTS PlatformPosition;
CREATE TABLE PlatformPosition (
  positionId bigint NOT NULL AUTO_INCREMENT,
  platformId bigint NOT NULL,
  alias varchar(10) NOT NULL,
  PRIMARY KEY (positionId),
  CONSTRAINT fk_platformPosition_platform FOREIGN KEY (platformId) REFERENCES Platform (platformId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE ServiceRecord ADD COLUMN positionId bigint;
ALTER TABLE ServiceRecord ADD CONSTRAINT fk_serviceRecord_position FOREIGN KEY (positionId) REFERENCES PlatformPosition (positionId);

ALTER TABLE Run_SequencerPartitionContainer ADD COLUMN positionId bigint;
ALTER TABLE Run_SequencerPartitionContainer ADD CONSTRAINT fk_run_container_position FOREIGN KEY (positionId) REFERENCES PlatformPosition (positionId);

-- StartNoTest
-- Add positions for Illumina HiSeq and NovaSeq
INSERT INTO PlatformPosition (platformId, alias)
SELECT platformId, 'A' FROM Platform WHERE instrumentModel LIKE 'Illumina HiSeq%' OR instrumentModel LIKE 'Illumina NovaSeq%';
INSERT INTO PlatformPosition (platformId, alias)
SELECT platformId, 'B' FROM Platform WHERE instrumentModel LIKE 'Illumina HiSeq%' OR instrumentModel LIKE 'Illumina NovaSeq%';

-- Add positions for Oxford Nanopore PromethION
SELECT platformId INTO @promethion FROM Platform WHERE instrumentModel = 'PromethION';
UPDATE Platform SET numContainers = 1 WHERE platformId = @promethion;
INSERT INTO PlatformPosition (platformId, alias) VALUES
(@promethion, 'P101_0'),
(@promethion, 'P101_1'),
(@promethion, 'P102_0'),
(@promethion, 'P102_1'),
(@promethion, 'P103_0'),
(@promethion, 'P103_1'),
(@promethion, 'P104_0'),
(@promethion, 'P104_1'),
(@promethion, 'P105_0'),
(@promethion, 'P105_1'),
(@promethion, 'P106_0'),
(@promethion, 'P106_1'),
(@promethion, 'P107_0'),
(@promethion, 'P107_1'),
(@promethion, 'P108_0'),
(@promethion, 'P108_1'),
(@promethion, 'P109_0'),
(@promethion, 'P109_1'),
(@promethion, 'P110_0'),
(@promethion, 'P110_1'),
(@promethion, 'P111_0'),
(@promethion, 'P111_1'),
(@promethion, 'P112_0'),
(@promethion, 'P112_1'),
(@promethion, 'P113_0'),
(@promethion, 'P113_1'),
(@promethion, 'P114_0'),
(@promethion, 'P114_1'),
(@promethion, 'P115_0'),
(@promethion, 'P115_1'),
(@promethion, 'P116_0'),
(@promethion, 'P116_1'),
(@promethion, 'P117_0'),
(@promethion, 'P117_1'),
(@promethion, 'P118_0'),
(@promethion, 'P118_1'),
(@promethion, 'P119_0'),
(@promethion, 'P119_1'),
(@promethion, 'P120_0'),
(@promethion, 'P120_1'),
(@promethion, 'P121_0'),
(@promethion, 'P121_1'),
(@promethion, 'P122_0'),
(@promethion, 'P122_1'),
(@promethion, 'P123_0'),
(@promethion, 'P123_1'),
(@promethion, 'P124_0'),
(@promethion, 'P124_1');

-- Set position for existing runs
UPDATE Run_SequencerPartitionContainer rspc
JOIN Run r ON r.runId = rspc.run_runId
JOIN Instrument inst ON inst.instrumentId = r.instrumentId
JOIN Platform p ON p.platformId = inst.platformId
SET rspc.positionId = (
  SELECT positionId FROM PlatformPosition
  WHERE platformId = inst.platformId
  AND alias = 'A'
)
WHERE (p.instrumentModel LIKE 'Illumina HiSeq%' OR p.instrumentModel LIKE 'Illumina NovaSeq%')
AND alias REGEXP '^[0-9]{6}_.*_[0-9]{4}_A.*$';

UPDATE Run_SequencerPartitionContainer rspc
JOIN Run r ON r.runId = rspc.run_runId
JOIN Instrument inst ON inst.instrumentId = r.instrumentId
JOIN Platform p ON p.platformId = inst.platformId
SET rspc.positionId = (
  SELECT positionId FROM PlatformPosition
  WHERE platformId = inst.platformId
  AND alias = 'B'
)
WHERE (p.instrumentModel LIKE 'Illumina HiSeq%' OR p.instrumentModel LIKE 'Illumina NovaSeq%')
AND alias REGEXP '^[0-9]{6}_.*_[0-9]{4}_B.*$';
-- EndNoTest


-- loading_concentration

ALTER TABLE _Partition ADD COLUMN loadingConcentration DECIMAL(14,10);
ALTER TABLE _Partition ADD COLUMN loadingConcentrationUnits varchar(30);


