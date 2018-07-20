DROP TABLE IF EXISTS Project_Attachment;
DROP TABLE IF EXISTS ServiceRecord_Attachment;
DROP TABLE IF EXISTS Attachment;

CREATE TABLE Attachment (
  attachmentId bigint(20) NOT NULL AUTO_INCREMENT,
  filename varchar(255) NOT NULL,
  path varchar(4096) NOT NULL,
  creator bigint(20) NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (attachmentId),
  CONSTRAINT fk_attachment_creator FOREIGN KEY (creator) REFERENCES User (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Project_Attachment (
  projectId bigint(20) NOT NULL,
  attachmentId bigint(20) NOT NULL,
  PRIMARY KEY (projectId, attachmentId),
  CONSTRAINT fk_attachment_project FOREIGN KEY (projectId) REFERENCES Project (projectId),
  CONSTRAINT fk_project_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ServiceRecord_Attachment (
  recordId bigint(20) NOT NULL,
  attachmentId bigint(20) NOT NULL,
  PRIMARY KEY (recordId, attachmentId),
  CONSTRAINT fk_attachment_serviceRecord FOREIGN KEY (recordId) REFERENCES ServiceRecord (recordId),
  CONSTRAINT fk_serviceRecord_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE TempValues (
  name varchar(255) NOT NULL,
  val varchar(255) NOT NULL,
  PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO TempValues(name, val) VALUES('filesDir', '${filesDir}');
