-- more_TOs
-- StartNoTest
--StartNoTest
SELECT userId INTO @user FROM User WHERE loginName = 'admin';
SET @time = NOW();

INSERT INTO TissueOrigin (alias, description, createdBy, creationDate, updatedBy, lastUpdated) VALUES
('Mo', 'Mouth', @user, @time, @USER, @time),
('Np', 'Nasopharynx', @user, @time, @user, @time),
('To', 'Throat', @user, @time, @user, @time),
('Hp', 'Hypopharynx', @user, @time, @user, @time);
--EndNoTest
-- EndNoTest

-- indices_and_origins
-- StartNoTest
--StartNoTest
INSERT INTO Indices(name, sequence, position, indexFamilyId)
VALUES ('N517', 'GCGTAAGA', 2, (SELECT indexFamilyId FROM IndexFamily WHERE name = 'Nextera DNA Dual Index'));

SELECT userId INTO @user FROM User WHERE loginName = 'admin';
SET @time = NOW();

INSERT INTO TissueOrigin (alias, description, createdBy, creationDate, updatedBy, lastUpdated)
VALUES ('Pn', 'Peripheral Nerve', @user, @time, @user, @time),
('Um', 'Umbilical Cord', @user, @time, @user, @time);
--EndNoTest
-- EndNoTest

-- cell_line_curls
-- StartNoTest
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
-- EndNoTest

-- add_missing_suffix_unders
-- StartNoTest
UPDATE SampleClass SET suffix = CONCAT(suffix, '_') WHERE sampleCategory = 'Aliquot' AND suffix NOT LIKE '%\\_';
-- EndNoTest

