-- StartNoTest
SELECT userId INTO @admin FROM `User` WHERE loginName = 'admin';
SELECT sampleClassId INTO @cellline FROM SampleClass WHERE alias = 'Cell Line';
SELECT sampleClassId INTO @curls FROM SampleClass WHERE alias = 'Curls';
SELECT sampleClassId INTO @rnastock FROM SampleClass WHERE alias = 'whole RNA (stock)';
INSERT INTO SampleValidRelationship(parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived) VALUES
  (@cellline, @curls, @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP, FALSE),
  (@curls, @rnastock, @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP, FALSE);

INSERT INTO LibraryType(description, platformType, archived) VALUES ('Total RNA', 'Illumina', FALSE);
-- EndNoTest
