-- arrays

DROP TABLE IF EXISTS ArrayRunChangeLog;
DROP TABLE IF EXISTS ArrayRun;
DROP TABLE IF EXISTS ArrayChangeLog;
DROP TABLE IF EXISTS ArrayPosition;
DROP TABLE IF EXISTS Array;
DROP TABLE IF EXISTS ArrayModel;

CREATE TABLE ArrayModel (
  arrayModelId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(255) NOT NULL,
  `rows` TINYINT UNSIGNED NOT NULL,
  `columns` TINYINT UNSIGNED NOT NULL,
  PRIMARY KEY (arrayModelId),
  CONSTRAINT uk_arrayModel_alias UNIQUE (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Array (
  arrayId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(255) NOT NULL,
  arrayModelId bigint NOT NULL,
  serialNumber varchar(255),
  description varchar(255),
  creator bigint NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  lastModifier bigint NOT NULL,
  lastModified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (arrayId),
  CONSTRAINT fk_array_model FOREIGN KEY (arrayModelId) REFERENCES ArrayModel(arrayModelId),
  CONSTRAINT fk_array_creator FOREIGN KEY (creator) REFERENCES User(userId),
  CONSTRAINT fk_array_modifier FOREIGN KEY (lastModifier) REFERENCES User(userId),
  CONSTRAINT uk_array_alias UNIQUE (alias),
  CONSTRAINT uk_array_serialNumber UNIQUE (serialNumber)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ArrayPosition (
  arrayId bigint NOT NULL,
  position varchar(6) NOT NULL,
  sampleId bigint NOT NULL,
  PRIMARY KEY (arrayId, position),
  CONSTRAINT fk_arrayPosition_array FOREIGN KEY (arrayId) REFERENCES Array(arrayId),
  CONSTRAINT fk_arrayPosition_sample FOREIGN KEY (sampleId) REFERENCES Sample(sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ArrayChangeLog (
  arrayChangeLogId bigint NOT NULL AUTO_INCREMENT,
  arrayId bigint NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint NOT NULL,
  message longtext NOT NULL,
  changeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (arrayChangeLogId),
  CONSTRAINT fk_arrayChangeLog_array FOREIGN KEY (arrayId) REFERENCES Array(arrayId),
  CONSTRAINT fk_arrayChangeLog_user FOREIGN KEY (userId) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ArrayRun (
  arrayRunId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(255) NOT NULL,
  instrumentId bigint NOT NULL,
  description varchar(255),
  filePath varchar(255),
  arrayId bigint,
  health varchar(50) NOT NULL,
  startDate DATE,
  completionDate DATE,
  creator bigint NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  lastModifier bigint NOT NULL,
  lastModified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (arrayRunId),
  CONSTRAINT uk_arrayRun_alias UNIQUE (alias),
  CONSTRAINT fk_arrayRun_instrument FOREIGN KEY (instrumentId) REFERENCES Instrument(instrumentId),
  CONSTRAINT fk_arrayRun_array FOREIGN KEY (arrayId) REFERENCES Array(arrayId),
  CONSTRAINT fk_arrayRun_creator FOREIGN KEY (creator) REFERENCES User(userId),
  CONSTRAINT fk_arrayRun_modifier FOREIGN KEY (lastModifier) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ArrayRunChangeLog (
  arrayRunChangeLogId bigint NOT NULL AUTO_INCREMENT,
  arrayRunId bigint NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint NOT NULL,
  message longtext NOT NULL,
  changeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (arrayRunChangeLogId),
  CONSTRAINT fk_arrayRunChangeLog_arrayRun FOREIGN KEY (arrayRunId) REFERENCES ArrayRun(arrayRunId),
  CONSTRAINT fk_arrayRunChangeLog_user FOREIGN KEY (userId) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO Platform (name, instrumentModel, description, numContainers, instrumentType)
SELECT 'ILLUMINA', 'Illumina iScan', 'Array scanner for extensive applications', 1, 'ARRAY_SCANNER' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Platform WHERE instrumentModel = 'Illumina iScan');



