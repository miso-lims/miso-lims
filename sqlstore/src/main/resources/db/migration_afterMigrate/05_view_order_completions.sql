DROP VIEW IF EXISTS CompletedPartitions;
CREATE OR REPLACE VIEW RunPartitionsByHealth AS
  SELECT pool_poolId AS poolId, sequencingParameters_parametersId as parametersId, COUNT(*) AS num_partitions, health AS health, MAX((SELECT MAX(changeTime) FROM RunChangeLog WHERE RunChangeLog.runId = Run.runId)) as lastUpdated, NULL AS description
    FROM Run JOIN Run_SequencerPartitionContainer ON Run.runId = Run_SequencerPartitionContainer.Run_runId
     JOIN SequencerPartitionContainer_Partition ON Run_SequencerPartitionContainer.containers_containerId = SequencerPartitionContainer_Partition.container_containerId
     JOIN _Partition ON SequencerPartitionContainer_Partition.partitions_partitionId = _Partition.partitionId
     LEFT JOIN Run_Partition_QC rpqc ON rpqc.runId = Run.runId AND rpqc.partitionId = _Partition.partitionId
     LEFT JOIN PartitionQCType qct ON qct.partitionQcTypeId = rpqc.partitionQcTypeId
    WHERE sequencingParameters_parametersId IS NOT NULL AND pool_poolId IS NOT NULL
    AND (qct.orderFulfilled IS NULL OR qct.orderFulfilled = TRUE)
    GROUP BY pool_poolId, sequencingParameters_parametersId, health;

CREATE OR REPLACE VIEW DesiredPartitions AS 
  SELECT poolId, parametersId, SUM(partitions) AS num_partitions, MAX(lastUpdated) as lastUpdated, GROUP_CONCAT(description SEPARATOR '; ') as description
    FROM PoolOrder
    GROUP BY poolId, parametersId;

CREATE OR REPLACE VIEW OrderCompletion_Backing AS
  (SELECT
    `RunPartitionsByHealth`.`poolId` AS `poolId`,
    `RunPartitionsByHealth`.`parametersId` AS `parametersId`,
    `RunPartitionsByHealth`.`num_partitions` AS `num_partitions`,
    `RunPartitionsByHealth`.`health` AS `health`,
    `RunPartitionsByHealth`.`lastUpdated` AS `lastUpdated`, 
    `RunPartitionsByHealth`.`description` AS `description` 
    from `RunPartitionsByHealth`
  ) UNION ALL (SELECT
    `DesiredPartitions`.`poolId` AS `poolId`,
    `DesiredPartitions`.`parametersId` AS `parametersId`,
    `DesiredPartitions`.`num_partitions` AS `num_partitions`,
    'Requested' AS `health`,
    `DesiredPartitions`.`lastUpdated` AS `lastUpdated`,
    `DesiredPartitions`.`description` AS `description` 
    FROM `DesiredPartitions`);

CREATE OR REPLACE VIEW OrderCompletion AS SELECT
    poolId,
    parametersId,
    MAX(lastUpdated) as lastUpdated,
    GREATEST(0, SUM(num_partitions * (CASE health
      WHEN 'Requested' THEN 1
      WHEN 'Unknown' THEN 0
      WHEN 'Failed' THEN 0
      ELSE -1 END))) AS remaining,
    COALESCE((SELECT COUNT(*)
          FROM SequencerPartitionContainer_Partition
           JOIN _Partition ON SequencerPartitionContainer_Partition.partitions_partitionId = _Partition.partitionId
        WHERE _Partition.pool_poolId = OrderCompletion_Backing.poolId
          AND NOT EXISTS(SELECT *
            FROM Run_SequencerPartitionContainer
            WHERE Run_SequencerPartitionContainer.containers_containerId = SequencerPartitionContainer_Partition.container_containerId)), 0) AS loaded,
    GROUP_CONCAT(description SEPARATOR '; ') as description
  FROM OrderCompletion_Backing
  GROUP BY poolId, parametersId;

CREATE OR REPLACE VIEW OrderCompletion_Items AS SELECT poolId, parametersId, health, num_partitions FROM OrderCompletion_Backing;
