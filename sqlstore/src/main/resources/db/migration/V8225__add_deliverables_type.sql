CREATE TABLE Deliverable (
  deliverableId bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY(deliverableId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Default values for stage and prod
INSERT INTO Deliverable(deliverableId, name) VALUES
(1, "fastq"),
(2, "full pipeline"),
(3, "clinical report");