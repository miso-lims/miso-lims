RENAME TABLE RunLibraryQcStatus TO RunItemQcStatus;

ALTER TABLE RunItemQcStatus DROP INDEX uk_runLibraryQcStatus_description;
ALTER TABLE RunItemQcStatus ADD CONSTRAINT uk_runItemQcStatus_description UNIQUE (description);