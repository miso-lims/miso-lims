-- pool_order_changelog
CREATE TABLE PoolOrderChangeLog (
  poolOrderChangeLogId bigint PRIMARY KEY AUTO_INCREMENT,
  poolOrderId bigint NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint NOT NULL,
  message longtext NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_poolOrderChangeLog_pool FOREIGN KEY (poolOrderId) REFERENCES PoolOrder(poolOrderId),
  CONSTRAINT fk_poolOrderChangeLog_user FOREIGN KEY (userId) REFERENCES User(userId)
) Engine=InnoDB DEFAULT CHARSET=utf8mb4;


-- pool_order_create_trigger
-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS BeforeInsertPoolOrder//

DROP TRIGGER IF EXISTS PoolOrderChange//
CREATE TRIGGER PoolOrderChange BEFORE UPDATE ON PoolOrder
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8mb4;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN CONCAT('description: ', COALESCE(OLD.description, 'n/a'), ' → ', COALESCE(NEW.description, 'n/a')) END,
        CASE WHEN (NEW.partitions IS NULL) <> (OLD.partitions IS NULL) OR NEW.partitions <> OLD.partitions THEN CONCAT('partitions: ', COALESCE(OLD.partitions, 'n/a'), ' → ', COALESCE(NEW.partitions, 'n/a')) END,
        CASE WHEN NEW.draft <> OLD.draft THEN CONCAT('draft: ', IF(OLD.draft = 0, 'No', 'Yes'), ' → ', IF(NEW.draft = 0, 'No', 'Yes')) END,
        CASE WHEN NEW.createdBy <> OLD.createdBy THEN CONCAT('created by: ', COALESCE(OLD.createdBy, 'n/a'), ' → ', COALESCE(NEW.createdBy, 'n/a')) END,
        CASE WHEN NEW.creationDate <> OLD.creationDate THEN CONCAT('creation date: ', COALESCE(OLD.creationDate, 'n/a'), ' → ', COALESCE(NEW.creationDate, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO PoolOrderChangeLog(poolOrderId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.poolOrderId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.alias <> OLD.alias THEN 'alias' END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN 'description' END,
        CASE WHEN (NEW.partitions IS NULL) <> (OLD.partitions IS NULL) OR NEW.partitions <> OLD.partitions THEN 'partitions' END,
        CASE WHEN NEW.draft <> OLD.draft THEN 'draft' END,
        CASE WHEN NEW.createdBy <> OLD.createdBy THEN 'createdBy' END,
        CASE WHEN NEW.creationDate <> OLD.creationDate THEN 'creationDate' END), ''),
      NEW.updatedBy,
      log_message,
      NEW.lastUpdated);
  END IF;
  END//

DROP TRIGGER IF EXISTS PoolOrderInsert//
CREATE TRIGGER PoolOrderInsert AFTER INSERT ON PoolOrder
FOR EACH ROW
  INSERT INTO PoolOrderChangeLog(poolOrderId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.poolOrderId,
    '',
    NEW.updatedBy,
    'Pool order created.',
    NEW.lastUpdated)//

DELIMITER ;
-- EndNoTest

-- sequencing_parameters_paired
ALTER TABLE SequencingParameters ADD COLUMN readLength2 int;
UPDATE SequencingParameters SET readLength2 = paired * readLength;
ALTER TABLE SequencingParameters MODIFY readLength2 int NOT NULL;
ALTER TABLE SequencingParameters DROP COLUMN paired;

