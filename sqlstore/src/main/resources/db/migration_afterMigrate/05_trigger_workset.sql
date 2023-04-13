DELIMITER //

DROP TRIGGER IF EXISTS WorksetChange//
CREATE TRIGGER WorksetChange BEFORE UPDATE ON Workset
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      makeChangeMessage('alias', OLD.alias, NEW.alias),
      makeChangeMessage('description', OLD.description, NEW.description)
    );
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO WorksetChangeLog(worksetId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.worksetId,
        COALESCE(CONCAT_WS(',',
          makeChangeColumn('alias', OLD.alias, NEW.alias),
          makeChangeColumn('description', OLD.description, NEW.description)
        ), ''),
        NEW.lastModifier,
        log_message,
        NEW.lastModified
      );
    END IF;
  END//

DROP TRIGGER IF EXISTS WorksetInsert//
CREATE TRIGGER WorksetInsert AFTER INSERT ON Workset
FOR EACH ROW
  INSERT INTO WorksetChangeLog(worksetId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.worksetId,
    '',
    NEW.lastModifier,
    'Workset created.',
    NEW.lastModified)//

DELIMITER ;
