DELIMITER //

DROP TRIGGER IF EXISTS TransferChange//
CREATE TRIGGER TransferChange BEFORE UPDATE ON Transfer
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      makeChangeMessage('transfer request name', OLD.transferRequestName, NEW.transferRequestName),
      makeChangeMessage('transfer time', OLD.transferTime, NEW.transferTime),
      makeChangeMessage('sender lab', (SELECT alias FROM Lab WHERE labId = OLD.senderLabId), (SELECT alias FROM Lab WHERE labId = NEW.senderLabId)),
      makeChangeMessage('sender group', (SELECT name FROM _Group WHERE groupId = OLD.senderGroupId), (SELECT name FROM _Group WHERE groupId = NEW.senderGroupId)),
      makeChangeMessage('recipient group', (SELECT name FROM _Group WHERE groupId = OLD.recipientGroupId), (SELECT name FROM _Group WHERE groupId = NEW.recipientGroupId)),
      makeChangeMessage('recipient', OLD.recipient, NEW.recipient)
    );
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO TransferChangeLog(transferId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.transferId,
        COALESCE(CONCAT_WS(',',
          makeChangeColumn('transferRequestName', OLD.transferRequestName, NEW.transferRequestName),
          makeChangeColumn('transferTime', OLD.transferTime, NEW.transferTime),
          makeChangeColumn('senderLabId', OLD.senderLabId, NEW.senderLabId),
          makeChangeColumn('senderGroupId', OLD.senderGroupId, NEW.senderGroupId),
          makeChangeColumn('recipientGroupId', OLD.recipientGroupId, NEW.recipientGroupId),
          makeChangeColumn('recipient', OLD.recipient, NEW.recipient)
        ), ''),
        NEW.lastModifier,
        log_message,
        NEW.lastModified
      );
    END IF;
  END//

DROP TRIGGER IF EXISTS TransferInsert//
CREATE TRIGGER TransferInsert AFTER INSERT ON Transfer
FOR EACH ROW
  INSERT INTO TransferChangeLog(transferId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.transferId,
    '',
    NEW.lastModifier,
    'Transfer created.',
    NEW.lastModified)//

DELIMITER ;
