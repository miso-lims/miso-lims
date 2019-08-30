DROP TRIGGER IF EXISTS PoolOrderInsert;
CREATE TRIGGER PoolOrderInsert AFTER INSERT ON PoolOrder
FOR EACH ROW
  INSERT INTO PoolOrderChangeLog(poolOrderId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.poolOrderId,
    '',
    NEW.updatedBy,
    'Pool created.',
    NEW.lastUpdated);