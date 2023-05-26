ALTER TABLE SampleAnalyte DROP FOREIGN KEY `FKmirq92ew3h3732cexgqdeyehk`;
ALTER TABLE SampleAnalyte DROP COLUMN `sampleGroupId`;
ALTER TABLE SampleAnalyte ADD COLUMN `groupId` int DEFAULT NULL;
ALTER TABLE SampleAnalyte ADD COLUMN `groupDescription` VARCHAR(255) DEFAULT NULL;
ALTER TABLE LibraryAdditionalInfo DROP FOREIGN KEY `libraryAdditionalInfo_sampleGroup_fkey`;
ALTER TABLE LibraryAdditionalInfo DROP COLUMN `sampleGroupId`;
ALTER TABLE LibraryAdditionalInfo ADD COLUMN `groupId` int DEFAULT NULL;
ALTER TABLE LibraryAdditionalInfo ADD COLUMN `groupDescription` VARCHAR(255) DEFAULT NULL;
