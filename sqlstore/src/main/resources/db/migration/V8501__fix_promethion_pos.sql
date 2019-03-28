-- StartNoTest
DELIMITER //
CREATE PROCEDURE FixPos(iOldName varchar(10), iNewName varchar(10)) BEGIN
  DECLARE promethion bigint(20);
  SELECT instrumentModelId INTO promethion FROM InstrumentModel WHERE alias = 'PromethION';

  IF EXISTS (SELECT 1 FROM InstrumentPosition WHERE instrumentModelId = promethion AND alias = iOldName)
  THEN
    UPDATE InstrumentPosition SET alias = iNewName WHERE instrumentModelId = promethion AND alias = iOldName;
  ELSE
    INSERT INTO InstrumentPosition (instrumentModelId, alias) VALUES (promethion, iNewName);
  END IF;
END//
DELIMITER ;

CALL FixPos('P101_0', '1-A1-D1');
CALL FixPos('P105_0', '1-A3-D3');
CALL FixPos('P109_0', '1-A5-D5');

CALL FixPos('P101_1', '1-E1-D1');
CALL FixPos('P105_1', '1-E3-D3');
CALL FixPos('P109_1', '1-E5-D5');

CALL FixPos('P102_0', '1-A7-D7');
CALL FixPos('P106_0', '1-A9-D9');
CALL FixPos('P110_0', '1-A11-D11');

CALL FixPos('P102_1', '1-E7-D7');
CALL FixPos('P106_1', '1-E9-D9');
CALL FixPos('P110_1', '1-E11-D11');

CALL FixPos('P103_0', '2-A1-D1');
CALL FixPos('P107_0', '2-A3-D3');
CALL FixPos('P111_0', '2-A5-D5');

CALL FixPos('P103_1', '2-E1-D1');
CALL FixPos('P107_1', '2-E3-D3');
CALL FixPos('P111_1', '2-E5-D5');

CALL FixPos('P104_0', '2-A7-D7');
CALL FixPos('P108_0', '2-A9-D9');
CALL FixPos('P112_0', '2-A11-D11');

CALL FixPos('P104_1', '2-E7-D7');
CALL FixPos('P108_1', '2-E9-D9');
CALL FixPos('P112_1', '2-E11-D11');

DROP PROCEDURE FixPos;

SELECT instrumentModelId INTO @promethion FROM InstrumentModel WHERE alias = 'PromethION';

INSERT INTO InstrumentPosition (instrumentModelId, alias) VALUES
(@promethion, '1-A2-D2'),
(@promethion, '1-A4-D4'),
(@promethion, '1-A6-D6'),
(@promethion, '1-E2-H2'),
(@promethion, '1-E4-H4'),
(@promethion, '1-E6-H6'),
(@promethion, '1-A8-D8'),
(@promethion, '1-A10-D10'),
(@promethion, '1-A12-D12'),
(@promethion, '1-E8-H8'),
(@promethion, '1-E10-H10'),
(@promethion, '1-E12-H12'),
(@promethion, '2-A2-D2'),
(@promethion, '2-A4-D4'),
(@promethion, '2-A6-D6'),
(@promethion, '2-E2-H2'),
(@promethion, '2-E4-H4'),
(@promethion, '2-E6-H6'),
(@promethion, '2-A8-D8'),
(@promethion, '2-A10-D10'),
(@promethion, '2-A12-D12'),
(@promethion, '2-E8-H8'),
(@promethion, '2-E10-H10'),
(@promethion, '2-E12-H12');

DELETE FROM InstrumentPosition WHERE instrumentModelId = @promethion AND alias LIKE 'P1__\__';
--EndNoTest
