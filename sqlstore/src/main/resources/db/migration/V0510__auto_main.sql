-- project_templates

DROP TABLE IF EXISTS LibraryTemplate_Index1;
DROP TABLE IF EXISTS LibraryTemplate_Index2;
DROP TABLE IF EXISTS DetailedLibraryTemplate;
DROP TABLE IF EXISTS LibraryTemplate;

CREATE TABLE LibraryTemplate (
  libraryTemplateId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(255) NOT NULL,
  projectId bigint NOT NULL,
  platformType varchar(255),
  libraryTypeId bigint,
  librarySelectionTypeId bigint,
  libraryStrategyTypeId bigint,
  kitDescriptorId bigint,
  indexFamilyId bigint,
  PRIMARY KEY (libraryTemplateId),
  CONSTRAINT fk_libraryTemplate_project FOREIGN KEY (projectId) REFERENCES Project (projectId),
  CONSTRAINT fk_libraryTemplate_libraryType FOREIGN KEY (libraryTypeId) REFERENCES LibraryType (libraryTypeId),
  CONSTRAINT fk_libraryTemplate_selection FOREIGN KEY (librarySelectionTypeId) REFERENCES LibrarySelectionType (librarySelectionTypeId),
  CONSTRAINT fk_libraryTemplate_strategy FOREIGN KEY (libraryStrategyTypeId) REFERENCES LibraryStrategyType (libraryStrategyTypeId),
  CONSTRAINT fk_libraryTemplate_kitDescriptor FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId),
  CONSTRAINT fk_libraryTemplate_indexFamily FOREIGN KEY (indexFamilyId) REFERENCES IndexFamily (indexFamilyId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE DetailedLibraryTemplate (
  libraryTemplateId bigint NOT NULL,
  libraryDesignId bigint,
  libraryDesignCodeId bigint,
  PRIMARY KEY (libraryTemplateId),
  CONSTRAINT fk_detailedLibraryTemplate_libraryTemplate FOREIGN KEY (libraryTemplateId) REFERENCES LibraryTemplate (libraryTemplateId),
  CONSTRAINT fk_detailedLibraryTemplate_design FOREIGN KEY (libraryDesignId) REFERENCES LibraryDesign (libraryDesignId),
  CONSTRAINT fk_detailedLibraryTemplate_designCode FOREIGN KEY (libraryDesignCodeId) REFERENCES LibraryDesignCode (libraryDesignCodeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE LibraryTemplate_Index1 (
  libraryTemplateId bigint NOT NULL,
  position varchar(3) NOT NULL,
  indexId bigint NOT NULL,
  PRIMARY KEY (libraryTemplateId, position),
  CONSTRAINT fk_libraryTemplateIndex1_libraryTemplate FOREIGN KEY (libraryTemplateId) REFERENCES LibraryTemplate (libraryTemplateId),
  CONSTRAINT fk_libraryTemplateIndex1_index FOREIGN KEY (indexId) REFERENCES Indices (indexId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE LibraryTemplate_Index2 (
  libraryTemplateId bigint NOT NULL,
  position varchar(3) NOT NULL,
  indexId bigint NOT NULL,
  PRIMARY KEY (libraryTemplateId, position),
  CONSTRAINT fk_libraryTemplateIndex2_libraryTemplate FOREIGN KEY (libraryTemplateId) REFERENCES LibraryTemplate (libraryTemplateId),
  CONSTRAINT fk_libraryTemplateIndex2_index FOREIGN KEY (indexId) REFERENCES Indices (indexId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- index_name

ALTER TABLE Indices MODIFY COLUMN name varchar(24);


