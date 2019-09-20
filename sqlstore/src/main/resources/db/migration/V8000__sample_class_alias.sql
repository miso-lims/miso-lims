ALTER TABLE SampleClass ADD COLUMN sampleSubcategory varchar(50);

UPDATE SampleClass SET sampleSubcategory = alias WHERE alias IN ('LCM Tube', 'Slide', 'Single Cell');
UPDATE SampleClass SET sampleSubcategory = 'Single Cell (stock)' WHERE alias = 'Single Cell DNA (stock)';
UPDATE SampleClass SET sampleSubcategory = 'Single Cell (aliquot)' WHERE alias = 'Single Cell DNA (aliquot)';
