
-- Switch tables to InnoDB to permit foreign key constraints.
ALTER TABLE Sample ENGINE = InnoDB ROW_FORMAT = DEFAULT;
ALTER TABLE Project ENGINE = InnoDB ROW_FORMAT = DEFAULT;


ALTER TABLE User ENGINE = InnoDB ROW_FORMAT = DEFAULT;

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
  `identityId` bigint(20) NOT NULL AUTO_INCREMENT,
  `sampleId` bigint(20) NOT NULL,
  `internalName` varchar(255) NOT NULL,
  `externalName` varchar(255) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`identityId`),
  UNIQUE KEY `UK_ew7ogw2mxhcs9d6ncgelgyh9t` (`internalName`),
  KEY `FKauqylg2sle5eudy0tqabtlmsb` (`createdBy`),
  KEY `FKa11fikh6ktu2cn1qbgudb2st6` (`sampleId`),
  KEY `FKa8c6e56hg9iucguhr0dcse62h` (`updatedBy`),
  CONSTRAINT `FKa11fikh6ktu2cn1qbgudb2st6` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`),
  CONSTRAINT `FKa8c6e56hg9iucguhr0dcse62h` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKauqylg2sle5eudy0tqabtlmsb` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Sample ADD COLUMN `identityId` BIGINT (20) DEFAULT NULL after taxonIdentifier;
ALTER TABLE Sample ADD FOREIGN KEY (identityId) REFERENCES Identity (identityId);

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

CREATE TABLE `SampleAdditionalInfo` (
  `sampleAdditionalInfoId` bigint(20) NOT NULL,
  `sampleId` bigint(20) NOT NULL,
  `sampleClassId` bigint(20) NOT NULL,
  `tissueOriginId` bigint(20) DEFAULT NULL,
  `tissueTypeId` bigint(20) DEFAULT NULL,
  `qcPassedDetailId` bigint(20) DEFAULT NULL,
  `subprojectId` bigint(20) DEFAULT NULL,
  `passageNumber` int(11) DEFAULT NULL,
  `timesReceived` int(11) DEFAULT NULL,
  `tubeNumber` int(11) DEFAULT NULL,
  `volume` double DEFAULT NULL,
  `concentration` double DEFAULT NULL,
  `archived` bit(1) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`sampleAdditionalInfoId`),
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
  CONSTRAINT `FKp8bvx3e7jsmnyw51toi7mq7cq` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  CONSTRAINT `FKisowhu857cxk85o0s0fklyevx` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FKnn6082qvedk02e1046e8y107d` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `SampleAnalyte` (
  `sampleAnalyteId` bigint(20) NOT NULL,
  `sampleId` bigint(20) NOT NULL,
  `samplePurposeId` bigint(20) DEFAULT NULL,
  `sampleGroupId` bigint(20) DEFAULT NULL,
  `tissueMaterialId` bigint(20) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `tubeId` varchar(255) DEFAULT NULL,
  `stockNumber` int(11) DEFAULT NULL,
  `aliquotNumber` int(11) DEFAULT NULL,
  `createdBy` bigint(20) NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`sampleAnalyteId`),
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


ALTER TABLE Sample ADD COLUMN `sampleAnalyteId` BIGINT (20) DEFAULT NULL after identityId;
ALTER TABLE Sample ADD FOREIGN KEY (sampleAnalyteId) REFERENCES SampleAnalyte (sampleAnalyteId);
