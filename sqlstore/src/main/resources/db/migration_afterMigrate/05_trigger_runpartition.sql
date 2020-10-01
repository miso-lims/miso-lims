-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS RunPartitionUpdate//
CREATE TRIGGER RunPartitionUpdate BEFORE UPDATE ON Run_Partition
FOR EACH ROW
  BEGIN
	DECLARE log_message longtext CHARACTER SET utf8;
	DECLARE log_columns varchar(500) CHARACTER SET utf8;
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
    DECLARE log_message longtext CHARACTER SET utf8;
    SET log_message = CONCAT_WS(', ',
      makeChangeMessage('purpose', NULL, (SELECT alias FROM RunPurpose WHERE purposeId = NEW.purposeId)),
      makeChangeMessage('QC status', NULL, CASE NEW.qcPassed WHEN TRUE THEN "Passed" WHEN FALSE THEN "Failed" ELSE NULL END),
      makeChangeMessage('QC note', NULL, NEW.qcNote)
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
        makeChangeColumn('aliquot purpose', NULL, NEW.purposeId),
        makeChangeColumn('aliquot qcPassed', NULL, NEW.qcPassed),
        makeChangeColumn('aliquot qcNote', NULL, NEW.qcNote)
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
    DECLARE log_message longtext CHARACTER SET utf8;
    SET log_message = CONCAT_WS(', ',
      makeChangeMessage('purpose', (SELECT alias FROM RunPurpose WHERE purposeId = OLD.purposeId), (SELECT alias FROM RunPurpose WHERE purposeId = NEW.purposeId)),
      makeChangeMessage('QC status', CASE OLD.qcPassed WHEN TRUE THEN "Passed" WHEN FALSE THEN "Failed" ELSE NULL END, CASE NEW.qcPassed WHEN TRUE THEN "Passed" WHEN FALSE THEN "Failed" ELSE NULL END),
      makeChangeMessage('QC note', OLD.qcNote, NEW.qcNote)
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
        makeChangeColumn('aliquot purpose', OLD.purposeId, NEW.purposeId),
        makeChangeColumn('aliquot qcPassed', OLD.qcPassed, NEW.qcPassed),
        makeChangeColumn('aliquot qcNote', OLD.qcNote, NEW.qcNote)
      ), ''),
      NEW.lastModifier,
      CONCAT(@container, '-', @partitionNumber, '-', @aliquot, ' ', log_message)
    );
    END IF;
  END//

DELIMITER ;
-- EndNoTest
