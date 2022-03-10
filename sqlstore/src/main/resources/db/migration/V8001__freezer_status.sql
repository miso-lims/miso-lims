-- freezer_status
ALTER TABLE StorageLocation ADD COLUMN retired BOOLEAN NOT NULL DEFAULT 0;

