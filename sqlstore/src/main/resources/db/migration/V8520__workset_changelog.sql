CREATE TABLE WorksetChangeLog (
  worksetChangeLogId bigint(20) NOT NULL AUTO_INCREMENT,
  worksetId bigint(20) NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint(20) NOT NULL,
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
