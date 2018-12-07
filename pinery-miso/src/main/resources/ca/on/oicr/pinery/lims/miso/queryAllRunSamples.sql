SELECT part.partitionId
  ,ld.name dilutionId
  ,bc1.sequence barcode
  ,bc2.sequence barcode_two
  ,tr.alias targeted_sequencing 
  ,CASE WHEN scm.fallback THEN 'NONE' ELSE (SELECT DISTINCT dataManglingPolicy FROM InstrumentModel JOIN SequencingContainerModel_InstrumentModel ON SequencingContainerModel_InstrumentModel.instrumentModelId = InstrumentModel.instrumentModelId WHERE SequencingContainerModel_InstrumentModel.sequencingContainerModelId = scm.sequencingContainerModelId) END dataManglingPolicy
FROM SequencingContainerModel scm
JOIN SequencerPartitionContainer spc ON scm.sequencingContainerModelId = spc.sequencingContainerModelId
JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = spc.containerId
JOIN _Partition part ON part.partitionId = spcp.partitions_partitionId
JOIN Pool pool ON pool.poolId = part.pool_poolId 
JOIN Pool_Dilution ele ON ele.pool_poolId = pool.poolId 
JOIN LibraryDilution ld ON ld.dilutionId = ele.dilution_dilutionId 
JOIN Library l ON l.libraryId = ld.library_libraryId 
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
