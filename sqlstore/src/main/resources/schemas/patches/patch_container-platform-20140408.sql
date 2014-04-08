USE lims;

ALTER TABLE lims.SequencerPartitionContainer CHANGE platformType platform BIGINT(20);

UPDATE SequencerPartitionContainer s,
(SELECT spc.containerId AS cId, p.platformId AS pId, p.name, p.instrumentModel
FROM Platform p
INNER JOIN SequencerReference sr ON sr.platformId = p.platformId
INNER JOIN Run ra ON sr.referenceId = ra.sequencerReference_sequencerReferenceId
INNER JOIN Run_SequencerPartitionContainer rf ON rf.Run_runId = ra.runId
LEFT JOIN SequencerPartitionContainer spc ON spc.containerId = rf.containers_containerId) AS src
SET s.platform = src.pId
WHERE s.containerId=src.cId;

