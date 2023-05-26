DELIMITER //

DROP TRIGGER IF EXISTS PoolOrderChange//
CREATE TRIGGER PoolOrderChange BEFORE UPDATE ON PoolOrder
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext;
  SET log_message = CONCAT_WS(', ',
    makeChangeMessage('alias', OLD.alias, NEW.alias),
    makeChangeMessage('description', OLD.description, NEW.description),
    makeChangeMessage('purpose', (SELECT alias FROM RunPurpose WHERE purposeId = OLD.purposeId), (SELECT alias FROM RunPurpose WHERE purposeId = NEW.purposeId)),
    makeChangeMessage('container model', (SELECT alias FROM SequencingContainerModel WHERE sequencingContainerModelId = OLD.sequencingContainerModelId), (SELECT alias FROM SequencingContainerModel WHERE sequencingContainerModelId = NEW.sequencingContainerModelId)),
    makeChangeMessage('parameters', (SELECT name FROM SequencingParameters WHERE parametersId = OLD.parametersId), (SELECT name FROM SequencingParameters WHERE parametersId = NEW.parametersId)),
    makeChangeMessage('partitions', OLD.partitions, NEW.partitions),
    makeChangeMessage('draft', booleanToString(OLD.draft), booleanToString(NEW.draft)),
    makeChangeMessage('pool', (SELECT alias FROM Pool WHERE poolId = OLD.poolId), (SELECT alias FROM Pool WHERE poolId = NEW.poolId)),
    makeChangeMessage('sequencing order', IF(OLD.sequencingOrderId IS NULL, NULL, 'linked'), IF(NEW.sequencingOrderId IS NULL, NULL, 'linked'))
  );
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO PoolOrderChangeLog(poolOrderId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.poolOrderId,
      COALESCE(CONCAT_WS(',',
        makeChangeColumn('alias', OLD.alias, NEW.alias),
        makeChangeColumn('description', OLD.description, NEW.description),
        makeChangeColumn('purposeId', OLD.purposeId, NEW.purposeId),
        makeChangeColumn('sequencingContainerModelId', OLD.sequencingContainerModelId, NEW.sequencingContainerModelId),
        makeChangeColumn('parametersId', OLD.parametersId, NEW.parametersId),
        makeChangeColumn('partitions', OLD.partitions, NEW.partitions),
        makeChangeColumn('draft', OLD.draft, NEW.draft),
        makeChangeColumn('poolId', OLD.poolId, NEW.poolId),
        makeChangeColumn('sequencingOrderId', OLD.sequencingOrderId, NEW.sequencingOrderId)
      ), ''),
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
