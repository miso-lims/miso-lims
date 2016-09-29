-- add basic defaults

INSERT INTO QcPassedDetail (status, description, noteRequired, createdBy, creationDate, updatedBy, lastUpdated) 
  SELECT TRUE, 'Ready', FALSE, (SELECT userId FROM User WHERE loginName = 'admin'), NOW(), (SELECT userId FROM User WHERE loginName = 'admin'), NOW() 
  WHERE NOT EXISTS (SELECT description FROM QcPassedDetail WHERE description = 'Ready' LIMIT 1);
INSERT INTO QcPassedDetail (status, description, noteRequired, createdBy, creationDate, updatedBy, lastUpdated) 
  SELECT FALSE, 'Failed: QC', FALSE, (SELECT userId FROM User WHERE loginName = 'admin'), NOW(), (SELECT userId FROM User WHERE loginName = 'admin'), NOW() 
  WHERE NOT EXISTS (SELECT description FROM QcPassedDetail WHERE description = 'Failed: QC' LIMIT 1);

-- remove unnecessary option, as it will be the null case.
ALTER TABLE DetailedSample DROP FOREIGN KEY `FKa2t38wms0eer896xo4fw76tw0`;
DELETE FROM QcPassedDetail WHERE description = 'Not Ready';
UPDATE QcPassedDetail SET noteRequired = false WHERE description IN ('Waiting: Path Report', 'Failed: QC'); 

-- add a detailedQcStatusId value for all samples since this is now required for detailed samples
UPDATE DetailedSample ds
  SET ds.qcPassedDetailId = (SELECT qcPassedDetailId FROM QcPassedDetail WHERE description = 'Ready')
  WHERE ds.sampleId IN (
    SELECT s.sampleId FROM Sample s
    WHERE s.qcPassed = true);
UPDATE DetailedSample ds
  SET ds.qcPassedDetailId = NULL
  WHERE ds.sampleId IN (
    SELECT s.sampleId FROM Sample s
    WHERE s.qcPassed IS NULL);
UPDATE DetailedSample ds
  SET ds.qcPassedDetailId = (SELECT qcPassedDetailId FROM QcPassedDetail WHERE description = 'Failed: QC')
  WHERE ds.sampleId IN (
    SELECT s.sampleId FROM Sample s
    WHERE s.qcPassed = false);

ALTER TABLE DetailedSample ADD COLUMN detailedQcStatusNote VARCHAR(500) DEFAULT NULL;

ALTER TABLE QcPassedDetail RENAME TO DetailedQcStatus;
ALTER TABLE DetailedQcStatus CHANGE COLUMN qcPassedDetailId detailedQcStatusId BIGINT(20) NOT NULL AUTO_INCREMENT;
ALTER TABLE DetailedSample CHANGE COLUMN qcPassedDetailId detailedQcStatusId BIGINT(20) DEFAULT NULL;
ALTER TABLE DetailedSample ADD FOREIGN KEY (detailedQcStatusId) REFERENCES DetailedQcStatus (detailedQcStatusId);
