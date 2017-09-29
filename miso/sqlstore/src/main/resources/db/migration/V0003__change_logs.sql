ALTER TABLE Experiment ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE Experiment ADD CONSTRAINT experiment_user_userid_fkey FOREIGN KEY(lastModifier) REFERENCES User(userId);
ALTER TABLE Library ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE Library ADD CONSTRAINT library_user_userid_fkey FOREIGN KEY(lastModifier) REFERENCES User(userId);
ALTER TABLE Plate ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE Plate ADD CONSTRAINT plate_user_userid_fkey FOREIGN KEY(lastModifier) REFERENCES User(userId);
ALTER TABLE Pool ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE Pool ADD CONSTRAINT pool_user_userid_fkey FOREIGN KEY(lastModifier) REFERENCES User(userId);
ALTER TABLE Run ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE Run ADD CONSTRAINT run_user_userid_fkey FOREIGN KEY(lastModifier) REFERENCES User(userId);
ALTER TABLE Sample ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE Sample ADD CONSTRAINT sample_user_userid_fkey FOREIGN KEY(lastModifier) REFERENCES User(userId);
ALTER TABLE SequencerPartitionContainer ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE SequencerPartitionContainer ADD CONSTRAINT sequencerpartitioncontainer_user_userid_fkey FOREIGN KEY(lastModifier) REFERENCES User(userId);
ALTER TABLE Study ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE Study ADD CONSTRAINT study_user_userid_fkey FOREIGN KEY(lastModifier) REFERENCES User(userId);

CREATE TABLE SampleChangeLog (
  sampleId bigint(20) NOT NULL REFERENCES Sample(sampleId),
  columnsChanged text NOT NULL,
  userId bigint(20) NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE PlateChangeLog (
  plateId bigint(20) NOT NULL REFERENCES Plate(plateId),
  columnsChanged text NULL,
  userId bigint(20) NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE RunChangeLog (
  runId bigint(20) NOT NULL REFERENCES Run(runId),
  columnsChanged text NULL,
  userId bigint(20) NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE PoolChangeLog (
  poolId bigint(20) NOT NULL REFERENCES Pool(poolId),
  columnsChanged text NULL,
  userId bigint(20) NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE ExperimentChangeLog (
  experimentId bigint(20) NOT NULL REFERENCES Experiment(experimentId),
  columnsChanged text NULL,
  userId bigint(20) NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE LibraryChangeLog (
  libraryId bigint(20) NOT NULL REFERENCES Library(libraryId),
  columnsChanged text NULL,
  userId bigint(20) NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE StudyChangeLog (
  studyId bigint(20) NOT NULL REFERENCES Study(studyId),
  columnsChanged text NULL,
  userId bigint(20) NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE SequencerPartitionContainerChangeLog (
  containerId bigint(20) NOT NULL REFERENCES SequencerPartitionContainer(containerId),
  columnsChanged text NULL,
  userId bigint(20) NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8;
