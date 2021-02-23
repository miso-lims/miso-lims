ALTER TABLE ArrayChangeLog DROP FOREIGN KEY fk_arrayChangeLog_array;
ALTER TABLE ArrayChangeLog ADD CONSTRAINT fk_arrayChangeLog_array
  FOREIGN KEY (arrayId) REFERENCES Array (arrayId) ON DELETE CASCADE;

ALTER TABLE ArrayRunChangeLog DROP FOREIGN KEY fk_arrayRunChangeLog_arrayRun;
ALTER TABLE ArrayRunChangeLog ADD CONSTRAINT fk_arrayRunChangeLog_arrayRun
  FOREIGN KEY (arrayRunId) REFERENCES ArrayRun (arrayRunId) ON DELETE CASCADE;

ALTER TABLE BoxChangeLog DROP FOREIGN KEY fk_boxChangeLog_box;
ALTER TABLE BoxChangeLog ADD CONSTRAINT fk_boxChangeLog_box
  FOREIGN KEY (boxId) REFERENCES Box (boxId) ON DELETE CASCADE;

ALTER TABLE ExperimentChangeLog DROP FOREIGN KEY fk_experimentChangeLog_experiment;
ALTER TABLE ExperimentChangeLog ADD CONSTRAINT fk_experimentChangeLog_experiment
  FOREIGN KEY (experimentId) REFERENCES Experiment (experimentId) ON DELETE CASCADE;

ALTER TABLE KitDescriptorChangeLog DROP FOREIGN KEY fk_kitDescriptorChangeLog_kitDescriptor;
ALTER TABLE KitDescriptorChangeLog ADD CONSTRAINT fk_kitDescriptorChangeLog_kitDescriptor
  FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId) ON DELETE CASCADE;

ALTER TABLE LibraryAliquotChangeLog DROP FOREIGN KEY fk_libraryAliquotChangeLog_libraryAliquot;
ALTER TABLE LibraryAliquotChangeLog ADD CONSTRAINT fk_libraryAliquotChangeLog_libraryAliquot
  FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId) ON DELETE CASCADE;

ALTER TABLE LibraryChangeLog DROP FOREIGN KEY fk_libraryChangeLog_library;
ALTER TABLE LibraryChangeLog ADD CONSTRAINT fk_libraryChangeLog_library
  FOREIGN KEY (libraryId) REFERENCES Library (libraryId) ON DELETE CASCADE;

ALTER TABLE PoolChangeLog DROP FOREIGN KEY fk_poolChangeLog_pool;
ALTER TABLE PoolChangeLog ADD CONSTRAINT fk_poolChangeLog_pool
  FOREIGN KEY (poolId) REFERENCES Pool (poolId) ON DELETE CASCADE;

ALTER TABLE PoolOrderChangeLog DROP FOREIGN KEY fk_poolOrderChangeLog_pool;
ALTER TABLE PoolOrderChangeLog ADD CONSTRAINT fk_poolOrderChangeLog_poolOrder
  FOREIGN KEY (poolOrderId) REFERENCES PoolOrder (poolOrderId) ON DELETE CASCADE;

ALTER TABLE ProjectChangeLog DROP FOREIGN KEY fk_projectChangeLog_project;
ALTER TABLE ProjectChangeLog ADD CONSTRAINT fk_projectChangeLog_project
  FOREIGN KEY (projectId) REFERENCES Project (projectId) ON DELETE CASCADE;

ALTER TABLE RunChangeLog DROP FOREIGN KEY fk_runChangeLog_run;
ALTER TABLE RunChangeLog ADD CONSTRAINT fk_runChangeLog_run
  FOREIGN KEY (runId) REFERENCES Run (runId) ON DELETE CASCADE;

ALTER TABLE SampleChangeLog DROP FOREIGN KEY fk_sampleChangeLog_sample;
ALTER TABLE SampleChangeLog ADD CONSTRAINT fk_sampleChangeLog_sample
  FOREIGN KEY (sampleId) REFERENCES Sample (sampleId) ON DELETE CASCADE;

ALTER TABLE SequencerPartitionContainerChangeLog DROP FOREIGN KEY fk_containerChangeLog_box;
ALTER TABLE SequencerPartitionContainerChangeLog ADD CONSTRAINT fk_containerChangeLog_container
  FOREIGN KEY (containerId) REFERENCES SequencerPartitionContainer (containerId) ON DELETE CASCADE;

ALTER TABLE StorageLocationChangeLog DROP FOREIGN KEY fk_storageLocationChangeLog_storageLocation;
ALTER TABLE StorageLocationChangeLog ADD CONSTRAINT fk_storageLocationChangeLog_storageLocation
  FOREIGN KEY (locationId) REFERENCES StorageLocation (locationId) ON DELETE CASCADE;

ALTER TABLE StudyChangeLog DROP FOREIGN KEY fk_studyChangeLog_study;
ALTER TABLE StudyChangeLog ADD CONSTRAINT fk_studyChangeLog_study
  FOREIGN KEY (studyId) REFERENCES Study (studyId) ON DELETE CASCADE;

ALTER TABLE TransferChangeLog DROP FOREIGN KEY fk_transferChangeLog_transfer;
ALTER TABLE TransferChangeLog ADD CONSTRAINT fk_transferChangeLog_transfer
  FOREIGN KEY (transferId) REFERENCES Transfer (transferId) ON DELETE CASCADE;

ALTER TABLE WorksetChangeLog DROP FOREIGN KEY fk_worksetChangeLog_workset;
ALTER TABLE WorksetChangeLog ADD CONSTRAINT fk_worksetChangeLog_workset
  FOREIGN KEY (worksetId) REFERENCES Workset (worksetId) ON DELETE CASCADE;
