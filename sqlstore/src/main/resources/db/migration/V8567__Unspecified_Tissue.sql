-- StartNoTest

SELECT userId INTO @user FROM User WHERE loginName = 'admin';
SET @time = NOW();

INSERT INTO SampleClass(alias, sampleCategory, createdBy, creationDate, updatedBy, lastUpdated)
VALUES ('Unspecified Tissue', 'Tissue', @user, @time, @user, @time);

SET @unspecified = LAST_INSERT_ID();
INSERT INTO SampleValidRelationship(parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
VALUES ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Identity'), @unspecified, @user, @time, @user, @time, 0),
    (@unspecified, (SELECT sampleClassId FROM SampleClass WHERE alias = 'Primary Tumor Tissue'), @user, @time, @user, @time, 1),
    (@unspecified, (SELECT sampleClassId FROM SampleClass WHERE alias = 'Metastatic Tumor Tissue'), @user, @time, @user, @time, 1),
    (@unspecified, (SELECT sampleClassId FROM SampleClass WHERE alias = 'Reference Tissue'), @user, @time, @user, @time, 1),
    (@unspecified, (SELECT sampleClassId FROM SampleClass WHERE alias = 'Xenograft Tissue'), @user, @time, @user, @time, 1);

-- EndNoTest
