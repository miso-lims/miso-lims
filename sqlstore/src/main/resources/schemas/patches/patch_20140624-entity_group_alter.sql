USE lims;

ALTER TABLE lims.EntityGroup ADD COLUMN creatorId BIGINT(20) NOT NULL;
ALTER TABLE lims.EntityGroup ADD COLUMN creationDate DATE NOT NULL;
ALTER TABLE lims.EntityGroup ADD COLUMN assigneeId BIGINT(20) NULL;