DROP TABLE IF EXISTS ArrayRun_Attachment;

CREATE TABLE ArrayRun_Attachment (
  arrayRunId bigint NOT NULL,
  attachmentId bigint NOT NULL,
  PRIMARY KEY (arrayRunId, attachmentId),
  CONSTRAINT fk_attachment_arrayrun FOREIGN KEY (arrayRunId) REFERENCES ArrayRun (arrayRunId),
  CONSTRAINT fk_arrayrun_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;