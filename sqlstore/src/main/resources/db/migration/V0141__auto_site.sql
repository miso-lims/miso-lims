-- gdna_aliquot_archivedrelationship
--StartNoTest
--StartNoTest
SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';
SELECT sampleClassId INTO @gDNAaqId FROM SampleClass WHERE alias = 'gDNA (aliquot)';

INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
SELECT @gDNAaqId, @gDNAaqId, @user,@time,@user,@time,1 WHERE NOT EXISTS (SELECT * FROM SampleValidRelationship WHERE parentId = @gDNAaqId AND childId = @gDNAaqId);
--EndNoTest
--EndNoTest

-- missing_ides_indices
--StartNoTest
--StartNoTest
SELECT indexFamilyId INTO @famId FROM IndexFamily WHERE name = 'iDES 8bp';

INSERT INTO Indices (name, sequence, position, indexFamilyId)
VALUES ('Index 11','GTCCTTGT',1,@famId),('Index 12','CATTCCGT',1,@famId);
--EndNoTest
--EndNoTest

-- steve_narod
--StartNoTest
SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
INSERT INTO Institute(alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES ('Womens College Hospital', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
INSERT INTO Lab(instituteId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES ((SELECT instituteId FROM Institute WHERE alias = 'Womens College Hospital'), 'Steve Narod', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
--EndNoTest

