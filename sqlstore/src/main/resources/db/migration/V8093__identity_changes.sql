ALTER TABLE `Identity` DROP COLUMN internalName;

CREATE TABLE `Identity_ExternalName` (
  `sampleId` BIGINT(20) NOT NULL,
  `externalName` VARCHAR(255) NOT NULL,
  CONSTRAINT `Sample_sampleId` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- do something here to move externalNames from Identity column to this newly-created table, then...

ALTER TABLE `Identity` DROP COLUMN externalName;