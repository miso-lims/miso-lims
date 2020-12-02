-- These will get re-created in afterMigrate. Need to be removed to avoid interfering with updates
DROP TRIGGER IF EXISTS SampleChange;
DROP TRIGGER IF EXISTS LibraryChange;
DROP TRIGGER IF EXISTS LibraryAliquotChange;
DROP TRIGGER IF EXISTS RunPartitionLibraryAliquotUpdate;

ALTER TABLE Sample ADD COLUMN qcUser bigint(20);
ALTER TABLE Sample ADD CONSTRAINT fk_sample_qcUser FOREIGN KEY (qcUser) REFERENCES User (userId);
UPDATE Sample SET qcUser = COALESCE((
  SELECT userId FROM SampleChangeLog
  WHERE SampleChangeLog.sampleId = Sample.sampleId
  AND columnsChanged LIKE '%detailedQcStatusId%'
  ORDER BY changeTime DESC LIMIT 1
), creator)
WHERE detailedQcStatusId IS NOT NULL;

ALTER TABLE Library ADD COLUMN qcUser bigint(20);
ALTER TABLE Library ADD CONSTRAINT fk_library_qcUser FOREIGN KEY (qcUser) REFERENCES User (userId);
UPDATE Library SET qcUser = COALESCE((
  SELECT userId FROM LibraryChangeLog
  WHERE LibraryChangeLog.libraryId = Library.libraryId
  AND columnsChanged LIKE '%detailedQcStatusId%'
  ORDER BY changeTime DESC LIMIT 1
), creator)
WHERE detailedQcStatusId IS NOT NULL;

ALTER TABLE LibraryAliquot ADD COLUMN qcUser bigint(20);
ALTER TABLE LibraryAliquot ADD CONSTRAINT fk_libraryAliquot_qcUser FOREIGN KEY (qcUser) REFERENCES User (userId);
UPDATE LibraryAliquot SET qcUser = COALESCE((
  SELECT userId FROM LibraryAliquotChangeLog
  WHERE LibraryAliquotChangeLog.aliquotId = LibraryAliquot.aliquotId
  AND columnsChanged LIKE '%detailedQcStatusId%'
  ORDER BY changeTime DESC LIMIT 1
), creator)
WHERE detailedQcStatusId IS NOT NULL;

ALTER TABLE Run_Partition_LibraryAliquot ADD COLUMN qcUser bigint(20);
ALTER TABLE Run_Partition_LibraryAliquot ADD CONSTRAINT fk_runPartitionLibraryAliquot_qcUser FOREIGN KEY (qcUser) REFERENCES User (userId);
SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
UPDATE Run_Partition_LibraryAliquot rla
JOIN _Partition part ON part.partitionId = rla.partitionId
JOIN SequencerPartitionContainer spc ON spc.containerId = part.containerId
JOIN LibraryAliquot ali ON ali.aliquotId = rla.aliquotId
SET rla.qcUser = COALESCE((
  SELECT userId FROM RunChangeLog
  WHERE RunChangeLog.runId = rla.runId
  AND columnsChanged LIKE '%aliquot qcPassed%'
  AND message LIKE CONCAT(spc.identificationBarcode, '-', part.partitionNumber, '-', ali.alias, '%')
  ORDER BY changeTime DESC LIMIT 1
), @admin) WHERE qcPassed IS NOT NULL;
