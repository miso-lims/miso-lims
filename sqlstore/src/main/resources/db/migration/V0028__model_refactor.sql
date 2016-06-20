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

--StartNoTest
DELETE svr FROM SampleValidRelationship svr
JOIN SampleClass parent ON parent.sampleClassId = svr.parentId
JOIN SampleClass child ON child.sampleClassId = svr.childId
WHERE parent.sampleCategory = 'Identity' AND child.sampleCategory <> 'Tissue';
--EndNoTest

ALTER TABLE SampleTissue ADD COLUMN tissueOriginId bigint(20) DEFAULT NULL;
ALTER TABLE SampleTissue ADD COLUMN tissueTypeId bigint(20) DEFAULT NULL;
ALTER TABLE SampleTissue ADD COLUMN passageNumber int(11) DEFAULT NULL;
ALTER TABLE SampleTissue ADD COLUMN timesReceived int(11) DEFAULT NULL;
ALTER TABLE SampleTissue ADD COLUMN tubeNumber int(11) DEFAULT NULL;
ALTER TABLE SampleTissue ADD COLUMN labId bigint(20) DEFAULT NULL;
ALTER TABLE SampleTissue ADD COLUMN externalInstituteIdentifier varchar(255) DEFAULT NULL;
ALTER TABLE SampleTissue ADD CONSTRAINT sampletissue_origin_fkey FOREIGN KEY (tissueOriginId) REFERENCES TissueOrigin(tissueOriginId);
ALTER TABLE SampleTissue ADD CONSTRAINT sampletissue_type_fkey FOREIGN KEY (tissueTypeId) REFERENCES TissueType(tissueTypeId);
ALTER TABLE SampleTissue ADD CONSTRAINT sampletissue_lab_fkey FOREIGN KEY (labId) REFERENCES Lab(labId);

--StartNoTest
UPDATE SampleTissue st, SampleAdditionalInfo sai
SET st.tissueOriginId=sai.tissueOriginId,
st.tissueTypeId=sai.tissueTypeId,
st.passageNumber=sai.passageNumber,
st.timesReceived=sai.timesReceived,
st.tubeNumber=sai.tubeNumber,
st.labId=sai.labId,
st.externalInstituteIdentifier=sai.externalInstituteIdentifier
WHERE sai.sampleId=st.sampleId;
--EndNoTest

-- tissueOrigin fkey
ALTER TABLE SampleAdditionalInfo DROP FOREIGN KEY FK24aduvv5cljo3ggnt0s2cs1w3;
-- tissueType fkey
ALTER TABLE SampleAdditionalInfo DROP FOREIGN KEY FKoulifnc7plonin8pbreiovb3x;
ALTER TABLE SampleAdditionalInfo DROP FOREIGN KEY sampleadditionalinfo_lab_fkey;
ALTER TABLE SampleAdditionalInfo DROP COLUMN tissueOriginId;
ALTER TABLE SampleAdditionalInfo DROP COLUMN tissueTypeId;
ALTER TABLE SampleAdditionalInfo DROP COLUMN passageNumber;
ALTER TABLE SampleAdditionalInfo DROP COLUMN timesReceived;
ALTER TABLE SampleAdditionalInfo DROP COLUMN tubeNumber;
ALTER TABLE SampleAdditionalInfo DROP COLUMN labId;
ALTER TABLE SampleAdditionalInfo DROP COLUMN externalInstituteIdentifier;

ALTER TABLE LibraryAdditionalInfo DROP FOREIGN KEY libraryAdditionalInfo_tissueOrigin_fkey;
ALTER TABLE LibraryAdditionalInfo DROP FOREIGN KEY libraryAdditionalInfo_tissueType_fkey;
ALTER TABLE LibraryAdditionalInfo DROP COLUMN tissueOriginId;
ALTER TABLE LibraryAdditionalInfo DROP COLUMN tissueTypeId;
