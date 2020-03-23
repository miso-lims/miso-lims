DELIMITER //

DROP TRIGGER IF EXISTS TransferChange//
CREATE TRIGGER TransferChange BEFORE UPDATE ON Transfer
FOR EACH ROW
  BEGIN
    DECLARE log_message varchar(500) CHARACTER SET utf8;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN NEW.transferTime <> OLD.transferTime THEN CONCAT('transfer time: ', OLD.transferTime, ' → ', NEW.transferTime) END,
      CASE WHEN (NEW.senderLabId IS NULL) <> (OLD.senderLabId IS NULL) OR NEW.senderLabId <> OLD.senderLabId THEN CONCAT('sender lab: ', COALESCE((SELECT alias FROM Lab WHERE labId = OLD.senderLabId), 'n/a'), ' → ', COALESCE((SELECT alias FROM Lab WHERE labId = NEW.senderLabId), 'n/a')) END,
      CASE WHEN (NEW.senderGroupId IS NULL) <> (OLD.senderGroupId IS NULL) OR NEW.senderGroupId <> OLD.senderGroupId THEN CONCAT('sender group: ', COALESCE((SELECT name FROM _Group WHERE groupId = OLD.senderGroupId), 'n/a'), ' → ', COALESCE((SELECT name FROM _Group WHERE groupId = NEW.senderGroupId), 'n/a')) END,
      CASE WHEN (NEW.recipientGroupId IS NULL) <> (OLD.recipientGroupId IS NULL) OR NEW.recipientGroupId <> OLD.recipientGroupId THEN CONCAT('recipient group: ', COALESCE((SELECT name FROM _Group WHERE groupId = OLD.recipientGroupId), 'n/a'), ' → ', COALESCE((SELECT name FROM _Group WHERE groupId = NEW.recipientGroupId), 'n/a')) END,
      CASE WHEN (NEW.recipient IS NULL) <> (OLD.recipient IS NULL) OR NEW.recipient <> OLD.recipient THEN CONCAT('recipient: ', COALESCE(OLD.recipient, 'n/a'), ' → ', COALESCE(NEW.recipient, 'n/a')) END);
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO TransferChangeLog(transferId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.transferId,
        COALESCE(CONCAT_WS(',',
          CASE WHEN NEW.transferTime <> OLD.transferTime THEN 'transferTime' END,
          CASE WHEN (NEW.senderLabId IS NULL) <> (OLD.senderLabId IS NULL) OR NEW.senderLabId <> OLD.senderLabId THEN 'senderLabId' END,
          CASE WHEN (NEW.senderGroupId IS NULL) <> (OLD.senderGroupId IS NULL) OR NEW.senderGroupId <> OLD.senderGroupId THEN 'senderGroupId' END,
          CASE WHEN (NEW.recipientGroupId IS NULL) <> (OLD.recipientGroupId IS NULL) OR NEW.recipientGroupId <> OLD.recipientGroupId THEN 'recipientGroupId' END,
          CASE WHEN (NEW.recipient IS NULL) <> (OLD.recipient IS NULL) OR NEW.recipient <> OLD.recipient THEN 'senderLabId' END
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
    CONCAT('Transfer created.'),
    NEW.lastModified)//

DELIMITER ;
