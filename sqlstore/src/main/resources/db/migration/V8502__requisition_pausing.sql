CREATE TABLE RequisitionPause (
  pauseId bigint NOT NULL AUTO_INCREMENT,
  requisitionId bigint NOT NULL,
  startDate DATE NOT NULL,
  endDate DATE,
  reason varchar(255),
  PRIMARY KEY (pauseId),
  CONSTRAINT fk_requisitionPause_requisition FOREIGN KEY (requisitionId) REFERENCES Requisition (requisitionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
