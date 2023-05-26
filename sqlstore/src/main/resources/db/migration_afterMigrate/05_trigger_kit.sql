DELIMITER //

DROP TRIGGER IF EXISTS KitDescriptorChange//
CREATE TRIGGER KitDescriptorChange BEFORE UPDATE ON KitDescriptor
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext;
  SET log_message = CONCAT_WS(', ',
    CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
    CASE WHEN (NEW.version IS NULL) <> (OLD.version IS NULL) OR NEW.version <> OLD.version THEN CONCAT('version: ', COALESCE(OLD.version, 'n/a'), ' → ', COALESCE(NEW.version, 'n/a')) END,
    CASE WHEN NEW.manufacturer <> OLD.manufacturer THEN CONCAT('manufacturer: ', OLD.manufacturer, ' → ', NEW.manufacturer) END,
    CASE WHEN NEW.partNumber <> OLD.partNumber THEN CONCAT('part number: ', OLD.partNumber, ' → ', NEW.partNumber) END,
    CASE WHEN NEW.stockLevel <> OLD.stockLevel THEN CONCAT('stock: ', OLD.stockLevel, ' → ', NEW.stockLevel) END,
    CASE WHEN NEW.kitType <> OLD.kitType THEN CONCAT('type: ', OLD.kitType, ' → ', NEW.kitType) END,
    CASE WHEN NEW.platformType <> OLD.platformType THEN CONCAT('platform: ', OLD.platformType, ' → ', NEW.platformType) END,
    CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN CONCAT('description: ', COALESCE(OLD.description, 'n/a'), ' → ', COALESCE(NEW.description, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO KitDescriptorChangeLog(kitDescriptorId, columnsChanged, userId, message) VALUES (
      NEW.kitDescriptorId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.name <> OLD.name THEN 'name' END,
        CASE WHEN (NEW.version IS NULL) <> (OLD.version IS NULL) OR NEW.version <> OLD.version THEN 'version' END,
        CASE WHEN NEW.manufacturer <> OLD.manufacturer THEN 'manufacturer' END,
        CASE WHEN NEW.partNumber <> OLD.partNumber THEN 'partNumber' END,
        CASE WHEN NEW.stockLevel <> OLD.stockLevel THEN 'stockLevel' END,
        CASE WHEN NEW.kitType <> OLD.kitType THEN 'kitType' END,
        CASE WHEN NEW.platformType <> OLD.platformType THEN 'platformType' END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN 'description' END), ''),
      NEW.lastModifier,
      log_message
    );
  END IF;
  END//

DROP TRIGGER IF EXISTS KitDescriptorInsert//
CREATE TRIGGER KitDescriptorInsert AFTER INSERT ON KitDescriptor
FOR EACH ROW
  INSERT INTO KitDescriptorChangeLog(kitDescriptorId, columnsChanged, userId, message) VALUES (
    NEW.kitDescriptorId,
    '',
    NEW.lastModifier,
    'Kit descriptor created.')//
    
DROP TRIGGER IF EXISTS TargetedSequencing_KitDescriptorInsert//
CREATE TRIGGER TargetedSequencing_KitDescriptorInsert AFTER INSERT ON TargetedSequencing_KitDescriptor
FOR EACH ROW
  INSERT INTO KitDescriptorChangeLog(kitDescriptorId, columnsChanged, userId, message) VALUES (
    NEW.kitDescriptorId,
    'targetedSequencingId',
    (SELECT kd.lastModifier FROM KitDescriptor kd WHERE kd.kitDescriptorId = NEW.kitDescriptorId),
    CONCAT('Added targeted sequencing: ', (SELECT ts.alias FROM TargetedSequencing ts WHERE ts.targetedSequencingId = NEW.targetedSequencingId)))//

DROP TRIGGER IF EXISTS TargetedSequencing_KitDescriptorDelete//
CREATE TRIGGER TargetedSequencing_KitDescriptorDelete AFTER DELETE ON TargetedSequencing_KitDescriptor
FOR EACH ROW
  INSERT INTO KitDescriptorChangeLog(kitDescriptorId, columnsChanged, userId, message) VALUES (
    OLD.kitDescriptorId,
    'targetedSequencingId',
    (SELECT kd.lastModifier FROM KitDescriptor kd WHERE kd.kitDescriptorId = OLD.kitDescriptorId),
    CONCAT('Removed targeted sequencing: ', (SELECT ts.alias FROM TargetedSequencing ts WHERE ts.targetedSequencingId = OLD.targetedSequencingId)))//

DELIMITER ;
