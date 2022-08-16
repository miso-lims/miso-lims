-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS SequencerPartitionContainerChange//
CREATE TRIGGER SequencerPartitionContainerChange BEFORE UPDATE ON SequencerPartitionContainer
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification barcode: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
        CASE WHEN NEW.sequencingContainerModelId <> OLD.sequencingContainerModelId THEN CONCAT('model: ', (SELECT alias FROM SequencingContainerModel WHERE sequencingContainerModelId = OLD.sequencingContainerModelId), ' → ', (SELECT alias FROM SequencingContainerModel WHERE sequencingContainerModelId = NEW.sequencingContainerModelId)) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.containerId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.identificationBarcode IS NULL) <> (OLD.identificationBarcode IS NULL) OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
        CASE WHEN NEW.sequencingContainerModelId <> OLD.sequencingContainerModelId THEN 'model' END), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified
    );
  END IF;
END//

DROP TRIGGER IF EXISTS SequencerPartitionContainerInsert//
CREATE TRIGGER SequencerPartitionContainerInsert AFTER INSERT ON SequencerPartitionContainer
FOR EACH ROW
  INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.containerId,
    '',
    NEW.lastModifier,
    'Container created.',
    NEW.lastModified)//

DROP TRIGGER IF EXISTS OxfordNanoporeContainerChange//
CREATE TRIGGER OxfordNanoporeContainerChange BEFORE UPDATE ON OxfordNanoporeContainer
FOR EACH ROW
BEGIN
  DECLARE log_message longtext CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.poreVersionId IS NULL) <> (OLD.poreVersionId IS NULL) OR NEW.poreVersionId <> OLD.poreVersionId THEN CONCAT('pore version: ', COALESCE((SELECT alias FROM PoreVersion WHERE poreVersionId = OLD.poreVersionId), 'n/a'), ' → ', COALESCE((SELECT alias FROM PoreVersion WHERE poreVersionId = NEW.poreVersionId), 'n/a')) END,
        CASE WHEN NEW.receivedDate <> OLD.receivedDate THEN CONCAT('received: ', OLD.receivedDate, ' → ', NEW.receivedDate) END,
        CASE WHEN (NEW.returnedDate IS NULL) <> (OLD.returnedDate IS NULL) OR NEW.returnedDate <> OLD.returnedDate THEN CONCAT('returned: ', COALESCE(OLD.returnedDate, 'n/a'), ' → ', COALESCE(NEW.returnedDate, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO SequencerPartitionContainerChangeLog(containerId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.containerId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.poreVersionId IS NULL) <> (OLD.poreVersionId IS NULL) OR NEW.poreVersionId <> OLD.poreVersionId THEN 'poreVersionId' END,
        CASE WHEN NEW.receivedDate <> OLD.receivedDate THEN 'receivedDate' END,
        CASE WHEN (NEW.returnedDate IS NULL) <> (OLD.returnedDate IS NULL) OR NEW.returnedDate <> OLD.returnedDate THEN  'returnedDate' END), ''),
      (SELECT lastModifier FROM SequencerPartitionContainer WHERE containerId = NEW.containerId),
      log_message,
      (SELECT lastModified FROM SequencerPartitionContainer WHERE containerId = NEW.containerId)
    );
  END IF;
END//

DELIMITER ;

-- EndNoTest
