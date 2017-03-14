-- fns_custom_indices
-- StartNoTest
-- StartNoTest
SELECT indexFamilyId INTO @nxxId FROM IndexFamily WHERE name = 'Nextera XT Dual Index' AND platformType = 'ILLUMINA';
SELECT indexFamilyId INTO @nxdId FROM IndexFamily WHERE name = 'Nextera DNA Dual Index' AND platformType = 'ILLUMINA';
INSERT INTO IndexFamily(name, platformType, archived) VALUES ('FNS Nextera DNA & XT Dual Index', 'ILLUMINA', TRUE);
SELECT LAST_INSERT_ID() INTO @fnsId;

INSERT INTO Indices(name, sequence, position, indexFamilyId)
  SELECT name, sequence, 1, @fnsId FROM Indices WHERE indexFamilyId = @nxxId AND position = 1
 UNION
  SELECT name, sequence, 2, @fnsId FROM Indices WHERE indexFamilyId = @nxdId AND position = 2;

-- EndNoTest
-- EndNoTest

-- miseq_nano_and_micro
-- StartNoTest
--StartNoTest
SELECT platformId INTO @miseq FROM Platform WHERE instrumentModel = 'Illumina MiSeq';
SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
INSERT INTO SequencingParameters (name, platformId, xpath, readLength, paired, createdBy, creationDate, updatedBy, lastUpdated) values
('Micro 2×151', @miseq, 'count(//RunInfoRead[@NumCycles=151]) = 2 and //FlowcellLayout[@TileCount=4]', 151, 1, @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP),
('Micro 2×251', @miseq, 'count(//RunInfoRead[@NumCycles=251]) = 2 and //FlowcellLayout[@TileCount=4]', 251, 1, @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP),
('Nano 2×151', @miseq, 'count(//RunInfoRead[@NumCycles=151]) = 2 and //FlowcellLayout[@TileCount=2]', 151, 1, @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP),
('Nano 2×251', @miseq, 'count(//RunInfoRead[@NumCycles=251]) = 2 and //FlowcellLayout[@TileCount=2]', 251, 1, @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
--EndNoTest
-- EndNoTest

-- fix_sequencer_name
-- StartNoTest
UPDATE SequencerReference SET name='i277' WHERE name='i227';
-- EndNoTest

