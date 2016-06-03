ALTER TABLE SampleAdditionalInfo ADD COLUMN groupId int(10);
ALTER TABLE SampleAdditionalInfo ADD COLUMN groupDescription varchar(255);

UPDATE SampleAdditionalInfo SET groupId = (SELECT groupId FROM SampleAnalyte WHERE SampleAdditionalInfo.sampleId = SampleAnalyte.sampleId), groupDescription = (SELECT groupDescription FROM SampleAnalyte WHERE SampleAdditionalInfo.sampleId = SampleAnalyte.sampleId);
