ALTER TABLE Library ADD COLUMN receivedDate DATE DEFAULT NULL;

ALTER TABLE DetailedLibrary ADD COLUMN groupId varchar(10) DEFAULT NULL;
ALTER TABLE DetailedLibrary ADD COLUMN groupDescription varchar(255) DEFAULT NULL;
