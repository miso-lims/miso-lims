INSERT INTO TagBarcodeFamily (name, platformType) VALUES ('EPIC-TCR Dual Index', 'ILLUMINA');

INSERT INTO TagBarcodes (name, sequence, position, tagFamilyId)
SELECT tb.name, tb.sequence, 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'EPIC-TCR Dual Index')
FROM TagBarcodes tb JOIN TagBarcodeFamily fam ON fam.tagFamilyId = tb.tagFamilyId
WHERE fam.name = 'NEXTflex 8bp';

INSERT INTO TagBarcodes (name, sequence, position, tagFamilyId)
SELECT tb.name, tb.sequence, 2, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'EPIC-TCR Dual Index')
FROM TagBarcodes tb JOIN TagBarcodeFamily fam ON fam.tagFamilyId = tb.tagFamilyId
WHERE fam.name = 'TCRindex2';
