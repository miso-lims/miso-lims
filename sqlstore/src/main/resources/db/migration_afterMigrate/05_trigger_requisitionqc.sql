-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS RequisitionQcInsert//
CREATE TRIGGER RequisitionQcInsert AFTER INSERT ON RequisitionQc
FOR EACH ROW
  INSERT INTO RequisitionChangeLog(requisitionId, columnsChanged, userId, message, changeTime) 
    SELECT 
      NEW.requisitionId,
      'qc',
      lastModifier, 
      CONCAT('QC added: ', (SELECT name FROM QCType WHERE qcTypeId = NEW.type)),
      lastModified
    FROM Requisition WHERE requisitionId = NEW.requisitionId;
 //

DROP TRIGGER IF EXISTS RequisitionQcUpdate//
CREATE TRIGGER RequisitionQcUpdate BEFORE UPDATE ON RequisitionQc
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN NEW.results <> OLD.results 
        THEN CONCAT('Updated ', (SELECT name FROM QCType WHERE qcTypeId = NEW.type), ' QC: ', OLD.results, ' → ', NEW.results, (SELECT units FROM QCType WHERE qcTypeId = NEW.type)) END);
      IF log_message IS NOT NULL AND log_message <> '' THEN
        INSERT INTO RequisitionChangeLog(requisitionId, columnsChanged, userId, message, changeTime) 
        SELECT 
          NEW.requisitionId,
          'QC',
          lastModifier,
          log_message,
          lastModified
        FROM Requisition WHERE requisitionId = NEW.requisitionId;
      END IF;
  END//

DELIMITER ;
-- EndNoTest
