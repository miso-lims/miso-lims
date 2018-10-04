CREATE TABLE Sample_Attachment (
  sampleId bigint(20) NOT NULL,
  attachmentId bigint(20) NOT NULL,
  PRIMARY KEY (sampleId, attachmentId),
  CONSTRAINT fk_attachment_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId),
  CONSTRAINT fk_sample_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
