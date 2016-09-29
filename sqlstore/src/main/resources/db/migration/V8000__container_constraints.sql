--StartNoTest
ALTER TABLE Run_SequencerPartitionContainer ADD FOREIGN KEY (Run_runId) REFERENCES Run (runId);
DELETE FROM Run_SequencerPartitionContainer where containers_containerId = 0;
ALTER TABLE Run_SequencerPartitionContainer ADD FOREIGN KEY (containers_containerId) REFERENCES SequencerPartitionContainer (containerId);

ALTER TABLE SequencerPartitionContainer_Partition ADD FOREIGN KEY (container_containerId) REFERENCES SequencerPartitionContainer (containerId);

ALTER TABLE _Partition ADD FOREIGN KEY (pool_poolId) REFERENCES Pool (poolId);

ALTER TABLE Pool_Elements ADD FOREIGN KEY (pool_poolId) REFERENCES Pool (poolId);
--EndNoTest