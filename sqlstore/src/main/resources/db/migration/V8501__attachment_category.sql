CREATE TABLE AttachmentCategory (
  categoryId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(255) NOT NULL,
  PRIMARY KEY (categoryId),
  UNIQUE KEY uk_attachmentCategory_alias (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Attachment ADD COLUMN categoryId bigint(20);
ALTER TABLE Attachment ADD CONSTRAINT fk_attachment_category FOREIGN KEY (categoryId) REFERENCES AttachmentCategory (categoryId);
