DELETE FROM SequencingParameters WHERE SequencingParameters.name = 'v4 1×136';

INSERT INTO Platform (name, instrumentModel, description, numContainers)
VALUES ('Illumina', 'NextSeq 550', '4-channel flowgram', 1);

INSERT INTO `SequencingParameters` (`platformId`, `name`, `createdBy`, `updatedBy`, `creationDate`, `lastUpdated`, `readLength`, `paired`, `xpath`)
VALUES
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'NextSeq 550'),'2×151', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 151, TRUE, '//PlannedRead1Cycles = 151 and //PlannedRead2Cycles = 151'),
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'NextSeq 550'),'2×75', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 75, TRUE, '//PlannedRead1Cycles = 75 and //PlannedRead2Cycles = 75'),
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'NextSeq 550'),'1×151', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 151, FALSE, '//PlannedRead1Cycles = 151 and not //PlannedRead2Cycles'),
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'NextSeq 550'),'1×75', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 75, FALSE, '//PlannedRead1Cycles = 75 and not //PlannedRead2Cycles'),
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'Illumina HiSeq 2500'),'2×251', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 251, TRUE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=251]) = 2'),
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'Illumina MiSeq'),'1×51', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 51, FALSE, 'count(//RunInfoRead[@NumCycles=51]) = 1'),
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'Illumina MiSeq'),'1×36', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 36, FALSE, 'count(//RunInfoRead[@NumCycles=36]) = 1'),
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'Illumina MiSeq'),'1×151', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 151, FALSE, 'count(//RunInfoRead[@NumCycles=151]) = 1'),
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'Illumina MiSeq'),'2x25', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 25, TRUE, 'count(//RunInfoRead[@NumCycles=25]) = 2'),
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'Illumina MiSeq'),'2x251', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 251, TRUE, 'count(//RunInfoRead[@NumCycles=251]) = 2'),
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'Illumina MiSeq'),'2x76', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 76, TRUE, 'count(//RunInfoRead[@NumCycles=76]) = 2'),
    ((SELECT platformId from Platform where Platform.`instrumentModel` = 'Illumina MiSeq'),'2x301', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 301, TRUE, 'count(//RunInfoRead[@NumCycles=301]) = 2');

DELETE FROM SequencerReference WHERE SequencerReference.name = 'p00118';

INSERT INTO SequencerReference (name, ipAddress, platformId, available, serialNUmber, dateCommissioned, dateDecommissioned, upgradedSequencerReferenceId)
VALUES 
    ('NB551051', UNHEX('7F000001'), (SELECT platformId from Platform where Platform.`instrumentModel` = 'NextSeq 550'), 1, 'NB551051', '2016-04-26', NULL, NULL),
    ('00118', UNHEX('7F000001'), (SELECT platformId from Platform where Platform.`instrumentModel` = 'PacBio RS'), 1, '00118', '2010-08-16', NULL, NULL);