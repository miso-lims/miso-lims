CREATE TABLE WorksetCategory (
  categoryId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(20),
  PRIMARY KEY (categoryId),
  CONSTRAINT uk_worksetCategory_alias UNIQUE (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE WorksetStage (
  stageId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(20),
  PRIMARY KEY (stageId),
  CONSTRAINT uk_worksetStage_alias UNIQUE (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Workset
  ADD COLUMN categoryId bigint(20),
  ADD COLUMN stageId bigint(20),
  ADD CONSTRAINT fk_workset_category FOREIGN KEY (categoryId) REFERENCES WorksetCategory (categoryId),
  ADD CONSTRAINT fk_workset_stage FOREIGN KEY (stageId) REFERENCES WorksetStage (stageId);
