-- serviceboards

-- Create table for instrument ServiceRecords
CREATE TABLE Instrument_ServiceRecord (
  recordId bigint(20) NOT NULL AUTO_INCREMENT,
  instrumentId bigint(20) NOT NULL,
  PRIMARY KEY (recordId),
  KEY sequencerServiceRecord_sequencer_fkey (instrumentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
-- 

-- add records for existing service records
INSERT INTO Instruction_ServiceRecord(recordId, instrumentId) 
SELECT recordId, instrumentId FROM ServiceRecord;
--

-- drop foeign key from ServiceRecord
ALTER TABLE ServiceRecord DROP FOREIGN KEY `fk_serviceRecord_instrument`;

-- add foreign key to Instrument service record
ALTER TABLE Instrument_ServiceRecord ADD CONSTRAINT `fk_instrumentServiceRecord_instrument` FOREIGN KEY (`instrumentId`) REFERENCES `Instrument` (`instrumentId`);

-- drop instrumentId columns
ALTER TABLE ServiceRecord DROP COLUMN instrumentId;

ALTER TABLE ServiceRecord ADD CONSTRAINT `fk_serviceRecord_instrumentServiceRecord` FOREIGN KEY (`recordId`) REFERENCES `Instrument_ServiceRecord` (`recordId`);


