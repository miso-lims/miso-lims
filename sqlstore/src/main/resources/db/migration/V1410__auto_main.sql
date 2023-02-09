-- delete_orphaned_runlibs
DELETE FROM Run_Partition_LibraryAliquot
WHERE NOT EXISTS (
  SELECT 1
  FROM Run_SequencerPartitionContainer rspc
  JOIN _Partition part ON part.containerId = rspc.containers_containerId
  JOIN Pool_LibraryAliquot pla ON pla.poolId = part.pool_poolId
  WHERE rspc.Run_runId = runId
  AND part.partitionId = partitionId
  AND pla.aliquotId = aliquotId
);

