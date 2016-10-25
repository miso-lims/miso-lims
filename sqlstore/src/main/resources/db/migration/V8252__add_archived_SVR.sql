--StartNoTest
SELECT userId INTO @user FROM User WHERE loginName = 'admin';
SET @time = NOW();

INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
VALUES (
    (SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA (aliquot)'),
    (SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (aliquot)'),
    @user, @time, @user, @time, 1
);
--EndNoTest