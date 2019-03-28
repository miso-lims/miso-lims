-- StartNoTest
DELIMITER //
CREATE PROCEDURE TempFixModel(iOldName varchar(10), iNewName varchar(10)) BEGIN
  IF EXISTS (SELECT 1 FROM SequencingContainerModel WHERE alias = iOldName) THEN
    UPDATE SequencingContainerModel SET alias = iNewName WHERE alias = iOldName;
  ELSE
    INSERT INTO SequencingContainerModel (alias, partitionCount, platformType) VALUES (iNewName, 1, 'ILLUMINA');
    INSERT INTO SequencingContainerModel_InstrumentModel (sequencingContainerModelId, instrumentModelId)
    VALUES (
      (SELECT sequencingContainerModelId FROM SequencingContainerModel WHERE alias = iNewName),
      (SELECT instrumentModelId FROM InstrumentModel WHERE alias = 'PromethION')
    );
  END IF;
END//
DELIMITER ;

CALL TempFixModel('PRO-001', 'FLO-PRO001');
CALL TempFixModel('PRO-002', 'FLO-PRO002');

DROP PROCEDURE TempFixModel;
-- EndNoTest
