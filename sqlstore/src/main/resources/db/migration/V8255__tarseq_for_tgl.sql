--StartNoTest
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

INSERT INTO KitDescriptor (name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier) VALUES
('KAPA Hyper Prep & SureSelect XT', 5, 'KAPA', 1, 0, 'Library', 'Illumina', NULL, @user);

SELECT kitDescriptorId INTO @kapa FROM KitDescriptor WHERE name = "KAPA Hyper Prep & SureSelect XT";

INSERT INTO TargetedResequencing (alias, description, createdBy, creationDate, updatedBy, lastUpdated, kitDescriptorId) VALUES
('Agilent SureSelect Human All Exon V5 + UTRs', 'Agilent SureSelect Human All Exon V5 + UTRs', @user, CURRENT_TIMESTAMP, @user, CURRENT_TIMESTAMP, @kapa),
('Agilent SureSelect Human All Exon V6 Cosmic', 'Agilent SureSelect Human All Exon V6 Cosmic', @user, CURRENT_TIMESTAMP, @user, CURRENT_TIMESTAMP, @kapa);
--EndNoTest