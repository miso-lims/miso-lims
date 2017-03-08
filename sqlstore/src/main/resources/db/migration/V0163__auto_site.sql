-- StartNoTest
INSERT INTO KitDescriptor(name, version, manufacturer, partNumber, kitType, platformType, description, lastModifier)
  SELECT 'Agilent SureSelect Human All Exon V5 + UTRs', 1, 'Agilent', 1, 'Library', 'Illumina', 'TGL', (SELECT userId FROM User WHERE loginName = 'admin') FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM KitDescriptor WHERE name = 'Agilent SureSelect Human All Exon V5 + UTRs');

UPDATE TargetedSequencing SET kitDescriptorId = (SELECT kitDescriptorId FROM KitDescriptor WHERE name = 'Agilent SureSelect Human All Exon V6 Cosmic') WHERE alias = 'Agilent SureSelect Human All Exon V6 Cosmic';
UPDATE TargetedSequencing SET kitDescriptorId = (SELECT kitDescriptorId FROM KitDescriptor WHERE name = 'Agilent SureSelect Human All Exon V5 + UTRs') WHERE alias = 'Agilent SureSelect Human All Exon V5 + UTRs';

-- if any libraries use a kit that is inappropriate for the newly-changed targeted sequencing values, update the kits.
UPDATE LibraryAdditionalInfo SET kitDescriptorId = (
  SELECT kitDescriptorId FROM TargetedSequencing WHERE alias = 'Agilent SureSelect Human All Exon V6 Cosmic')
WHERE libraryId IN (SELECT library_libraryId FROM LibraryDilution WHERE targetedSequencingId IN (SELECT targetedSequencingId FROM TargetedSequencing 
WHERE alias = 'Agilent SureSelect Human All Exon V6 Cosmic'));

UPDATE LibraryAdditionalInfo SET kitDescriptorId = (
  SELECT kitDescriptorId FROM TargetedSequencing WHERE alias = 'Agilent SureSelect Human All Exon V5 + UTRs')
WHERE libraryId IN (SELECT library_libraryId FROM LibraryDilution WHERE targetedSequencingId IN (SELECT targetedSequencingId FROM TargetedSequencing 
WHERE alias = 'Agilent SureSelect Human All Exon V5 + UTRs'));
-- EndNoTest
