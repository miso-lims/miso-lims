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
    makeChangeMessage('title', OLD.title, NEW.title),
    makeChangeMessage('code', OLD.code, NEW.code),
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
        makeChangeColumn('title', OLD.title, NEW.title),
        makeChangeColumn('code', OLD.code, NEW.code),
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

DROP TRIGGER IF EXISTS ProjectDeliverableInsert//
CREATE TRIGGER ProjectDeliverableInsert AFTER INSERT ON Project_Deliverable
FOR EACH ROW
  INSERT INTO ProjectChangeLog(projectId, columnsChanged, userId, message, changeTime)
  SELECT
    NEW.projectId,
    'deliverables',
    lastModifier,
    CONCAT('Added deliverable: ', (SELECT name FROM Deliverable WHERE deliverableId = NEW.deliverableId)),
    lastModified
  FROM Project p
  WHERE p.projectId = NEW.projectId//

DROP TRIGGER IF EXISTS ProjectDeliverableDelete//
CREATE TRIGGER ProjectDeliverableDelete AFTER DELETE ON Project_Deliverable
FOR EACH ROW
  INSERT INTO ProjectChangeLog(projectId, columnsChanged, userId, message, changeTime)
  SELECT
    OLD.projectId,
    'deliverables',
    lastModifier,
    CONCAT('Removed deliverable: ', (SELECT name FROM Deliverable WHERE deliverableId = OLD.deliverableId)),
    lastModified
  FROM Project
  WHERE projectId = OLD.projectId//

DROP TRIGGER IF EXISTS ProjectContactInsert//
CREATE TRIGGER ProjectContactInsert AFTER INSERT ON Project_Contact
FOR EACH ROW
  INSERT INTO ProjectChangeLog(projectId, columnsChanged, userId, message, changeTime)
  SELECT
    NEW.projectId,
    'contacts',
    lastModifier,
    CONCAT('Added ', (SELECT name FROM ContactRole WHERE contactRoleId = NEW.contactRoleId), ' contact: ', (SELECT name FROM Contact WHERE contactId = NEW.contactId)),
    lastModified
  FROM Project p
  WHERE p.projectId = NEW.projectId//

DROP TRIGGER IF EXISTS ProjectContactDelete//
CREATE TRIGGER ProjectContactDelete AFTER DELETE ON Project_Contact
FOR EACH ROW
  INSERT INTO ProjectChangeLog(projectId, columnsChanged, userId, message, changeTime)
  SELECT
    OLD.projectId,
    'contacts',
    lastModifier,
    CONCAT('Removed ', (SELECT name FROM ContactRole WHERE contactRoleId = OLD.contactRoleId), ' contact: ', (SELECT name FROM Contact WHERE contactId = OLD.contactId)),
    lastModified
  FROM Project
  WHERE projectId = OLD.projectId//

DELIMITER ;
