CREATE TABLE Project_Deliverable (
  projectId bigint NOT NULL,
  deliverableId bigint NOT NULL,
  PRIMARY KEY (projectId, deliverableId),
  CONSTRAINT fk_deliverable_project FOREIGN KEY (projectId) REFERENCES Project (projectId),
  CONSTRAINT fk_project_deliverable FOREIGN KEY (deliverableId) REFERENCES Deliverable (deliverableId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO Project_Deliverable (projectId, deliverableId)
SELECT projectId, deliverableId
FROM Project
WHERE deliverableId IS NOT NULL;

ALTER TABLE Project
  DROP FOREIGN KEY fk_project_deliverableId,
  DROP COLUMN deliverableId;
