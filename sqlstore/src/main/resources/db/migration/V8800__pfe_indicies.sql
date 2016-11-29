-- StartNoTest
SELECT indexFamilyId INTO @nxdId FROM IndexFamily WHERE name = 'Nextera DNA Dual Index' AND platformType = 'ILLUMINA';
SELECT indexFamilyId INTO @rbcId FROM IndexFamily WHERE name = 'RBC1' AND platformType = 'ILLUMINA';
INSERT INTO IndexFamily(name, platformType, archived) VALUES ('PFE Mixed Index', 'ILLUMINA', TRUE);
SELECT LAST_INSERT_ID() INTO @pfeId;

INSERT INTO Indices(name, sequence, position, indexFamilyId)
  SELECT name, sequence, 1, @pfeId FROM Indices WHERE indexFamilyId = @nxdId AND position = 1
 UNION
  SELECT name, sequence, 2, @pfeId FROM Indices WHERE indexFamilyId = @rbcId AND position = 1;

-- EndNoTest
