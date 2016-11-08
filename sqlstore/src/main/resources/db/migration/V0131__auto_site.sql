-- lockdown_aml
--StartNoTest
--StartNoTest
SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

INSERT INTO KitDescriptor (name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier) VALUES
('KAPA Hyper Prep & SureSelect XT2 v5', 5, 'KAPA', 1, 0, 'Library', 'Illumina', NULL, @user);

SELECT kitDescriptorId INTO @kapa FROM KitDescriptor WHERE name = "KAPA Hyper Prep & SureSelect XT2 v5";

INSERT INTO TargetedResequencing (alias, description, createdBy, creationDate, updatedBy, lastUpdated, kitDescriptorId) VALUES
('xGen Lockdown AML', 'xGen Lockdown AML', @user, @time, @user, @time, @kapa);
--EndNoTest
--EndNoTest

-- add_more_libraryDesigns
--StartNoTest
--StartNoTest
INSERT INTO LibraryDesignCode (code, description) VALUES ("TR", "TR");

DELETE FROM LibraryDesign WHERE sampleClassId = (SELECT sampleClassId FROM SampleClass WHERE alias = "cDNA (aliquot)") AND name = "SM";

INSERT INTO LibraryDesign (name, librarySelectionType, libraryStrategyType, libraryDesignCodeId, sampleClassId) VALUES
  ("WG", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "PCR"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "WGS"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "WG"), (SELECT sampleClassId FROM SampleClass WHERE alias = "gDNA_wga (aliquot)")),
  ("TS (Hybrid Selection)", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "Hybrid Selection"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "AMPLICON"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "TS"), (SELECT sampleClassId FROM SampleClass WHERE alias = "gDNA_wga (aliquot)")),
  ("TS (PCR)", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "PCR"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "AMPLICON"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "TS"), (SELECT sampleClassId FROM SampleClass WHERE alias = "gDNA_wga (aliquot)")),
  ("EX", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "Hybrid Selection"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "WXS"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "EX"), (SELECT sampleClassId FROM SampleClass WHERE alias = "gDNA_wga (aliquot)")),
  ("MR", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "cDNA"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "RNA-Seq"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "MR"), (SELECT sampleClassId FROM SampleClass WHERE alias = "mRNA")),
  ("SM", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "size fractionation"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "RNA-Seq"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "SM"), (SELECT sampleClassId FROM SampleClass WHERE alias = "smRNA")),
  ("WT", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "cDNA"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "RNA-Seq"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "WT"), (SELECT sampleClassId FROM SampleClass WHERE alias = "rRNA_depleted")),
  ("TR", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "cDNA"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "RNA-Seq"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "TR"), (SELECT sampleClassId FROM SampleClass WHERE alias = "whole RNA (aliquot)"));
--EndNoTest
--EndNoTest

-- misc_values
--StartNoTest
--StartNoTest
SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

INSERT INTO SamplePurpose (alias, createdBy, creationDate, updatedBy, lastUpdated)
VALUES ('Ion Torrent', @user, @time, @user, @time);

UPDATE Lab SET instituteId = (SELECT instituteId FROM Institute WHERE alias = 'Ottawa Hospital Research Institute')
WHERE alias = 'John Bell';

INSERT INTO Institute (alias, createdBy, creationDate, updatedBy, lastUpdated)
VALUES ('Sick Kids', @user, @time, @user, @time);
SET @institute = LAST_INSERT_ID();

INSERT INTO Lab (instituteId, alias, createdBy, creationDate, updatedBy, lastUpdated)
VALUES (@institute, 'Not Specified', @user, @time, @user, @time);

INSERT INTO Indices (name, sequence, position, indexFamilyId)
VALUES ('S517', 'GCGTAAGA', 2, (SELECT indexFamilyId FROM IndexFamily WHERE name = 'Nextera XT Dual Index'));
--EndNoTest
--EndNoTest

-- london_hs
--StartNoTest
--StartNoTest
SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
INSERT INTO Institute(alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES ('London Health Sciences Center', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
INSERT INTO Lab(instituteId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES ((SELECT instituteId FROM Institute WHERE alias = 'London Health Sciences Center'), 'Not Specified', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
--EndNoTest
--EndNoTest

