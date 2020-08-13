SELECT DISTINCT Run.runId
  ,part.partitionId
  ,ld.name aliquotId
  ,bc1.sequence barcode
  ,bc2.sequence barcode_two
  ,tr.alias targeted_sequencing 
  ,im.dataManglingPolicy dataManglingPolicy
  ,rp.alias run_purpose
FROM SequencingContainerModel scm
JOIN SequencerPartitionContainer spc ON scm.sequencingContainerModelId = spc.sequencingContainerModelId
JOIN _Partition part ON part.containerId = spc.containerId
JOIN Pool pool ON pool.poolId = part.pool_poolId 
JOIN Pool_LibraryAliquot ele ON ele.poolId = pool.poolId 
JOIN LibraryAliquot ld ON ld.aliquotId = ele.aliquotId 
JOIN Library l ON l.libraryId = ld.libraryId 
LEFT JOIN TargetedSequencing tr ON tr.targetedSequencingId = ld.targetedSequencingId
LEFT JOIN ( 
  SELECT library_libraryId, sequence FROM Library_Index 
  JOIN Indices ON Indices.indexId = Library_Index.index_indexId 
  WHERE position = 1
) bc1 ON bc1.library_libraryId = l.libraryId
LEFT JOIN ( 
  SELECT library_libraryId, sequence FROM Library_Index 
  JOIN Indices ON Indices.indexId = Library_Index.index_indexId
  WHERE position = 2 
) bc2 ON bc2.library_libraryId = l.libraryId
JOIN Run_SequencerPartitionContainer rspc ON rspc.containers_containerId = spc.containerId
JOIN Run ON Run.runId = rspc.run_runId
JOIN Instrument inst ON inst.instrumentId = Run.instrumentId
JOIN InstrumentModel im ON im.instrumentModelId = inst.instrumentModelId
LEFT JOIN Run_Partition_LibraryAliquot rpa
  ON rpa.runId = Run.runId
  AND rpa.partitionId = part.partitionId
  AND rpa.aliquotId = ld.aliquotId
LEFT JOIN RunPurpose rp ON rp.purposeId = rpa.purposeId
