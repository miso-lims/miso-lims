-- collapse_tissues
-- StartNoTest
-- StartNoTest
INSERT INTO SampleClass(alias, sampleCategory, suffix, createdBy, creationDate, updatedBy, lastUpdated, dnaseTreatable)
  VALUES ('Tissue', 'Tissue', NULL, (SELECT userId FROM User WHERE loginName = 'admin'), CURRENT_TIMESTAMP, (SELECT userId FROM User WHERE loginName = 'admin'), CURRENT_TIMESTAMP, FALSE);

SET @tissueId = LAST_INSERT_ID();

ALTER TABLE TissueType ADD COLUMN sampleTypeName varchar(255);

-- This query will fail if there's more than one sample type for each tissue class. This is by design. Those records need to be fixed.
UPDATE TissueType SET sampleTypeName = (
  SELECT DISTINCT alias
  FROM SampleClass
  JOIN DetailedSample USING (sampleClassId)
  JOIN SampleTissue USING (sampleId)
  WHERE sampleCategory = 'Tissue'
  AND SampleTissue.tissueTypeId = TissueType.tissueTypeId
);

UPDATE DetailedSample SET sampleClassId = @tissueId WHERE sampleClassId IN (SELECT sampleClassId FROM SampleClass WHERE sampleCategory = 'Tissue');

SELECT userId INTO @user FROM User WHERE loginName = 'admin';
SET @now = CURRENT_TIMESTAMP();

INSERT INTO SampleValidRelationship(parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
(
  SELECT DISTINCT parentId, @tissueId, @user, @now, @user, @now, MIN(archived)
  FROM SampleValidRelationship
  WHERE childId IN (SELECT sampleClassId FROM SampleClass WHERE sampleCategory = 'Tissue')
  GROUP BY parentId
)
UNION
(
  SELECT DISTINCT @tissueId, childId, @user, @now, @user, @now, MIN(archived)
  FROM SampleValidRelationship
  WHERE parentId IN (SELECT sampleClassId FROM SampleClass WHERE sampleCategory = 'Tissue')
  GROUP BY childId
);

INSERT INTO SampleValidRelationship(parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
VALUES (@tissueId, @tissueId, @user, @now, @user, @now, 0);

DELETE FROM SampleValidRelationship WHERE childId IN (SELECT sampleClassId FROM SampleClass WHERE sampleCategory = 'Tissue' AND sampleClassId != @tissueId);
DELETE FROM SampleValidRelationship WHERE parentId IN (SELECT sampleClassId FROM SampleClass WHERE sampleCategory = 'Tissue' AND sampleClassId != @tissueId);

DELETE FROM SampleClass WHERE sampleCategory = 'Tissue' AND sampleClassId != @tissueId;
-- EndNoTest
-- EndNoTest

