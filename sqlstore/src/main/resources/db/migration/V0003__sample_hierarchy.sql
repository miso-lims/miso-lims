CREATE TABLE `SampleAnalyte` (
  `sampleAnalyteId` bigint(20) NOT NULL AUTO_INCREMENT,
  `aliquotNumber` int(11) DEFAULT NULL,
  `purpose` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `stockNumber` int(11) DEFAULT NULL,
  `tubeId` varchar(255) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `lastUpdated` datetime DEFAULT NULL,
  PRIMARY KEY (`sampleAnalyteId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Switch Sample table to InnoDB to permit foreign key constraints.
ALTER TABLE Sample ENGINE = InnoDB ROW_FORMAT = DEFAULT;
ALTER TABLE Sample ADD COLUMN `sampleAnalyteId` BIGINT (20) DEFAULT NULL after taxonIdentifier;
ALTER TABLE Sample ADD FOREIGN KEY (sampleAnalyteId) REFERENCES SampleAnalyte (sampleAnalyteId);

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

ALTER TABLE Sample ADD COLUMN `identityId` BIGINT (20) DEFAULT NULL after sampleAnalyteId;
ALTER TABLE Sample ADD FOREIGN KEY (identityId) REFERENCES Identity (identityId);
