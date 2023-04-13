-- freezer_changelogs

DROP TABLE IF EXISTS StorageLocationChangeLog;
CREATE TABLE StorageLocationChangeLog (
  storageLocationChangeLogId bigint NOT NULL AUTO_INCREMENT,
  locationId bigint NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint NOT NULL,
  message longtext NOT NULL,
  changeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (storageLocationChangeLogId),
  CONSTRAINT fk_storageLocationChangeLog_storageLocation FOREIGN KEY (locationId) REFERENCES StorageLocation(locationId),
  CONSTRAINT fk_storageLocationChangeLog_user FOREIGN KEY (userId) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE StorageLocation ADD COLUMN creator bigint;
ALTER TABLE StorageLocation ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE StorageLocation ADD COLUMN lastModifier bigint;
ALTER TABLE StorageLocation ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

-- StartNoTest
SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
UPDATE StorageLocation SET
  creator = @admin, lastModifier = @admin;
-- EndNoTest

ALTER TABLE StorageLocation CHANGE COLUMN creator creator bigint NOT NULL;
ALTER TABLE StorageLocation ADD CONSTRAINT fk_storagelocation_creator FOREIGN KEY (creator) REFERENCES User (userId);
ALTER TABLE StorageLocation CHANGE COLUMN lastModifier lastModifier bigint NOT NULL;
ALTER TABLE StorageLocation ADD CONSTRAINT fk_storagelocation_modifier FOREIGN KEY (lastModifier) REFERENCES User (userId);


