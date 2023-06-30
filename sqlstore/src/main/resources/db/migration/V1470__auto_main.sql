-- project_assay_join_table
CREATE TABLE Project_Assay (
  projectId bigint NOT NULL,
  assayId bigint NOT NULL,
  PRIMARY KEY (projectId, assayId),
  CONSTRAINT fk_projectAssay_project FOREIGN KEY (projectId) REFERENCES Project (projectId),
  CONSTRAINT fk_projectAssay_assay FOREIGN KEY (assayId) REFERENCES Assay (assayId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- project_additional_details_field
ALTER TABLE Project ADD additionalDetails TEXT DEFAULT NULL;

