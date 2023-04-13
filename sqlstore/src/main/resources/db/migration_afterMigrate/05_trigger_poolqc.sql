-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS PoolQCInsert//
CREATE TRIGGER PoolQCInsert AFTER INSERT ON PoolQC
FOR EACH ROW
  INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.pool_poolId,
      'qc',
      lastModifier,
      CONCAT('QC added: ', (SELECT name FROM QCType WHERE qcTypeId = NEW.type)),
      lastModified
    FROM Pool WHERE poolId = NEW.pool_poolId;
//

DROP TRIGGER IF EXISTS PoolQcUpdate//
CREATE TRIGGER PoolQcUpdate BEFORE UPDATE ON PoolQC
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      CASE WHEN NEW.results <> OLD.results 
        THEN CONCAT('Updated ', (SELECT name FROM QCType WHERE qcTypeId = NEW.type), ' QC: ', OLD.results, ' → ', NEW.results, (SELECT units FROM QCType WHERE qcTypeId = NEW.type)) END);
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message, changeTime) 
      SELECT
        NEW.pool_poolId,
        'QC',
        lastModifier,
        log_message,
        lastModified
      FROM Pool WHERE poolId = NEW.pool_poolId;
    END IF;
  END//

DELIMITER;
-- EndNoTest