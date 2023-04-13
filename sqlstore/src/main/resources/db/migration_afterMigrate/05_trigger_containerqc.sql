-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS ContainerQCInsert//
CREATE TRIGGER ContainerQCInsert AFTER INSERT ON ContainerQC
FOR EACH ROW
  INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message, changeTime) 
    SELECT 
      NEW.containerId,
      'qc',
      lastModifier, 
      CONCAT('QC added: ', (SELECT name FROM QCType WHERE qcTypeId = NEW.type)),
      lastModified
    FROM SequencerPartitionContainer WHERE containerId = NEW.containerId;
 //

DROP TRIGGER IF EXISTS ContainerQcUpdate//
CREATE TRIGGER ContainerQcUpdate BEFORE UPDATE ON ContainerQC
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN NEW.results <> OLD.results 
        THEN CONCAT('Updated ', (SELECT name FROM QCType WHERE qcTypeId = NEW.type), ' QC: ', OLD.results, ' → ', NEW.results, (SELECT units FROM QCType WHERE qcTypeId = NEW.type)) END);
      IF log_message IS NOT NULL AND log_message <> '' THEN
        INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message, changeTime) 
        SELECT 
          NEW.containerId,
          'QC',
          lastModifier,
          log_message,
          lastModified
        FROM SequencerPartitionContainer WHERE containerId = NEW.containerId;
      END IF;
  END//

DELIMITER ;
-- EndNoTest
