-- move_library_kit

ALTER TABLE Library ADD COLUMN `kitDescriptorId` bigint;
ALTER TABLE Library ADD CONSTRAINT library_kitDescriptor_fkey FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);
UPDATE Library SET kitDescriptorId = (SELECT kitDescriptorId FROM DetailedLibrary WHERE DetailedLibrary.libraryId = Library.libraryId);
ALTER TABLE DetailedLibrary DROP FOREIGN KEY libraryAdditionalInfo_kitDescriptor_fkey;
ALTER TABLE DetailedLibrary DROP COLUMN kitDescriptorId;

UPDATE Library SET kitDescriptorId = (SELECT DISTINCT KitDescriptor.kitDescriptorId
  FROM KitDescriptor
   JOIN Kit ON KitDescriptor.kitDescriptorId = Kit.kitDescriptorId
   JOIN Experiment_Kit ON Experiment_Kit.kits_kitId = Kit.kitId
   JOIN Experiment ON Experiment.experimentId = Experiment_Kit.experiments_experimentId
   JOIN Pool ON Pool.poolId = Experiment.pool_poolId
   JOIN Pool_Dilution ON Pool_Dilution.pool_poolId = Pool.poolId
   JOIN LibraryDilution ON LibraryDilution.dilutionId = Pool_Dilution.dilution_dilutionId
  WHERE KitDescriptor.kitType = 'LIBRARY' AND LibraryDilution.library_libraryId = Library.libraryId)
 WHERE kitDescriptorId IS NULL;


-- add_kits_to_containers

ALTER TABLE SequencerPartitionContainer ADD COLUMN clusteringKit bigint;
ALTER TABLE SequencerPartitionContainer ADD COLUMN multiplexingKit bigint;

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


-- drop_container_barcodes

ALTER TABLE SequencerPartitionContainer DROP COLUMN locationBarcode;
ALTER TABLE SequencerPartitionContainer DROP COLUMN validationBarcode;


