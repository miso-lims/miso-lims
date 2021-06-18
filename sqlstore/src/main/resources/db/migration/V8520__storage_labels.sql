CREATE TABLE StorageLabel (
  labelId bigint(20) NOT NULL AUTO_INCREMENT,
  label varchar(100) NOT NULL,
  PRIMARY KEY (labelId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE StorageLocation
  ADD COLUMN labelId bigint(20),
  ADD CONSTRAINT fk_storageLocation_label FOREIGN KEY (labelId) REFERENCES StorageLabel (labelId);
