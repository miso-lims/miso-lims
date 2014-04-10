USE lims;

UPDATE SequencerPartitionContainer s SET s.platformType = 0;

UPDATE SequencerPartitionContainer s,
(SELECT spc.containerId AS cId, p.platformId AS pId, p.name, p.instrumentModel
FROM Platform p
INNER JOIN SequencerReference sr ON sr.platformId = p.platformId
INNER JOIN Run ra ON sr.referenceId = ra.sequencerReference_sequencerReferenceId
INNER JOIN Run_SequencerPartitionContainer rf ON rf.Run_runId = ra.runId
LEFT JOIN SequencerPartitionContainer spc ON spc.containerId = rf.containers_containerId) AS src
SET s.platformType = src.pId
WHERE s.containerId=src.cId;

ALTER TABLE lims.SequencerPartitionContainer CHANGE platformType platform BIGINT(20);