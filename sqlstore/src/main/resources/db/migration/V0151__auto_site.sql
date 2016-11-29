-- KAPA_xGen_kit
-- StartNoTest
--StartNoTest
SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

INSERT INTO KitDescriptor (name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier) VALUES
('KAPA Hyper Prep & IDT xGen', 5, 'KAPA', 1, 0, 'Library', 'Illumina', NULL, @user),
('Agilent SureSelect XT2 v5', 5, 'Agilent', 1, 0, 'Library', 'Illumina', NULL, @user),
('Agilent SureSelect XT Clinical Research', 5, 'Agilent', 1, 0, 'Library', 'Illumina', NULL, @user),
('AmpliSeq & NEB', 5, 'AmpliSeq', 1, 0, 'Library', 'Illumina', NULL, @user);

SELECT kitDescriptorId INTO @kapaXgen FROM KitDescriptor WHERE name = "KAPA Hyper Prep & IDT xGen";

UPDATE TargetedSequencing SET kitDescriptorId = @kapaXgen WHERE alias = 'xGen Lockdown AML';
--EndNoTest
-- EndNoTest

-- tarseq_for_tgl
-- StartNoTest
--StartNoTest
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

INSERT INTO KitDescriptor (name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier) VALUES
('KAPA Hyper Prep & SureSelect XT', 5, 'KAPA', 1, 0, 'Library', 'Illumina', NULL, @user);

SELECT kitDescriptorId INTO @kapa FROM KitDescriptor WHERE name = "KAPA Hyper Prep & SureSelect XT";

INSERT INTO TargetedSequencing (alias, description, createdBy, creationDate, updatedBy, lastUpdated, kitDescriptorId) VALUES
('Agilent SureSelect Human All Exon V5 + UTRs', 'Agilent SureSelect Human All Exon V5 + UTRs', @user, CURRENT_TIMESTAMP, @user, CURRENT_TIMESTAMP, @kapa),
('Agilent SureSelect Human All Exon V6 Cosmic', 'Agilent SureSelect Human All Exon V6 Cosmic', @user, CURRENT_TIMESTAMP, @user, CURRENT_TIMESTAMP, @kapa);
--EndNoTest
-- EndNoTest

-- deduplicate_seq_params
-- StartNoTest
--StartNoTest
UPDATE Run SET sequencingParameters_parametersId = (
  SELECT MIN(parametersId) FROM SequencingParameters sp 
  WHERE sp.platformId = (
    SELECT platformId FROM SequencingParameters
    WHERE parametersId = Run.sequencingParameters_parametersId) 
  AND sp.name = (SELECT name FROM SequencingParameters WHERE parametersId = Run.sequencingParameters_parametersId) 
  GROUP BY sp.platformId, sp.name
);

DELETE FROM SequencingParameters
WHERE parametersId NOT IN (
  SELECT MIN(sp.parametersId) FROM (SELECT * FROM SequencingParameters) sp
  GROUP BY sp.platformId, sp.name
);
--EndNoTest
-- EndNoTest

-- pfe_indicies
-- StartNoTest
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
-- EndNoTest

-- thymus_and_cervix
-- StartNoTest
-- StartNoTest
SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
INSERT INTO TissueOrigin(alias, description, createdBy, creationDate, updatedBy, lastUpdated) VALUES
  ('Ce', 'Cervix', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP),
  ('Th', 'Thymus', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
-- EndNoTest
-- EndNoTest

