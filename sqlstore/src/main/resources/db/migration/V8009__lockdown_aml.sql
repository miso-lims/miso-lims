SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

INSERT INTO KitDescriptor (name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier) VALUES
('KAPA Hyper Prep & SureSelect XT2 v5', 5, 'KAPA', 1, 0, 'Library', 'Illumina', NULL, @user);

SELECT kitDescriptorId INTO @kapa FROM KitDescriptor WHERE name = "KAPA Hyper Prep & SureSelect XT2 v5";

INSERT INTO TargetedResequencing (alias, description, createdBy, creationDate, updatedBy, lastUpdated, kitDescriptorId) VALUES
('xGen Lockdown AML', 'xGen Lockdown AML', @user, @time, @user, @time, @kapa);