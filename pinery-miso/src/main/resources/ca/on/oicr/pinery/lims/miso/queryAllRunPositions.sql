SELECT p.partitionId
  ,p.partitionNumber
  ,pqt.analysisSkipped AS analysis_skipped
  ,pqt.description AS qc_status
  ,r_spc.Run_runId
  ,pool.alias AS pool_name
  ,pool.identificationBarcode AS pool_barcode
  ,pool.description AS pool_description
  ,pool.creator AS pool_createdById
  ,pool.creationDate AS pool_created
FROM _Partition AS p
LEFT JOIN Pool pool ON pool.poolId = p.pool_poolId
JOIN SequencerPartitionContainer_Partition AS spc_p ON spc_p.partitions_partitionId = p.partitionId
JOIN Run_SequencerPartitionContainer AS r_spc ON r_spc.containers_containerId = spc_p.container_containerId
LEFT JOIN Run_Partition_QC rpq ON rpq.runId = r_spc.Run_runId AND rpq.partitionId = p.partitionId
LEFT JOIN PartitionQCType pqt ON pqt.partitionQcTypeId = rpq.partitionQcTypeId
