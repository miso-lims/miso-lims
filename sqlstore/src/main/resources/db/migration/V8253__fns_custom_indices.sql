-- StartNoTest
SELECT indexFamilyId INTO @nxxId FROM IndexFamily WHERE name = 'Nextera XT Dual Index' AND platformType = 'ILLUMINA';
SELECT indexFamilyId INTO @nxdId FROM IndexFamily WHERE name = 'Nextera DNA Dual Index' AND platformType = 'ILLUMINA';
INSERT INTO IndexFamily(name, platformType, archived) VALUES ('FNS Nextera DNA & XT Dual Index', 'ILLUMINA', TRUE);
SELECT LAST_INSERT_ID() INTO @fnsId;

INSERT INTO Indices(name, sequence, position, indexFamilyId)
  SELECT name, sequence, 1, @fnsId FROM Indices WHERE indexFamilyId = @nxxId AND position = 1
 UNION
  SELECT name, sequence, 2, @fnsId FROM Indices WHERE indexFamilyId = @nxdId AND position = 2;

-- EndNoTest