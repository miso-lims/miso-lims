-- This migration may delete sample valid relationships that are in use. To find bad entities, do:
-- SELECT child.sampleId AS sampleId FROM SampleAdditionalInfo child JOIN SampleAdditionalInfo parent ON child.parentId = parent.sampleId WHERE (SELECT COUNT(*) FROM SampleValidRelationship WHERE SampleValidRelationship.parentId = parent.sampleClassId AND SampleValidRelationship.childId = child.sampleClassId) = 0;
ALTER TABLE SampleAdditionalInfo ADD COLUMN groupId int;
ALTER TABLE SampleAdditionalInfo ADD COLUMN groupDescription varchar(255);
ALTER TABLE SampleAdditionalInfo ADD COLUMN isSynthetic BOOLEAN NOT NULL DEFAULT FALSE;

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
ALTER TABLE SampleTissue DROP COLUMN cellularity;

ALTER TABLE SampleTissue ADD COLUMN tissueOriginId bigint;
ALTER TABLE SampleTissue ADD CONSTRAINT `FK_st_tissueOrigin_tissueOriginId` FOREIGN KEY (`tissueOriginId`) REFERENCES `TissueOrigin` (`tissueOriginId`);
ALTER TABLE SampleTissue ADD COLUMN tissueTypeId bigint;
ALTER TABLE SampleTissue ADD CONSTRAINT `FK_st_tissueType_tissueTypeId` FOREIGN KEY (`tissueTypeId`) REFERENCES `TissueType` (`tissueTypeId`);
ALTER TABLE SampleTissue ADD COLUMN externalInstituteIdentifier varchar(255);
ALTER TABLE SampleTissue ADD COLUMN labId bigint;
ALTER TABLE SampleTissue ADD CONSTRAINT `FK_st_lab_labId` FOREIGN KEY (`labId`) REFERENCES `Lab` (`labId`);
ALTER TABLE SampleTissue ADD COLUMN region varchar(255); 
ALTER TABLE SampleTissue ADD COLUMN passageNumber int;
ALTER TABLE SampleTissue ADD COLUMN tubeNumber int;
ALTER TABLE SampleTissue ADD COLUMN timesReceived int;
ALTER TABLE SampleTissue ADD COLUMN tissueMaterialId bigint;
ALTER TABLE SampleTissue ADD CONSTRAINT `FK_st_tm_tissueMaterialId` FOREIGN KEY (`tissueMaterialId`) REFERENCES `TissueMaterial` (`tissueMaterialId`);

-- StartNoTest
-- H2 can't cope with a JOIN in an UPDATE
UPDATE SampleTissue JOIN SampleAdditionalInfo ON SampleTissue.sampleId = SampleAdditionalInfo.sampleId SET
  SampleTissue.tissueOriginId = SampleAdditionalInfo.tissueOriginId,
  SampleTissue.tissueTypeId = SampleAdditionalInfo.tissueTypeId,
  SampleTissue.externalInstituteIdentifier = SampleAdditionalInfo.externalInstituteIdentifier,
  SampleTissue.labId = SampleAdditionalInfo.labId,
  SampleTissue.passageNumber = SampleAdditionalInfo.passageNumber,
  SampleTissue.tubeNumber = SampleAdditionalInfo.tubeNumber,
  SampleTissue.timesReceived = SampleAdditionalInfo.timesReceived;
-- EndNoTest

DROP TABLE IF EXISTS SampleStock;
DROP TABLE IF EXISTS SampleAliquot;
CREATE TABLE `SampleStock` (
  `sampleId` bigint NOT NULL,
  `concentration` int DEFAULT NULL,
  `strStatus` varchar(50) NOT NULL DEFAULT 'NOT_SUBMITTED',
  PRIMARY KEY (`sampleId`),
  KEY `K_ss_sampleId` (`sampleId`),
  CONSTRAINT `FK_ss_sample_sampleId` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleAliquot` (
  `sampleId` bigint NOT NULL,
  `samplePurposeId` bigint DEFAULT NULL,
  PRIMARY KEY (`sampleId`),
  KEY `K_sa_sampleId` (`sampleId`),
  KEY `K_sa_samplePurposeId` (`samplePurposeId`),
  CONSTRAINT `FK_sa_samplePurpose_samplePurposeId` FOREIGN KEY (`samplePurposeId`) REFERENCES `SamplePurpose` (`samplePurposeId`),
  CONSTRAINT `FK_sa_sample_sampleId` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

UPDATE SampleClass SET sampleCategory = 'Aliquot' WHERE sampleCategory = 'Analyte' AND NOT isStock;
UPDATE SampleClass SET sampleCategory = 'Stock' WHERE sampleCategory = 'Analyte' AND isStock;
INSERT INTO SampleStock(sampleId, concentration, strStatus) SELECT SampleAnalyte.sampleId, concentration, strStatus FROM SampleAnalyte JOIN SampleAdditionalInfo ON SampleAnalyte.sampleId = SampleAdditionalInfo.sampleId JOIN SampleClass ON SampleAdditionalInfo.sampleClassId = SampleClass.sampleClassId WHERE SampleClass.sampleCategory = 'Stock';
INSERT INTO SampleAliquot(sampleId, samplePurposeId) SELECT sampleId, samplePurposeId FROM SampleAnalyte WHERE sampleId IN (SELECT sampleId FROM SampleAdditionalInfo JOIN SampleClass ON SampleAdditionalInfo.sampleClassId = SampleClass.sampleClassId WHERE SampleClass.sampleCategory = 'Aliquot');

DROP TABLE SampleAnalyte;

DELETE FROM SampleClass WHERE sampleCategory = 'Analyte';
ALTER TABLE SampleClass DROP COLUMN isStock;

-- StartNoTest
DELETE svr FROM SampleValidRelationship svr
JOIN SampleClass parent ON parent.sampleClassId = svr.parentId
JOIN SampleClass child ON child.sampleClassId = svr.childId
WHERE parent.sampleCategory = 'Identity' AND child.sampleCategory <> 'Tissue';
-- EndNoTest

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
ALTER TABLE SampleAdditionalInfo DROP COLUMN concentration;

ALTER TABLE LibraryAdditionalInfo DROP FOREIGN KEY libraryAdditionalInfo_tissueOrigin_fkey;
ALTER TABLE LibraryAdditionalInfo DROP FOREIGN KEY libraryAdditionalInfo_tissueType_fkey;
ALTER TABLE LibraryAdditionalInfo DROP COLUMN tissueOriginId;
ALTER TABLE LibraryAdditionalInfo DROP COLUMN tissueTypeId;

CREATE TABLE `SampleTissueProcessing` (
    `sampleId` bigint PRIMARY KEY,
    CONSTRAINT `sampleTP_sample_fkey` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleCVSlide` (
    `sampleId` bigint PRIMARY KEY,
    `cuts` int NOT NULL DEFAULT 0,
    `discards` int DEFAULT 0,
    `thickness` int,
    CONSTRAINT `sampleCVSlide_sample_fkey` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleLCMTube` (
    `sampleId` bigint PRIMARY KEY,
    `cutsConsumed` int NOT NULL DEFAULT 0,
    CONSTRAINT `sampleLCMTube_sample_fkey` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

