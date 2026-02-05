RENAME TABLE RunLibraryQcStatus TO RunItemQcStatus;

ALTER TABLE RunItemQcStatus DROP INDEX uk_runLibraryQcStatus_description;
ALTER TABLE RunItemQcStatus ADD CONSTRAINT uk_runItemQcStatus_description UNIQUE (description);

ALTER TABLE RunItemQcStatus CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;