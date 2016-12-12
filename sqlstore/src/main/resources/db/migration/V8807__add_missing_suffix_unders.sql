UPDATE SampleClass SET suffix = CONCAT(suffix, '_') WHERE sampleCategory = 'Aliquot' AND suffix NOT LIKE '%\\_';
