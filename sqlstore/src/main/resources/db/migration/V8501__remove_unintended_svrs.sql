--StartNoTest
DELETE svr FROM SampleValidRelationship svr
JOIN SampleClass parent ON parent.sampleClassId = svr.parentId
JOIN SampleClass child ON child.sampleClassId = svr.childId
WHERE parent.sampleCategory = 'Identity' AND child.sampleCategory <> 'Tissue';
--EndNoTest
