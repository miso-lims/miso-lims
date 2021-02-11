-- qc_dates
-- These will get re-created in afterMigrate. Need to be removed to avoid interfering with updates
DROP TRIGGER IF EXISTS SampleChange;
DROP TRIGGER IF EXISTS LibraryChange;
DROP TRIGGER IF EXISTS LibraryAliquotChange;
DROP TRIGGER IF EXISTS RunPartitionLibraryAliquotUpdate;

ALTER TABLE Sample ADD COLUMN qcDate DATE;
ALTER TABLE Library ADD COLUMN qcDate DATE;
ALTER TABLE LibraryAliquot ADD COLUMN qcDate DATE;
ALTER TABLE Run ADD COLUMN qcDate DATE;
ALTER TABLE Run ADD COLUMN dataReviewDate DATE;
ALTER TABLE Run_Partition_LibraryAliquot ADD COLUMN qcDate DATE;

UPDATE Sample SET qcDate = COALESCE((
  SELECT MAX(changeTime) FROM SampleChangeLog
  WHERE SampleChangeLog.sampleId = Sample.sampleId
  AND columnsChanged LIKE '%detailedQcStatusId%'
), created)
WHERE detailedQcStatusId IS NOT NULL;

UPDATE Library SET qcDate = COALESCE((
  SELECT MAX(changeTime) FROM LibraryChangeLog
  WHERE LibraryChangeLog.libraryId = Library.libraryId
  AND columnsChanged LIKE '%detailedQcStatusId%'
), created)
WHERE detailedQcStatusId IS NOT NULL;

UPDATE LibraryAliquot SET qcDate = COALESCE((
  SELECT MAX(changeTime) FROM LibraryAliquotChangeLog
  WHERE LibraryAliquotChangeLog.aliquotId = LibraryAliquot.aliquotId
  AND columnsChanged LIKE '%detailedQcStatusId%'
), created)
WHERE detailedQcStatusId IS NOT NULL;

UPDATE Run SET qcDate = COALESCE((
  SELECT MAX(changeTime) FROM RunChangeLog
  WHERE RunChangeLog.runId = Run.runId
  AND columnsChanged LIKE '%qcPassed%'
), created)
WHERE qcPassed IS NOT NULL;

UPDATE Run SET dataReviewDate = COALESCE((
  SELECT MAX(changeTime) FROM RunChangeLog
  WHERE RunChangeLog.runId = Run.runId
  AND columnsChanged LIKE '%dataReview%'
), created)
WHERE dataReview IS NOT NULL;

UPDATE Run_Partition_LibraryAliquot rla
JOIN _Partition part ON part.partitionId = rla.partitionId
JOIN SequencerPartitionContainer spc ON spc.containerId = part.containerId
JOIN LibraryAliquot ali ON ali.aliquotId = rla.aliquotId
SET rla.qcDate = COALESCE((
  SELECT MAX(changeTime) FROM RunChangeLog
  WHERE RunChangeLog.runId = rla.runId
  AND columnsChanged LIKE '%aliquot qcPassed%'
  AND message LIKE CONCAT(spc.identificationBarcode, '-', part.partitionNumber, '-', ali.alias, '%')
), NOW())
WHERE qcPassed IS NOT NULL;

-- max_volumes
ALTER TABLE Sample MODIFY COLUMN initialVolume DECIMAL(16,10);
ALTER TABLE Sample MODIFY COLUMN volume DECIMAL(16,10);
ALTER TABLE Sample MODIFY COLUMN volumeUsed DECIMAL(16,10);

ALTER TABLE Library MODIFY COLUMN initialVolume DECIMAL(16,10);
ALTER TABLE Library MODIFY COLUMN volume DECIMAL(16,10);
ALTER TABLE Library MODIFY COLUMN volumeUsed DECIMAL(16,10);

ALTER TABLE LibraryAliquot MODIFY COLUMN volume DECIMAL(16,10);
ALTER TABLE LibraryAliquot MODIFY COLUMN volumeUsed DECIMAL(16,10);

ALTER TABLE Pool MODIFY COLUMN volume DECIMAL(16,10);

