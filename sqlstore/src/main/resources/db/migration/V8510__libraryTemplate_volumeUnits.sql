ALTER TABLE LibraryTemplate ADD COLUMN volumeUnits varchar(30);
UPDATE LibraryTemplate SET volumeUnits = 'MICROLITRES' WHERE defaultVolume IS NOT NULL;
