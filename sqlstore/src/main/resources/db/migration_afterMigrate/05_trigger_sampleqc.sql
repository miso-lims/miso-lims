-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS SampleQCInsert//
CREATE TRIGGER SampleQCInsert AFTER INSERT ON SampleQC
FOR EACH ROW
  INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
    NEW.sample_sampleId,
    'qc',
    (SELECT lastModifier FROM Sample WHERE sampleId = NEW.sample_sampleId),
    CONCAT('QC added: ', (SELECT name FROM QCType WHERE qcTypeId = NEW.qcMethod))
  )//

DROP TRIGGER IF EXISTS SampleQcUpdate//
CREATE TRIGGER SampleQcUpdate BEFORE UPDATE ON SampleQC
FOR EACH ROW
  BEGIN
    DECLARE log_message varchar(500) CHARACTER SET utf8;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN NEW.results <> OLD.results 
        THEN CONCAT('Updated ', (SELECT name FROM QCType WHERE qcTypeId = NEW.qcMethod), ' QC: ', OLD.results, ' → ', NEW.results, (SELECT units FROM QCType WHERE qcTypeId = NEW.qcMethod)) END);
      IF log_message IS NOT NULL AND log_message <> '' THEN
        INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
          NEW.sample_sampleId,
          'QC',
          (SELECT lastModifier FROM Sample WHERE sampleId = NEW.sample_sampleId),
          log_message);
      END IF;
  END//

DELIMITER ;
-- EndNoTest
