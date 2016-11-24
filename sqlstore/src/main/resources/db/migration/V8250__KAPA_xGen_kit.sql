--StartNoTest
SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

INSERT INTO KitDescriptor (name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier) VALUES
('KAPA Hyper Prep & IDT xGen', 5, 'KAPA', 1, 0, 'Library', 'Illumina', NULL, @user),
('Agilent SureSelect XT2 v5', 5, 'Agilent', 1, 0, 'Library', 'Illumina', NULL, @user),
('Agilent SureSelect XT Clinical Research', 5, 'Agilent', 1, 0, 'Library', 'Illumina', NULL, @user),
('AmpliSeq & NEB', 5, 'AmpliSeq', 1, 0, 'Library', 'Illumina', NULL, @user);

SELECT kitDescriptorId INTO @kapaXgen FROM KitDescriptor WHERE name = "KAPA Hyper Prep & IDT xGen";

UPDATE TargetedResequencing SET kitDescriptorId = @kapaXgen WHERE alias = 'xGen Lockdown AML';
--EndNoTest
