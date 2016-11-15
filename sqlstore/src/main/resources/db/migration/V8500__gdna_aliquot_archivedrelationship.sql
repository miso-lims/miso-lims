--StartNoTest
SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
VALUES (
    (SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA (aliquot)'),
    (SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA (aliquot)'),
    @user,@time,@user,@time,1
);
--EndNoTest
