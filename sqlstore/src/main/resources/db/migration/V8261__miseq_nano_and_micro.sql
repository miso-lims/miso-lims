--StartNoTest
SELECT platformId INTO @miseq FROM Platform WHERE instrumentModel = 'Illumina MiSeq';
SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
INSERT INTO SequencingParameters (name, platformId, xpath, readLength, paired, createdBy, creationDate, updatedBy, lastUpdated) values
('Micro 2×151', @miseq, 'count(//RunInfoRead[@NumCycles=151]) = 2 and //FlowcellLayout[@TileCount=4]', 151, 1, @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP),
('Micro 2×251', @miseq, 'count(//RunInfoRead[@NumCycles=251]) = 2 and //FlowcellLayout[@TileCount=4]', 251, 1, @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP),
('Nano 2×151', @miseq, 'count(//RunInfoRead[@NumCycles=151]) = 2 and //FlowcellLayout[@TileCount=2]', 151, 1, @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP),
('Nano 2×251', @miseq, 'count(//RunInfoRead[@NumCycles=251]) = 2 and //FlowcellLayout[@TileCount=2]', 251, 1, @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
--EndNoTest
