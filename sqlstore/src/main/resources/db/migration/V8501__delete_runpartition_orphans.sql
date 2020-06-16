DELETE FROM Run_Partition
WHERE NOT EXISTS (
  SELECT 1 FROM Run_SequencerPartitionContainer rspc
  JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = rspc.containers_containerId
  WHERE rspc.Run_runId = runId
  AND spcp.partitions_partitionId = partitionId
);

DELETE FROM rpa
USING Run_Partition_LibraryAliquot AS rpa
WHERE NOT EXISTS (
  SELECT 1 FROM Run_SequencerPartitionContainer rspc
  JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = rspc.containers_containerId
  JOIN _Partition part ON part.partitionId = spcp.partitions_partitionId
  JOIN Pool_LibraryAliquot pla ON pla.poolId = part.pool_poolId
  WHERE rspc.Run_runId = rpa.runId
  AND spcp.partitions_partitionId = rpa.partitionId
  AND pla.aliquotId = rpa.aliquotId
);
