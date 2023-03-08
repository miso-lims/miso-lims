DELIMITER //

DROP TRIGGER IF EXISTS RequisitionChange//
CREATE TRIGGER RequisitionChange BEFORE UPDATE ON Requisition
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext CHARACTER SET utf8;
    SET log_message = CONCAT_WS(', ',
      makeChangeMessage('alias', OLD.alias, NEW.alias),
      makeChangeMessage('assay', (SELECT alias FROM Assay WHERE assayId = OLD.assayId), (SELECT alias FROM Assay WHERE assayId = NEW.assayId)),
      makeChangeMessage('stopped', booleanToString(OLD.stopped), booleanToString(NEW.stopped)),
      makeChangeMessage('stop reason', OLD.stopReason, NEW.stopReason)
    );
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO RequisitionChangeLog(requisitionId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.requisitionId,
        COALESCE(CONCAT_WS(',',
          makeChangeColumn('alias', OLD.alias, NEW.alias),
          makeChangeColumn('assayId', OLD.assayId, NEW.assayId),
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

DELIMITER ;
