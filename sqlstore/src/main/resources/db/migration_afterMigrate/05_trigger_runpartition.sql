DELIMITER //

DROP TRIGGER IF EXISTS RunPartitionUpdate//
CREATE TRIGGER RunPartitionUpdate BEFORE UPDATE ON Run_Partition
FOR EACH ROW
  BEGIN
	DECLARE log_message longtext;
	DECLARE log_columns varchar(500);
    SELECT
      spc.identificationBarcode,
      spc.containerId,
      part.partitionNumber
    INTO @container, @containerId, @partitionNumber
    FROM _Partition part
    JOIN SequencerPartitionContainer spc ON spc.containerId = part.containerId
    WHERE part.partitionId = NEW.partitionId;
    
    SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.partitionQcTypeId IS NULL) <> (OLD.partitionQcTypeId IS NULL) OR NEW.partitionQcTypeId <> OLD.partitionQcTypeId THEN CONCAT('QC for ', @container, '-', @partitionNumber, ': ', COALESCE((SELECT description FROM PartitionQCType WHERE partitionQcTypeId = OLD.partitionQcTypeId), 'n/a'), ' → ', COALESCE((SELECT description FROM PartitionQCType WHERE partitionQcTypeId = NEW.partitionQcTypeId), 'n/a')) END,
        CASE WHEN NEW.purposeId <> OLD.purposeId THEN CONCAT('Purpose for ', @container, '-', @partitionNumber, ': ', (SELECT alias FROM RunPurpose WHERE purposeId = OLD.purposeId), ' → ', (SELECT alias FROM RunPurpose WHERE purposeId = NEW.purposeId)) END);
    
    IF log_message IS NOT NULL AND log_message <> '' THEN
      SET log_columns = CONCAT_WS(', ',
        CASE WHEN (NEW.partitionQcTypeId IS NULL) <> (OLD.partitionQcTypeId IS NULL) OR NEW.partitionQcTypeId <> OLD.partitionQcTypeId THEN 'partition QC' END,
        CASE WHEN NEW.purposeId <> OLD.purposeId THEN 'partition purpose' END);

      INSERT INTO RunChangeLog (runId, columnsChanged, userId, message)
      VALUES (NEW.runId, log_columns, NEW.lastModifier, log_message);
    END IF;
  END//

DROP TRIGGER IF EXISTS RunPartitionLibraryAliquotInsert//
CREATE TRIGGER RunPartitionLibraryAliquotInsert AFTER INSERT ON Run_Partition_LibraryAliquot
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      makeChangeMessage('purpose', NULL, (SELECT alias FROM RunPurpose WHERE purposeId = NEW.purposeId)),
      makeChangeMessage('QC status', NULL, (SELECT description FROM RunLibraryQcStatus WHERE statusId = NEW.statusId)),
      makeChangeMessage('QC note', NULL, NEW.qcNote),
      makeChangeMessage('QC user', NULL, (SELECT fullName FROM User WHERE userId = NEW.qcUser)),
      makeChangeMessage('QC date', NULL, NEW.qcDate),
      makeChangeMessage('data review', NULL, dataReviewToString(NEW.dataReview)),
      makeChangeMessage('data reviewer', NULL, (SELECT fullName from User WHERE userId = NEW.dataReviewerId)),
      makeChangeMessage('data review date', NULL, NEW.dataReviewDate)
    );
    
    IF log_message IS NOT NULL AND log_message <> '' THEN
      SELECT spc.identificationBarcode, part.partitionNumber
      INTO @container, @partitionNumber
      FROM _Partition part
      JOIN SequencerPartitionContainer spc ON spc.containerId = part.containerId
      WHERE part.partitionId = NEW.partitionId;
      
      SELECT alias INTO @aliquot FROM LibraryAliquot WHERE aliquotId = NEW.aliquotId;
      
      INSERT INTO RunChangeLog(runId, columnsChanged, userId, message) VALUES (
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        makeChangeColumn('aliquot purposeId', NULL, NEW.purposeId),
        makeChangeColumn('aliquot statusId', NULL, NEW.statusId),
        makeChangeColumn('aliquot qcNote', NULL, NEW.qcNote),
        makeChangeColumn('aliquot qcUser', NULL, NEW.qcUser),
        makeChangeColumn('aliquot qcDate', NULL, NEW.qcDate),
        makeChangeColumn('aliquot dataReview', NULL, NEW.dataReview),
        makeChangeColumn('aliquot dataReviewerId', NULL, NEW.dataReviewerId),
        makeChangeColumn('aliquot dataReviewDate', NULL, NEW.dataReviewDate)
      ), ''),
      NEW.lastModifier,
      CONCAT(@container, '-', @partitionNumber, '-', @aliquot, ' ', log_message)
    );
    END IF;
  END//

DROP TRIGGER IF EXISTS RunPartitionLibraryAliquotUpdate//
CREATE TRIGGER RunPartitionLibraryAliquotUpdate BEFORE UPDATE ON Run_Partition_LibraryAliquot
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      makeChangeMessage('purpose', (SELECT alias FROM RunPurpose WHERE purposeId = OLD.purposeId), (SELECT alias FROM RunPurpose WHERE purposeId = NEW.purposeId)),
      makeChangeMessage('QC status', (SELECT description FROM RunLibraryQcStatus WHERE statusId = OLD.statusId), (SELECT description FROM RunLibraryQcStatus WHERE statusId = NEW.statusId)),
      makeChangeMessage('QC note', OLD.qcNote, NEW.qcNote),
      makeChangeMessage('QC user', (SELECT fullName FROM User WHERE userId = OLD.qcUser), (SELECT fullName FROM User WHERE userId = NEW.qcUser)),
      makeChangeMessage('QC date', OLD.qcDate, NEW.qcDate),
      makeChangeMessage('data review', dataReviewToString(OLD.dataReview), dataReviewToString(NEW.dataReview)),
      makeChangeMessage('data reviewer', (SELECT fullName from User WHERE userId = OLD.dataReviewerId), (SELECT fullName from User WHERE userId = NEW.dataReviewerId)),
      makeChangeMessage('data review date', OLD.dataReviewDate, NEW.dataReviewDate)
    );
    
    IF log_message IS NOT NULL AND log_message <> '' THEN
      SELECT spc.identificationBarcode, part.partitionNumber
      INTO @container, @partitionNumber
      FROM _Partition part
      JOIN SequencerPartitionContainer spc ON spc.containerId = part.containerId
      WHERE part.partitionId = NEW.partitionId;
      
      SELECT alias INTO @aliquot FROM LibraryAliquot WHERE aliquotId = NEW.aliquotId;
      
      INSERT INTO RunChangeLog(runId, columnsChanged, userId, message) VALUES (
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        makeChangeColumn('aliquot purposeId', OLD.purposeId, NEW.purposeId),
        makeChangeColumn('aliquot statusId', OLD.statusId, NEW.statusId),
        makeChangeColumn('aliquot qcNote', OLD.qcNote, NEW.qcNote),
        makeChangeColumn('aliquot qcUser', OLD.qcUser, NEW.qcUser),
        makeChangeColumn('aliquot qcDate', OLD.qcDate, NEW.qcDate),
        makeChangeColumn('aliquot dataReview', OLD.dataReview, NEW.dataReview),
        makeChangeColumn('aliquot dataReviewerId', OLD.dataReviewerId, NEW.dataReviewerId),
        makeChangeColumn('aliquot dataReviewDate', OLD.dataReviewDate, NEW.dataReviewDate)
      ), ''),
      NEW.lastModifier,
      CONCAT(@container, '-', @partitionNumber, '-', @aliquot, ' ', log_message)
    );
    END IF;
  END//

DELIMITER ;
