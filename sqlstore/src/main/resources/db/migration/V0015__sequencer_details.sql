-- StartNoTest
ALTER TABLE SequencerReference ENGINE=InnoDB;
-- EndNoTest
ALTER TABLE SequencerReference ADD COLUMN serialNumber varchar(30);
ALTER TABLE SequencerReference ADD COLUMN dateCommissioned date DEFAULT NULL;
ALTER TABLE SequencerReference ADD COLUMN dateDecommissioned date DEFAULT NULL;
ALTER TABLE SequencerReference ADD COLUMN upgradedSequencerReferenceId bigint;
ALTER TABLE SequencerReference ADD CONSTRAINT sequencerReference_upgradedReference_fkey FOREIGN KEY (upgradedSequencerReferenceId) REFERENCES SequencerReference(referenceId);

CREATE TABLE SequencerServiceRecord (
  recordId bigint PRIMARY KEY AUTO_INCREMENT,
  sequencerReferenceId bigint NOT NULL,
  title varchar(50) NOT NULL,
  details text,
  servicedBy varchar(30) NOT NULL,
  referenceNumber varchar(30),
  serviceDate date NOT NULL,
  shutdownTime datetime DEFAULT NULL,
  restoredTime datetime DEFAULT NULL,
  CONSTRAINT sequencerServiceRecord_sequencer_fkey FOREIGN KEY (sequencerReferenceId) REFERENCES SequencerReference(referenceId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
