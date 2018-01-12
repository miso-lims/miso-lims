-- StartNoTest
ALTER TABLE SequencerReference DROP FOREIGN KEY sequencerReference_upgradedReference_fkey;
ALTER TABLE Run DROP FOREIGN KEY fk_run_sequencerReference;
ALTER TABLE SequencerServiceRecord DROP FOREIGN KEY sequencerServiceRecord_sequencer_fkey;
-- EndNoTest

ALTER TABLE SequencerReference RENAME TO Instrument;
ALTER TABLE Instrument CHANGE COLUMN referenceId instrumentId bigint(20) AUTO_INCREMENT;
ALTER TABLE Instrument CHANGE COLUMN upgradedSequencerReferenceId upgradedInstrumentId bigint(20);
ALTER TABLE Instrument CHANGE COLUMN ip ip varchar(50) DEFAULT NULL;

-- StartNoTest
ALTER TABLE Instrument ADD CONSTRAINT fk_instrument_upgradedInstrument FOREIGN KEY(upgradedInstrumentId) REFERENCES Instrument(instrumentId);
ALTER TABLE Run ADD CONSTRAINT fk_run_instrument FOREIGN KEY(sequencerReference_sequencerReferenceId) REFERENCES Instrument(instrumentId);
ALTER TABLE SequencerServiceRecord ADD CONSTRAINT fk_serviceRecord_instrument FOREIGN KEY(sequencerReferenceId) REFERENCES Instrument(instrumentId);
-- EndNoTest

ALTER TABLE Run CHANGE COLUMN sequencerReference_sequencerReferenceId instrumentId bigint(20) NOT NULL;

ALTER TABLE SequencerServiceRecord RENAME TO ServiceRecord;
ALTER TABLE ServiceRecord CHANGE COLUMN sequencerReferenceId instrumentId bigint(20) NOT NULL;
