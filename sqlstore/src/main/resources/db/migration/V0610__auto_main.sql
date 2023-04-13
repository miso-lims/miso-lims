-- attachments

DROP TABLE IF EXISTS Project_Attachment;
DROP TABLE IF EXISTS ServiceRecord_Attachment;
DROP TABLE IF EXISTS Run_Attachment;
DROP TABLE IF EXISTS Pool_Attachment;
DROP TABLE IF EXISTS Attachment;
DROP TABLE IF EXISTS TempValues;

CREATE TABLE Attachment (
  attachmentId bigint NOT NULL AUTO_INCREMENT,
  filename varchar(255) NOT NULL,
  path varchar(4096) NOT NULL,
  creator bigint NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (attachmentId),
  CONSTRAINT fk_attachment_creator FOREIGN KEY (creator) REFERENCES User (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Project_Attachment (
  projectId bigint NOT NULL,
  attachmentId bigint NOT NULL,
  PRIMARY KEY (projectId, attachmentId),
  CONSTRAINT fk_attachment_project FOREIGN KEY (projectId) REFERENCES Project (projectId),
  CONSTRAINT fk_project_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ServiceRecord_Attachment (
  recordId bigint NOT NULL,
  attachmentId bigint NOT NULL,
  PRIMARY KEY (recordId, attachmentId),
  CONSTRAINT fk_attachment_serviceRecord FOREIGN KEY (recordId) REFERENCES ServiceRecord (recordId),
  CONSTRAINT fk_serviceRecord_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Run_Attachment (
  runId bigint NOT NULL,
  attachmentId bigint NOT NULL,
  PRIMARY KEY (runId, attachmentId),
  CONSTRAINT fk_attachment_run FOREIGN KEY (runId) REFERENCES Run (runId),
  CONSTRAINT fk_run_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Pool_Attachment (
  poolId bigint NOT NULL,
  attachmentId bigint NOT NULL,
  PRIMARY KEY (poolId, attachmentId),
  CONSTRAINT fk_attachment_pool FOREIGN KEY (poolId) REFERENCES Pool (poolId),
  CONSTRAINT fk_pool_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE TempValues (
  name varchar(255) NOT NULL,
  val varchar(255) NOT NULL,
  PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO TempValues(name, val) VALUES('filesDir', '${filesDir}');


-- issues

DROP TABLE IF EXISTS Project_Issues;


-- pool_proportions

ALTER TABLE Pool_Dilution ADD COLUMN proportion SMALLINT UNSIGNED NOT NULL DEFAULT 1;


-- AutoFill_Dual_Index

ALTER TABLE `IndexFamily` ADD COLUMN `uniqueDualIndex` tinyint NOT NULL DEFAULT 0;


