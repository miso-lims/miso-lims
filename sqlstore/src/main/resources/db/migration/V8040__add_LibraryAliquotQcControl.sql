
CREATE TABLE LibraryAliquotQcControl (
  qcControlId bigint PRIMARY KEY AUTO_INCREMENT,
  qcId bigint NOT NULL,
  controlId bigint NOT NULL,
  lot varchar(50) NOT NULL,
  qcPassed BOOLEAN NOT NULL,
  CONSTRAINT fk_libraryAliquotQcControl_qc FOREIGN KEY (qcId) REFERENCES LibraryAliquotQC (qcId),
  CONSTRAINT fk_libraryAliquotQcControl_control FOREIGN KEY (controlId) REFERENCES QcControl (controlId)
) Engine=InnoDB DEFAULT CHARSET=utf8mb4;