-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS ProjectInsert//
CREATE TRIGGER ProjectInsert AFTER INSERT ON Project
FOR EACH ROW
  INSERT INTO ProjectChangeLog(projectId, columnsChanged, userId, message, changeTime)
  VALUES (NEW.projectId, '', NEW.lastModifier, 'Project created.', NEW.lastModified)//

DROP TRIGGER IF EXISTS ProjectChange//
CREATE TRIGGER ProjectChange BEFORE UPDATE ON Project
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN (NEW.shortName IS NULL) <> (OLD.shortName IS NULL) OR NEW.shortName <> OLD.shortName THEN CONCAT('short name: ', COALESCE(OLD.shortName, 'n/a'), ' → ', COALESCE(NEW.shortName, 'n/a')) END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN NEW.status <> OLD.status THEN CONCAT('status: ', OLD.status, ' → ', NEW.status) END,
        CASE WHEN NEW.referenceGenomeId <> OLD.referenceGenomeId THEN CONCAT('reference genome: ', (SELECT alias FROM ReferenceGenome WHERE referenceGenomeId = OLD.referenceGenomeId), ' → ', (SELECT alias FROM ReferenceGenome WHERE referenceGenomeId = NEW.referenceGenomeId)) END,
        CASE WHEN (NEW.targetedSequencingId IS NULL) <> (OLD.targetedSequencingId IS NULL) OR NEW.targetedSequencingId <> OLD.targetedSequencingId THEN CONCAT('targeted sequencing: ', COALESCE((SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = OLD.targetedSequencingId), 'n/a'), ' → ', COALESCE((SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = NEW.targetedSequencingId), 'n/a')) END,
        CASE WHEN NEW.clinical <> OLD.clinical THEN CONCAT('clinical: ', booleanToString(OLD.clinical), ' → ', booleanToString(NEW.clinical)) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO ProjectChangeLog(projectId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.projectId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
        CASE WHEN (NEW.shortName IS NULL) <> (OLD.shortName IS NULL) OR NEW.shortName <> OLD.shortName THEN 'short name' END,
        CASE WHEN (NEW.description IS NULL) <> (OLD.description IS NULL) OR NEW.description <> OLD.description THEN 'description' END,
        CASE WHEN NEW.status <> OLD.status THEN 'status' END,
        CASE WHEN NEW.referenceGenomeId <> OLD.referenceGenomeId THEN 'reference genome' END,
        CASE WHEN (NEW.targetedSequencingId IS NULL) <> (OLD.targetedSequencingId IS NULL) OR NEW.targetedSequencingId <> OLD.targetedSequencingId THEN 'targeted sequencing' END,
        CASE WHEN NEW.clinical <> OLD.clinical THEN 'clinical' END), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified);
  END IF;
END//

DELIMITER ;
-- EndNoTest
