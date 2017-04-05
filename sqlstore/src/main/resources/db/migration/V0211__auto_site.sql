-- update_kit_names
-- StartNoTest
--StartNoTest
UPDATE KitDescriptor SET name = 'KAPA Hyper Prep and SureSelect XT' WHERE name = 'KAPA Hyper Prep & SureSelect XT';
UPDATE KitDescriptor SET name = 'AmpliSeq and NEB' WHERE name = 'AmpliSeq & NEB';
UPDATE KitDescriptor SET name = 'KAPA Hyper Prep and IDT xGen' WHERE name = 'KAPA Hyper Prep & IDT xGen';
UPDATE KitDescriptor SET name = 'KAPA Hyper Prep and SureSelect XT2 v5' WHERE name = 'KAPA Hyper Prep & SureSelect XT2 v5';
--EndNoTest
-- EndNoTest

-- delete_institute_typo
-- StartNoTest
-- StartNoTest
SELECT instituteId INTO @centre FROM Institute WHERE alias = 'Sheba Medical Centre';
DELETE FROM Lab WHERE instituteId = @centre;
DELETE FROM Institute WHERE instituteId = @centre;
-- EndNoTest
-- EndNoTest

-- Unspecified_Tissue
-- StartNoTest
-- StartNoTest

SELECT userId INTO @user FROM User WHERE loginName = 'admin';
SET @time = NOW();

INSERT INTO SampleClass(alias, sampleCategory, createdBy, creationDate, updatedBy, lastUpdated)
SELECT * FROM (
  SELECT 'Unspecified Tissue', 'Tissue', @user AS creator, @time AS created, @user AS modifier, @time AS modified FROM DUAL
) AS data
WHERE NOT EXISTS (SELECT 1 FROM SampleClass WHERE alias = 'Unspecified Tissue' AND sampleCategory = 'Tissue');

SET @unspecified = LAST_INSERT_ID();
INSERT INTO SampleValidRelationship(parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
VALUES ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Identity'), @unspecified, @user, @time, @user, @time, 0),
    (@unspecified, (SELECT sampleClassId FROM SampleClass WHERE alias = 'Primary Tumor Tissue'), @user, @time, @user, @time, 1),
    (@unspecified, (SELECT sampleClassId FROM SampleClass WHERE alias = 'Metastatic Tumor Tissue'), @user, @time, @user, @time, 1),
    (@unspecified, (SELECT sampleClassId FROM SampleClass WHERE alias = 'Reference Tissue'), @user, @time, @user, @time, 1),
    (@unspecified, (SELECT sampleClassId FROM SampleClass WHERE alias = 'Xenograft Tissue'), @user, @time, @user, @time, 1);

-- EndNoTest
-- EndNoTest

