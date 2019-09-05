ALTER TABLE SequencingParameters ADD COLUMN readLength2 int;
UPDATE SequencingParameters SET readLength2 = paired * readLength;
ALTER TABLE SequencingParameters MODIFY readLength2 int NOT NULL;
ALTER TABLE SequencingParameters DROP COLUMN paired;
