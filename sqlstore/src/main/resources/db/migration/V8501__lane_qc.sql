ALTER TABLE PartitionQCType ADD COLUMN orderFulfilled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE PartitionQCType ADD COLUMN analysisSkipped BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE PartitionQCType SET orderFulfilled = FALSE WHERE description IN ('Failed: Instrument problem', 'Failed: Other problem');
UPDATE PartitionQCType SET analysisSkipped = TRUE WHERE description LIKE 'Failed: %';
