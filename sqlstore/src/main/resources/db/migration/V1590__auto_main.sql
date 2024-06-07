-- archive_detailedQCStatus
ALTER TABLE DetailedQcStatus ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;

