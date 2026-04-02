DELIMITER //

DROP TRIGGER IF EXISTS ArrayRunSampleInsert//
CREATE TRIGGER ArrayRunSampleInsert AFTER INSERT ON ArrayRun_Sample
FOR EACH ROW
BEGIN
  DECLARE log_message longtext;

  SET log_message = CONCAT_WS(', ',
    makeChangeMessage('QC status', NULL, (SELECT description FROM RunItemQcStatus WHERE statusId = NEW.statusId)),
    makeChangeMessage('QC note', NULL, NEW.qcNote),
    makeChangeMessage('QC user', NULL, (SELECT fullName FROM User WHERE userId = NEW.qcUser)),
    makeChangeMessage('QC date', NULL, NEW.qcDate)
  );

  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO ArrayRunChangeLog(arrayRunId, columnsChanged, userId, message, changeTime)
    VALUES (
      NEW.arrayRunId,
      COALESCE(CONCAT_WS(',',
        makeChangeColumn('sample statusId', NULL, NEW.statusId),
        makeChangeColumn('sample qcNote', NULL, NEW.qcNote),
        makeChangeColumn('sample qcUser', NULL, NEW.qcUser),
        makeChangeColumn('sample qcDate', NULL, NEW.qcDate)
      ), ''),
      COALESCE(NEW.qcUser, (SELECT lastModifier FROM ArrayRun WHERE arrayRunId = NEW.arrayRunId)),
      CONCAT(
        COALESCE((SELECT name FROM Sample WHERE sampleId = NEW.sampleId), NEW.position),
        ' at ', NEW.position, ' ',
        log_message
      ),
      CURRENT_TIMESTAMP()
    );
  END IF;
END//

DROP TRIGGER IF EXISTS ArrayRunSampleUpdate//
CREATE TRIGGER ArrayRunSampleUpdate BEFORE UPDATE ON ArrayRun_Sample
FOR EACH ROW
BEGIN
  DECLARE log_message longtext;

  SET log_message = CONCAT_WS(', ',
    makeChangeMessage('QC status',
      (SELECT description FROM RunItemQcStatus WHERE statusId = OLD.statusId),
      (SELECT description FROM RunItemQcStatus WHERE statusId = NEW.statusId)),
    makeChangeMessage('QC note', OLD.qcNote, NEW.qcNote),
    makeChangeMessage('QC user',
      (SELECT fullName FROM User WHERE userId = OLD.qcUser),
      (SELECT fullName FROM User WHERE userId = NEW.qcUser)),
    makeChangeMessage('QC date', OLD.qcDate, NEW.qcDate)
  );

  IF log_message IS NOT NULL AND log_message <> '' THEN
    INSERT INTO ArrayRunChangeLog(arrayRunId, columnsChanged, userId, message, changeTime)
    VALUES (
      NEW.arrayRunId,
      COALESCE(CONCAT_WS(',',
        makeChangeColumn('sample statusId', OLD.statusId, NEW.statusId),
        makeChangeColumn('sample qcNote', OLD.qcNote, NEW.qcNote),
        makeChangeColumn('sample qcUser', OLD.qcUser, NEW.qcUser),
        makeChangeColumn('sample qcDate', OLD.qcDate, NEW.qcDate)
      ), ''),
      COALESCE(NEW.qcUser, (SELECT lastModifier FROM ArrayRun WHERE arrayRunId = NEW.arrayRunId)),
      CONCAT(
        COALESCE((SELECT name FROM Sample WHERE sampleId = NEW.sampleId), NEW.position),
        ' at ', NEW.position, ': ',
        log_message
      ),
      CURRENT_TIMESTAMP()
    );
  END IF;
END//

DELIMITER ;
