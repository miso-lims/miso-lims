-- volume_concentration_units

UPDATE Sample SET volumeUnits = NULL WHERE volume IS NULL;
UPDATE Sample SET concentrationUnits = NULL WHERE concentration IS NULL;
UPDATE Sample SET volumeUnits = 'MICROLITRES' WHERE volumeUnits = 'MICROLITRE';
UPDATE Sample SET volumeUnits = 'MICROLITRES' WHERE volume IS NOT NULL AND volumeUnits IS NULL;
UPDATE Sample SET concentrationUnits = 'NANOGRAMS_PER_MICROLITRE' WHERE concentration IS NOT NULL AND concentrationUnits IS NULL;

UPDATE Library SET volumeUnits = NULL WHERE volume IS NULL;
UPDATE Library SET concentrationUnits = NULL WHERE concentration IS NULL;
UPDATE Library SET volumeUnits = 'MICROLITRES' WHERE volumeUnits = 'MICROLITRE';
UPDATE Library SET volumeUnits = 'MICROLITRES' WHERE volume IS NOT NULL AND volumeUnits IS NULL;
UPDATE Library SET concentrationUnits = 'NANOGRAMS_PER_MICROLITRE' WHERE concentration IS NOT NULL AND concentrationUnits IS NULL;

UPDATE LibraryDilution SET volumeUnits = NULL WHERE volume IS NULL;
UPDATE LibraryDilution SET concentrationUnits = NULL WHERE concentration IS NULL;
UPDATE LibraryDilution SET volumeUnits = 'MICROLITRES' WHERE volumeUnits = 'MICROLITRE';
UPDATE LibraryDilution SET volumeUnits = 'MICROLITRES' WHERE volume IS NOT NULL AND volumeUnits IS NULL;
UPDATE LibraryDilution SET concentrationUnits = 'NANOGRAMS_PER_MICROLITRE' WHERE concentration IS NOT NULL AND concentrationUnits IS NULL;

UPDATE Pool SET volumeUnits = NULL WHERE volume IS NULL;
UPDATE Pool SET concentrationUnits = NULL WHERE concentration IS NULL;
UPDATE Pool SET volumeUnits = 'MICROLITRES' WHERE volumeUnits = 'MICROLITRE';
UPDATE Pool SET volumeUnits = 'MICROLITRES' WHERE volume IS NOT NULL AND volumeUnits IS NULL;
UPDATE Pool SET concentrationUnits = 'NANOGRAMS_PER_MICROLITRE' WHERE concentration IS NOT NULL AND concentrationUnits IS NULL;


-- fix_deleted_box_items

DELETE FROM BoxPosition WHERE targetType = 'SAMPLE' AND targetId NOT IN (SELECT sampleId FROM Sample);
DELETE FROM BoxPosition WHERE targetType = 'LIBRARY' AND targetId NOT IN (SELECT libraryId FROM Library);
DELETE FROM BoxPosition WHERE targetType = 'DILUTION' AND targetId NOT IN (SELECT dilutionId FROM LibraryDilution);


