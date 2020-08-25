CREATE OR REPLACE VIEW RunProjectView AS
  SELECT rspc.Run_runId AS runId, GROUP_CONCAT(DISTINCT COALESCE(proj.shortName, proj.name) SEPARATOR ', ') AS projects
  FROM Run_SequencerPartitionContainer rspc
  JOIN SequencerPartitionContainer spc ON spc.containerId = rspc.containers_containerId
  JOIN _Partition part ON part.containerId = spc.containerId
  JOIN Pool ON Pool.poolId = part.pool_poolId
  JOIN Pool_LibraryAliquot ele ON ele.poolId = Pool.poolId
  JOIN LibraryAliquot ali ON ali.aliquotId = ele.aliquotId
  JOIN Library lib ON lib.libraryId = ali.libraryId
  JOIN Sample sam ON sam.sampleId = lib.sample_sampleId
  JOIN Project proj ON proj.projectId = sam.project_projectId
  GROUP BY rspc.Run_runId;
