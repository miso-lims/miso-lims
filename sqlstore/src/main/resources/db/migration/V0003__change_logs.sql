ALTER TABLE Experiment ADD COLUMN lastModifier bigint NOT NULL DEFAULT 1;
ALTER TABLE Library ADD COLUMN lastModifier bigint NOT NULL DEFAULT 1;
ALTER TABLE Plate ADD COLUMN lastModifier bigint NOT NULL DEFAULT 1;
ALTER TABLE Pool ADD COLUMN lastModifier bigint NOT NULL DEFAULT 1;
ALTER TABLE Run ADD COLUMN lastModifier bigint NOT NULL DEFAULT 1;
ALTER TABLE Sample ADD COLUMN lastModifier bigint NOT NULL DEFAULT 1;
ALTER TABLE SequencerPartitionContainer ADD COLUMN lastModifier bigint NOT NULL DEFAULT 1;
ALTER TABLE Study ADD COLUMN lastModifier bigint NOT NULL DEFAULT 1;

CREATE TABLE SampleChangeLog (
  sampleId bigint NOT NULL REFERENCES Sample(sampleId),
  columnsChanged text NOT NULL,
  userId bigint NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;

CREATE TABLE PlateChangeLog (
  plateId bigint NOT NULL REFERENCES Plate(plateId),
  columnsChanged text NULL,
  userId bigint NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;

CREATE TABLE RunChangeLog (
  runId bigint NOT NULL REFERENCES Run(runId),
  columnsChanged text NULL,
  userId bigint NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;

CREATE TABLE PoolChangeLog (
  poolId bigint NOT NULL REFERENCES Pool(poolId),
  columnsChanged text NULL,
  userId bigint NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;

CREATE TABLE ExperimentChangeLog (
  experimentId bigint NOT NULL REFERENCES Experiment(experimentId),
  columnsChanged text NULL,
  userId bigint NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;

CREATE TABLE LibraryChangeLog (
  libraryId bigint NOT NULL REFERENCES Library(libraryId),
  columnsChanged text NULL,
  userId bigint NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;

CREATE TABLE StudyChangeLog (
  studyId bigint NOT NULL REFERENCES Study(studyId),
  columnsChanged text NULL,
  userId bigint NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;

CREATE TABLE SequencerPartitionContainerChangeLog (
  containerId bigint NOT NULL REFERENCES SequencerPartitionContainer(containerId),
  columnsChanged text NULL,
  userId bigint NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;
