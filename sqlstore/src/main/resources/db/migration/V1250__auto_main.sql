-- run_library_detailed_qc
CREATE TABLE RunLibraryQcStatus (
  statusId bigint NOT NULL AUTO_INCREMENT,
  description varchar(50) NOT NULL,
  qcPassed BOOLEAN,
  PRIMARY KEY (statusId),
  CONSTRAINT uk_runLibraryQcStatus_description UNIQUE (description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO RunLibraryQcStatus (statusId, description, qcPassed) VALUES
(1, 'Passed', TRUE),
(2, 'Failed', FALSE);

ALTER TABLE Run_Partition_LibraryAliquot
  ADD COLUMN statusId bigint,
  ADD CONSTRAINT fk_runAliquot_status FOREIGN KEY (statusId) REFERENCES RunLibraryQcStatus (statusId);

UPDATE Run_Partition_LibraryAliquot
SET statusId = 1
WHERE qcPassed = TRUE;

UPDATE Run_Partition_LibraryAliquot
SET statusId = 2
WHERE qcPassed = FALSE;

ALTER TABLE Run_Partition_LibraryAliquot DROP COLUMN qcPassed;

-- workset_category
CREATE TABLE WorksetCategory (
  categoryId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(20),
  PRIMARY KEY (categoryId),
  CONSTRAINT uk_worksetCategory_alias UNIQUE (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE WorksetStage (
  stageId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(20),
  PRIMARY KEY (stageId),
  CONSTRAINT uk_worksetStage_alias UNIQUE (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE Workset
  ADD COLUMN categoryId bigint,
  ADD COLUMN stageId bigint,
  ADD CONSTRAINT fk_workset_category FOREIGN KEY (categoryId) REFERENCES WorksetCategory (categoryId),
  ADD CONSTRAINT fk_workset_stage FOREIGN KEY (stageId) REFERENCES WorksetStage (stageId);

-- instrument_workstations
ALTER TABLE Workstation ADD COLUMN identificationBarcode varchar(255);
ALTER TABLE Instrument
  ADD COLUMN identificationBarcode varchar(255),
  ADD COLUMN workstationId bigint,
  ADD CONSTRAINT fk_instrument_workstation FOREIGN KEY (workstationId) REFERENCES Workstation (workstationId);

