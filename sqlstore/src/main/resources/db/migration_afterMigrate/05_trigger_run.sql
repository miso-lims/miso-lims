-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS RunChange//
CREATE TRIGGER RunChange BEFORE UPDATE ON Run
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias AND (OLD.alias NOT LIKE 'TEMPORARY%') THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN (NEW.completionDate IS NULL) <> (OLD.completionDate IS NULL) OR NEW.completionDate <> OLD.completionDate THEN CONCAT('completion: ', COALESCE(OLD.completionDate, 'n/a'), ' → ', COALESCE(NEW.completionDate, 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN (NEW.filePath IS NULL) <> (OLD.filePath IS NULL) OR NEW.filePath <> OLD.filePath THEN CONCAT('file path: ', COALESCE(OLD.filePath, 'n/a'), ' → ', COALESCE(NEW.filePath, 'n/a')) END,
        CASE WHEN NEW.health <> OLD.health THEN CONCAT('health: ', COALESCE(OLD.health, 'n/a'), ' → ', COALESCE(NEW.health, 'n/a')) END,
        CASE WHEN (NEW.startDate IS NULL) <> (OLD.startDate IS NULL) OR NEW.startDate <> OLD.startDate THEN CONCAT('startDate: ', COALESCE(OLD.startDate, 'n/a'), ' → ', COALESCE(NEW.startDate, 'n/a')) END,
        CASE WHEN (NEW.sequencingParameters_parametersId IS NULL) <> (OLD.sequencingParameters_parametersId IS NULL) OR NEW.sequencingParameters_parametersId <> OLD.sequencingParameters_parametersId THEN CONCAT('parameters: ', COALESCE((SELECT name FROM SequencingParameters WHERE parametersId = OLD.sequencingParameters_parametersId), 'n/a'), ' → ', COALESCE((SELECT name FROM SequencingParameters WHERE parametersId = NEW.sequencingParameters_parametersId), 'n/a')) END,
        CASE WHEN (NEW.sequencerReference_sequencerReferenceId IS NULL) <> (OLD.sequencerReference_sequencerReferenceId IS NULL) OR NEW.sequencerReference_sequencerReferenceId <> OLD.sequencerReference_sequencerReferenceId THEN CONCAT('sequencer: ', COALESCE((SELECT name FROM SequencerReference WHERE referenceId = OLD.sequencerReference_sequencerReferenceId), 'n/a'), ' → ', COALESCE((SELECT name FROM SequencerReference WHERE referenceId = NEW.sequencerReference_sequencerReferenceId), 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.accession IS NULL) <> (OLD.accession IS NULL) OR NEW.accession <> OLD.accession THEN 'accession' END,
        CASE WHEN (NEW.alias IS NULL) <> (OLD.alias IS NULL) OR NEW.alias <> OLD.alias THEN 'alias' END,
        CASE WHEN (NEW.completionDate IS NULL) <> (OLD.completionDate IS NULL) OR NEW.completionDate <> OLD.completionDate THEN 'completionDate' END,
        CASE WHEN NEW.description <> OLD.description THEN 'description' END,
        CASE WHEN (NEW.filePath IS NULL) <> (OLD.filePath IS NULL) OR NEW.filePath <> OLD.filePath THEN 'filePath' END,
        CASE WHEN (NEW.health IS NULL) <> (OLD.health IS NULL) OR NEW.health <> OLD.health THEN 'health' END,
        CASE WHEN (NEW.metrics IS NULL) <> (OLD.metrics IS NULL) OR NEW.metrics <> OLD.metrics THEN 'metrics' END,
        CASE WHEN (NEW.startDate IS NULL) <> (OLD.startDate IS NULL) OR NEW.startDate <> OLD.startDate THEN 'startDate' END,
        CASE WHEN (NEW.sequencingParameters_parametersId IS NULL) <> (OLD.sequencingParameters_parametersId IS NULL) OR NEW.sequencingParameters_parametersId <> OLD.sequencingParameters_parametersId THEN 'parameters' END,
        CASE WHEN (NEW.sequencerReference_sequencerReferenceId IS NULL) <> (OLD.sequencerReference_sequencerReferenceId IS NULL) OR NEW.sequencerReference_sequencerReferenceId <> OLD.sequencerReference_sequencerReferenceId THEN 'sequencerReference_sequencerReferenceId' END), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified);
  END IF;
  END//

DROP TRIGGER IF EXISTS RunChangePacBio//

DROP TRIGGER IF EXISTS RunChangeLS454//
CREATE TRIGGER RunChangeLS454 BEFORE UPDATE ON RunLS454
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN CONCAT('ends: ', CASE WHEN OLD.pairedEnd THEN 'paired' ELSE 'single' END, ' → ', CASE WHEN NEW.pairedEnd THEN 'paired' ELSE 'single' END) END,
        CASE WHEN (NEW.cycles IS NULL) <> (OLD.cycles IS NULL) OR NEW.cycles <> OLD.cycles THEN CONCAT('cycles: ', COALESCE(OLD.cycles, 'n/a'), ' → ', COALESCE(NEW.cycles, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN 'pairedend' END,
        CASE WHEN (NEW.cycles IS NULL) <> (OLD.cycles IS NULL) OR NEW.cycles <> OLD.cycles THEN 'cycles' END), ''),
      (SELECT lastModifier FROM Run WHERE Run.runId = NEW.runId),
      log_message,
      (SELECT lastModified FROM Run WHERE Run.runId = NEW.runId));
  END IF;
  END//
  
DROP TRIGGER IF EXISTS RunChangeSolid//
CREATE TRIGGER RunChangeSolid BEFORE UPDATE ON RunSolid
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN CONCAT('ends: ', CASE WHEN OLD.pairedEnd THEN 'paired' ELSE 'single' END, ' → ', CASE WHEN NEW.pairedEnd THEN 'paired' ELSE 'single' END) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN 'pairedend' END), ''),
      (SELECT lastModifier FROM Run WHERE Run.runId = NEW.runId),
      log_message,
      (SELECT lastModified FROM Run WHERE Run.runId = NEW.runId));
  END IF;
  END//

DROP TRIGGER IF EXISTS RunChangeIllumina//
CREATE TRIGGER RunChangeIllumina BEFORE UPDATE ON RunIllumina
FOR EACH ROW
  BEGIN
  DECLARE log_message varchar(500) CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN CONCAT('ends: ', CASE WHEN OLD.pairedEnd THEN 'paired' ELSE 'single' END, ' → ', CASE WHEN NEW.pairedEnd THEN 'paired' ELSE 'single' END) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN 'pairedend' END), ''),
      (SELECT lastModifier FROM Run WHERE Run.runId = NEW.runId),
      log_message,
      (SELECT lastModified FROM Run WHERE Run.runId = NEW.runId));
  END IF;
  END//
  
DROP TRIGGER IF EXISTS RunInsert//
CREATE TRIGGER RunInsert AFTER INSERT ON Run
FOR EACH ROW
  INSERT INTO RunChangeLog(runId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.runId,
    '',
    NEW.lastModifier,
    'Run created.',
    NEW.lastModified)//
DELIMITER ;
-- EndNoTest
