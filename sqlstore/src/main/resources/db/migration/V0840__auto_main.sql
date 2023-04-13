-- dilution_changelog
CREATE TABLE DilutionChangeLog (
  dilutionChangeLogId bigint NOT NULL AUTO_INCREMENT,
  dilutionId bigint NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint NOT NULL,
  message longtext NOT NULL,
  changeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (dilutionChangeLogId),
  CONSTRAINT fk_dilutionChangeLog_dilution FOREIGN KEY (dilutionId) REFERENCES LibraryDilution(dilutionId),
  CONSTRAINT fk_dilutionChangeLog_user FOREIGN KEY (userId) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO DilutionChangeLog (dilutionId, columnsChanged, userId, message, changeTime)
SELECT dilutionId, columnsChanged, userId, REPLACE(message, CONCAT(d.name, ' '), ''), changeTime
FROM LibraryDilution d
JOIN Library l ON l.libraryId = d.library_libraryId
JOIN LibraryChangeLog c ON c.libraryId = l.libraryId
  AND c.message = CONCAT('Library Dilution ', d.name, ' created.');

INSERT INTO DilutionChangeLog (dilutionId, columnsChanged, userId, message, changeTime)
SELECT dilutionId, REPLACE(columnsChanged, CONCAT(d.name, ' '), ''), userId, REPLACE(message, CONCAT(d.name, ' '), ''), changeTime
FROM LibraryDilution d
JOIN Library l ON l.libraryId = d.library_libraryId
JOIN LibraryChangeLog c ON c.libraryId = l.libraryId
  AND c.message LIKE CONCAT(d.name, ' %');

DELETE FROM LibraryChangeLog WHERE message LIKE 'Library Dilution LDI% created.';
DELETE FROM LibraryChangeLog WHERE message LIKE 'LDI% %';

