DROP TABLE IF EXISTS `LibraryTemplate_Project`;

CREATE TABLE `LibraryTemplate_Project` (
  `libraryTemplateId` bigint(20) NOT NULL,
  `projectId` bigint(20) NOT NULL,
  PRIMARY KEY (`libraryTemplateId`,`projectId`),
  KEY `projectId` (`projectId`),
  CONSTRAINT `LibraryTemplate_Project_fk_1` 
   FOREIGN KEY (`libraryTemplateId`) REFERENCES `LibraryTemplate` (`libraryTemplateId`),
  CONSTRAINT `LibraryTemplate_Project_fk_2` 
   FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `LibraryTemplate_Project` (`libraryTemplateId`, `projectId`) 
  SELECT `libraryTemplateId`, `projectId` FROM `LibraryTemplate`;

ALTER TABLE `LibraryTemplate` DROP FOREIGN KEY `fk_libraryTemplate_project`;  
ALTER TABLE `LibraryTemplate` DROP COLUMN `projectId`;
