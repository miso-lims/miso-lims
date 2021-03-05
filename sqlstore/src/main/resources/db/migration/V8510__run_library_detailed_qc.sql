CREATE TABLE RunLibraryQcStatus (
  statusId bigint(20) NOT NULL AUTO_INCREMENT,
  description varchar(50) NOT NULL,
  qcPassed BOOLEAN,
  PRIMARY KEY (statusId),
  CONSTRAINT uk_runLibraryQcStatus_description UNIQUE (description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO RunLibraryQcStatus (statusId, description, qcPassed) VALUES
(1, 'Passed', TRUE),
(2, 'Failed', FALSE);

ALTER TABLE Run_Partition_LibraryAliquot
  ADD COLUMN statusId bigint(20),
  ADD CONSTRAINT fk_runAliquot_status FOREIGN KEY (statusId) REFERENCES RunLibraryQcStatus (statusId);

UPDATE Run_Partition_LibraryAliquot
SET statusId = 1
WHERE qcPassed = TRUE;

UPDATE Run_Partition_LibraryAliquot
SET statusId = 2
WHERE qcPassed = FALSE;

ALTER TABLE Run_Partition_LibraryAliquot DROP COLUMN qcPassed;
