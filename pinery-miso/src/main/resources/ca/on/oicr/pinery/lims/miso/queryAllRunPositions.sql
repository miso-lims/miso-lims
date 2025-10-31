SELECT spc.containerId
  ,ipos.alias AS instrument_position
  ,sp.name AS sequencing_parameters
  ,scm.alias AS container_model
  ,p.partitionId
  ,p.partitionNumber
  ,pqt.analysisSkipped AS analysis_skipped
  ,rp.alias AS run_purpose
  ,pqt.description AS qc_status
  ,r_spc.Run_runId
  ,pool.poolId
  ,pool.alias AS pool_name
  ,pool.identificationBarcode AS pool_barcode
  ,pool.description AS pool_description
  ,pool.qcPassed AS pool_qc_passed
  ,pool.creator AS pool_createdById
  ,pool.created AS pool_created
  ,pool.lastModifier AS pool_modifiedById
  ,pool.lastModified AS pool_modified
FROM _Partition AS p
LEFT JOIN Pool pool ON pool.poolId = p.pool_poolId
JOIN SequencerPartitionContainer spc ON spc.containerId = p.containerId
JOIN Run_SequencerPartitionContainer AS r_spc ON r_spc.containers_containerId = p.containerId
JOIN SequencingContainerModel scm ON scm.sequencingContainerModelId = spc.sequencingContainerModelId
LEFT JOIN InstrumentPosition ipos ON ipos.positionId = r_spc.positionId
LEFT JOIN SequencingParameters sp ON sp.parametersId = r_spc.sequencingParametersId
LEFT JOIN Run_Partition rpq ON rpq.runId = r_spc.Run_runId AND rpq.partitionId = p.partitionId
LEFT JOIN PartitionQCType pqt ON pqt.partitionQcTypeId = rpq.partitionQcTypeId
LEFT JOIN RunPurpose rp ON rp.purposeId = rpq.purposeId
