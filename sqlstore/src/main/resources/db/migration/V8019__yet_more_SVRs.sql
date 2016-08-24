INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated) VALUES 
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Primary Tumor Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Primary Tumor Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'cDNA (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Metastatic Tumor Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Metastatic Tumor Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'cDNA (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Reference Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Reference Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'cDNA (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Xenograft Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Xenograft Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'cDNA (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Cell Line'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Cell Line'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'cDNA (stock)'),1,NOW(),1,NOW());