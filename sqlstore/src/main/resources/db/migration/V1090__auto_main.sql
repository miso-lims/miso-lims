-- delete_runpartition_orphans
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

-- library_aliquot_qc
ALTER TABLE LibraryAliquot ADD COLUMN qcPassed BOOLEAN;

-- sops
CREATE TABLE Sop (
  sopId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(100) NOT NULL,
  version varchar(50) NOT NULL,
  category varchar(20) NOT NULL,
  url varchar(255) NOT NULL,
  archived BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (sopId),
  CONSTRAINT uk_sop_version UNIQUE (category, alias, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Sample ADD COLUMN sopId bigint;
ALTER TABLE Sample ADD CONSTRAINT fk_sample_sop FOREIGN KEY (sopId) REFERENCES Sop (sopId);
ALTER TABLE Library ADD COLUMN sopId bigint;
ALTER TABLE Library ADD CONSTRAINT fk_library_sop FOREIGN KEY (sopId) REFERENCES Sop (sopId);
ALTER TABLE Run ADD COLUMN sopId bigint;
ALTER TABLE Run ADD CONSTRAINT fk_run_sop FOREIGN KEY (sopId) REFERENCES Sop (sopId);

