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
  DECLARE log_message longtext;
  SET log_message = CONCAT_WS(', ',
    makeChangeMessage('alias', OLD.alias, NEW.alias),
    makeChangeMessage('short name', OLD.shortName, NEW.shortName),
    makeChangeMessage('description', OLD.description, NEW.description),
    makeChangeMessage('status', OLD.status, NEW.status),
    makeChangeMessage('reference genome', (SELECT alias FROM ReferenceGenome WHERE referenceGenomeId = OLD.referenceGenomeId), (SELECT alias FROM ReferenceGenome WHERE referenceGenomeId = NEW.referenceGenomeId)),
    makeChangeMessage('targeted sequencing', (SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = OLD.targetedSequencingId), (SELECT alias FROM TargetedSequencing WHERE targetedSequencingId = NEW.targetedSequencingId)),
    makeChangeMessage('pipeline', (SELECT alias FROM Pipeline WHERE pipelineId = OLD.pipelineId), (SELECT alias FROM Pipeline WHERE pipelineId = NEW.pipelineId))
  );
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO ProjectChangeLog(projectId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.projectId,
      COALESCE(CONCAT_WS(',',
        makeChangeColumn('alias', OLD.alias, NEW.alias),
        makeChangeColumn('short name', OLD.shortName, NEW.shortName),
        makeChangeColumn('description', OLD.description, NEW.description),
        makeChangeColumn('status', OLD.status, NEW.status),
        makeChangeColumn('reference genome', OLD.referenceGenomeId, NEW.referenceGenomeId),
        makeChangeColumn('targeted sequencing', OLD.targetedSequencingId, NEW.targetedSequencingId),
        makeChangeColumn('pipeline', OLD.pipelineId, NEW.pipelineId)
      ), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified);
  END IF;
END//

DELIMITER ;
-- EndNoTest
