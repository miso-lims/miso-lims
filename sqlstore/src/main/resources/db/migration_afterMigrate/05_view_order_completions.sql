-- loaded (no health, unknown parameters)
CREATE OR REPLACE VIEW SequencingOrderLoadedPartitionView AS
SELECT
  part.pool_poolId AS poolId,
  spc.sequencingContainerModelId,
  COUNT(*) AS loaded
FROM _Partition part
JOIN SequencerPartitionContainer spc ON spc.containerId = part.containerId
LEFT JOIN Run_SequencerPartitionContainer rspc ON rspc.containers_containerId = spc.containerId
WHERE part.pool_poolId IS NOT NULL
AND rspc.Run_runId IS NULL
GROUP BY part.pool_poolId, spc.sequencingContainerModelId;


CREATE OR REPLACE VIEW SequencingOrderSummaryView AS
SELECT
  CONCAT(o.poolId, '_', COALESCE(o.sequencingContainerModelId, 0), '_', o.parametersId) AS orderSummaryId,
  o.poolId,
  o.sequencingContainerModelId,
  o.parametersId,
  SUM(partitions) AS requested,
  COALESCE(loaded.loaded, 0) AS loaded,
  GROUP_CONCAT(description SEPARATOR '; ') as description,
  GROUP_CONCAT(DISTINCT RunPurpose.alias SEPARATOR '; ') as purpose,
  MAX(lastUpdated) AS lastUpdated
FROM SequencingOrder o
JOIN RunPurpose ON RunPurpose.purposeId = o.purposeId
LEFT JOIN SequencingOrderLoadedPartitionView loaded ON loaded.poolId = o.poolId
  AND (loaded.sequencingContainerModelId = o.sequencingContainerModelId
    OR (loaded.sequencingContainerModelId IS NULL AND o.sequencingContainerModelId IS NULL))
GROUP BY poolId, sequencingContainerModelId, parametersId, loaded.loaded;


-- partitions (used in run)
-- join via orderSummaryId for matching pool+containerModel+params
-- join via noContainerModelId for orders with null containerModel
CREATE OR REPLACE VIEW SequencingOrderPartitionView AS
SELECT
  part.partitionId,
  CONCAT(part.pool_poolId, '_', spc.sequencingContainerModelId, '_', run.sequencingParameters_parametersId) AS orderSummaryId,
  CONCAT(part.pool_poolId, '_0_', run.sequencingParameters_parametersId) AS noContainerModelId,
  run.health
FROM _Partition part
JOIN SequencerPartitionContainer spc ON spc.containerId = part.containerId
JOIN Run_SequencerPartitionContainer rspc ON rspc.containers_containerId = spc.containerId
JOIN Run run ON run.runId = rspc.Run_runId
LEFT JOIN Run_Partition rpqc ON rpqc.runId = run.runId AND rpqc.partitionId = part.partitionId
LEFT JOIN PartitionQCType qct ON qct.partitionQcTypeId = rpqc.partitionQcTypeId
WHERE part.pool_poolId IS NOT NULL
AND (qct.orderFulfilled IS NULL OR qct.orderFulfilled = TRUE);

-- Fulfilled (loaded or health NOT failed/unknown)
-- join via orderSummaryId for matching pool+containerModel+params
CREATE OR REPLACE VIEW SequencingOrderFulfillmentView AS
SELECT
  CONCAT(part.pool_poolId, '_', COALESCE(spc.sequencingContainerModelId, 0), '_', COALESCE(run.sequencingParameters_parametersId, 0)) AS orderSummaryId,
  COUNT(part.partitionId) AS fulfilled
FROM _Partition part
JOIN SequencerPartitionContainer spc ON spc.containerId = part.containerId
LEFT JOIN Run_SequencerPartitionContainer rspc ON rspc.containers_containerId = spc.containerId
LEFT JOIN Run run ON run.runId = rspc.Run_runId
LEFT JOIN Run_Partition rpqc ON rpqc.runId = run.runId AND rpqc.partitionId = part.partitionId
LEFT JOIN PartitionQCType qct ON qct.partitionQcTypeId = rpqc.partitionQcTypeId
WHERE part.pool_poolId IS NOT NULL
AND (qct.orderFulfilled IS NULL OR qct.orderFulfilled = TRUE)
AND (run.health IS NULL OR (run.health <> 'Failed' AND run.health <> 'Unknown'))
GROUP BY part.pool_poolId, spc.sequencingContainerModelId, run.sequencingParameters_parametersId;

-- fulfilled, ignoring containerModel
-- join via orderSummaryId for orders with null containerModel
CREATE OR REPLACE VIEW SequencingOrderNoContainerModelFulfillmentView AS
SELECT
  CONCAT(part.pool_poolId, '_0_', COALESCE(run.sequencingParameters_parametersId, 0)) AS orderSummaryId,
  COUNT(part.partitionId) AS fulfilled
FROM _Partition part
JOIN SequencerPartitionContainer spc ON spc.containerId = part.containerId
LEFT JOIN Run_SequencerPartitionContainer rspc ON rspc.containers_containerId = spc.containerId
LEFT JOIN Run run ON run.runId = rspc.Run_runId
LEFT JOIN Run_Partition rpqc ON rpqc.runId = run.runId AND rpqc.partitionId = part.partitionId
LEFT JOIN PartitionQCType qct ON qct.partitionQcTypeId = rpqc.partitionQcTypeId
WHERE part.pool_poolId IS NOT NULL
AND (qct.orderFulfilled IS NULL OR qct.orderFulfilled = TRUE)
AND (run.health IS NULL OR (run.health <> 'Failed' AND run.health <> 'Unknown'))
GROUP BY part.pool_poolId, run.sequencingParameters_parametersId;
