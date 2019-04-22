
-- Switch tables to InnoDB to permit foreign key constraints.
-- StartNoTest
ALTER TABLE Sample ENGINE = InnoDB ROW_FORMAT = DEFAULT;
ALTER TABLE Platform ENGINE = InnoDB ROW_FORMAT = DEFAULT;
ALTER TABLE Pool ENGINE = InnoDB ROW_FORMAT = DEFAULT;
ALTER TABLE Project ENGINE = InnoDB ROW_FORMAT = DEFAULT;
ALTER TABLE KitDescriptor ENGINE = InnoDB ROW_FORMAT = DEFAULT;
ALTER TABLE User ENGINE = InnoDB ROW_FORMAT = DEFAULT;
ALTER TABLE Library ENGINE = InnoDB ROW_FORMAT = DEFAULT;
ALTER TABLE LibrarySelectionType ENGINE = InnoDB ROW_FORMAT = DEFAULT;
ALTER TABLE LibraryStrategyType ENGINE = InnoDB ROW_FORMAT = DEFAULT;
-- EndNoTest

CREATE TABLE `TissueOrigin` (
  `tissueOriginId` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`tissueOriginId`),
  UNIQUE KEY `UK_m3j5fpd9m5hpofmdggxxmxtde` (`alias`),
  KEY `FK8gy70defmu4xsbhiubahuwto9` (`createdBy`),
  KEY `FKjdbxm47tiwma7ge045wjgvjdi` (`updatedBy`),
  CONSTRAINT `FK8gy70defmu4xsbhiubahuwto9` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKjdbxm47tiwma7ge045wjgvjdi` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `TissueType` (
  `tissueTypeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`tissueTypeId`),
  UNIQUE KEY `UK_5kvipym1ykutjwljtmigu043a` (`alias`),
  KEY `FKsnq8m3yj353mujw9c0iqrsjma` (`createdBy`),
  KEY `FK47m56tfdlpjqwgg79txgdt141` (`updatedBy`),
  CONSTRAINT `FK47m56tfdlpjqwgg79txgdt141` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKsnq8m3yj353mujw9c0iqrsjma` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Identity` (
  `sampleId` bigint(20) PRIMARY KEY,
  `internalName` varchar(255) NOT NULL,
  `externalName` varchar(255) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  UNIQUE KEY `UK_ew7ogw2mxhcs9d6ncgelgyh9t` (`internalName`),
  KEY `FKauqylg2sle5eudy0tqabtlmsb` (`createdBy`),
  KEY `FKa11fikh6ktu2cn1qbgudb2st6` (`sampleId`),
  KEY `FKa8c6e56hg9iucguhr0dcse62h` (`updatedBy`),
  CONSTRAINT `FKa11fikh6ktu2cn1qbgudb2st6` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `FKa8c6e56hg9iucguhr0dcse62h` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKauqylg2sle5eudy0tqabtlmsb` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Subproject` (
  `subprojectId` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectId` bigint(20) NOT NULL,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `priority` bit(1) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`subprojectId`),
  UNIQUE KEY `UK_n88wwxd7kv4q0m2xhfek9xl70` (`alias`),
  KEY `FK5mudbpqu96ccsmoldngfn7ulx` (`createdBy`),
  KEY `FKhb5p2460x4v7hd29wia24nnbu` (`projectId`),
  KEY `FKl477a3ed1xwaqx5k9hqu8naqi` (`updatedBy`),
  CONSTRAINT `FK5mudbpqu96ccsmoldngfn7ulx` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKhb5p2460x4v7hd29wia24nnbu` FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`),
  CONSTRAINT `FKl477a3ed1xwaqx5k9hqu8naqi` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleClass` (
  `sampleClassId` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `sampleCategory` varchar(255) NOT NULL,
  `suffix` varchar(5) DEFAULT NULL,
  `isStock` bit NOT NULL DEFAULT 0,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`sampleClassId`),
  UNIQUE KEY `UKdewdpl9hfwp6plc9gln8rtcx5` (`alias`,`sampleCategory`),
  KEY `FK30w1j1qqj9qoxgpopa0uq9jeh` (`createdBy`),
  KEY `FKoyjjomji7fvjrlgevecxqv12o` (`updatedBy`),
  CONSTRAINT `FK30w1j1qqj9qoxgpopa0uq9jeh` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKoyjjomji7fvjrlgevecxqv12o` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `QcPassedDetail` (
  `qcPassedDetailId` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `noteRequired` bit(1) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`qcPassedDetailId`),
  UNIQUE KEY `UKd5487vldy3xo0x7iw6vmspvpf` (`status`,`description`),
  KEY `FK82obpt4ig4g20eycits1ss1am` (`createdBy`),
  KEY `FK8xn9wkmnf09k06en6m91g5ks3` (`updatedBy`),
  CONSTRAINT `FK82obpt4ig4g20eycits1ss1am` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK8xn9wkmnf09k06en6m91g5ks3` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Institute` (
  `instituteId` bigint(20) PRIMARY KEY AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL UNIQUE,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  CONSTRAINT `institute_createUser_fkey` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `institute_updateUser_fkey` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Lab` (
  `labId` bigint(20) PRIMARY KEY AUTO_INCREMENT,
  `instituteId` bigint(20) NOT NULL,
  `alias` varchar(255) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  UNIQUE KEY `lab_institute-alias_uk` (`instituteId`,`alias`),
  CONSTRAINT `lab_institute_fkey` FOREIGN KEY (`instituteId`) REFERENCES `Institute` (`instituteId`),
  CONSTRAINT `lab_createUser_fkey` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `lab_updateUser_fkey` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleAdditionalInfo` (
  `sampleId` bigint(20) PRIMARY KEY,
  `sampleClassId` bigint(20) NOT NULL,
  `tissueOriginId` bigint(20) DEFAULT NULL,
  `tissueTypeId` bigint(20) DEFAULT NULL,
  `qcPassedDetailId` bigint(20) DEFAULT NULL,
  `subprojectId` bigint(20) DEFAULT NULL,
  `passageNumber` int(11) DEFAULT NULL,
  `timesReceived` int(11) DEFAULT NULL,
  `tubeNumber` int(11) DEFAULT NULL,
  `concentration` double DEFAULT NULL,
  `archived` bit(1) NOT NULL,
  `externalInstituteIdentifier` varchar(255) DEFAULT NULL,
  `labId` bigint(20) DEFAULT NULL,
  `parentId` bigint(20) DEFAULT NULL,
  `siblingNumber` int(11) DEFAULT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  KEY `FKeutn2473w3yr16khgalspuviw` (`createdBy`),
  KEY `FKa2t38wms0eer896xo4fw76tw0` (`qcPassedDetailId`),
  KEY `FKd4g1h1n50bflt9a2v2pgr0jn` (`sampleId`),
  KEY `FKbucvvrpgkejjwt5jb3bx2rnqy` (`sampleClassId`),
  KEY `FKlgx09pit706ehsyqq2tpe42do` (`subprojectId`),
  KEY `FK24aduvv5cljo3ggnt0s2cs1w3` (`tissueOriginId`),
  KEY `FKoulifnc7plonin8pbreiovb3x` (`tissueTypeId`),
  KEY `FKp8bvx3e7jsmnyw51toi7mq7cq` (`updatedBy`),
  CONSTRAINT `FK24aduvv5cljo3ggnt0s2cs1w3` FOREIGN KEY (`tissueOriginId`) REFERENCES `TissueOrigin` (`tissueOriginId`),
  CONSTRAINT `FKa2t38wms0eer896xo4fw76tw0` FOREIGN KEY (`qcPassedDetailId`) REFERENCES `QcPassedDetail` (`qcPassedDetailId`),
  CONSTRAINT `FKbucvvrpgkejjwt5jb3bx2rnqy` FOREIGN KEY (`sampleClassId`) REFERENCES `SampleClass` (`sampleClassId`),
  CONSTRAINT `FKd4g1h1n50bflt9a2v2pgr0jn` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `FKeutn2473w3yr16khgalspuviw` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKlgx09pit706ehsyqq2tpe42do` FOREIGN KEY (`subprojectId`) REFERENCES `Subproject` (`subprojectId`),
  CONSTRAINT `FKoulifnc7plonin8pbreiovb3x` FOREIGN KEY (`tissueTypeId`) REFERENCES `TissueType` (`tissueTypeId`),
  CONSTRAINT `sampleadditionalinfo_lab_fkey` FOREIGN KEY (`labId`) REFERENCES `Lab` (`labId`),
  CONSTRAINT `sampleadditionalinfo_parent_fkey` FOREIGN KEY (`parentId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `FKp8bvx3e7jsmnyw51toi7mq7cq` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE SampleAdditionalInfo ADD COLUMN `kitDescriptorId` BIGINT (20) DEFAULT NULL after subprojectId;
ALTER TABLE SampleAdditionalInfo ADD FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);


CREATE TABLE `TissueMaterial` (
  `tissueMaterialId` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`tissueMaterialId`),
  UNIQUE KEY `UK_6pr0m7xvv7g5ajmmv93mqvdg7` (`alias`),
  KEY `FKtrwn1w8po9spxnkex9rpgsn64` (`createdBy`),
  KEY `FK69r5v1ppgjw6jth6saekcmv96` (`updatedBy`),
  CONSTRAINT `FK69r5v1ppgjw6jth6saekcmv96` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKtrwn1w8po9spxnkex9rpgsn64` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SamplePurpose` (
  `samplePurposeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`samplePurposeId`),
  UNIQUE KEY `UK_t1fmado2v5jf9troedycvnxfv` (`alias`),
  KEY `FKcgjgyju8kvxgi1uaceewhtmbt` (`createdBy`),
  KEY `FKf50vooqtktimgba328whal3o0` (`updatedBy`),
  CONSTRAINT `FKcgjgyju8kvxgi1uaceewhtmbt` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKf50vooqtktimgba328whal3o0` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleGroup` (
  `sampleGroupId` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectId` bigint(20) NOT NULL,
  `subprojectId` bigint(20),
  `groupId` int(11) NOT NULL,
  `description` varchar(255) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`sampleGroupId`),
  UNIQUE KEY `UKhxm3cpjjq797dggaonl8lbq2a` (`projectId`,`groupId`),
  KEY `FKnn6082qvedk02e1046e8y107d` (`createdBy`),
  KEY `FKisowhu857cxk85o0s0fklyevx` (`updatedBy`),
  CONSTRAINT `FKaykeqkgvy3fgpq8it98acblmv` FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`),
  CONSTRAINT `FKsubproject` FOREIGN KEY (`subprojectId`) REFERENCES `Subproject` (`subprojectId`),
  CONSTRAINT `FKisowhu857cxk85o0s0fklyevx` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKnn6082qvedk02e1046e8y107d` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleAnalyte` (
  `sampleId` bigint(20) PRIMARY KEY,
  `samplePurposeId` bigint(20) DEFAULT NULL,
  `sampleGroupId` bigint(20) DEFAULT NULL,
  `tissueMaterialId` bigint(20) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `tubeId` varchar(255) DEFAULT NULL,
  `strStatus` varchar(50) NOT NULL DEFAULT 'NOT_SUBMITTED',
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  KEY `FKpras819b6p7vh12xbeovne8o0` (`createdBy`),
  KEY `FKe6n6a5x04km19m5376iaah9gy` (`sampleId`),
  KEY `FKmirq92ew3h3732cexgqdeyehk` (`sampleGroupId`),
  KEY `FK4ko5768dgvwrv1ueey5u3gkqe` (`samplePurposeId`),
  KEY `FKkh0wcve3c24usco99e544ftc7` (`tissueMaterialId`),
  KEY `FKprqyhv40bntjrf5l64mjdgl1j` (`updatedBy`),
  CONSTRAINT `FK4ko5768dgvwrv1ueey5u3gkqe` FOREIGN KEY (`samplePurposeId`) REFERENCES `SamplePurpose` (`samplePurposeId`),
  CONSTRAINT `FKe6n6a5x04km19m5376iaah9gy` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `FKkh0wcve3c24usco99e544ftc7` FOREIGN KEY (`tissueMaterialId`) REFERENCES `TissueMaterial` (`tissueMaterialId`),
  CONSTRAINT `FKmirq92ew3h3732cexgqdeyehk` FOREIGN KEY (`sampleGroupId`) REFERENCES `SampleGroup` (`sampleGroupId`),
  CONSTRAINT `FKpras819b6p7vh12xbeovne8o0` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKprqyhv40bntjrf5l64mjdgl1j` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleNumberPerProject` (
  `sampleNumberPerProjectId` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectId` bigint(20) NOT NULL,
  `highestSampleNumber` int(11) NOT NULL,
  `padding` int(11) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`sampleNumberPerProjectId`),
  UNIQUE KEY `UK_dw1vcaxddbxopw3imu0rxm1ww` (`projectId`),
  KEY `FKjxikp47dpisx3tr3vkxuknfeh` (`createdBy`),
  KEY `FKlgd3qd6d25aawdl1ldqvc1vxf` (`updatedBy`),
  CONSTRAINT `FKjxikp47dpisx3tr3vkxuknfeh` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKlgd3qd6d25aawdl1ldqvc1vxf` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKpbhtha4po9so0lup7x3sxge5p` FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleValidRelationship` (
  `sampleValidRelationshipId` bigint(20) NOT NULL AUTO_INCREMENT,
  `parentId` bigint(20) NOT NULL,
  `childId` bigint(20) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`sampleValidRelationshipId`),
  UNIQUE KEY `UK6h6c3shh0sluresucsxf5ixb7` (`parentId`,`childId`),
  KEY `FKk7dtvey4xjbrt9qdwkjl00wlb` (`childId`),
  KEY `FKfk3wsykea5rk3svf1n702eti0` (`createdBy`),
  KEY `FKb9uqsxsfb2fxnl8jjo8p5ifer` (`updatedBy`),
  CONSTRAINT `FK9tn7y9gmki3ygroc0fd3288vm` FOREIGN KEY (`parentId`) REFERENCES `SampleClass` (`sampleClassId`),
  CONSTRAINT `FKb9uqsxsfb2fxnl8jjo8p5ifer` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKfk3wsykea5rk3svf1n702eti0` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKk7dtvey4xjbrt9qdwkjl00wlb` FOREIGN KEY (`childId`) REFERENCES `SampleClass` (`sampleClassId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleTissue` (
  `sampleId` bigint(20) PRIMARY KEY,
  `cellularity` int,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  CONSTRAINT `sampleTissue_sample_fkey` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `sampleTissue_createUser_fkey` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `sampleTissue_updateUser_fkey` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `LibraryAdditionalInfo` (
  `libraryId` bigint(20) NOT NULL PRIMARY KEY,
  `tissueOriginId` bigint(20) NOT NULL,
  `tissueTypeId` bigint(20) NOT NULL,
  `sampleGroupId` bigint(20) DEFAULT NULL,
  `kitDescriptorId` bigint(20) DEFAULT NULL,
  `archived` bit(1) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  CONSTRAINT `libraryAdditionalInfo_tissueOrigin_fkey` FOREIGN KEY (`tissueOriginId`) REFERENCES `TissueOrigin` (`tissueOriginId`),
  CONSTRAINT `libraryAdditionalInfo_tissueType_fkey` FOREIGN KEY (`tissueTypeId`) REFERENCES `TissueType` (`tissueTypeId`),
  CONSTRAINT `libraryAdditionalInfo_sampleGroup_fkey` FOREIGN KEY (`sampleGroupId`) REFERENCES `SampleGroup` (`sampleGroupId`),
  CONSTRAINT `libraryAdditionalInfo_kitDescriptor_fkey` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`),
  CONSTRAINT `libraryAdditionalInfo_library_fkey` FOREIGN KEY (`libraryId`) REFERENCES `Library` (`libraryId`),
  CONSTRAINT `libraryAdditionalInfo_createUser_fkey` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `libraryAdditionalInfo_updateUser_fkey` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `LibraryPropagationRule` (
  `libraryPropagationRuleId` bigint(20) PRIMARY KEY AUTO_INCREMENT,
  `name` text NOT NULL,
  `sampleClassId` bigint(20) NOT NULL,
  `platformName` varchar(255) DEFAULT NULL,
  `paired` boolean DEFAULT NULL,
  `librarySelectionType` bigint(20) DEFAULT NULL,
  `libraryStrategyType` bigint(20) DEFAULT NULL,
  CONSTRAINT `FK_lpr_sampleClassId` FOREIGN KEY (`sampleClassId`) REFERENCES `SampleClass` (`sampleClassId`),
  CONSTRAINT `FK_lpr_selectiontype` FOREIGN KEY (`librarySelectionType`) REFERENCES `LibrarySelectionType` (`librarySelectionTypeId`),
  CONSTRAINT `FK_lpr_strategytype` FOREIGN KEY (`libraryStrategyType`) REFERENCES `LibraryStrategyType` (`libraryStrategyTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SequencingParameters` (
  `parametersId` bigint(20) PRIMARY KEY AUTO_INCREMENT,
  `name` text NOT NULL,
  `platformId` bigint(20) NOT NULL,
  `xpath` varchar(1024) DEFAULT NULL,
  `readLength` int DEFAULT NULL,
  `paired` boolean DEFAULT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  CONSTRAINT `sequencingParameters_createUser_fkey` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `sequencingParameters_updateUser_fkey` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `parameter_platformId_fkey` FOREIGN KEY (`platformId`) REFERENCES `Platform` (`platformId`)
) ENGINE=InnoDB CHARSET=utf8;

INSERT INTO `SequencingParameters` (`platformId`, `name`, `createdBy`, `updatedBy`, `creationDate`, `lastUpdated`, `readLength`, `paired`, `xpath`)
VALUES
	(16,'v3 1×101', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 101, FALSE, '//Flowcell="HiSeq Flow Cell v3" and count(//Read[@NumCycles=101]) = 1 or //Flowcell="HiSeq Flow Cell" and (//Read1 = 100 or //Read1 = 101) and not //Read2'),
	(16,'v3 1×51', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 51, FALSE, '//Flowcell="HiSeq Flow Cell v3" and count(//Read[@NumCycles=51]) = 1 or //Flowcell="HiSeq Flow Cell" and (//Read1 = 50 or //Read1 = 51) and not //Read2'),
	(16,'v3 2×101', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 101, TRUE, '//Flowcell="HiSeq Flow Cell v3" and count(//Read[@NumCycles=101]) = 2 or //Flowcell="HiSeq Flow Cell" and (//Read1 = 100 or //Read1 = 101) and //Read2'),
	(16,'v3 2×51', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 51, TRUE, '//Flowcell="HiSeq Flow Cell v3" and count(//Read[@NumCycles=51]) = 2 or //Flowcell="HiSeq Flow Cell" and (//Read1 = 50 or //Read1 = 51) and //Read2'),
	(16,'v4 1×136', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 136, FALSE, '//Flowcell="HiSeq Flow Cell v4" and count(//Read[@NumCycles=136]) = 2'),
	(16,'v4 2×126', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 126, FALSE, '//Flowcell="HiSeq Flow Cell v4" and count(//Read[@NumCycles=126]) = 2'),
	(16,'Rapid Run 1×101', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 101, FALSE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=101]) = 1'),
	(16,'Rapid Run 1×151', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 151, FALSE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=151]) = 1'),
	(16,'Rapid Run 1×51', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 51, FALSE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=51]) = 1'),
	(16,'Rapid Run 2×101', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 101, TRUE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=101]) = 2'),
	(16,'Rapid Run 2×151', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 151, TRUE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=151]) = 2'),
	(16,'Rapid Run 2×51', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 51, TRUE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=51]) = 2'),
	(24,'1×300', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 300, FALSE, 'count(//RunInfoRead[@NumCycles=300]) = 1'),
	(24,'1×50', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 50, FALSE, 'count(//RunInfoRead[@NumCycles=50]) = 1'),
	(24,'1×500', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 500, FALSE, 'count(//RunInfoRead[@NumCycles=500]) = 1'),
	(24,'2×101', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 101, TRUE, 'count(//RunInfoRead[@NumCycles=101]) = 2'),
	(24,'2×151', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 151, TRUE, 'count(//RunInfoRead[@NumCycles=151]) = 2'),
	(24,'2×250', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 250, TRUE, 'count(//RunInfoRead[@NumCycles=250]) = 2'),
	(24,'2×26', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 26, TRUE, 'count(//RunInfoRead[@NumCycles=26]) = 2'),
	(24,'2×36', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 36, TRUE, 'count(//RunInfoRead[@NumCycles=36]) = 2'),
	(24,'300×200', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 300, FALSE, 'count(//RunInfoRead[@NumCycles=200]) > 1'),
	(25,'v3 1×101', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 101, FALSE, '//Flowcell="HiSeq Flow Cell v3" and count(//Read[@NumCycles=101]) = 1 or //Flowcell="HiSeq Flow Cell" and (//Read1 = 100 or //Read1 = 101) and not //Read2'),
	(25,'v3 1×51', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 51, FALSE, '//Flowcell="HiSeq Flow Cell v3" and count(//Read[@NumCycles=51]) = 1 or //Flowcell="HiSeq Flow Cell" and (//Read1 = 50 or //Read1 = 51) and not //Read2'),
	(25,'v3 2×101', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 101, TRUE, '//Flowcell="HiSeq Flow Cell v3" and count(//Read[@NumCycles=101]) = 2 or //Flowcell="HiSeq Flow Cell" and (//Read1 = 100 or //Read1 = 101) and //Read2'),
	(25,'v3 2×51', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 51, TRUE, '//Flowcell="HiSeq Flow Cell v3" and count(//Read[@NumCycles=51]) = 2 or //Flowcell="HiSeq Flow Cell" and (//Read1 = 50 or //Read1 = 51) and //Read2'),
	(25,'v4 1×136', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 136, FALSE, '//Flowcell="HiSeq Flow Cell v4" and count(//Read[@NumCycles=136]) = 2'),
	(25,'v4 2×126', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 126, TRUE, '//Flowcell="HiSeq Flow Cell v4" and count(//Read[@NumCycles=126]) = 2'),
	(25,'Rapid Run 1×101', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 101, FALSE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=101]) = 1'),
	(25,'Rapid Run 1×151', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 151, FALSE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=151]) = 1'),
	(25,'Rapid Run 1×51', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 51, FALSE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=51]) = 1'),
	(25,'Rapid Run 2×101', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 101, TRUE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=101]) = 2'),
	(25,'Rapid Run 2×151', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 151, TRUE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=151]) = 2'),
	(25,'Rapid Run 2×51', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 51, TRUE, 'starts-with(//Flowcell, "HiSeq Rapid Flow Cell") and count(//Read[@NumCycles=51]) = 2');

CREATE TABLE `PoolOrder` (
  `poolOrderId` bigint(20) PRIMARY KEY AUTO_INCREMENT,
  `poolId` bigint(20) NOT NULL,
  `partitions` int NOT NULL,
  `parametersId` bigint(20),
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  CONSTRAINT `order_poolId_fkey` FOREIGN KEY (`poolId`) REFERENCES `Pool` (`poolId`),
  CONSTRAINT `order_parametersId_fkey` FOREIGN KEY (`parametersId`) REFERENCES `SequencingParameters` (`parametersId`),
  CONSTRAINT `order_createUser_fkey` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `order_updateUser_fkey` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Run ADD COLUMN sequencingParameters_parametersId bigint(20) DEFAULT NULL;
ALTER TABLE Run ADD FOREIGN KEY (sequencingParameters_parametersId) REFERENCES SequencingParameters (parametersId);
