ALTER TABLE OxfordNanoporeContainer MODIFY COLUMN receivedDate DATE;

INSERT INTO OxfordNanoporeContainer (containerId)
SELECT containerId FROM SequencerPartitionContainer c
JOIN SequencingContainerModel m ON m.sequencingContainerModelId = c.sequencingContainerModelId
WHERE m.platformType = 'OXFORDNANOPORE'
AND containerId NOT IN (SELECT containerId FROM OxfordNanoporeContainer);
