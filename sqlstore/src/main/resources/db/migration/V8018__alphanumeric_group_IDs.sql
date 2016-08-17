ALTER TABLE `SampleAdditionalInfo` CHANGE COLUMN `groupId` `groupId` VARCHAR(10) DEFAULT NULL;
ALTER TABLE `LibraryAdditionalInfo` DROP COLUMN `groupId`;
ALTER TABLE `LibraryAdditionalInfo` DROP COLUMN `groupDescription`;