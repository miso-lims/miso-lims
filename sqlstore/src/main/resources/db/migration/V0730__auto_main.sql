-- distribution

ALTER TABLE Sample ADD COLUMN distributed BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE Sample ADD COLUMN distributionDate DATE DEFAULT NULL;
ALTER TABLE Sample ADD COLUMN distributionRecipient VARCHAR(250) DEFAULT NULL;

ALTER TABLE Library ADD COLUMN distributed BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE Library ADD COLUMN distributionDate DATE DEFAULT NULL;
ALTER TABLE Library ADD COLUMN distributionRecipient VARCHAR(250) DEFAULT NULL;


-- remove_instrument_ip

ALTER TABLE Instrument DROP COLUMN ip;


-- rename_platform

ALTER TABLE Platform RENAME TO InstrumentModel;

ALTER TABLE InstrumentModel CHANGE COLUMN platformId instrumentModelId bigint NOT NULL AUTO_INCREMENT;
ALTER TABLE InstrumentModel CHANGE COLUMN name platform varchar(50) NOT NULL;
ALTER TABLE InstrumentModel CHANGE COLUMN instrumentModel alias varchar(100) NOT NULL;

ALTER TABLE PlatformPosition RENAME TO InstrumentPosition;
ALTER TABLE InstrumentPosition CHANGE COLUMN platformId instrumentModelId bigint NOT NULL;
ALTER TABLE InstrumentPosition DROP FOREIGN KEY fk_platformPosition_platform;
ALTER TABLE InstrumentPosition ADD CONSTRAINT fk_instrumentPosition_instrumentModel FOREIGN KEY (instrumentModelId)
  REFERENCES InstrumentModel (instrumentModelId);

ALTER TABLE Instrument CHANGE COLUMN platformId instrumentModelId bigint NOT NULL;
ALTER TABLE Instrument DROP FOREIGN KEY fk_sequencerReference_platform;
ALTER TABLE Instrument ADD CONSTRAINT fk_instrument_instrumentModel FOREIGN KEY (instrumentModelId)
  REFERENCES InstrumentModel (instrumentModelId);

ALTER TABLE SequencingContainerModel_Platform RENAME TO SequencingContainerModel_InstrumentModel;
ALTER TABLE SequencingContainerModel_InstrumentModel CHANGE COLUMN platformId instrumentModelId bigint NOT NULL;
ALTER TABLE SequencingContainerModel_InstrumentModel DROP FOREIGN KEY fk_SequencingContainerModel_Platform_platform;
ALTER TABLE SequencingContainerModel_InstrumentModel ADD CONSTRAINT fk_sequencingContainerModel_instrumentModel
  FOREIGN KEY (instrumentModelId) REFERENCES InstrumentModel (instrumentModelId);

ALTER TABLE SequencingParameters CHANGE COLUMN platformId instrumentModelId bigint NOT NULL;
ALTER TABLE SequencingParameters DROP FOREIGN KEY parameter_platformId_fkey;
ALTER TABLE SequencingParameters ADD CONSTRAINT fk_sequencingParameters_instrumentModel FOREIGN KEY (instrumentModelId)
  REFERENCES InstrumentModel (instrumentModelId);

ALTER TABLE Experiment CHANGE COLUMN platform_platformId instrumentModelId bigint NOT NULL;
ALTER TABLE Experiment ADD CONSTRAINT fk_experiment_instrumentModel FOREIGN KEY (instrumentModelId)
  REFERENCES InstrumentModel (instrumentModelId);

-- StartNoTest
DROP FUNCTION IF EXISTS toIpAddressBlob;
DROP FUNCTION IF EXISTS toHexString;
DROP PROCEDURE IF EXISTS addPlatform;
-- EndNoTest


