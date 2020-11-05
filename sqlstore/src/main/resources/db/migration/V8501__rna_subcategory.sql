UPDATE SampleClass SET sampleSubcategory = 'RNA (stock)'
WHERE sampleCategory = 'Stock'
AND sampleSubCategory IS NULL
AND dnaseTreatable = TRUE;

UPDATE SampleClass SET sampleSubcategory = 'RNA (aliquot)'
WHERE sampleCategory = 'Aliquot'
AND sampleSubcategory IS NULL
AND alias LIKE '%RNA%';

ALTER TABLE SampleClass DROP COLUMN dnaseTreatable;

UPDATE Sample SET discriminator = 'StockRna' WHERE sampleClassId IN
  (SELECT sampleClassId FROM SampleClass WHERE sampleSubcategory = 'RNA (stock)');

UPDATE Sample SET discriminator = 'AliquotRna' WHERE sampleClassId IN
  (SELECT sampleClassId FROM SampleClass WHERE sampleSubcategory = 'RNA (aliquot)');
