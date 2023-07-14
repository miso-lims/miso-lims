-- add_deliverables_type
CREATE TABLE Deliverable (
  deliverableId bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY(deliverableId),
  CONSTRAINT UQ_deliverable_name UNIQUE(name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE Project ADD deliverableId bigint DEFAULT NULL;

ALTER TABLE Project ADD CONSTRAINT fk_project_deliverableId FOREIGN KEY (deliverableId) REFERENCES Deliverable (deliverableId);

