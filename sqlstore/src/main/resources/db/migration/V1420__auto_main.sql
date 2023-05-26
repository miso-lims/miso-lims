-- instrument_serviceRecord
-- Create table for instrument ServiceRecords

CREATE TABLE Instrument_ServiceRecord (
  recordId bigint NOT NULL,
  instrumentId bigint NOT NULL,
  PRIMARY KEY (recordId, instrumentId),
  CONSTRAINT fk_instrumentServiceRecord_instrument FOREIGN KEY (instrumentId) REFERENCES Instrument (instrumentId),
  CONSTRAINT fk_instrumentServiceRecord_ServiceRecord FOREIGN KEY (recordId) REFERENCES ServiceRecord (recordId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- 

-- add records for existing service records
INSERT INTO Instrument_ServiceRecord(recordId, instrumentId) 
SELECT recordId, instrumentId FROM ServiceRecord;
--

-- drop foeign key from ServiceRecord
ALTER TABLE ServiceRecord DROP FOREIGN KEY `fk_serviceRecord_instrument`;

-- drop instrumentId columns
ALTER TABLE ServiceRecord DROP COLUMN instrumentId;



-- supplemental_samples
CREATE TABLE Requisition_SupplementalSample (
  requisitionId bigint NOT NULL,
  sampleId bigint NOT NULL,
  PRIMARY KEY (requisitionId, sampleId),
  FOREIGN KEY fk_supplementalSample_requisition (requisitionId) REFERENCES Requisition (requisitionId),
  FOREIGN KEY fk_requisition_supplementalSample (sampleId) REFERENCES Sample (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

