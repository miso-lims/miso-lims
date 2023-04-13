-- default_sample_type
ALTER TABLE SampleClass ADD COLUMN defaultSampleTypeId bigint;
ALTER TABLE SampleClass ADD CONSTRAINT fk_sampleClass_defaultSampleType
  FOREIGN KEY (defaultSampleTypeId) REFERENCES SampleType (typeId);

-- workset_changelog
CREATE TABLE WorksetChangeLog (
  worksetChangeLogId bigint NOT NULL AUTO_INCREMENT,
  worksetId bigint NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint NOT NULL,
  message longtext NOT NULL,
  changeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (worksetChangeLogId),
  CONSTRAINT fk_worksetChangeLog_workset FOREIGN KEY (worksetId) REFERENCES Workset(worksetId),
  CONSTRAINT fk_worksetChangeLog_user FOREIGN KEY (userId) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO WorksetChangeLog (worksetId, columnsChanged, userId, message, changeTime)
SELECT worksetId, '', creator, 'Workset created', created FROM Workset;

ALTER TABLE Workset_Sample ADD COLUMN addedTime TIMESTAMP NULL DEFAULT NULL;
ALTER TABLE Workset_Library ADD COLUMN addedTime TIMESTAMP NULL DEFAULT NULL;
ALTER TABLE Workset_LibraryAliquot ADD COLUMN addedTime TIMESTAMP NULL DEFAULT NULL;

UPDATE Workset SET description = NULL WHERE description = '';

