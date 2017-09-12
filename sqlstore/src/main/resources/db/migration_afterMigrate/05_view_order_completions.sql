DROP VIEW IF EXISTS CompletedPartitions;
CREATE OR REPLACE VIEW RunPartitionsByHealth AS
  SELECT pool_poolId AS poolId, sequencingParameters_parametersId as parametersId, COUNT(*) AS num_partitions, health AS health, MAX((SELECT MAX(changeTime) FROM RunChangeLog WHERE RunChangeLog.runId = Run.runId)) as lastUpdated
    FROM Run JOIN Run_SequencerPartitionContainer ON Run.runId = Run_SequencerPartitionContainer.Run_runId
     JOIN SequencerPartitionContainer_Partition ON Run_SequencerPartitionContainer.containers_containerId = SequencerPartitionContainer_Partition.container_containerId
     JOIN _Partition ON SequencerPartitionContainer_Partition.partitions_partitionId = _Partition.partitionId
    WHERE sequencingParameters_parametersId IS NOT NULL AND pool_poolId IS NOT NULL
    GROUP BY pool_poolId, sequencingParameters_parametersId, health;

CREATE OR REPLACE VIEW DesiredPartitions AS
  SELECT poolId, parametersId, SUM(partitions) AS num_partitions, MAX(lastUpdated) as lastUpdated
    FROM PoolOrder
    GROUP BY poolId, parametersId;

CREATE OR REPLACE VIEW OrderCompletion_Backing AS
  (SELECT
    `RunPartitionsByHealth`.`poolId` AS `poolId`,
    `RunPartitionsByHealth`.`parametersId` AS `parametersId`,
    `RunPartitionsByHealth`.`num_partitions` AS `num_partitions`,
    `RunPartitionsByHealth`.`health` AS `health`,
    `RunPartitionsByHealth`.`lastUpdated` AS `lastUpdated` from `RunPartitionsByHealth`
  ) UNION ALL (SELECT
    `DesiredPartitions`.`poolId` AS `poolId`,
    `DesiredPartitions`.`parametersId` AS `parametersId`,
    `DesiredPartitions`.`num_partitions` AS `num_partitions`,
    'Requested' AS `health`,
    `DesiredPartitions`.`lastUpdated` AS `lastUpdated` FROM `DesiredPartitions`);

CREATE OR REPLACE VIEW OrderCompletion AS SELECT
    poolId,
    parametersId,
    MAX(lastUpdated) as lastUpdated,
    GREATEST(0, SUM(num_partitions * (CASE health
      WHEN 'Requested' THEN 1
      WHEN 'Unknown' THEN 0
      WHEN 'Failed' THEN 0
      ELSE -1 END))) AS remaining
  FROM OrderCompletion_Backing
  GROUP BY poolId, parametersId;

CREATE OR REPLACE VIEW OrderCompletion_Items AS SELECT poolId, parametersId, health, num_partitions FROM OrderCompletion_Backing;
