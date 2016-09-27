SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';
SELECT sampleClassId INTO @rnaStockId FROM SampleClass WHERE alias = 'whole RNA (stock)';
INSERT INTO SampleValidRelationship  (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived) VALUES 
  (@rnaStockId,(SELECT sampleClassId FROM SampleClass WHERE alias = 'smRNA'),@user,@time,@user,@time,1),
  (@rnaStockId,(SELECT sampleClassId FROM SampleClass WHERE alias = 'mRNA'),@user,@time,@user,@time,1);
