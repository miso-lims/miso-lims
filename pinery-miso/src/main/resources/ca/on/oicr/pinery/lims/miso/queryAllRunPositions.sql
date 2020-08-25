SELECT p.partitionId
  ,p.partitionNumber
  ,pqt.analysisSkipped AS analysis_skipped
  ,rp.alias AS run_purpose
  ,pqt.description AS qc_status
  ,r_spc.Run_runId
  ,pool.poolId
  ,pool.alias AS pool_name
  ,pool.identificationBarcode AS pool_barcode
  ,pool.description AS pool_description
  ,pool.creator AS pool_createdById
  ,pool.created AS pool_created
  ,pool.lastModifier AS pool_modifiedById
  ,pool.lastModified AS pool_modified
FROM _Partition AS p
LEFT JOIN Pool pool ON pool.poolId = p.pool_poolId
JOIN Run_SequencerPartitionContainer AS r_spc ON r_spc.containers_containerId = p.containerId
LEFT JOIN Run_Partition rpq ON rpq.runId = r_spc.Run_runId AND rpq.partitionId = p.partitionId
LEFT JOIN PartitionQCType pqt ON pqt.partitionQcTypeId = rpq.partitionQcTypeId
LEFT JOIN RunPurpose rp ON rp.purposeId = rpq.purposeId
