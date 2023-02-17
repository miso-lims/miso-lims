CREATE TABLE Requisition_SupplementalSample (
  requisitionId bigint(20) NOT NULL,
  sampleId bigint(20) NOT NULL,
  PRIMARY KEY (requisitionId, sampleId),
  FOREIGN KEY fk_supplementalSample_requisition (requisitionId) REFERENCES Requisition (requisitionId),
  FOREIGN KEY fk_requisition_supplementalSample (sampleId) REFERENCES Sample (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
