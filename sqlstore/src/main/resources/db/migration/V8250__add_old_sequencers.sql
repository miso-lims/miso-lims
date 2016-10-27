--StartNoTest
-- get rid of duplicate Ion Torrent PGM
DELETE FROM Platform WHERE platformId = 21;

-- add old sequencers
INSERT INTO SequencerReference (name, ipAddress, platformId, available, serialNumber, dateCommissioned, dateDecommissioned, upgradedSequencerReferenceId) VALUES
('i095', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'Illumina Genome Analyzer II'), 0, NULL, '2008-08-07', '2010-01-11', NULL),
('i227', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'Illumina Genome Analyzer II'), 0, NULL, '2009-07-22', '2011-01-22', NULL),
('i280', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'Illumina Genome Analyzer II'), 0, NULL, '2009-07-22', '2011-01-02', NULL),
('i320', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'Illumina Genome Analyzer II'), 0, NULL, '2009-07-22', '2011-02-13', NULL),
('i580', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'Illumina Genome Analyzer II'), 0, NULL, '2009-07-22', '2011-10-18', NULL),
('i581', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'Illumina Genome Analyzer II'), 0, NULL, '2009-07-22', '2011-10-18', NULL),
('i1551', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'Illumina Genome Analyzer II'), 0, NULL, '2009-10-19', '2012-03-30', NULL),
('i278', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'Illumina Genome Analyzer II'), 0, NULL, '2009-07-22', '2011-01-19', NULL),
('h231', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'Illumina Genome Analyzer II'), 0, NULL, '2010-07-23', '2010-10-19', NULL),
('a075', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'AB SOLiD System'), 0, NULL, '2009-07-22', '2010-12-16', NULL),
('a085', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'AB SOLiD System'), 0, NULL, '2009-07-22', '2011-01-05', NULL),
('a139', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'AB SOLiD System'), 0, NULL, '2009-007-22', '2010-04-03', NULL),
('a151', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'AB SOLiD System'), 0, NULL, '2009-07-22', '2010-05-21', NULL),
('a037', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'AB SOLiD System'), 0, NULL, '2008-08-07', '2010-09-20', NULL),
('a056', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'AB SOLiD System'), 0, NULL, '2009-07-22', '2010-09-20', NULL),
('a035', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'AB SOLiD System'), 0, NULL, '2009-07-22', '2010-09-20', NULL),
('heliscope', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = '454 GS'), 0, NULL, '2009-08-10', '2010-04-10', NULL),
('Sciclone1', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = '454 GS'), 1, 'SS1117R1236', '2012-03-30', NULL, NULL),
('it1', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'Ion Torrent PGM'), 0, '11C031704', '2011-10-24', '2013-08-05', NULL),
('it2', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'Ion Torrent PGM'), 0, '274670063', '2013-04-24', '2013-10-20', NULL),
('ip1', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'AB SOLiD System'), 1, '2456351-0555', '2013-08-19', '2014-04-17', NULL),
('x016', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'AB SOLiD 5500xl'), 0, '23303-016', '2011-08-22', '2011-08-22', NULL),
('NB551056', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE  instrumentModel = 'NextSeq 550'), 1, 'NB551056', '2016-08-08', NULL, NULL);
--EndNoTest