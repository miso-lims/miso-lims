-- fix_promethion_pos

DELIMITER //
CREATE PROCEDURE TempFixPos(iOldName varchar(10), iNewName varchar(10)) BEGIN
  DECLARE promethion bigint;
  SELECT instrumentModelId INTO promethion FROM InstrumentModel WHERE alias = 'PromethION';
  
  IF EXISTS (SELECT 1 FROM InstrumentPosition WHERE instrumentModelId = promethion AND alias = iOldName) THEN
    UPDATE InstrumentPosition SET alias = iNewName WHERE instrumentModelId = promethion AND alias = iOldName;
  ELSEIF NOT EXISTS (SELECT 1 FROM InstrumentPosition WHERE instrumentModelId = promethion AND alias = iNewName) THEN
    INSERT INTO InstrumentPosition (instrumentModelId, alias) VALUES (promethion, iNewName);
  END IF;
END//
DELIMITER ;

CALL TempFixPos('P101_0', '1-A1-D1');
CALL TempFixPos('P105_0', '1-A3-D3');
CALL TempFixPos('P109_0', '1-A5-D5');

CALL TempFixPos('P101_1', '1-E1-H1');
CALL TempFixPos('P105_1', '1-E3-H3');
CALL TempFixPos('P109_1', '1-E5-H5');

CALL TempFixPos('P102_0', '1-A7-D7');
CALL TempFixPos('P106_0', '1-A9-D9');
CALL TempFixPos('P110_0', '1-A11-D11');

CALL TempFixPos('P102_1', '1-E7-H7');
CALL TempFixPos('P106_1', '1-E9-H9');
CALL TempFixPos('P110_1', '1-E11-H11');

CALL TempFixPos('P103_0', '2-A1-D1');
CALL TempFixPos('P107_0', '2-A3-D3');
CALL TempFixPos('P111_0', '2-A5-D5');

CALL TempFixPos('P103_1', '2-E1-H1');
CALL TempFixPos('P107_1', '2-E3-H3');
CALL TempFixPos('P111_1', '2-E5-H5');

CALL TempFixPos('P104_0', '2-A7-D7');
CALL TempFixPos('P108_0', '2-A9-D9');
CALL TempFixPos('P112_0', '2-A11-D11');

CALL TempFixPos('P104_1', '2-E7-H7');
CALL TempFixPos('P108_1', '2-E9-H9');
CALL TempFixPos('P112_1', '2-E11-H11');

DROP PROCEDURE TempFixPos;

DELIMITER //
CREATE PROCEDURE TempAddPos(iName varchar(10)) BEGIN
  DECLARE promethion bigint;
  SELECT instrumentModelId INTO promethion FROM InstrumentModel WHERE alias = 'PromethION';
  
  IF NOT EXISTS (SELECT 1 FROM InstrumentPosition WHERE instrumentModelId = promethion AND alias = iName) THEN
    INSERT INTO InstrumentPosition (instrumentModelId, alias) VALUES (promethion, iName);
  END IF;
END//
DELIMITER ;

CALL TempAddPos('1-A2-D2');
CALL TempAddPos('1-A4-D4');
CALL TempAddPos('1-A6-D6');
CALL TempAddPos('1-E2-H2');
CALL TempAddPos('1-E4-H4');
CALL TempAddPos('1-E6-H6');
CALL TempAddPos('1-A8-D8');
CALL TempAddPos('1-A10-D10');
CALL TempAddPos('1-A12-D12');
CALL TempAddPos('1-E8-H8');
CALL TempAddPos('1-E10-H10');
CALL TempAddPos('1-E12-H12');
CALL TempAddPos('2-A2-D2');
CALL TempAddPos('2-A4-D4');
CALL TempAddPos('2-A6-D6');
CALL TempAddPos('2-E2-H2');
CALL TempAddPos('2-E4-H4');
CALL TempAddPos('2-E6-H6');
CALL TempAddPos('2-A8-D8');
CALL TempAddPos('2-A10-D10');
CALL TempAddPos('2-A12-D12');
CALL TempAddPos('2-E8-H8');
CALL TempAddPos('2-E10-H10');
CALL TempAddPos('2-E12-H12');

DROP PROCEDURE TempAddPos;

SELECT instrumentModelId INTO @promethion FROM InstrumentModel WHERE alias = 'PromethION';
DELETE FROM InstrumentPosition WHERE instrumentModelId = @promethion AND alias LIKE 'P___\__';


-- fix_promethion_models

DELIMITER //
CREATE PROCEDURE TempFixModel(iOldName varchar(10), iNewName varchar(10)) BEGIN
  IF EXISTS (SELECT 1 FROM SequencingContainerModel WHERE alias = iOldName) THEN
    UPDATE SequencingContainerModel SET alias = iNewName WHERE alias = iOldName;
  ELSE
    INSERT INTO SequencingContainerModel (alias, partitionCount, platformType) VALUES (iNewName, 1, 'OXFORDNANOPORE');
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
