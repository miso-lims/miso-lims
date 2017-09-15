ALTER TABLE SequencerPartitionContainer ADD COLUMN clusteringKit bigint(20);
ALTER TABLE SequencerPartitionContainer ADD COLUMN multiplexingKit bigint(20);

ALTER TABLE SequencerPartitionContainer ADD CONSTRAINT container_clusteringKit_kitDescriptor_fkey FOREIGN KEY (clusteringKit) REFERENCES KitDescriptor (kitDescriptorId);
ALTER TABLE SequencerPartitionContainer ADD CONSTRAINT container_multiplexingKit_kitDescriptor_fkey FOREIGN KEY (multiplexingKit) REFERENCES KitDescriptor (kitDescriptorId);

UPDATE SequencerPartitionContainer SET clusteringKit = (SELECT DISTINCT KitDescriptor.kitDescriptorId
   FROM KitDescriptor
    JOIN Kit ON KitDescriptor.kitDescriptorId = Kit.kitDescriptorId
    JOIN Experiment_Kit ON Experiment_Kit.kits_kitId = Kit.kitId
    JOIN Experiment_Run ON Experiment_Run.Experiment_experimentId = Experiment_Kit.experiments_experimentId
    JOIN Run_SequencerPartitionContainer ON Run_SequencerPartitionContainer.Run_runId = Experiment_Run.runs_runId
   WHERE KitDescriptor.kitType = 'CLUSTERING' AND Run_SequencerPartitionContainer.containers_containerId = SequencerPartitionContainer.containerId);

UPDATE SequencerPartitionContainer SET multiplexingKit = (SELECT DISTINCT KitDescriptor.kitDescriptorId
   FROM KitDescriptor
    JOIN Kit ON KitDescriptor.kitDescriptorId = Kit.kitDescriptorId
    JOIN Experiment_Kit ON Experiment_Kit.kits_kitId = Kit.kitId
    JOIN Experiment_Run ON Experiment_Run.Experiment_experimentId = Experiment_Kit.experiments_experimentId
    JOIN Run_SequencerPartitionContainer ON Run_SequencerPartitionContainer.Run_runId = Experiment_Run.runs_runId
   WHERE KitDescriptor.kitType = 'MULTIPLEXING' AND Run_SequencerPartitionContainer.containers_containerId = SequencerPartitionContainer.containerId);
