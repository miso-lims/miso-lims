ALTER TABLE SampleClass ADD COLUMN dnaseTreatable boolean DEFAULT FALSE;
UPDATE SampleClass SET dnaseTreatable = TRUE WHERE alias LIKE '%RNA%' AND sampleCategory = 'Stock';
ALTER TABLE SampleStock ADD COLUMN dnaseTreated boolean DEFAULT FALSE;
