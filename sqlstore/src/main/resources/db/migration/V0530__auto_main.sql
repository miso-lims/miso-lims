-- increase_groupId_length

ALTER TABLE DetailedSample CHANGE COLUMN groupId groupId VARCHAR(100) DEFAULT NULL;
ALTER TABLE DetailedLibrary CHANGE COLUMN groupId groupId VARCHAR(100) DEFAULT NULL;


