CREATE TABLE StorageLocation_ServiceRecord (
  recordId bigint(20) NOT NULL,
  locationId bigint(20) NOT NULL,
  PRIMARY KEY (recordId, locationId),
  CONSTRAINT fk_storageLocationServiceRecord_storageLocation FOREIGN KEY (locationId) REFERENCES StorageLocation (locationId),
  CONSTRAINT fk_storageLocationServiceRecord_serviceRecord FOREIGN KEY (recordId) REFERENCES ServiceRecord (recordId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;