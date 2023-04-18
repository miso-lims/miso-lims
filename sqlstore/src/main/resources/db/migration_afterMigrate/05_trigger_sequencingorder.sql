DELIMITER //

DROP TRIGGER IF EXISTS SequencingOrderInsert//
CREATE TRIGGER SequencingOrderInsert AFTER INSERT ON SequencingOrder
FOR EACH ROW
  INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
    NEW.poolId,
    '',
    NEW.updatedBy,
    CONCAT(
      'New order: ',
      NEW.partitions,
      ' of ',
      COALESCE((SELECT CONCAT(InstrumentModel.alias, ' ', SequencingParameters.name) FROM SequencingParameters JOIN InstrumentModel ON SequencingParameters.instrumentModelId = InstrumentModel.instrumentModelId WHERE SequencingParameters.parametersId = NEW.parametersId), 'n/a')))//

DROP TRIGGER IF EXISTS SequencingOrderDelete//
CREATE TRIGGER SequencingOrderDelete AFTER DELETE ON SequencingOrder
FOR EACH ROW
  INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
    OLD.poolId,
    '',
    (SELECT lastModifier FROM Pool WHERE poolId = OLD.poolId),
    CONCAT(
      'Removed order: ',
      OLD.partitions,
      ' of ',
      COALESCE((SELECT CONCAT(InstrumentModel.alias, ' ', SequencingParameters.name) FROM SequencingParameters JOIN InstrumentModel ON SequencingParameters.instrumentModelId = InstrumentModel.instrumentModelId WHERE SequencingParameters.parametersId = OLD.parametersId), 'n/a')))//

DROP TRIGGER IF EXISTS SequencingOrderChange//
CREATE TRIGGER SequencingOrderChange BEFORE UPDATE ON SequencingOrder
FOR EACH ROW
  BEGIN
  IF NEW.partitions <> OLD.partitions OR (NEW.parametersId IS NULL) <> (OLD.parametersId IS NULL) OR NEW.parametersId <> OLD.parametersId THEN
    INSERT INTO PoolChangeLog(poolId, columnsChanged, userId, message) VALUES (
      NEW.poolId,
      '',
      NEW.updatedBy,
      CONCAT(
        'Changed order: ',
        OLD.partitions,
        ' of ',
        COALESCE((SELECT CONCAT(InstrumentModel.alias, ' ', SequencingParameters.name) FROM SequencingParameters JOIN InstrumentModel ON SequencingParameters.instrumentModelId = InstrumentModel.instrumentModelId WHERE SequencingParameters.parametersId = OLD.parametersId), 'n/a'),
        ' → ',
        NEW.partitions,
        ' of ',
        COALESCE((SELECT CONCAT(InstrumentModel.alias, ' ', SequencingParameters.name) FROM SequencingParameters JOIN InstrumentModel ON SequencingParameters.instrumentModelId = InstrumentModel.instrumentModelId WHERE SequencingParameters.parametersId = NEW.parametersId), 'n/a')));
  END IF;
  END//

DELIMITER ;
