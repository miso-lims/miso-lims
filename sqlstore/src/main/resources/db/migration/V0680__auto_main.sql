-- discards_non-null

UPDATE SampleSlide SET discards = 0 WHERE discards IS NULL;
ALTER TABLE SampleSlide CHANGE COLUMN discards discards int NOT NULL DEFAULT 0;


-- attachment_category

CREATE TABLE AttachmentCategory (
  categoryId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(255) NOT NULL,
  PRIMARY KEY (categoryId),
  UNIQUE KEY uk_attachmentCategory_alias (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE Attachment ADD COLUMN categoryId bigint;
ALTER TABLE Attachment ADD CONSTRAINT fk_attachment_category FOREIGN KEY (categoryId) REFERENCES AttachmentCategory (categoryId);


-- attachments

CREATE TABLE Sample_Attachment (
  sampleId bigint NOT NULL,
  attachmentId bigint NOT NULL,
  PRIMARY KEY (sampleId, attachmentId),
  CONSTRAINT fk_attachment_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId),
  CONSTRAINT fk_sample_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Library_Attachment (
  libraryId bigint NOT NULL,
  attachmentId bigint NOT NULL,
  PRIMARY KEY (libraryId, attachmentId),
  CONSTRAINT fk_attachment_library FOREIGN KEY (libraryId) REFERENCES Library (libraryId),
  CONSTRAINT fk_library_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


