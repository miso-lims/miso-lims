-- add_truseq_methylation_kit
--StartNoTest
INSERT INTO KitDescriptor(name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier)
VALUES ('TruSeq Methylation Epic Exome', 0, 'Illumina', 1, 0, 'Library', 'Illumina', 'n/a', 1);
--EndNoTest

-- missing_things_archived_svrs
--StartNoTest
--StartNoTest
SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

INSERT INTO Lab (instituteId, alias, createdBy, creationDate, updatedBy, lastUpdated)
VALUES ((SELECT instituteId FROM Institute WHERE alias = 'University Health Network'), 'Pathology', @user, @time, @user, @time);

INSERT INTO TissueOrigin (alias, description, createdBy, creationDate, updatedBy, lastUpdated)
VALUES ('As', 'Ascites Fluid', @user, @time, @user, @time);

-- archived relationships allowing bypass of second stock in stock -> stock -> aliquot relationships
INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
VALUES (
  (SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA (stock)'),
  (SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (aliquot)'),
  @user, @time, @user, @time, 1
), (
  (SELECT sampleClassId FROM SampleClass WHERE alias = 'whole RNA (stock)'),
  (SELECT sampleClassId FROM SampleClass WHERE alias = 'cDNA (aliquot)'),
  @user, @time, @user, @time, 1
);
--EndNoTest
--EndNoTest

-- remove_unintended_svrs
--StartNoTest
--StartNoTest
DELETE svr FROM SampleValidRelationship svr
JOIN SampleClass parent ON parent.sampleClassId = svr.parentId
JOIN SampleClass child ON child.sampleClassId = svr.childId
WHERE parent.sampleCategory = 'Identity' AND child.sampleCategory <> 'Tissue';
--EndNoTest
--EndNoTest

