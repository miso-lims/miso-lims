-- storageLocation_ServiceRecord
CREATE TABLE StorageLocation_ServiceRecord (
  recordId bigint NOT NULL,
  locationId bigint NOT NULL,
  PRIMARY KEY (recordId, locationId),
  CONSTRAINT fk_storageLocationServiceRecord_serviceRecord FOREIGN KEY (recordId) REFERENCES ServiceRecord (recordId),
    CONSTRAINT fk_storageLocationServiceRecord_storageLocation FOREIGN KEY (locationId) REFERENCES StorageLocation (locationId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- requisition_stop_reason
ALTER TABLE Requisition ADD COLUMN stopReason varchar(255);
UPDATE Requisition SET stopReason = 'Unspecified' WHERE stopped = TRUE;

