-- seq_params_paired_not_null

ALTER TABLE SequencingParameters CHANGE COLUMN paired paired tinyint NOT NULL DEFAULT 0;
ALTER TABLE SequencingParameters CHANGE COLUMN readLength readLength int NOT NULL DEFAULT 0;


-- experiment_refactor

DELETE FROM Experiment_Run WHERE Experiment_Run.experiment_experimentId IN (SELECT experimentId  FROM Experiment WHERE alias LIKE 'EXP_AUTOGEN%' AND NOT EXISTS(SELECT * FROM Experiment_Kit WHERE Experiment_Kit.experiments_experimentId = Experiment.experimentId) AND NOT EXISTS(SELECT * FROM Submission_Experiment WHERE Submission_Experiment.experiments_experimentId = Experiment.experimentId));

DELETE FROM ExperimentChangeLog WHERE experimentId IN (SELECT experimentId  FROM Experiment WHERE alias LIKE 'EXP_AUTOGEN%' AND NOT EXISTS(SELECT * FROM Experiment_Kit WHERE Experiment_Kit.experiments_experimentId = Experiment.experimentId) AND NOT EXISTS(SELECT * FROM Submission_Experiment WHERE Submission_Experiment.experiments_experimentId = Experiment.experimentId));

DELETE FROM Experiment WHERE alias LIKE 'EXP_AUTOGEN%' AND NOT EXISTS(SELECT * FROM Experiment_Kit WHERE Experiment_Kit.experiments_experimentId = Experiment.experimentId) AND NOT EXISTS(SELECT * FROM Submission_Experiment WHERE Submission_Experiment.experiments_experimentId = Experiment.experimentId);

ALTER TABLE Experiment ADD COLUMN library_libraryId bigint;
ALTER TABLE Experiment ADD CONSTRAINT experiment_library_libraryId_fkey FOREIGN KEY(library_libraryId) REFERENCES Library(libraryId);

UPDATE Experiment SET library_libraryId = (SELECT DISTINCT library_libraryId FROM LibraryDilution JOIN Pool_Dilution ON LibraryDilution.dilutionId = Pool_Dilution.dilution_dilutionId WHERE Pool_Dilution.pool_poolId = Experiment.pool_poolId);

CREATE TABLE Experiment_Run_Partition (
  experiment_experimentId bigint NOT NULL,
  run_runId bigint NOT NULL,
  partition_partitionId bigint NOT NULL,
  PRIMARY KEY (experiment_experimentId,run_runId, partition_partitionId),
  CONSTRAINT experiment_run_partition_experimentId_fkey FOREIGN KEY (experiment_experimentId) REFERENCES Experiment (experimentId),
  CONSTRAINT experiment_run_partition_runId_fkey FOREIGN KEY (run_runId) REFERENCES Run (runId),
  CONSTRAINT experiment_run_partition_partitionId_fkey FOREIGN KEY (partition_partitionId) REFERENCES _Partition (partitionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Experiment_Run_Partition(experiment_experimentId, run_runId, partition_partitionId)
  SELECT DISTINCT Experiment.experimentId, Experiment_Run.runs_runId, _Partition.partitionId
    FROM Experiment
      JOIN Experiment_Run ON Experiment_Run.Experiment_experimentId = Experiment.experimentId
      JOIN Run_SequencerPartitionContainer ON Run_SequencerPartitionContainer.Run_runId = Experiment_Run.runs_runId
      JOIN SequencerPartitionContainer_Partition ON SequencerPartitionContainer_Partition.container_containerId = Run_SequencerPartitionContainer.containers_containerId
      JOIN _Partition ON _Partition.partitionId = SequencerPartitionContainer_Partition.partitions_partitionId
    WHERE _Partition.pool_poolId = Experiment.pool_poolId;

ALTER TABLE Experiment DROP FOREIGN KEY fk_experiment_pool_poolId;
ALTER TABLE Experiment DROP pool_poolId;
DROP TABLE Experiment_Run;


-- drop_submission

DROP TABLE Submission_Sample;
DROP TABLE Submission_Partition_Dilution;
DROP TABLE Submission_Study;


