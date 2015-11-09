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