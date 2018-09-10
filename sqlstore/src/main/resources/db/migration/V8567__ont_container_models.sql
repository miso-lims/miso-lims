-- StartNoTest
INSERT INTO SequencingContainerModel(alias, partitionCount, platformType)
SELECT alias, 1, 'OXFORDNANOPORE' FROM FlowCellVersion;

INSERT INTO SequencingContainerModel_Platform(sequencingContainerModelId, platformId)
SELECT sequencingContainerModelId, (SELECT platformId FROM Platform WHERE instrumentModel = 'MinION')
FROM SequencingContainerModel
WHERE alias LIKE 'FLO-MIN%' AND platformType = 'OXFORDNANOPORE';

INSERT INTO SequencingContainerModel_Platform(sequencingContainerModelId, platformId)
SELECT sequencingContainerModelId, (SELECT platformId FROM Platform WHERE instrumentModel = 'PromethION')
FROM SequencingContainerModel
WHERE alias LIKE 'PRO-%' AND platformType = 'OXFORDNANOPORE';

UPDATE SequencerPartitionContainer spc
JOIN OxfordNanoporeContainer ont ON ont.containerId = spc.containerId
JOIN FlowCellVersion fcv ON fcv.flowCellVersionId = ont.flowCellVersionId
SET spc.sequencingContainerModelId = COALESCE(
  (SELECT sequencingContainerModelId FROM SequencingContainerModel WHERE alias = fcv.alias AND platformType = 'OXFORDNANOPORE'),
  spc.sequencingContainerModelId
);
-- EndNoTest

ALTER TABLE OxfordNanoporeContainer DROP FOREIGN KEY FK_OxfordNanoporeContainer_FlowCellVersion;
ALTER TABLE OxfordNanoporeContainer DROP COLUMN flowCellVersionId;
DROP TABLE FlowCellVersion;
