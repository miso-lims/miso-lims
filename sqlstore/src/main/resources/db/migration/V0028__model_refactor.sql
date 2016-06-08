ALTER TABLE SampleAdditionalInfo ADD COLUMN groupId int(10);
ALTER TABLE SampleAdditionalInfo ADD COLUMN groupDescription varchar(255);

UPDATE SampleAdditionalInfo SET groupId = (SELECT groupId FROM SampleAnalyte WHERE SampleAdditionalInfo.sampleId = SampleAnalyte.sampleId), groupDescription = (SELECT groupDescription FROM SampleAnalyte WHERE SampleAdditionalInfo.sampleId = SampleAnalyte.sampleId);

-- createdBy fkey
ALTER TABLE SampleAdditionalInfo DROP FOREIGN KEY `FKeutn2473w3yr16khgalspuviw`;
-- updatedBy fkey
ALTER TABLE SampleAdditionalInfo DROP FOREIGN KEY `FKp8bvx3e7jsmnyw51toi7mq7cq`;
ALTER TABLE SampleAdditionalInfo DROP COLUMN createdBy;
ALTER TABLE SampleAdditionalInfo DROP COLUMN creationDate;
ALTER TABLE SampleAdditionalInfo DROP COLUMN updatedBy;
ALTER TABLE SampleAdditionalInfo DROP COLUMN lastUpdated;
-- createdBy fkey
ALTER TABLE Identity DROP FOREIGN KEY `FKauqylg2sle5eudy0tqabtlmsb`;
-- updatedBy fkey
ALTER TABLE Identity DROP FOREIGN KEY `FKa8c6e56hg9iucguhr0dcse62h`;
ALTER TABLE Identity DROP COLUMN createdBy;
ALTER TABLE Identity DROP COLUMN creationDate;
ALTER TABLE Identity DROP COLUMN updatedBy;
ALTER TABLE Identity DROP COLUMN lastUpdated;
ALTER TABLE SampleTissue DROP FOREIGN KEY `sampleTissue_createUser_fkey`;
ALTER TABLE SampleTissue DROP FOREIGN KEY `sampleTissue_updateUser_fkey`;
ALTER TABLE SampleTissue DROP COLUMN createdBy;
ALTER TABLE SampleTissue DROP COLUMN creationDate;
ALTER TABLE SampleTissue DROP COLUMN updatedBy;
ALTER TABLE SampleTissue DROP COLUMN lastUpdated;
-- createdBy fkey
ALTER TABLE SampleAnalyte DROP FOREIGN KEY `FKpras819b6p7vh12xbeovne8o0`;
-- updatedBy fkey
ALTER TABLE SampleAnalyte DROP FOREIGN KEY `FKprqyhv40bntjrf5l64mjdgl1j`;
ALTER TABLE SampleAnalyte DROP COLUMN createdBy;
ALTER TABLE SampleAnalyte DROP COLUMN creationDate;
ALTER TABLE SampleAnalyte DROP COLUMN updatedBy;
ALTER TABLE SampleAnalyte DROP COLUMN lastUpdated;
