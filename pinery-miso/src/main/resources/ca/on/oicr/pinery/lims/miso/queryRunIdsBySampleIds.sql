SELECT DISTINCT runId
FROM Run r
JOIN Run_SequencerPartitionContainer rspc ON rspc.Run_runId = r.runId
JOIN _Partition part ON part.containerId = rspc.containers_containerId
JOIN Pool p ON p.poolId = part.pool_poolId
JOIN Pool_LibraryAliquot pla ON pla.poolId = p.poolId
