-- rename_dilution
CREATE TABLE LibraryAliquot (
  aliquotId bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  libraryId bigint NOT NULL,
  preMigrationId bigint DEFAULT NULL,
  identificationBarcode varchar(255) DEFAULT NULL,
  volumeUsed double DEFAULT NULL,
  volume double DEFAULT NULL,
  volumeUnits varchar(30) DEFAULT NULL,
  discarded tinyint NOT NULL DEFAULT '0',
  concentration double DEFAULT NULL,
  concentrationUnits varchar(30) DEFAULT NULL,
  targetedSequencingId bigint DEFAULT NULL,
  ngUsed double DEFAULT NULL,
  distributed tinyint NOT NULL DEFAULT '0',
  distributionDate date DEFAULT NULL,
  distributionRecipient varchar(250) DEFAULT NULL,
  creationDate date NOT NULL,
  creator bigint NOT NULL,
  lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastModifier bigint NOT NULL,
  created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (aliquotId),
  UNIQUE KEY uk_libraryAliquot_name (name),
  UNIQUE KEY uk_libraryAliquot_preMigrationId (preMigrationId),
  UNIQUE KEY uk_libraryAliquot_identificationBarcode (identificationBarcode),
  CONSTRAINT fk_libraryAliquot_targetedSequencing FOREIGN KEY (targetedSequencingId) REFERENCES TargetedSequencing (targetedSequencingId),
  CONSTRAINT fk_libraryAliquot_creator FOREIGN KEY (creator) REFERENCES User (userId),
  CONSTRAINT fk_libraryAliquot_lastModifier_user FOREIGN KEY (lastModifier) REFERENCES User (userId),
  CONSTRAINT fk_libraryAliquot_library FOREIGN KEY (libraryId) REFERENCES Library (libraryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO LibraryAliquot(aliquotId, name, libraryId, preMigrationId, identificationBarcode, volumeUsed, volume, volumeUnits, discarded,
  concentration, concentrationUnits, targetedSequencingId, ngUsed, distributed, distributionDate, distributionRecipient, creationDate,
  creator, lastUpdated, lastModifier, created)
SELECT dilutionId, name, library_libraryId, preMigrationId, identificationBarcode, volumeUsed, volume, volumeUnits, discarded,
  concentration, concentrationUnits, targetedSequencingId, ngUsed, distributed, distributionDate, distributionRecipient, creationDate,
  creator, lastUpdated, lastModifier, created
FROM LibraryDilution;

CREATE TABLE LibraryAliquotChangeLog (
  aliquotChangeLogId bigint NOT NULL AUTO_INCREMENT,
  aliquotId bigint NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint NOT NULL,
  message longtext NOT NULL,
  changeTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (aliquotChangeLogId),
  CONSTRAINT fk_libraryAliquotChangeLog_libraryAliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId),
  CONSTRAINT fk_libraryAliquotChangeLog_user FOREIGN KEY (userId) REFERENCES User (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO LibraryAliquotChangeLog (aliquotChangeLogId, aliquotId, columnsChanged, userId, message, changeTime)
SELECT dilutionChangeLogId, dilutionId, columnsChanged, userId, message, changeTime
FROM DilutionChangeLog;

CREATE TABLE Workset_LibraryAliquot (
  worksetId bigint NOT NULL,
  aliquotId bigint NOT NULL,
  PRIMARY KEY (worksetId, aliquotId),
  CONSTRAINT fk_libraryAliquot_workset FOREIGN KEY (worksetId) REFERENCES Workset (worksetId),
  CONSTRAINT fk_workset_libraryAliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO Workset_LibraryAliquot (worksetId, aliquotId)
SELECT worksetId, dilutionId
FROM Workset_Dilution;

CREATE TABLE Pool_LibraryAliquot (
  poolId bigint NOT NULL,
  aliquotId bigint NOT NULL,
  proportion smallint UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (poolId, aliquotId),
  CONSTRAINT fk_libraryAliquot_pool FOREIGN KEY (poolId) REFERENCES Pool (poolId),
  CONSTRAINT fk_pool_libraryAliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO Pool_LibraryAliquot (poolId, aliquotId, proportion)
SELECT pool_poolId, dilution_dilutionId, proportion
FROM Pool_Dilution;

DROP TABLE Pool_Dilution;
DROP TABLE Workset_Dilution;
DROP TABLE DilutionChangeLog;
DROP TABLE LibraryDilution;

DROP TRIGGER IF EXISTS LibraryDilutionChange;
DROP TRIGGER IF EXISTS LibraryDilutionInsert;

DROP VIEW IF EXISTS DilutionBoxableView;
DROP VIEW IF EXISTS DilutionBoxPosition;

-- StartNoTest
DROP PROCEDURE IF EXISTS deleteDilution;

UPDATE BoxPosition SET targetType = 'LIBRARY_ALIQUOT' WHERE targetType = 'DILUTION';
-- EndNoTest

