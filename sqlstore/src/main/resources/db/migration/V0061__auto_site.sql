-- archived_SVR
--StartNoTest
INSERT INTO SampleValidRelationship  (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived) VALUES 
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'whole RNA (stock)'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'rRNA_depleted'),1,NOW(),1,NOW(), 1);
--EndNoTest

-- ATAC-seq_library_design
--StartNoTest
INSERT INTO LibraryDesign (name, sampleClassId, librarySelectionType, libraryStrategyType, suffix, libraryType) VALUES ('AS', (SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA (aliquot)'), (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = 'Hybrid Selection'), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = 'OTHER'), '_AS', (SELECT libraryTypeId FROM LibraryType WHERE description = 'Paired End' AND platformType = 'Illumina'));
--EndNoTest

-- qc_details
--StartNoTest
SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

UPDATE QcPassedDetail SET description = "Ready" WHERE status=1 AND description = "";

UPDATE QcPassedDetail SET description = "Failed: STR" WHERE description = "Failed STR";
UPDATE QcPassedDetail SET description = "Failed: Diagnosis" WHERE description = "Failed Diagnosis";
UPDATE QcPassedDetail SET description = "Failed: QC" WHERE description = "Failed QC";

UPDATE QcPassedDetail SET description = "Not Ready" WHERE status IS NULL AND description = "";
UPDATE QcPassedDetail SET status = NULL WHERE description = "Reference Required";

INSERT INTO QcPassedDetail (status, description, noteRequired, createdBy, creationDate, updatedBy, lastUpdated)
VALUES (NULL, "Waiting: Receive Tissue", false, @user, @time, @user, @time);
--EndNoTest

