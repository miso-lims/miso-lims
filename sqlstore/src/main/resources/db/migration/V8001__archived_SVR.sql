INSERT INTO SampleValidRelationship  (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived) VALUES 
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'whole RNA (stock)'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'rRNA_depleted'),1,NOW(),1,NOW(), 1);
