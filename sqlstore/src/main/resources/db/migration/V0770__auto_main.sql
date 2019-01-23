-- add_probeId_to_freezer

ALTER TABLE StorageLocation ADD COLUMN probeId varchar(50) DEFAULT NULL;
ALTER TABLE StorageLocation ADD CONSTRAINT storageLocation_probeId_uk UNIQUE(probeId);


