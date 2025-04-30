SELECT DISTINCT Run.runId
  ,part.partitionId
  ,ld.name aliquotId
  ,bc1.sequence barcode
  ,bc2.sequence barcode_two
  ,tr.alias targeted_sequencing 
  ,COALESCE(Run.dataManglingPolicy, im.dataManglingPolicy) dataManglingPolicy
  ,rp.alias run_purpose
  ,rlqc.qcPassed qc_passed
  ,rlqc.description qc_description
  ,rpa.qcNote
  ,rpa.qcDate qc_date
  ,rpa.qcUser qcUserId
  ,rpa.dataReview
  ,rpa.dataReviewDate
  ,rpa.dataReviewerId
FROM SequencingContainerModel scm
JOIN SequencerPartitionContainer spc ON scm.sequencingContainerModelId = spc.sequencingContainerModelId
JOIN _Partition part ON part.containerId = spc.containerId
JOIN Pool pool ON pool.poolId = part.pool_poolId 
JOIN Pool_LibraryAliquot ele ON ele.poolId = pool.poolId 
JOIN LibraryAliquot ld ON ld.aliquotId = ele.aliquotId 
JOIN Library l ON l.libraryId = ld.libraryId 
LEFT JOIN TargetedSequencing tr ON tr.targetedSequencingId = ld.targetedSequencingId
LEFT JOIN LibraryIndex bc1 ON bc1.indexId = l.index1Id
LEFT JOIN LibraryIndex bc2 ON bc2.indexId = l.index2Id
JOIN Run_SequencerPartitionContainer rspc ON rspc.containers_containerId = spc.containerId
JOIN Run ON Run.runId = rspc.run_runId
JOIN Instrument inst ON inst.instrumentId = Run.instrumentId
JOIN InstrumentModel im ON im.instrumentModelId = inst.instrumentModelId
LEFT JOIN Run_Partition_LibraryAliquot rpa
  ON rpa.runId = Run.runId
  AND rpa.partitionId = part.partitionId
  AND rpa.aliquotId = ld.aliquotId
LEFT JOIN RunPurpose rp ON rp.purposeId = rpa.purposeId
LEFT JOIN RunLibraryQcStatus rlqc ON rlqc.statusId = rpa.statusId
