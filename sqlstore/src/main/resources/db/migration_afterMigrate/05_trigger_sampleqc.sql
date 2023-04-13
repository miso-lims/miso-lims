-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS SampleQCInsert//
CREATE TRIGGER SampleQCInsert AFTER INSERT ON SampleQC
FOR EACH ROW
  INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime) 
    SELECT 
      NEW.sample_sampleId,
      'qc',
      lastModifier, 
      CONCAT('QC added: ', (SELECT name FROM QCType WHERE qcTypeId = NEW.type)),
      lastModified
    FROM Sample WHERE sampleId = NEW.sample_sampleId;
 //

DROP TRIGGER IF EXISTS SampleQcUpdate//
CREATE TRIGGER SampleQcUpdate BEFORE UPDATE ON SampleQC
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN NEW.results <> OLD.results 
        THEN CONCAT('Updated ', (SELECT name FROM QCType WHERE qcTypeId = NEW.type), ' QC: ', OLD.results, ' → ', NEW.results, (SELECT units FROM QCType WHERE qcTypeId = NEW.type)) END);
      IF log_message IS NOT NULL AND log_message <> '' THEN
        INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message, changeTime) 
        SELECT 
          NEW.sample_sampleId,
          'QC',
          lastModifier,
          log_message,
          lastModified
        FROM Sample WHERE sampleId = NEW.sample_sampleId;
      END IF;
  END//

DELIMITER ;
-- EndNoTest
