DELIMITER //

DROP TRIGGER IF EXISTS RequisitionChange//
CREATE TRIGGER RequisitionChange BEFORE UPDATE ON Requisition
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      makeChangeMessage('alias', OLD.alias, NEW.alias),
      makeChangeMessage('stopped', booleanToString(OLD.stopped), booleanToString(NEW.stopped)),
      makeChangeMessage('stop reason', OLD.stopReason, NEW.stopReason)
    );
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO RequisitionChangeLog(requisitionId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.requisitionId,
        COALESCE(CONCAT_WS(',',
          makeChangeColumn('alias', OLD.alias, NEW.alias),
          makeChangeColumn('stopped', OLD.stopped, NEW.stopped),
          makeChangeColumn('stopReason', OLD.stopReason, NEW.stopReason)
        ), ''),
        NEW.lastModifier,
        log_message,
        NEW.lastModified
      );
    END IF;
  END//

DROP TRIGGER IF EXISTS RequisitionInsert//
CREATE TRIGGER RequisitionInsert AFTER INSERT ON Requisition
FOR EACH ROW
  INSERT INTO RequisitionChangeLog(requisitionId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.requisitionId,
    '',
    NEW.lastModifier,
    'Requisition created.',
    NEW.lastModified)//

DROP TRIGGER IF EXISTS RequisitionAssayInsert//
CREATE TRIGGER RequisitionAssayInsert AFTER INSERT ON Requisition_Assay
FOR EACH ROW
  BEGIN
    IF TIMESTAMPDIFF(HOUR, (SELECT created FROM Requisition WHERE requisitionId = NEW.requisitionId), NOW()) > 0 THEN
      INSERT INTO RequisitionChangeLog(requisitionId, columnsChanged, userId, message, changeTime)
        SELECT
          NEW.requisitionId,
          'assays',
          lastModifier,
          CONCAT('Added assay ', (SELECT alias FROM Assay WHERE assayId = NEW.assayId), ' ', (SELECT version FROM Assay WHERE assayId = NEW.assayId)),
          lastModified
        FROM Requisition
        WHERE requisitionId = NEW.requisitionId;
    END IF;
  END//

DROP TRIGGER IF EXISTS RequisitionAssayDelete//
CREATE TRIGGER RequisitionAssayDelete AFTER DELETE ON Requisition_Assay
FOR EACH ROW
  INSERT INTO RequisitionChangeLog(requisitionId, columnsChanged, userId, message, changeTime)
    SELECT
      OLD.requisitionId,
      'assays',
      lastModifier,
      CONCAT('Removed assay ', (SELECT alias FROM Assay WHERE assayId = OLD.assayId), ' ', (SELECT version FROM Assay WHERE assayId = OLD.assayId)),
      lastModified
    FROM Requisition
    WHERE requisitionId = OLD.requisitionId//

DELIMITER ;
