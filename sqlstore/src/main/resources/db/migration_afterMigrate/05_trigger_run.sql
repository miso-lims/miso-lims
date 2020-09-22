-- StartNoTest
DELIMITER //

DROP TRIGGER IF EXISTS RunChange//
CREATE TRIGGER RunChange BEFORE UPDATE ON Run
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
    makeChangeMessage('accession', OLD.accession, NEW.accession),
    makeChangeMessage('alias', OLD.alias, NEW.alias),
    makeChangeMessage('completion', OLD.completionDate, NEW.completionDate),
    makeChangeMessage('description', OLD.description, NEW.description),
    makeChangeMessage('file path', OLD.filePath, NEW.filePath),
    makeChangeMessage('health', OLD.health, NEW.health),
    makeChangeMessage('startDate', OLD.startDate, NEW.startDate),
    makeChangeMessage('parameters', (SELECT name FROM SequencingParameters WHERE parametersId = OLD.sequencingParameters_parametersId), (SELECT name FROM SequencingParameters WHERE parametersId = NEW.sequencingParameters_parametersId)),
    makeChangeMessage('sequencer', (SELECT name FROM Instrument WHERE instrumentId = OLD.instrumentId), (SELECT name FROM Instrument WHERE instrumentId = NEW.instrumentId)),
    makeChangeMessage('data approved', booleanToString(OLD.dataApproved), booleanToString(NEW.dataApproved)),
    makeChangeMessage('data approver', (SELECT fullName FROM User WHERE userId = OLD.dataApproverId), (SELECT fullName FROM User WHERE userId = NEW.dataApproverId)),
    makeChangeMessage('SOP', (SELECT CONCAT(alias, ' (', version, ')') FROM Sop WHERE sopId = OLD.sopId), (SELECT CONCAT(alias, ' (', version, ')') FROM Sop WHERE sopId = NEW.sopId))
  );
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        makeChangeColumn('accession', OLD.accession, NEW.accession),
        makeChangeColumn('alias', OLD.alias, NEW.alias),
        makeChangeColumn('completionDate', OLD.completionDate, NEW.completionDate),
        makeChangeColumn('description', OLD.description, NEW.description),
        makeChangeColumn('filePath', OLD.filePath, NEW.filePath),
        makeChangeColumn('health', OLD.health, NEW.health),
        CASE WHEN (NEW.metrics IS NULL) <> (OLD.metrics IS NULL) OR NEW.metrics <> OLD.metrics THEN 'metrics' END,
        makeChangeColumn('startDate', OLD.startDate, NEW.startDate),
        makeChangeColumn('sequencingParameters_parametersId', OLD.sequencingParameters_parametersId, NEW.sequencingParameters_parametersId),
        makeChangeColumn('instrumentId', OLD.instrumentId, NEW.instrumentId),
        makeChangeColumn('dataApproved', OLD.dataApproved, NEW.dataApproved),
        makeChangeColumn('dataApproverId', OLD.dataApproverId, NEW.dataApproverId),
        makeChangeColumn('sopId', OLD.sopId, NEW.sopId)
      ), ''),
      NEW.lastModifier,
      log_message,
      NEW.lastModified);
  END IF;
  END//

DROP TRIGGER IF EXISTS RunChangeLS454//
CREATE TRIGGER RunChangeLS454 BEFORE UPDATE ON RunLS454
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN CONCAT('ends: ', CASE WHEN OLD.pairedEnd THEN 'paired' ELSE 'single' END, ' → ', CASE WHEN NEW.pairedEnd THEN 'paired' ELSE 'single' END) END,
        CASE WHEN (NEW.cycles IS NULL) <> (OLD.cycles IS NULL) OR NEW.cycles <> OLD.cycles THEN CONCAT('cycles: ', COALESCE(OLD.cycles, 'n/a'), ' → ', COALESCE(NEW.cycles, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN 'pairedend' END,
        CASE WHEN (NEW.cycles IS NULL) <> (OLD.cycles IS NULL) OR NEW.cycles <> OLD.cycles THEN 'cycles' END), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Run WHERE Run.runId = NEW.runId;
  END IF;
  END//
  
DROP TRIGGER IF EXISTS RunChangeSolid//
CREATE TRIGGER RunChangeSolid BEFORE UPDATE ON RunSolid
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN CONCAT('ends: ', CASE WHEN OLD.pairedEnd THEN 'paired' ELSE 'single' END, ' → ', CASE WHEN NEW.pairedEnd THEN 'paired' ELSE 'single' END) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN 'pairedend' END), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Run WHERE Run.runId = NEW.runId;
  END IF;
  END//

DROP TRIGGER IF EXISTS RunChangeIllumina//
CREATE TRIGGER RunChangeIllumina BEFORE UPDATE ON RunIllumina
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext CHARACTER SET utf8;
  -- Note: cycles are not change logged as they are expected to change a lot via Run Scanner during a run 
  SET log_message = CONCAT_WS(', ',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN CONCAT('ends: ', CASE WHEN OLD.pairedEnd THEN 'paired' ELSE 'single' END, ' → ', CASE WHEN NEW.pairedEnd THEN 'paired' ELSE 'single' END) END,
        CASE WHEN (NEW.runBasesMask IS NULL) <> (OLD.runBasesMask IS NULL) OR NEW.runBasesMask <> OLD.runBasesMask THEN CONCAT('run bases mask: ', COALESCE(OLD.runBasesMask, 'n/a'), ' → ', COALESCE(NEW.runBasesMask, 'n/a')) END,
        CASE WHEN (NEW.workflowType IS NULL) <> (OLD.workflowType IS NULL) OR NEW.workflowType <> OLD.workflowType THEN CONCAT('workflow type: ', COALESCE(OLD.workflowType, 'n/a'), ' → ', COALESCE(NEW.workflowType, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN NEW.pairedEnd <> OLD.pairedEnd THEN 'pairedEnd' END,
        CASE WHEN (NEW.runBasesMask IS NULL) <> (OLD.runBasesMask IS NULL) OR NEW.runBasesMask <> OLD.runBasesMask THEN 'runBasesMask' END,
        CASE WHEN (NEW.workflowType IS NULL) <> (OLD.workflowType IS NULL) OR NEW.workflowType <> OLD.workflowType THEN 'workflowType' END), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Run WHERE Run.runId = NEW.runId;
  END IF;
  END//

DROP TRIGGER IF EXISTS RunChangeOxfordNanopore//
CREATE TRIGGER RunChangeOxfordNanopore BEFORE UPDATE ON RunOxfordNanopore
FOR EACH ROW
  BEGIN
  DECLARE log_message longtext CHARACTER SET utf8;
  SET log_message = CONCAT_WS(', ',
        CASE WHEN (NEW.minKnowVersion IS NULL) <> (OLD.minKnowVersion IS NULL) OR NEW.minKnowVersion <> OLD.minKnowVersion THEN CONCAT('MinKNOW version: ', COALESCE(OLD.minKnowVersion, 'n/a'), ' → ', COALESCE(NEW.minKnowVersion, 'n/a')) END,
        CASE WHEN (NEW.protocolVersion IS NULL) <> (OLD.protocolVersion IS NULL) OR NEW.protocolVersion <> OLD.protocolVersion THEN CONCAT('Protocol version: ', COALESCE(OLD.protocolVersion, 'n/a'), ' → ', COALESCE(NEW.protocolVersion, 'n/a')) END);
  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO RunChangeLog(runId, columnsChanged, userId, message, changeTime)
    SELECT
      NEW.runId,
      COALESCE(CONCAT_WS(',',
        CASE WHEN (NEW.minKnowVersion IS NULL) <> (OLD.minKnowVersion IS NULL) OR NEW.minKnowVersion <> OLD.minKnowVersion THEN 'MinKNOW version' END,
        CASE WHEN (NEW.protocolVersion IS NULL) <> (OLD.protocolVersion IS NULL) OR NEW.protocolVersion <> OLD.protocolVersion THEN 'Protocol version' END), ''),
      lastModifier,
      log_message,
      lastModified
    FROM Run WHERE Run.runId = NEW.runId;
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
