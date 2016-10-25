--StartNoTest
SELECT userId INTO @user FROM User WHERE loginName = 'admin';
SET @time = NOW();

INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
VALUES (
    (SELECT sampleClassId FROM SampleClass WHERE alias = 'Primary Tumor Tissue'),
    (SELECT sampleClassId FROM SampleClass WHERE alias = 'LCM Tube'),
    @user, @time, @user, @time, 1
);

INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
VALUES (
    (SELECT sampleClassId FROM SampleClass WHERE alias = 'Xenograft Tissue'),
    (SELECT sampleClassId FROM SampleClass WHERE alias = 'LCM Tube'),
    @user, @time, @user, @time, 1
);
--EndNoTest
