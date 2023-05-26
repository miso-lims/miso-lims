-- worksets

DROP TABLE IF EXISTS Workset_Sample;
DROP TABLE IF EXISTS Workset_Library;
DROP TABLE IF EXISTS Workset_Dilution;
DROP TABLE IF EXISTS Workset;

CREATE TABLE Workset (
  worksetId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(100) NOT NULL,
  description varchar(255),
  creator bigint NOT NULL,
  created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastModifier bigint NOT NULL,
  lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (worksetId),
  UNIQUE KEY uk_workset_alias (alias),
  CONSTRAINT fk_workset_creator FOREIGN KEY (creator) REFERENCES User (userId),
  CONSTRAINT fk_workset_modifier FOREIGN KEY (lastModifier) REFERENCES User (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Workset_Sample (
  worksetId bigint NOT NULL,
  sampleId bigint NOT NULL,
  PRIMARY KEY (worksetId, sampleId),
  CONSTRAINT fk_sample_workset FOREIGN KEY (worksetId) REFERENCES Workset (worksetId),
  CONSTRAINT fk_workset_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Workset_Library (
  worksetId bigint NOT NULL,
  libraryId bigint NOT NULL,
  PRIMARY KEY (worksetId, libraryId),
  CONSTRAINT fk_library_workset FOREIGN KEY (worksetId) REFERENCES Workset (worksetId),
  CONSTRAINT fk_workset_library FOREIGN KEY (libraryId) REFERENCES Library (libraryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Workset_Dilution (
  worksetId bigint NOT NULL,
  dilutionId bigint NOT NULL,
  PRIMARY KEY (worksetId, dilutionId),
  CONSTRAINT fk_dilution_workset FOREIGN KEY (worksetId) REFERENCES Workset (worksetId),
  CONSTRAINT fk_workset_dilution FOREIGN KEY (dilutionId) REFERENCES LibraryDilution (dilutionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- serviceRecord

ALTER TABLE ServiceRecord MODIFY servicedBy varchar(30) DEFAULT NULL;


-- container_description

ALTER TABLE SequencerPartitionContainer ADD COLUMN description varchar(255);


-- LibraryTemplate_Multiple_Projects

DROP TABLE IF EXISTS `LibraryTemplate_Project`;

CREATE TABLE `LibraryTemplate_Project` (
  `libraryTemplateId` bigint NOT NULL,
  `projectId` bigint NOT NULL,
  PRIMARY KEY (`libraryTemplateId`,`projectId`),
  KEY `projectId` (`projectId`),
  CONSTRAINT `LibraryTemplate_Project_fk_1` 
   FOREIGN KEY (`libraryTemplateId`) REFERENCES `LibraryTemplate` (`libraryTemplateId`),
  CONSTRAINT `LibraryTemplate_Project_fk_2` 
   FOREIGN KEY (`projectId`) REFERENCES `Project` (`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `LibraryTemplate_Project` (`libraryTemplateId`, `projectId`) 
  SELECT `libraryTemplateId`, `projectId` FROM `LibraryTemplate`;

ALTER TABLE `LibraryTemplate` DROP FOREIGN KEY `fk_libraryTemplate_project`;  
ALTER TABLE `LibraryTemplate` DROP COLUMN `projectId`;


