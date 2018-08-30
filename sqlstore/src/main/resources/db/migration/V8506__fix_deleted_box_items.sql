DELETE FROM BoxPosition WHERE targetType = 'SAMPLE' AND targetId NOT IN (SELECT sampleId FROM Sample);
DELETE FROM BoxPosition WHERE targetType = 'LIBRARY' AND targetId NOT IN (SELECT libraryId FROM Library);
DELETE FROM BoxPosition WHERE targetType = 'DILUTION' AND targetId NOT IN (SELECT dilutionId FROM LibraryDilution);
