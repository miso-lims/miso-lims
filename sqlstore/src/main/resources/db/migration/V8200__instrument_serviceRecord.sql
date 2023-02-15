-- Create table for instrument ServiceRecords

CREATE TABLE Instrument_ServiceRecord (
  recordId bigint(20) NOT NULL,
  instrumentId bigint(20) NOT NULL,
  PRIMARY KEY (recordId, instrumentId),
  CONSTRAINT fk_instrumentServiceRecord_instrument FOREIGN KEY (instrumentId) REFERENCES Instrument (instrumentId)
  CONSTRAINT fk_instrumentServiceRecord_ServiceRecord FOREIGN KEY (recordId) REFERENCES ServiceRecord (recordId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 

-- add records for existing service records
INSERT INTO Instrument_ServiceRecord(recordId, instrumentId) 
SELECT recordId, instrumentId FROM ServiceRecord;
--

-- drop foeign key from ServiceRecord
ALTER TABLE ServiceRecord DROP FOREIGN KEY `fk_serviceRecord_instrument`;

-- drop instrumentId columns
ALTER TABLE ServiceRecord DROP COLUMN instrumentId;


