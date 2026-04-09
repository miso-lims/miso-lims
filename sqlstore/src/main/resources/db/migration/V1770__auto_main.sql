-- assay_changelog
CREATE TABLE AssayChangeLog (
  assayChangeLogId bigint NOT NULL AUTO_INCREMENT,
  assayId bigint NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint NOT NULL,
  message longtext NOT NULL,
  changeTime timestamp NOT NULL,
  PRIMARY KEY (assayChangeLogId),
  CONSTRAINT fk_assayChangeLog_assay FOREIGN KEY (assayId) REFERENCES Assay (assayId),
  CONSTRAINT fk_assayChangeLog_user FOREIGN KEY (userId) REFERENCES User (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE Assay
  ADD COLUMN creator bigint NOT NULL DEFAULT 1,
  ADD COLUMN created timestamp NOT NULL DEFAULT NOW(),
  ADD COLUMN lastModifier bigint NOT NULL DEFAULT 1,
  ADD COLUMN lastModified timestamp NOT NULL DEFAULT NOW();

ALTER TABLE Assay
  ALTER COLUMN creator DROP DEFAULT,
  ALTER COLUMN created DROP DEFAULT,
  ALTER COLUMN lastModifier DROP DEFAULT,
  ALTER COLUMN lastModified DROP DEFAULT;

