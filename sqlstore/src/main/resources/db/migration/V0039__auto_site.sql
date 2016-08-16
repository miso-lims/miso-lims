-- EPIC-TCR_barcodes
--StartNoTest
INSERT INTO TagBarcodeFamily (name, platformType) VALUES ('EPIC-TCR Dual Index', 'ILLUMINA');

INSERT INTO TagBarcodes (name, sequence, position, tagFamilyId)
SELECT tb.name, tb.sequence, 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'EPIC-TCR Dual Index')
FROM TagBarcodes tb JOIN TagBarcodeFamily fam ON fam.tagFamilyId = tb.tagFamilyId
WHERE fam.name = 'NEXTflex 8bp';

INSERT INTO TagBarcodes (name, sequence, position, tagFamilyId)
SELECT tb.name, tb.sequence, 2, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'EPIC-TCR Dual Index')
FROM TagBarcodes tb JOIN TagBarcodeFamily fam ON fam.tagFamilyId = tb.tagFamilyId
WHERE fam.name = 'TCRindex2';
--EndNoTest

-- add_kits
--StartNoTest
INSERT INTO KitDescriptor(name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier)
SELECT 'KAPA OnBead', 0, 'KAPA', 1, 0, 'Library', 'Illumina', 'n/a', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM KitDescriptor WHERE name = 'KAPA OnBead');

INSERT INTO KitDescriptor(name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier)
VALUES ('CRISPR Seq Prep', 0, 'CRISPR', 1, 0, 'Library', 'Illumina', 'n/a', 1),
('Nextera XT', 0, 'Nextera', 1, 0, 'Library', 'Illumina', 'n/a', 1);
--EndNoTest

