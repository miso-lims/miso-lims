ALTER TABLE Library ADD COLUMN requisitionId bigint;

CREATE TABLE Requisition_SupplementalLibrary (
  requisitionId bigint NOT NULL,
  libraryId bigint NOT NULL,
  PRIMARY KEY (requisitionId, libraryId),
  FOREIGN KEY fk_supplementalLibrary_requisition (requisitionId) REFERENCES Requisition (requisitionId),
  FOREIGN KEY fk_requisition_supplementalLibrary (libraryId) REFERENCES Library (libraryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
