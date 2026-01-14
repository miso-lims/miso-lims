CREATE TABLE Library_Aliquot_Attachment (
  aliquotId bigint NOT NULL,
  attachmentId bigint NOT NULL,
  PRIMARY KEY (aliquotId, attachmentId),
  CONSTRAINT fk_attachment_library_aliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId),
  CONSTRAINT fk_library_aliquot_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
