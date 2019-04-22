-- Switch tables to InnoDB to permit foreign key constraints.
-- StartNoTest
ALTER TABLE LibraryDilution ENGINE = InnoDB ROW_FORMAT = DEFAULT;
-- EndNoTest

CREATE TABLE `TargetedResequencing` (
  `targetedResequencingId` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `kitDescriptorId` bigint(20) NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`targetedResequencingId`),
  UNIQUE KEY `UK_TargetedResequencing_a_kdi` (`alias`,`kitDescriptorId`),
  KEY `FK_TargetedResequencing_cb` (`createdBy`),
  KEY `FK_TargetedResequencing_ub` (`updatedBy`),
  CONSTRAINT `FK_TargetedResequencing_kdi` FOREIGN KEY (`kitDescriptorId`) REFERENCES `KitDescriptor` (`kitDescriptorId`),
  CONSTRAINT `FK_TargetedResequencing_cb` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_TargetedResequencing_ub` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE LibraryDilution ADD COLUMN `targetedResequencingId` BIGINT (20) DEFAULT NULL after securityProfile_profileId;
ALTER TABLE LibraryDilution ADD FOREIGN KEY (targetedResequencingId) REFERENCES TargetedResequencing (targetedResequencingId);