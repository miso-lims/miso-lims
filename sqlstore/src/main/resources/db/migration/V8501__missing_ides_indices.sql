--StartNoTest
SELECT indexFamilyId INTO @famId FROM IndexFamily WHERE name = 'iDES 8bp';

INSERT INTO Indices (name, sequence, position, indexFamilyId)
VALUES ('Index 11','GTCCTTGT',1,@famId),('Index 12','CATTCCGT',1,@famId);
--EndNoTest
