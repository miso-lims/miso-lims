ALTER TABLE `LibraryDilution` ADD COLUMN `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
UPDATE `LibraryDilution` SET `created` = `creationDate`;
