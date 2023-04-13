-- library_detailed_qc
SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
SET @ready = 'Ready';
SET @failed = 'Failed: QC';

INSERT INTO DetailedQcStatus (status, description, noteRequired, createdBy, creationDate, updatedBy, lastUpdated) 
  SELECT TRUE, 'Ready', FALSE, @admin, NOW(), (SELECT userId FROM User WHERE loginName = 'admin'), NOW() 
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM DetailedQcStatus WHERE description = @ready);
INSERT INTO DetailedQcStatus (status, description, noteRequired, createdBy, creationDate, updatedBy, lastUpdated) 
  SELECT FALSE, 'Failed: QC', FALSE, @admin, NOW(), (SELECT userId FROM User WHERE loginName = 'admin'), NOW() 
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM DetailedQcStatus WHERE description = @failed);

SELECT detailedQcStatusId INTO @readyId FROM DetailedQcStatus WHERE description = @ready;
SELECT detailedQcStatusId INTO @failedId FROM DetailedQcStatus WHERE description = @failed;

-- Library
ALTER TABLE Library ADD COLUMN detailedQcStatusId bigint;
ALTER TABLE Library ADD CONSTRAINT fk_library_detailedQcStatus FOREIGN KEY (detailedQcStatusId) REFERENCES DetailedQcStatus (detailedQcStatusId);
ALTER TABLE Library ADD COLUMN detailedQcStatusNote varchar(500);

UPDATE Library SET detailedQcStatusId = @readyId WHERE qcPassed = TRUE;
UPDATE Library SET detailedQcStatusId = @failedId WHERE qcPassed = FALSE;

ALTER TABLE Library DROP COLUMN qcPassed;

-- Library Aliquot
ALTER TABLE LibraryAliquot ADD COLUMN detailedQcStatusId bigint;
ALTER TABLE LibraryAliquot ADD CONSTRAINT fk_libraryAliquot_detailedQcStatus FOREIGN KEY (detailedQcStatusId) REFERENCES DetailedQcStatus (detailedQcStatusId);
ALTER TABLE LibraryAliquot ADD COLUMN detailedQcStatusNote varchar(500);

UPDATE LibraryAliquot SET detailedQcStatusId = @readyId WHERE qcPassed = TRUE;
UPDATE LibraryAliquot SET detailedQcStatusId = @failedId WHERE qcPassed = FALSE;

ALTER TABLE LibraryAliquot DROP COLUMN qcPassed;

-- Sample

-- fix for bug with detailed sample mode where qcPassed was not updated when detailedQcStatusId was null
SELECT COUNT(*) INTO @plainSamples FROM Sample WHERE sampleClassId IS NULL;
UPDATE Sample SET qcPassed = NULL
WHERE qcPassed IS NOT NULL AND detailedQcStatusId IS NULL
AND @plainSamples = 0;

UPDATE Sample SET detailedQcStatusId = @readyId WHERE detailedQcStatusId IS NULL AND qcPassed = TRUE;
UPDATE Sample SET detailedQcStatusId = @failedId WHERE detailedQcStatusId IS NULL AND qcPassed = FALSE;

ALTER TABLE Sample DROP COLUMN qcPassed;

