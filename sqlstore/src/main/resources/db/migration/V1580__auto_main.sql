-- multi-assay_req
CREATE TABLE Requisition_Assay (
  requisitionId bigint NOT NULL,
  assayId bigint NOT NULL,
  PRIMARY KEY (requisitionId, assayId),
  CONSTRAINT fk_requisitionAssay_requisition FOREIGN KEY (requisitionId) REFERENCES Requisition (requisitionId),
  CONSTRAINT fk_requisitionAssay_assay FOREIGN KEY (assayId) REFERENCES Assay (assayId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO Requisition_Assay (requisitionId, assayId)
SELECT requisitionId, assayId FROM Requisition
WHERE assayId IS NOT NULL;

ALTER TABLE Requisition
  DROP FOREIGN KEY fk_requisition_assay,
  DROP COLUMN assayId;

-- Fix missing constraints
UPDATE Library SET requisitionId = NULL
  WHERE requisitionId IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM Requisition WHERE requisitionId = Library.requisitionId);

ALTER TABLE Library
  ADD CONSTRAINT fk_library_requisition FOREIGN KEY (requisitionId) REFERENCES Requisition (requisitionId);

ALTER TABLE RequisitionChangeLog
  DROP FOREIGN KEY fk_requisitionChangeLog_requisition;
ALTER TABLE RequisitionChangeLog
  ADD CONSTRAINT fk_requisitionChangeLog_requisition FOREIGN KEY (requisitionId) REFERENCES Requisition (requisitionId) ON DELETE CASCADE;

